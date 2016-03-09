package com.gezhii.fitgroup.ui.fragment.discovery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ContributionGroupsDTO;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.adapter.ContributionGroupListAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/23.
 */
public class ContributionGroupListFragment extends BaseFragment {


    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.back_text)
    TextView backText;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.right_text)
    TextView rightText;
    @InjectView(R.id.right_img)
    ImageView rightImg;
    @InjectView(R.id.my_group_contribution_text)
    TextView myGroupContributionText;
    @InjectView(R.id.my_group_contribution_sort_text)
    TextView myGroupContributionSortText;
    @InjectView(R.id.contribution_group_list_view)
    LoadMoreListView contributionGroupListView;

    ContributionGroupListAdapter contributionGroupListAdapter;
    @InjectView(R.id.my_group_contribution_layout)
    LinearLayout myGroupContributionLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.contribution_group_list_fragment, null);
        ButterKnife.inject(this, view);
        setTitle();
        contributionGroupListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    public void setTitle() {
        titleText.setText(R.string.sort_group);
//        rightText.setVisibility(View.GONE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().post(new ShowLoadingEvent());
        if (UserModel.getInstance().isLogin()) {
            myGroupContributionLayout.setVisibility(View.VISIBLE);
        } else {
            myGroupContributionLayout.setVisibility(View.GONE);
        }
        API.getContributionSortGroups(1, UserModel.getInstance().getUserId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                Log.i("response-----con--->",response);
                EventBus.getDefault().post(new CloseLoadingEvent());

                ContributionGroupsDTO contributionGroupsDTO = ContributionGroupsDTO.parserJson(response);
                if(contributionGroupsDTO.my_group_contribution_sort!=-1){
                    myGroupContributionSortText.setText("" + contributionGroupsDTO.my_group_contribution_sort);
                }
                else{
                    myGroupContributionLayout.setVisibility(View.GONE);
                }
                contributionGroupListAdapter = new ContributionGroupListAdapter();
                contributionGroupListAdapter.init_data_list(contributionGroupsDTO.data_list);
                contributionGroupListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
                    @Override
                    public void onListItemClick(View v, int position) {
                        MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "all_sort_list"));
                        Group group = (Group) contributionGroupListAdapter.getTotal_data_list().get(position);
                        GroupSimpleProfileFragment.start(getActivity(), group.getId(), group.getLeader().getHuanxin_id());
                    }
                });
                contributionGroupListView.setLoadMoreListViewAdapter(contributionGroupListAdapter);

                if (contributionGroupsDTO.data_list.size() == Config.loadPageCount) {
                    Object[] params = new Object[3];
                    params[0] = 1;
                    params[1] = UserModel.getInstance().getUserId();
                    params[2] = Config.loadPageCount;
                    contributionGroupListAdapter.setIsHasMore(true);
                    contributionGroupListView.setApiAutoInvoker("getContributionSortGroups", params, ContributionGroupsDTO.class);
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

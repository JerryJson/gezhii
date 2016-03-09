package com.gezhii.fitgroup.ui.fragment.group.leader;

import android.app.Activity;
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
import com.gezhii.fitgroup.dto.GroupMemberApplicationDTO;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.PrivateMessageModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.GroupMemberApplicationListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/28.
 */
public class GroupMemberApplicationListFragment extends BaseFragment {


    GroupMemberApplicationListAdapter groupMemberApplicationListAdapter;
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
    @InjectView(R.id.application_list_view)
    LoadMoreListView applicationListView;
    @InjectView(R.id.empty_layout)
    LinearLayout emptyLayout;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(GroupMemberApplicationListFragment.class);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_member_application_list_fragment, null);
        ButterKnife.inject(this, view);
        MobclickAgent.onEvent(getActivity(), "application_list", UmengEvents.getEventMap("click", "进入列表"));
        setView();
        applicationListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        PrivateMessageModel.getInstance().setApplicationUnReadCount(0);
        return view;
    }

    void setView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        titleText.setText("入会申请");
    }


    @Override
    public void onFragmentResume() {
        super.onFragmentResume();

        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getApplicationList(1, UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());

                GroupMemberApplicationDTO groupMemberApplicationDTO = GroupMemberApplicationDTO.parserJson(response);

                if (groupMemberApplicationDTO.data_list.size() == 0) {
                    applicationListView.setVisibility(View.GONE);
                    emptyLayout.setVisibility(View.VISIBLE);
                } else {
                    emptyLayout.setVisibility(View.GONE);
                    applicationListView.setVisibility(View.VISIBLE);

                    groupMemberApplicationListAdapter = new GroupMemberApplicationListAdapter(getActivity(),GroupMemberApplicationListFragment.this);
                    groupMemberApplicationListAdapter.init_data_list(groupMemberApplicationDTO.data_list);
                    applicationListView.setLoadMoreListViewAdapter(groupMemberApplicationListAdapter);

                    if (groupMemberApplicationDTO.data_list.size() == Config.loadPageCount) {
                        Object[] params = new Object[3];
                        params[0] = 1;
                        params[1] = UserModel.getInstance().getUserId();
                        params[2] = Config.loadPageCount;
                        groupMemberApplicationListAdapter.setIsHasMore(true);
                        applicationListView.setApiAutoInvoker("getApplicationList", params, GroupMemberApplicationDTO.class);
                    }
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

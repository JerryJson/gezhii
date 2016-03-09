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
import com.gezhii.fitgroup.dto.ActiveGroupsDTO;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.adapter.ActiveGroupListAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/23.
 */
public class ActiveGroupListFragment extends BaseFragment {


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
    @InjectView(R.id.my_group_activeness_text)
    TextView myGroupActivenessText;
    @InjectView(R.id.my_group_activeness_sort_text)
    TextView myGroupActivenessSortText;
    @InjectView(R.id.active_group_list_view)
    LoadMoreListView activeGroupListView;

    ActiveGroupListAdapter activeGroupListAdapter;
    @InjectView(R.id.my_group_active_layout)
    LinearLayout myGroupActiveLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.active_group_list_fragment, null);
        ButterKnife.inject(this, view);
        setTitle();

        activeGroupListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    public void setTitle() {
        titleText.setText(R.string.active_group);
        rightText.setVisibility(View.GONE);
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


        int user_id;
        if (UserModel.getInstance().isLogin()) {
            user_id = UserModel.getInstance().getUserId();
            myGroupActiveLayout.setVisibility(View.VISIBLE);
        } else {
            myGroupActiveLayout.setVisibility(View.GONE);
            user_id = -1;
        }
        API.getActiveGroups(1, user_id, Config.loadMessageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());

                ActiveGroupsDTO activeGroupsDTO = ActiveGroupsDTO.parserJson(response);

                if (activeGroupsDTO.my_group_yestoday_activeness != null)
                    myGroupActivenessText.setText("我的公会活跃度" +
                            (int) Math.floor(activeGroupsDTO.my_group_yestoday_activeness.getActiveness() * 100) + "%" + "排名");
                else
                    myGroupActiveLayout.setVisibility(View.GONE);

                myGroupActivenessSortText.setText("" + activeGroupsDTO.my_group_activeness_sort);

                initAdapter(activeGroupsDTO);


            }
        });
    }

    void initAdapter(ActiveGroupsDTO activeGroupsDTO) {

        activeGroupListAdapter = new ActiveGroupListAdapter();
        activeGroupListAdapter.init_data_list(activeGroupsDTO.data_list);
        activeGroupListView.setLoadMoreListViewAdapter(activeGroupListAdapter);
        activeGroupListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                MobclickAgent.onEvent(getActivity(), "group_square", UmengEvents.getEventMap("click", "all_active_list"));
                Group group = (Group) activeGroupListAdapter.getTotal_data_list().get(position);
                GroupSimpleProfileFragment.start(getActivity(), group.getId(), group.getLeader().getHuanxin_id());
            }
        });
        if (activeGroupsDTO.data_list.size() == Config.loadPageCount) {
            Object[] params = new Object[3];
            params[0] = 1;
            params[1] = UserModel.getInstance().getUserId();
            params[2] = Config.loadPageCount;
            activeGroupListAdapter.setIsHasMore(true);
            activeGroupListView.setApiAutoInvoker("getActiveGroups", params, ActiveGroupsDTO.class);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

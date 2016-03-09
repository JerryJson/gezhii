package com.gezhii.fitgroup.ui.fragment.group;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupMemberListDTO;
import com.gezhii.fitgroup.dto.GroupNoticeListDTO;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.adapter.GroupNoticeListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.group.AddGroupNoticeFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/24.
 */
public class GroupNoticeListFragment extends BaseFragment {
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
    @InjectView(R.id.group_notice_list_view)
    LoadMoreListView groupNoticeListView;

    GroupNoticeListAdapter groupNoticeListAdapter;
    @InjectView(R.id.no_notice_text)
    TextView noNoticeText;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.group_notice_list_fragment, null);
        ButterKnife.inject(this, view);

        setTitle();
        groupNoticeListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return view;
    }

    public void setTitle() {
        titleText.setText("公告");

        if (UserModel.getInstance().isGroupLeader()) {
            rightText.setText("写新公告");
            rightText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(getActivity(), "group_notice_list", UmengEvents.getEventMap("click", "create"));
                    ((MainActivity) getActivity()).showNext(AddGroupNoticeFragment.class, null);
                }
            });
        } else {
            rightText.setVisibility(View.GONE);
        }

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();

        EventBus.getDefault().post(new ShowLoadingEvent());

        API.getGroupNotices(1, UserModel.getInstance().getGroupId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());

                GroupNoticeListDTO groupNoticeListDTO = GroupNoticeListDTO.parserJson(response);
                if(groupNoticeListDTO.data_list ==null || groupNoticeListDTO.data_list.size()==0){
                    noNoticeText.setVisibility(View.VISIBLE);
                }else{
                    noNoticeText.setVisibility(View.INVISIBLE);
                    initListView(groupNoticeListDTO);
                }
            }
        });

    }

    void initListView(GroupNoticeListDTO groupNoticeListDTO) {
        groupNoticeListAdapter = new GroupNoticeListAdapter();
        groupNoticeListAdapter.init_data_list(groupNoticeListDTO.data_list);
        groupNoticeListView.setLoadMoreListViewAdapter(groupNoticeListAdapter);

        if (groupNoticeListDTO.data_list.size() == Config.loadPageCount) {
            Object[] params = new Object[3];
            params[0] = 1;
            params[1] = UserModel.getInstance().getUserId();
            params[2] = Config.loadPageCount;
            groupNoticeListAdapter.setIsHasMore(true);
            groupNoticeListView.setApiAutoInvoker("getActiveGroups", params, GroupMemberListDTO.class);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

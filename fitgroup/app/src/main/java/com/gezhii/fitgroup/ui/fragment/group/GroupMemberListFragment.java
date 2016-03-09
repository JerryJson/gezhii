package com.gezhii.fitgroup.ui.fragment.group;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupMemberListDTO;
import com.gezhii.fitgroup.dto.basic.GroupMember;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.GoOutMessageExt;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.adapter.GroupMemberListAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.adapter.OnListItemLongClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/24.
 */
public class GroupMemberListFragment extends BaseFragment {
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
    @InjectView(R.id.my_contribution_sort_text)
    TextView myContributionSortText;
    @InjectView(R.id.group_member_list_view)
    LoadMoreListView groupMemberListView;


    @InjectView(R.id.long_click_delete_group_member_img)
    ImageView longClickDeleteGroupMemberImg;
    @InjectView(R.id.long_click_delete_group_member_layout)
    LinearLayout longClickDeleteGroupMemberLayout;

    GroupMemberListAdapter groupMemberListAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_member_list_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "group_member_list", UmengEvents.getEventMap("click", "load"));
        setTitle();
        groupMemberListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        if (UserModel.getInstance().isNeedPromptDeleteMember()) {
            longClickDeleteGroupMemberLayout.setVisibility(View.VISIBLE);
            longClickDeleteGroupMemberImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UserModel.getInstance().setHideDeleteMemberPrompt();
                    longClickDeleteGroupMemberLayout.setVisibility(View.GONE);
                }
            });
        } else {
            longClickDeleteGroupMemberLayout.setVisibility(View.GONE);
        }
        return rootView;
    }

    public void setTitle() {
        titleText.setText("公会成员");
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
        API.getGroupMembers(1, UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());

                final GroupMemberListDTO groupMemberListDTO = GroupMemberListDTO.parserJson(response);

                myContributionSortText.setText("" + groupMemberListDTO.my_contribution_sort);

                initListAdapter(groupMemberListDTO);
                //如果是会长,在item上添加长按,踢出某人的功能
                if (UserModel.getInstance().isGroupLeader()) {
                    groupMemberListAdapter.setOnListItemLongClickListener(new OnListItemLongClickListener() {
                        @Override
                        public boolean onListItemLongClick(View v, int position) {
                            Log.i("darren", groupMemberListDTO.data_list.size());
                            final GroupMember groupMember = (GroupMember) groupMemberListAdapter.getTotal_data_list().get(position);
                            Log.i("groupMember.getId()--------->" + groupMember.getUser_id());
                            Log.i("UserModel.getInstance().getUserId()--------------->" + UserModel.getInstance().getUserId());
                            if (groupMember.getUser_id() != UserModel.getInstance().getUserId()) {
                                AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                                alertParams.setTitle("踢出公会");
                                alertParams.setMessage("确定要把该成员踢出公会?");
                                alertParams.setCancelString("取消");
                                alertParams.setConfirmString("确定");
                                alertParams.setCancelListener(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                        EventBus.getDefault().post(new ShowLoadingEvent());
                                        API.kickoffGroupHttp(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(),
                                                groupMember.getUser().getId(), new APICallBack() {
                                                    @Override
                                                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                                                        EventBus.getDefault().post(new CloseLoadingEvent());
                                                        Toast.makeText(MyApplication.getApplication(), "该成员已被踢出公会", Toast.LENGTH_SHORT).show();


                                                        HuanXinHelper.sendPrivateTextMessage(new GoOutMessageExt(UserModel.getInstance().getUserNickName(),
                                                                        UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), UserModel.getInstance().getMyGroup().getGroup_name()), groupMember.getUser().getHuanxin_id(),
                                                                UserModel.getInstance().getUserNickName() + "已把你踢出了公会", new EMCallBack() {
                                                                    @Override
                                                                    public void onSuccess() {

                                                                    }

                                                                    @Override
                                                                    public void onError(int i, String s) {

                                                                    }

                                                                    @Override
                                                                    public void onProgress(int i, String s) {

                                                                    }
                                                                });

                                                        groupMemberListDTO.data_list.remove(groupMember);
                                                        groupMemberListAdapter.init_data_list(groupMemberListDTO.data_list);
                                                        groupMemberListAdapter.notifyDataSetChanged();
                                                        Log.i("darren", groupMemberListDTO.data_list.size());
                                                        UserModel.getInstance().tryLoadRemote(true);
                                                    }
                                                });
                                    }
                                });

                                AlertHelper.showAlert(getActivity(), alertParams);
                            }

                            return true;
                        }
                    });
                }
            }
        });
    }

    void initListAdapter(GroupMemberListDTO groupMemberListDTO) {
        groupMemberListAdapter = new GroupMemberListAdapter();
        groupMemberListAdapter.init_data_list(groupMemberListDTO.data_list);
        groupMemberListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
            @Override
            public void onListItemClick(View v, int position) {
                MobclickAgent.onEvent(getActivity(), "group_member_list", UmengEvents.getEventMap("click", "member"));
                final GroupMember groupMember = (GroupMember) groupMemberListAdapter.getTotal_data_list().get(position);
                //MemberInfoFragment.start(getActivity(), groupMember.getUser_id());
                UserProfileFragment.start(getActivity(), groupMember.getUser_id());
            }
        });
        groupMemberListView.setLoadMoreListViewAdapter(groupMemberListAdapter);
        if (groupMemberListDTO.data_list.size() == Config.loadPageCount) {
            Object[] params = new Object[4];
            params[0] = 1;
            params[1] = UserModel.getInstance().getUserId();
            params[2] = UserModel.getInstance().getMyGroup().getId();
            params[3] = Config.loadPageCount;
            groupMemberListAdapter.setIsHasMore(true);
            groupMemberListView.setApiAutoInvoker("getGroupMembers", params, GroupMemberListDTO.class);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

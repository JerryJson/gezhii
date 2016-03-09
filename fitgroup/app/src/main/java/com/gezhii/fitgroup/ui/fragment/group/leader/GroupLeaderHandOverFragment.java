package com.gezhii.fitgroup.ui.fragment.group.leader;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.gezhii.fitgroup.huanxin.messageExt.LeadHandOverMessageExt;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.ui.adapter.GroupMemberListAdapter;
import com.gezhii.fitgroup.ui.adapter.OnListItemClickListener;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/24.
 */
public class GroupLeaderHandOverFragment extends BaseFragment {

    GroupMemberListAdapter groupMemberListAdapter;
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
    @InjectView(R.id.group_member_list_view)
    LoadMoreListView groupMemberListView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.group_leader_hand_over_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        setTitle();
        groupMemberListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    public void setTitle() {
        titleText.setText("会长禅让");
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
        API.getGroupMembers(1, UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), Config.loadPageCount, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());

                GroupMemberListDTO groupMemberListDTO = GroupMemberListDTO.parserJson(response);

                groupMemberListAdapter = new GroupMemberListAdapter();
                groupMemberListAdapter.init_data_list(groupMemberListDTO.data_list);
                groupMemberListView.setLoadMoreListViewAdapter(groupMemberListAdapter);

                groupMemberListAdapter.setOnListItemClickListener(new OnListItemClickListener() {
                    @Override
                    public void onListItemClick(View v, int position) {
                        GroupMember groupMember = (GroupMember) groupMemberListAdapter.getTotal_data_list().get(position);

                        if (groupMember.getUser().getId() == UserModel.getInstance().getUserId()) {
                            Toast.makeText(MyApplication.getApplication(), "不能禅让给自己!", Toast.LENGTH_SHORT).show();
                        } else {
                            handOverDialog(groupMember.getNick_name(), groupMember);
                        }

                    }
                });

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
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public void handOverDialog(String nickName, final GroupMember groupMember) {
        AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
        alertParams.setMessage("确定要任命 " + nickName + " 为下一任会长吗");
        alertParams.setCancelString("取消");
        alertParams.setConfirmString("确定");
        alertParams.setCancelListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                EventBus.getDefault().post(new ShowLoadingEvent());
                API.leaderDemise(UserModel.getInstance().getUserId(), groupMember.getUser().getId(), UserModel.getInstance().getGroupId(), new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        EventBus.getDefault().post(new CloseLoadingEvent());
                        dialog.dismiss();
                        Toast.makeText(MyApplication.getApplication(), "禅让成功!", Toast.LENGTH_SHORT).show();
                        HuanXinHelper.sendPrivateTextMessage(new LeadHandOverMessageExt(UserModel.getInstance().getUserNickName(),
                                        UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), UserModel.getInstance().getMyGroup().getGroup_name()), groupMember.getUser().getHuanxin_id(),
                                UserModel.getInstance().getUserNickName() + "已把公会禅让给你", new EMCallBack() {
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
                        UserModel.getInstance().tryLoadRemote(true);
                        finishAll();
                    }
                });
            }
        });
        AlertHelper.showAlert(getActivity(), alertParams);
    }

}

package com.gezhii.fitgroup.ui.fragment.discovery;

import android.app.Activity;
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
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupSimpleProfileDTO;
import com.gezhii.fitgroup.dto.SignHistoryDto;
import com.gezhii.fitgroup.dto.UserDto;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.GroupStateChangeEvent;
import com.gezhii.fitgroup.event.JoinGroupSuccessEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.NewMemberMessageExt;
import com.gezhii.fitgroup.model.GroupLevelConfigModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Config;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.activity.RegisterAndLoginActivity;
import com.gezhii.fitgroup.ui.adapter.GroupSimpleProfileSignHistoryListAdapter;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.group.ApplicationGroupFragment;
import com.gezhii.fitgroup.ui.fragment.group.GroupChatFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/23.
 */
public class GroupSimpleProfileFragment extends BaseFragment {
    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.application_group_text)
    TextView applicationGroupText;
    //    @InjectView(R.id.group_signin_history_list_view)
    LoadMoreListView groupSignHistoryListView;

    GroupSimpleProfileSignHistoryListAdapter groupSignHistoryListAdapter;

    private int group_id;

    public static void start(Activity activity, int group_id, String leader_huanxin_id) {
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("group_id", group_id);
        params.put("leader_huanxin_id", leader_huanxin_id);
        ((MainActivity) activity).showNext(GroupSimpleProfileFragment.class, params);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_simple_profile_fragment, null);
        ButterKnife.inject(this, view);
        EventBus.getDefault().register(this);
        groupSignHistoryListView = (LoadMoreListView) view.findViewById(R.id.group_signin_history_list_view);
        group_id = (Integer) getNewInstanceParams().get("group_id");
        groupSignHistoryListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        setView();
        return view;
    }

    public void setView() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        applicationGroupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "other_group_profile", UmengEvents.getEventMap("click", "申请入会"));
                if (UserModel.getInstance().isLogin()) {
                    if (simpleProfileDTO.group.need_check == 0)  //入会不需要审核
                    {
                        EventBus.getDefault().post(new ShowLoadingEvent());
                        API.fastJoinGroup(UserModel.getInstance().getUserId(), simpleProfileDTO.group.getId(), new APICallBack() {
                            @Override
                            public void subRequestSuccess(String response) throws NoSuchFieldException {
                                EventBus.getDefault().post(new CloseLoadingEvent());
                                EventBus.getDefault().post(new GroupStateChangeEvent());
                                Toast.makeText(getActivity(), "你已成功加入公会", Toast.LENGTH_SHORT).show();

                                //api返回值里面,传回了整个User对象
                                UserDto userDto = UserDto.parserJson(response);
                                UserModel.getInstance().updateUserDto(userDto);

                                //某人入会,在群里发条消息
                                HuanXinHelper.sendGroupTextMessage(new NewMemberMessageExt(UserModel.getInstance().getUserNickName(),
                                                UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), UserModel.getInstance().getUserNickName()),
                                        UserModel.getInstance().getMyGroup().getGroup_huanxin_id(), "欢迎@" + UserModel.getInstance().getUserNickName() + "加入我们，请先做一下自我介绍吧",
                                        new EMCallBack() {
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
                                //finish();
                                finishAll();
                                GroupChatFragment.start(getActivity());
                            }
                        });
                    } else //入会需要审核,跳到"填写入会理由"界面
                    {
                        ((MainActivity) getActivity()).showNext(ApplicationGroupFragment.class, getNewInstanceParams());
                    }
                } else {
                    boolean need_check = false;
                    if (simpleProfileDTO.group.need_check != 0) {
                        need_check = true;
                    }
                    Log.i("group-groupId", group_id);
                    RegisterAndLoginActivity.start(getActivity(), group_id, need_check, getNewInstanceParams().get("leader_huanxin_id").toString());
                }

            }
        });


        EventBus.getDefault().post(new ShowLoadingEvent());

        API.getGroupSimpleProfile(UserModel.getInstance().getUserId(), group_id, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {

                simpleProfileDTO = GroupSimpleProfileDTO.parserJson(response);

                //申请按钮的状态
                if (GroupLevelConfigModel.getInstance().isGroupFull(simpleProfileDTO.group.getLevel(), simpleProfileDTO.group.getMember_count())) {
                    applicationGroupText.setText("人数已满无法申请");
                    applicationGroupText.setBackgroundColor(getResources().getColor(R.color.gray_c8));
                    applicationGroupText.setEnabled(false);
                } else {
                    if (simpleProfileDTO.group.need_check == 0) {
                        applicationGroupText.setText("加入公会");
                    } else {
                        applicationGroupText.setText("申请入会");
                    }
                }
                Log.i("logger-----params", UserModel.getInstance().getUserId() + "    " + group_id);
                API.getSigninHistoryByGroup(1, UserModel.getInstance().getUserId(), group_id, Config.loadPageCount, new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        EventBus.getDefault().post(new CloseLoadingEvent());

                        SignHistoryDto signHistoryDto = SignHistoryDto.parserJson(response);

                        groupSignHistoryListAdapter = new GroupSimpleProfileSignHistoryListAdapter(getActivity(), GroupSimpleProfileFragment.this, group_id, 10);

                        groupSignHistoryListAdapter.init_data_list(signHistoryDto.data_list);
                        groupSignHistoryListView.setLoadMoreListViewAdapter(groupSignHistoryListAdapter);

                        if (signHistoryDto.data_list.size() == Config.loadPageCount) {
                            Object[] params = new Object[4];
                            params[0] = 1;
                            params[1] = UserModel.getInstance().getUserId();
                            params[2] = group_id;
                            params[3] = Config.loadPageCount;
                            groupSignHistoryListAdapter.setIsHasMore(true);
                            groupSignHistoryListView.setApiAutoInvoker("getSigninHistoryByGroup", params, SignHistoryDto.class);
                        }
                        groupSignHistoryListAdapter.getTotal_data_list().add(0, simpleProfileDTO);
                    }
                });


            }
        });

    }

    public void onEventMainThread(JoinGroupSuccessEvent joinGroupSuccessEvent) {
        Log.i("darren", "joinGroupSuccessEvent");
        finishAll();
//        MainActivity mainActivity = (MainActivity) getActivity();
//        mainActivity.popToFragment(mainActivity.group_tag_list_id);

        GroupChatFragment.start(getActivity());
    }

    GroupSimpleProfileDTO simpleProfileDTO = null;

    @Override
    public void onResume() {
        super.onResume();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}
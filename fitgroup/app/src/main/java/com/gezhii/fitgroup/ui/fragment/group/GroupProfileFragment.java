package com.gezhii.fitgroup.ui.fragment.group;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupProfileDTO;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.GroupStateChangeEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.event.UserDataChangeEvent;
import com.gezhii.fitgroup.model.GroupLevelConfigModel;
import com.gezhii.fitgroup.model.GroupNoticeCacheModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.WebFragment;
import com.gezhii.fitgroup.ui.fragment.group.leader.GroupLeaderHandOverFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.IconsView;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;
import com.zcw.togglebutton.ToggleButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by ycl on 15/10/23.
 */
public class GroupProfileFragment extends BaseFragment {

    public static final String TAG_IS_SHOW_GROUP_MESSAGE = "tag_is_show_group_message";
    public static final String TAG_IS_NEED_INTO_GROUP_APPLICATION = "tag_is_need_into_group_application";
    @InjectView(R.id.group_profile_mask_img)
    ImageView groupProfileMaskImg;
    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.edit_group_img)
    ImageView editGroupImg;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.group_leader_info_text)
    TextView groupLeaderInfoText;
    @InjectView(R.id.group_leader_icon_img)
    RectImageView groupLeaderIconImg;
    @InjectView(R.id.star_leader_interview_layout)
    FrameLayout starLeaderInterviewLayout;
    @InjectView(R.id.group_yestoday_signin_count)
    TextView groupYestodaySigninCount;
    @InjectView(R.id.group_activeness_text)
    TextView groupActivenessText;
    @InjectView(R.id.today_signin_statistic_layout)
    LinearLayout todaySigninStatisticLayout;
    @InjectView(R.id.group_level_info_text)
    TextView groupLevelInfoText;
    @InjectView(R.id.group_level_upgrade_layout)
    LinearLayout groupLevelUpgradeLayout;
    @InjectView(R.id.task_layout)
    LinearLayout taskLayout;
    @InjectView(R.id.group_intro_layout)
    LinearLayout groupIntroLayout;
    @InjectView(R.id.group_notice_layout)
    LinearLayout groupNoticeLayout;
    @InjectView(R.id.group_member_count_text)
    TextView groupMemberCountText;
    @InjectView(R.id.group_full_flag_text)
    TextView groupFullFlagText;
    @InjectView(R.id.icons_view)
    IconsView iconsView;
    @InjectView(R.id.show_all_group_members_layout)
    LinearLayout showAllGroupMembersLayout;
    @InjectView(R.id.no_disturb_btn)
    ToggleButton noDisturbBtn;
    @InjectView(R.id.quit_group_layout)
    LinearLayout quitGroupLayout;
    @InjectView(R.id.share_group_layout)
    LinearLayout shareGroupLayout;
    @InjectView(R.id.allow_into_group)
    ToggleButton allowIntoGroup;
    @InjectView(R.id.into_group_application_layout)
    LinearLayout intoGroupApplicationLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.group_profile_fragment, null);
        ButterKnife.inject(this, view);
        setView();
        EventBus.getDefault().register(this);
        return view;
    }

    public void setView() {
        backBtn.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                }
        );
        editGroupImg.setVisibility(View.GONE);
//        if (!UserModel.getInstance().isGroupLeader()) {
//            editGroupImg.setVisibility(View.GONE);
//        }

//        //编辑公会信息
//        editGroupImg.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("click", "edit group info"));
//                EditGroupFragment.start(getActivity());
//            }
//        });


        //入会申请开关，只有会长显示
        if (UserModel.getInstance().isGroupLeader()) {
            intoGroupApplicationLayout.setVisibility(View.VISIBLE);
        } else {
            intoGroupApplicationLayout.setVisibility(View.GONE);
        }


        //退出公会
        quitGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("click", "quit group"));
                //如果不是会长,或者成员只有1个
                if (!UserModel.getInstance().isGroupLeader() || UserModel.getInstance().getMyGroup().getMember_count() == 1) {
                    exitGroupDialog();
                } else {
                    ((MainActivity) getActivity()).showNext(GroupLeaderHandOverFragment.class, null);
                }

            }
        });

        //分享公会名片
        shareGroupLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("click", "share group card"));
                HashMap<String, Object> fragmentParams = new HashMap<String, Object>();
                fragmentParams.put("groupProfileDTO", profileDTO);
                ((MainActivity) getActivity()).showNext(ShareGroupProfileFragment.class, fragmentParams);
            }
        });

        //公告列表
        groupNoticeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("click", "最新公告"));
                ((MainActivity) getActivity()).showNext(GroupNoticeListFragment.class, null);
            }
        });

        //成员列表
        showAllGroupMembersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("click", "memberlist"));
                ((MainActivity) getActivity()).showNext(GroupMemberListFragment.class, null);
            }
        });

        //今日打卡报告
        todaySigninStatisticLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("click", "report"));
                ((MainActivity) getActivity()).showNext(GroupSigninDailyStatisticFragment.class, null);
            }
        });

        groupLevelUpgradeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("click", "group level"));
                ((MainActivity) getActivity()).showNext(GroupLevelConfigFragment.class, null);
            }
        });

//        boolean isNeedIntoGroupApplication = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_IS_NEED_INTO_GROUP_APPLICATION, false);
//        if (isNeedIntoGroupApplication) {
//            allowIntoGroup.setToggleOn();
//        } else {
//            allowIntoGroup.setToggleOff();
//        }


        boolean isShowGroupMessage = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_IS_SHOW_GROUP_MESSAGE, true);
        if (isShowGroupMessage) {
            noDisturbBtn.setToggleOff();
        } else {
            noDisturbBtn.setToggleOn();
        }
        //消息免打扰开关
        noDisturbBtn.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("click", "disturbingSwitch"));
                DataKeeperHelper.getInstance().getDataKeeper().put(TAG_IS_SHOW_GROUP_MESSAGE, !on);
            }
        });

        //公会介绍
        groupIntroLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GroupProfileSubFragment.start(getActivity(), UserModel.getInstance().getMyGroup());
            }
        });
        //公会任务
        taskLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("click", "load"));
                ((MainActivity) getActivity()).showNext(GroupTaskListSignStatisticFragment.class, null);
            }
        });
    }

    protected void exitGroupDialog() {
        AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
        alertParams.setMessage("退出公会后您的公会贡献值将会被清零");
        alertParams.setTitle("确认要退出本公会吗?");
        alertParams.setConfirmString("确定");
        alertParams.setCancelString("点错了");
        alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                EventBus.getDefault().post(new ShowLoadingEvent());
                API.quitGroupHttp(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) {
                        UserModel.getInstance().tryLoadRemote(true);
                        EventBus.getDefault().post(new CloseLoadingEvent());
                        EventBus.getDefault().post(new GroupStateChangeEvent());
                        //关掉2个页面:GroupProfile, GroupChat, 回到Home页??
                        GroupNoticeCacheModel.getInstance().clear();
                        finishAll();
                        Toast.makeText(MyApplication.getApplication(), "退出公会成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        alertParams.setCancelListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertHelper.showAlert(getActivity(), alertParams);
    }

    GroupProfileDTO profileDTO;

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().post(new ShowLoadingEvent());

        API.getGroupProfile(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                Log.i("response-------------->", response);
                EventBus.getDefault().post(new CloseLoadingEvent());
                profileDTO = GroupProfileDTO.parserJson(response);
                onRequestEnd();

            }
        });
    }

    private void onRequestEnd() {

        QiniuHelper.bindImage(profileDTO.group.getLeader().getIcon(), groupProfileMaskImg);

        //title + 公会号
        titleText.setText(UserModel.getInstance().getMyGroup().getGroup_name());

        groupLeaderInfoText.setText("会长: " + profileDTO.group.getLeader().getNick_name()
                + "  公会号: " + profileDTO.group.getGroup_number());

        //判断是否有明显专访
        if (profileDTO.group.getLeader().getUrl().equals("")) {
            starLeaderInterviewLayout.setVisibility(View.INVISIBLE);
        } else {
            starLeaderInterviewLayout.setVisibility(View.VISIBLE);
            starLeaderInterviewLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MobclickAgent.onEvent(getActivity(), "mygroup_profile", UmengEvents.getEventMap("click", "star Leader"));
                    WebFragment.client(getActivity(), profileDTO.group.getLeader().getUrl());
                }
            });
        }

        //会长卡片部分
        QiniuHelper.bindAvatarImage(profileDTO.group.getLeader().getIcon(), groupLeaderIconImg);

        if (profileDTO.group.yesterdayGroupDailyStatistics != null) {
            groupActivenessText.setText("" +
                    (int) Math.floor(profileDTO.group.yesterdayGroupDailyStatistics.getActiveness() * 100) + "%");
            groupYestodaySigninCount.setText("" + profileDTO.group.yesterdayGroupDailyStatistics.getSignin_user_count());
        } else {
            groupActivenessText.setText("0.0%");
            groupYestodaySigninCount.setText("0");
        }

        //等级与升级信息
        groupLevelInfoText.setText("" + profileDTO.group.getLevel() + "级公会,会员上限" +
                GroupLevelConfigModel.getInstance().getLevelMaxCount(profileDTO.group.getLevel()) + "人");


        //公会成员
        groupMemberCountText.setText("公会成员: " + profileDTO.group.getMember_count() + "人");
        if (!GroupLevelConfigModel.getInstance().isGroupFull(profileDTO.group.getLevel(), profileDTO.group.getMember_count())) {
            groupFullFlagText.setVisibility(View.INVISIBLE);
        }
        List<String> icons = new ArrayList<String>();
        for (int i = 0; i < profileDTO.group.group_members.size(); i++) {
            icons.add(profileDTO.group.group_members.get(i).getUser().getIcon());
        }
        if (icons.size() > 0) {
            iconsView.setTotalWidth(Screen.getScreenWidth() - Screen.dip2px(15) - Screen.dip2px(55));
            iconsView.setIcons(icons);
        }

        int isNeedCheck = profileDTO.group.getNeed_check();
        Log.i("darren", "isNeedcheck", isNeedCheck);
        if (isNeedCheck == 0) {
            allowIntoGroup.setToggleOff();
        } else {
            allowIntoGroup.setToggleOn();
        }
        //会长 入会申请开关
        allowIntoGroup.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(final boolean on) {
                Log.i("on", on);
                if (on) {
                    API.setGroupNeedCheck(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), 1, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            // DataKeeperHelper.getInstance().getDataKeeper().put(TAG_IS_NEED_INTO_GROUP_APPLICATION, on);
                        }
                    });
                } else
                    API.setGroupNeedCheck(UserModel.getInstance().getUserId(), UserModel.getInstance().getGroupId(), 0, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            //  DataKeeperHelper.getInstance().getDataKeeper().put(TAG_IS_NEED_INTO_GROUP_APPLICATION, on);
                        }
                    });
            }
        });


    }

    public void onEventMainThread(UserDataChangeEvent userDataChangeEvent) {

        onRequestEnd();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}

package com.gezhii.fitgroup.ui.fragment.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupMemberDailyListDto;
import com.gezhii.fitgroup.dto.basic.GroupMember;
import com.gezhii.fitgroup.dto.basic.UserBadge;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.GroupLevelConfigModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.umeng.analytics.MobclickAgent;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/28.
 */
public class GroupSigninDailyStatisticFragment extends BaseFragment {


    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.select_day_btn)
    TextView selectDayBtn;
    @InjectView(R.id.select_week_btn)
    TextView selectWeekBtn;
    @InjectView(R.id.today_layout)
    LinearLayout todayLayout;
    @InjectView(R.id.today_layout_text_hint)
    TextView todayLayoutTextHint;
    @InjectView(R.id.members_clock_in_layout)
    LinearLayout membersClockInLayout;
    @InjectView(R.id.members_not_clock_in_layout)
    LinearLayout membersNotClockInLayout;
    @InjectView(R.id.today_scroller_layout)
    ScrollView todayScrollerLayout;
    @InjectView(R.id.week_layout)
    LinearLayout weekLayout;
    @InjectView(R.id.week_scroller_layout)
    ScrollView weekScrollerLayout;

    GroupMemberDailyListDto mGroupMemberDayListDto;
    GroupMemberDailyListDto mGroupMemberWeekListDto;


    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(GroupSigninDailyStatisticFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.system_sign_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        selectDayBtn.setSelected(true);
        selectDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "group_report", UmengEvents.getEventMap("page", "today"));
                selectDayBtn.setSelected(true);
                selectWeekBtn.setSelected(false);
                todayScrollerLayout.setVisibility(View.VISIBLE);
                weekScrollerLayout.setVisibility(View.GONE);
            }
        });
        selectWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "group_report", UmengEvents.getEventMap("page", "week"));
                selectWeekBtn.setSelected(true);
                selectDayBtn.setSelected(false);
                todayScrollerLayout.setVisibility(View.GONE);
                weekScrollerLayout.setVisibility(View.VISIBLE);
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getGroupMembersDailySortHttp(UserModel.getInstance().getGroupId(), Calendar.getInstance().getTime(),
                new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        mGroupMemberDayListDto = GroupMemberDailyListDto.parserJson(response);

                        API.getGroupMembersWeekSortHttp(UserModel.getInstance().getGroupId(), Calendar.getInstance().getTime(),
                                new APICallBack() {
                                    @Override
                                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                                        EventBus.getDefault().post(new CloseLoadingEvent());
                                        mGroupMemberWeekListDto = GroupMemberDailyListDto.parserJson(response);
                                        setView();
                                    }
                                });

                    }
                });
    }

    private void setView() {
        setDayList();
        setWeekList();
    }

    private void setWeekList() {
        weekLayout.removeAllViews();
        int position = 0;
        for (final GroupMember groupMember : mGroupMemberWeekListDto.members) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.group_report_week_list_item, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(getResources().getDimensionPixelOffset(R.dimen.spacing_small), getResources().getDimensionPixelOffset(R.dimen.spacing_normal), getResources().getDimensionPixelOffset(R.dimen.spacing_small),
                    0);
            itemView.setLayoutParams(layoutParams);
            ReportWeekViewHolder viewHolder = new ReportWeekViewHolder(itemView);
            QiniuHelper.bindAvatarImage(groupMember.getUser().getIcon(), viewHolder.groupMemberIcon);
            viewHolder.groupWeekLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(getActivity(), "group_report", UmengEvents.getEventMap("page", "week", "click", "member"));
                    //MemberInfoFragment.start(getActivity(), groupMember.getUser().getId());
                    UserProfileFragment.start(getActivity(), groupMember.getUser().getId());
                }
            });
            viewHolder.groupMemberNameText.setText(groupMember.getUser().getNick_name());
            String levelString = String.valueOf(groupMember.getUser().getLevel()) + getString(R.string.level);
            viewHolder.groupMemberLevelText.setText(levelString);
//            for (UserBadge userBadge : groupMember.getUser().getBadges()) {
//                ImageView badge = new ImageView(getActivity());
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Screen.dip2px(15), Screen.dip2px(15));
//                params.setMargins(0, 0, getResources().getDimensionPixelOffset(R.dimen.spacing_small), 0);
//                badge.setLayoutParams(params);
//                QiniuHelper.bindAvatarImage(userBadge.badge.getIcon(), badge);
//
//                viewHolder.badgesContainer.addView(badge);
//            }

            for (int i = 0; i < groupMember.getUser().getBadges().size(); i++) {
                UserBadge userBadge = groupMember.getUser().getBadges().get(i);
                if (i <= 7) {
                    ImageView badge = new ImageView(getActivity());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Screen.dip2px(15), Screen.dip2px(15));
                    params.setMargins(0, 0, getResources().getDimensionPixelOffset(R.dimen.spacing_small), 0);
                    badge.setLayoutParams(params);
                    QiniuHelper.bindAvatarImage(userBadge.badge.getIcon(), badge);
                    viewHolder.badgesContainer.addView(badge);
                }
            }
            viewHolder.signCountText.setText(String.format(getString(R.string.week_layout_sign_count), groupMember.getSign_count()));
            switch (position) {
                case 0:
                    viewHolder.goldImg.setImageResource(R.mipmap.golden);
                    break;
                case 1:
                    viewHolder.goldImg.setImageResource(R.mipmap.silver);
                    break;
                case 2:
                    viewHolder.goldImg.setImageResource(R.mipmap.copper);
                    break;
            }
            weekLayout.addView(itemView);
            position++;
        }
    }

    private void setDayList() {
        membersClockInLayout.removeAllViews();
        membersNotClockInLayout.removeAllViews();
        int signCount = 0;
        for (final GroupMember groupMember : mGroupMemberDayListDto.members) {
            View itemView = LayoutInflater.from(getActivity()).inflate(R.layout.group_report_day_list_item, null);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(getResources().getDimensionPixelOffset(R.dimen.spacing_small),
                    getResources().getDimensionPixelOffset(R.dimen.spacing_tiny),
                    getResources().getDimensionPixelOffset(R.dimen.spacing_small),
                    getResources().getDimensionPixelOffset(R.dimen.spacing_small));

            ReportDayViewHolder viewHolder = new ReportDayViewHolder(itemView);
            QiniuHelper.bindAvatarImage(groupMember.getUser().getIcon(), viewHolder.groupMemberIcon);
            viewHolder.userInfoLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(getActivity(), "group_report", UmengEvents.getEventMap("page", "today", "click", "member"));
                    //MemberInfoFragment.start(getActivity(), groupMember.getUser().getId());
                    UserProfileFragment.start(getActivity(), groupMember.getUser().getId());
                }
            });
            viewHolder.groupMemberNameText.setText(groupMember.getUser().getNick_name());
            String levelString = String.valueOf(groupMember.getUser().getLevel()) + getString(R.string.level);
            viewHolder.groupMemberLevelText.setText(levelString);
            if (groupMember.getSign_count() > 0) {
                viewHolder.groupMemberTimeText.setText(TimeHelper.hourFormat.format(groupMember.getSign_time()));
                for (int i = 0; i < groupMember.getUser().getBadges().size(); i++) {
                    UserBadge userBadge = groupMember.getUser().getBadges().get(i);
                    if (i <= 7) {
                        ImageView badge = new ImageView(getActivity());
                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Screen.dip2px(15), Screen.dip2px(15));
                        params.setMargins(0, 0, getResources().getDimensionPixelOffset(R.dimen.spacing_small), 0);
                        badge.setLayoutParams(params);
                        QiniuHelper.bindAvatarImage(userBadge.badge.getIcon(), badge);
                        viewHolder.badgesContainer.addView(badge);
                    }
                }
                membersClockInLayout.addView(itemView);
                signCount++;
            } else {
                viewHolder.groupMemberTimeTextHint.setVisibility(View.GONE);
                viewHolder.badgesContainer.setVisibility(View.GONE);
                membersNotClockInLayout.addView(itemView);
            }
        }

        String todayLayoutText;
        if (signCount < GroupLevelConfigModel.getInstance().getLevelSignCount(UserModel.getInstance().getMyGroup().getLevel())) {
            todayLayoutText = String.format(getString(R.string.today_layout_text_hint),
                    signCount);
        } else {
            todayLayoutText = String.format(getString(R.string.today_layout_text_hint),
                    signCount);
        }
        todayLayoutTextHint.setText(todayLayoutText);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_report_day_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ReportDayViewHolder {
        @InjectView(R.id.group_member_icon)
        RectImageView groupMemberIcon;
        @InjectView(R.id.group_member_name_text)
        TextView groupMemberNameText;
        @InjectView(R.id.group_member_level_text)
        TextView groupMemberLevelText;
        @InjectView(R.id.badges_container)
        LinearLayout badgesContainer;
        @InjectView(R.id.group_member_time_text)
        TextView groupMemberTimeText;
        @InjectView(R.id.group_member_time_text_hint)
        TextView groupMemberTimeTextHint;
        @InjectView(R.id.user_info_layout)
        LinearLayout userInfoLayout;

        ReportDayViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_report_week_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ReportWeekViewHolder {
        @InjectView(R.id.group_member_icon)
        RectImageView groupMemberIcon;
        @InjectView(R.id.group_member_name_text)
        TextView groupMemberNameText;
        @InjectView(R.id.group_member_level_text)
        TextView groupMemberLevelText;
        @InjectView(R.id.badges_container)
        LinearLayout badgesContainer;
        @InjectView(R.id.sign_count_text)
        TextView signCountText;
        @InjectView(R.id.gold_img)
        ImageView goldImg;
        @InjectView(R.id.group_week_layout)
        LinearLayout groupWeekLayout;

        ReportWeekViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

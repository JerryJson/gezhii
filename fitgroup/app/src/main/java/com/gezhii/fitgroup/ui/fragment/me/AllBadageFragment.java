package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.BadgesDto;
import com.gezhii.fitgroup.dto.basic.Badge;
import com.gezhii.fitgroup.dto.basic.UserBadge;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/11/6.
 */
public class AllBadageFragment extends BaseFragment {


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
    @InjectView(R.id.badge_list_layout)
    LinearLayout badgeListLayout;

    public static void start(Activity activity, List<UserBadge> badgeList) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("badgeList", badgeList);
        mainActivity.showNext(AllBadageFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.all_badge_fragment, null);
        ButterKnife.inject(this, rootView);
        rootView.setOnTouchListener(this);
        MobclickAgent.onEvent(getActivity(), "badges_list", UmengEvents.getEventMap("click", "load"));
        setTitle();
        return rootView;
    }

    public void setTitle() {
        titleText.setText("所有徽章");
        rightText.setVisibility(View.GONE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getAllBadages(new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                EventBus.getDefault().post(new CloseLoadingEvent());
                BadgesDto badgesDto = BadgesDto.parserJson(response);
                List<UserBadge> badgeList = (List<UserBadge>) getNewInstanceParams().get("badgeList");
                if(badgeList!=null && badgeList.size()!=0){
                    initBadageList(badgesDto, badgeList);
                }
            }
        });
    }

    View lockedCardView = null;
    View unlockedCardView = null;

    public void initBadageList(BadgesDto badgesDto, List<UserBadge> badgeList) {
        if (lockedCardView != null) {
            badgeListLayout.removeView(lockedCardView);
            badgeListLayout.removeView(unlockedCardView);
        }
        lockedCardView = LayoutInflater.from(getActivity()).inflate(R.layout.badge_card_view, null);
        unlockedCardView = LayoutInflater.from(getActivity()).inflate(R.layout.badge_card_view, null);
        final BadageCardViewHolder unlockedbadageCardViewHolder = new BadageCardViewHolder(unlockedCardView);
        unlockedbadageCardViewHolder.badgeTitleText.setText("已解锁徽章");

        BadageCardViewHolder lockedbadageCardViewHolder = new BadageCardViewHolder(lockedCardView);
        lockedbadageCardViewHolder.badgeTitleText.setText("未解锁徽章");


        int userUnlockedBadgeCount = badgeList.size();
        int userUnlockedBadageLayoutCount = (userUnlockedBadgeCount + 2) / 3;
        int userUnlockedEmptyViewCount = 3 - userUnlockedBadgeCount % 3;
        for (int i = 0; i < userUnlockedBadageLayoutCount; i++) {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(Screen.dip2px(33), Screen.dip2px(10), Screen.dip2px(33), Screen.dip2px(10));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            for (int j = i * 3; j < (i + 1) * 3 && j < userUnlockedBadgeCount; j++) {
                final UserBadge userBadge = badgeList.get(j);
                LinearLayout itemView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.badge_item_view, null);
                BadageItemViewHolder badageItemViewHolder = new BadageItemViewHolder(itemView);
                badageItemViewHolder.badgeText.setText(userBadge.badge.getName());
                badageItemViewHolder.grayBadgeImg.setVisibility(View.GONE);
                QiniuHelper.bindImage(userBadge.badge.getIcon(), badageItemViewHolder.badgeImg);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getActivity(), "badges_list", UmengEvents.getEventMap("click", "unloacked_badge"));
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("icon", userBadge.badge.getIcon());
                        hashMap.put("bg", userBadge.badge.getBackground());
                        hashMap.put("name", userBadge.badge.getName());
                        hashMap.put("description", userBadge.badge.getDescription());
                        hashMap.put("unlock_condition", userBadge.badge.getUnlock_condition());
                        hashMap.put("isLocked", false);
                        ((MainActivity) getActivity()).showNext(BadgeDetailFragment.class, hashMap);
                    }
                });
                layout.addView(itemView, layoutParams);
            }
            if (i == userUnlockedBadageLayoutCount - 1 && userUnlockedEmptyViewCount != 3) {
                for (int k = 0; k < userUnlockedEmptyViewCount; k++) {
                    View emptyView = new View(getActivity());
                    layout.addView(emptyView, layoutParams);
                }
            }
            unlockedbadageCardViewHolder.badgeLayout.addView(layout);
        }


        List<Badge> lockedBadge = lockedBadge(badgesDto, badgeList);

        int userLockedBadgeCount = lockedBadge.size();
        int userLockedBadageLayoutCount = (userLockedBadgeCount + 2) / 3;
        int userLockedEmptyViewCount = 3 - userLockedBadgeCount % 3;
        for (int i = 0; i < userLockedBadageLayoutCount; i++) {
            LinearLayout layout = new LinearLayout(getActivity());
            layout.setOrientation(LinearLayout.HORIZONTAL);
            layout.setPadding(Screen.dip2px(33), Screen.dip2px(10), Screen.dip2px(33), Screen.dip2px(10));
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.weight = 1;
            for (int j = i * 3; j < (i + 1) * 3 && j < userLockedBadgeCount; j++) {
                final Badge badge = lockedBadge.get(j);
                LinearLayout itemView = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.badge_item_view, null);
                BadageItemViewHolder badageItemViewHolder = new BadageItemViewHolder(itemView);
                badageItemViewHolder.badgeText.setText(badge.getName());
                badageItemViewHolder.grayBadgeImg.setVisibility(View.VISIBLE);
                QiniuHelper.bindImage(badge.getIcon(), badageItemViewHolder.badgeImg);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(getActivity(), "badges_list", UmengEvents.getEventMap("click", "loacked_badge"));
                        HashMap<String, Object> hashMap = new HashMap<String, Object>();
                        hashMap.put("icon", badge.getIcon());
                        hashMap.put("bg", badge.getBackground());
                        hashMap.put("name", badge.getName());
                        hashMap.put("description", badge.getDescription());
                        hashMap.put("unlock_condition", badge.getUnlock_condition());
                        hashMap.put("isLocked", true);
                        ((MainActivity) getActivity()).showNext(BadgeDetailFragment.class, hashMap);
                    }
                });
                layout.addView(itemView, layoutParams);
            }
            if (i == userLockedBadageLayoutCount - 1 && userLockedEmptyViewCount != 3) {
                for (int k = 0; k < userLockedEmptyViewCount; k++) {
                    View emptyView = new View(getActivity());
                    layout.addView(emptyView, layoutParams);
                }
            }
            lockedbadageCardViewHolder.badgeLayout.addView(layout);
        }

        badgeListLayout.addView(unlockedCardView);
        badgeListLayout.addView(lockedCardView);
    }

    public List<Badge> lockedBadge(BadgesDto allbadgesDto, List<UserBadge> badgeList) {
        List<Badge> lockedBadge = new ArrayList<Badge>();
        for (int i = 0; i < badgeList.size(); i++) {
            for (int j = 0; j < allbadgesDto.badges.size(); j++) {
                if (badgeList.get(i).badge.getId() == allbadgesDto.badges.get(j).getId()) {
                    allbadgesDto.badges.remove(j);
                }
            }

        }
        lockedBadge = allbadgesDto.badges;
        return lockedBadge;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }


    static class BadageCardViewHolder {
        @InjectView(R.id.badge_title_text)
        TextView badgeTitleText;
        @InjectView(R.id.badge_layout)
        LinearLayout badgeLayout;

        BadageCardViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class BadageItemViewHolder {
        @InjectView(R.id.badge_img)
        ImageView badgeImg;
        @InjectView(R.id.badge_text)
        TextView badgeText;
        @InjectView(R.id.locked_badge_img)
        ImageView grayBadgeImg;

        BadageItemViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

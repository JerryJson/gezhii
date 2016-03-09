package com.gezhii.fitgroup.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.EdgeEffectCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.akexorcist.roundcornerprogressbar.RoundCornerProgressBar;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.PushExperienceDto;
import com.gezhii.fitgroup.dto.basic.Badge;
import com.gezhii.fitgroup.model.GroupLevelConfigModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.fragment.me.BadgeShareFragment;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/11/16.
 */
public class PushBadgeDialog extends Dialog {


    @InjectView(R.id.close_btn)
    ImageView closeBtn;
    @InjectView(R.id.content_text)
    TextView contentText;
    @InjectView(R.id.progress_view)
    RoundCornerProgressBar progressView;
    @InjectView(R.id.badge_view_pager)
    ViewPager badgeViewPager;
    @InjectView(R.id.share_text)
    TextView shareText;
    @InjectView(R.id.wechat_img)
    ImageView wechatImg;
    @InjectView(R.id.wechat_moment_img)
    ImageView wechatMomentImg;
    @InjectView(R.id.sina_weibo_img)
    ImageView sinaWeiboImg;
    @InjectView(R.id.qq_img)
    ImageView qqImg;
    @InjectView(R.id.share_to_layout)
    LinearLayout shareToLayout;


    private EdgeEffectCompat leftEdge;

    private EdgeEffectCompat rightEdge;

    private Activity mFragment;


    public PushBadgeDialog(Activity context) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public PushBadgeDialog(Activity context, int theme) {
        super(context, theme);
        this.mFragment = context;
    }

    PushExperienceDto pushExperienceDto;
    String contentTextString;
    int maxValue;
    int currentValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.push_badge_dialog);
        ButterKnife.inject(this);
        MobclickAgent.onEvent(mFragment, "badge_get", UmengEvents.getEventMap("click", "load"));
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        progressView.setBackgroundColor(getContext().getResources().getColor(android.R.color.transparent));
//        progressView.setFgColor(getContext().getResources().getColor(android.R.color.transparent));
        progressView.setProgressBackgroundColor(getContext().getResources().getColor(R.color.gray_c8));
        progressView.setProgressColor(getContext().getResources().getColor(R.color.colorPrimary));
        progressView.setRadius(Screen.dip2px(4));

        badgeViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (leftEdge != null && rightEdge != null) {
                    leftEdge.finish();
                    rightEdge.finish();
                    leftEdge.setSize(0, 0);
                    rightEdge.setSize(0, 0);
                }
            }

            @Override
            public void onPageSelected(int position) {
//                selectPoint(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        getEdge();
    }

    @Override
    public void show() {
        super.show();
        if (pushExperienceDto != null) {
            contentText.setText(contentTextString);
            progressView.setMax(maxValue);
            progressView.setProgress(currentValue);
            badgeViewPager.setAdapter(new BadgeAdapter(getContext(), pushExperienceDto.badge));
            shareBadge();
        }
    }

    public void setPushExperienceDto(PushExperienceDto pushExperienceDto) {
        this.pushExperienceDto = pushExperienceDto;
        contentTextString = "+" + pushExperienceDto.experience_inr + "轻元素";
        if (pushExperienceDto.level_upgrade > 0) {
            int level = UserModel.getInstance().getUserDto().getUser().getLevel() + pushExperienceDto.level_upgrade;
            contentTextString += "升为" + level + "级！";
        }
        maxValue = GroupLevelConfigModel.getInstance().getLevelMaxCount(pushExperienceDto.level);
        currentValue = (int) (GroupLevelConfigModel.getInstance().getLevelMaxCount(pushExperienceDto.level) * pushExperienceDto.progress);

    }

    private void getEdge() {
        try {
            Field leftEdgeField = badgeViewPager.getClass().getDeclaredField("mLeftEdge");
            Field rightEdgeField = badgeViewPager.getClass().getDeclaredField("mRightEdge");
            if (leftEdgeField != null && rightEdgeField != null) {
                leftEdgeField.setAccessible(true);
                rightEdgeField.setAccessible(true);
                leftEdge = (EdgeEffectCompat) leftEdgeField.get(badgeViewPager);
                rightEdge = (EdgeEffectCompat) rightEdgeField.get(badgeViewPager);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void shareBadge() {
        wechatImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mFragment, "badge_get", UmengEvents.getEventMap("click", "wechat_session"));
                BadgeShareFragment.start(mFragment, BadgeShareFragment.TAG_WECHAT, pushExperienceDto.badge.get(0));
            }
        });
        wechatMomentImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mFragment, "badge_get", UmengEvents.getEventMap("click", "wechat_timeline"));
                BadgeShareFragment.start(mFragment, BadgeShareFragment.TAG_WECHAT_MOMENT, pushExperienceDto.badge.get(0));
            }
        });
        sinaWeiboImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mFragment, "badge_get", UmengEvents.getEventMap("click", "sinaweibo"));
                BadgeShareFragment.start(mFragment, BadgeShareFragment.TAG_SINA_WEIBO, pushExperienceDto.badge.get(0));
            }
        });
        qqImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mFragment, "badge_get", UmengEvents.getEventMap("click", "qq"));
                BadgeShareFragment.start(mFragment, BadgeShareFragment.TAG_QQ, pushExperienceDto.badge.get(0));
            }
        });
    }


    public static class BadgeAdapter extends PagerAdapter {

        List<Badge> badgeList;
        HashMap<Integer, View> viewHashMap;
        Context mContext;

        public BadgeAdapter(Context mContext, List<Badge> badgeList) {
            this.badgeList = badgeList;
            this.viewHashMap = new HashMap<>();
            this.mContext = mContext;
        }


        @Override
        public int getCount() {
            return badgeList.size();
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(viewHashMap.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View rootView = LayoutInflater.from(mContext).inflate(R.layout.push_badge_item_view, null);
            viewHashMap.put(position, rootView);
            Badge badge = badgeList.get(position);
            ViewHolder viewHolder = new ViewHolder(rootView);
            QiniuHelper.bindImage(badge.getIcon(), viewHolder.badgeImg);
            QiniuHelper.bindImage(badge.getBackground(), viewHolder.badgeBgImg);
            viewHolder.badgeText.setText(badge.getName());
            viewHolder.badgeInformationText.setText(badge.getDescription());
            viewHolder.unlockConditionText.setText(badge.getUnlock_condition());
            container.addView(rootView);
            return rootView;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {

            return view == object;
        }


        static class ViewHolder {
            @InjectView(R.id.badge_bg_img)
            ImageView badgeBgImg;
            @InjectView(R.id.badge_text)
            TextView badgeText;
            @InjectView(R.id.badge_img)
            ImageView badgeImg;
            @InjectView(R.id.badge_information_text)
            TextView badgeInformationText;
            @InjectView(R.id.unlock_condition_text)
            TextView unlockConditionText;

            ViewHolder(View view) {
                ButterKnife.inject(this, view);
            }
        }
    }


}

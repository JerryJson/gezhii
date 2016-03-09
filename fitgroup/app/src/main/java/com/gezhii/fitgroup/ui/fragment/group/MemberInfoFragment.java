package com.gezhii.fitgroup.ui.fragment.group;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.OtherUserProfileDto;
import com.gezhii.fitgroup.dto.basic.UserWeight;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.me.PrivateChatFragment;
import com.gezhii.fitgroup.ui.fragment.me.AllBadageFragment;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.gezhii.fitgroup.ui.view.bessel_curve.BesselCurveViewScroller;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/11/24.
 */
public class MemberInfoFragment extends BaseFragment {
    public static final String TAG_HUANXIN_ID = "tag_huanxin_id";
    public static final String TAG_USER = "tag_user";
    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.icon_img)
    RectImageView iconImg;
    @InjectView(R.id.user_name_text)
    TextView userNameText;
    @InjectView(R.id.badges_container)
    LinearLayout badgesContainer;
    @InjectView(R.id.private_message_layout)
    RelativeLayout privateMessageLayout;
    @InjectView(R.id.member_card_records)
    LinearLayout memberCardRecords;
    @InjectView(R.id.card_days_layout)
    RelativeLayout cardDaysLayout;
    @InjectView(R.id.bessel_curve_normal_weight_title_text)
    TextView besselCurveNormalWeightTitleText;
    @InjectView(R.id.bessel_curve_normal_weight_text)
    TextView besselCurveNormalWeightText;
    @InjectView(R.id.bessel_curve_view)
    BesselCurveViewScroller besselCurveView;
    @InjectView(R.id.bessel_curve_normal_layout)
    LinearLayout besselCurveNormalLayout;
    @InjectView(R.id.no_weight_layout)
    RelativeLayout noWeightLayout;
    @InjectView(R.id.card_days_text)
    TextView cardDaysText;


    public static void start(Activity activity, int user_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(TAG_USER, user_id);
        mainActivity.showNext(MemberInfoFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.member_info_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "other_profile");
        int user_id = (int) getNewInstanceParams().get(TAG_USER);
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getOtherUserProfile(user_id, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                final OtherUserProfileDto otherUserProfileDto = OtherUserProfileDto.parserJson(response);
                initView(otherUserProfileDto);
            }
        });

        return rootView;
    }

    public void initView(final OtherUserProfileDto otherUserProfileDto) {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        QiniuHelper.bindAvatarImage(otherUserProfileDto.getUser().getIcon(), iconImg);
        userNameText.setText(otherUserProfileDto.getUser().getNick_name() + " " + otherUserProfileDto.getUser().getLevel() + "级");
        for (int i = 0; i < otherUserProfileDto.getUser().getBadges().size(); i++) {
            ImageView badgeView = new ImageView(getActivity());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Screen.dip2px(25), Screen.dip2px(25));
            params.setMargins(0, 0, Screen.dip2px(17), 0);
            badgeView.setLayoutParams(params);
            if (otherUserProfileDto.getUser().getBadges().get(i).badge != null) {
                if (i <= 5) {
                    QiniuHelper.bindAvatarImage(otherUserProfileDto.getUser().getBadges().get(i).badge.getIcon(), badgeView);
                    badgesContainer.addView(badgeView);
                }
            }
        }
        badgesContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AllBadageFragment.start(getActivity(), otherUserProfileDto.getUser().getBadges());
            }
        });
        cardDaysText.setText(String.format(getString(R.string.continue_sign_days), otherUserProfileDto.getUser().getSignin_count()));


        if (otherUserProfileDto.getWeight_list().size() > 0) {
            final List<UserWeight> weightList = new ArrayList<>();
            weightList.addAll(otherUserProfileDto.getWeight_list());
            Collections.reverse(weightList);
            besselCurveView.setUserWeightList(weightList);
//            besselCurveView.setGoalWeight(otherUserProfileDto.getUser().getGoal_weight());
            noWeightLayout.setVisibility(View.GONE);
            besselCurveNormalLayout.setVisibility(View.VISIBLE);
            besselCurveView.setOnBesselCurveViewSelectChangeListener(new BesselCurveViewScroller.OnBesselCurveViewSelectChangeListener() {
                @Override
                public void onBesselCurveViewSelectChange(int position) {
                    UserWeight userWeight = weightList.get(position);
                    besselCurveNormalWeightTitleText.setText(TimeHelper.DATE_FORMAT_m_d_h_m.format(userWeight.getCreated_time()));
                    //besselCurveNormalWeightText.setText(userWeight.getWeight() + "kg");
                }
            });
            besselCurveView.post(new Runnable() {
                @Override
                public void run() {
                    besselCurveView.setCurrentPosition(weightList.size() - 1);
                }
            });

        } else {
            noWeightLayout.setVisibility(View.VISIBLE);
            besselCurveNormalLayout.setVisibility(View.GONE);
        }


        privateMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "other_profile", UmengEvents.getEventMap("click", "chat"));
                PrivateChatFragment.start(getActivity(), otherUserProfileDto.getUser().getHuanxin_id());
            }
        });
        cardDaysLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "other_profile", UmengEvents.getEventMap("click", "signin_history"));
                Log.i("darren", otherUserProfileDto.getUser().getId());
                SignHistoryFragment.start(getActivity(), otherUserProfileDto.getUser());
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

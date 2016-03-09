package com.gezhii.fitgroup.ui.fragment.me;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
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
import com.gezhii.fitgroup.dto.UserApplicationVipDTO;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 16/2/17.
 */
public class ApplicationToBeVipFragment extends BaseFragment {
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
    @InjectView(R.id.introduce_myself_btn)
    LinearLayout introduceMyselfBtn;
    @InjectView(R.id.submit_application_btn)
    RelativeLayout submitApplicationBtn;
    @InjectView(R.id.vip_status_img)
    ImageView vipStatusImg;
    @InjectView(R.id.vip_status_text)
    TextView vipStatusText;
    @InjectView(R.id.vip_power_description_text)
    TextView vipPowerDescriptionText;
    @InjectView(R.id.application_to_be_vip_layout)
    LinearLayout applicationToBeVipLayout;
    @InjectView(R.id.need_days_text)
    TextView needDaysText;

    User user;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(ApplicationToBeVipFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.application_to_be_vip_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "ApplyDaRen", UmengEvents.getEventMap("click", "load"));
        rootView.setOnTouchListener(this);
        initTitle();
        user = UserModel.getInstance().getUserDto().getUser();
        if (user.getVip() != 0) {
            vipStatusText.setText("已经成为达人啦");
            vipStatusImg.setImageResource(R.mipmap.smile_vip);
            applicationToBeVipLayout.setVisibility(View.INVISIBLE);
        } else {
            API.getUserApplicationVipState(UserModel.getInstance().getUserId(), new APICallBack() {
                @Override
                public void subRequestSuccess(String response) throws NoSuchFieldException {
                    Log.i("darren", response);
                    UserApplicationVipDTO userApplicationVipDTO = new UserApplicationVipDTO();
                    userApplicationVipDTO = UserApplicationVipDTO.parserJson(response);
                    if (userApplicationVipDTO.getUser_application_vip() != null) {
                        if (userApplicationVipDTO.getUser_application_vip().getState() == 0) {//审核中
                            vipStatusText.setText("申请审核中...");
                            applicationToBeVipLayout.setVisibility(View.INVISIBLE);
                        } else {
                            vipStatusText.setText("成为运动达人");
                            applicationToBeVipLayout.setVisibility(View.VISIBLE);
                        }
                    } else {
                        vipStatusText.setText("成为运动达人");
                        applicationToBeVipLayout.setVisibility(View.VISIBLE);
                    }
                }
            });
        }

        return rootView;
    }

    public void initTitle() {
        titleText.setText("申请达人");
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        submitApplicationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = UserModel.getInstance().getUserDto().getUser();
                String name = user.getNick_name();
                String user_icon = user.getIcon();
                String age = user.getAge_description();
                String job = user.getJob();
                String frequency = user.getSport_frequency();
                String way = user.getSport_way();
                String description = user.getSelf_description();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(user_icon) || TextUtils.isEmpty(age) || TextUtils.isEmpty(job) || TextUtils.isEmpty(frequency) || TextUtils.isEmpty(way) || TextUtils.isEmpty(description)) {
                    // showToast("请先完善个人介绍信息");

                    AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                    alertParams.setCancelString("取消");
                    alertParams.setConfirmString("去完善");
                    alertParams.setTitle("你的个人信息不完整哦");
                    alertParams.setCancelListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            MyPersonInfoFragment.start(getActivity());
                        }
                    });
                    AlertHelper.showAlert(getActivity(), alertParams);
                } else {
//                    if (user.getContinue_signin_days() < 5) {
//                        showToast("请至少连续完成任务5天");
//                        needDaysText.setText("还差" + String.valueOf(5 - user.getContinue_signin_days()) + "天");
//                    } else {
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.applicationVip(UserModel.getInstance().getUserId(), new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            EventBus.getDefault().post(new CloseLoadingEvent());
                            vipStatusText.setText("申请审核中...");
                            applicationToBeVipLayout.setVisibility(View.INVISIBLE);
                        }
                    });
//                    }
                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

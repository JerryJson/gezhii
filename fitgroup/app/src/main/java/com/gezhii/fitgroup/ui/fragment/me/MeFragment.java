package com.gezhii.fitgroup.ui.fragment.me;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ResInformationDto;
import com.gezhii.fitgroup.dto.UserDto;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.dto.basic.UserBadge;
import com.gezhii.fitgroup.dto.basic.UserLevelConfig;
import com.gezhii.fitgroup.dto.basic.UserWeight;
import com.gezhii.fitgroup.dto.db.UserLevelConfigDTO;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.event.UserDataChangeEvent;
import com.gezhii.fitgroup.model.PrivateMessageModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.dialog.ImagePickDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.ProgressView;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.gezhii.fitgroup.ui.view.bessel_curve.BesselCurveViewScroller;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;


/**
 * Created by xianrui on 15/10/19.
 */
public class MeFragment extends BaseFragment {
    public static final String TAG = MeFragment.class.getName();


    @InjectView(R.id.settings_img)
    ImageView settingsImg;
    @InjectView(R.id.icon_img)
    RectImageView iconImg;
    @InjectView(R.id.user_name_text)
    TextView userNameText;
    @InjectView(R.id.bagde_1_img)
    ImageView bagde1Img;
    @InjectView(R.id.bagde_2_img)
    ImageView bagde2Img;
    @InjectView(R.id.bagde_3_img)
    ImageView bagde3Img;
    @InjectView(R.id.all_badages_layout)
    LinearLayout allBadagesLayout;
    @InjectView(R.id.group_level_text)
    TextView groupLevelText;
    @InjectView(R.id.next_load_need_badges)
    TextView nextLoadNeedBadges;
    @InjectView(R.id.upgrade_layout)
    LinearLayout upgradeLayout;
    @InjectView(R.id.progress_view)
    ProgressView progressView;
    @InjectView(R.id.bessel_curve_normal_weight_title_text)
    TextView besselCurveNormalWeightTitleText;
    @InjectView(R.id.bessel_curve_normal_add_weight_btn)
    ImageView besselCurveNormalAddWeightBtn;
    @InjectView(R.id.bessel_curve_normal_weight_text)
    TextView besselCurveNormalWeightText;
    @InjectView(R.id.bessel_curve_view)
    BesselCurveViewScroller besselCurveView;
    @InjectView(R.id.bessel_curve_normal_bmi_text)
    TextView besselCurveNormalBmiText;
    @InjectView(R.id.bessel_curve_normal_body_type_text)
    TextView besselCurveNormalBodyTypeText;
    @InjectView(R.id.bessel_curve_normal_layout)
    LinearLayout besselCurveNormalLayout;
    @InjectView(R.id.empty_title_text)
    TextView emptyTitleText;
    @InjectView(R.id.empty_add_weight_btn)
    ImageView emptyAddWeightBtn;
    @InjectView(R.id.bessel_curve_empty_layout)
    LinearLayout besselCurveEmptyLayout;
    @InjectView(R.id.punch_card_records_layout)
    LinearLayout punchCardRecordsLayout;
    @InjectView(R.id.private_new_message_img)
    ImageView privateNewMessageImg;
    @InjectView(R.id.private_chat_list_btn)
    LinearLayout privateChatListBtn;

    ImagePickDialog imagePickDialog;
    @InjectView(R.id.go_to_bmi_img)
    ImageView goToBmiImg;
    @InjectView(R.id.go_to_bmi_layout)
    LinearLayout goToBmiLayout;
    @InjectView(R.id.no_fit_type_layout)
    RelativeLayout noFitTypeLayout;

    public static final String tagLightSlim = "偏瘦";
    public static final String tagStandard = "标准";
    public static final String tagLightFat = "偏胖";
    public static final String tagFat = "肥胖";

    private User user;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.me_fragment, null);
        ButterKnife.inject(this, rootView);
        MobclickAgent.onEvent(getActivity(), "me", UmengEvents.getEventMap("click", "load"));
        userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNickNameDialog();
            }
        });
        settingsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "me", UmengEvents.getEventMap("click", "setting"));
                ((MainActivity) getActivity()).showNext(SettingsFragment.class, null);
            }
        });
        allBadagesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "me", UmengEvents.getEventMap("click", "all_badge"));
                AllBadageFragment.start(getActivity(), UserModel.getInstance().getUserDto().getUser().getBadges());
            }
        });
        upgradeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).showNext(RoadToUpgradeFragment.class, null);
            }
        });
        goToBmiLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "me_data", UmengEvents.getEventMap("click", "bmi_info"));
                BmiFragment.start(getActivity());
            }
        });
        if (PrivateMessageModel.getInstance().getMessageDto().getAllUnReadMessageCount() > 0) {
            privateNewMessageImg.setVisibility(View.VISIBLE);
        } else {
            privateNewMessageImg.setVisibility(View.GONE);
        }
        punchCardRecordsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "me", UmengEvents.getEventMap("click", "signin_history"));
                ((MainActivity) getActivity()).showNext(SigninRecordFragment.class, null);
            }
        });

        PrivateMessageModel.getInstance().addMessageUnReadCountChangeListener(new PrivateMessageModel.OnUnReadMessageCountChangeListener() {
            @Override
            public void onUnReadMessageCountChange(final int count) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (count > 0) {
                            privateNewMessageImg.setVisibility(View.VISIBLE);
                        } else {
                            privateNewMessageImg.setVisibility(View.GONE);
                        }
                    }
                });
            }
        });

        EventBus.getDefault().register(this);
        return rootView;
    }

    @OnClick(R.id.private_chat_list_btn)
    public void onPrivateChatListBtnClick(View v) {
        MobclickAgent.onEvent(getActivity(), "me", UmengEvents.getEventMap("click", "private_mes_list"));
        PrivateMessageFragment.start(getActivity());
    }

    public void onEvent(UserDataChangeEvent userDataChangeEvent) {
        setView();
    }


    @Override
    public void onResume() {
        super.onResume();
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.isNeedShowFragment(this);
        }
        setView();
    }


    public void initProgressView(int current, int max) {
        progressView.setBgColor(R.color.white);
        progressView.setFgColor(R.color.gray_d2);
        progressView.setCurrentProgress(current);
        progressView.setMaxProgress(max);
        progressView.setProgressColor(R.color.colorPrimary);
        progressView.invalidate();
    }

    public void initBadgesView(List<UserBadge> badgeList) {
        int userUnlockedBadgeCount = badgeList.size();
        String[] array = new String[userUnlockedBadgeCount];
        for (int i = 0; i < userUnlockedBadgeCount; i++) {
            UserBadge userBadge = badgeList.get(i);
            if (userBadge.badge != null) {
                array[i] = userBadge.badge.getIcon();
            }
        }
        bagde1Img.setVisibility(View.VISIBLE);
        bagde2Img.setVisibility(View.VISIBLE);
        bagde3Img.setVisibility(View.VISIBLE);
        if (1 == userUnlockedBadgeCount) {
            QiniuHelper.bindAvatarImage(array[0], bagde1Img);
            bagde2Img.setVisibility(View.GONE);
            bagde3Img.setVisibility(View.GONE);
        } else if (2 == userUnlockedBadgeCount) {
            QiniuHelper.bindAvatarImage(array[0], bagde1Img);
            QiniuHelper.bindAvatarImage(array[1], bagde2Img);
            bagde3Img.setVisibility(View.GONE);
        } else if (3 <= userUnlockedBadgeCount) {
            QiniuHelper.bindAvatarImage(array[0], bagde1Img);
            QiniuHelper.bindAvatarImage(array[1], bagde2Img);
            QiniuHelper.bindAvatarImage(array[2], bagde3Img);
        }
    }

    @OnClick(R.id.bessel_curve_normal_add_weight_btn)
    public void onNormalAddWeightBtnClick(View v) {
        InputWeightFragment.start(getActivity(), false);
    }

    @OnClick({R.id.bessel_curve_empty_layout, R.id.no_fit_type_layout})
    public void onEmptyAddWeightBtnClick(View v) {
        MobclickAgent.onEvent(getActivity(), "me_data");
        ChooseSexFragment.start(getActivity());
    }

    private void setView() {
        if (UserModel.getInstance().isLogin()) {
            UserModel.getInstance().tryLoadRemote(false);
            List<UserBadge> badgeList = new ArrayList<UserBadge>();
            badgeList = UserModel.getInstance().getUserDto().getUser().getBadges();
            if (badgeList != null) {
                initBadgesView(badgeList);
            }
            user = UserModel.getInstance().getUserDto().getUser();
            API.getUserLevelConfigs(new APICallBack() {
                @Override
                public void subRequestSuccess(String response) {
                    UserLevelConfigDTO userLevelConfigDTO = new UserLevelConfigDTO();
                    userLevelConfigDTO = UserLevelConfigDTO.parserJson(response);
                    List<UserLevelConfig> levelConfigList = new ArrayList<UserLevelConfig>();
                    levelConfigList = userLevelConfigDTO.level_configs;
                    int nextExperience = levelConfigList.get(user.getLevel()).getExpericence();
                    nextLoadNeedBadges.setText("下一级所需轻元素为" + String.valueOf(nextExperience - user.getExperience()));
                    groupLevelText.setText(user.getLevel() + "级");
                    initProgressView(user.getExperience(), nextExperience);
                }
            });


            QiniuHelper.bindAvatarImage(UserModel.getInstance().getUserIcon(), iconImg);
            iconImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (imagePickDialog == null) {
                        createImagePickDialog();
                    }
                    imagePickDialog.show();
                }
            });
            if (!TextUtils.isEmpty(UserModel.getInstance().getUserDto().getUser().getNick_name())) {
                userNameText.setText(UserModel.getInstance().getUserDto().getUser().getNick_name());
            } else {
                userNameText.setText("请填写昵称");
            }
            if (UserModel.getInstance().getUserDto().getWeight_list() != null && UserModel.getInstance().getUserDto().getWeight_list().size() > 0) {
                Log.i("isFirstSetWeight", UserModel.getInstance().isFristSetWeightFlag());
                if (UserModel.getInstance().isFristSetWeightFlag()) {
                    besselCurveView.initOffsetX();
                    UserModel.getInstance().setFirstSetWeightFlag(1);
                }
                final List<UserWeight> weightList = new ArrayList<>();
                weightList.addAll(UserModel.getInstance().getUserDto().getWeight_list());
                Collections.reverse(weightList);
                besselCurveView.setUserWeightList(weightList);
                besselCurveView.setGoalWeight(UserModel.getInstance().getUserDto().getUser().getGoal_weight());
                besselCurveView.setGoalWeightOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditGoalWeightFragment.start(getActivity());
                    }
                });
                besselCurveNormalLayout.setVisibility(View.VISIBLE);
                besselCurveEmptyLayout.setVisibility(View.GONE);
                besselCurveView.setOnBesselCurveViewSelectChangeListener(new BesselCurveViewScroller.OnBesselCurveViewSelectChangeListener() {
                    @Override
                    public void onBesselCurveViewSelectChange(int position) {
                        MobclickAgent.onEvent(getActivity(), "me_data", UmengEvents.getEventMap("slide", "weight_list"));
                        UserWeight userWeight = weightList.get(position);
                        besselCurveNormalWeightTitleText.setText(TimeHelper.DATE_FORMAT_m_d_h_m.format(userWeight.getCreated_time()));
                        besselCurveNormalWeightText.setText(userWeight.getWeight() + "kg");
                    }
                });
                besselCurveView.post(new Runnable() {
                    @Override
                    public void run() {
                        besselCurveView.setCurrentPosition(UserModel.getInstance().getUserDto().getWeight_list().size() - 1);
                    }
                });
                if (UserModel.getInstance().getUserDto().getUser().getHeight() > 0 && UserModel.getInstance().getUserDto().getUser().getGoal_weight() > 0) {
                    noFitTypeLayout.setVisibility(View.INVISIBLE);
                } else {
                    noFitTypeLayout.setVisibility(View.VISIBLE);
                }

                float weight = UserModel.getInstance().getUserDto().getWeight_list().get(0).getWeight();
                int height = UserModel.getInstance().getUserDto().getUser().getHeight();
                float bmi = weight / (height * height) * 10000;
                float bmiShow = (float) (Math.round(bmi * 10)) / 10;
                Log.i("height---->" + height);
                Log.i("weight---->" + weight);
                besselCurveNormalBmiText.setText("BMI:" + bmiShow);

                if (bmi < 18.5) {
                    besselCurveNormalBodyTypeText.setText("体型: " + tagLightSlim);
                } else if (bmi < 24) {
                    besselCurveNormalBodyTypeText.setText("体型: " + tagStandard);
                } else if (bmi < 28) {
                    besselCurveNormalBodyTypeText.setText("体型: " + tagLightFat);
                } else {
                    besselCurveNormalBodyTypeText.setText("体型: " + tagFat);
                }


            } else {
                besselCurveEmptyLayout.setVisibility(View.VISIBLE);
                besselCurveNormalLayout.setVisibility(View.GONE);
            }

        }
//        else {
//            userNameText.setText("未登录");
//        }
    }


    //imagePickDialog就是那个有3个按钮的ActionSheet, "相册", "相机", "取消"
    private void createImagePickDialog() {
        imagePickDialog = new ImagePickDialog(MeFragment.this);
        imagePickDialog.setCallback(new ImagePickDialog.Callback() {
            @Override
            public void onImageBack(ResInformationDto resInformationDto) {
                QiniuHelper.uploadImgs(UserModel.getInstance().getUserId(), resInformationDto.getPushFileName(), resInformationDto.getFilePath(),
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                if (info.isOK()) {
                                    String icon = QiniuHelper.QINIU_SPACE + "_" + key;
                                    Log.i("xianrui", "push_icon_name " + icon);
                                    API.updateUserProfileHttp(UserModel.getInstance().getUserId(),
                                            icon, null, 0, null, null, null, null, null, new OnRequestEnd() {
                                                @Override
                                                public void onRequestSuccess(String response) {
                                                    UserModel.getInstance().tryLoadRemote(true);
                                                    setView();
                                                }

                                                @Override
                                                public void onRequestFail(VolleyError error) {

                                                }
                                            });
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //从系统相机回来, dialog可能被销毁. 为了解决这个bug, 重建dialog
        //同时在manifest里面,设置了main activity的属性
        //android:configChanges="orientation|keyboardHidden|screenSize"
        if (imagePickDialog == null) {
            createImagePickDialog();
        }
        imagePickDialog.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }


    public void editNickNameDialog() {
        AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
        alertParams.setCancelString("取消");
        alertParams.setConfirmString("确定");
        alertParams.setTitle("修改昵称");
        final EditText editText = new EditText(getActivity());
        editText.setSingleLine();
        editText.setText(UserModel.getInstance().getUserDto().getUser().getNick_name());
        editText.setSelection(editText.getText().length());
        alertParams.setView(editText);
        alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int which) {
                if ("".equals(editText.getText().toString().trim())) {
                    Toast.makeText(getActivity(), "昵称不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.updateUserProfileHttp(user.getId(), null, editText.getText().toString(), 0, null, null, null, null, null, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            Log.i("darren", response);
                            EventBus.getDefault().post(new CloseLoadingEvent());
                            dialog.dismiss();

                            Toast.makeText(getActivity(), "修改成功", Toast.LENGTH_SHORT).show();
                            UserModel.getInstance().updateUserDto(UserDto.parserJson(response));
                        }
                    });
                }

            }
        });
        alertParams.setCancelListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertHelper.showAlert(getActivity(), alertParams);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager inputMethodManager = (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                inputMethodManager.showSoftInput(editText, 0);
            }
        }, 300);

    }
}

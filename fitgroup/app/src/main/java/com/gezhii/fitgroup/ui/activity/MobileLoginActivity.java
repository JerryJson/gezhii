package com.gezhii.fitgroup.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.UserDto;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.CloseLoginActivityEvent;
import com.gezhii.fitgroup.event.JoinGroupSuccessEvent;
import com.gezhii.fitgroup.event.JumpToChooseTagsEvent;
import com.gezhii.fitgroup.event.JumpToGroupApplicationEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.NewMemberMessageExt;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.ui.dialog.LoadingDialog;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/12/17.
 */
public class MobileLoginActivity extends Activity {
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
    @InjectView(R.id.mobile_input)
    EditText mobileInput;
    @InjectView(R.id.confirm_input)
    EditText confirmInput;
    @InjectView(R.id.send_message_confirm_btn)
    TextView sendMessageConfirmBtn;
    @InjectView(R.id.voice_btn)
    TextView voiceBtn;
    @InjectView(R.id.login)
    RelativeLayout login;
    private LoadingDialog loadDialog;
    private int mGroupId;
    private String mLeaderHuanxinId;
    private boolean mNeedCheck;

    private boolean hasTags = false;//判断用户是否选择过标签

    public static void start(Context context) {
        Intent goMobileLoginAct = new Intent(context, MobileLoginActivity.class);
        context.startActivity(goMobileLoginAct);
    }

    public static void start(Context context, int group_id, boolean need_check, String leader_huanxin_id) {
        Intent goMobileLoginAct = new Intent(context, MobileLoginActivity.class);
        goMobileLoginAct.putExtra("group_id", group_id);
        goMobileLoginAct.putExtra("need_check", need_check);
        goMobileLoginAct.putExtra("leader_huanxin_id", leader_huanxin_id);
        context.startActivity(goMobileLoginAct);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.mobile_login_activity);
        ButterKnife.inject(this);
        loadDialog = new LoadingDialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        initTitleView();
        Intent intent = getIntent();
        mGroupId = intent.getIntExtra("group_id", -1);
        Log.i("mobile-groupId", mGroupId);
        if (mGroupId != -1) {
            mLeaderHuanxinId = intent.getStringExtra("leader_huanxin_id");
            mNeedCheck = intent.getBooleanExtra("need_check", false);
        }
        sendMessageConfirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile_num = mobileInput.getText().toString();
                if (mobile_num.length() != 11) {
                    Toast.makeText(MobileLoginActivity.this, "手机号长度不正确", Toast.LENGTH_SHORT).show();
                } else {
                    API.SendSms(mobile_num, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            Toast.makeText(MobileLoginActivity.this, "获取验证码中", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
        voiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mobile_num = mobileInput.getText().toString();
                if (mobile_num.length() != 11) {
                    Toast.makeText(MobileLoginActivity.this, "手机号长度不正确", Toast.LENGTH_SHORT).show();
                } else {
                    API.SendVoiceCode(mobile_num, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            Toast.makeText(MobileLoginActivity.this, "获取验证码中", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        confirmInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    login.setBackgroundResource(R.drawable.rounded_rectangle_blue);
                    login.setClickable(true);
                } else {
                    login.setBackgroundResource(R.drawable.rounded_rectangle_gray_frame_bg_gray97);
                    login.setClickable(false);
                }
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                final HashMap<String, String> hashMap = new HashMap<>();
//                hashMap.put("userName", "");
//                hashMap.put("userIcon", "");
                String loginType = "auto";
                String third_party_id = mobileInput.getText().toString();
                String sms_code = confirmInput.getText().toString();
                showLoading();
                API.loginHttp(loginType, third_party_id, "", "", sms_code, new APICallBack() {
                    @Override
                    public void onRequestSuccess(String response) {
                        hideLoading();
                        super.onRequestSuccess(response);
                    }

                    @Override
                    public void subRequestSuccess(String response) {
                        hideLoading();
                        UserDto userDto = UserDto.parserJson(response);
                        UserModel.getInstance().updateUserDto(userDto);
                        if (userDto.getUser().getTags() != null && userDto.getUser().getTags().size() > 0) {
                            hasTags = true;
                        }
                        Log.i("darrenHasTag", hasTags);
                        int userId = userDto.getUser().getId();
                        //hashMap.put("userId", userId);
                        if (userDto.getUser().getNick_name() == null || ("".equals(userDto.getUser().getNick_name()))) {
                            //Intent goHeadProtraitAndNicknameAct = new Intent(MobileLoginActivity.this, HeadProtraitAndNicknameActivity.class);
                            //goHeadProtraitAndNicknameAct.putExtra("hashMap", hashMap);
                            //startActivity(goHeadProtraitAndNicknameAct);
                            if (mGroupId == -1) {
                                HeadProtraitAndNicknameActivity.start(MobileLoginActivity.this, "", "", userId);
                            } else {
                                HeadProtraitAndNicknameActivity.start(MobileLoginActivity.this, "", "", userId, mGroupId, mNeedCheck, mLeaderHuanxinId);
                            }

                            finish();
                        } else {
                            registerChatServer();
                        }

                    }

                    @Override
                    public void onRequestFail(VolleyError error) {
                        super.onRequestFail(error);
                        hideLoading();
                    }
                });
            }
        });
        login.setClickable(false);
    }


    Handler mHandler = new Handler();

    public void registerChatServer() {

        EMChatManager.getInstance().login(UserModel.getInstance().getUserHuanXinName()
                , UserModel.getInstance().getUserDto().getUser().getHuanxin_password()
                , new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        hideLoading();
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                        Log.d(getClass().getSimpleName(), "登陆聊天服务器成功！");


                        if (mGroupId != -1) {
                            if (mNeedCheck) {
                                Log.i("head-groupId----", mGroupId);
                                HashMap<String, Object> params = new HashMap<String, Object>();
                                params.put("group_id", mGroupId);
                                params.put("leader_huanxin_id", mLeaderHuanxinId);
                                EventBus.getDefault().post(new JumpToGroupApplicationEvent(params));
                                finish();
                            } else {
                                EventBus.getDefault().post(new ShowLoadingEvent());
                                API.fastJoinGroup(UserModel.getInstance().getUserId(), mGroupId, new APICallBack() {
                                    @Override
                                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                                        EventBus.getDefault().post(new CloseLoginActivityEvent());
                                        EventBus.getDefault().post(new CloseLoadingEvent());
                                        mHandler.postDelayed(new Runnable() {
                                            @Override
                                            public void run() {
                                                EventBus.getDefault().post(new JoinGroupSuccessEvent());
                                            }
                                        }, 500);
                                        finish();
                                        Toast.makeText(MobileLoginActivity.this, "你已成功加入公会", Toast.LENGTH_SHORT).show();

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
                                    }

                                    @Override
                                    public void subRequestFail(String error_msg) throws NoSuchFieldException {
                                        super.subRequestFail(error_msg);
                                        EventBus.getDefault().post(new CloseLoginActivityEvent());
                                        EventBus.getDefault().post(new CloseLoadingEvent());
                                        Toast.makeText(MyApplication.getApplication(), error_msg, Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            }
                        } else {
                            EventBus.getDefault().post(new CloseLoginActivityEvent());
                            if (!hasTags) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("state_selected_position", 5);
                                Intent intent = new Intent(MobileLoginActivity.this, MainActivity.class);
                                intent.putExtras(bundle);
                                startActivity(intent);
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        EventBus.getDefault().post(new JumpToChooseTagsEvent());
                                    }
                                }, 500);
                            } else {
                                startActivity(new Intent(MobileLoginActivity.this, MainActivity.class));
                            }
                            finish();
                        }
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                hideLoading();
                Log.d(getClass().getSimpleName(), "registerChatServer onError " + s);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler = null;
    }

    private void initTitleView() {
        titleText.setText(getString(R.string.mobile_login));
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showLoading() {
        if (!loadDialog.isShowing()) {
            loadDialog.show();
        }
    }

    private void hideLoading() {
        if (loadDialog.isShowing()) {
            loadDialog.dismiss();
        }
    }

}

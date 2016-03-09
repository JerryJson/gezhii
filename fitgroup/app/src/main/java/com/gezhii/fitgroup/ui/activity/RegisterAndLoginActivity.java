package com.gezhii.fitgroup.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.UserDto;
import com.gezhii.fitgroup.event.CloseLoginActivityEvent;
import com.gezhii.fitgroup.event.JumpToChooseTagsEvent;
import com.gezhii.fitgroup.event.JumpToGroupApplicationEvent;
import com.gezhii.fitgroup.event.LoginConflictEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.ui.dialog.LoadingDialog;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.mob.tools.utils.UIHandler;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/11/12.
 */
public class RegisterAndLoginActivity extends Activity implements Handler.Callback,
        PlatformActionListener {

    private LoadingDialog loadDialog;
    private static final int MSG_USERID_FOUND = 1;
    private static final int MSG_LOGIN = 2;
    private static final int MSG_AUTH_CANCEL = 3;
    private static final int MSG_AUTH_ERROR = 4;
    private static final int MSG_AUTH_COMPLETE = 5;


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
    @InjectView(R.id.wechat_login_img)
    ImageView wechatLoginImg;
    @InjectView(R.id.qq_login_img)
    ImageView qqLoginImg;
    @InjectView(R.id.sina_login_img)
    ImageView sinaLoginImg;
    @InjectView(R.id.mobile_login)
    RelativeLayout mobileLogin;

    private int mGroupId = -1;
    private String mLeaderHuanxinId;
    private boolean mNeedCheck;

    private boolean hasTags = false;//判断用户是否选择过标签

    public static void start(Context context) {
        context.startActivity(new Intent(context, RegisterAndLoginActivity.class));
    }

    public static void start(Context context, int group_id, boolean need_check, String leader_huanxin_id) {//未登录时点击加入公会
        Intent intent = new Intent(context, RegisterAndLoginActivity.class);
        intent.putExtra("group_id", group_id);
        intent.putExtra("need_check", need_check);
        intent.putExtra("leader_huanxin_id", leader_huanxin_id);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.register_and_login_activity);
        ButterKnife.inject(this);
        MobclickAgent.onEvent(this, "login", UmengEvents.getEventMap("click", "load"));
        loadDialog = new LoadingDialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        Intent mIntent = getIntent();
        mGroupId = mIntent.getIntExtra("group_id", -1);
        Log.i("Register-groupId", mGroupId);
        if (mGroupId != -1) {
            mLeaderHuanxinId = mIntent.getStringExtra("leader_huanxin_id");
            mNeedCheck = mIntent.getBooleanExtra("need_check", false);
        }
        ShareSDK.initSDK(this);
        initView();
        EventBus.getDefault().registerSticky(this);

    }

    public void initView() {
        backBtn.setVisibility(View.INVISIBLE);
//        backBtn.setImageResource(R.mipmap.bmi_close);
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                MobclickAgent.onEvent(RegisterAndLoginActivity.this, "login", UmengEvents.getEventMap("click", "cancel"));
//                finish();
//            }
//        });
        titleText.setText("注册/登录");
        wechatLoginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(RegisterAndLoginActivity.this, "login", UmengEvents.getEventMap("click", "微信登入"));
                Platform platform = ShareSDK.getPlatform(Wechat.NAME);
                if (true == platform.isClientValid()) {
                    showLoading();
                    authorize(new Wechat(RegisterAndLoginActivity.this), "wechat");
                } else {
                    Toast.makeText(RegisterAndLoginActivity.this, "请先安装微信客户端", Toast.LENGTH_SHORT).show();
                }

            }
        });
        qqLoginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(RegisterAndLoginActivity.this, "login", UmengEvents.getEventMap("click", "QQ登入"));
                showLoading();
                authorize(new QQ(RegisterAndLoginActivity.this), "qq");
            }
        });
        sinaLoginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(RegisterAndLoginActivity.this, "login", UmengEvents.getEventMap("click", "微博登入"));
                showLoading();
                authorize(new SinaWeibo(RegisterAndLoginActivity.this), "sina");
            }
        });
        mobileLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent goMobileLoginAct = new Intent(RegisterAndLoginActivity.this, MobileLoginActivity.class);
//                startActivity(goMobileLoginAct);
                if (mGroupId == -1) {
                    MobileLoginActivity.start(RegisterAndLoginActivity.this);
                } else {
                    MobileLoginActivity.start(RegisterAndLoginActivity.this, mGroupId, mNeedCheck, mLeaderHuanxinId);
                }

            }
        });
    }

    public void onEventMainThread(JumpToGroupApplicationEvent jumpToGroupApplicationEvent) {
        finish();
    }

    public void onEventMainThread(CloseLoginActivityEvent closeLoginActivityEvent) {
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideLoading();
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

    protected void onDestroy() {
        ShareSDK.stopSDK(this);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        mHandler = null;
    }

    private void authorize(Platform plat, String loginType) {
        if (plat == null) {
            //popupOthers();
            return;
        }
        Log.i("plat.isAuthValid()------------->" + plat.isAuthValid());
        //判断指定平台是否已经完成授权
        if (plat.isAuthValid()) {
            System.out.println("------User Name ---------" + plat.getDb().getUserName());
            System.out.println("------User ID ---------" + plat.getDb().getUserId());
            System.out.println("------User Icon---------" + plat.getDb().getUserIcon());
            System.out.println("------User loginType---------" + plat.getDb().getPlatformNname());
            String third_party_id = plat.getDb().getUserId();
            String userName = plat.getDb().getUserName();
            String userIcon = plat.getDb().getUserIcon();
            showLoading();
            API.loginHttp(loginType, third_party_id, "", "", "", new APICallBack() {
                @Override
                public void subRequestSuccess(String response) {
                    UserDto userDto = UserDto.parserJson(response);
                    UserModel.getInstance().updateUserDto(userDto);
                    if (userDto.getUser().getTags() != null && userDto.getUser().getTags().size() > 0) {
                        hasTags = true;
                    }
                    registerChatServer();
                }
            });
            if (third_party_id != null) {
                UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
                login(plat.getName(), third_party_id, null);
                return;
            }
        }
        plat.setPlatformActionListener(this);
        // true不使用SSO授权，false使用SSO授权
        plat.SSOSetting(true);
        //获取用户资料
        plat.showUser(null);
    }

    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
            login(platform.getName(), platform.getDb().getUserId(), res);
        }
        String third_party_id = platform.getDb().getUserId();
        final String userName = platform.getDb().getUserName();
        final String userIcon = platform.getDb().getUserIcon();
        String loginType = platform.getDb().getPlatformNname().trim();
        if (loginType.equals("Wechat")) {
            loginType = "wechat";
        } else if (loginType.equals("QQ")) {
            loginType = "qq";
        } else if (loginType.equals("SinaWeibo")) {
            loginType = "sina";
        }
//        final HashMap<String, String> hashMap = new HashMap<>();
//        hashMap.put("userName", userName);
//        hashMap.put("userIcon", userIcon);
        API.loginHttp(loginType, third_party_id, "", "", "", new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                UserDto userDto = UserDto.parserJson(response);
                UserModel.getInstance().updateUserDto(userDto);
                if (userDto.getUser().getTags() != null && userDto.getUser().getTags().size() > 0) {
                    hasTags = true;
                }
                int userId = userDto.getUser().getId();
                // hashMap.put("userId", userId);
                if (userDto.getUser().getNick_name() == null || ("".equals(userDto.getUser().getNick_name()))) {
//                        Intent goHeadProtraitAndNicknameAct = new Intent(RegisterAndLoginActivity.this, HeadProtraitAndNicknameActivity.class);
//                        goHeadProtraitAndNicknameAct.putExtra("hashMap", hashMap);
//                        startActivity(goHeadProtraitAndNicknameAct);
                    if (mGroupId == -1) {
                        HeadProtraitAndNicknameActivity.start(RegisterAndLoginActivity.this, userName, userIcon, userId);
                    } else {
                        HeadProtraitAndNicknameActivity.start(RegisterAndLoginActivity.this, userName, userIcon, userId, mGroupId, mNeedCheck, mLeaderHuanxinId);
                    }

                    finish();
                } else {
                    registerChatServer();
                }

            }
        });
    }

    public void onError(Platform platform, int action, Throwable t) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
        }
        t.printStackTrace();
    }

    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
        }
    }

    private void login(String plat, String userId,
                       HashMap<String, Object> userInfo) {
        Message msg = new Message();
        msg.what = MSG_LOGIN;
        msg.obj = plat;
        UIHandler.sendMessage(msg, this);

    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_USERID_FOUND: {
                // Toast.makeText(this, "用户信息已存在，正在跳转登录操作…", Toast.LENGTH_SHORT).show();
            }
            break;
            case MSG_LOGIN: {
                String text = getString(R.string.logining, msg.obj);
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
                System.out.println("---------------");
            }
            break;
            case MSG_AUTH_CANCEL: {
                hideLoading();
                //  Toast.makeText(this, "授权操作已取消", Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_AUTH_CANCEL--------");
            }
            break;
            case MSG_AUTH_ERROR: {
                hideLoading();
                // Toast.makeText(this, "授权操作遇到错误，请阅读Logcat输出", Toast.LENGTH_SHORT).show();
                System.out.println("-------MSG_AUTH_ERROR--------");
            }
            break;
            case MSG_AUTH_COMPLETE: {
                //Toast.makeText(this, "授权成功，正在跳转登录操作…", Toast.LENGTH_SHORT).show();
                System.out.println("--------MSG_AUTH_COMPLETE-------");
            }
            break;
        }
        return false;
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
                        if (!hasTags) {
                            Bundle bundle = new Bundle();
                            bundle.putInt("state_selected_position", 5);
                            Intent intent = new Intent(RegisterAndLoginActivity.this, MainActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    EventBus.getDefault().post(new JumpToChooseTagsEvent());
                                }
                            }, 500);
                        } else {
                            startActivity(new Intent(RegisterAndLoginActivity.this, MainActivity.class));
                        }
                        finish();
//                        if (mGroupId != -1) {
//                            if (mNeedCheck) {
//                                Log.i("head-groupId----", mGroupId);
//                                HashMap<String, Object> params = new HashMap<String, Object>();
//                                params.put("group_id", mGroupId);
//                                params.put("leader_huanxin_id", mLeaderHuanxinId);
//                                EventBus.getDefault().post(new JumpToGroupApplicationEvent(params));
//                                finish();
//                            } else {
//                                EventBus.getDefault().post(new ShowLoadingEvent());
//                                API.fastJoinGroup(UserModel.getInstance().getUserId(), mGroupId, new APICallBack() {
//                                    @Override
//                                    public void subRequestSuccess(String response) throws NoSuchFieldException {
//                                        EventBus.getDefault().post(new CloseLoadingEvent());
//                                        mHandler.postDelayed(new Runnable() {
//                                            @Override
//                                            public void run() {
//                                                EventBus.getDefault().post(new JoinGroupSuccessEvent());
//                                            }
//                                        }, 500);
//                                        finish();
//                                        Toast.makeText(RegisterAndLoginActivity.this, "你已成功加入公会", Toast.LENGTH_SHORT).show();
//
//                                        //api返回值里面,传回了整个User对象
//                                        UserDto userDto = UserDto.parserJson(response);
//                                        UserModel.getInstance().updateUserDto(userDto);
//
//                                        //某人入会,在群里发条消息
//                                        HuanXinHelper.sendGroupTextMessage(new NewMemberMessageExt(UserModel.getInstance().getUserNickName(),
//                                                        UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), UserModel.getInstance().getUserNickName()),
//                                                UserModel.getInstance().getMyGroup().getGroup_huanxin_id(), "欢迎@" + UserModel.getInstance().getUserNickName() + "加入我们，请先做一下自我介绍吧",
//                                                new EMCallBack() {
//                                                    @Override
//                                                    public void onSuccess() {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onError(int i, String s) {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onProgress(int i, String s) {
//
//                                                    }
//                                                });
//                                    }
//
//                                    @Override
//                                    public void subRequestFail(String error_msg) throws NoSuchFieldException {
//                                        super.subRequestFail(error_msg);
//                                        EventBus.getDefault().post(new CloseLoadingEvent());
//                                        Toast.makeText(MyApplication.getApplication(), error_msg, Toast.LENGTH_SHORT).show();
//                                        finish();
//                                    }
//                                });
//
//                            }
//
//                        } else {
//                            finish();
//                        }
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

    public void onEvent(LoginConflictEvent loginConflictEvent) {
        EventBus.getDefault().removeStickyEvent(loginConflictEvent);
        AlertHelper.AlertParams params = new AlertHelper.AlertParams();
        params.setTitle("您的帐号已在其他设备上登录");
        params.setConfirmString("确定");
        params.setConfirmListener(new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertHelper.showAlert(this, params);
    }

}

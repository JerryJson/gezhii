package com.gezhii.fitgroup.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ResInformationDto;
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
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.dialog.ImagePickDialog;
import com.gezhii.fitgroup.ui.dialog.LoadingDialog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.xianrui.lite_common.litesuits.android.log.Log;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 15/11/17.
 */
public class HeadProtraitAndNicknameActivity extends Activity {
    private LoadingDialog loadDialog;
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
    @InjectView(R.id.head_protrait_img)
    ImageView headProtraitImg;
    @InjectView(R.id.nickname_input)
    EditText nicknameInput;
    @InjectView(R.id.sure_layout)
    RelativeLayout sureLayout;

    private String loginType, userName, userIcon, phone_user_icon;
    private int userId;
    ImagePickDialog imagePickDialog;
    private int mGroupId = -1;
    private String mLeaderHuanxinId;
    private boolean mNeedCheck;

    public static void start(Context context, String user_name, String user_icon, int user_id) {
        Intent intent = new Intent(context, HeadProtraitAndNicknameActivity.class);
        intent.putExtra("userName", user_name);
        intent.putExtra("userIcon", user_icon);
        intent.putExtra("userId", user_id);
        context.startActivity(intent);
    }

    public static void start(Context context, String user_name, String user_icon, int user_id, int group_id, boolean need_check, String leader_huanxin_id) {
        Intent intent = new Intent(context, HeadProtraitAndNicknameActivity.class);
        intent.putExtra("userName", user_name);
        intent.putExtra("userIcon", user_icon);
        intent.putExtra("userId", user_id);
        intent.putExtra("group_id", group_id);
        intent.putExtra("need_check", need_check);
        intent.putExtra("leader_huanxin_id", leader_huanxin_id);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.head_protrait_and_nickname_activity);
        ButterKnife.inject(this);
        loadDialog = new LoadingDialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        initView();
    }


    private void initView() {
        titleText.setText("头像和昵称");
        backBtn.setVisibility(View.INVISIBLE);
        //HashMap<String, String> hashMap = new HashMap<>();
        Intent intent = getIntent();
        userId = intent.getIntExtra("userId", -1);
        userName = intent.getStringExtra("userName");
        userIcon = intent.getStringExtra("userIcon");
        mGroupId = intent.getIntExtra("group_id", -1);
        Log.i("head-groupId", mGroupId);
        if (mGroupId != -1) {
            mLeaderHuanxinId = intent.getStringExtra("leader_huanxin_id");
            mNeedCheck = intent.getBooleanExtra("need_check", false);
        }
        // hashMap = (HashMap) intent.getSerializableExtra("hashMap");
//        userId = Integer.valueOf(hashMap.get("userId"));
//        userName = hashMap.get("userName");
//        userIcon = hashMap.get("userIcon");
        if (!userIcon.equals("")) {
            ImageLoader.getInstance().displayImage(userIcon, headProtraitImg);
        }
        headProtraitImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagePickDialog == null) {
                    createImagePickDialog();
                }
                imagePickDialog.show();
            }
        });
        nicknameInput.setText(userName);
        sureLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("".equals(nicknameInput.getText().toString().trim())) {
                    Toast.makeText(HeadProtraitAndNicknameActivity.this, "昵称不能为空", Toast.LENGTH_SHORT).show();
                } else {

                    userName = nicknameInput.getText().toString().trim();
                    if (!TextUtils.isEmpty(userIcon)) {
                        showLoading();
                        downLodaImg(userIcon);
                    } else {
                        if (TextUtils.isEmpty(phone_user_icon)) {
                            Toast.makeText(HeadProtraitAndNicknameActivity.this, "请上传头像", Toast.LENGTH_SHORT).show();
                        } else {
                            showLoading();
                            API.updateUserProfileHttp(UserModel.getInstance().getUserId(),
                                    phone_user_icon, userName, 0, null, null, null, null, null, new OnRequestEnd() {

                                        @Override
                                        public void onRequestSuccess(String response) {
                                            UserModel.getInstance().tryLoadRemote(true);
                                            hideLoading();
                                            registerChatServer();
                                        }

                                        @Override
                                        public void onRequestFail(VolleyError error) {
                                            hideLoading();
                                            Toast.makeText(HeadProtraitAndNicknameActivity.this, "提交失败,请重试", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                    }

                }
            }
        });
    }

    private void createImagePickDialog() {
        imagePickDialog = new ImagePickDialog(HeadProtraitAndNicknameActivity.this);
        imagePickDialog.setCallback(new ImagePickDialog.Callback() {
            @Override
            public void onImageBack(ResInformationDto resInformationDto) {
                showLoading();
                QiniuHelper.uploadImgs(UserModel.getInstance().getUserId(), resInformationDto.getPushFileName(), resInformationDto.getFilePath(),
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                hideLoading();
                                if (info.isOK()) {
                                    phone_user_icon = QiniuHelper.QINIU_SPACE + "_" + key;
                                    QiniuHelper.bindAvatarImage(phone_user_icon, headProtraitImg);
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (imagePickDialog == null) {
            createImagePickDialog();
        }
        imagePickDialog.onActivityResult(requestCode, resultCode, data);
    }


    private void downLodaImg(String url) {
        ImageLoader.getInstance().loadImage(url, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                hideLoading();
                Log.i("failReason------>" + failReason);
                Toast.makeText(HeadProtraitAndNicknameActivity.this, "提交失败,请重试", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                String localFile = saveBitmap(loadedImage);
                QiniuHelper.uploadImgs(UserModel.getInstance().getUserId(), localFile,
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                if (info.isOK()) {
                                    String icon = QiniuHelper.QINIU_SPACE + "_" + key;
                                    API.updateUserProfileHttp(UserModel.getInstance().getUserId(),
                                            icon, null, 0, null, null, null, null, null, new OnRequestEnd() {
                                                @Override
                                                public void onRequestSuccess(String response) {
                                                    UserModel.getInstance().tryLoadRemote(true);
                                                    API.updateUserProfileHttp(userId, UserModel.getInstance().getUserIcon(), userName, 0, null, null, null, null, null, new APICallBack() {
                                                        @Override
                                                        public void subRequestSuccess(String response) {
                                                            UserModel.getInstance().tryLoadRemote(true);
                                                            hideLoading();
                                                            registerChatServer();
                                                        }
                                                    });
                                                }

                                                @Override
                                                public void onRequestFail(VolleyError error) {
                                                    hideLoading();
                                                    Toast.makeText(HeadProtraitAndNicknameActivity.this, "提交失败,请重试", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    hideLoading();
                                    Toast.makeText(HeadProtraitAndNicknameActivity.this, "提交失败,请重试", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });
    }

    public static String getNowTimeAccurate() {

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String time = sdFormat.format(new Date());

        return time;
    }

    public static String saveBitmap(Bitmap bitmap) {
        String localPath = Environment.getExternalStorageDirectory()
                + "/fitGroup/Image/";
        String localFile = localPath + getNowTimeAccurate() + ".jpg";
        File f = new File(localPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        File imageFile = new File(localFile);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localFile;
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

    Handler mHandler = new Handler();

    public void registerChatServer() {
        EMChatManager.getInstance().login(UserModel.getInstance().getUserHuanXinName()
                , UserModel.getInstance().getUserDto().getUser().getHuanxin_password()
                , new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
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
                                        Toast.makeText(HeadProtraitAndNicknameActivity.this, "你已成功加入公会", Toast.LENGTH_SHORT).show();

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
                                });
                            }
                        } else {
                            EventBus.getDefault().post(new CloseLoginActivityEvent());
                            Bundle bundle = new Bundle();
                            bundle.putInt("state_selected_position", 5);
                            Intent intent = new Intent(HeadProtraitAndNicknameActivity.this, MainActivity.class);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    EventBus.getDefault().post(new JumpToChooseTagsEvent());
                                }
                            }, 500);

                            finish();
                        }

                    }
                });
            }

            @Override
            public void onError(int i, String s) {
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

    public void registerChatServerNoJump() {
        EMChatManager.getInstance().login(UserModel.getInstance().getUserHuanXinName()
                , UserModel.getInstance().getUserDto().getUser().getHuanxin_password()
                , new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                        Log.d(getClass().getSimpleName(), "登陆聊天服务器成功！");
//                        startActivity(new Intent(HeadProtraitAndNicknameActivity.this, MainActivity.class));
                        finish();
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                Log.d(getClass().getSimpleName(), "registerChatServer onError " + s);
            }

            @Override
            public void onProgress(int i, String s) {
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            return false;
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
}

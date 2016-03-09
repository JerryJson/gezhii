package com.gezhii.fitgroup.ui.fragment.signin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ResInformationDto;
import com.gezhii.fitgroup.dto.SigninDto;
import com.gezhii.fitgroup.dto.basic.SignLeaveOrContent;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.JustSigninEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.event.SignLeaveOrContentEditEvent;
import com.gezhii.fitgroup.event.SigninCompleteEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.SignMessageExt;
import com.gezhii.fitgroup.model.SignCacheModel;
import com.gezhii.fitgroup.model.SignRecordModel;
import com.gezhii.fitgroup.model.UserCustomerTaskModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.dialog.ImagePickDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.group.GroupChatFragment;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/30.
 */
public class SignAddContentFragment extends BaseFragment {

    public final static String TAG_SIGNIN_TASK_NAME = "tag_signin_task_name";
    public final static String TAG_SIGNIN_BODY_WEIGHT = "tag_signin_body_weight";
    public final static String TAG_CUSTOMER_TASK_ID = "tag_customer_task_id";
    public final static String TAG_IS_IMAGE_SIGN = "tag_is_image_sign";
    public final static String TAG_SIGNIN_ID = "tag_signin_id";//已经打过卡，用于更新卡的图片和文字
    @InjectView(R.id.sign_content_photo_img)
    ImageView signContentPhotoImg;
    @InjectView(R.id.content_img_input_btn)
    ImageView contentImgInputBtn;
    @InjectView(R.id.content_img_layout)
    LinearLayout contentImgLayout;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.sign_task_text)
    TextView signTaskText;
    @InjectView(R.id.sign_content_input)
    TextView signContentInput;
    @InjectView(R.id.sign_content_layout)
    LinearLayout signContentLayout;
    @InjectView(R.id.share_wechat)
    ImageView shareWechat;
    @InjectView(R.id.share_wechat_mask)
    ImageView shareWechatMask;
    @InjectView(R.id.share_wechat_btn)
    FrameLayout shareWechatBtn;
    @InjectView(R.id.share_wechat_moments)
    ImageView shareWechatMoments;
    @InjectView(R.id.share_wechat_moments_mask)
    ImageView shareWechatMomentsMask;
    @InjectView(R.id.share_wechat_moments_btn)
    FrameLayout shareWechatMomentsBtn;
    @InjectView(R.id.share_weibo)
    ImageView shareWeibo;
    @InjectView(R.id.share_weibo_mask)
    ImageView shareWeiboMask;
    @InjectView(R.id.share_weibo_btn)
    FrameLayout shareWeiboBtn;
    @InjectView(R.id.share_qq)
    ImageView shareQq;
    @InjectView(R.id.share_qq_mask)
    ImageView shareQqMask;
    @InjectView(R.id.share_qq_btn)
    FrameLayout shareQqBtn;
    @InjectView(R.id.publish_text)
    TextView publishText;
    @InjectView(R.id.sign_content_publish_layout)
    RelativeLayout signContentPublishLayout;
    SharedPreferences sharedPreferences;
    final int[] selected = {0, 0, 0, 0};
    public static final String TAG = "tag";
    public static final String TAG_WECHAT = "tag_wechat";
    public static final String TAG_WECHAT_MOMENT = "tag_wechat_moment";
    public static final String TAG_SINA_WEIBO = "tag_sina_weibo";
    public static final String TAG_QQ = "tag_qq";
    ImagePickDialog mImagePickDialog;
    @InjectView(R.id.back_text)
    TextView backText;
    @InjectView(R.id.skip_text)
    TextView skipText;
    @InjectView(R.id.back_btn)
    ImageView backBtn;
    private SignLeaveOrContent signLeaveOrContent;
    private String shareType = TAG;
    private int isDefault = 0;
    String taskName;
    String bodyWeight;
    String customerTaskId;//用户自定义任务id
    boolean isImageSign;//判断是否为图片打卡

    boolean hasAddPhoto = false;//是否添加图片
    ResInformationDto resInformationDto;

    int signinId = 0;

    public static void start(Activity activity, String task_name, String body_weight, String customer_task_id, boolean isImageSign) {

        start(activity, task_name, body_weight, customer_task_id, isImageSign, 0);

    }

    public static void start(Activity activity, String task_name, String body_weight, String customer_task_id, boolean isImageSign, int signin_id) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> params = new HashMap<>();
        params.put(TAG_SIGNIN_TASK_NAME, task_name);
        params.put(TAG_SIGNIN_BODY_WEIGHT, body_weight);
        params.put(TAG_CUSTOMER_TASK_ID, customer_task_id);
        params.put(TAG_IS_IMAGE_SIGN, isImageSign);
        params.put(TAG_SIGNIN_ID, signin_id);
        mainActivity.showNext(SignAddContentFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sign_add_content_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        EventBus.getDefault().register(this);
        MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("click", "load"));
        titleText.setText(R.string.edit_content);

        signinId = (int) getNewInstanceParams().get(TAG_SIGNIN_ID);
        if (getNewInstanceParams().get(TAG_IS_IMAGE_SIGN) != null) {
            isImageSign = (boolean) getNewInstanceParams().get(TAG_IS_IMAGE_SIGN);
        }


        if (UserModel.getInstance().isFromGroupSignin) {//群里打卡
            backBtn.setVisibility(View.INVISIBLE);
            if (isImageSign) {
                backText.setVisibility(View.VISIBLE);
                skipText.setVisibility(View.INVISIBLE);
                backText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
                //signContentPublishLayout.setBackgroundColor(getResources().getColor(R.color.gray_c8));
                //publishText.setText("发布");
            } else {
                backText.setVisibility(View.INVISIBLE);
                skipText.setVisibility(View.VISIBLE);
                //signContentPublishLayout.setClickable(true);
                skipText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        publish();
                    }
                });

            }
            signContentPublishLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    publish();
                }
            });
            signContentPublishLayout.setClickable(false);
        } else {
            backBtn.setVisibility(View.VISIBLE);
            skipText.setVisibility(View.INVISIBLE);
            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (signinId == 0) {
                        if (isImageSign) {
                            finish();
                        } else {
                            JustSignin();
                        }
                    } else {
                        finish();
                    }
                }
            });
            signContentPublishLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isImageSign && !hasAddPhoto) {
                        showToast("请添加图片");
                    } else {
                        if (signinId == 0) {
                            publish();
                        } else {
                            UpdateSignin();
                        }
                    }
                }
            });
            signContentPublishLayout.setClickable(false);
        }

        signLeaveOrContent = new SignLeaveOrContent();
        signContentInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signLeaveOrContent.setIsLeaveSign(false);
                signLeaveOrContent.setContent(signContentInput.getText().toString().trim());
                SignLeaveOrContentEditFragment.start(getActivity(), signLeaveOrContent);
            }
        });


        taskName = (String) getNewInstanceParams().get(TAG_SIGNIN_TASK_NAME);
        bodyWeight = (String) getNewInstanceParams().get(TAG_SIGNIN_BODY_WEIGHT);
        customerTaskId = (String) getNewInstanceParams().get(TAG_CUSTOMER_TASK_ID);
        setView();
        return rootView;
    }

    public void publish() {
        MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("click", "send"));

        EventBus.getDefault().post(new ShowLoadingEvent());

        if (hasAddPhoto) {
            EventBus.getDefault().postSticky(new ShowLoadingEvent());
            QiniuHelper.uploadImgs(UserModel.getInstance().getUserId(), resInformationDto.getPushFileName(), resInformationDto.getFilePath(),
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            EventBus.getDefault().postSticky(new CloseLoadingEvent());
                            if (info.isOK()) {
                                MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("click", "add photo", "click", "success"));
                                signLeaveOrContent.img = QiniuHelper.QINIU_SPACE + "_" + key;
                                isDefault = 1;
                                Signin();
                            }
                        }
                    });
        } else {
            Signin();
        }

    }

    public String judgeShareType() {
        int shareToThird = sharedPreferences.getInt("shareToThird", -1);
        if (shareToThird == 0) {
            shareType = TAG_WECHAT;
        } else if (shareToThird == 1) {
            shareType = TAG_WECHAT_MOMENT;
        } else if (shareToThird == 2) {
            shareType = TAG_SINA_WEIBO;
        } else if (shareToThird == 3) {
            shareType = TAG_QQ;
        } else {
            shareType = TAG;
        }

        return shareType;
    }

    public void Signin() {
        shareType = judgeShareType();
        int is_share = 0;
        if (!shareType.equals(TAG)) {
            is_share = 1;
        }
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("task_name", taskName);
        if (!TextUtils.isEmpty(bodyWeight)) {
            hashMap.put("body_weight", Float.valueOf(bodyWeight));
        }
        if (TextUtils.isEmpty(signLeaveOrContent.getContent())) {
            MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("content", "empty"));
        } else {
            MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("content", "text"));
        }
        HashMap<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("task", hashMap);
        API.signinHttp(UserModel.getInstance().getUserId(),
                GsonHelper.getInstance().getGson().toJson(taskMap),
                signLeaveOrContent.getContent(),
                signLeaveOrContent.img, 2, is_share, isDefault, taskName, new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {

                        final SigninDto signinDto = SigninDto.parserJson(response);
                        SignCacheModel.getInstance().putSign(signinDto);
                        SignRecordModel.getInstance().setDateSignin(Calendar.getInstance().getTime());
                        if (UserModel.getInstance().isFromGroupSignin) {
                            if (UserModel.getInstance().getUserDto().getUser().getIsChecking() == 0 && UserModel.getInstance().getMyGroup() != null) {
                                HuanXinHelper.sendGroupTextMessage(new SignMessageExt(UserModel.getInstance().getUserNickName()
                                        , UserModel.getInstance().getUserIcon(), String.valueOf(signinDto.getSignin().getId()), String.valueOf(UserModel.getInstance().getUserId()))
                                        , UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(), "[打卡]", new EMCallBack() {
                                    @Override
                                    public void onSuccess() {
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                EventBus.getDefault().post(new CloseLoadingEvent());
                                                showToast("打卡成功");
                                                if (!TextUtils.isEmpty(customerTaskId)) {
                                                    UserCustomerTaskModel.getInstance().editUserCustomerTask(customerTaskId);
                                                }
                                                finishAll();
                                                GroupChatFragment.start(getActivity());
                                                if (!shareType.equals(TAG)) {
                                                    SignCardShareFragment.start(getActivity(), signinDto.getSignin().getId(), UserModel.getInstance().getUserDto().getUser().getHuanxin_id(), shareType);
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onError(int i, String s) {

                                    }

                                    @Override
                                    public void onProgress(int i, String s) {

                                    }
                                });
                            }
                        } else {
                            EventBus.getDefault().post(new CloseLoadingEvent());
                            EventBus.getDefault().post(new SigninCompleteEvent());
                            showToast("打卡成功");
                            if (!TextUtils.isEmpty(customerTaskId)) {
                                UserCustomerTaskModel.getInstance().editUserCustomerTask(customerTaskId);
                            }
                            finishAll();
                            if (!shareType.equals(TAG)) {
                                SignCardShareFragment.start(getActivity(), signinDto.getSignin().getId(), UserModel.getInstance().getUserDto().getUser().getHuanxin_id(), shareType);
                            }
                        }

                    }
                });
    }

    public void UpdateSignin() {
        MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("click", "send"));
        shareType = judgeShareType();
        int is_share = 0;
        if (!shareType.equals(TAG)) {
            is_share = 1;
        }
        EventBus.getDefault().postSticky(new ShowLoadingEvent());
        if (hasAddPhoto) {
            QiniuHelper.uploadImgs(UserModel.getInstance().getUserId(), resInformationDto.getPushFileName(), resInformationDto.getFilePath(),
                    new UpCompletionHandler() {
                        @Override
                        public void complete(String key, ResponseInfo info, JSONObject response) {
                            EventBus.getDefault().postSticky(new CloseLoadingEvent());
                            if (info.isOK()) {
                                MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("click", "add photo", "click", "success"));
                                signLeaveOrContent.img = QiniuHelper.QINIU_SPACE + "_" + key;
                                API.updateSignin(signinId, signLeaveOrContent.getContent(),
                                        signLeaveOrContent.img, new APICallBack() {
                                            @Override
                                            public void subRequestSuccess(String response) throws NoSuchFieldException {
                                                final SigninDto signinDto = SigninDto.parserJson(response);
                                                SignCacheModel.getInstance().putSign(signinDto);
                                                EventBus.getDefault().post(new CloseLoadingEvent());
                                                EventBus.getDefault().post(new SigninCompleteEvent());
                                                finishAll();
                                                if (!shareType.equals(TAG)) {
                                                    SignCardShareFragment.start(getActivity(), signinDto.getSignin().getId(), UserModel.getInstance().getUserDto().getUser().getHuanxin_id(), shareType);
                                                }
                                            }
                                        });
                            }
                        }
                    });
        } else {
            API.updateSignin(signinId, signLeaveOrContent.getContent(),
                    signLeaveOrContent.img, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            final SigninDto signinDto = SigninDto.parserJson(response);
                            SignCacheModel.getInstance().putSign(signinDto);
                            EventBus.getDefault().post(new CloseLoadingEvent());
                            EventBus.getDefault().post(new SigninCompleteEvent());
                            finishAll();
                            if (!shareType.equals(TAG)) {
                                SignCardShareFragment.start(getActivity(), signinDto.getSignin().getId(), UserModel.getInstance().getUserDto().getUser().getHuanxin_id(), shareType);
                            }
                        }
                    });
        }

    }

    public void JustSignin() {//1.3版本后点击返回的事件，只打卡，没有图片或文字
        MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("click", "send"));
        int shareToThird = sharedPreferences.getInt("shareToThird", -1);
        if (shareToThird == 0) {
            shareType = TAG_WECHAT;
        } else if (shareToThird == 1) {
            shareType = TAG_WECHAT_MOMENT;
        } else if (shareToThird == 2) {
            shareType = TAG_SINA_WEIBO;
        } else if (shareToThird == 3) {
            shareType = TAG_QQ;
        } else {
            shareType = TAG;
        }
        int is_share = 0;
        if (!shareType.equals(TAG)) {
            is_share = 1;
        }
        EventBus.getDefault().post(new ShowLoadingEvent());
        if (!TextUtils.isEmpty(signLeaveOrContent.img)) {
            isDefault = 1;
        }
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("task_name", taskName);
        if (!TextUtils.isEmpty(bodyWeight)) {
            hashMap.put("body_weight", Float.valueOf(bodyWeight));
        }
        if (TextUtils.isEmpty(signLeaveOrContent.getContent())) {
            MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("content", "empty"));
        } else {
            MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("content", "text"));
        }
        HashMap<String, Object> taskMap = new HashMap<String, Object>();
        taskMap.put("task", hashMap);
        API.signinHttp(UserModel.getInstance().getUserId(),
                GsonHelper.getInstance().getGson().toJson(taskMap),
                null,
                null, 0, is_share, isDefault, taskName, new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        final SigninDto signinDto = SigninDto.parserJson(response);
                        SignCacheModel.getInstance().putSign(signinDto);
                        SignRecordModel.getInstance().setDateSignin(Calendar.getInstance().getTime());
                        EventBus.getDefault().post(new CloseLoadingEvent());
                        EventBus.getDefault().post(new JustSigninEvent(signinDto.getSignin().getId()));
                        EventBus.getDefault().post(new SigninCompleteEvent());
                        showToast("打卡成功");
                        finish();
                        if (!TextUtils.isEmpty(customerTaskId)) {
                            UserCustomerTaskModel.getInstance().editUserCustomerTask(customerTaskId);
                        }
                        if (!shareType.equals(TAG)) {
                            SignCardShareFragment.start(getActivity(), signinDto.getSignin().getId(), UserModel.getInstance().getUserDto().getUser().getHuanxin_id(), shareType);
                        }
                    }
                });

    }

    @OnClick({R.id.sign_content_photo_img, R.id.content_img_input_btn})
    public void onAddImageClick(View v) {
        MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("click", "add photo"));
        if (mImagePickDialog == null) {
            createImagePickDialog();
        }
        mImagePickDialog.show();
    }

    public void onEventMainThread(SignLeaveOrContentEditEvent signLeaveOrContentEditEvent) {
        Log.i("darrens", signLeaveOrContentEditEvent.getParams().get("content"));
        signContentInput.setText(signLeaveOrContentEditEvent.getParams().get("content"));
        if (isImageSign && UserModel.getInstance().isFromGroupSignin) {

        } else {
            if (signLeaveOrContentEditEvent.getParams().get("content").length() > 0) {
                signContentPublishLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                signContentPublishLayout.setClickable(true);
            } else {
                signContentPublishLayout.setBackgroundColor(getResources().getColor(R.color.gray_c8));
                signContentPublishLayout.setClickable(false);
            }
        }

    }

    private void createImagePickDialog() {
        mImagePickDialog = new ImagePickDialog(this);
        mImagePickDialog.setCallback(new ImagePickDialog.Callback() {
            @Override
            public void onImageBack(ResInformationDto resInformation) {
                resInformationDto = resInformation;
                contentImgLayout.setVisibility(View.INVISIBLE);
                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                layoutParams.height = Screen.getScreenWidth();
                signContentPhotoImg.setLayoutParams(layoutParams);
                QiniuHelper.bindLocalImage(resInformationDto.getFilePath(), signContentPhotoImg);
                publishText.setText(getString(R.string.publication));
                signContentPublishLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                signContentPublishLayout.setClickable(true);
                hasAddPhoto = true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mImagePickDialog == null) {
            createImagePickDialog();
        }
        mImagePickDialog.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    private void setView() {
        signTaskText.setText(taskName);
        sharedPreferences = getActivity().getSharedPreferences("fitGroup", 0);
        int shareToThird = sharedPreferences.getInt("shareToThird", -1);
        if (shareToThird == 0) {
            shareWechatMask.setVisibility(View.VISIBLE);
            shareWechatMomentsMask.setVisibility(View.GONE);
            shareWeiboMask.setVisibility(View.GONE);
            shareQqMask.setVisibility(View.GONE);
            selected[0] = 1;
        } else if (shareToThird == 1) {
            shareWechatMask.setVisibility(View.GONE);
            shareWechatMomentsMask.setVisibility(View.VISIBLE);
            shareWeiboMask.setVisibility(View.GONE);
            shareQqMask.setVisibility(View.GONE);
            selected[1] = 1;
        } else if (shareToThird == 2) {
            shareWechatMask.setVisibility(View.GONE);
            shareWechatMomentsMask.setVisibility(View.GONE);
            shareWeiboMask.setVisibility(View.VISIBLE);
            shareQqMask.setVisibility(View.GONE);
            selected[2] = 1;
        } else if (shareToThird == 3) {
            shareWechatMask.setVisibility(View.GONE);
            shareWechatMomentsMask.setVisibility(View.GONE);
            shareWeiboMask.setVisibility(View.GONE);
            shareQqMask.setVisibility(View.VISIBLE);
            selected[3] = 1;
        } else {
            shareWechatMask.setVisibility(View.GONE);
            shareWechatMomentsMask.setVisibility(View.GONE);
            shareWeiboMask.setVisibility(View.GONE);
            shareQqMask.setVisibility(View.GONE);
        }

        shareToThirdListener();
    }

    public void judgeIsselect() {
        if (selected[0] == 1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("shareToThird", 0);
            editor.commit();
            shareWechatMask.setVisibility(View.VISIBLE);
            shareWechatMomentsMask.setVisibility(View.GONE);
            shareWeiboMask.setVisibility(View.GONE);
            shareQqMask.setVisibility(View.GONE);

        } else if (selected[1] == 1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("shareToThird", 1);
            editor.commit();
            shareWechatMask.setVisibility(View.GONE);
            shareWechatMomentsMask.setVisibility(View.VISIBLE);
            shareWeiboMask.setVisibility(View.GONE);
            shareQqMask.setVisibility(View.GONE);
        } else if (selected[2] == 1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("shareToThird", 2);
            editor.commit();
            shareWechatMask.setVisibility(View.GONE);
            shareWechatMomentsMask.setVisibility(View.GONE);
            shareWeiboMask.setVisibility(View.VISIBLE);
            shareQqMask.setVisibility(View.GONE);
        } else if (selected[3] == 1) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("shareToThird", 3);
            editor.commit();
            shareWechatMask.setVisibility(View.GONE);
            shareWechatMomentsMask.setVisibility(View.GONE);
            shareWeiboMask.setVisibility(View.GONE);
            shareQqMask.setVisibility(View.VISIBLE);
        } else {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("shareToThird", -1);
            editor.commit();
            shareWechatMask.setVisibility(View.GONE);
            shareWechatMomentsMask.setVisibility(View.GONE);
            shareWeiboMask.setVisibility(View.GONE);
            shareQqMask.setVisibility(View.GONE);
        }
    }


    public void shareToThirdListener() {

        shareWechatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "signin_edit", UmengEvents.getEventMap("click", "share 微信好友"));
                if (0 == selected[0]) {
                    for (int i = 0; i < selected.length; i++) {
                        selected[i] = 0;
                    }
                    selected[0] = 1;
                } else {
                    selected[0] = 0;
                }
                judgeIsselect();
            }
        });
        shareWechatMomentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "signin_edit", UmengEvents.getEventMap("click", "share 微信朋友圈"));
                if (0 == selected[1]) {
                    for (int i = 0; i < selected.length; i++) {
                        selected[i] = 0;
                    }
                    selected[1] = 1;
                } else {
                    selected[1] = 0;
                }
                judgeIsselect();
            }
        });
        shareWeiboBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "signin_edit", UmengEvents.getEventMap("click", "share 微博"));
                if (0 == selected[2]) {
                    for (int i = 0; i < selected.length; i++) {
                        selected[i] = 0;
                    }
                    selected[2] = 1;
                } else {
                    selected[2] = 0;
                }
                judgeIsselect();
            }
        });
        shareQqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getActivity(), "signin_edit", UmengEvents.getEventMap("click", "share QQ"));
                if (0 == selected[3]) {
                    for (int i = 0; i < selected.length; i++) {
                        selected[i] = 0;
                    }
                    selected[3] = 1;
                } else {
                    selected[3] = 0;
                }
                judgeIsselect();
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}

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
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.event.SignLeaveOrContentEditEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.SignMessageExt;
import com.gezhii.fitgroup.model.SignCacheModel;
import com.gezhii.fitgroup.model.SignInfoModel;
import com.gezhii.fitgroup.model.SignRecordModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.dialog.ImagePickDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.group.GroupChatFragment;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.xianrui.lite_common.litesuits.android.log.Log;

import org.json.JSONObject;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/11/16.
 */
public class SignLeaveFragment extends BaseFragment {

    @InjectView(R.id.back_btn)
    ImageView backBtn;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.content_img_input_btn)
    ImageView contentImgInputBtn;
    @InjectView(R.id.content_img_layout)
    LinearLayout contentImgLayout;
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
    @InjectView(R.id.sign_leave_layout)
    RelativeLayout signLeaveLayout;
    @InjectView(R.id.sign_leave_photo_img)
    ImageView signLeavePhotoImg;
    private String shareType = TAG;
    ImagePickDialog mImagePickDialog;
    String leaveImageString;
    SharedPreferences sharedPreferences;
    final int[] selected = {0, 0, 0, 0};
    public static final String TAG = "tag";
    public static final String TAG_WECHAT = "tag_wechat";
    public static final String TAG_WECHAT_MOMENT = "tag_wechat_moment";
    public static final String TAG_SINA_WEIBO = "tag_sina_weibo";
    public static final String TAG_QQ = "tag_qq";
    private int isDefault;

    private SignLeaveOrContent signLeaveOrContent;

    public static void start(Activity activity) {
        MainActivity mainActivity = (MainActivity) activity;
        mainActivity.showNext(SignLeaveFragment.class);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        View rootView = inflater.inflate(R.layout.sing_leave_fragment, null);
        ButterKnife.inject(this, rootView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        titleText.setText(R.string.leave);
        signLeaveOrContent = new SignLeaveOrContent();
        signContentInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signLeaveOrContent.setIsLeaveSign(true);
                signLeaveOrContent.setContent(signContentInput.getText().toString().trim());
                SignLeaveOrContentEditFragment.start(getActivity(), signLeaveOrContent);
            }
        });
        signLeaveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String contentString = signContentInput.getText().toString().trim();
                if (TextUtils.isEmpty(contentString)) {
                    showToast("请假理由必须填写");
                } else {
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
                    if (!TextUtils.isEmpty(signLeaveOrContent.img)) {
                        isDefault = 1;
                    }
                    EventBus.getDefault().post(new ShowLoadingEvent());
                    API.signinHttp(UserModel.getInstance().getUserId(),
                            "", contentString, leaveImageString, 1, is_share, isDefault, "", new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) throws NoSuchFieldException {
                                    final SigninDto signinDto = SigninDto.parserJson(response);
                                    SignCacheModel.getInstance().putSign(signinDto);
                                    SignRecordModel.getInstance().setDateSignin(Calendar.getInstance().getTime());
                                    //UserCacheModel.getInstance().setUserIcon(UserModel.getInstance().getUserDto().getUser().getHuanxin_id(),UserModel.getInstance().getUserIcon());
                                    if (UserModel.getInstance().getUserDto().getUser().getIsChecking() == 0 && UserModel.getInstance().getMyGroup() != null) {
                                        HuanXinHelper.sendGroupTextMessage(new SignMessageExt(UserModel.getInstance().getUserNickName()
                                                , UserModel.getInstance().getUserIcon(), String.valueOf(signinDto.getSignin().getId()), String.valueOf(UserModel.getInstance().getUserId()))
                                                , UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(), "[请假]", new EMCallBack() {
                                            @Override
                                            public void onSuccess() {
                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        EventBus.getDefault().post(new CloseLoadingEvent());
                                                        showToast("打卡成功");
                                                        finishAll();
                                                        SignInfoModel.getInstance().clearListData();
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
                                    } else {
                                        showToast("打卡成功");
                                        finishAll();
                                        SignInfoModel.getInstance().clearListData();
                                        GroupChatFragment.start(getActivity());
                                    }


                                }
                            });
                }
            }
        });
        setView();
        return rootView;
    }

    public void onEventMainThread(SignLeaveOrContentEditEvent signLeaveOrContentEditEvent) {
        Log.i("darrens", signLeaveOrContentEditEvent.getParams().get("content"));
        signContentInput.setText(signLeaveOrContentEditEvent.getParams().get("content"));
    }

    private void setView() {

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

    @OnClick({R.id.sign_leave_photo_img, R.id.content_img_input_btn})
    public void onImagePickBtnClick(View v) {
        if (mImagePickDialog == null) {
            createPickImageDialog();
        }
        mImagePickDialog.show();
    }


    private void createPickImageDialog() {
        mImagePickDialog = new ImagePickDialog(this);
        mImagePickDialog.setCallback(new ImagePickDialog.Callback() {
            @Override
            public void onImageBack(ResInformationDto resInformationDto) {
                EventBus.getDefault().postSticky(new ShowLoadingEvent());
                QiniuHelper.uploadImgs(UserModel.getInstance().getUserId(), resInformationDto.getFilePath(),
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                EventBus.getDefault().postSticky(new CloseLoadingEvent());
                                if (info.isOK()) {
                                    leaveImageString = QiniuHelper.QINIU_SPACE + "_" + key;
                                    contentImgLayout.setVisibility(View.INVISIBLE);
                                    FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.WRAP_CONTENT);
                                    layoutParams.height = Screen.getScreenWidth();
                                    signLeavePhotoImg.setLayoutParams(layoutParams);
                                    QiniuHelper.bindImage(leaveImageString, signLeavePhotoImg);
                                }
                            }
                        });
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mImagePickDialog == null) {
            createPickImageDialog();
        }
        mImagePickDialog.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        EventBus.getDefault().unregister(this);
    }
}

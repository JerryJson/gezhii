package com.gezhii.fitgroup.ui.fragment.follow;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ResInformationDto;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.PostEditEvent;
import com.gezhii.fitgroup.event.RefreshChannelEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.dialog.ImagePickDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 16/2/20.
 */
public class AddPostFragment extends BaseFragment {
    @InjectView(R.id.sign_content_photo_img)
    ImageView signContentPhotoImg;
    @InjectView(R.id.content_img_input_btn)
    ImageView contentImgInputBtn;
    @InjectView(R.id.content_img_layout)
    LinearLayout contentImgLayout;
    @InjectView(R.id.back_text)
    TextView backText;
    @InjectView(R.id.title_text)
    TextView titleText;
    @InjectView(R.id.skip_text)
    TextView skipText;
    @InjectView(R.id.tag_name_text)
    TextView tagNameText;
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
    @InjectView(R.id.post_scrollview)
    ScrollView postScrollview;
    @InjectView(R.id.back_btn)
    ImageView backBtn;


    ImagePickDialog mImagePickDialog;
    SharedPreferences sharedPreferences;
    final int[] selected = {0, 0, 0, 0};
    public static final String TAG = "tag";
    public static final String TAG_WECHAT = "tag_wechat";
    public static final String TAG_WECHAT_MOMENT = "tag_wechat_moment";
    public static final String TAG_SINA_WEIBO = "tag_sina_weibo";
    public static final String TAG_QQ = "tag_qq";

    private String shareType = TAG;

    int tagId;
    String description;
    String img;
    ResInformationDto resInformationDto;
    boolean hasAddPhoto = false;//是否添加图片

    public static void start(Activity activity, int tagId) {
        MainActivity mainActivity = (MainActivity) activity;
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("tagId", tagId);
        mainActivity.showNext(AddPostFragment.class, hashMap);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.add_post_fragment, null);
        rootView.setOnTouchListener(this);
        ButterKnife.inject(this, rootView);
        EventBus.getDefault().register(this);
        if (savedInstanceState != null) {
            tagId = savedInstanceState.getInt("tagId");
        } else {
            tagId = (int) getNewInstanceParams().get("tagId");
        }
        titleText.setText("发帖");
        backBtn.setVisibility(View.VISIBLE);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setView();
        signContentPublishLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publish();
            }
        });
        signContentInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputContent = signContentInput.getText().toString().trim();
                AddPostEditFragment.start(getActivity(), inputContent);
            }
        });
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("tagId", tagId);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mImagePickDialog == null) {
            createImagePickDialog();
        }
        mImagePickDialog.onActivityResult(requestCode, resultCode, data);
    }

    public void publish() {
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
        description = signContentInput.getText().toString().trim();
        if (description.length() > 0 || hasAddPhoto) {
            if (hasAddPhoto) {
                EventBus.getDefault().postSticky(new ShowLoadingEvent());
                QiniuHelper.uploadImgs(UserModel.getInstance().getUserId(), resInformationDto.getPushFileName(), resInformationDto.getFilePath(),
                        new UpCompletionHandler() {
                            @Override
                            public void complete(String key, ResponseInfo info, JSONObject response) {
                                EventBus.getDefault().postSticky(new CloseLoadingEvent());
                                if (info.isOK()) {
                                    img = QiniuHelper.QINIU_SPACE + "_" + key;
                                    API.publishPost(UserModel.getInstance().getUserId(), tagId, description, img, new APICallBack() {
                                        @Override
                                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                                            EventBus.getDefault().postSticky(new CloseLoadingEvent());
                                            EventBus.getDefault().post(new RefreshChannelEvent());
                                            finish();
                                        }
                                    });
                                }
                            }
                        });
            } else {
                EventBus.getDefault().postSticky(new ShowLoadingEvent());
                API.publishPost(UserModel.getInstance().getUserId(), tagId, description, img, new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        EventBus.getDefault().post(new CloseLoadingEvent());
                        EventBus.getDefault().post(new RefreshChannelEvent());
                        finish();
                    }
                });
            }
        } else {
            showToast("帖子内容不能为空");
        }

    }

    public void onEventMainThread(PostEditEvent postEditEvent) {

        String content = postEditEvent.getPost_content();
        signContentInput.setText(content);
        if (content.length() > 0) {
            signContentPublishLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
            signContentPublishLayout.setClickable(true);
        } else {
            signContentPublishLayout.setBackgroundColor(getResources().getColor(R.color.gray_c8));
            signContentPublishLayout.setClickable(false);
        }

    }


    @OnClick({R.id.sign_content_photo_img, R.id.content_img_input_btn})
    public void onAddImageClick(View v) {
        MobclickAgent.onEvent(getActivity(), "signin_feeling", UmengEvents.getEventMap("click", "add photo"));
        if (mImagePickDialog == null) {
            createImagePickDialog();
        }
        mImagePickDialog.show();
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
    public void onResume() {
        super.onResume();
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

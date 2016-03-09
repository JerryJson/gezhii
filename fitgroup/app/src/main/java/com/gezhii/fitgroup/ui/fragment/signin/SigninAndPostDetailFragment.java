package com.gezhii.fitgroup.ui.fragment.signin;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.SigninDto;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.FollowStateChangeEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.activity.MainActivity;
import com.gezhii.fitgroup.ui.dialog.ShareToThirdDialog;
import com.gezhii.fitgroup.ui.fragment.BaseFragment;
import com.gezhii.fitgroup.ui.fragment.follow.AllCommentsFragment;
import com.gezhii.fitgroup.ui.fragment.follow.LikeSigninUsersFragment;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.RectImageView;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by zj on 16/2/21.
 */
public class SigninAndPostDetailFragment extends BaseFragment {
    public static final String TAG = SigninAndPostDetailFragment.class.getName();
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
    @InjectView(R.id.user_icon_img)
    RectImageView userIconImg;
    @InjectView(R.id.user_vip_icon)
    RectImageView userVipIcon;
    @InjectView(R.id.user_name_text)
    TextView userNameText;
    @InjectView(R.id.sign_now_date)
    TextView signNowDate;
    @InjectView(R.id.whether_follow_btn)
    TextView whetherFollowBtn;
    @InjectView(R.id.sign_description_text)
    TextView signDescriptionText;
    @InjectView(R.id.sign_img)
    ImageView signImg;
    @InjectView(R.id.sign_card_small_icon_img)
    ImageView signCardSmallIconImg;
    @InjectView(R.id.sign_task_name_text)
    TextView signTaskNameText;
    @InjectView(R.id.sign_task_continue_text)
    TextView signTaskContinueText;
    @InjectView(R.id.task_layout)
    LinearLayout taskLayout;
    @InjectView(R.id.like_count)
    TextView likeCount;
    @InjectView(R.id.like_count_layout)
    LinearLayout likeCountLayout;
    @InjectView(R.id.comment_content_layout)
    LinearLayout commentContentLayout;
    @InjectView(R.id.comment_more_count)
    TextView commentMoreCount;
    @InjectView(R.id.comment_more_layout)
    LinearLayout commentMoreLayout;
    @InjectView(R.id.sign_like_img)
    ImageView signLikeImg;
    @InjectView(R.id.comment_like)
    LinearLayout commentLike;
    @InjectView(R.id.comment_btn)
    LinearLayout commentBtn;
    @InjectView(R.id.share_to_third_img)
    ImageView shareToThirdImg;
    @InjectView(R.id.share_to_third_btn)
    LinearLayout shareToThirdBtn;
    @InjectView(R.id.more_dot_btn)
    ImageView moreDotBtn;
    @InjectView(R.id.share_btn_layout)
    LinearLayout shareBtnLayout;
    @InjectView(R.id.share_to_third_layout)
    LinearLayout shareToThirdLayout;
    @InjectView(R.id.report_layout)
    LinearLayout reportLayout;

    private ShareToThirdDialog mShareToThirdDialog;
    private Signin signin;
    private int signin_id;

    public static void start(Activity activity, Signin signAndPost) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("signin", signAndPost);
        ((MainActivity) activity).showNext(SigninAndPostDetailFragment.class, params);
    }

    public static void start(Activity activity, int signin_id) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("signin_id", signin_id);
        ((MainActivity) activity).showNext(SigninAndPostDetailFragment.class, params);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.signin_and_post_detail_fragment, null);
        ButterKnife.inject(this, rootView);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        titleText.setText("动态详情");
        getData();
        return rootView;
    }

    private void getData() {
        if (getNewInstanceParams().get("signin") != null) {
            signin = (Signin) getNewInstanceParams().get("signin");
            signin_id = signin.getId();
        } else if (getNewInstanceParams().get("signin_id") != null) {
            signin_id = (int) getNewInstanceParams().get("signin_id");
        }
        EventBus.getDefault().post(new ShowLoadingEvent());
        API.getSigninHttp(UserModel.getInstance().getUserId(), signin_id, new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
                EventBus.getDefault().post(new CloseLoadingEvent());
                SigninDto signinDto = SigninDto.parserJson(response);
                if (signinDto.getSignin() != null) {
                    signin = signinDto.getSignin();
                    setView();
                }
            }
        });
    }

    private void setView() {
        QiniuHelper.bindImage(signin.getUser().getIcon(), userIconImg);
        userNameText.setText(signin.getUser().getNick_name());
        if (signin.getUser().getVip() == 1) {
            userVipIcon.setVisibility(View.VISIBLE);
        } else {
            userVipIcon.setVisibility(View.GONE);
        }
        if (signin.getDescription().isEmpty()) {
            signDescriptionText.setVisibility(View.GONE);
        } else {
            signDescriptionText.setVisibility(View.VISIBLE);
            signDescriptionText.setText(signin.getDescription());
        }
        if (signin.getImg().isEmpty()) {
            signImg.setVisibility(View.GONE);
        } else {
            signImg.setVisibility(View.VISIBLE);
            QiniuHelper.bingImageType(signin.getImg(), signImg);
//            signImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    BigPhotoDialog bigPhotoDialog = new BigPhotoDialog(getActivity());
//                    bigPhotoDialog.setQiniuUrl(signin.getImg());
//                    bigPhotoDialog.show();
//                }
//            });
        }
        if (signin.getPost_type() == 0) {
            taskLayout.setVisibility(View.VISIBLE);
            signTaskNameText.setText("#" + signin.getTask_name() + "#");
            signTaskContinueText.setText("累计打卡" + signin.getTask_continue_days() + "天");
        } else {
            taskLayout.setVisibility(View.GONE);
        }
        if (signin.getComment_count() > 3) {
            commentMoreLayout.setVisibility(View.VISIBLE);
            commentMoreCount.setText("查看所有" + signin.getComment_count() + "条评论");
            commentMoreCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AllCommentsFragment.start(getActivity(), signin.getId());
                }
            });
        } else {
            commentMoreLayout.setVisibility(View.GONE);
        }
        commentContentLayout.removeAllViews();
        for (int i = 0; i < (signin.getComment_count() > 3 ? 3 : signin.getComment_count()); i++) {
            if (signin.getComments() != null) {
                TextView tv = new TextView(getActivity());
                String name = signin.getComments().get(i).getUser().getNick_name();//评论者的昵称
                String nameAndComment = name + ":" + signin.getComments().get(i).getComment();
                SpannableStringBuilder style = new SpannableStringBuilder(nameAndComment);
                style.setSpan(new ForegroundColorSpan(getActivity().getResources().getColor(R.color.colorPrimary)), 0, name.length() + 1, 0);
                tv.setText(style);
                tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AllCommentsFragment.start(getActivity(), signin.getId());
                    }
                });
                commentContentLayout.addView(tv);
            }
        }
        if (signin.getLike_count() != 0) {
            likeCountLayout.setVisibility(View.VISIBLE);
            likeCount.setText(signin.getLike_count() + "人赞");
        } else {
            likeCountLayout.setVisibility(View.GONE);
        }
        likeCountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LikeSigninUsersFragment.start(getActivity(), signin.getId());
            }
        });
        if (signin.getFlag() == 1) {
            signLikeImg.setImageResource(R.mipmap.chat_sign_like_select);
            signLikeImg.setEnabled(false);
        } else {
            signLikeImg.setImageResource(R.mipmap.chat_sign_like_normal);
            signLikeImg.setEnabled(true);
            signLikeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    view.setEnabled(false);
                    API.LikeUserSigninHttp(UserModel.getInstance().getUserId(), signin.getId(), new OnRequestEnd() {
                        @Override
                        public void onRequestSuccess(String response) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    signLikeImg.setImageResource(R.mipmap.chat_sign_like_select);
                                    signin.setFlag(1);
                                    likeCountLayout.setVisibility(View.VISIBLE);
                                    likeCount.setText((signin.getLike_count() + 1) + "人赞");
                                }
                            });
                        }

                        @Override
                        public void onRequestFail(VolleyError error) {
                            view.setEnabled(true);
                        }
                    });
                }
            });
        }
        shareToThirdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mShareToThirdDialog = new ShareToThirdDialog(getActivity(), signin);
                mShareToThirdDialog.show();
            }
        });
        commentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllCommentsFragment.start(getActivity(), signin.getId());
            }
        });
        signNowDate.setText(TimeHelper.getTimeDifferenceString(signin.getCreated_time()));
        userIconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileFragment.start(getActivity(), signin.getUser_id());
            }
        });
        reportLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                alertParams.setMessage("确定要举报该用户吗?");
                alertParams.setTitle("举报");
                alertParams.setCancelString("取消");
                alertParams.setConfirmString("确定");
                alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        showToast("举报成功");
                    }
                });
                alertParams.setCancelListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertHelper.showAlert(getActivity(), alertParams);
            }
        });
        if (signin.getUser().getIs_following() == 0 && signin.getUser_id() != UserModel.getInstance().getUserId()) {
            whetherFollowBtn.setVisibility(View.VISIBLE);
            whetherFollowBtn.setText("关 注");

            whetherFollowBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    API.followUser(UserModel.getInstance().getUserId(), signin.getUser_id(), 1, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            EventBus.getDefault().post(new FollowStateChangeEvent(0));
                            whetherFollowBtn.setVisibility(View.GONE);
                        }
                    });
                }
            });
        } else {
            whetherFollowBtn.setVisibility(View.GONE);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }
}

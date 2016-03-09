package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.SigninDto;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.LikeSigninMessageExt;
import com.gezhii.fitgroup.model.SignCacheModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.dialog.BigPhotoDialog;
import com.gezhii.fitgroup.ui.dialog.ShareToThirdDialog;
import com.gezhii.fitgroup.ui.fragment.me.PrivateChatFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SignCardDetailFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/11/6.
 */
public class SignHistoryListAdapter extends LoadMoreListViewAdapter {

    Activity mContext;
    User user;
    int mFlag;
    ShareToThirdDialog mShareToThirdDialog;

    public SignHistoryListAdapter(Activity mContext, User user, int flag, SignCardDetailFragment signCardDetailFragment) {
        this.mContext = mContext;
        this.user = user;
        this.mFlag = flag;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_history_list_item, null);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        ViewHolder viewHolder = new ViewHolder(itemView, getOnListItemClickListener());
        return viewHolder;
    }


    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final Signin signin = (Signin) getTotal_data_list().get(position);
        final SigninDto signinDto = new SigninDto();
        if (signin.getFlag() == 0) {
            signinDto.setIsLiked(false);
        } else {
            signinDto.setIsLiked(true);
        }
        signinDto.setSignin(signin);
        SignCacheModel.getInstance().putSign(signinDto);


        final ViewHolder viewHolder = (ViewHolder) holder;


        if (signinDto != null) {
            //头像,昵称,打卡时间
            String icon = user.getIcon();
            final String nick_name = user.getNick_name();
            if (!TextUtils.isEmpty(icon)) QiniuHelper.bindImage(icon, viewHolder.userIconImg);
            viewHolder.userNameText.setText(nick_name);
            viewHolder.signDateText.setText(TimeHelper.getInstance().formatDateForTitle(signinDto.getSignin().getCreated_time()));
            viewHolder.privateChatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PrivateChatFragment.start(mContext, user.getHuanxin_id());
                }
            });
            //描述
            if (!TextUtils.isEmpty(signinDto.getSignin().getDescription())) {
                viewHolder.signDescriptionText.setVisibility(View.VISIBLE);
                viewHolder.signDescriptionText.setText(signinDto.getSignin().getDescription());
            } else {
                viewHolder.signDescriptionText.setVisibility(View.GONE);
            }


            if (!TextUtils.isEmpty(signinDto.getSignin().getImg())) {
                viewHolder.signImg.setVisibility(View.VISIBLE);
                QiniuHelper.bindImage(signinDto.getSignin().getImg(), viewHolder.signImg);
                viewHolder.signImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BigPhotoDialog bigPhotoDialog = new BigPhotoDialog(mContext);
                        bigPhotoDialog.setQiniuUrl(signinDto.getSignin().getImg());
                        bigPhotoDialog.show();
                    }
                });
            } else {
                viewHolder.signImg.setVisibility(View.GONE);
            }

//            if (!TextUtils.isEmpty(signinDto.getSignin().getTask_name())) {
//                SpannableString spannableString = new SpannableString("已坚持[" + signinDto.getSignin().getTask_name() + "]" + signinDto.getSignin().getTask_continue_days() + "天");
//                spannableString.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.pink_ff)), 3, 3 + signinDto.getSignin().getTask_name().length() + 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                viewHolder.signTaskNameText.setText(spannableString);
//            } else {
//                viewHolder.taskLayout.setVisibility(View.GONE);
//            }
            if (!TextUtils.isEmpty(signinDto.getSignin().getTask_name())) {
                viewHolder.taskLayout.setVisibility(View.VISIBLE);
                viewHolder.signTaskNameText.setText(signinDto.getSignin().getTask_name() + " "
                        + "累计打卡" + signinDto.getSignin().getTask_continue_days() + "天");
            } else {
                viewHolder.taskLayout.setVisibility(View.GONE);
            }

            viewHolder.signCountText.setText(String.valueOf(signinDto.getSignin().getLike_count()));
            if (signinDto.isLiked()) {
                viewHolder.signCountText.setTextColor(mContext.getResources().getColor(R.color.pink_ff));
                viewHolder.signLikeImg.setImageResource(R.mipmap.chat_sign_like_select);
                viewHolder.signLikeImg.setEnabled(false);
            } else {
                viewHolder.signCountText.setTextColor(mContext.getResources().getColor(R.color.gray_97));
                viewHolder.signLikeImg.setImageResource(R.mipmap.chat_sign_like_normal);
                viewHolder.signLikeImg.setEnabled(true);
                viewHolder.signLikeImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        API.LikeUserSigninHttp(UserModel.getInstance().getUserId(), signinDto.getSignin().getId(), new APICallBack() {
                            @Override
                            public void subRequestSuccess(String response) {
                                HuanXinHelper.sendGroupTextMessage(new LikeSigninMessageExt(UserModel.getInstance().getUserNickName(),
                                                UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), String.valueOf(signinDto.getSignin().getId())), UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(),
                                        "[赞]@" + nick_name, new EMCallBack() {
                                            @Override
                                            public void onSuccess() {

                                                mContext.runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
//
                                                        signin.setLike_count(signin.getLike_count() + 1);
                                                        signin.setFlag(1);
                                                        notifyItemChanged(position);
                                                    }
                                                });
//
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
                });
            }
        }


        if (SignCacheModel.getInstance().getSign(signin.getId()) == null) {
            SigninDto msigninDto = new SigninDto();
            msigninDto.setSignin(signin);
            if (signin.getFlag() == 1) {
                msigninDto.setIsLiked(true);
            } else {
                msigninDto.setIsLiked(false);
            }
            SignCacheModel.getInstance().putSign(msigninDto);
        }
        viewHolder.shareToThirdImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "signin_history", UmengEvents.getEventMap("click", "share"));
                mShareToThirdDialog = new ShareToThirdDialog(mContext, ((Signin) getTotal_data_list().get(position)).getId(), user.getHuanxin_id());
                mShareToThirdDialog.show();
            }
        });


        if (signin.getFlag() == 1) {
            viewHolder.signCountText.setTextColor(mContext.getResources().getColor(R.color.pink_ff));
            viewHolder.signLikeImg.setImageResource(R.mipmap.chat_sign_like_select);
            viewHolder.signLikeImg.setEnabled(false);
        } else {
            viewHolder.signCountText.setTextColor(mContext.getResources().getColor(R.color.gray_97));
            viewHolder.signLikeImg.setImageResource(R.mipmap.chat_sign_like_normal);
            viewHolder.signLikeImg.setEnabled(true);
            viewHolder.signLikeImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(mContext, "signin_history", UmengEvents.getEventMap("click", "like"));
                    API.LikeUserSigninHttp(UserModel.getInstance().getUserId(), signin.getId(), new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) {
                            HuanXinHelper.sendGroupTextMessage(new LikeSigninMessageExt(UserModel.getInstance().getUserNickName(),
                                            UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId())
                                            , String.valueOf(signin.getId())), UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(),
                                    "[赞]@" + user.getNick_name(), new EMCallBack() {
                                        @Override
                                        public void onSuccess() {
                                            Log.i("darren", "[赞]@" + user.getNick_name());
                                            mContext.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    signin.setLike_count(signin.getLike_count() + 1);
                                                    signin.setFlag(1);
                                                    notifyItemChanged(position);
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
                    });
                }
            });
        }
    }

    static class ViewHolder extends BaseViewHolder {
        @InjectView(R.id.user_icon_img)
        RectImageView userIconImg;
        @InjectView(R.id.user_name_text)
        TextView userNameText;
        @InjectView(R.id.sign_date_text)
        TextView signDateText;
        @InjectView(R.id.sign_description_text)
        TextView signDescriptionText;
        @InjectView(R.id.sign_img)
        ImageView signImg;
        @InjectView(R.id.sign_task_name_text)
        TextView signTaskNameText;
        @InjectView(R.id.task_layout)
        LinearLayout taskLayout;
        @InjectView(R.id.sign_like_img)
        ImageView signLikeImg;
        @InjectView(R.id.sign_count_text)
        TextView signCountText;
        @InjectView(R.id.share_to_third_img)
        ImageView shareToThirdImg;
        @InjectView(R.id.share_btn_layout)
        LinearLayout shareBtnLayout;
        @InjectView(R.id.share_to_third_layout)
        LinearLayout shareToThirdLayout;
        @InjectView(R.id.private_chat_btn)
        LinearLayout privateChatBtn;

        public ViewHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
            ButterKnife.inject(this, itemView);
        }
    }
}

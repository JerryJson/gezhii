package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.basic.Badge;
import com.gezhii.fitgroup.dto.basic.UserInfo;
import com.gezhii.fitgroup.dto.basic.UserInfoDto;
import com.gezhii.fitgroup.model.UserCacheModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.dialog.BigPhotoDialog;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.melink.baseframe.bitmap.BitmapCreate;
import com.melink.baseframe.utils.DensityUtils;
import com.melink.baseframe.utils.StringUtils;
import com.melink.bqmmsdk.bean.Emoji;
import com.melink.bqmmsdk.sdk.BQMMSdk;
import com.melink.bqmmsdk.widget.AnimatedGifDrawable;
import com.melink.bqmmsdk.widget.AnimatedImageSpan;
import com.melink.bqmmsdk.widget.GifMovieView;
import com.thirdparty.bumptech.glide.Glide;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/10/22.
 */
public class PrivateChatListAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_OTHER_CHAT = 0;
    private static final int ITEM_TYPE_ME_CHAT = 1;
    private static final int ITEM_TYPE_ME_SIGN = 2;
    private static final int ITEM_TYPE_OTHER_SIGN = 3;
    private static final int ITEM_TYPE_SYSTEM_SIGN = 4;
    private static final int ITEM_TYPE_SYSTEM_NOTICE = 5;
    private static final int ITEM_TYPE_NULL = 8;
    private static final int ITEM_TYPE_ME_LIKE = 6;
    private static final int ITEM_TYPE_OTHER_LIKE = 7;

    Activity mContext;
    List<EMMessage> mEmMessageList;
    String huanxin_id;

    public PrivateChatListAdapter(Activity mContext, List<EMMessage> mEmMessageList, String huanxin_id) {
        this.mEmMessageList = mEmMessageList;
        this.mContext = mContext;
        this.huanxin_id = huanxin_id;
    }

    public void setEmMessageList(List<EMMessage> mEmMessageList) {
        this.mEmMessageList = mEmMessageList;
    }

    @Override
    public int getItemViewType(int position) {
        boolean isFromMeSend = UserModel.getInstance().getUserHuanXinName().equals(mEmMessageList.get(position).getFrom());
        EMMessage emMessage = mEmMessageList.get(position);
        int type = emMessage.getIntAttribute("type", -1);
//        if (type == 10) {
//            if (isFromMeSend) {
//                return ITEM_TYPE_ME_SIGN;
//            } else {
//                return ITEM_TYPE_OTHER_SIGN;
//            }
//        }
//        if (type == 12) {
//            return ITEM_TYPE_NULL;
//        } else if (type == 11) {
//            if (isFromMeSend) {
//                return ITEM_TYPE_ME_LIKE;
//            } else {
//                return ITEM_TYPE_OTHER_LIKE;
//            }
//        } else if (type == 13) {
//            return ITEM_TYPE_SYSTEM_NOTICE;
//        } else if (type == 20) {
//            return ITEM_TYPE_SYSTEM_SIGN;
//        } else {
//            if (isFromMeSend) {
//                return ITEM_TYPE_ME_CHAT;
//            } else {
//                return ITEM_TYPE_OTHER_CHAT;
//            }
//        }

        switch (type) {
            case 30:
            case 40:
            case 41:
            case 42:
            case 43:
            case 44:
            case 45:
            case 46:
                return ITEM_TYPE_NULL;
            default:
                if (isFromMeSend) {
                    return ITEM_TYPE_ME_CHAT;
                } else {
                    return ITEM_TYPE_OTHER_CHAT;
                }
        }


    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = null;
        RecyclerView.ViewHolder viewHolder = null;
        if (viewType == ITEM_TYPE_OTHER_CHAT) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_other_say_list_item, null);
            viewHolder = new OtherViewHolder(rootView, getOnListItemClickListener());
        } else if (viewType == ITEM_TYPE_ME_CHAT) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_me_say_list_item, null);
            viewHolder = new MeViewHolder(rootView, getOnListItemClickListener());
        } else if (viewType == ITEM_TYPE_NULL) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_null_list_item, null);
            viewHolder = new NullViewHolder(rootView, getOnListItemClickListener());
        }


        if (rootView != null) {
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT);
            rootView.setLayoutParams(params);
        }

        return viewHolder;
    }

    private static final int UNBOUNDED = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);

    public static int getItemHeight(View rootView) {
        rootView.measure(UNBOUNDED, UNBOUNDED);
        return rootView.getMeasuredHeight();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        EMMessage emMessage = mEmMessageList.get(position);


        int itemType = getItemViewType(position);
        switch (itemType) {
            case ITEM_TYPE_ME_CHAT:
                setMeChatItem(holder, emMessage, position);
                setBaseItem(holder, emMessage, position);
                break;
            case ITEM_TYPE_OTHER_CHAT:
                setOtherChatItem(holder, emMessage, position);
                setBaseItem(holder, emMessage, position);
                break;

            default:
                break;

        }
    }


    private void setBaseItem(RecyclerView.ViewHolder holder, EMMessage emMessage, final int position) {
        BaseGroupItemViewHolder viewHolder = (BaseGroupItemViewHolder) holder;

        boolean isShowTimeView = emMessage.getBooleanAttribute("isShowTimeView", false);
        if (isShowTimeView) {
            viewHolder.timeLayout.setVisibility(View.VISIBLE);
            viewHolder.messageTimeText.setText(TimeHelper.getTimeDifferenceString(new Date(emMessage.getMsgTime())));
        } else {
            viewHolder.timeLayout.setVisibility(View.GONE);
        }


        int badgeCount;
        badgeCount = emMessage.getIntAttribute("max_badge_count", -1);
        viewHolder.badgeListLayout.removeAllViews();
        if (badgeCount < 0) {
            int itemHeight = getItemHeight(viewHolder.itemView);
            int surplusHeight = itemHeight - viewHolder.iconImg.getMeasuredHeight();
            int badgeHeight = Screen.dip2px(24);
            badgeCount = surplusHeight / badgeHeight;
            emMessage.setAttribute("max_badge_count", badgeCount);
            EMChatManager.getInstance().updateMessageBody(emMessage);
            Log.i("xianrui", "itemHeight " + itemHeight + " surplusHeight " + surplusHeight + " badgeHeight " + badgeHeight);
            Log.i("xianrui", "badgeCount " + badgeCount);
        }

        UserCacheModel.UserCacheInfo userCacheInfo = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom());
        if (System.currentTimeMillis() / 1000 - userCacheInfo.lastCacheBadgeTime > 60 * 60 * 2) {
            String[] userId = new String[]{emMessage.getFrom()};
            API.getUserInfoHttp(GsonHelper.getInstance().getGson().toJson(userId),
                    new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) {
                            UserInfoDto userInfoDto = UserInfoDto.parserJson(response);
                            UserInfo userInfo = userInfoDto.getGroup_members().get(0);
                            List<Badge> badgeList = userInfo.getBadges_list();
                            UserCacheModel.getInstance().setUserBadgeList(userInfo.getHuanxin_id(), badgeList);
                            UserCacheModel.getInstance().setLastCacheBadgeTime(userInfo.getHuanxin_id(), System.currentTimeMillis() / 1000);
                            notifyItemChanged(position);
                        }
                    });
        } else {
            int count = userCacheInfo.badgeIconList.size();
            if (count > badgeCount) {
                count = badgeCount;
            }

            for (int i = 0; i < count; i++) {
                ImageView view = new ImageView(mContext);
//                view.setBackgroundColor(mContext.getResources().getColor(R.color.colorPrimary));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Screen.dip2px(20), Screen.dip2px(20));
                params.setMargins(0, Screen.dip2px(2), 0, 0);
                params.gravity = Gravity.CENTER;
                view.setLayoutParams(params);
                QiniuHelper.bindImage(userCacheInfo.badgeIconList.get(i).getIcon(), view);
                viewHolder.badgeListLayout.addView(view);
            }
        }

    }


    private void setOtherChatItem(RecyclerView.ViewHolder holder, final EMMessage emMessage, int position) {
        final OtherViewHolder viewHolder = (OtherViewHolder) holder;
        String icon = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).icon;
        if (!TextUtils.isEmpty(icon)) {
            QiniuHelper.bindImage(icon, viewHolder.iconImg);
        }


        viewHolder.iconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String user_id = emMessage.getStringAttribute("user_id", "");
                //MemberInfoFragment.start(mContext, Integer.valueOf(user_id));
                UserProfileFragment.start(mContext, Integer.valueOf(user_id));
            }
        });


        String nickName = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).nickName;
        if (!TextUtils.isEmpty(nickName)) {
            viewHolder.userNameText.setText(nickName);
        }
//        if (emMessage.getType() == EMMessage.Type.TXT) {
//            viewHolder.contentGif.setVisibility(View.GONE);
//            viewHolder.contentImg.setVisibility(View.GONE);
//            viewHolder.contentText.setVisibility(View.VISIBLE);
//            viewHolder.contentText.setText(((TextMessageBody) emMessage.getBody()).getMessage());
//        }


        //Gif动画(BQMM)
        if (emMessage.getIntAttribute("type", -1) == 47) {
            viewHolder.rootLayout.setVisibility(View.VISIBLE);
            String emoji_code = emMessage.getStringAttribute("emoji_code", "");
            Log.i("ycl", "face code = " + emoji_code);

            BQMMSdk.getInstance().fetchEmojiByCode(mContext, emoji_code, new BQMMSdk.IFetchEmojiByCodeCallback() {
                @Override
                public void onSuccess(Emoji emoji_) {
                    Log.i("ycl", "face url = " + emoji_.getMainImage());
                    final Emoji emoji = emoji_;
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (emoji.getMainImage().endsWith(".png")) {
                                viewHolder.contentGif.setVisibility(View.GONE);
                                viewHolder.contentText.setVisibility(View.GONE);
                                viewHolder.contentImg.setVisibility(View.VISIBLE);

                                Glide.with(mContext).load(emoji.getMainImage()).override(DensityUtils.dip2px(mContext, 50), DensityUtils.dip2px(mContext, 50)).into(viewHolder.contentImg);
                            } else if (emoji.getMainImage().endsWith(".gif")) {
                                viewHolder.contentImg.setVisibility(View.GONE);
                                viewHolder.contentText.setVisibility(View.GONE);
                                viewHolder.contentGif.setVisibility(View.VISIBLE);

                                if (emoji.getPathofImage() == null || emoji.getPathofImage().equals("")) {
                                    viewHolder.contentGif.setResource(StringUtils.decodestr(emoji.getMainImage()));//读网络上的
                                } else {
                                    viewHolder.contentGif.setMovieResourceByUri(emoji.getPathofImage());
                                }
                            }
                        }
                    });
                }

                @Override
                public void onError(Throwable throwable) {
                    Toast.makeText(mContext, "表情下载失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (emMessage.getType() == EMMessage.Type.TXT || emMessage.getIntAttribute("type", -1) == 48) {
            viewHolder.rootLayout.setVisibility(View.VISIBLE);
            viewHolder.contentImg.setVisibility(View.GONE);
            viewHolder.contentGif.setVisibility(View.GONE);
            viewHolder.contentText.setVisibility(View.VISIBLE);

            //小表情和文字混排
            if (emMessage.getIntAttribute("type", -1) == 48) {
                String message = emMessage.getStringAttribute("emoji_and_text", "");
                if (!StringUtils.isEmpty(message)) {
                    List<Object> emojis = BQMMSdk.getInstance().parseMsg(message);
                    showTextInfo(viewHolder.contentText, emojis, position);
                }
            } else //纯文字
            {
                viewHolder.contentText.setText(((TextMessageBody) emMessage.getBody()).getMessage());
            }
        } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
            viewHolder.rootLayout.setVisibility(View.VISIBLE);
            viewHolder.contentGif.setVisibility(View.GONE);
            viewHolder.contentText.setVisibility(View.GONE);
            viewHolder.contentImg.setVisibility(View.VISIBLE);
            final ImageMessageBody imageMessageBody = (ImageMessageBody) emMessage.getBody();

            int w, h;
            int maxHeight = Screen.getScreenWidth() / 3;
            if (maxHeight < imageMessageBody.getHeight()) {
                h = maxHeight;
                w = (int) ((float) maxHeight / (float) imageMessageBody.getHeight() * imageMessageBody.getWidth());
            } else {
                h = imageMessageBody.getHeight();
                w = imageMessageBody.getWidth();
            }
            viewHolder.contentImg.setLayoutParams(new FrameLayout.LayoutParams(w, h));
            QiniuHelper.bindImageByUrl(imageMessageBody.getRemoteUrl(), viewHolder.contentImg);
            viewHolder.contentImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BigPhotoDialog bigPhotoDialog = new BigPhotoDialog(mContext);
                    bigPhotoDialog.setRemoteUrl(imageMessageBody.getRemoteUrl());
                    bigPhotoDialog.show();
                }
            });
//            if (emMessage.getFrom().equals(UserModel.getInstance().getUserHuanXinName())) {
//                QiniuHelper.bindLocalImage(imageMessageBody.getLocalUrl(), viewHolder.iconImg);
//            } else {
//                QiniuHelper.bindImage(imageMessageBody.getRemoteUrl(), viewHolder.iconImg);
//            }
        } else if (emMessage.getIntAttribute("type", -1) == 46) {
            viewHolder.rootLayout.setVisibility(View.GONE);
        }
    }

    private void setMeChatItem(final RecyclerView.ViewHolder vh, final EMMessage emMessage, int position) {
        final MeViewHolder viewHolder = (MeViewHolder) vh;
        String icon = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).icon;
        if (!TextUtils.isEmpty(icon)) {
            QiniuHelper.bindImage(icon, viewHolder.iconImg);
        }

        if (emMessage.status == EMMessage.Status.SUCCESS) {
            viewHolder.statusLayout.setVisibility(View.GONE);
        } else if (emMessage.status == EMMessage.Status.FAIL) {
            viewHolder.statusLayout.setVisibility(View.VISIBLE);
            viewHolder.messageResendImage.setVisibility(View.VISIBLE);
            viewHolder.messageLoadingImage.setVisibility(View.GONE);
        } else if (emMessage.status == EMMessage.Status.INPROGRESS) {
            Log.i("ycl", "emMessage.status = " + emMessage.status);

            viewHolder.statusLayout.setVisibility(View.VISIBLE);
            viewHolder.messageResendImage.setVisibility(View.GONE);
            viewHolder.messageLoadingImage.setVisibility(View.VISIBLE);
            //  if (viewHolder.messageLoadingImage.getAnimation() != null) {
            RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            rotateAnimation.setDuration(1000);
            rotateAnimation.setRepeatCount(Animation.INFINITE);
            rotateAnimation.setRepeatMode(Animation.RESTART);
            rotateAnimation.setInterpolator(new LinearInterpolator());
            viewHolder.messageLoadingImage.startAnimation(rotateAnimation);
            //  }
        }
        emMessage.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.statusLayout.setVisibility(View.GONE);
                        viewHolder.messageLoadingImage.clearAnimation();
                    }
                });

            }

            @Override
            public void onError(int i, String s) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.statusLayout.setVisibility(View.VISIBLE);
                        viewHolder.messageResendImage.setVisibility(View.VISIBLE);
                        viewHolder.messageLoadingImage.setVisibility(View.GONE);
                        viewHolder.messageLoadingImage.clearAnimation();
//                        viewHolder.messageResendImage.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                emMessage.status = EMMessage.Status.CREATE;
//
//                            }
//                        });
                    }
                });

            }

            @Override
            public void onProgress(int i, String s) {
                mContext.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewHolder.statusLayout.setVisibility(View.VISIBLE);
                        viewHolder.messageResendImage.setVisibility(View.GONE);
                        viewHolder.messageLoadingImage.setVisibility(View.VISIBLE);
                        if (viewHolder.messageLoadingImage.getAnimation() != null) {
                            RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                            rotateAnimation.setDuration(1000);
                            rotateAnimation.setRepeatCount(Animation.INFINITE);
                            rotateAnimation.setRepeatMode(Animation.RESTART);
                            rotateAnimation.setInterpolator(new LinearInterpolator());
                            viewHolder.messageLoadingImage.startAnimation(rotateAnimation);
                        }
                    }
                });

            }
        });


//        if (emMessage.getType() == EMMessage.Type.TXT) {
//            viewHolder.contentGif.setVisibility(View.GONE);
//            viewHolder.contentImg.setVisibility(View.GONE);
//            viewHolder.contentText.setVisibility(View.VISIBLE);
//            viewHolder.contentText.setText(((TextMessageBody) emMessage.getBody()).getMessage());
//        }

        Log.i("darren---type", emMessage.getIntAttribute("type", -1));
        //Gif动画(表情MM SDK)
        if (emMessage.getIntAttribute("type", -1) == 47) {
            viewHolder.rootLayout.setVisibility(View.VISIBLE);
            String emoji_code = emMessage.getStringAttribute("emoji_code", "");
            Log.i("ycl", "face code = " + emoji_code);

            BQMMSdk.getInstance().fetchEmojiByCode(mContext, emoji_code, new BQMMSdk.IFetchEmojiByCodeCallback() {
                @Override
                public void onSuccess(Emoji emoji_) {
                    Log.i("ycl", "face url = " + emoji_.getMainImage());
                    final Emoji emoji = emoji_;
                    mContext.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (emoji.getMainImage().endsWith(".png")) {
                                viewHolder.contentGif.setVisibility(View.GONE);
                                viewHolder.contentText.setVisibility(View.GONE);
                                viewHolder.contentImg.setVisibility(View.VISIBLE);

                                Glide.with(mContext).load(emoji.getMainImage()).override(DensityUtils.dip2px(mContext, 50), DensityUtils.dip2px(mContext, 50)).into(viewHolder.contentImg);
                            } else if (emoji.getMainImage().endsWith(".gif")) {
                                viewHolder.contentImg.setVisibility(View.GONE);
                                viewHolder.contentText.setVisibility(View.GONE);
                                viewHolder.contentGif.setVisibility(View.VISIBLE);

                                if (emoji.getPathofImage() == null || emoji.getPathofImage().equals("")) {
                                    viewHolder.contentGif.setResource(StringUtils.decodestr(emoji.getMainImage()));//读网络上的
                                } else {
                                    viewHolder.contentGif.setMovieResourceByUri(emoji.getPathofImage());
                                }
                            }
                        }
                    });
                }

                @Override
                public void onError(Throwable throwable) {
                    Toast.makeText(mContext, "表情下载失败", Toast.LENGTH_SHORT).show();
                }
            });
        } else if (emMessage.getType() == EMMessage.Type.TXT || emMessage.getIntAttribute("type", -1) == 48) {
            viewHolder.rootLayout.setVisibility(View.VISIBLE);
            viewHolder.contentImg.setVisibility(View.GONE);
            viewHolder.contentGif.setVisibility(View.GONE);
            viewHolder.contentText.setVisibility(View.VISIBLE);

            //小表情和文字混排
            if (emMessage.getIntAttribute("type", -1) == 48) {
                String message = emMessage.getStringAttribute("emoji_and_text", "");
                if (!StringUtils.isEmpty(message)) {
                    List<Object> emojis = BQMMSdk.getInstance().parseMsg(message);
                    showTextInfo(viewHolder.contentText, emojis, position);
                }
            } else //纯文字
            {
                viewHolder.contentText.setText(((TextMessageBody) emMessage.getBody()).getMessage());
            }
        } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
            viewHolder.rootLayout.setVisibility(View.VISIBLE);
            viewHolder.contentGif.setVisibility(View.GONE);
            viewHolder.contentText.setVisibility(View.GONE);
            viewHolder.contentImg.setVisibility(View.VISIBLE);
            final ImageMessageBody imageMessageBody = (ImageMessageBody) emMessage.getBody();
            int w, h;
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imageMessageBody.getLocalUrl(), options);
            int height = options.outHeight;
            int width = options.outWidth;
            int maxHeight = Screen.getScreenWidth() / 3;
            if (maxHeight < height) {
                h = maxHeight;
                w = (int) ((float) maxHeight / (float) height * width);
            } else {
                h = height;
                w = width;
            }
            viewHolder.contentImg.setLayoutParams(new FrameLayout.LayoutParams(w, h));
            QiniuHelper.bindLocalImage(imageMessageBody.getLocalUrl(), viewHolder.contentImg);
            viewHolder.contentImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BigPhotoDialog bigPhotoDialog = new BigPhotoDialog(mContext);
                    bigPhotoDialog.setLocalUrl(imageMessageBody.getLocalUrl());
                    bigPhotoDialog.show();
                }
            });
        } else if (emMessage.getIntAttribute("type", -1) == 46) {
            viewHolder.rootLayout.setVisibility(View.GONE);
        }
    }


    private void showTextInfo(final TextView tv_chatcontent, final List<Object> emojis, final int position) {
        //根据返回的list集合实现图文混排
        SpannableStringBuilder sb = new SpannableStringBuilder();
//		if(spanCache.get(position +"") != null){
//			sb = spanCache.get(position + "");
//		}else{
        for (int i = 0; i < emojis.size(); i++) {
            if (emojis.get(i).getClass().equals(Emoji.class)) {
                Emoji item = (Emoji) emojis.get(i);
                String tempText = "[" + item.getEmoCode() + "]";
                sb.append(tempText);
                // 判断当前的Emoji对象是不是gif表情
                if (item.getMainImage().endsWith(".png") || emojis.size() > 10) {
                    try {
                        Bitmap bit = BitmapCreate.bitmapFromFile(item.getPathofThumb(), 0, 0);
                        sb.setSpan(
                                new ImageSpan(mContext, bit), sb.length()
                                        - tempText.length(), sb.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        AnimatedImageSpan aspan;
                        InputStream is = null;
                        is = new FileInputStream(item.getPathofImage());
                        aspan = new AnimatedImageSpan(
                                new AnimatedGifDrawable(
                                        is, item.getPathofImage(),
                                        new AnimatedGifDrawable.UpdateListener() {
                                            @Override
                                            public void update() {
                                                //如果实在滑动状态，或者隐藏状态，则更新动态表情
//                                                if(getScrollstate() == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && position>= getFirstVisible() && position <=getFirstVisible() + getViewTypeCount()){
//                                                    tv_chatcontent.postInvalidate();
//                                                }
                                            }
                                        }));
                        is.close();
                        sb.setSpan(
                                aspan,
                                sb.length() - tempText.length(), sb.length(),
                                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    } catch (Exception e) {
                    }
                }
            } else {
                sb.append(emojis.get(i).toString());
            }
        }
//		spanCache.put(position + "", sb);
//		}
        tv_chatcontent.setText(sb);
    }

    @Override
    public int getItemCount() {
        return mEmMessageList.size();
    }

    static class MeViewHolder extends BaseGroupItemViewHolder {
        @InjectView(R.id.root_layout)
        LinearLayout rootLayout;
        @InjectView(R.id.content_text)
        TextView contentText;
        @InjectView(R.id.content_img)
        ImageView contentImg;
        @InjectView(R.id.status_layout)
        FrameLayout statusLayout;
        @InjectView(R.id.message_loading_image)
        ImageView messageLoadingImage;
        @InjectView(R.id.message_resend_image)
        ImageView messageResendImage;
        @InjectView(R.id.content_gif)
        GifMovieView contentGif;

        MeViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
            ButterKnife.inject(this, view);
        }
    }

    static class OtherViewHolder extends BaseGroupItemViewHolder {
        @InjectView(R.id.root_layout)
        LinearLayout rootLayout;
        @InjectView(R.id.content_text)
        TextView contentText;
        @InjectView(R.id.content_img)
        ImageView contentImg;
        @InjectView(R.id.user_name_text)
        TextView userNameText;
        @InjectView(R.id.content_gif)
        GifMovieView contentGif;

        OtherViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
            ButterKnife.inject(this, view);
        }
    }


    static class BaseGroupItemViewHolder extends BaseViewHolder {
        @InjectView(R.id.message_time_text)
        TextView messageTimeText;
        @InjectView(R.id.time_layout)
        RelativeLayout timeLayout;
        @InjectView(R.id.icon_img)
        ImageView iconImg;
        @InjectView(R.id.icon_layout)
        LinearLayout iconLayout;
        @InjectView(R.id.badge_list_layout)
        LinearLayout badgeListLayout;

        public BaseGroupItemViewHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }


    static class NullViewHolder extends BaseViewHolder {

        NullViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
        }
    }
}

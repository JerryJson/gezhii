package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Html;
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
import android.widget.EditText;
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
import com.gezhii.fitgroup.dto.SigninDto;
import com.gezhii.fitgroup.dto.basic.Badge;
import com.gezhii.fitgroup.dto.basic.PushArticleAndSignin;
import com.gezhii.fitgroup.dto.basic.UserInfo;
import com.gezhii.fitgroup.dto.basic.UserInfoDto;
import com.gezhii.fitgroup.event.ATailEvent;
import com.gezhii.fitgroup.event.GroupMessageEvent;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.LikeSigninMessageExt;
import com.gezhii.fitgroup.model.SignCacheModel;
import com.gezhii.fitgroup.model.UserCacheModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.dialog.BigPhotoDialog;
import com.gezhii.fitgroup.ui.fragment.WebFragment;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.fragment.group.GroupChatPushArticleSigninDetailFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SignCardDetailFragment;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.melink.baseframe.bitmap.BitmapCreate;
import com.melink.baseframe.utils.DensityUtils;
import com.melink.baseframe.utils.StringUtils;
import com.melink.bqmmsdk.bean.Emoji;
import com.melink.bqmmsdk.sdk.BQMMSdk;
import com.melink.bqmmsdk.widget.AnimatedGifDrawable;
import com.melink.bqmmsdk.widget.AnimatedImageSpan;
import com.melink.bqmmsdk.widget.GifMovieView;
import com.thirdparty.bumptech.glide.Glide;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/10/22.
 */
public class GroupChatListAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    private static final int ITEM_TYPE_OTHER_CHAT = 0;
    private static final int ITEM_TYPE_ME_CHAT = 1;
    private static final int ITEM_TYPE_ME_SIGN = 2;
    private static final int ITEM_TYPE_OTHER_SIGN = 3;
    private static final int ITEM_TYPE_Q_REPORT = 4;
    private static final int ITEM_TYPE_SYSTEM_NOTICE = 5;
    private static final int ITEM_TYPE_NULL = 8;
    private static final int ITEM_TYPE_ME_LIKE = 6;
    private static final int ITEM_TYPE_OTHER_LIKE = 7;
    private static final int ITEM_TYPE_Q_PUSH = 9;

    Activity mContext;
    List<EMMessage> mEmMessageList;
    String huanxin_id;
    EditText mEditText;

    public GroupChatListAdapter(Activity mContext, List<EMMessage> mEmMessageList, String huanxin_id, EditText groupchatInput) {
        this.mEmMessageList = mEmMessageList;
        this.mContext = mContext;
        this.huanxin_id = huanxin_id;
        this.mEditText = groupchatInput;
    }

    public void setEmMessageList(List<EMMessage> mEmMessageList) {
        this.mEmMessageList = mEmMessageList;
    }

    //根据数据来确定每一个Item的类型(每一种类型,分左,右)
    @Override
    public int getItemViewType(int position) {
        boolean isFromMeSend = UserModel.getInstance().getUserHuanXinName().equals(mEmMessageList.get(position).getFrom());
        EMMessage emMessage = mEmMessageList.get(position);
        int type = emMessage.getIntAttribute("type", -1);
        if (type == 10) {
            if (isFromMeSend) {
                return ITEM_TYPE_ME_SIGN;
            } else {
                return ITEM_TYPE_OTHER_SIGN;
            }
        }
        if (type == 12) {
            return ITEM_TYPE_NULL;  //会长发公告,用了一个空的ViewHolder(NullViewHolder)
        } else if (type == 11) {
            if (isFromMeSend) {
                return ITEM_TYPE_ME_LIKE;
            } else {
                return ITEM_TYPE_OTHER_LIKE;
            }
        } else if (type == 13) {  //会长在群里发送有人入会的通知(纯文字,不能点击)
            return ITEM_TYPE_SYSTEM_NOTICE;
        } else if (type == 20) {  //氧气君在群里发日报,以及其他消息(纯文字,不能点击)
            return ITEM_TYPE_Q_REPORT;
        } else if (type == 23) {   //氧气君在群里推送文章、打卡（可以点击跳转相应详情）
            return ITEM_TYPE_Q_PUSH;
        } else {
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
        } else if (viewType == ITEM_TYPE_ME_SIGN) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_me_sign_list_item, null);
            viewHolder = new SignViewHolder(rootView, getOnListItemClickListener());
        } else if (viewType == ITEM_TYPE_OTHER_SIGN) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_other_sign_list_item, null);
            viewHolder = new SignViewHolder(rootView, getOnListItemClickListener());
        } else if (viewType == ITEM_TYPE_ME_LIKE) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_me_say_list_item, null);
            viewHolder = new MeViewHolder(rootView, getOnListItemClickListener());
        } else if (viewType == ITEM_TYPE_OTHER_LIKE) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_other_say_list_item, null);
            viewHolder = new OtherViewHolder(rootView, getOnListItemClickListener());
        } else if (viewType == ITEM_TYPE_Q_REPORT) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_system_notice_list_item, null);
            viewHolder = new QReportViewHolder(rootView, getOnListItemClickListener());
        } else if (viewType == ITEM_TYPE_NULL) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_null_list_item, null);
            viewHolder = new NullViewHolder(rootView, getOnListItemClickListener());
        } else if (viewType == ITEM_TYPE_SYSTEM_NOTICE) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_system_notice_list_item, null);
            viewHolder = new QReportViewHolder(rootView, getOnListItemClickListener());
        } else if (viewType == ITEM_TYPE_Q_PUSH) {
            rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_chat_push_article_signin_list_item, null);
            viewHolder = new QPushViewHolder(rootView, getOnListItemClickListener());
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
            case ITEM_TYPE_ME_SIGN:
                setSignItem(holder, emMessage, position);
                setBaseItem(holder, emMessage, position);
                break;
            case ITEM_TYPE_OTHER_SIGN:
                setSignItem(holder, emMessage, position);
                setBaseItem(holder, emMessage, position);
                break;
            case ITEM_TYPE_ME_LIKE:
                setMeLikeItem(holder, emMessage, position);
                setBaseItem(holder, emMessage, position);
                break;
            case ITEM_TYPE_OTHER_LIKE:
                setOtherLikeItem(holder, emMessage, position);
                setBaseItem(holder, emMessage, position);
                break;
            case ITEM_TYPE_Q_REPORT:
                setQReportItem(holder, emMessage, position);
//                setBaseItem(holder, emMessage, position);
                break;
            case ITEM_TYPE_SYSTEM_NOTICE:
                setSystemNoticeItem(holder, emMessage, position);
//                setBaseItem(holder, emMessage, position);
                break;
            case ITEM_TYPE_Q_PUSH:
                setQPushArticleAndSigninItem(holder, emMessage, position);
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
        if (UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()) != null) {
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
    }

    private void setSystemNoticeItem(RecyclerView.ViewHolder holder, EMMessage emMessage, int position) {
        final QReportViewHolder viewHolder = (QReportViewHolder) holder;
        boolean isShowTimeView = emMessage.getBooleanAttribute("isShowTimeView", false);
        if (isShowTimeView) {
            viewHolder.timeLayout.setVisibility(View.VISIBLE);
            viewHolder.messageTimeText.setText(TimeHelper.getTimeDifferenceString(new Date(emMessage.getMsgTime())));
        } else {
            viewHolder.timeLayout.setVisibility(View.GONE);
        }

        String newMemberNickName = emMessage.getStringAttribute("new_member_nick_name", "");
        if (!TextUtils.isEmpty(newMemberNickName)) {
            viewHolder.signTitle.setText(((TextMessageBody) emMessage.getBody()).getMessage());
        }
    }


    private void setQReportItem(RecyclerView.ViewHolder holder, EMMessage emMessage, int position) {
        final QReportViewHolder viewHolder = (QReportViewHolder) holder;
        boolean isShowTimeView = emMessage.getBooleanAttribute("isShowTimeView", false);
        if (isShowTimeView) {
            viewHolder.timeLayout.setVisibility(View.VISIBLE);
            viewHolder.messageTimeText.setText(TimeHelper.getTimeDifferenceString(new Date(emMessage.getMsgTime())));
        } else {
            viewHolder.timeLayout.setVisibility(View.GONE);
        }
        viewHolder.signTitle.setText(((TextMessageBody) emMessage.getBody()).getMessage());
    }

    private void setMeLikeItem(RecyclerView.ViewHolder holder, EMMessage emMessage, int position) {
        final MeViewHolder viewHolder = (MeViewHolder) holder;
        String icon = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).icon;
        QiniuHelper.bindAvatarImage(icon, viewHolder.iconImg);
        if (UserModel.getInstance().isGroupLeader()) {
            viewHolder.contentLayout.setBackgroundResource(R.drawable.me_sign_leader);
            viewHolder.groupLeaderCrownImg.setVisibility(View.VISIBLE);
        } else {
            viewHolder.contentLayout.setBackgroundResource(R.drawable.me_say_normal);
            viewHolder.groupLeaderCrownImg.setVisibility(View.INVISIBLE);
        }

        viewHolder.contentGif.setVisibility(View.GONE);

        //[赞]@user_name
        String message = ((TextMessageBody) emMessage.getBody()).getMessage();

        //1.2版本以后,[赞]后面加了@xxx (1.1之前,走的下面else逻辑)
        if (message.contains("@")) {
            message = message.substring(3, message.length());

            //把[赞]换成大拇指的图片,显示在TextView上
            Html.ImageGetter imgGetter = new Html.ImageGetter() {
                public Drawable getDrawable(String source) {
                    Drawable drawable = null;
                    int rId = Integer.parseInt(source);
                    drawable = mContext.getResources().getDrawable(rId);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                    return drawable;
                }
            };

            viewHolder.contentImg.setVisibility(View.GONE);

            if (!TextUtils.isEmpty(message)) {
                viewHolder.contentText.setVisibility(View.VISIBLE);
                viewHolder.contentText.setText(Html.fromHtml("<img src='" + R.mipmap.like_sign + "'/>" + message, imgGetter, null));
            } else {
                viewHolder.contentText.setVisibility(View.VISIBLE);
                viewHolder.contentText.setText(Html.fromHtml("<img src='" + R.mipmap.like_sign + "'/>", imgGetter, null));
            }

        } else {
            viewHolder.contentText.setVisibility(View.GONE);
            viewHolder.contentImg.setVisibility(View.VISIBLE);
            viewHolder.contentImg.setImageResource(R.mipmap.like_sign);
        }
    }

    private void setOtherLikeItem(RecyclerView.ViewHolder holder, final EMMessage emMessage, int position) {
        final OtherViewHolder viewHolder = (OtherViewHolder) holder;
        Log.i("---------oo--------->", UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()));

        if (UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()) != null) {
            String icon = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).icon;
            QiniuHelper.bindAvatarImage(icon, viewHolder.iconImg);
            if (UserModel.getInstance().getMyGroup().getLeader().getId() == Integer.valueOf(emMessage.getStringAttribute("user_id", "1"))) {
                viewHolder.groupLeaderCrownImg.setVisibility(View.VISIBLE);
                viewHolder.contentLayout.setBackgroundResource(R.drawable.other_say_red);
                viewHolder.contentText.setTextColor(mContext.getResources().getColor(R.color.white));

            } else {
                viewHolder.groupLeaderCrownImg.setVisibility(View.INVISIBLE);
                viewHolder.contentLayout.setBackgroundResource(R.drawable.other_say_normal);
            }
            viewHolder.iconImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String user_id = emMessage.getStringAttribute("user_id", "");
                    //MemberInfoFragment.start(mContext, Integer.valueOf(user_id));
                    UserProfileFragment.start(mContext, Integer.valueOf(user_id));
                }
            });

            final String nickName = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).nickName;
            if (!TextUtils.isEmpty(nickName)) {
                viewHolder.userNameText.setText(nickName);
            } else {
                viewHolder.userNameText.setText(" ");
            }
            viewHolder.iconImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MobclickAgent.onEvent(mContext, "group_chat", UmengEvents.getEventMap("click", "long press at"));
                    int index = mEditText.getSelectionStart();
                    Editable editable = mEditText.getText();
                    editable.insert(index, "@" + nickName + " ");
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(nickName, emMessage.getFrom());
                    EventBus.getDefault().post(new ATailEvent(params));
                    return true;
                }
            });

            viewHolder.contentGif.setVisibility(View.GONE);
            viewHolder.contentImg.setVisibility(View.GONE);
            viewHolder.contentText.setVisibility(View.VISIBLE);


            //[赞]@user_name
            String message = ((TextMessageBody) emMessage.getBody()).getMessage();

            if (message.contains("@")) {
                message = message.substring(3, message.length());
                //

                //把[赞]换成大拇指的图片,显示在TextView上
                Html.ImageGetter imgGetter = new Html.ImageGetter() {
                    public Drawable getDrawable(String source) {
                        Drawable drawable = null;
                        int rId = Integer.parseInt(source);
                        drawable = mContext.getResources().getDrawable(rId);
                        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                        return drawable;
                    }
                };

                viewHolder.contentImg.setVisibility(View.GONE);

                if (!TextUtils.isEmpty(message)) {
                    viewHolder.contentText.setVisibility(View.VISIBLE);
                    viewHolder.contentText.setText(Html.fromHtml("<img src='" + R.mipmap.like_sign + "'/>" + message, imgGetter, null));
                } else {
                    viewHolder.contentText.setVisibility(View.VISIBLE);
                    viewHolder.contentText.setText(Html.fromHtml("<img src='" + R.mipmap.like_sign + "'/>", imgGetter, null));
                }

            } else {
                viewHolder.contentText.setVisibility(View.GONE);
                viewHolder.contentImg.setVisibility(View.VISIBLE);
                viewHolder.contentImg.setImageResource(R.mipmap.like_sign);
            }

        }
    }


    private void setSignItem(RecyclerView.ViewHolder holder, final EMMessage emMessage, final int position) {

        final SignViewHolder viewHolder = (SignViewHolder) holder;
        String icon = "";
        if (UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()) != null) {
            icon = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).icon;
            QiniuHelper.bindAvatarImage(icon, viewHolder.iconImg);

            if (!emMessage.getFrom().equals(UserModel.getInstance().getUserHuanXinName())) {
                viewHolder.iconImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String user_id = emMessage.getStringAttribute("user_id", "");
                        //MemberInfoFragment.start(mContext, Integer.valueOf(user_id));
                        UserProfileFragment.start(mContext, Integer.valueOf(user_id));
                    }
                });
            }
            final String nickName = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).nickName;
            if (!TextUtils.isEmpty(nickName)) {
                viewHolder.userNameText.setText(nickName);
            } else {
                viewHolder.userNameText.setText(" ");
            }
            viewHolder.iconImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MobclickAgent.onEvent(mContext, "group_chat", UmengEvents.getEventMap("click", "long press at"));
                    int index = mEditText.getSelectionStart();
                    Editable editable = mEditText.getText();
                    editable.insert(index, "@" + nickName + " ");
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(nickName, emMessage.getFrom());
                    EventBus.getDefault().post(new ATailEvent(params));
                    return true;
                }
            });
        }


        String signin_id = emMessage.getStringAttribute("signin_id", "");
        if (!TextUtils.isEmpty(signin_id)) {
            final SigninDto signinDto = SignCacheModel.getInstance().getSign(Integer.parseInt(signin_id));
            if (signinDto == null) {
                //viewHolder.signCardEmptyImg.setVisibility(View.VISIBLE);
                //viewHolder.signCardNormalLayout.setVisibility(View.INVISIBLE);
                API.getSigninHttp(UserModel.getInstance().getUserId(), Integer.parseInt(signin_id),
                        new APICallBack() {
                            @Override
                            public void subRequestSuccess(String response) {
//                                emMessage.setAttribute("signin_data", response);
//                                EMChatManager.getInstance().updateMessageBody(emMessage);
                                SignCacheModel.getInstance().putSign(SigninDto.parserJson(response));
                                notifyItemChanged(position);
                            }
                        });
            } else {
                //设置卡片的标题 例如:"打卡-11月20日"
                if (signinDto.getSignin().getSignin_type() == 1) {
                    viewHolder.signCardTitleText.setText("请假-" + TimeHelper.getInstance().formatDateForTitle(signinDto.getSignin().getCreated_time()));
                } else {
                    viewHolder.signCardTitleText.setText("打卡-" + TimeHelper.getInstance().formatDateForTitle(signinDto.getSignin().getCreated_time()));
                }

                //设置卡片的图片
                if (!TextUtils.isEmpty(signinDto.getSignin().getImg())) {
                    QiniuHelper.bindImage(signinDto.getSignin().getImg(), viewHolder.signCardImg);
                } else {
                    if (signinDto.getSignin().getSignin_type() == 1) {
                        viewHolder.signCardImg.setImageResource(R.mipmap.leave_default_img);
                    } else {
                        viewHolder.signCardImg.setImageResource(R.mipmap.signin_default_img);
                    }
                }

                //设置图片右边的文字
                //请假,没有task,只有description
                if (signinDto.getSignin().getSignin_type() == 1) {
                    viewHolder.signTaskLayout.setVisibility(View.GONE);
                    viewHolder.signDescriptionText.setText(signinDto.getSignin().getDescription());
                    viewHolder.signDescriptionText.setLines(3);
                } else {
                    viewHolder.signTaskLayout.setVisibility(View.VISIBLE);
                    //打卡的task_name
                    if (!TextUtils.isEmpty(signinDto.getSignin().getTask_name()))
                        viewHolder.signTaskText.setText(signinDto.getSignin().getTask_name() + " "
                                + "累计打卡" + signinDto.getSignin().getTask_continue_days() + "天");

                    //打卡的感想(也就是描述)
                    if (!TextUtils.isEmpty(signinDto.getSignin().getDescription())) {
                        viewHolder.signDescriptionText.setVisibility(View.VISIBLE);
                        viewHolder.signDescriptionText.setLines(2);
                        viewHolder.signDescriptionText.setText(signinDto.getSignin().getDescription());
                    } else {
                        viewHolder.signDescriptionText.setVisibility(View.INVISIBLE);
                    }
                }

                //判断是否是会长,头上加皇冠, 同时边框改成红色
                if (Integer.valueOf(emMessage.getStringAttribute("user_id", "1")) == UserModel.getInstance().getMyGroup().getLeader().getId()) {
                    if (Integer.valueOf(emMessage.getStringAttribute("user_id", "1")) == UserModel.getInstance().getUserId()) {
                        viewHolder.signCardLayout.setBackgroundResource(R.drawable.me_sign_leader);
                    } else {
                        viewHolder.signCardLayout.setBackgroundResource(R.drawable.other_say_red);
                    }
                    viewHolder.groupLeaderCrownImg.setVisibility(View.VISIBLE);
                } else {
                    if (Integer.valueOf(emMessage.getStringAttribute("user_id", "1")) == UserModel.getInstance().getUserId()) {
                        viewHolder.signCardLayout.setBackgroundResource(R.drawable.me_say_normal);
                    } else {
                        viewHolder.signCardLayout.setBackgroundResource(R.drawable.other_say_normal);
                    }
                    viewHolder.groupLeaderCrownImg.setVisibility(View.INVISIBLE);
                }


                //点赞,点赞数
                viewHolder.signCountText.setText(String.valueOf(signinDto.getSignin().getLike_count()));
                if (signinDto.isLiked()) {
                    viewHolder.signCountText.setTextColor(mContext.getResources().getColor(R.color.pink_ff));
                    viewHolder.signImg.setImageResource(R.mipmap.chat_sign_like_select);
                    viewHolder.signLikeLayout.setEnabled(false);
                } else {
                    viewHolder.signCountText.setTextColor(mContext.getResources().getColor(R.color.gray_97));
                    viewHolder.signImg.setImageResource(R.mipmap.chat_sign_like_normal);
                    viewHolder.signLikeLayout.setEnabled(true);
                    viewHolder.signLikeLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MobclickAgent.onEvent(mContext, "group_chat", UmengEvents.getEventMap("click", "打卡点赞"));
                            API.LikeUserSigninHttp(UserModel.getInstance().getUserId(), signinDto.getSignin().getId(), new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) {
                                    HuanXinHelper.sendGroupTextMessage(new LikeSigninMessageExt(UserModel.getInstance().getUserNickName(),
                                                    UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), String.valueOf(signinDto.getSignin().getId())), UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(),
                                            "[赞]@" + UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).nickName, new EMCallBack() {
                                                @Override
                                                public void onSuccess() {
                                                    signinDto.getSignin().setLike_count(signinDto.getSignin().getLike_count() + 1);
                                                    signinDto.setIsLiked(true);
                                                    SignCacheModel.getInstance().putSign(signinDto);
                                                    EventBus.getDefault().post(new GroupMessageEvent());
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
                viewHolder.signCardLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(mContext, "group_chat", UmengEvents.getEventMap("click", "signin profile"));
                        SignCardDetailFragment.start(mContext, signinDto.getSignin().getId(), emMessage.getFrom());
                    }
                });
            }
        }
    }

    private void setOtherChatItem(RecyclerView.ViewHolder holder, final EMMessage emMessage, int position) {
        final OtherViewHolder viewHolder = (OtherViewHolder) holder;
        Log.i("darren", GsonHelper.getInstance().getGson().toJson(UserCacheModel.getInstance().getUserInfo(emMessage.getFrom())));
        if (UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()) != null) {
            String icon = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).icon;
            QiniuHelper.bindAvatarImage(icon, viewHolder.iconImg);

            final String nickName = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).nickName;
            if (!TextUtils.isEmpty(nickName)) {
                viewHolder.userNameText.setText(nickName);
            } else {
                viewHolder.userNameText.setText(" ");
            }
            viewHolder.iconImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    MobclickAgent.onEvent(mContext, "group_chat", UmengEvents.getEventMap("click", "long press at"));
                    int index = mEditText.getSelectionStart();
                    Editable editable = mEditText.getText();
                    editable.insert(index, "@" + nickName + " ");
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put(nickName, emMessage.getFrom());
                    EventBus.getDefault().post(new ATailEvent(params));
                    return true;
                }
            });
        }

        viewHolder.iconImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "group_chat", UmengEvents.getEventMap("click", "memeber icon"));
                final String user_id = emMessage.getStringAttribute("user_id", "");
                Log.i("user_id", user_id);
                //MemberInfoFragment.start(mContext, Integer.valueOf(user_id));
                UserProfileFragment.start(mContext, Integer.valueOf(user_id));
            }
        });

        if (UserModel.getInstance().getMyGroup().getLeader().getId() == Integer.valueOf(emMessage.getStringAttribute("user_id", "1"))) {
            viewHolder.groupLeaderCrownImg.setVisibility(View.VISIBLE);
            viewHolder.contentLayout.setBackgroundResource(R.drawable.other_say_red);
            viewHolder.contentText.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            viewHolder.groupLeaderCrownImg.setVisibility(View.INVISIBLE);
            viewHolder.contentLayout.setBackgroundResource(R.drawable.other_say_normal);
            viewHolder.contentText.setTextColor(mContext.getResources().getColor(R.color.black));
        }
        //Gif动画(BQMM)
        if (emMessage.getIntAttribute("type", -1) == 47) {
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
            viewHolder.contentText.setVisibility(View.GONE);
            viewHolder.contentGif.setVisibility(View.GONE);
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


//            imageMessageBody.setDownloadCallback(new EMCallBack() {
//                @Override
//                public void onSuccess() {
//                    QiniuHelper.bindImageByUrl(imageMessageBody.getRemoteUrl(), viewHolder.contentImg);
//                }
//
//                @Override
//                public void onError(int i, String s) {
//
//                }
//
//                @Override
//                public void onProgress(int i, String s) {
//
//                }
//            });
//            if (emMessage.getFrom().equals(UserModel.getInstance().getUserHuanXinName())) {
//                QiniuHelper.bindLocalImage(imageMessageBody.getLocalUrl(), viewHolder.iconImg);
//            } else {
//                QiniuHelper.bindImage(imageMessageBody.getRemoteUrl(), viewHolder.iconImg);
//            }
        }
    }

    private void setMeChatItem(RecyclerView.ViewHolder vh, final EMMessage emMessage, int position) {
        final MeViewHolder viewHolder = (MeViewHolder) vh;
        String icon = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).icon;
        QiniuHelper.bindAvatarImage(icon, viewHolder.iconImg);

        //statusLayout是个FrameLayout,里面包含了2张图:LoadingImage, ResendImage
        //消息有4种状态,是sdk维护的. 我们只是读取状态, 相应的显示菊花, 重发, 消失. 然后在回调里面,刷新列表.
        if (emMessage.status == EMMessage.Status.SUCCESS) {
            viewHolder.messageLoadingImage.clearAnimation();
            viewHolder.statusLayout.setVisibility(View.GONE);
        } else if (emMessage.status == EMMessage.Status.FAIL) {
            viewHolder.statusLayout.setVisibility(View.VISIBLE);
            viewHolder.messageResendImage.setVisibility(View.VISIBLE);

            viewHolder.messageLoadingImage.clearAnimation();
            viewHolder.messageLoadingImage.setVisibility(View.GONE);

            //重发的时候,会自动判重.所以不会加入到list view 末尾
            viewHolder.messageResendImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    viewHolder.messageResendImage.setVisibility(View.GONE);
                    viewHolder.messageLoadingImage.setVisibility(View.VISIBLE);

                    RotateAnimation rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                    rotateAnimation.setDuration(1000);
                    rotateAnimation.setRepeatCount(Animation.INFINITE);
                    rotateAnimation.setRepeatMode(Animation.RESTART);
                    rotateAnimation.setInterpolator(new LinearInterpolator());
                    viewHolder.messageLoadingImage.startAnimation(rotateAnimation);

                    EMChatManager.getInstance().sendMessage(emMessage, new EMCallBack() {
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


        if (UserModel.getInstance().isGroupLeader()) {
            viewHolder.contentLayout.setBackgroundResource(R.drawable.me_sign_leader);
            viewHolder.groupLeaderCrownImg.setVisibility(View.VISIBLE);
        } else {
            viewHolder.contentLayout.setBackgroundResource(R.drawable.me_say_normal);
            viewHolder.groupLeaderCrownImg.setVisibility(View.INVISIBLE);
        }
        //Gif动画(表情MM SDK)
        if (emMessage.getIntAttribute("type", -1) == 47) {
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
            viewHolder.contentText.setVisibility(View.GONE);
            viewHolder.contentGif.setVisibility(View.GONE);
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


        }
    }

    // TODO: 15/12/30 设置推送消息显示
    private void setQPushArticleAndSigninItem(RecyclerView.ViewHolder holder, final EMMessage emMessage, final int position) {
        final QPushViewHolder viewHolder = (QPushViewHolder) holder;

        boolean isShowTimeView = emMessage.getBooleanAttribute("isShowTimeView", false);
        if (isShowTimeView) {
            viewHolder.timeLayout.setVisibility(View.VISIBLE);
            viewHolder.messageTimeText.setText(TimeHelper.getTimeDifferenceString(new Date(emMessage.getMsgTime())));
        } else {
            viewHolder.timeLayout.setVisibility(View.GONE);
        }

        LinearLayout contentLayout = viewHolder.contentArticleSigninLayout;
        contentLayout.removeAllViews();

        String message = emMessage.getStringAttribute("content", "");
//        Log.i("logger----before", message + "--------" + message.length());

        message = message.replaceAll("\"\\[", "[").replaceAll("\\]\"", "]");

        List<PushArticleAndSignin> list = null;
//        Log.i("logger----after", message + "--------" + message.length());
        try {
            Gson gson = GsonHelper.getInstance().getGson();
            list = (List<PushArticleAndSignin>) gson.fromJson(message, new TypeToken<List<PushArticleAndSignin>>() {
            }.getType());
        } catch (Exception e) {
            message = null;
            Log.i("logger-----json", "json解析error");
        }
        if (null != message) {
            View view = null;

            for (int i = 0; i < list.size(); i++) {
                if (i == 0) {
                    view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_push_article_signin_type_bigimg, null);
                    ((TextView) view.findViewById(R.id.push_bigimg_title)).setText(list.get(i).getTitle());
                    QiniuHelper.bindImage(list.get(i).getImg(), (ImageView) view.findViewById(R.id.push_bigimg_img));
                } else {
                    view = LayoutInflater.from(mContext).inflate(R.layout.group_chat_push_article_signin_type_littleimg, null);
                    ((TextView) view.findViewById(R.id.push_litimg_title)).setText(list.get(i).getTitle());
                    QiniuHelper.bindImage(list.get(i).getImg(), (ImageView) view.findViewById(R.id.push_litimg_img));
                }

                view.setTag(i);
                final List<PushArticleAndSignin> finalList = list;
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MobclickAgent.onEvent(mContext, "group", UmengEvents.getEventMap("click", "小Q推送"));
                        Log.i("logger-------push", "index = " + view.getTag());
                        if (finalList.get((int) view.getTag()).getLink() != null) {
                            WebFragment.client(mContext, finalList.get((int) view.getTag()).getLink());
                        } else if (finalList.get((int) view.getTag()).getSign_ids() != null) {
                            GroupChatPushArticleSigninDetailFragment.start(mContext, finalList.get((int) view.getTag()).getSign_ids());
                        }
                    }
                });
                contentLayout.addView(view);
            }
        }
    }


    //这里的总个数 > 实际显示个数
    // messageList里面含有"会长公告",但是实际没显示,而是作为浮层,显示在外面的(这个不归adapter处理)
    //这里用的是NullViewHolder
    @Override
    public int getItemCount() {
        return mEmMessageList.size();
    }


    //自己发文字(带表情),图片,点赞, Gif动画(Face)
    static class MeViewHolder extends BaseGroupItemViewHolder {
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
        @InjectView(R.id.group_leader_crown_img)
        ImageView groupLeaderCrownImg;
        @InjectView(R.id.content_layout)
        FrameLayout contentLayout;

        @InjectView(R.id.content_gif)
        GifMovieView contentGif;

        MeViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
            ButterKnife.inject(this, view);
        }
    }

    //接受别人发文字(带表情),图片,点赞,Gif动画(Face)
    static class OtherViewHolder extends BaseGroupItemViewHolder {
        @InjectView(R.id.content_text)
        TextView contentText;
        @InjectView(R.id.content_img)
        ImageView contentImg;
        @InjectView(R.id.user_name_text)
        TextView userNameText;
        @InjectView(R.id.group_leader_crown_img)
        ImageView groupLeaderCrownImg;
        @InjectView(R.id.content_layout)
        FrameLayout contentLayout;

        @InjectView(R.id.content_gif)
        GifMovieView contentGif;

        OtherViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
            ButterKnife.inject(this, view);
        }
    }

    //自己和别人发打卡
    static class SignViewHolder extends BaseGroupItemViewHolder {
        @InjectView(R.id.user_name_text)
        TextView userNameText;
        @InjectView(R.id.sign_card_title_text)
        TextView signCardTitleText;
        @InjectView(R.id.sign_card_img)
        ImageView signCardImg;
        @InjectView(R.id.sign_card_small_icon_img)
        ImageView signCardSmallIconImg;
        @InjectView(R.id.sign_task_text)
        TextView signTaskText;
        @InjectView(R.id.sign_img)
        ImageView signImg;
        @InjectView(R.id.sign_count_text)
        TextView signCountText;
        @InjectView(R.id.sign_like_layout)
        LinearLayout signLikeLayout;
        @InjectView(R.id.root_layout)
        LinearLayout rootLayout;

        @InjectView(R.id.sign_card_layout)
        LinearLayout signCardLayout;
        @InjectView(R.id.group_leader_crown_img)
        ImageView groupLeaderCrownImg;

        @InjectView(R.id.sign_task_layout)
        LinearLayout signTaskLayout;
        @InjectView(R.id.sign_description_text)
        TextView signDescriptionText;

        SignViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
            ButterKnife.inject(this, view);
        }
    }


    //纯文字(会长发送有人入会, 小Q发日报)
    static class QReportViewHolder extends BaseGroupItemViewHolder {
        @InjectView(R.id.sign_title)
        TextView signTitle;

        QReportViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
        }
    }


    //所有item都有的3个属性:
    //1.消息时间: messageTimeText显示时间   timeLayout用于显示\隐藏
    //2.头像
    //3.徽章列表
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

    static class QPushViewHolder extends BaseViewHolder {

        @InjectView(R.id.time_layout)
        RelativeLayout timeLayout;
        @InjectView(R.id.message_time_text)
        TextView messageTimeText;
        @InjectView(R.id.content_article_signin_layout)
        LinearLayout contentArticleSigninLayout;
        @InjectView(R.id.icon_img)
        ImageView iconImg;
        @InjectView(R.id.user_name_text)
        TextView userNameText;

        public QPushViewHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_chat_me_sign_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */

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
}

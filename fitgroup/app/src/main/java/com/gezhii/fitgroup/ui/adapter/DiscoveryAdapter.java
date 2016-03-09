package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ChannelsDTO;
import com.gezhii.fitgroup.dto.StarLeaderListDTO;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.dto.basic.Tag;
import com.gezhii.fitgroup.event.FollowStateChangeEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.dialog.ShareToThirdDialog;
import com.gezhii.fitgroup.ui.fragment.WebFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.AllChannelsFragment;
import com.gezhii.fitgroup.ui.fragment.follow.AllCommentsFragment;
import com.gezhii.fitgroup.ui.fragment.follow.ChannelProfileFragment;
import com.gezhii.fitgroup.ui.fragment.follow.LikeSigninUsersFragment;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by fantasy on 16/2/15.
 */
public class DiscoveryAdapter extends LoadMoreListViewAdapter {

    private static final int TYPE_HEAD = 0;
    private static final int TYPE_ITEM = 1;
    int mFlag;
    Activity mContext;
    StarLeaderListDTO starLeaderListDTO;
    ChannelsDTO channelsDTO;
    List<Tag> channels;
    private ShareToThirdDialog mShareToThirdDialog;

    public DiscoveryAdapter(Activity mContext) {
        this.mContext = mContext;
    }

    @Override
    public int subGetItemViewType(int position) {

        return position == 0 ? TYPE_HEAD : TYPE_ITEM;

    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_HEAD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.discovery_fragment_header, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolderHead(view, getOnListItemClickListener());
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_and_post_history_list_item, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolderItem(view, getOnListItemClickListener());
        }

    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (getItemViewType(position) == TYPE_HEAD) {
            final ViewHolderHead viewHolderHead = (ViewHolderHead) holder;
            List list = new ArrayList();
            list = (ArrayList) getTotal_data_list().get(0);
            starLeaderListDTO = (StarLeaderListDTO) list.get(0);
            if (starLeaderListDTO.data_list.size() > 0) {
                int width = Screen.getScreenWidth();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.width = width;
                params.height = width * 400 / 750;
                viewHolderHead.discoveryHeaderGallery.setLayoutParams(params);
                viewHolderHead.discoveryHeaderGallery.setAdapter(new BaseAdapter() {
                    @Override
                    public int getCount() {
                        return Integer.MAX_VALUE;
                    }

                    @Override
                    public Object getItem(int position) {
                        return null;
                    }

                    @Override
                    public long getItemId(int position) {
                        return 0;
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {

                        final int index = position % starLeaderListDTO.data_list.size();

                        if (convertView == null) {
                            convertView = new ImageView(mContext);
                            Gallery.LayoutParams params = new Gallery.LayoutParams(Screen.getScreenWidth(), (int) ((Screen.getScreenWidth() * 2 / 3)));
                            convertView.setLayoutParams(params);
                        }
                        QiniuHelper.bindImage(starLeaderListDTO.data_list.get(index).img, (ImageView) convertView);
                        return convertView;
                    }

                });
                viewHolderHead.discoveryHeaderGallery.setSelection(Integer.MAX_VALUE / 2 - (Integer.MAX_VALUE / 2) % starLeaderListDTO.data_list.size());
                viewHolderHead.discoveryHeaderGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final int index = position % starLeaderListDTO.data_list.size();
                        WebFragment.client(mContext, starLeaderListDTO.data_list.get(index).img_link, 1);
                    }
                });
            }
            channelsDTO = (ChannelsDTO) list.get(1);
            channels = new ArrayList<Tag>();
            channels = channelsDTO.data_list;
            viewHolderHead.selectedChannel.removeAllViews();
            for (int i = 0; i < channels.size(); i++) {
                final Tag tag = channels.get(i);
                View channelView = LayoutInflater.from(mContext).inflate(R.layout.channel_list_item, null);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.setMargins(0, 0, 0,
                        5);
                channelView.setLayoutParams(layoutParams);
                ViewHolderChannel viewHolderChannel = new ViewHolderChannel(channelView);
                viewHolderChannel.channelNameText.setText(tag.getName());
                viewHolderChannel.channelContentCountText.setText("已产生" + tag.getContent_count() + "条内容");
                QiniuHelper.bindChannelImage(tag.getImg(), viewHolderChannel.channelImg);
                viewHolderChannel.channelLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ChannelProfileFragment.start(mContext, tag, tag.getId());
                    }
                });
                viewHolderHead.selectedChannel.addView(channelView);
            }
            viewHolderHead.goAllChannelsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MobclickAgent.onEvent(mContext, "RecommenChannel", UmengEvents.getEventMap("click", "load", "from", "all"));
                    AllChannelsFragment.start(mContext);
                }
            });

        } else {

            final ViewHolderItem mHolder = (ViewHolderItem) holder;
            if (total_data_list.get(position) instanceof Signin) {
                final Signin signin = (Signin) total_data_list.get(position);
                QiniuHelper.bindImage(signin.getUser().getIcon(), mHolder.userIconImg);
                mHolder.userNameText.setText(signin.getUser().getNick_name());
                if (signin.getUser().getVip() == 1) {
                    mHolder.userVipIcon.setVisibility(View.VISIBLE);
                } else {
                    mHolder.userVipIcon.setVisibility(View.GONE);
                }
                if (signin.getDescription().isEmpty()) {
                    mHolder.signDescriptionText.setVisibility(View.GONE);
                } else {
                    mHolder.signDescriptionText.setVisibility(View.VISIBLE);
                    mHolder.signDescriptionText.setText(signin.getDescription());
                }
                if (signin.getImg().isEmpty()) {
                    mHolder.signImg.setVisibility(View.GONE);
                } else {
                    mHolder.signImg.setVisibility(View.VISIBLE);
                    QiniuHelper.bingImageType(signin.getImg(), mHolder.signImg);
//                    mHolder.signImg.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            BigPhotoDialog bigPhotoDialog = new BigPhotoDialog(mContext);
//                            bigPhotoDialog.setQiniuUrl(signin.getImg());
//                            bigPhotoDialog.show();
//                        }
//                    });
                }
                if (signin.getPost_type() == 0) {
                    mHolder.taskLayout.setVisibility(View.VISIBLE);
                    mHolder.signTaskNameText.setText("#" + signin.getTask_name() + "#");
                    mHolder.signTaskContinueText.setText("累计打卡" + signin.getTask_continue_days() + "天");
                } else {
                    mHolder.taskLayout.setVisibility(View.GONE);
                }
                if (signin.getComment_count() > 3) {
                    mHolder.commentMoreLayout.setVisibility(View.VISIBLE);
                    mHolder.commentMoreCount.setText("查看所有" + signin.getComment_count() + "条评论");
                    mHolder.commentMoreCount.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AllCommentsFragment.start(mContext, signin.getId());
                        }
                    });
                } else {
                    mHolder.commentMoreLayout.setVisibility(View.GONE);
                }
                mHolder.commentContentLayout.removeAllViews();
                for (int i = 0; i < (signin.getComment_count() > 3 ? 3 : signin.getComment_count()); i++) {
                    TextView tv = new TextView(mContext);
                    String name = signin.getComments().get(i).getUser().getNick_name();//评论者的昵称
                    String nameAndComment = name + ":" + signin.getComments().get(i).getComment();
                    SpannableStringBuilder style = new SpannableStringBuilder(nameAndComment);
                    style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.colorPrimary)), 0, name.length() + 1, 0);
                    tv.setText(style);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AllCommentsFragment.start(mContext, signin.getId());
                        }
                    });
                    mHolder.commentContentLayout.addView(tv);
                }
                if (signin.getLike_count() != 0) {
                    mHolder.likeCountLayout.setVisibility(View.VISIBLE);
                    mHolder.likeCount.setText(signin.getLike_count() + "人赞");
                } else {
                    mHolder.likeCountLayout.setVisibility(View.GONE);
                }
                mHolder.likeCountLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LikeSigninUsersFragment.start(mContext, signin.getId());
                    }
                });
                if (signin.getFlag() == 1) {
                    mHolder.signLikeImg.setImageResource(R.mipmap.chat_sign_like_select);
                    mHolder.signLikeImg.setEnabled(false);
                } else {
                    mHolder.signLikeImg.setImageResource(R.mipmap.chat_sign_like_normal);
                    mHolder.signLikeImg.setEnabled(true);
                    mHolder.signLikeImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(final View view) {
                            view.setEnabled(false);
                            API.LikeUserSigninHttp(UserModel.getInstance().getUserId(), signin.getId(), new OnRequestEnd() {
                                @Override
                                public void onRequestSuccess(String response) {
                                    mContext.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            mHolder.signLikeImg.setImageResource(R.mipmap.chat_sign_like_select);
                                            signin.setFlag(1);
                                            mHolder.likeCountLayout.setVisibility(View.VISIBLE);
                                            mHolder.likeCount.setText((signin.getLike_count() + 1) + "人赞");
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
                mHolder.reportLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                        alertParams.setMessage("确定要举报该用户吗");
                        alertParams.setTitle("举报");
                        alertParams.setCancelString("取消");
                        alertParams.setConfirmString("确定");
                        alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                Toast.makeText(mContext, "举报成功", Toast.LENGTH_SHORT).show();
                            }
                        });
                        alertParams.setCancelListener(new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertHelper.showAlert(mContext, alertParams);
                    }
                });
                mHolder.shareToThirdBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mShareToThirdDialog = new ShareToThirdDialog(mContext, (Signin) getTotal_data_list().get(position));
                        mShareToThirdDialog.show();
                    }
                });
                mHolder.commentBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AllCommentsFragment.start(mContext, signin.getId());
                    }
                });
                mHolder.signNowDate.setText(TimeHelper.getNewTimeDifferenceString(signin.getCreated_time()));

                if (signin.getUser().getIs_following() == 0) {
                    mHolder.whetherFollowBtn.setVisibility(View.VISIBLE);
                    mHolder.whetherFollowBtn.setText("关 注");

                    mHolder.whetherFollowBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            API.followUser(UserModel.getInstance().getUserId(), signin.getUser_id(), 1, new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) throws NoSuchFieldException {
                                    mHolder.whetherFollowBtn.setVisibility(View.GONE);
                                    EventBus.getDefault().post(new FollowStateChangeEvent(0));
                                }
                            });
                        }
                    });
                } else {
                    mHolder.whetherFollowBtn.setVisibility(View.GONE);
                }
                mHolder.userIconImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserProfileFragment.start(mContext, signin.getUser_id());
                    }
                });

            }
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'discovery_fragment_header.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolderHead extends BaseViewHolder {
        @InjectView(R.id.discovery_header_gallery)
        Gallery discoveryHeaderGallery;
        @InjectView(R.id.selected_channel)
        LinearLayout selectedChannel;
        @InjectView(R.id.go_all_channels_btn)
        LinearLayout goAllChannelsBtn;

        ViewHolderHead(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'sign_history_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolderItem extends BaseViewHolder {
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

        ViewHolderItem(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'channel_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolderChannel {
        @InjectView(R.id.channel_img)
        ImageView channelImg;
        @InjectView(R.id.channel_name_text)
        TextView channelNameText;
        @InjectView(R.id.channel_content_count_text)
        TextView channelContentCountText;
        @InjectView(R.id.channel_layout)
        LinearLayout channelLayout;

        ViewHolderChannel(View view) {
            ButterKnife.inject(this, view);
        }
    }


}

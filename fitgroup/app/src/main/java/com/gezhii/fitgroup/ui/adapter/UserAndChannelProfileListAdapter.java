package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.ChannelSigninsDTO;
import com.gezhii.fitgroup.dto.OtherUserProfileDto;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.event.FollowStateChangeEvent;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.dialog.ShareToThirdDialog;
import com.gezhii.fitgroup.ui.fragment.WebFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.GroupSimpleProfileFragment;
import com.gezhii.fitgroup.ui.fragment.follow.AllCommentsFragment;
import com.gezhii.fitgroup.ui.fragment.follow.LikeSigninUsersFragment;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.fragment.me.MyFollowFragment;
import com.gezhii.fitgroup.ui.fragment.me.MyFollowerFragment;
import com.gezhii.fitgroup.ui.fragment.me.MyMenteesFragment;
import com.gezhii.fitgroup.ui.fragment.me.SportScheduleFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by zj on 16/2/16.
 */
public class UserAndChannelProfileListAdapter extends LoadMoreListViewAdapter {
    public static final String FLAG_USER = "FLAG_USER";
    public static final String FLAG_CHANNEL = "FLAG_CHANNEL";
    public static final String FLAG_NO_HEAD = "FLAG_NO_HEAD";

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_USER_HEAD = 1;
    private static final int TYPE_CHANNEL_HEAD = 2;

    private String mFlag;
    private Activity mContext;
    private ShareToThirdDialog mShareToThirdDialog;

    public UserAndChannelProfileListAdapter(Activity context, String flag_type) {
        this.mContext = context;
        this.mFlag = flag_type;
    }

    @Override
    public int subGetItemViewType(int position) {
        if (FLAG_USER.equals(mFlag))
            return position == 0 ? TYPE_USER_HEAD : TYPE_ITEM;
        else if (FLAG_CHANNEL.equals(mFlag))
            return position == 0 ? TYPE_CHANNEL_HEAD : TYPE_ITEM;
        else
            return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = null;
        if (viewType == TYPE_USER_HEAD) {
            rootView = View.inflate(mContext, R.layout.user_profile_head, null);
            return new ProfileUserHeadHolder(rootView, getOnListItemClickListener());
        } else if (viewType == TYPE_CHANNEL_HEAD) {
            rootView = View.inflate(mContext, R.layout.channel_profile_head, null);
            return new ProfileChannelHeadHolder(rootView, getOnListItemClickListener());
        } else {
            rootView = View.inflate(mContext, R.layout.sign_and_post_history_list_item, null);
            return new ProfileItemHolder(rootView, getOnListItemClickListener());
        }
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int itemType = getItemViewType(position);
        switch (itemType) {
            case TYPE_USER_HEAD:
                setUserHeadView(holder, position);
                break;
            case TYPE_CHANNEL_HEAD:
                setChannelHeadView(holder, position);
                break;
            case TYPE_ITEM:
                setProfileItemView(holder, position);
                break;
        }
    }

    private void setChannelHeadView(RecyclerView.ViewHolder holder, int position) {
        final ProfileChannelHeadHolder mHolder = (ProfileChannelHeadHolder) holder;

        final ChannelSigninsDTO dto = (ChannelSigninsDTO) total_data_list.get(0);
        QiniuHelper.bindChannelImage(dto.getTag().getImg(), mHolder.channelProfileIcon);
        if (!TextUtils.isEmpty(dto.getTag().getTag_description())) {
            mHolder.channelProfileDesc.setVisibility(View.VISIBLE);
            mHolder.channelProfileDesc.setText(dto.getTag().getTag_description());
        } else {
            mHolder.channelProfileDesc.setVisibility(View.GONE);
        }
        if (dto.getChannel_follow_flag() == 1) {
            mHolder.channelProfileFollowBtn.setSelected(true);
            mHolder.channelProfileFollowBtn.setText("已关注");
            mHolder.channelProfileFollowBtn.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            mHolder.channelProfileFollowBtn.setSelected(false);
            mHolder.channelProfileFollowBtn.setText("关注频道");
            mHolder.channelProfileFollowBtn.setTextColor(mContext.getResources().getColor(R.color.pink_ff));
        }
        mHolder.channelProfileFollowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int changeFlag = mHolder.channelProfileFollowBtn.isSelected() == true ? 0 : 1;
                if (changeFlag == 0) {
                    AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                    alertParams.setMessage("确定要取消关注频道吗");
                    alertParams.setTitle("取消关注");
                    alertParams.setCancelString("取消");
                    alertParams.setConfirmString("确定");
                    alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            API.followChannel(UserModel.getInstance().getUserId(), dto.getTag().getId(), changeFlag, new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) throws NoSuchFieldException {
                                    EventBus.getDefault().post(new FollowStateChangeEvent(3));
                                    mHolder.channelProfileFollowBtn.setText("关注频道");
                                    mHolder.channelProfileFollowBtn.setSelected(false);
                                    mHolder.channelProfileFollowBtn.setTextColor(mContext.getResources().getColor(R.color.pink_ff));
                                }
                            });
                        }
                    });
                    alertParams.setCancelListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertHelper.showAlert(mContext, alertParams);
                } else {
                    API.followChannel(UserModel.getInstance().getUserId(), dto.getTag().getId(), changeFlag, new APICallBack() {
                        @Override
                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                            EventBus.getDefault().post(new FollowStateChangeEvent(3));
                            mHolder.channelProfileFollowBtn.setText("已关注");
                            mHolder.channelProfileFollowBtn.setSelected(true);
                            mHolder.channelProfileFollowBtn.setTextColor(mContext.getResources().getColor(R.color.white));
                        }
                    });
                }
            }
        });
        mHolder.channelProfileName.setText(dto.getTag().getName());
        if (!TextUtils.isEmpty(dto.getTag().getBanner_img())) {
            mHolder.channelAdvLayout.setVisibility(View.VISIBLE);
            QiniuHelper.bindImage(dto.getTag().getBanner_img(), mHolder.channelAdvImg);
            mHolder.channelAdvImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WebFragment.client(mContext, dto.getTag().getBanner_link(), 1);
                }
            });
        } else {
            mHolder.channelAdvLayout.setVisibility(View.GONE);
        }
    }

    private void setProfileItemView(RecyclerView.ViewHolder holder, final int position) {


        final ProfileItemHolder mHolder = (ProfileItemHolder) holder;
        if (total_data_list.get(position) instanceof Signin) {
            final Signin signin = (Signin) total_data_list.get(position);
            if (mFlag.equals(FLAG_USER)) {
                final OtherUserProfileDto dto = (OtherUserProfileDto) total_data_list.get(0);
                final User user = dto.getUser();
                signin.setUser(user);
            }
            QiniuHelper.bindAvatarImage(signin.getUser().getIcon(), mHolder.userIconImg);
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
//                mHolder.signImg.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        BigPhotoDialog bigPhotoDialog = new BigPhotoDialog(mContext);
//                        bigPhotoDialog.setQiniuUrl(signin.getImg());
//                        bigPhotoDialog.show();
//                    }
//                });
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

            if (mFlag.equals(FLAG_CHANNEL)) {
                if (signin.getUser().getIs_following() == 0 && signin.getUser_id() != UserModel.getInstance().getUserId()) {
                    mHolder.whetherFollowBtn.setVisibility(View.VISIBLE);
                    mHolder.whetherFollowBtn.setText("关 注");

                    mHolder.whetherFollowBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            API.followUser(UserModel.getInstance().getUserId(), signin.getUser_id(), 1, new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) throws NoSuchFieldException {
                                    EventBus.getDefault().post(new FollowStateChangeEvent(0));
                                    mHolder.whetherFollowBtn.setVisibility(View.GONE);
                                    signin.getUser().setIs_following(1);
                                }
                            });
                        }
                    });
                } else {
                    mHolder.whetherFollowBtn.setVisibility(View.GONE);
                }
            } else {
                mHolder.whetherFollowBtn.setVisibility(View.GONE);
            }
            if (mFlag.equals(FLAG_CHANNEL) || mFlag.equals(FLAG_NO_HEAD)) {
                mHolder.userIconImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserProfileFragment.start(mContext, signin.getUser_id());
                    }
                });
            }
            mHolder.reportLayout.setOnClickListener(new View.OnClickListener() {
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
        }
    }

    private void setUserHeadView(RecyclerView.ViewHolder holder, int position) {
        final ProfileUserHeadHolder mHolder = (ProfileUserHeadHolder) holder;
        if (total_data_list.get(0) instanceof OtherUserProfileDto) {
            final OtherUserProfileDto dto = (OtherUserProfileDto) total_data_list.get(0);
            final User user = dto.getUser();
            if (user.getId() == UserModel.getInstance().getUserId()) {
                mHolder.userProfileLayout.setVisibility(View.GONE);
            } else {
                QiniuHelper.bindImage(user.getIcon(), mHolder.userProfileIcon);
                if (user.getVip() == 0) {
                    mHolder.userProfileVip.setVisibility(View.INVISIBLE);
                } else {
                    mHolder.userProfileVip.setVisibility(View.VISIBLE);
                }
                if (user.getSelf_description().trim().isEmpty()) {
                    mHolder.userProfileDescription.setVisibility(View.GONE);
                } else {
                    mHolder.userProfileDescription.setVisibility(View.VISIBLE);
                    mHolder.userProfileDescription.setText("自我介绍:" + user.getSelf_description());
                }
                if (user.getIs_following() == 1) {
                    mHolder.userProfileFollowBtn.setSelected(true);
                    mHolder.userProfileFollowBtn.setText("已关注");
                    mHolder.userProfileFollowBtn.setTextColor(mContext.getResources().getColor(R.color.white));
                } else {
                    mHolder.userProfileFollowBtn.setSelected(false);
                    mHolder.userProfileFollowBtn.setText("关注");
                    mHolder.userProfileFollowBtn.setTextColor(mContext.getResources().getColor(R.color.pink_ff));
                }
                mHolder.userProfileFollowBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final int changeFlag = mHolder.userProfileFollowBtn.isSelected() == true ? 0 : 1;
                        if (changeFlag == 0) {
                            AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                            alertParams.setMessage("确定要取消关注吗");
                            alertParams.setTitle("取消关注");
                            alertParams.setCancelString("取消");
                            alertParams.setConfirmString("确定");
                            alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                    API.followUser(UserModel.getInstance().getUserId(), user.getId(), changeFlag, new APICallBack() {
                                        @Override
                                        public void subRequestSuccess(String response) throws NoSuchFieldException {
                                            EventBus.getDefault().post(new FollowStateChangeEvent(1));
                                            mHolder.userProfileFollowBtn.setText("关注");
                                            mHolder.userProfileFollowBtn.setSelected(false);
                                            mHolder.userProfileFollowBtn.setTextColor(mContext.getResources().getColor(R.color.pink_ff));
                                        }
                                    });
                                }
                            });
                            alertParams.setCancelListener(new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            AlertHelper.showAlert(mContext, alertParams);
                        } else {
                            API.followUser(UserModel.getInstance().getUserId(), user.getId(), changeFlag, new APICallBack() {
                                @Override
                                public void subRequestSuccess(String response) throws NoSuchFieldException {
                                    EventBus.getDefault().post(new FollowStateChangeEvent(0));
                                    mHolder.userProfileFollowBtn.setText("已关注");
                                    mHolder.userProfileFollowBtn.setSelected(true);
                                    mHolder.userProfileFollowBtn.setTextColor(mContext.getResources().getColor(R.color.white));
                                }
                            });
                        }

                    }
                });
                if (null != dto.getGroup()) {
                    mHolder.userProfileGroup.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            GroupSimpleProfileFragment.start(mContext, dto.getGroup().getId(), dto.getGroup().getLeader().getHuanxin_id());
                        }
                    });
                } else {
                    mHolder.userProfileGroup.setBackgroundColor(mContext.getResources().getColor(R.color.gray_ea));
                    mHolder.userProfileGroupImg.setImageResource(R.mipmap.group_icon_null);
                    mHolder.userProfileGroupText.setTextColor(mContext.getResources().getColor(R.color.gray_c8));
                }
                mHolder.userProfileSportPlan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SportScheduleFragment.start(mContext, user.getId(), user.getIcon());
                    }
                });
                mHolder.userProfileFollowedCount.setText(String.valueOf(user.getFollowed_count()));
                mHolder.userProfileFollowingCount.setText(String.valueOf(user.getFollowing_count()));
                mHolder.userProfileAdoptionCount.setText(String.valueOf(user.getMentor_adoption_count()));
                mHolder.userProfileLevel.setText(String.valueOf(user.getLevel()));
            }
            mHolder.menteesLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyMenteesFragment.start(mContext, user.getId());
                }
            });
            mHolder.followingLaout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyFollowFragment.start(mContext, user.getId());
                }
            });
            mHolder.fansLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MyFollowerFragment.start(mContext, user.getId());
                }
            });
        }
    }

    static class ProfileUserHeadHolder extends BaseViewHolder {
        @InjectView(R.id.user_profile_layout)
        LinearLayout userProfileLayout;
        @InjectView(R.id.user_profile_icon)
        RectImageView userProfileIcon;
        @InjectView(R.id.user_profile_vip)
        RectImageView userProfileVip;
        @InjectView(R.id.user_profile_level)
        TextView userProfileLevel;
        @InjectView(R.id.user_profile_followed_count)
        TextView userProfileFollowedCount;
        @InjectView(R.id.user_profile_following_count)
        TextView userProfileFollowingCount;
        @InjectView(R.id.user_profile_follow_btn)
        TextView userProfileFollowBtn;
        @InjectView(R.id.user_profile_description)
        TextView userProfileDescription;
        @InjectView(R.id.user_profile_adoption_count)
        TextView userProfileAdoptionCount;
        @InjectView(R.id.user_profile_group)
        LinearLayout userProfileGroup;
        @InjectView(R.id.user_profile_sport_plan)
        LinearLayout userProfileSportPlan;
        @InjectView(R.id.user_profile_group_img)
        ImageView userProfileGroupImg;
        @InjectView(R.id.user_profile_group_text)
        TextView userProfileGroupText;
        @InjectView(R.id.mentees_layout)
        LinearLayout menteesLayout;
        @InjectView(R.id.fans_layout)
        LinearLayout fansLayout;
        @InjectView(R.id.following_layout)
        LinearLayout followingLaout;

        public ProfileUserHeadHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }

    static class ProfileChannelHeadHolder extends BaseViewHolder {
        @InjectView(R.id.channel_profile_icon)
        RectImageView channelProfileIcon;
        @InjectView(R.id.channel_profile_follow_btn)
        TextView channelProfileFollowBtn;
        @InjectView(R.id.channel_profile_name)
        TextView channelProfileName;
        @InjectView(R.id.channel_profile_desc)
        TextView channelProfileDesc;
        @InjectView(R.id.channel_adv_img)
        RectImageView channelAdvImg;
        @InjectView(R.id.channel_adv_layout)
        RelativeLayout channelAdvLayout;

        public ProfileChannelHeadHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }

    static class ProfileItemHolder extends BaseViewHolder {
        @InjectView(R.id.user_icon_img)
        RectImageView userIconImg;
        @InjectView(R.id.user_name_text)
        TextView userNameText;
        @InjectView(R.id.user_vip_icon)
        RectImageView userVipIcon;
        @InjectView(R.id.sign_description_text)
        TextView signDescriptionText;
        @InjectView(R.id.sign_img)
        ImageView signImg;
        @InjectView(R.id.like_count_layout)
        LinearLayout likeCountLayout;
        @InjectView(R.id.like_count)
        TextView likeCount;
        @InjectView(R.id.comment_content_layout)
        LinearLayout commentContentLayout;
        @InjectView(R.id.comment_more_count)
        TextView commentMoreCount;
        @InjectView(R.id.comment_more_layout)
        LinearLayout commentMoreLayout;
        @InjectView(R.id.sign_like_img)
        ImageView signLikeImg;
        @InjectView(R.id.comment_btn)
        LinearLayout commentBtn;
        @InjectView(R.id.share_to_third_btn)
        LinearLayout shareToThirdBtn;
        @InjectView(R.id.task_layout)
        LinearLayout taskLayout;
        @InjectView(R.id.sign_task_name_text)
        TextView signTaskNameText;
        @InjectView(R.id.sign_task_continue_text)
        TextView signTaskContinueText;
        @InjectView(R.id.sign_now_date)
        TextView signNowDate;
        @InjectView(R.id.whether_follow_btn)
        TextView whetherFollowBtn;
        @InjectView(R.id.report_layout)
        LinearLayout reportLayout;

        public ProfileItemHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }
}

package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupSimpleProfileDTO;
import com.gezhii.fitgroup.dto.SigninDto;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.huanxin.messageExt.LikeSigninMessageExt;
import com.gezhii.fitgroup.model.SignCacheModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.dialog.BigPhotoDialog;
import com.gezhii.fitgroup.ui.dialog.ShareToThirdDialog;
import com.gezhii.fitgroup.ui.fragment.WebFragment;
import com.gezhii.fitgroup.ui.fragment.discovery.GroupSimpleProfileFragment;
import com.gezhii.fitgroup.ui.fragment.me.PrivateChatFragment;
import com.gezhii.fitgroup.ui.view.IconsView;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.gezhii.fitgroup.ui.view.TagsView;
import com.gezhii.fitgroup.ui.view.TasksView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.List;

import butterknife.InjectView;

/**
 * Created by isansmith on 15/12/28.
 */
public class GroupSimpleProfileSignHistoryListAdapter extends LoadMoreListViewAdapter {
    private static final int TYPE_HEAD = 0;
    private static final int TYPE_ITEM = 1;
    private static final int FLAG_HASHEAD = 10;
    private static final int FLAG_NOHEAD = 11;

    Activity mContext;
    ShareToThirdDialog mShareToThirdDialog;
    GroupSimpleProfileDTO simpleProfileDTO;
    GroupSimpleProfileFragment groupSimpleProfileFragment;
    int mFlag;
    int group_id;

    public GroupSimpleProfileSignHistoryListAdapter(Activity mContext, GroupSimpleProfileFragment groupSimpleProfileFragment, int group_id, int flag) {
        this.mContext = mContext;
        this.groupSimpleProfileFragment = groupSimpleProfileFragment;
        this.mFlag = flag;
        this.group_id = group_id;
    }

    public GroupSimpleProfileSignHistoryListAdapter(Activity mContext, int flag) {
        this.mContext = mContext;
        this.mFlag = flag;
    }

    @Override
    public int subGetItemViewType(int position) {
        if (mFlag == FLAG_HASHEAD)
            return position == 0 ? TYPE_HEAD : TYPE_ITEM;
        else
            return TYPE_ITEM;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        if (viewType == TYPE_HEAD) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_simple_profile_fragment_header, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolderHead(view, getOnListItemClickListener());
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_history_list_item, null);
            view.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ViewHolderItem(view, getOnListItemClickListener());
        }
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        Log.i("logger-list-size", getTotal_data_list().size());
        Log.i("logger-position", position);
        if (getItemViewType(position) == TYPE_ITEM && position < getTotal_data_list().size()) {
            final ViewHolderItem viewHolder = (ViewHolderItem) holder;
            final Signin signin = (Signin) getTotal_data_list().get(position);
            final User user = signin.getUser();
            final SigninDto signinDto = new SigninDto();
            Log.i("logger-----signid", signin.getId());
            signinDto.setSignin(signin);
            SignCacheModel.getInstance().putSign(signinDto);

            if (signin.getFlag() == 0) {
                signinDto.setIsLiked(false);
            } else {
                signinDto.setIsLiked(true);
            }

            if (signinDto != null) {
                //头像,昵称,打卡时间
                String icon = user.getIcon();
                final String nick_name = user.getNick_name();
                if (!TextUtils.isEmpty(icon))
                    QiniuHelper.bindImage(icon, viewHolder.userIconImg);
                viewHolder.userNameText.setText(nick_name);
                viewHolder.signDateText.setText(TimeHelper.getInstance().formatDateForTitle(signinDto.getSignin().getCreated_time()));

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
                    if (UserModel.getInstance().getUserId() != -1) {
                        viewHolder.signLikeImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                API.LikeUserSigninHttp(UserModel.getInstance().getUserId(), signinDto.getSignin().getId(), new APICallBack() {
                                    @Override
                                    public void subRequestSuccess(String response) {
                                        if (UserModel.getInstance().getGroupId() == group_id) {
                                            Log.i("darren", nick_name);
                                            HuanXinHelper.sendGroupTextMessage(new LikeSigninMessageExt(UserModel.getInstance().getUserNickName(),
                                                            UserModel.getInstance().getUserIcon(), String.valueOf(UserModel.getInstance().getUserId()), String.valueOf(signinDto.getSignin().getId())), UserModel.getInstance().getUserDto().getGroup().getGroup_huanxin_id(),
                                                    "[赞]@" + nick_name, new EMCallBack() {
                                                        @Override
                                                        public void onSuccess() {
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
                                        } else {
                                            mContext.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    signin.setLike_count(signin.getLike_count() + 1);
                                                    signin.setFlag(1);
                                                    notifyItemChanged(position);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        });
                    }
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


            if (UserModel.getInstance().getUserId() != -1) {
                //分享
                viewHolder.shareToThirdBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MobclickAgent.onEvent(mContext, "signin_history", UmengEvents.getEventMap("click", "share"));
                        mShareToThirdDialog = new ShareToThirdDialog(mContext, (Signin) getTotal_data_list().get(position));
                        mShareToThirdDialog.show();
                    }
                });
                //私信
                viewHolder.privateChatBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PrivateChatFragment.start(mContext, ((Signin) getTotal_data_list().get(position)).getUser().getHuanxin_id(), ((Signin) getTotal_data_list().get(position)).getUser().getNick_name());
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
                }
            }


        } else if (getItemViewType(position) == TYPE_HEAD) {
            final ViewHolderHead viewHolder = (ViewHolderHead) holder;
            if (getTotal_data_list().size() > 1) {
                viewHolder.groupNoSignRecordsLayout.setVisibility(View.GONE);
            }
            simpleProfileDTO = (GroupSimpleProfileDTO) getTotal_data_list().get(0);

            viewHolder.rootLayout.setVisibility(View.INVISIBLE);
            viewHolder.rootLayout.setVisibility(View.VISIBLE);

            viewHolder.titleText.setText(simpleProfileDTO.group.getGroup_name());

            viewHolder.groupLeaderInfoText.setText("会长: " + simpleProfileDTO.group.getLeader().getNick_name()
                    + "  公会号: " + simpleProfileDTO.group.getGroup_number());

            //判断是否有明显专访
            if (TextUtils.isEmpty(simpleProfileDTO.group.getLeader().getUrl())) {
                viewHolder.starLeaderInterviewLayout.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.starLeaderInterviewLayout.setVisibility(View.VISIBLE);
                viewHolder.starLeaderInterviewLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        WebFragment.client(mContext, simpleProfileDTO.group.getLeader().getUrl());
                    }
                });
            }

            //会长及卡片
            QiniuHelper.bindAvatarImage(simpleProfileDTO.group.getLeader().getIcon(), viewHolder.groupLeaderIconImg);
            QiniuHelper.bindBlurImage(simpleProfileDTO.group.getLeader().getIcon(), viewHolder.groupSimpleProfileTitleBgImage);
            viewHolder.groupLevelText.setText(Integer.toString(simpleProfileDTO.group.getLevel()));

            if (simpleProfileDTO.group.yestoday_group_statistic != null)
                viewHolder.groupActivenessText.setText("" + (int) Math.floor(simpleProfileDTO.group.yestoday_group_statistic.getActiveness() * 100) + "%");
            else
                viewHolder.groupActivenessText.setText("0.0%");

            //公会描述
            viewHolder.groupDescriptionText.setText(simpleProfileDTO.group.getDescription());
            if (simpleProfileDTO.group.getDescription().length() >= 70) {
                viewHolder.showMoreDescriptionText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MobclickAgent.onEvent(mContext, "mygroup_profile", UmengEvents.getEventMap("click", "展开公会介绍"));
                        viewHolder.showMoreDescriptionText.setVisibility(View.GONE);
                        viewHolder.groupDescriptionText.setMaxLines(100);
                    }
                });
            } else {
                viewHolder.showMoreDescriptionText.setVisibility(View.GONE);
            }

            //公会标签
            if (!TextUtils.isEmpty(simpleProfileDTO.group.getTags()) && !simpleProfileDTO.group.getTags().equals("") && !simpleProfileDTO.group.getTags().equals("[]")) {
                viewHolder.groupTagText.setVisibility(View.VISIBLE);
                Gson gson = GsonHelper.getInstance().getGson();
                ArrayList<String> tags = gson.fromJson(simpleProfileDTO.group.getTags(), new TypeToken<ArrayList<String>>() {
                }.getType());
                Log.i("xianrui", GsonHelper.getInstance().getGson().toJson(simpleProfileDTO));
                if (tags != null && tags.size() > 0) {
                    viewHolder.tagsView.setTags(tags);
                }
            } else {
                viewHolder.groupTagText.setVisibility(View.GONE);
            }

            //公会任务,类似公会标签一样显示
            if (simpleProfileDTO.group.group_tasks.size() > 0) {
                List<String> tasks = new ArrayList<String>();
                for (int j = 0; j < simpleProfileDTO.group.group_tasks.size(); j++)
                    tasks.add(simpleProfileDTO.group.group_tasks.get(j).getTask_name());

                viewHolder.tasksView.setTasks(tasks);

            } else {
                viewHolder.tasksView.setVisibility(View.GONE);
                viewHolder.groupTaskTitleText.setVisibility(View.GONE);
                viewHolder.groupTaskDividerView.setVisibility(View.GONE);
            }

            //公会成员头像列表
            viewHolder.groupMemberCountText.setText("公会成员: " + simpleProfileDTO.group.getMember_count());
            List<String> icons = new ArrayList<String>();
            for (int i = 0; i < simpleProfileDTO.group.all_members.size(); i++) {
                if (simpleProfileDTO.group.all_members.get(i) != null) {
                    if (simpleProfileDTO.group.all_members.get(i).icon != null) {
                        icons.add(simpleProfileDTO.group.all_members.get(i).icon);
                    }
                }

            }
            if (icons.size() > 0) {
                viewHolder.iconsView.setTotalWidth(Screen.getScreenWidth() - 2 * Screen.dip2px(12) - 2 * Screen.dip2px(14));
                viewHolder.iconsView.setIcons(icons);
            }

        }
    }

    static class ViewHolderHead extends BaseViewHolder {
        //        @InjectView(R.id.back_btn)
//        ImageView backBtn;
        @InjectView(R.id.title_text)
        TextView titleText;
        @InjectView(R.id.group_leader_info_text)
        TextView groupLeaderInfoText;
        @InjectView(R.id.group_leader_icon_img)
        RectImageView groupLeaderIconImg;
        @InjectView(R.id.star_leader_interview_layout)
        FrameLayout starLeaderInterviewLayout;
        @InjectView(R.id.group_level_text)
        TextView groupLevelText;
        @InjectView(R.id.group_activeness_text)
        TextView groupActivenessText;
        @InjectView(R.id.group_description_text)
        TextView groupDescriptionText;
        @InjectView(R.id.tags_view)
        TagsView tagsView;
        @InjectView(R.id.icons_view)
        IconsView iconsView;
        @InjectView(R.id.group_member_count_text)
        TextView groupMemberCountText;
        @InjectView(R.id.group_simple_profile_title_bg_image)
        ImageView groupSimpleProfileTitleBgImage;
        @InjectView(R.id.root_layout)
        ScrollView rootLayout;
        @InjectView(R.id.group_tag_text)
        TextView groupTagText;
        @InjectView(R.id.show_more_description_text)
        TextView showMoreDescriptionText;


        @InjectView(R.id.tasks_view)
        TasksView tasksView;
        @InjectView(R.id.group_task_title_text)
        TextView groupTaskTitleText;
        @InjectView(R.id.group_task_divider_view)
        View groupTaskDividerView;
        @InjectView(R.id.group_no_sign_records_layout)
        RelativeLayout groupNoSignRecordsLayout;


        public ViewHolderHead(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }

    static class ViewHolderItem extends BaseViewHolder {
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
        @InjectView(R.id.share_to_third_btn)
        LinearLayout shareToThirdBtn;
        @InjectView(R.id.share_btn_layout)
        LinearLayout shareBtnLayout;
        @InjectView(R.id.share_to_third_layout)
        LinearLayout shareToThirdLayout;
        @InjectView(R.id.private_chat_btn)
        LinearLayout privateChatBtn;

        public ViewHolderItem(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }

}

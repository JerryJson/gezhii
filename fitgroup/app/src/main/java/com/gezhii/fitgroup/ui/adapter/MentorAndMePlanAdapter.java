package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.MentorAndMePlanDto;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.dto.basic.UserCustomerTask;
import com.gezhii.fitgroup.model.UserCustomerTaskModel;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.fragment.plan.MySelfAddTaskFragment;
import com.gezhii.fitgroup.ui.fragment.signin.AddTaskFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SignTaskLinkedFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SigninAndPostDetailFragment;
import com.gezhii.fitgroup.ui.view.AlertHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by zj on 16/2/20.
 */
public class MentorAndMePlanAdapter extends LoadMoreListViewAdapter {

    private Activity mContext;

    public MentorAndMePlanAdapter(Activity activity) {
        this.mContext = activity;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mContext, R.layout.follow_mentor_plan_fragment, null);
        return new PlanItemHolder(rootView, getOnListItemClickListener());
    }

    @Override
    public void subOnBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        final PlanItemHolder mHolder = (PlanItemHolder) holder;
        final MentorAndMePlanDto mentorAndMePlanDto = (MentorAndMePlanDto) total_data_list.get(position);
        final List<Signin> mentor_signin = mentorAndMePlanDto.getMentor_signin();
        final List<Signin> user_signin = mentorAndMePlanDto.getUser_signin();

        mHolder.mentorSigninLayout.removeAllViews();
        mHolder.userSigninLayout.removeAllViews();
        mHolder.dayNumber.setText("第" + mentorAndMePlanDto.getDay_number() + "天");
        mHolder.selfPlanUser.setVisibility(View.GONE);

        mHolder.planMentorIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserProfileFragment.start(mContext,mentorAndMePlanDto.getMentor().getId());
            }
        });

        QiniuHelper.bindImage(UserModel.getInstance().getUserIcon(), mHolder.planUserIcon);
        if (null != mentorAndMePlanDto.getMentor()) {
            mHolder.mentorPlanLayout.setVisibility(View.VISIBLE);
            mHolder.mentorSigninLayout.setVisibility(View.VISIBLE);
            QiniuHelper.bindImage(mentorAndMePlanDto.getMentor().getIcon(), mHolder.planMentorIcon);
        } else {
            mHolder.mentorSigninLayout.setVisibility(View.GONE);
            mHolder.mentorPlanLayout.setVisibility(View.GONE);
        }

        if (mentorAndMePlanDto.isMentor_is_rest()) {
            View mentor_item = View.inflate(mContext, R.layout.plan_mentor_signin_item, null);
            MentorSignItemHolder mentorSignItemHolder = new MentorSignItemHolder(mentor_item);
            mentorSignItemHolder.mentorRestLayout.setVisibility(View.VISIBLE);
            mentorSignItemHolder.mentorSigninLayout.setVisibility(View.GONE);
            mentorSignItemHolder.mentorRestTime.setText(mentorAndMePlanDto.getMentor_date());
            mHolder.mentorSigninLayout.addView(mentor_item);
        }
        if (mentorAndMePlanDto.getIsChangeMentor()) {
            mHolder.followMentorTipNfo.setVisibility(View.VISIBLE);
            if (mentorAndMePlanDto.getMentor() != null)
                mHolder.followMentorTipNfo.setText("跟随" + mentorAndMePlanDto.getMentor().getNick_name() + "一起运动");
            else
                mHolder.followMentorTipNfo.setText("自己练");
        } else {
            mHolder.followMentorTipNfo.setVisibility(View.GONE);
        }
        if (mentorAndMePlanDto.isToday()) {
            if (mentorAndMePlanDto.getMentor() != null) {
                //跟随mentor练的今天的View视图
                mHolder.mentorUserLayout.setVisibility(View.VISIBLE);
                mHolder.mentorSigninLayout.setVisibility(View.VISIBLE);
                mHolder.planUserTitle.setText("今日任务");
                for (int i = 0; i < mentor_signin.size(); i++) {
                    View mentor_item = View.inflate(mContext, R.layout.plan_mentor_signin_item, null);
                    MentorSignItemHolder mentorSignItemHolder = new MentorSignItemHolder(mentor_item);
                    mentorSignItemHolder.mentorSigninName.setText(mentor_signin.get(i).getTask_name());
                    mentorSignItemHolder.mentorSigninTime.setText(TimeHelper.getTimeDifferenceString(mentor_signin.get(i).getCreated_time()));
                    mentorSignItemHolder.mentorSigninDesc.setText(mentor_signin.get(i).getDescription());
                    mentor_item.setTag(mentor_signin.get(i));
                    mentor_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Signin signin = (Signin) view.getTag();
                            SigninAndPostDetailFragment.start(mContext, signin);
                        }
                    });

                    if (!TextUtils.isEmpty(mentor_signin.get(i).getImg())) {
                        QiniuHelper.bindImage(mentor_signin.get(i).getImg(), mentorSignItemHolder.mentorSigninImg);
                    }
                    mHolder.mentorSigninLayout.addView(mentor_item);
                    if (mentorAndMePlanDto.getAll_done().contains(mentor_signin.get(i).getTask_name())) {
                        View user_item = View.inflate(mContext, R.layout.plan_user_signin_item, null);
                        UserSignItemHolder userSignItemHolder = new UserSignItemHolder(user_item);
                        userSignItemHolder.userSigninDoneLayout.setVisibility(View.VISIBLE);
                        final Signin signin = getSiginUseTaskName(user_signin, mentor_signin.get(i).getTask_name());
                        if(signin.getHasVideo() != 0){
                            userSignItemHolder.videoCourseLayout.setVisibility(View.VISIBLE);
                        }else {
                            userSignItemHolder.videoCourseLayout.setVisibility(View.INVISIBLE);
                        }
                        userSignItemHolder.userSigninName.setText(signin.getTask_name());
                        if (!TextUtils.isEmpty(signin.getDescription())) {
                            userSignItemHolder.userSigninDesc.setText(signin.getDescription());
                            userSignItemHolder.userSigninEdit.setVisibility(View.GONE);
                        }
                        if(!TextUtils.isEmpty(signin.getImg())){
                            userSignItemHolder.userSigninImg.setVisibility(View.VISIBLE);
                            userSignItemHolder.userSigninEdit.setVisibility(View.GONE);
                            QiniuHelper.bindAvatarImage(signin.getImg(),userSignItemHolder.userSigninImg);
                        }else{
                            userSignItemHolder.userSigninImg.setVisibility(View.GONE);
                        }
                        if(TextUtils.isEmpty(signin.getDescription()) && TextUtils.isEmpty(signin.getImg())){
                            userSignItemHolder.userSigninEdit.setVisibility(View.VISIBLE);
                        }
                        user_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                signin.setUser(UserModel.getInstance().getUserDto().getUser());
                                boolean isNote = !TextUtils.isEmpty(signin.getDescription()) || !TextUtils.isEmpty(signin.getImg());
                                SignTaskLinkedFragment.start(mContext, signin.getTask_name(), true, isNote,signin.getId());
                            }
                        });
                        mHolder.userSigninLayout.addView(user_item);
                    } else if (mentorAndMePlanDto.getOnly_mentor_done().contains(mentor_signin.get(i).getTask_name())) {
                        View user_item = View.inflate(mContext, R.layout.plan_user_signin_item, null);
                        UserSignItemHolder userSignItemHolder = new UserSignItemHolder(user_item);
                        final Signin signin = getSiginUseTaskName(mentor_signin, mentor_signin.get(i).getTask_name());
                        userSignItemHolder.userSigninName.setText(signin.getTask_name());
                        userSignItemHolder.userSigninUndoneLayout.setVisibility(View.VISIBLE);
                        userSignItemHolder.userSigninEdit.setVisibility(View.GONE);

                        user_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                SignTaskLinkedFragment.start(mContext, signin.getTask_name(), false, false);
                            }
                        });
                        mHolder.userSigninLayout.addView(user_item);
                    }

                }
                for (int i = 0; i < user_signin.size(); i++) {
                    if (mentorAndMePlanDto.getOnly_user_done().contains(user_signin.get(i).getTask_name())) {
                        View user_item = View.inflate(mContext, R.layout.plan_user_signin_item, null);
                        UserSignItemHolder userSignItemHolder = new UserSignItemHolder(user_item);
                        user_item.setTag(user_signin.get(i));
                        userSignItemHolder.userSigninName.setText(user_signin.get(i).getTask_name());
                        userSignItemHolder.userSigninDesc.setText(user_signin.get(i).getDescription());
                        userSignItemHolder.userSigninDoneLayout.setVisibility(View.VISIBLE);
                        if(!TextUtils.isEmpty(user_signin.get(i).getImg())){
                            userSignItemHolder.userSigninImg.setVisibility(View.VISIBLE);
                            QiniuHelper.bindAvatarImage(user_signin.get(i).getImg(),userSignItemHolder.userSigninImg);
                        }else{
                            userSignItemHolder.userSigninImg.setVisibility(View.GONE);
                        }
                        userSignItemHolder.userSigninDesc.setText(user_signin.get(i).getDescription());
                        if(TextUtils.isEmpty(user_signin.get(i).getDescription()) && TextUtils.isEmpty(user_signin.get(i).getImg())){
                            userSignItemHolder.userSigninEdit.setVisibility(View.VISIBLE);
                        }else{
                            userSignItemHolder.userSigninEdit.setVisibility(View.GONE);
                        }
                        mHolder.userSigninLayout.addView(user_item);
                        user_item.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Signin signin = (Signin) view.getTag();
                                signin.setUser(UserModel.getInstance().getUserDto().getUser());
                                boolean isNote = !TextUtils.isEmpty(signin.getDescription()) || !TextUtils.isEmpty(signin.getImg());
                                SignTaskLinkedFragment.start(mContext, signin.getTask_name(), true, isNote,signin.getId());
                            }
                        });
                        View nullView = View.inflate(mContext, R.layout.mentor_sign_null, null);
                        mHolder.mentorSigninLayout.addView(nullView);
                    }
                }
                View moreMissionView = View.inflate(mContext, R.layout.plan_user_more_mission, null);
                moreMissionView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MySelfAddTaskFragment.start(mContext,user_signin);
                    }
                });
                mHolder.userSigninLayout.addView(moreMissionView);
                mHolder.mentorSigninLayout.addView(View.inflate(mContext,R.layout.plan_mentor_more_layout,null));
                if(mentorAndMePlanDto.isMentor_is_rest() && mHolder.mentorSigninLayout.getChildCount() > 2){
                    mHolder.mentorSigninLayout.removeViewAt(1);
                }
            } else {
                //自己练，今天的显示的view视图
                mHolder.mentorUserLayout.setVisibility(View.GONE);
                mHolder.mentorSigninLayout.setVisibility(View.GONE);
                mHolder.addTaskBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        UserCustomerTaskModel.getInstance().isFromUserCustomerTask = true;
                        AddTaskFragment.start(mContext);
                    }
                });
                mHolder.todaySiginLayout.removeAllViews();
                final List<UserCustomerTask> userCustomerTaskList = UserCustomerTaskModel.getInstance().getUserCustomerTaskListModel();
                if (userCustomerTaskList != null && userCustomerTaskList.size() != 0) {
                    if (UserCustomerTaskModel.getInstance().isShowLongClickDeleteCustomerTask()) {
                        mHolder.longClickDeleteCustomerTaskLayout.setVisibility(View.VISIBLE);
                        mHolder.longClickDeleteCustomerTaskImg.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                UserCustomerTaskModel.getInstance().hideLongClickDeleteCustomerTask();
                                mHolder.longClickDeleteCustomerTaskLayout.setVisibility(View.GONE);
                            }
                        });
                    } else {
                        mHolder.longClickDeleteCustomerTaskLayout.setVisibility(View.GONE);
                    }
                    for (final UserCustomerTask customerTask : userCustomerTaskList) {
                        final View itemView = LayoutInflater.from(mContext).inflate(R.layout.plan_user_signin_item, null);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.setMargins(0, 0, 0, 0);
                        itemView.setLayoutParams(layoutParams);
                        Log.i("darrens", customerTask.getSign_type());
                        SelfPlanUserHolder viewHolder = new SelfPlanUserHolder(itemView);
                        viewHolder.userSigninName.setText(customerTask.getTask_name());
                        if (getSiginUseTaskName(user_signin, customerTask.getTask_name()) != null) {
                            Signin signin = getSiginUseTaskName(user_signin, customerTask.getTask_name());
                            viewHolder.userSigninDesc.setText(signin.getDescription());
                            if (!TextUtils.isEmpty(signin.getImg())) {
                                viewHolder.userSigninImg.setVisibility(View.VISIBLE);
                                QiniuHelper.bindAvatarImage(getSiginUseTaskName(user_signin, customerTask.getTask_name()).getImg(), viewHolder.userSigninImg);
                            }else{
                                viewHolder.userSigninImg.setVisibility(View.GONE);
                            }
                            if(TextUtils.isEmpty(signin.getDescription()) && TextUtils.isEmpty(signin.getImg())){
                                viewHolder.userSigninEdit.setVisibility(View.VISIBLE);
                            }else{
                                viewHolder.userSigninEdit.setVisibility(View.GONE);
                            }
                            viewHolder.userSigninUndoneLayout.setVisibility(View.GONE);
                            viewHolder.userSigninDoneLayout.setVisibility(View.VISIBLE);
                            viewHolder.userSigninLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Signin signin = getSiginUseTaskName(mentorAndMePlanDto.getUser_signin(),customerTask.getTask_name());
                                    boolean isNote = (!TextUtils.isEmpty(signin.getImg()) || !TextUtils.isEmpty(signin.getDescription()));
                                    SignTaskLinkedFragment.start(mContext,customerTask.getTask_name(),true,isNote,signin.getId());
                                }
                            });
                        } else {
                            viewHolder.userSigninLayout.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(final View v) {
                                    AlertHelper.AlertParams alertParams = new AlertHelper.AlertParams();
                                    alertParams.setTitle("删除任务");
                                    alertParams.setMessage("确定要删除该自定义任务吗?");
                                    alertParams.setCancelString("取消");
                                    alertParams.setConfirmString("确定");
                                    alertParams.setCancelListener(new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alertParams.setConfirmListener(new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            UserCustomerTaskModel.getInstance().deleteUserCustomerTask(customerTask.getTask_id());
                                            mHolder.todaySiginLayout.removeView(itemView);
                                        }
                                    });
                                    AlertHelper.showAlert(mContext, alertParams);
                                    return true;
                                }
                            });
                            viewHolder.userSigninEdit.setVisibility(View.GONE);
                            viewHolder.userSigninUndoneLayout.setVisibility(View.VISIBLE);
                            viewHolder.userSigninDoneLayout.setVisibility(View.GONE);
                            viewHolder.userSigninLayout.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    SignTaskLinkedFragment.start(mContext, customerTask.getTask_name(), false, false);
                                }
                            });
                        }
                        mHolder.todaySiginLayout.addView(itemView);
                    }
                }
                for(int i=0;i<user_signin.size();i++){
                    boolean isShow = true;
                    if(userCustomerTaskList != null && userCustomerTaskList.size() != 0){
                        for (final UserCustomerTask customerTask : userCustomerTaskList) {
                            if(customerTask.getTask_name().equals(user_signin.get(i).getTask_name()))
                                isShow = false;
                        }
                    }
                    if(isShow){
                        View itemView = View.inflate(mContext,R.layout.plan_user_signin_item,null);
                        itemView.setTag(user_signin.get(i));
                        UserSignItemHolder itemHolder = new UserSignItemHolder(itemView);
                        itemHolder.userSigninDoneLayout.setVisibility(View.VISIBLE);
                        itemHolder.userSigninUndoneLayout.setVisibility(View.GONE);
                        itemHolder.userSigninDesc.setText(user_signin.get(i).getDescription());
                        itemHolder.userSigninName.setText(user_signin.get(i).getTask_name());
                        if(!TextUtils.isEmpty(user_signin.get(i).getImg())){
                            itemHolder.userSigninImg.setVisibility(View.VISIBLE);
                            QiniuHelper.bindAvatarImage(user_signin.get(i).getImg(),itemHolder.userSigninImg);
                        }else{
                            itemHolder.userSigninImg.setVisibility(View.GONE);
                        }
                        if(TextUtils.isEmpty(user_signin.get(i).getDescription()) && TextUtils.isEmpty(user_signin.get(i).getImg())){
                            itemHolder.userSigninEdit.setVisibility(View.VISIBLE);
                        }else{
                            itemHolder.userSigninEdit.setVisibility(View.GONE);
                        }
                        itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Signin signin = (Signin) view.getTag();
                                boolean isNote = (!TextUtils.isEmpty(signin.getImg()) || !TextUtils.isEmpty(signin.getDescription()));
                                SignTaskLinkedFragment.start(mContext,signin.getTask_name(),true,isNote,signin.getId());
                            }
                        });
                        mHolder.todaySiginLayout.addView(itemView);
                    }
                }
                mHolder.selfPlanUser.setVisibility(View.VISIBLE);
            }
        } else {
            //今天之前的历史打卡View视图
            if (mentorAndMePlanDto.getMentor() != null) {
                mHolder.mentorUserLayout.setVisibility(View.VISIBLE);
                mHolder.mentorSigninLayout.setVisibility(View.VISIBLE);
            } else {
                mHolder.mentorUserLayout.setVisibility(View.VISIBLE);
                mHolder.mentorSigninLayout.setVisibility(View.GONE);
            }
            if (mentorAndMePlanDto.isUser_is_rest()) {
                View user_item = View.inflate(mContext, R.layout.plan_user_signin_item, null);
                UserSignItemHolder userSignItemHolder = new UserSignItemHolder(user_item);
                userSignItemHolder.userSigninLayout.setVisibility(View.GONE);
                userSignItemHolder.userRestLayout.setVisibility(View.VISIBLE);
                mHolder.userSigninLayout.addView(user_item);
            }
            mHolder.planUserTitle.setText(mentorAndMePlanDto.getUser_date());
            for (int i = 0; i < mentor_signin.size(); i++) {
                View mentor_item = View.inflate(mContext, R.layout.plan_mentor_signin_item, null);
                Signin signin_mentor = mentor_signin.get(i);
                mentor_item.setTag(signin_mentor);
                MentorSignItemHolder mentorSignItemHolder = new MentorSignItemHolder(mentor_item);
                mentorSignItemHolder.mentorSigninName.setText(mentor_signin.get(i).getTask_name());
                mentorSignItemHolder.mentorSigninTime.setText(TimeHelper.getTimeDifferenceString(mentor_signin.get(i).getCreated_time()));
                mentorSignItemHolder.mentorSigninDesc.setText(mentor_signin.get(i).getDescription());
                if (!TextUtils.isEmpty(mentor_signin.get(i).getImg())) {
                    QiniuHelper.bindImage(mentor_signin.get(i).getImg(), mentorSignItemHolder.mentorSigninImg);
                }
                mentor_item.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SigninAndPostDetailFragment.start(mContext, (Signin) view.getTag());
                    }
                });
                mHolder.mentorSigninLayout.addView(mentor_item);

                if (mentorAndMePlanDto.getAll_done().contains(mentor_signin.get(i).getTask_name())) {
                    View user_item = View.inflate(mContext, R.layout.plan_user_signin_item, null);
                    UserSignItemHolder userSignItemHolder = new UserSignItemHolder(user_item);
                    userSignItemHolder.userSigninDoneLayout.setVisibility(View.VISIBLE);
                    final Signin signin = getSiginUseTaskName(user_signin, mentor_signin.get(i).getTask_name());
                    userSignItemHolder.userSigninName.setText(signin.getTask_name());
                    userSignItemHolder.userSigninDesc.setText(signin.getDescription());
                    userSignItemHolder.userSigninEdit.setVisibility(View.GONE);
                    if(!TextUtils.isEmpty(signin.getImg())) {
                        userSignItemHolder.userSigninImg.setVisibility(View.VISIBLE);
                        QiniuHelper.bindAvatarImage(signin.getImg(), userSignItemHolder.userSigninImg);
                    }
                    user_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            SigninAndPostDetailFragment.start(mContext, signin);
                        }
                    });
                    mHolder.userSigninLayout.addView(user_item);
                } else if (mentorAndMePlanDto.getOnly_mentor_done().contains(mentor_signin.get(i).getTask_name())) {
                    View nullView = View.inflate(mContext, R.layout.user_sign_null, null);
                    mHolder.userSigninLayout.addView(nullView);
                }
            }
            for (int i = 0; i < user_signin.size(); i++) {
                if (mentorAndMePlanDto.getOnly_user_done().contains(user_signin.get(i).getTask_name())) {
                    View user_item = View.inflate(mContext, R.layout.plan_user_signin_item, null);
                    UserSignItemHolder userSignItemHolder = new UserSignItemHolder(user_item);
                    user_item.setTag(user_signin.get(i));
                    userSignItemHolder.userSigninName.setText(user_signin.get(i).getTask_name());
                    userSignItemHolder.userSigninDesc.setText(user_signin.get(i).getDescription());
                    userSignItemHolder.userSigninDoneLayout.setVisibility(View.VISIBLE);
                    userSignItemHolder.userSigninEdit.setVisibility(View.GONE);
                    if(!TextUtils.isEmpty(user_signin.get(i).getImg())) {
                        userSignItemHolder.userSigninImg.setVisibility(View.VISIBLE);
                        QiniuHelper.bindAvatarImage(user_signin.get(i).getImg(), userSignItemHolder.userSigninImg);
                    }
                    user_item.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Signin signin = (Signin) view.getTag();
                            SigninAndPostDetailFragment.start(mContext, signin);
                        }
                    });
                    mHolder.userSigninLayout.addView(user_item);
                    View nullView = View.inflate(mContext, R.layout.mentor_sign_null, null);
                    mHolder.mentorSigninLayout.addView(nullView);
                }
            }
            if(mentorAndMePlanDto.isMentor_is_rest() && mHolder.mentorSigninLayout.getChildCount() > 1){
                mHolder.mentorSigninLayout.removeViewAt(1);
            }

            if(mentorAndMePlanDto.isUser_is_rest() && mHolder.userSigninLayout.getChildCount() > 1){
                mHolder.userSigninLayout.removeViewAt(1);
            }
        }


    }

    //根据打卡名字从打卡历史中获取Signin
    private Signin getSiginUseTaskName(List<Signin> list, String taskName) {
        Signin signin = null;
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getTask_name().equals(taskName))
                signin = list.get(i);
        }
        return signin;
    }

    static class PlanItemHolder extends BaseViewHolder {
        @InjectView(R.id.long_click_delete_customer_task_img)
        ImageView longClickDeleteCustomerTaskImg;
        @InjectView(R.id.long_click_delete_customer_task_layout)
        LinearLayout longClickDeleteCustomerTaskLayout;
        @InjectView(R.id.follow_mentor_tip_nfo)
        TextView followMentorTipNfo;
        @InjectView(R.id.day_number)
        TextView dayNumber;
        @InjectView(R.id.plan_mentor_icon)
        RectImageView planMentorIcon;
        @InjectView(R.id.mentor_plan_layout)
        RelativeLayout mentorPlanLayout;
        @InjectView(R.id.plan_user_icon)
        RectImageView planUserIcon;
        @InjectView(R.id.plan_user_title)
        TextView planUserTitle;
        @InjectView(R.id.mentor_user_layout)
        LinearLayout mentorUserLayout;
        @InjectView(R.id.mentor_signin_layout)
        LinearLayout mentorSigninLayout;
        @InjectView(R.id.user_signin_layout)
        LinearLayout userSigninLayout;
        @InjectView(R.id.mentor_user_sign)
        LinearLayout mentorUserSign;
        @InjectView(R.id.today_sigin_layout)
        LinearLayout todaySiginLayout;
        @InjectView(R.id.add_task_btn)
        RelativeLayout addTaskBtn;
        @InjectView(R.id.self_plan_user)
        LinearLayout selfPlanUser;

        public PlanItemHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }


    static class SelfPlanUserHolder {
        @InjectView(R.id.user_signin_name)
        TextView userSigninName;
        @InjectView(R.id.user_signin_undone_layout)
        LinearLayout userSigninUndoneLayout;
        @InjectView(R.id.user_signin_done_layout)
        LinearLayout userSigninDoneLayout;
        @InjectView(R.id.user_signin_desc)
        TextView userSigninDesc;
        @InjectView(R.id.user_signin_img)
        ImageView userSigninImg;
        @InjectView(R.id.user_signin_edit)
        ImageView userSigninEdit;
        @InjectView(R.id.user_signin_right_bottm)
        FrameLayout userSigninRightBottm;
        @InjectView(R.id.user_signin_layout)
        LinearLayout userSigninLayout;
        @InjectView(R.id.user_rest)
        TextView userRest;
        @InjectView(R.id.user_rest_layout)
        LinearLayout userRestLayout;

        public SelfPlanUserHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class UserSignItemHolder {
        @InjectView(R.id.user_signin_name)
        TextView userSigninName;
        @InjectView(R.id.user_signin_undone_layout)
        LinearLayout userSigninUndoneLayout;
        @InjectView(R.id.user_signin_done_layout)
        LinearLayout userSigninDoneLayout;
        @InjectView(R.id.user_signin_desc)
        TextView userSigninDesc;
        @InjectView(R.id.user_signin_img)
        ImageView userSigninImg;
        @InjectView(R.id.user_signin_edit)
        ImageView userSigninEdit;
        @InjectView(R.id.user_signin_right_bottm)
        FrameLayout userSigninRightBottm;
        @InjectView(R.id.user_signin_layout)
        LinearLayout userSigninLayout;
        @InjectView(R.id.user_rest)
        TextView userRest;
        @InjectView(R.id.user_rest_layout)
        LinearLayout userRestLayout;
        @InjectView(R.id.video_course_layout)
        LinearLayout videoCourseLayout;

        public UserSignItemHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

    static class MentorSignItemHolder {
        @InjectView(R.id.mentor_signin_name)
        TextView mentorSigninName;
        @InjectView(R.id.mentor_signin_time)
        TextView mentorSigninTime;
        @InjectView(R.id.mentor_signin_desc)
        TextView mentorSigninDesc;
        @InjectView(R.id.mentor_signin_img)
        ImageView mentorSigninImg;
        @InjectView(R.id.mentor_signin_layout)
        LinearLayout mentorSigninLayout;
        @InjectView(R.id.mentor_rest)
        TextView mentorRest;
        @InjectView(R.id.mentor_rest_time)
        TextView mentorRestTime;
        @InjectView(R.id.mentor_rest_layout)
        LinearLayout mentorRestLayout;
        @InjectView(R.id.video_course_layout)
        LinearLayout videoCourseLayout;

        public MentorSignItemHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }
}

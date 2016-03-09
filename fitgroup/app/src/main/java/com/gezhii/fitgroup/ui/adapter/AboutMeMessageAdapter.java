package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.AboutMeMessage;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.fragment.group.leader.GroupMemberApplicationListFragment;
import com.gezhii.fitgroup.ui.fragment.me.MyFollowerFragment;
import com.gezhii.fitgroup.ui.fragment.me.MyMenteesFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SigninAndPostDetailFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;

import butterknife.InjectView;

/**
 * Created by zj on 16/2/22.
 */
public class AboutMeMessageAdapter extends LoadMoreListViewAdapter {

    private Activity mContext;

    public AboutMeMessageAdapter(Activity activity) {
        this.mContext = activity;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mContext, R.layout.about_me_message_item, null);
        return new AboutMeMessageHolder(rootView, getOnListItemClickListener());
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        AboutMeMessageHolder mHolder = (AboutMeMessageHolder) holder;
        final AboutMeMessage aboutMeMessage = (AboutMeMessage) total_data_list.get(position);
        mHolder.senderUserName.setText(aboutMeMessage.getSender_nick_name());
        mHolder.sendTime.setText(aboutMeMessage.getTime());
        mHolder.sendContent.setText(aboutMeMessage.getContent());
        QiniuHelper.bindAvatarImage(aboutMeMessage.getSender_icon(), mHolder.senderUserIcon);
        if(aboutMeMessage.getSender_user_vip() == 1){
            mHolder.senderVipIcon.setVisibility(View.VISIBLE);
        }else{
            mHolder.senderVipIcon.setVisibility(View.GONE);
        }
        mHolder.aboutMessageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (aboutMeMessage.getType()) {
                    case 2://公会申请
                        GroupMemberApplicationListFragment.start(mContext);
                        break;
                    case 3://userprofile
                        UserProfileFragment.start(mContext, Integer.valueOf(aboutMeMessage.getSend_user_id()));
                        break;
                    case 4://打卡详情
                        SigninAndPostDetailFragment.start(mContext, aboutMeMessage.getSignin_id());
                        break;
                    case 5://followedList
                        MyMenteesFragment.start(mContext);
                        break;
                }
            }
        });
    }

    static class AboutMeMessageHolder extends BaseViewHolder {
        @InjectView(R.id.sender_user_icon)
        RectImageView senderUserIcon;
        @InjectView(R.id.sender_vip_icon)
        RectImageView senderVipIcon;
        @InjectView(R.id.sender_user_name)
        TextView senderUserName;
        @InjectView(R.id.send_content)
        TextView sendContent;
        @InjectView(R.id.send_time)
        TextView sendTime;
        @InjectView(R.id.about_message_layout)
        RelativeLayout aboutMessageLayout;

        public AboutMeMessageHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }
}

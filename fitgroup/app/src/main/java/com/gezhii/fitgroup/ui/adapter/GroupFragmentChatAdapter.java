package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.model.UserCacheModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.UmengEvents;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.fragment.group.GroupChatFragment;
import com.gezhii.fitgroup.ui.view.RectImageView;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by xianrui on 15/11/5.
 */
public class GroupFragmentChatAdapter extends BaseListAdapter {


    List<EMMessage> mEmMessageList;
    Activity mContext;

    public GroupFragmentChatAdapter(Activity mContext, List<EMMessage> mEmMessageList) {
        this.mContext = mContext;
        this.mEmMessageList = mEmMessageList;
    }

    public void setEmMessageList(List<EMMessage> mEmMessageList) {
        this.mEmMessageList = mEmMessageList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_fragment_chat_item, null);
        ViewHolder viewHolder = new ViewHolder(itemView, getOnListItemClickListener());
        itemView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), parent.getContext().getResources().getDimensionPixelOffset(R.dimen.button_height_taller)));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        EMMessage emMessage = mEmMessageList.get(position);
        viewHolder.groupChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, "group", UmengEvents.getEventMap("click", "消息列表"));
                GroupChatFragment.start(mContext);
            }
        });
        int type = emMessage.getIntAttribute("type", -1);
        //String icon = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).icon;

        if (UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()) != null) {
            if (!TextUtils.isEmpty(UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).icon)) {
                QiniuHelper.bindAvatarImage(UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).icon, viewHolder.userIconImg);
            }
            String nickName = UserCacheModel.getInstance().getUserInfo(emMessage.getFrom()).nickName;
            if (!TextUtils.isEmpty(nickName)) {
                viewHolder.userNameText.setText(nickName);
            }
        }
        if (type == 10) {
            viewHolder.userContentText.setText("[打卡]");
        } else if (type == 12) {
            viewHolder.userContentText.setText("[公告]");
        } else if (type == 11) {
            viewHolder.userContentText.setText("[赞]");
        } else if (type == 13) {
            QiniuHelper.bindLocalImage(R.mipmap.head_q, viewHolder.userIconImg);
            viewHolder.userNameText.setText("小Q");
            viewHolder.userContentText.setText(((TextMessageBody) emMessage.getBody()).getMessage());
        } else if (type == 20) {
            viewHolder.userContentText.setText("[日报]");
        } else if (type == 23) {
            viewHolder.userContentText.setText("[今日精选]");
        } else {
            if (emMessage.getType() == EMMessage.Type.TXT) {
                viewHolder.userContentText.setText(((TextMessageBody) emMessage.getBody()).getMessage());
            } else if (emMessage.getType() == EMMessage.Type.IMAGE) {
                viewHolder.userContentText.setText("[图片]");
            }
        }
    }

    @Override
    public int getItemCount() {
        return mEmMessageList.size();
    }


    static class ViewHolder extends BaseViewHolder {
        @InjectView(R.id.user_icon_img)
        RectImageView userIconImg;
        @InjectView(R.id.user_name_text)
        TextView userNameText;
        @InjectView(R.id.user_content_text)
        TextView userContentText;
        @InjectView(R.id.group_chat_layout)
        FrameLayout groupChatLayout;

        public ViewHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }
}

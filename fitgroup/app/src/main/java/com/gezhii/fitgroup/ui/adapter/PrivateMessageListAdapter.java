package com.gezhii.fitgroup.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.PrivateMessageDto;
import com.gezhii.fitgroup.model.UserCacheModel;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;

import butterknife.InjectView;

/**
 * Created by xianrui on 15/8/24.
 */
public class PrivateMessageListAdapter extends BaseListAdapter<PrivateMessageListAdapter.ViewHolder> {


    Context mContext;
    PrivateMessageDto mMessageDto;

    public PrivateMessageListAdapter(Context mContext, PrivateMessageDto messageDto) {
        this.mContext = mContext;
        this.mMessageDto = messageDto;
    }

    public PrivateMessageDto getmMessageDto() {
        return mMessageDto;
    }

    public void setmMessageDto(PrivateMessageDto mMessageDto) {
        this.mMessageDto = mMessageDto;
    }

    @Override
    public PrivateMessageListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.private_message_fragment_item, null);
        ViewHolder viewHolder = new ViewHolder(rootView, getOnListItemClickListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        PrivateMessageDto.MESSAGE message = mMessageDto.getMessageList().get(position);

        UserCacheModel.UserCacheInfo userCacheInfo = UserCacheModel.getInstance().getUserInfo(message.getHuanxin_id());
        if (userCacheInfo != null) {
            viewHolder.messageTitle.setText(UserCacheModel.getInstance().getUserInfo(message.getHuanxin_id()).nickName);
            viewHolder.messageText.setText(message.getLast_message());
            if (message.getUnread_message_count() == 0) {
                viewHolder.unreadCount.setVisibility(View.INVISIBLE);
            } else {
                viewHolder.unreadCount.setVisibility(View.VISIBLE);
                viewHolder.unreadCount.setText(String.valueOf(message.getUnread_message_count()));
            }
            QiniuHelper.bindAvatarImage(UserCacheModel.getInstance().getUserInfo(message.getHuanxin_id()).icon, viewHolder.icon);
        }


    }


    @Override
    public int getItemCount() {
        return mMessageDto.getMessageList().size();
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'message_fragment_item_ui.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder extends BaseViewHolder {
        @InjectView(R.id.icon)
        ImageView icon;
        @InjectView(R.id.unread_count)
        TextView unreadCount;
        @InjectView(R.id.icon_layout)
        FrameLayout iconLayout;
        @InjectView(R.id.message_title)
        TextView messageTitle;
        @InjectView(R.id.message_text)
        TextView messageText;
        @InjectView(R.id.message_layout)
        LinearLayout messageLayout;

        public ViewHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }
}

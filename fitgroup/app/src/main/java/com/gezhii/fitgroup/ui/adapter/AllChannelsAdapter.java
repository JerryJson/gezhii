package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.ChannelsDTO;
import com.gezhii.fitgroup.dto.basic.Tag;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.fragment.follow.ChannelProfileFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;

import butterknife.InjectView;

/**
 * Created by fantasy on 16/2/17.
 */
public class AllChannelsAdapter extends LoadMoreListViewAdapter {
    Activity mContext;
    ChannelsDTO channelsDTO;


    public AllChannelsAdapter(Activity mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.channel_list_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder itemViewHolder = new ChannelViewHolder(rootView, getOnListItemClickListener());
        return itemViewHolder;
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final Tag tag = (Tag) getTotal_data_list().get(position);
        ChannelViewHolder channelViewHolder = (ChannelViewHolder) holder;
        channelViewHolder.channelNameText.setText(tag.getName());
        channelViewHolder.channelContentCountText.setText("已产生" + tag.getContent_count() + "条内容");
        QiniuHelper.bindChannelImage(tag.getImg(), channelViewHolder.channelImg);
        channelViewHolder.channelLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChannelProfileFragment.start(mContext, tag, tag.getId());
            }
        });
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'channel_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ChannelViewHolder extends BaseViewHolder {
        @InjectView(R.id.channel_img)
        ImageView channelImg;
        @InjectView(R.id.channel_name_text)
        TextView channelNameText;
        @InjectView(R.id.channel_content_count_text)
        TextView channelContentCountText;
        @InjectView(R.id.channel_layout)
        LinearLayout channelLayout;

        ChannelViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
        }
    }
}

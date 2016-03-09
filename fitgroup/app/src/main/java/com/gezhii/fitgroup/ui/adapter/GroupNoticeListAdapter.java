package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.GroupNoticeListDTO;
import com.gezhii.fitgroup.dto.basic.GroupNotice;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by ycl on 15/10/24.
 */
public class GroupNoticeListAdapter extends LoadMoreListViewAdapter{


    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_notice_list_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder itemViewHolder = new GroupNoticeListViewHolder(rootView, getOnListItemClickListener());
        return itemViewHolder;
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupNoticeListViewHolder viewHolder = (GroupNoticeListViewHolder)holder;

        GroupNotice notice = (GroupNotice)total_data_list.get(position);
        viewHolder.noticeDateText.setText(TimeHelper.dateFormat1.format(notice.created_time));
        viewHolder.noticeDescriptionText.setText(notice.description);
    }



    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_notice_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class GroupNoticeListViewHolder extends BaseViewHolder{
        @InjectView(R.id.notice_date_text)
        TextView noticeDateText;
        @InjectView(R.id.notice_description_text)
        TextView noticeDescriptionText;

        GroupNoticeListViewHolder(View view, OnListItemClickListener listItemClickListener){
            super(view, listItemClickListener);
        }
    }
}

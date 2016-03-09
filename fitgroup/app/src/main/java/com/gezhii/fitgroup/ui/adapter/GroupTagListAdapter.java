package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.GroupTagConfig;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by fantasy on 15/12/9.
 */
public class GroupTagListAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    List<GroupTagConfig> group_tag_configs;

    public List<GroupTagConfig> getGroup_tag_configs() {
        return group_tag_configs;
    }

    public void setGroup_tag_configs(List<GroupTagConfig> group_tag_configs) {
        this.group_tag_configs = group_tag_configs;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_tag_list_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder itemViewHolder = new GroupTagListViewHolder(rootView, getOnListItemClickListener(), getOnListItemLongClickListener());
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupTagListViewHolder viewHolder = (GroupTagListViewHolder) holder;
        GroupTagConfig groupTagConfig = group_tag_configs.get(position);

        QiniuHelper.bindImage(groupTagConfig.getImg(), viewHolder.tagImage);
        viewHolder.tagNameText.setText(groupTagConfig.getName());
        viewHolder.groupCountText.setText(groupTagConfig.getGroup_count() + "个公会");
    }
    @Override
    public int getItemCount() {
        return group_tag_configs.size();
    }
    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_task_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class GroupTagListViewHolder extends BaseViewHolder {
        @InjectView(R.id.tag_image)
        ImageView tagImage;
        @InjectView(R.id.tag_name_text)
        TextView tagNameText;
        @InjectView(R.id.group_count_text)
        TextView groupCountText;

        GroupTagListViewHolder(View view, OnListItemClickListener onListItemClickListener, OnListItemLongClickListener onListItemLongClickListener) {
            super(view, onListItemClickListener, onListItemLongClickListener);
        }
    }
}

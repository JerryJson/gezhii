package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;

import butterknife.InjectView;

/**
 * Created by fantasy on 15/12/25.
 */
public class GroupListByTagAdapter extends LoadMoreListViewAdapter {
    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_contains_task_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder itemViewHolder = new GroupListByTagViewHolder(rootView, getOnListItemClickListener());
        return itemViewHolder;
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Group squareGroup = (Group) total_data_list.get(position);
        GroupListByTagViewHolder groupListByTagViewHolder = (GroupListByTagViewHolder) holder;
        QiniuHelper.bindAvatarImage(squareGroup.getLeader().getIcon(), groupListByTagViewHolder.leaderIconImg);
        groupListByTagViewHolder.groupNameText.setText(squareGroup.getGroup_name());
        groupListByTagViewHolder.groupIntroduceText.setText(squareGroup.getDescription());
        groupListByTagViewHolder.memberNumText.setText(String.valueOf(squareGroup.getMember_count() + "人"));
        groupListByTagViewHolder.taskListLayout.removeAllViews();
        for (int i = 0; i < squareGroup.group_tasks.size(); i++) {
            LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(groupListByTagViewHolder.itemView.getContext()).inflate(R.layout.task_text_view, null);
            TextView t = (TextView) linearLayout.findViewById(R.id.task_name_text);
            t.setText(squareGroup.group_tasks.get(i).getTask_name());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            groupListByTagViewHolder.taskListLayout.addView(linearLayout, layoutParams);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_list_contains_task_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class GroupListByTagViewHolder extends BaseViewHolder {
        @InjectView(R.id.leader_icon_img)
        ImageView leaderIconImg;
        @InjectView(R.id.group_name_text)
        TextView groupNameText;
        @InjectView(R.id.member_num_text)
        TextView memberNumText;
        @InjectView(R.id.group_introduce_text)
        TextView groupIntroduceText;
        @InjectView(R.id.task_list_layout)
        LinearLayout taskListLayout;

        GroupListByTagViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
        }
    }
}

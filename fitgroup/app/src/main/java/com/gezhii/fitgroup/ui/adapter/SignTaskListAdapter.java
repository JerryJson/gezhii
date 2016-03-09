package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.GroupTask;
import com.gezhii.fitgroup.tools.Screen;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by fantasy on 15/12/10.
 */
public class SignTaskListAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    List<GroupTask> groupTaskList;

    public List<GroupTask> getGroupTaskList() {
        return groupTaskList;
    }

    public void setGroupTaskList(List<GroupTask> groupTaskList) {
        this.groupTaskList = groupTaskList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_task_list_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder itemViewHolder = new GroupTaskListViewHolder(rootView, getOnListItemClickListener(), getOnListItemLongClickListener());
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupTaskListViewHolder viewHolder = (GroupTaskListViewHolder) holder;
        viewHolder.taskName.setText(groupTaskList.get(position).getTask_name());
        if (groupTaskList.get(position).getSignin() == 0) {
            viewHolder.taskFinishImg.setVisibility(View.VISIBLE);
            viewHolder.taskFinishText.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.taskFinishImg.setVisibility(View.INVISIBLE);
            viewHolder.taskFinishText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return groupTaskList.size();
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_task_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class GroupTaskListViewHolder extends BaseViewHolder {
        @InjectView(R.id.task_name)
        TextView taskName;
        @InjectView(R.id.task_finish_img)
        ImageView taskFinishImg;
        @InjectView(R.id.task_finish_text)
        TextView taskFinishText;

        GroupTaskListViewHolder(View view, OnListItemClickListener onListItemClickListener, OnListItemLongClickListener onListItemLongClickListener) {
            super(view, onListItemClickListener, onListItemLongClickListener);
        }
    }
}

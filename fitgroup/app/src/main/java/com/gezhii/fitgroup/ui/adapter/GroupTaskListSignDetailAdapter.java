package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.GroupTask;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.Screen;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by fantasy on 15/12/9.
 */
public class GroupTaskListSignDetailAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    List<GroupTask> groupTaskList;

    public List<GroupTask> getGroupTaskList() {
        return groupTaskList;
    }

    public void setGroupTaskList(List<GroupTask> groupTaskList) {
        this.groupTaskList = groupTaskList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_task_list_sign_statistic_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder itemViewHolder = new GroupTaskListViewHolder(rootView, getOnListItemClickListener(), getOnListItemLongClickListener());
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupTaskListViewHolder viewHolder = (GroupTaskListViewHolder) holder;
        GroupTask groupTask = groupTaskList.get(position);

        viewHolder.taskName.setText(groupTask.getTask_name());
        viewHolder.taskCompleteText.setText("今日已有" + groupTask.getCount() + "人完成");

        if(UserModel.getInstance().getMyGroup().getMember_count() > 0)
        {
            int screenWidth = Screen.getScreenWidth();
            ViewGroup.LayoutParams params = viewHolder.progressBkView.getLayoutParams();
            params.width = (screenWidth - 12 - 12) * (groupTask.getCount())/(UserModel.getInstance().getMyGroup().getMember_count());
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
        @InjectView(R.id.progress_bk_view)
        View progressBkView;
        @InjectView(R.id.task_name)
        TextView taskName;
        @InjectView(R.id.task_complete_text)
        TextView taskCompleteText;

        GroupTaskListViewHolder(View view, OnListItemClickListener onListItemClickListener, OnListItemLongClickListener onListItemLongClickListener) {
            super(view, onListItemClickListener, onListItemLongClickListener);
        }
    }

}

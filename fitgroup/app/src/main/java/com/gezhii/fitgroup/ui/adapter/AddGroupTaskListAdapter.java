package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.db.DBTaskCategory;
import com.gezhii.fitgroup.tools.Screen;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by fantasy on 15/12/8.
 */
public class AddGroupTaskListAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    List<DBTaskCategory> mDBTaskCategoryList;

    public List<DBTaskCategory> getmDBTaskCategoryList() {
        return mDBTaskCategoryList;
    }

    public void setmDBTaskCategoryList(List<DBTaskCategory> mDBTaskCategoryList) {
        this.mDBTaskCategoryList = mDBTaskCategoryList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_task_list_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), parent.getContext().getResources().getDimensionPixelOffset(R.dimen.button_height_normal)));
        RecyclerView.ViewHolder viewHolder = new ViewHolder(rootView, getOnListItemClickListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.itemText.setText(mDBTaskCategoryList.get(position).name);
    }


    @Override
    public int getItemCount() {
        return mDBTaskCategoryList.size();
    }


    static class ViewHolder extends BaseViewHolder {
        @InjectView(R.id.item_text)
        TextView itemText;
        @InjectView(R.id.arrow)
        ImageView arrow;

        public ViewHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }
}

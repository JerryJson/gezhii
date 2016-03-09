package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.db.DBTask;
import com.gezhii.fitgroup.tools.Screen;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by fantasy on 15/12/9.
 */
public class SelectTaskAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    List<DBTask> mDBTaskList;

    public List<DBTask> getmDBTaskList() {
        return mDBTaskList;
    }

    public void setmDBTaskList(List<DBTask> mDBTaskList) {
        this.mDBTaskList = mDBTaskList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView;
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_sport_list_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), parent.getContext().getResources().getDimensionPixelOffset(R.dimen.button_height_normal)));
        RecyclerView.ViewHolder viewHolder = new ViewHolder(rootView, getOnListItemClickListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.itemText.setText(mDBTaskList.get(position).name);
//        viewHolder.arrow.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return mDBTaskList.size();
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

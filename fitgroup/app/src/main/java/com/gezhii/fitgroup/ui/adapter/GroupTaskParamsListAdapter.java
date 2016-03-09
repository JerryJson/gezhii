package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.db.DBTask;
import com.gezhii.fitgroup.tools.Screen;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by fantasy on 15/12/9.
 */
public class GroupTaskParamsListAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

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
        rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_task_params_list_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), parent.getContext().getResources().getDimensionPixelOffset(R.dimen.item_height)));
        RecyclerView.ViewHolder viewHolder = new ViewHolder(rootView, getOnListItemClickListener());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Log.i("darren", mDBTaskList.get(position).name + "   " + mDBTaskList.get(position).parameters);
        viewHolder.itemText.setText(mDBTaskList.get(position).name);
        if ("step".equals(mDBTaskList.get(position).parameters)) {
            viewHolder.signStepFlagLayout.setVisibility(View.VISIBLE);
        } else if ("body_weight".equals(mDBTaskList.get(position).parameters)) {
            viewHolder.signWeightFlagLayout.setVisibility(View.VISIBLE);
        } else if ("image".equals(mDBTaskList.get(position).parameters)) {
            viewHolder.signImgFlagLayout.setVisibility(View.VISIBLE);
        } else {
            viewHolder.signOnceFlagLayout.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return mDBTaskList.size();
    }


    static class ViewHolder extends BaseViewHolder {
        @InjectView(R.id.item_text)
        TextView itemText;
        @InjectView(R.id.sign_step_flag_layout)
        LinearLayout signStepFlagLayout;
        @InjectView(R.id.sign_img_flag_layout)
        LinearLayout signImgFlagLayout;
        @InjectView(R.id.sign_once_flag_layout)
        LinearLayout signOnceFlagLayout;
        @InjectView(R.id.sign_weight_flag_layout)
        LinearLayout signWeightFlagLayout;
        @InjectView(R.id.line_view)
        View lineView;
        @InjectView(R.id.task_layout)
        RelativeLayout taskLayout;

        public ViewHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }
}

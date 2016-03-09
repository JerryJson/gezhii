package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.db.DBSportCategory;
import com.gezhii.fitgroup.tools.Screen;

import java.util.List;

import butterknife.InjectView;

/**
 * Created by xianrui on 15/10/30.
 */
public class SelectSportCategoryAdapter extends BaseListAdapter<RecyclerView.ViewHolder> {

    List<DBSportCategory> mSportCategoryList;


    public List<DBSportCategory> getmSportCategoryList() {
        return mSportCategoryList;
    }

    public void setSportCategoryList(List<DBSportCategory> mSportCategoryList) {
        this.mSportCategoryList = mSportCategoryList;
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
        viewHolder.itemText.setText(mSportCategoryList.get(position).name);
    }

    @Override
    public int getItemCount() {
        return mSportCategoryList.size();
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

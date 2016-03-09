package com.gezhii.fitgroup.ui.view;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.ui.adapter.BaseListAdapter;

import java.util.ArrayList;
import java.util.List;

public abstract class LoadMoreListViewAdapter extends BaseListAdapter {
    private final int TYPE_LOAD_MORE_ITEM = 999;
    boolean isHasMore;

    public void setIsHasMore(boolean isHasMore) {
        this.isHasMore = isHasMore;
    }

    protected List<Object> total_data_list;

    //初始化的时候, 在Fragment中调用
    public void init_data_list(List<? extends Object> init_data_list) {
        this.total_data_list = new ArrayList<Object>();
        for (int i = 0; i < init_data_list.size(); i++)
            this.total_data_list.add(init_data_list.get(i));
    }

    //被APIAutoInvoker调用
    public void setTotal_data_list(List<Object> total_data_list) {
        this.total_data_list = total_data_list;
    }

    public List<Object> getTotal_data_list() {
        return this.total_data_list;
    }

    @Override
    public int getItemViewType(int position) {
        if (this.total_data_list.size() == position) {
            return TYPE_LOAD_MORE_ITEM;
        } else {
            return subGetItemViewType(position);
        }
    }

    public int subGetItemViewType(int position) {
        return 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_LOAD_MORE_ITEM) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.load_more_foot_view, null);
            itemView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), Screen.dip2px(40)));
            return new FootViewHolder(itemView);
        } else {
            return this.subOnCreateViewHolder(parent, viewType);
        }
    }

    public abstract RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType);

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (getItemViewType(position) != TYPE_LOAD_MORE_ITEM) {
            this.subOnBindViewHolder(holder, position);
        } else {
            FootViewHolder footViewHolder = (FootViewHolder) holder;
            if (isHasMore) {
                footViewHolder.footText.setText(R.string.load_more_ing);
            } else {
//                footViewHolder.footText.setText(R.string.load_more_finish);
                footViewHolder.footText.setVisibility(View.GONE);
            }
        }
    }


    public abstract void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position);


    @Override
    public int getItemCount() {
        return total_data_list.size() + 1;
    }


    class FootViewHolder extends RecyclerView.ViewHolder {
        public TextView footText;

        public FootViewHolder(View itemView) {
            super(itemView);
            footText = (TextView) itemView.findViewById(R.id.foot_text);
        }
    }
}






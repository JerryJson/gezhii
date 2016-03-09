package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;

/**
 * Created by xianrui on 15/8/24.
 */
public abstract class BaseListAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter {
    private OnListItemClickListener mOnListItemClickListener;
    private OnListItemLongClickListener mOnListItemLongClickListener;

    public OnListItemClickListener getOnListItemClickListener() {
        return mOnListItemClickListener;
    }

    public void setOnListItemClickListener(OnListItemClickListener mOnListItemClickListener) {
        this.mOnListItemClickListener = mOnListItemClickListener;
    }

    public OnListItemLongClickListener getOnListItemLongClickListener() {
        return mOnListItemLongClickListener;
    }

    public void setOnListItemLongClickListener(OnListItemLongClickListener mOnListItemLongClickListener) {
        this.mOnListItemLongClickListener = mOnListItemLongClickListener;
    }
}

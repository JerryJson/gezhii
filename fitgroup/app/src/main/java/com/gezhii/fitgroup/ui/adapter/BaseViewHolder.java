package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import butterknife.ButterKnife;

/**
 * Created by xianrui on 15/8/24.
 */
public class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public BaseViewHolder(View itemView, final OnListItemClickListener onListItemClickListener) {
        this(itemView, onListItemClickListener, null);
    }

    public BaseViewHolder(View itemView, final OnListItemClickListener onListItemClickListener, final OnListItemLongClickListener onListItemLongClickListener) {
        this(itemView);
        if (onListItemClickListener != null) {
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onListItemClickListener.onListItemClick(v, getAdapterPosition());
                }
            });
        }
        if (onListItemLongClickListener != null) {
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onListItemLongClickListener.onListItemLongClick(v, getAdapterPosition());
                }
            });
        }
        ButterKnife.inject(this, itemView);

    }

}

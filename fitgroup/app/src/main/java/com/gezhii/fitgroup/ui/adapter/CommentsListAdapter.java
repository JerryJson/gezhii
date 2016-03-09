package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.Comment;
import com.gezhii.fitgroup.event.CommentATailEvent;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;

import butterknife.InjectView;
import de.greenrobot.event.EventBus;

/**
 * Created by zj on 16/2/18.
 */
public class CommentsListAdapter extends LoadMoreListViewAdapter {
    private Activity mContext;

    public CommentsListAdapter(Activity activity) {
        this.mContext = activity;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = View.inflate(mContext, R.layout.allcomments_list_item, null);
        return new CommentsItemHolder(rootView, getOnListItemClickListener());
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Comment comment = (Comment) getTotal_data_list().get(position);
        final CommentsItemHolder mHolder = (CommentsItemHolder) holder;
        mHolder.commentsUserName.setText(comment.getUser().getNick_name());
        mHolder.commentsContent.setText(comment.getComment());
        QiniuHelper.bindAvatarImage(comment.getUser().getIcon(), mHolder.commentsUserIcon);
        if (comment.getUser().getVip() == 0) {
            mHolder.commentsUserVip.setVisibility(View.INVISIBLE);
        } else {
            mHolder.commentsUserVip.setVisibility(View.VISIBLE);
        }
        mHolder.commentsDate.setText(TimeHelper.RecordWeekStartFormat.format(comment.getCreated_time()));
        mHolder.commentItemLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                EventBus.getDefault().post(new CommentATailEvent(comment.getUser().getNick_name()));
                return false;
            }
        });
    }

    static class CommentsItemHolder extends BaseViewHolder {
        @InjectView(R.id.comment_item_layout)
        LinearLayout commentItemLayout;
        @InjectView(R.id.comments_user_icon)
        RectImageView commentsUserIcon;
        @InjectView(R.id.comments_user_name)
        TextView commentsUserName;
        @InjectView(R.id.comments_user_vip)
        RectImageView commentsUserVip;
        @InjectView(R.id.comments_content)
        TextView commentsContent;
        @InjectView(R.id.comments_date)
        TextView commentsDate;

        public CommentsItemHolder(View itemView, OnListItemClickListener onListItemClickListener) {
            super(itemView, onListItemClickListener);
        }
    }
}

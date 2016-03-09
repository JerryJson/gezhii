package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.fragment.follow.UserProfileFragment;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;

import butterknife.InjectView;

/**
 * Created by fantasy on 16/2/15.
 */
public class MyFollowerAdapter extends LoadMoreListViewAdapter {
    Activity mContext;

    public MyFollowerAdapter(Activity activity) {
        this.mContext = activity;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_follower_list_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder itemViewHolder = new MyFollowerViewHolder(rootView, getOnListItemClickListener(), getOnListItemLongClickListener());
        return itemViewHolder;
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MyFollowerViewHolder viewHolder = (MyFollowerViewHolder) holder;
        final User user = (User) total_data_list.get(position);
        QiniuHelper.bindAvatarImage(user.getIcon(), viewHolder.followerIcon);
        if (user.getVip() == 0) {
            viewHolder.userProfileVip.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.userProfileVip.setVisibility(View.VISIBLE);
        }
        viewHolder.followerNameText.setText(user.getNick_name());
        viewHolder.followerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfileFragment.start(mContext, user.getId());
            }
        });
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'my_follower_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class MyFollowerViewHolder extends BaseViewHolder {
        @InjectView(R.id.follower_btn)
        LinearLayout followerBtn;
        @InjectView(R.id.follower_icon)
        RectImageView followerIcon;
        @InjectView(R.id.user_profile_vip)
        RectImageView userProfileVip;
        @InjectView(R.id.follower_name_text)
        TextView followerNameText;

        MyFollowerViewHolder(View view, OnListItemClickListener listItemClickListener, OnListItemLongClickListener listItemLongClickListener) {
            super(view, listItemClickListener, listItemLongClickListener);
        }
    }


}

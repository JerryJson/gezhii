package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.GroupMember;
import com.gezhii.fitgroup.model.UserCacheModel;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;

import butterknife.InjectView;

/**
 * Created by ycl on 15/10/24.
 */
public class GroupMemberListAdapter extends LoadMoreListViewAdapter {


    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_member_list_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder itemViewHolder = new GroupMemberListViewHolder(rootView, getOnListItemClickListener(), getOnListItemLongClickListener());
        return itemViewHolder;
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        GroupMemberListViewHolder viewHolder = (GroupMemberListViewHolder) holder;

        GroupMember groupMember = (GroupMember)total_data_list.get(position);

        UserCacheModel.getInstance().setUserNickName(groupMember.getUser().getHuanxin_id(),groupMember.getUser().getNick_name());
        UserCacheModel.getInstance().setUserIcon(groupMember.getUser().getHuanxin_id(), groupMember.getUser().getIcon());


        QiniuHelper.bindAvatarImage(groupMember.getUser().getIcon(), viewHolder.groupMemberIcon);
        viewHolder.groupMemberNameText.setText(groupMember.getUser().getNick_name());
        viewHolder.groupMemberLevelText.setText("" + groupMember.getUser().getLevel() + "级");
        viewHolder.groupMemberContributionText.setText("" + groupMember.getContribution_value());

        viewHolder.badgesContainer.removeAllViews();
        for(int i = 0; i < groupMember.getUser().getBadges().size(); i++)
        {
            ImageView badgeView = new ImageView(viewHolder.itemView.getContext());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Screen.dip2px(15),Screen.dip2px(15));
            params.setMargins(0, 0, Screen.dip2px(4), 0);
            badgeView.setLayoutParams(params);
            if(groupMember.getUser().getBadges().get(i).badge!=null){
                if(i<=7){
                    QiniuHelper.bindAvatarImage(groupMember.getUser().getBadges().get(i).badge.getIcon(), badgeView);
                    viewHolder.badgesContainer.addView(badgeView);
                }

            }

        }

    }



    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_member_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class GroupMemberListViewHolder extends BaseViewHolder{
        @InjectView(R.id.group_member_icon)
        RectImageView groupMemberIcon;
        @InjectView(R.id.group_member_name_text)
        TextView groupMemberNameText;
        @InjectView(R.id.group_member_level_text)
        TextView groupMemberLevelText;
        @InjectView(R.id.badges_container)
        LinearLayout badgesContainer;
        @InjectView(R.id.group_member_contribution_text)
        TextView groupMemberContributionText;

        GroupMemberListViewHolder(View view, OnListItemClickListener listItemClickListener, OnListItemLongClickListener listItemLongClickListener) {
            super(view, listItemClickListener, listItemLongClickListener);
        }
    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_notice_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */

}

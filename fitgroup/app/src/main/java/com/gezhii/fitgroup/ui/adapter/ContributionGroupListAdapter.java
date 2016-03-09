package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;

import butterknife.InjectView;

/**
 * Created by y on 2015/10/21.
 */
public class ContributionGroupListAdapter extends LoadMoreListViewAdapter {

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_list_item_1, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder itemViewHolder = new ActiveGroupListViewHolder(rootView, getOnListItemClickListener());
        return itemViewHolder;
    }


    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Group squareGroup = (Group)total_data_list.get(position);
        ActiveGroupListViewHolder viewHolder = (ActiveGroupListViewHolder) holder;
        viewHolder.groupNameText.setText(squareGroup.getGroup_name());
        viewHolder.groupLevelText.setText("" + squareGroup.getLevel() + "级");

        QiniuHelper.bindAvatarImage(squareGroup.getLeader().getIcon(), viewHolder.leaderIconImg);

        viewHolder.groupInfoText.setText("会长: " + squareGroup.getLeader().getNick_name() +
                " | " + "贡献值: " + Integer.toString(squareGroup.getTotal_contribution_value()));
        viewHolder.goldImg.setVisibility(View.GONE);
        if(position == 0){
            viewHolder.goldImg.setVisibility(View.VISIBLE);
            viewHolder.goldImg.setImageResource(R.mipmap.golden);
        }
        else if(position == 1)
        {
            viewHolder.goldImg.setVisibility(View.VISIBLE);
            viewHolder.goldImg.setImageResource(R.mipmap.silver);
        }
        else if(position == 2)
        {
            viewHolder.goldImg.setVisibility(View.VISIBLE);
            viewHolder.goldImg.setImageResource(R.mipmap.copper);
        }
    } //绑数据用的，根据数据的不同状态，控制这个ViewHolder的显示方式

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_list_item_1.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ActiveGroupListViewHolder extends BaseViewHolder{
        @InjectView(R.id.group_name_text)
        TextView groupNameText;
        @InjectView(R.id.group_level_text)
        TextView groupLevelText;
        @InjectView(R.id.leader_icon_img)
        RectImageView leaderIconImg;
        @InjectView(R.id.group_info_text)
        TextView groupInfoText;
        @InjectView(R.id.gold_img)
        ImageView goldImg;

        ActiveGroupListViewHolder(View view, OnListItemClickListener listItemClickListener) {
            super(view, listItemClickListener);
        }
    }


    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'group_list_item.xml
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */

}

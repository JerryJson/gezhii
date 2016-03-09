package com.gezhii.fitgroup.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.StarLeaderBanner;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;
import com.gezhii.fitgroup.ui.view.RectImageView;

import butterknife.InjectView;

/**
 * Created by ycl on 15/10/26.
 */
public class StarLeaderListAdapter extends LoadMoreListViewAdapter {

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.start_leader_list_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder itemViewHolder = new StarLeaderListViewHolder(rootView,getOnListItemClickListener());
        return itemViewHolder;
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StarLeaderBanner banner = (StarLeaderBanner)total_data_list.get(position);
        StarLeaderListViewHolder viewHolder = (StarLeaderListViewHolder)holder;
        QiniuHelper.bindImage(banner.img, viewHolder.starLeaderBannerImg);
        int width = Screen.getScreenWidth()-Screen.dip2px(24);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.width=width;
        layoutParams.height= width*468/700;
        layoutParams.setMargins(Screen.dip2px(12), Screen.dip2px(12), Screen.dip2px(12), Screen.dip2px(12));
        viewHolder.starLeaderBannerImg.setLayoutParams(layoutParams);
        viewHolder.starLeaderBannerImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.starLeaderBannerImg.setCornerRadius(Screen.dip2px(5));
    }



    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'start_leader_list_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class StarLeaderListViewHolder extends BaseViewHolder{
        @InjectView(R.id.star_leader_banner_img)
        RectImageView starLeaderBannerImg;

        StarLeaderListViewHolder(View view, OnListItemClickListener listItemClickListener) {
            super(view, listItemClickListener);
        }
    }
}

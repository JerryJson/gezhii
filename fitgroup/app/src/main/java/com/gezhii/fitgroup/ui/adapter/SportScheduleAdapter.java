package com.gezhii.fitgroup.ui.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.SportScheduleDTO;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.ui.view.LoadMoreListViewAdapter;

import butterknife.InjectView;

/**
 * Created by fantasy on 16/2/17.
 */
public class SportScheduleAdapter extends LoadMoreListViewAdapter {

    Activity mContext;

    public SportScheduleAdapter(Activity mContext) {
        this.mContext = mContext;
    }

    @Override
    public RecyclerView.ViewHolder subOnCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sport_schedule_item, null);
        rootView.setLayoutParams(new RecyclerView.LayoutParams(Screen.getScreenWidth(), ViewGroup.LayoutParams.WRAP_CONTENT));
        RecyclerView.ViewHolder viewHolder = new SportScheduleAViewHolder(rootView, getOnListItemClickListener());
        return viewHolder;
    }

    @Override
    public void subOnBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final SportScheduleDTO sportScheduleDTO = (SportScheduleDTO) getTotal_data_list().get(position);

        final SportScheduleAViewHolder viewHolder = (SportScheduleAViewHolder) holder;
        viewHolder.theDayNum.setText("第" + sportScheduleDTO.getThe_day_number() + "天");
        viewHolder.dailySportTaskList.removeAllViews();
        int flag = 0;
        for (int i = 0; i < sportScheduleDTO.getList().size(); i++) {
            TextView tv = new TextView(mContext);
            if (!TextUtils.isEmpty(sportScheduleDTO.getList().get(i).getTask_name())) {
                tv.setText(sportScheduleDTO.getList().get(i).getTask_name());
                viewHolder.dailySportTaskList.addView(tv);
                flag = 1;
            }
        }
        if (sportScheduleDTO.getList().size() == 0 || flag == 0) {
            TextView tv = new TextView(mContext);
            tv.setText("休息日");
            viewHolder.dailySportTaskList.addView(tv);
        }

    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'sport_schedule_item.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class SportScheduleAViewHolder extends BaseViewHolder {
        @InjectView(R.id.gray_circle_img)
        ImageView grayCircleImg;
        @InjectView(R.id.above_line)
        View aboveLine;
        @InjectView(R.id.below_line)
        View belowLine;
        @InjectView(R.id.the_day_num)
        TextView theDayNum;
        @InjectView(R.id.daily_sport_task_list)
        LinearLayout dailySportTaskList;

        SportScheduleAViewHolder(View view, OnListItemClickListener onListItemClickListener) {
            super(view, onListItemClickListener);
        }
    }
}

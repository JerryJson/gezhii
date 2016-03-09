package com.gezhii.fitgroup.ui.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.GroupTagConfig;
import com.gezhii.fitgroup.model.GroupTagsModel;
import com.gezhii.fitgroup.tools.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ycl on 15/10/27.
 */
public class TasksView extends ViewGroup {
    public TasksView(Context context) {
        super(context);
    }

    public TasksView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TasksView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTasks(List<String> tasks) {
        Log.i("ycl", "setTasks");
        this.tasks = tasks;

        for (int i = 0; i < tasks.size(); i++) {
            //TextView t = new TextView(getContext());
            //t.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_24));
            //t.setTextColor(getResources().getColor(R.color.white));
            //t.setBackgroundResource(R.drawable.rounded_rectangle_primary);
            LinearLayout taskContainer = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.task_text_view, null);
            TextView t = (TextView)taskContainer.findViewById(R.id.task_name_text);
            t.setText(tasks.get(i));

            this.addView(taskContainer);
        }

        if (tasks.size() > 0)
            calcMeasure();
    }

    @Override
    public void removeAllViews() {
        tasks.clear();
        tasksRect.clear();
        super.removeAllViews();
    }

    class Rect {
        public int x = 0;
        public int y = 0;
        public int width;
        public int height;
    }

    List<String> tasks = new ArrayList<String>();
    int total_width = Screen.getScreenWidth() - 2 * Screen.dip2px(12) - 2 * Screen.dip2px(14);
    int total_height;
    List<Rect> tasksRect = new ArrayList<Rect>();

    private void calcMeasure() {
        tasksRect.clear();
        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimension(R.dimen.text_size_24));
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontHeight = -fontMetrics.top + fontMetrics.bottom;

        float text_total_width = paint.measureText(tasks.get(0));
        Rect r0 = new Rect();
        r0.x = 0;
        r0.y = 0;
        r0.width = (int) text_total_width + Screen.dip2px(6) + Screen.dip2px(6) + Screen.dip2px(2);
        r0.height = (int) fontHeight + Screen.dip2px(14);
        tasksRect.add(r0);


        int x_offset = (int) Screen.dip2px(20);
        int y_offset = (int) Screen.dip2px(12);

        int remain_width = total_width - (r0.width + x_offset);
        total_height = r0.height;

        for (int i = 1; i < tasks.size(); i++) {

            text_total_width = paint.measureText(tasks.get(i));

            Rect r = new Rect();
            r.width = (int) text_total_width +  Screen.dip2px(6) + Screen.dip2px(6) + Screen.dip2px(2);
            r.height = (int) fontHeight + Screen.dip2px(14);

            if (remain_width >= r.width) {
                Rect pre_r = tasksRect.get(i - 1);

                r.x = pre_r.x + pre_r.width + x_offset;
                r.y = pre_r.y;

                remain_width = remain_width - (r.width + x_offset);
            } else {
                Rect pre_r = tasksRect.get(i - 1);

                r.x = 0;
                r.y = pre_r.y + pre_r.height + y_offset;
                remain_width = total_width - (r.width + x_offset);

                total_height = total_height + r.height + y_offset;
            }

            tasksRect.add(r);
        }

        total_height = total_height + y_offset;
    }

    //LinearLayout自己会负责它内部元素的摆放,你不用管它内部的.你只用管它本身
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        Log.i("ycl", "taskView onMeasure");
        for (int i = 0; i < tasks.size(); i++) {
            LinearLayout taskContainer = (LinearLayout)getChildAt(i);
            TextView t = (TextView)taskContainer.findViewById(R.id.task_name_text);
            Rect r = tasksRect.get(i);

            taskContainer.measure(r.width, r.height);
        }
        Log.i("ycl", "total_height: " + total_height);
        setMeasuredDimension(total_width, total_height);
    }

    @Override
    protected void onLayout(boolean b, int i0, int i1, int i2, int i3) {
        Log.i("ycl", "taskView onLayout");
        for (int i = 0; i < tasks.size(); i++) {
            Rect r = tasksRect.get(i);
            LinearLayout taskContainer = (LinearLayout) getChildAt(i);

            taskContainer.layout(r.x, r.y, r.x + r.width, r.y + r.height);
        }
    }
}

package com.gezhii.fitgroup.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ycl on 15/10/27.
 */
public class DigitalView extends ViewGroup{


    public DigitalView(Context context) {
        super(context);
    }

    public DigitalView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DigitalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setTotalWidth(int total_width)
    {
        this.total_width = total_width;
    }

    public void setData(int total_count, int current_count, int state)
    {
        this.total_count = total_count;
        this.current_count = current_count;
        this.state = state;

        for(int i = 0; i < total_count; i++)
        {
            TextView t = new TextView(getContext());
            t.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_30));
            t.setTextColor(getResources().getColor(R.color.white));
            t.setGravity(Gravity.CENTER);
            t.setPadding(0,Screen.dip2px(2),0,0);

            if(i < current_count) {
                if(state == 0) {
                    t.setTextColor(getResources().getColor(R.color.gray_97));
                    t.setBackgroundResource(R.drawable.oval_white);
                }
                else
                {
                    t.setTextColor(getResources().getColor(R.color.pink_ff));
                    t.setBackgroundResource(R.drawable.oval_white);
                }
            }

            this.addView(t);
        }

        calcMeasure();
    }

    class Rect
    {
        public int x = 0;
        public int y = 0;
        public int width;
        public int height;
    }

    int state = 0; //0, 1 灰色,正常
    int total_count;
    int current_count;

    int total_width;
    int total_height;
    List<Rect> iconsRect = new ArrayList<Rect>();

    private void calcMeasure()
    {
        Rect r0 = new Rect();
        r0.x = 0;
        r0.y = 0;
        r0.width =  Screen.dip2px(25);
        r0.height = Screen.dip2px(25);
        iconsRect.add(r0);

        total_height = r0.height;

        int min_offset = Screen.dip2px(10);
        int max_number_per_row = 7;

        int x_offset = (total_width - max_number_per_row * r0.width)/(max_number_per_row - 1);
        int y_offset = Screen.dip2px(9);

        int number_per_row = 0;

        for(int i = 1; i < total_count ; i++) {

            number_per_row++;

            Rect r = new Rect();
            r.width =  Screen.dip2px(25);
            r.height = Screen.dip2px(25);

            if(number_per_row <= max_number_per_row)
            {
                    Rect pre_r = iconsRect.get(i - 1);

                    r.x = pre_r.x + pre_r.width + x_offset;
                    r.y = pre_r.y;
            }
            else
            {
                number_per_row = 1;

                Rect pre_r = iconsRect.get(i - 1);
                r.x = 0;
                r.y = pre_r.y + pre_r.height + y_offset;

                total_height = total_height + r.height + y_offset;
            }

            iconsRect.add(r);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        for(int i = 0; i < total_count; i++)
        {
            TextView textView = (TextView)getChildAt(i);
            Rect r = iconsRect.get(i);
            textView.measure(r.width, r.height);

        }

        setMeasuredDimension(total_width, total_height);
    }

    @Override
    protected void onLayout(boolean b, int i0, int i1, int i2, int i3) {
        for(int i = 0; i < total_count; i++)
        {
            Rect r = iconsRect.get(i);
            TextView textView = (TextView)getChildAt(i);
            textView.setText("" + (i + 1));
            textView.layout(r.x, r.y, r.x + r.width, r.y + r.height);
        }

    }
}

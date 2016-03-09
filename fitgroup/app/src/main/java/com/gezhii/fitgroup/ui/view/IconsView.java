package com.gezhii.fitgroup.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ycl on 15/10/27.
 */
public class IconsView extends ViewGroup{


    public IconsView(Context context) {
        super(context);
    }

    public IconsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public IconsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setTotalWidth(int total_width)
    {
        this.total_width = total_width;
    }

    public void setIcons(List<String> icons)
    {
        this.icons = icons;

        for(int i = 0; i < icons.size(); i++)
        {
            ImageView imageView = (ImageView)LayoutInflater.from(getContext()).inflate(R.layout.oval_image_view,null);
            QiniuHelper.bindAvatarImage(icons.get(i), imageView);
            this.addView(imageView);
        }

        if(icons.size() > 0)
            calcMeasure();
    }

    class Rect
    {
        public int x = 0;
        public int y = 0;
        public int width;
        public int height;
    }

    List<String> icons = new ArrayList<String>();
    int total_width;
    int total_height;
    List<Rect> iconsRect = new ArrayList<Rect>();

    private void calcMeasure()
    {
        Rect r0 = new Rect();
        r0.x = 0;
        r0.y = 0;
        r0.width =  Screen.dip2px(30);
        r0.height = Screen.dip2px(30);
        iconsRect.add(r0);

        total_height = r0.height;

        int min_offset = Screen.dip2px(10);
        int max_number_per_row = total_width/(r0.width + min_offset);

        int x_offset = (total_width - max_number_per_row * r0.width)/(max_number_per_row - 1);
        int y_offset = Screen.dip2px(9);

        int number_per_row = 0;

        for(int i = 1; i < icons.size() ; i++) {

            number_per_row++;

            Rect r = new Rect();
            r.width =  Screen.dip2px(30);
            r.height = Screen.dip2px(30);

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

        for(int i = 0; i < icons.size(); i++)
        {
            ImageView imageView = (ImageView)getChildAt(i);
            Rect r = iconsRect.get(i);
            imageView.measure(r.width, r.height);

        }

        setMeasuredDimension(total_width, total_height);
    }

    @Override
    protected void onLayout(boolean b, int i0, int i1, int i2, int i3) {
        for(int i = 0; i < icons.size(); i++)
        {
            Rect r = iconsRect.get(i);
            ImageView imageView = (ImageView)getChildAt(i);
            imageView.layout(r.x, r.y, r.x + r.width, r.y + r.height);
        }

    }
}

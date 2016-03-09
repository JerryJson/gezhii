package com.gezhii.fitgroup.ui.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class TagsView extends ViewGroup {


    public TagsView(Context context) {
        super(context);
    }

    public TagsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TagsView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    public void setTags(List<String> tags) {
        this.tags = tags;

        for (int i = 0; i < tags.size(); i++) {
            //TextView t = new TextView(getContext());
            //t.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_24));
            //t.setTextColor(getResources().getColor(R.color.white));
            //t.setBackgroundResource(R.drawable.rounded_rectangle_primary);
            TextView t = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.tag_text_view, null);
            this.addView(t);
        }

        if (tags.size() > 0)
            calcMeasure();
    }

    public void addTags(GroupTagsModel.GroupTags groupTags) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        tags.add(groupTags.name);
        TextView t = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.tag_text_view, null);
        if (groupTags.isSelect) {
            t.setSelected(true);
            t.setBackgroundResource(R.drawable.rounded_rectangle_primary);
        } else {
            t.setSelected(false);
            t.setBackgroundResource(R.drawable.rounded_rectangle_gray_c8);
        }
        t.setOnClickListener(groupTags.onClickListener);
        this.addView(t);
        calcMeasure();
    }

    public void addTags(final List<GroupTagConfig> groupTagConfigs) {
        if (tags == null) {
            tags = new ArrayList<>();
        }
        for (int i = 0; i < groupTagConfigs.size(); i++) {
            final GroupTagConfig groupTagConfig = groupTagConfigs.get(i);
            tags.add(groupTagConfig.getName());
            TextView t = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.tag_text_view, null);
            if (groupTagConfig.getFlag() == 1) {
                t.setSelected(true);
                t.setBackgroundResource(R.drawable.rounded_rectangle_primary);
            } else {
                t.setSelected(false);
                t.setBackgroundResource(R.drawable.rounded_rectangle_gray_c8);
            }
            t.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    int k = 0;
                    for (int j = 0; j < groupTagConfigs.size(); j++) {
                        if (groupTagConfigs.get(j).getFlag() == 1) {
                            k++;
                        }
                    }
                    if (k < 3) {
                        if (v.isSelected()) {
                            groupTagConfig.setFlag(0);
                            v.setSelected(false);
                            v.setBackgroundResource(R.drawable.rounded_rectangle_gray_c8);
                        } else {
                            groupTagConfig.setFlag(1);
                            v.setSelected(true);
                            v.setBackgroundResource(R.drawable.rounded_rectangle_primary);
                        }
                    } else {
                        if (v.isSelected()) {
                            groupTagConfig.setFlag(0);
                            v.setSelected(false);
                            v.setBackgroundResource(R.drawable.rounded_rectangle_gray_c8);
                        } else {
                            groupTagConfig.setFlag(0);
                            v.setSelected(false);
                            v.setBackgroundResource(R.drawable.rounded_rectangle_gray_c8);
                        }
                    }
                }
            });
            this.addView(t);
            calcMeasure();
        }


    }

    @Override
    public void removeAllViews() {
        tags.clear();
        tagsRect.clear();
        super.removeAllViews();
    }

    class Rect {
        public int x = 0;
        public int y = 0;
        public int width;
        public int height;
    }

    List<String> tags = new ArrayList<String>();
    int total_width = Screen.getScreenWidth() - 2 * Screen.dip2px(12) - 2 * Screen.dip2px(14);
    int total_height;
    List<Rect> tagsRect = new ArrayList<Rect>();

    private void calcMeasure() {
        tagsRect.clear();
        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimension(R.dimen.text_size_24));
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        float fontHeight = -fontMetrics.top + fontMetrics.bottom;

        float text_total_width = paint.measureText(tags.get(0));
        Rect r0 = new Rect();
        r0.x = 0;
        r0.y = 0;
        r0.width = (int) text_total_width + Screen.dip2px(19);
        r0.height = (int) fontHeight + Screen.dip2px(14);
        tagsRect.add(r0);


        int remain_width = total_width - r0.width;
        int x_offset = (int) Screen.dip2px(20);
        int y_offset = (int) Screen.dip2px(12);
        total_height = r0.height;

        for (int i = 1; i < tags.size(); i++) {

            text_total_width = paint.measureText(tags.get(i));

            Rect r = new Rect();
            r.width = (int) text_total_width + Screen.dip2px(19);
            r.height = (int) fontHeight + Screen.dip2px(14);

            if (remain_width >= r.width) {
                Rect pre_r = tagsRect.get(i - 1);

                r.x = pre_r.x + pre_r.width + x_offset;
                r.y = pre_r.y;

                remain_width = remain_width - (r.width + x_offset);
            } else {
                Rect pre_r = tagsRect.get(i - 1);

                r.x = 0;
                r.y = pre_r.y + pre_r.height + y_offset;
                remain_width = total_width - (r.width + x_offset);

                total_height = total_height + r.height + y_offset;
            }

            tagsRect.add(r);
        }

        total_height = total_height + y_offset;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        for (int i = 0; i < tags.size(); i++) {
            TextView t = (TextView) getChildAt(i);
            Rect r = tagsRect.get(i);
            t.measure(r.width, r.height);
        }
        Log.i("ycl", "total_height: " + total_height);
        setMeasuredDimension(total_width, total_height);
    }

    @Override
    protected void onLayout(boolean b, int i0, int i1, int i2, int i3) {
        for (int i = 0; i < tags.size(); i++) {
            Rect r = tagsRect.get(i);
            TextView t = (TextView) getChildAt(i);
            t.setText(tags.get(i));
            t.layout(r.x, r.y, r.x + r.width, r.y + r.height);
            //t.setGravity(Gravity.CENTER);
        }
    }
}

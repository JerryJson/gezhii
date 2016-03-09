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
import com.gezhii.fitgroup.tools.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ycl on 15/10/27.
 */
public class MeasureText extends TextView {


    public MeasureText(Context context) {
        super(context);
    }

    public int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    public int calcOneLine(String text, int textSize, int width)
    {
        Paint paint = new Paint();
        paint.setTextSize(getResources().getDimension(textSize));
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();

        float oneCharWidth =  getResources().getDimension(textSize);
        int charCount = width/((int)oneCharWidth);

        Log.i("ycl", "charCount = " + charCount);
        int low = charCount - 10;
        int high = charCount + 10;
        int pos;

        while(low < high)
        {
            pos = (low + high)/2;
            if(pos == low || pos == high) break;
            if(pos >= text.length()) break;
            String subText = text.substring(0, pos);
            float calcWidth = paint.measureText(subText);

            Log.i("ycl", "pos = " + pos);
            Log.i("ycl", "calcWidth = " + calcWidth);
            Log.i("ycl", "width = " + width);
            Log.i("ycl", "oneCharWidth = " + oneCharWidth);

            if(calcWidth <= width && calcWidth > width - oneCharWidth)
            {
                return pos+1;
            }
            else if(calcWidth > width)
            {
                high = pos;
            }
            else if(calcWidth <= width - oneCharWidth)
            {
                low = pos;
            }

            Log.i("ycl", "low = " + low);
            Log.i("ycl", "high = " + high);
        }
        Log.i("ycl", "calc one line error!");
        return -1;
    }

    public int getTotalLines(String text, int textSize, int width)
    {
        int lines = 0;
        String subText = text;

        while(subText != null && subText.length() > 0)
        {
            int row1 = calcOneLine(subText, textSize, width);
            lines++;
            if(row1 == -1)
                break;

            subText = subText.substring(row1, subText.length() - 1);
        }
        return lines;
    }

}

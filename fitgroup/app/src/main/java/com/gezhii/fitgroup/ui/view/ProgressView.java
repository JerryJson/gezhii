package com.gezhii.fitgroup.ui.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.gezhii.fitgroup.R;
import com.xianrui.lite_common.litesuits.android.log.Log;

/**
 * Created by ycl on 15/11/3.
 */
public class ProgressView extends View {

    public ProgressView(Context context) {
        super(context);
    }

    public ProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public ProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    Paint mPaint = new Paint();

    int offset = 5;
    int forgroundColor = R.color.white;
    int backgroundColor = R.color.black;
    int progressColor = R.color.pink_ff;
    int max_progress = 100;
    int current_progress = 0;

    public void setBgColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setFgColor(int forgroundColor) {
        this.forgroundColor = forgroundColor;
    }

    public void setProgressColor(int progressColor) {
        this.progressColor = progressColor;
    }

    public void setMaxProgress(int max_progress) {
        this.max_progress = max_progress;
    }

    public void setCurrentProgress(int current_progress) {
        this.current_progress = current_progress;
    }
    public int getProgressWidth(){
        int width = getWidth();
        int height = getHeight();
        int progress_width = (int) ((width - 2 * height) * (current_progress - 2) * 1.0 / (max_progress - 2));
        Log.i("da--width",width);
        Log.i("da--higth",height);
        Log.i("da--progress_width",progress_width);
        return progress_width;

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        mPaint.setColor(getResources().getColor(backgroundColor));
        canvas.drawArc(new RectF(0, 0, height, height), 90, 180, true, mPaint);
        canvas.drawRect(height / 2, 0, width - height / 2, height, mPaint);
        canvas.drawArc(new RectF(width - height, 0, width, height), -90, 180, true, mPaint);

        mPaint.setColor(getResources().getColor(forgroundColor));
        canvas.drawArc(new RectF(offset, offset, height - 2 * offset + offset, height - 2 * offset + offset), 90, 180, true, mPaint);
        canvas.drawRect(height / 2, offset, width - height / 2, height - offset, mPaint);
        canvas.drawArc(new RectF(width - height + offset, offset, width - height + offset + (height - 2 * offset), offset + (height - 2 * offset)), -90, 180, true, mPaint);

        if (current_progress == 0) {
            //do nothing
        } else if (current_progress == 1) {
            mPaint.setColor(getResources().getColor(progressColor));
            canvas.drawArc(new RectF(offset, offset, height - 2 * offset + offset, height - 2 * offset + offset), 90, 180, true, mPaint);
        } else if (current_progress == max_progress - 1) {
            mPaint.setColor(getResources().getColor(progressColor));
            canvas.drawArc(new RectF(offset, offset, height - 2 * offset + offset, height - 2 * offset + offset), 90, 180, true, mPaint);
            canvas.drawRect(height / 2, offset, width - height / 2, height - offset, mPaint);
        } else if (current_progress == max_progress) {
            mPaint.setColor(getResources().getColor(progressColor));
            canvas.drawArc(new RectF(offset, offset, height - 2 * offset + offset, height - 2 * offset + offset), 90, 180, true, mPaint);
            canvas.drawRect(height / 2, offset, width - height / 2, height - offset, mPaint);
            canvas.drawArc(new RectF(width - height + offset, offset, width - height + offset + (height - 2 * offset), offset + (height - 2 * offset)), -90, 180, true, mPaint);
        } else {
            int progress_width = (int) ((width - 2 * height) * (current_progress - 2) * 1.0 / (max_progress - 2));
            mPaint.setColor(getResources().getColor(progressColor));
            canvas.drawArc(new RectF(offset, offset, height - 2 * offset + offset, height - 2 * offset + offset), 90, 180, true, mPaint);
            canvas.drawRect(height / 2, offset, height / 2 + progress_width, height - offset, mPaint);
        }
    }
}

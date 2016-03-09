package com.gezhii.fitgroup.ui.view.bessel_curve;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.Screen;

import java.util.List;

/**
 * Created by xianrui on 15/11/27.
 */
public class BesselCurveView extends View {

    Path mPath;
    Paint mPaint;
    List<Double> drawHeightOffsetList;
    int intervalWidth = Screen.getScreenWidth() / 6;


    public BesselCurveView(Context context) {
        this(context, null);
    }

    public BesselCurveView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BesselCurveView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setStrokeWidth(Screen.dip2px(1));
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setTextSize(getResources().getDimension(R.dimen.text_size_20));
        mPaint.setColor(getResources().getColor(R.color.white));
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setDrawHeightOffsetList(List<Double> drawHeightOffsetList) {
        this.drawHeightOffsetList = drawHeightOffsetList;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPath.reset();
        float startX = 0;
        float viewHeight = getHeight() - Screen.dip2px(15);
        float startY;
        if (drawHeightOffsetList != null) {
            if (drawHeightOffsetList.size() == 1) {
                return;
            }

            startY = (int) (viewHeight * (1 - drawHeightOffsetList.get(0)));
            mPath.moveTo(startX, startY);

            for (int i = 1; i < drawHeightOffsetList.size(); i++) {
                double offset = drawHeightOffsetList.get(i);
                float x = startX + intervalWidth;
                float y = (int) (viewHeight * (1 - offset));

                float quadX1 = startX + ((x - startX) / 3);
                float quadY1 = startY;

                float quadX2 = startX + ((x - startX) * 2 / 3);
                float quadY2 = y;

                mPath.cubicTo(quadX1, quadY1, quadX2, quadY2, x, y);
                startX = x;
                startY = y;
                mPath.moveTo(startX, startY);
            }
            canvas.drawPath(mPath, mPaint);
        }
    }

    public int getViewWidth() {
        if (drawHeightOffsetList != null) {
            return (drawHeightOffsetList.size() - 1) * intervalWidth;
        } else {
            return 0;
        }
    }


}

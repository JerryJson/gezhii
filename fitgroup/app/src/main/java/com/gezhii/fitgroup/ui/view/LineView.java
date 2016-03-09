package com.gezhii.fitgroup.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.gezhii.fitgroup.R;


public class LineView extends View {

    private LineStyle mLineStyle;
    private int mColor;

    public enum LineStyle {
        Normal(0), Dash(1);

        public final int value;

        private LineStyle(int value) {
            this.value = value;
        }

        public static LineStyle fromValue(int value) {
            for (LineStyle style : LineStyle.values()) {
                if (style.value == value) {
                    return style;
                }
            }
            return null;
        }
    }

    public LineView(Context context) {
        this(context, null);
    }

    public LineView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.DivLine);
    }

    public LineView(Context context, AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Line,
                R.style.DivLine, defStyle);
        mLineStyle = LineStyle.fromValue(a.getInteger(
                R.styleable.Line_line_mode, LineStyle.Normal.value));
        mColor = a.getColor(R.styleable.Line_line_color, 0xFF000000);
        a.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        int mWidth = getWidth();
        int mHeight = getHeight();

        Paint paint = new Paint();
        // 让画出的图形是空心的
        paint.setStyle(Paint.Style.STROKE);
        paint.setTypeface(Typeface.DEFAULT_BOLD);
        paint.setColor(mColor);
        Path path = new Path();
        path.moveTo(0, 0);
        if (mHeight > mWidth) {
            paint.setStrokeWidth(mWidth);
            path.lineTo(0, getHeight());
        } else {
            paint.setStrokeWidth(getHeight());
            path.lineTo(getWidth(), 0);
        }
        if (mLineStyle.value == LineStyle.Dash.value) {
            PathEffect effects = new DashPathEffect(
                    new float[]{6, 3, 6, 3},
                    1
            );
            paint.setPathEffect(effects);
        }
        canvas.drawPath(path, paint);
    }


}

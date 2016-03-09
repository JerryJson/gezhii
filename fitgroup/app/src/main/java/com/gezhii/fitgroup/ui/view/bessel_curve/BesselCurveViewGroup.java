package com.gezhii.fitgroup.ui.view.bessel_curve;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.Screen;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianrui on 15/11/27.
 */
public class BesselCurveViewGroup extends ViewGroup {

    BesselCurveView mBesselCurveView;
    List<View> pointViewList;
    List<Double> offSetList;
    int currentPosition;
    int edgeWidth = (Screen.getScreenWidth() - Screen.dip2px(24)) / 2;

    public static ImageView getPointView(Context context) {
        ImageView pointView = new ImageView(context);
        pointView.setBackgroundResource(R.drawable.bessel_curve_point_bg);
        pointView.setImageResource(R.drawable.bessel_curve_point);
        int padding = Screen.dip2px(3);
        pointView.setPadding(padding, padding, padding, padding);
        return pointView;
    }


    public BesselCurveViewGroup(Context context) {
        this(context, null);
    }

    public BesselCurveViewGroup(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BesselCurveViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        pointViewList = new ArrayList<>();
        offSetList = new ArrayList<>();

        initBaseView();
        initPoint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (mBesselCurveView != null) {
            mBesselCurveView.measure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(mBesselCurveView.getViewWidth() + edgeWidth * 2, heightMeasureSpec);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }

    }

    public int getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        for (View v : pointViewList) {
            v.setSelected(false);
        }
        pointViewList.get(currentPosition).setSelected(true);
        requestLayout();
    }


    public List<View> getPointViewList() {
        return pointViewList;
    }

    public int getIntervalWidth() {
        return mBesselCurveView.intervalWidth;
    }

    public void setOffSetList(List<Double> offSetList) {
        this.offSetList = offSetList;
        removeAllViews();
        initBaseView();
        initPoint();
    }

    private void initPoint() {
        if (offSetList != null && offSetList.size() > 0) {
            pointViewList.clear();
            for (int i = 0; i < offSetList.size(); i++) {
                ImageView pointView = getPointView(getContext());
                if (i == currentPosition) {
                    pointView.setSelected(true);
                } else {
                    pointView.setSelected(false);
                }
                pointViewList.add(pointView);
                addView(pointView);
            }
        }
    }


    private void initBaseView() {
        mBesselCurveView = new BesselCurveView(getContext());
        mBesselCurveView.setDrawHeightOffsetList(offSetList);
        addView(mBesselCurveView);
    }

    @Override
    public void requestLayout() {
        super.requestLayout();
    }


    //初始的时候,是从"屏幕中间往右边画", besselCurView的宽带是可以计算的(2个点之间的间距是固定的)
    //在初始化的时候,MeFragment里面,滚到了最左边
//    besselCurveView.post(new Runnable() {
//        @Override
//        public void run() {
//            besselCurveView.setCurrentPosition(UserModel.getInstance().getUserDto().getWeight_list().size() - 1);
//        }
//    });
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mBesselCurveView.layout(edgeWidth, 0, mBesselCurveView.getViewWidth() + edgeWidth, getHeight());

        int height = getHeight() - Screen.dip2px(15);

        int startX = edgeWidth;
        int intervalWidth = mBesselCurveView.intervalWidth;
        for (int i = 0; i < offSetList.size(); i++) {
            Double offSet = offSetList.get(i);
            int pointWidth;
            if (currentPosition == i) {
                pointWidth = Screen.dip2px(15);
            } else {
                pointWidth = Screen.dip2px(14);
            }
            int top = (int) (height * (1 - offSet));
            View pointView = pointViewList.get(i);
            pointView.layout(startX - pointWidth / 2, top - pointWidth / 2, startX + pointWidth / 2, top + pointWidth / 2);

            startX += intervalWidth;
        }

    }
}

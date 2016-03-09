package com.gezhii.fitgroup.ui.view.bessel_curve;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.UserWeight;
import com.gezhii.fitgroup.tools.Screen;
import com.gezhii.fitgroup.ui.adapter.BaseListAdapter;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianrui on 15/11/27.
 */
public class BesselCurveViewScroller extends FrameLayout {
    BesselCurveViewGroup mBesselCurveViewGroup;

    int currentPosition;

    //整个的实现思路:FrameLayout里面就1个RecyclerView
    //RecyclerView是一个水平的LinearLayout,里面只有1个元素,就是上面的mBesselCurveViewGroup
    //通过RecyclerView实现滚动效果, mRecyclerView.smoothScrollBy
    RecyclerView mRecyclerView;
    BesselAdapter mBesselAdapter;
    int offsetX;

    List<UserWeight> mUserWeightList;
    View goalWeightView;
    float goalWeight;
    float maxWeight;

    OnBesselCurveViewSelectChangeListener mOnBesselCurveViewSelectChangeListener;

    public void initOffsetX() {
        this.offsetX = 0;
    }

    public BesselCurveViewScroller(Context context) {
        this(context, null);
    }

    public BesselCurveViewScroller(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BesselCurveViewScroller(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initBaseView();
        initCurrentView();
        initGoalWeightView();
    }

    private void initGoalWeightView() {
        goalWeightView = LayoutInflater.from(getContext()).inflate(R.layout.bessel_curve_line_view, null);
        goalWeightView.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        goalWeightView.setVisibility(INVISIBLE);
        addView(goalWeightView);
    }

    private void setGoalWeightView() {
        float offset;
        if (goalWeight > 0 && maxWeight > 0) {
            goalWeightView.setVisibility(VISIBLE);
            offset = 1 - (goalWeight / maxWeight);
        } else {
            goalWeightView.setVisibility(INVISIBLE);
            offset = 1;
        }
        int height = mRecyclerView.getMeasuredHeight();
        goalWeightView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        height -= Screen.dip2px(15);
        int heightOffset = (int) (height * offset);
        heightOffset = heightOffset - goalWeightView.getMeasuredHeight() / 2;

        FrameLayout.LayoutParams layoutParams = (LayoutParams) goalWeightView.getLayoutParams();
        layoutParams.setMargins(0, heightOffset, 0, 0);
        goalWeightView.setLayoutParams(layoutParams);
        TextView contentText = (TextView) goalWeightView.findViewById(R.id.content_text);
        contentText.setText("目标体重 : " + goalWeight + "kg");
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    public void setGoalWeight(float goalWeight) {
        this.goalWeight = goalWeight;
        if (mBesselCurveViewGroup != null) {
            setGoalWeightView();
        }
    }

    public void setGoalWeightOnClickListener(View.OnClickListener onClickListener) {
        if (goalWeightView != null) {
            goalWeightView.findViewById(R.id.content_text).setOnClickListener(onClickListener);
        }
    }

    public void setUserWeightList(List<UserWeight> mUserWeightList) {
        this.mUserWeightList = mUserWeightList;
        if (mBesselCurveViewGroup != null) {
            setBaseView();
            setGoalWeightView();
        }
    }

    private void setBaseView() {
        float maxValue = 0;
        for (UserWeight userWeight : mUserWeightList) {
            if (userWeight.getWeight() > maxValue) {
                maxValue = userWeight.getWeight();
            }
        }
        maxValue += maxValue / 5;
        List<Double> offsetList = new ArrayList<>();
        for (UserWeight userWeight : mUserWeightList) {
            double offset = userWeight.getWeight() / maxValue;
            offsetList.add(offset);
        }
        maxWeight = maxValue;
        if (mBesselCurveViewGroup == null) {
            mBesselCurveViewGroup = (BesselCurveViewGroup) mBesselAdapter.mViewHolder.itemView;
        }
        mBesselCurveViewGroup.setOffSetList(offsetList);
    }

    public void setOnBesselCurveViewSelectChangeListener(OnBesselCurveViewSelectChangeListener mOnBesselCurveViewSelectChangeListener) {
        this.mOnBesselCurveViewSelectChangeListener = mOnBesselCurveViewSelectChangeListener;
    }

    public void setCurrentPosition(int currentPosition) {
        this.currentPosition = currentPosition;
        mBesselCurveViewGroup.setCurrentPosition(currentPosition);
        mRecyclerView.smoothScrollBy(mBesselCurveViewGroup.getIntervalWidth() * currentPosition - offsetX, 0);
        if (mOnBesselCurveViewSelectChangeListener != null) {
            mOnBesselCurveViewSelectChangeListener.onBesselCurveViewSelectChange(currentPosition);
        }

        Log.i("ycl", "offsetX init = " + offsetX);
    }

    private void initCurrentView() {
        ImageView currentView = new ImageView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(Screen.dip2px(10), Screen.dip2px(7));

        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        currentView.setLayoutParams(params);
        currentView.setImageResource(R.mipmap.current_view);
        addView(currentView);
    }

    private void initBaseView() {
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i("ycl", "onScrollStateChanged " + newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (mBesselCurveViewGroup == null) {
                        mBesselCurveViewGroup = (BesselCurveViewGroup) mBesselAdapter.mViewHolder.itemView;
                    }
                    int currentPosition;

                    currentPosition = offsetX / mBesselCurveViewGroup.getIntervalWidth();
                    int offsetRemainder = offsetX % mBesselCurveViewGroup.getIntervalWidth();
                    if (offsetRemainder > mBesselCurveViewGroup.getIntervalWidth() / 2) {
                        currentPosition += 1;
                    }

                    Log.i("ycl", "currentPosition = " + currentPosition + ", " + offsetX);

                    if (currentPosition != BesselCurveViewScroller.this.currentPosition) {
                        mBesselCurveViewGroup.setCurrentPosition(currentPosition);
                        BesselCurveViewScroller.this.currentPosition = currentPosition;
                        if (mOnBesselCurveViewSelectChangeListener != null) {
                            mOnBesselCurveViewSelectChangeListener.onBesselCurveViewSelectChange(currentPosition);
                        }
                    }
                    mRecyclerView.smoothScrollBy(mBesselCurveViewGroup.getIntervalWidth() * currentPosition - offsetX, 0);
                }
            }


            //1.往左划, dx > 0, 往右划, dx < 0
            //2.初始的时候,往左划到了底(调用setCurrentPosition函数),所以offsetX初始,处于最大值
            //3.offsetX一直没有清0, 所以offsetX实际 = 向左划动的距离, 并且offsetX >= 0, 不可能 < 0
            //4.所以offsetX/internalWidth,就是当前的第几个结点(初始的时候,是处于最后1个点, pointList.size() - 1的位置)
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                offsetX += dx;
                Log.i("ycl", "onScrolled = " + offsetX + "," + dx + "," + dy);
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        mBesselAdapter = new BesselAdapter(getContext());
        mRecyclerView.setAdapter(mBesselAdapter);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.setMargins(0, 0, 0, Screen.dip2px(7));
        mRecyclerView.setLayoutParams(params);
        addView(mRecyclerView);

        if (mBesselCurveViewGroup == null) {
            mBesselCurveViewGroup = (BesselCurveViewGroup) mBesselAdapter.mViewHolder.itemView;
        }

        if (mUserWeightList != null && mUserWeightList.size() > 0) {
            setBaseView();
        }
//        removeAllViews();
//        mBesselCurveViewGroup = new BesselCurveViewGroup(getContext());
//        addView(mBesselCurveViewGroup);
    }


    public interface OnBesselCurveViewSelectChangeListener {
        void onBesselCurveViewSelectChange(int position);
    }


    private static class BesselAdapter extends BaseListAdapter {

        ViewHolder mViewHolder;

        public BesselAdapter(Context mContext) {
            BesselCurveViewGroup mBesselCurveViewGroup = new BesselCurveViewGroup(mContext);
            mViewHolder = new ViewHolder(mBesselCurveViewGroup);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return mViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }


}

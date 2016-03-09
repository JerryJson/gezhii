package com.gezhii.fitgroup.ui.view.blur;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class BlurFrameLayout extends FrameLayout {
	private BlurCalculate mBlurCalculate;

	public BlurFrameLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mBlurCalculate = new BlurCalculate(this);
        mBlurCalculate.setaction(1);
	}

	public BlurFrameLayout(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	/***
	 * radius for linearlayout
	 ***/
	public void setRadius(int arg0) {
		if (mBlurCalculate != null)
			mBlurCalculate.setRadius(arg0);
		invalidate();
	}

	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		mBlurCalculate.onAttachedToWindow();
	}

	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		mBlurCalculate.BluronDetachedFromWindow();
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (mBlurCalculate.isCanvasChanged(canvas))
			mBlurCalculate.BlurCanvas();
		else {
			mBlurCalculate.DrawCanvas(canvas);
			super.dispatchDraw(canvas);

		}
	}
}

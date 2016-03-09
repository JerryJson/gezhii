package com.gezhii.fitgroup.ui.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gezhii.fitgroup.R;


public class RectImageView extends ImageView {
	/** 填充方式 */
	private static final ScaleType[] SCALE_TYPES = { ScaleType.MATRIX,
			ScaleType.FIT_XY, ScaleType.FIT_START, ScaleType.FIT_CENTER,
			ScaleType.FIT_END, ScaleType.CENTER, ScaleType.CENTER_CROP,
			ScaleType.CENTER_INSIDE };
	/** 弧度 */
	private int mCornerRadius = 0;
	/** 边框值 */
	private int mBorderWidth = 0;
	private ColorStateList mBorderColor = ColorStateList
			.valueOf(RectDrawable.DEFAULT_BORDER_COLOR);
	/** 是否是圆形 */
	private boolean mOval = false;

	private int mResource;
	private Drawable mDrawable;
	private Drawable mBackgroundDrawable;
	/** 对其方式 */
	private ScaleType mScaleType;

	private int mBaseWidth = 0;
	private int mBaseHeight = 0;
	private boolean mResetLayout = true;

	public RectImageView(Context context) {
		this(context, null);
	}

	public RectImageView(Context context, AttributeSet attrs) {
		this(context, attrs, R.style.DivRectImageView);
	}

	public RectImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		final Resources res = getResources();
		int DEFAULT_RADIUS = res
				.getDimensionPixelSize(R.dimen.ri_radius_default);
		int DEFAULT_BORDER_WIDTH = res
				.getDimensionPixelSize(R.dimen.ri_border_width_default);
		int DEFAULT_BORDER_COLOR = res
				.getColor(R.color.ri_border_color_default);
		boolean DEFAULT_OVAL = res.getBoolean(R.bool.ri_is_oval_default);

		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.RectImageView, defStyle, 0);
		int index = a.getInt(R.styleable.RectImageView_android_scaleType, 5);
		if (index >= 0) {
			setScaleType(SCALE_TYPES[index]);
		} else {
			setScaleType(ScaleType.CENTER_CROP);
		}
		mCornerRadius = a.getDimensionPixelSize(
				R.styleable.RectImageView_ri_radius, DEFAULT_RADIUS);
		mBorderWidth = a
				.getDimensionPixelSize(
						R.styleable.RectImageView_ri_border_width,
						DEFAULT_BORDER_WIDTH);
		mBorderColor = ColorStateList.valueOf(a
				.getColor(R.styleable.RectImageView_ri_border_color,
						DEFAULT_BORDER_COLOR));
		mOval = a
				.getBoolean(R.styleable.RectImageView_ri_is_oval, DEFAULT_OVAL);
		mBaseWidth = a.getInt(R.styleable.RectImageView_ri_base_width, 0);
		mBaseHeight = a.getInt(R.styleable.RectImageView_ri_base_height, 0);
		if (mOval) {
			mBaseWidth = 0;
			mBaseHeight = 0;
		}

		updateDrawableAttrs();
		updateBackgroundDrawableAttrs();
		a.recycle();
	}

	@Override
	protected void drawableStateChanged() {
		super.drawableStateChanged();
		invalidate();
	}

	/**
	 * Return the current scale type in use by this ImageView.
	 * 
	 * @attr ref android.R.styleable#ImageView_scaleType
	 * @see ScaleType
	 */
	@Override
	public ScaleType getScaleType() {
		return mScaleType;
	}

	/**
	 * Controls how the image should be resized or moved to match the size of
	 * this ImageView.
	 * 
	 * @param scaleType
	 *            The desired scaling mode.
	 * @attr ref android.R.styleable#ImageView_scaleType
	 */
	@Override
	public void setScaleType(ScaleType scaleType) {
		if (scaleType == null) {
			throw new NullPointerException();
		}

		if (mScaleType != scaleType) {
			mScaleType = scaleType;

			switch (scaleType) {
			case CENTER:
			case CENTER_CROP:
			case CENTER_INSIDE:
			case FIT_CENTER:
			case FIT_START:
			case FIT_END:
			case FIT_XY:
				super.setScaleType(ScaleType.FIT_XY);
				break;
			default:
				super.setScaleType(scaleType);
				break;
			}

			updateDrawableAttrs();
			updateBackgroundDrawableAttrs();
			invalidate();
		}
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		mResource = 0;
		mDrawable = RectDrawable.fromDrawable(drawable);
		updateDrawableAttrs();
		super.setImageDrawable(mDrawable);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		mResource = 0;
		mDrawable = RectDrawable.fromBitmap(bm);
		updateDrawableAttrs();
		super.setImageDrawable(mDrawable);
	}

	@Override
	public void setImageResource(int resId) {
		if (mResource != resId) {
			mResource = resId;
			mDrawable = resolveResource();
			updateDrawableAttrs();
			super.setImageDrawable(mDrawable);
		}
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		setImageDrawable(getDrawable());
	}

	private Drawable resolveResource() {
		Resources rsrc = getResources();
		if (rsrc == null) {
			return null;
		}

		Drawable d = null;

		if (mResource != 0) {
			try {
				d = rsrc.getDrawable(mResource);
			} catch (Exception e) {
				mResource = 0;
			}
		}
		return RectDrawable.fromDrawable(d);
	}

	@Override
	public void setBackground(Drawable background) {
		setBackgroundDrawable(background);
	}

	private void updateDrawableAttrs() {
		updateAttrs(mDrawable, false);
	}

	private void updateBackgroundDrawableAttrs() {
		updateAttrs(mBackgroundDrawable, true);
	}

	private void updateAttrs(Drawable drawable, boolean background) {
		if (drawable == null) {
			return;
		}

		if (drawable instanceof RectDrawable) {
			((RectDrawable) drawable).setScaleType(mScaleType)
					.setCornerRadius(background ? 0 : mCornerRadius)
					.setBorderWidth(background ? 0 : mBorderWidth)
					.setBorderColors(mBorderColor).setOval(mOval);
		} else if (drawable instanceof LayerDrawable) {
			// loop through layers to and set drawable attrs
			LayerDrawable ld = ((LayerDrawable) drawable);
			int layers = ld.getNumberOfLayers();
			for (int i = 0; i < layers; i++) {
				updateAttrs(ld.getDrawable(i), background);
			}
		}
	}

	@Override
	@Deprecated
	public void setBackgroundDrawable(Drawable background) {
		mBackgroundDrawable = RectDrawable.fromDrawable(background);
		updateBackgroundDrawableAttrs();
		super.setBackgroundDrawable(mBackgroundDrawable);
	}

	/** 设置基础宽度与高度 */
	public void setBaseRect(int mBaseWidth, int mBaseHeight) {
		mResetLayout = true;
		this.mBaseWidth = mBaseWidth;
		this.mBaseHeight = mBaseHeight;
		invalidate();
	}

	public int getCornerRadius() {
		return mCornerRadius;
	}

	/** 设置弧度值 */
	public void setCornerRadius(int radius) {
		if (mCornerRadius == radius) {
			return;
		}
		mCornerRadius = radius;
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs();
	}

	public int getBorderWidth() {
		return mBorderWidth;
	}

	/** 设置宽度值 */
	public void setBorderWidth(int width) {
		if (mBorderWidth == width) {
			return;
		}

		mBorderWidth = width;
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs();
		invalidate();
	}

	public int getBorderColor() {
		return mBorderColor.getDefaultColor();
	}

	/** 设置边框颜色 */
	public void setBorderColor(int color) {
		setBorderColors(ColorStateList.valueOf(color));
	}

	public ColorStateList getBorderColors() {
		return mBorderColor;
	}

	/** 设置边框颜色 */
	public void setBorderColors(ColorStateList colors) {
		if (mBorderColor.equals(colors)) {
			return;
		}

		mBorderColor = (colors != null) ? colors : ColorStateList
				.valueOf(RectDrawable.DEFAULT_BORDER_COLOR);
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs();
		if (mBorderWidth > 0) {
			invalidate();
		}
	}

	public boolean isOval() {
		return mOval;
	}

	/** 设置是否为圆形 */
	public void setOval(boolean oval) {
		mOval = oval;
		updateDrawableAttrs();
		updateBackgroundDrawableAttrs();
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {

		if (getWidth() != 0 && getHeight() != 0 && mBaseHeight > 0
				&& mBaseWidth > 0 && mResetLayout) {
			mResetLayout = false;
			int width = getWidth();
			int height = width * mBaseHeight / mBaseWidth;
			ViewGroup.LayoutParams mLayoutParams = getLayoutParams();
			mLayoutParams.width = width;
			mLayoutParams.height = height;
			if (mBaseWidth > width || mBaseHeight > height) {
				mBaseWidth = width;
				mBaseHeight = height;
			}
			setLayoutParams(mLayoutParams);
		} else {
			super.onDraw(canvas);
		}
	}

	/**
	 * 
	 * @param x
	 *            图像的宽度
	 * @param y
	 *            图像的高度
	 * @param image
	 *            源图片
	 * @param outerRadiusRat
	 *            圆角的大小
	 * @return 圆角图片
	 */

	public static Bitmap createBitmapPhoto(int mWidth, int mHeight,
			Drawable imageDrawable, float outerRadiusRat) {

		if (imageDrawable == null)
			return null;
		// 新建一个新的输出图片
		Bitmap output = Bitmap.createBitmap(mWidth, mHeight,
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		// 新建一个矩形
		RectF outerRect = new RectF(0, 0, mWidth, mHeight);
		// 产生一个白色的圆角矩形
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setColor(Color.WHITE);
		canvas.drawRoundRect(outerRect, outerRadiusRat, outerRadiusRat, paint);
		// 将源图片绘制到这个圆角矩形上
		paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		imageDrawable.setBounds(0, 0, mWidth, mHeight);
		canvas.saveLayer(outerRect, paint, Canvas.ALL_SAVE_FLAG);
		imageDrawable.draw(canvas);
		canvas.restore();
		return output;

	}

	public static Drawable createDrawablePhoto(int x, int y,
			Drawable imageDrawable, float outerRadiusRat) {

		if (imageDrawable == null)
			return imageDrawable;
		return new BitmapDrawable(createBitmapPhoto(x, y, imageDrawable,
				outerRadiusRat));

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		Drawable d = getDrawable();
		if (mBaseHeight <= 0 && mBaseWidth <= 0 && d != null && !mOval) {
			int width = MeasureSpec.getSize(widthMeasureSpec);
			int height = (int) Math.ceil((float) width
					* (float) d.getIntrinsicHeight()
					/ (float) d.getIntrinsicWidth());
			setMeasuredDimension(width, height);
		} else {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}

	}

}

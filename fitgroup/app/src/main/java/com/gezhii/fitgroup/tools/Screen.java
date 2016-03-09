package com.gezhii.fitgroup.tools;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.gezhii.fitgroup.MyApplication;

import java.lang.reflect.Field;

/**
 * Created by xianrui on 15/4/29.
 */
public class Screen {

    private static int mScreenWidth = 0;
    private static int mScreenHeight = 0;

    private static void initScreen() {
        DisplayMetrics displaymetrics = new DisplayMetrics();
        WindowManager window = (WindowManager) (MyApplication.getApplication().getSystemService(Context.WINDOW_SERVICE));
        window.getDefaultDisplay().getMetrics(displaymetrics);
        mScreenWidth = displaymetrics.widthPixels;
        mScreenHeight = displaymetrics.heightPixels;
    }

    public static int getScreenWidth() {
        if (mScreenWidth == 0) {
            initScreen();
        }
        return mScreenWidth;
    }

    public static int getScreenHeight() {
        if (mScreenHeight == 0) {
            initScreen();
        }
        return mScreenHeight;
    }


    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, sbar = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            sbar = MyApplication.getApplication().getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return sbar;
    }

    /**
     * 将px值转换为dip或dp值，保证尺寸大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2dip(float pxValue) {
        float scale = MyApplication.getApplication().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将dip或dp值转换为px值，保证尺寸大小不变
     *
     * @param dipValue
     * @return
     */
    public static int dip2px(float dipValue) {
        float scale = MyApplication.getApplication().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     *
     * @param pxValue
     * @return
     */
    public static int px2sp(float pxValue) {
        float fontScale = MyApplication.getApplication().getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     *
     * @param spValue
     * @return
     */
    public static int sp2px(float spValue) {
        float fontScale = MyApplication.getApplication().getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}

package com.gezhii.fitgroup.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.gezhii.fitgroup.R;


/**
 * Created by xianrui on 15/5/11.
 */
public class LoadingDialog extends Dialog {

    ImageView mImageView;
    RotateAnimation rotateAnimation;

    public LoadingDialog(Context context) {
        super(context);
    }

    public LoadingDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        setContentView(R.layout.loading_dialog);
        mImageView = (ImageView) findViewById(R.id.image);
        rotateAnimation = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setRepeatMode(Animation.RESTART);
        rotateAnimation.setInterpolator(new LinearInterpolator());

    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    public void show() {
        try {
            super.show();
            mImageView.startAnimation(rotateAnimation);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

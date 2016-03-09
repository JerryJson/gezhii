package com.gezhii.fitgroup.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.event.ShowLoadingEvent;
import com.gezhii.fitgroup.ui.dialog.LoadingDialog;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/4/21.
 */
public class BaseActivity extends FragmentActivity {

    private LoadingDialog loadDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        loadDialog = new LoadingDialog(this, android.R.style.Theme_Translucent_NoTitleBar);
        EventBus.getDefault().registerSticky(this);
        MyApplication.getApplication().addActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        View rootView = LayoutInflater.from(this).inflate(layoutResID, null);
        setContentView(rootView);
        ButterKnife.inject(this);
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    private void showLoading() {
        if (!loadDialog.isShowing()) {
            loadDialog.show();
        }
    }

    private void hideLoading() {
        if (loadDialog.isShowing()) {
            loadDialog.dismiss();
        }
    }

    public boolean isLoadingShowing() {
        return loadDialog.isShowing();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    public void onEvent(ShowLoadingEvent showLoadingEvent) {
        if (!isLoadingShowing()) {
            showLoading();
        }
    }


    public void onEvent(CloseLoadingEvent closeLoadingEvent) {
        if (isLoadingShowing()) {
            hideLoading();
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        MyApplication.getApplication().removeActivity(this);
        super.onDestroy();
    }
}

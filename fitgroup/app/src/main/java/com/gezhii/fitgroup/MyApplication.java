package com.gezhii.fitgroup;

import android.app.Activity;
import android.app.Application;

import com.facebook.stetho.Stetho;
import com.gezhii.fitgroup.huanxin.HuanXinHelper;
import com.gezhii.fitgroup.model.GroupLevelConfigModel;
import com.gezhii.fitgroup.model.PrivateMessageModel;
import com.gezhii.fitgroup.model.TaskDBModel;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.melink.bqmmsdk.sdk.BQMMSdk;
import com.tencent.bugly.crashreport.CrashReport;

import java.util.ArrayList;

/**
 * Created by xianrui on 15/9/16.
 */
public class MyApplication extends Application {

    private static MyApplication application;
    HuanXinHelper mHuanXinHelper;

    private static ArrayList<Activity> activities;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        activities = new ArrayList<>();
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
        mHuanXinHelper = new HuanXinHelper(this);
//        ImageLoadHelper.init();
        QiniuHelper.init();

        GroupLevelConfigModel.getInstance().getGroupLevelConfig();
        PrivateMessageModel.getInstance();
        //SportDBModel.getInstance();
        TaskDBModel.getInstance();
        CrashReport.initCrashReport(this, "900014569", false);
        //CrashReport.testJavaCrash();


        BQMMSdk.getInstance().initConfig(getApplicationContext(),"31cc134aa28a4f469c5f7134ba401be8", "5e55867b37384f26a66061af8c52845b");


    }

    public static MyApplication getApplication() {
        return application;
    }

    public HuanXinHelper getHuanXinHelper() {
        return mHuanXinHelper;
    }

    public void addActivity(Activity activity) {
        if (activities != null) {
            for (Activity ac : activities) {
                if (ac.getClass().getSimpleName().equals(activity.getClass().getSimpleName())) {
                    ac.finish();
                }
            }
            activities.add(activity);
        }
    }

    public void removeActivity(Activity activity) {
        if (activities != null) {
            activities.remove(activity);
        }
    }

    public void finish() {
        if (activities != null) {
            for (Activity activity : activities) {
                activity.finish();
            }
        }
    }
}

package com.gezhii.fitgroup.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.SplashDTO;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.xianrui.lite_common.litesuits.android.log.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;


/**
 * Created by xianrui on 15/4/20.
 */
public class SplashActivity extends Activity {


    public static final String TAG_FIRST_LOGIN = "tag_first_login";
    public static final String TAG_HAVE_ANSWER = "tag_have_answer";
    int countDownSecond;
    @InjectView(R.id.splash_img)
    ImageView splashImg;
    @InjectView(R.id.logo_img)
    ImageView logoImg;
    @InjectView(R.id.normal_text_layout)
    LinearLayout normalTextLayout;
    @InjectView(R.id.special_image)
    ImageView specialImage;

    String cacheUrl;
    String img;
    String url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        ButterKnife.inject(this);
        countDownSecond = 3;

        final SharedPreferences sharedPreferences = getSharedPreferences("fitGroup", 0);
        cacheUrl = sharedPreferences.getString("cacheUrl", "");
        if (!"".equals(cacheUrl)) {
            ImageLoader.getInstance().displayImage(cacheUrl, splashImg);
        }
        API.getSplash(new APICallBack() {
            @Override
            public void subRequestSuccess(String response) {
                SplashDTO splashDTO = SplashDTO.parserJson(response);
                img = splashDTO.getSplash().getImg();
                Log.i("darren------->", img);
                url = QiniuHelper.getQiNiuDownLoadUrl(img);
                //Log.i("darren----------------->", url);
                ImageLoader.getInstance().displayImage(url, splashImg, QiniuHelper.options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        cacheUrl = img;
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("cacheUrl", cacheUrl);
                        editor.commit();
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }
        });
        timeDownHandler.postDelayed(timeDownRunnable, 3000);


        try {

            ApplicationInfo appInfo = this.getPackageManager()
                    .getApplicationInfo(getPackageName(),
                            PackageManager.GET_META_DATA);
            String msg = appInfo.metaData.getString("UMENG_CHANNEL");
            Log.i("xianrui", "UMENG_CHANNEL " + msg);

//            gezhii {}
//            wandoujia {}
//            _360 {}
//            baidu {}
//            xiaomi {}
//            tencent {}
//            taobao {}
            if (!TextUtils.isEmpty(msg)) {
                logoImg.setVisibility(View.VISIBLE);
                normalTextLayout.setVisibility(View.VISIBLE);
                specialImage.setVisibility(View.GONE);
                if (msg.equals("huawei")) {
                    logoImg.setVisibility(View.GONE);
                    normalTextLayout.setVisibility(View.GONE);
                    specialImage.setVisibility(View.VISIBLE);
                    specialImage.setImageResource(R.mipmap.splash_huawei);
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


    }


    Handler timeDownHandler = new Handler();
    Runnable timeDownRunnable = new Runnable() {
        @Override
        public void run() {
            if (timeDownHandler != null) {
                timeDownHandler.removeCallbacks(timeDownRunnable);
                Intent intent;
                SharedPreferences sharedPreferences = getSharedPreferences("fitGroup", 0);
                Boolean isFirstLogin = sharedPreferences.getBoolean(TAG_FIRST_LOGIN, true);
//                Boolean haveAnswerd = sharedPreferences.getBoolean(TAG_HAVE_ANSWER, false);
                if (UserModel.getInstance().isLogin()) {
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    finish();
                } else {
                    if (isFirstLogin) {
                        startActivity(new Intent(SplashActivity.this, GuideActivity.class));
                    } else {
                        startActivity(new Intent(SplashActivity.this, RegisterAndLoginActivity.class));
                    }

                }
                finish();
            }
        }
    };


    @Override
    protected void onDestroy() {
        timeDownHandler = null;
        timeDownRunnable = null;
        super.onDestroy();
    }

}

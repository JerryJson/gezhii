package com.gezhii.fitgroup.tools.qiniu;

import android.graphics.Bitmap;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.FastBlur;
import com.gezhii.fitgroup.tools.Screen;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.xianrui.lite_common.litesuits.android.log.Log;
import com.xianrui.lite_common.litesuits.common.utils.MD5Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by xianrui on 15/4/23.
 */
public class QiniuHelper {
    public static final String QINIU_HOST = "http://7xnoeh.com2.z0.glb.qiniucdn.com/";
    public static String QINIU_PIC_HOST = "http://7xnoeh.com2.z0.glb.qiniucdn.com/";
    public static final String QINIU_ACCESS_KEY = "dVeuNRbWUJ4pOhgHME80s-qCYxMZ5v3HVLU7RFRK";
    public static final String QINIU_SECRET_KEY = "r_-FBAH-CagE95J9aVVOC7LMK7uMybSQEHRHEi_7";
    public static final String QINIU_SPACE = "fitgroup-pic";
    public static final int DEFAULT_IMG = R.color.gray_97;
    public static DisplayImageOptions options;
    private static HashMap<String, String> picUrlMap;
    public static final String[] imgs = {"fitgroup-pic_fitgroupcheckindefaultimage0.png", "fitgroup-pic_fitgroupcheckindefaultimage1.png", "fitgroup-pic_fitgroupcheckindefaultimage2.png"};

    static {
        options = new DisplayImageOptions.Builder()//
                .showImageOnLoading(DEFAULT_IMG)// 设置图片在下载期间显示的图片
                .showImageForEmptyUri(DEFAULT_IMG)// 设置图片Uri为空或是错误的时候显示的图片
                .showImageOnFail(DEFAULT_IMG)// 设置图片加载/解码过程中错误时候显示的图片
                .cacheInMemory(false)// 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true)// 设置下载的图片是否缓存在SD卡中
                .considerExifParams(true)//
                        // .displayer(new FadeInBitmapDisplayer(300))//设置图片渐现时间
                        // .decodingOptions(getDefaultOptions())// 设置图片的解码配置
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// 设置图片以如何的编码方式显示
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        picUrlMap = new HashMap<>();
    }

    public static void init() {
        ImageLoaderConfiguration imageLoaderConfiguration = ImageLoaderConfiguration.createDefault(MyApplication.getApplication());
        ImageLoader.getInstance().init(imageLoaderConfiguration);
    }

    public static String uploadImgs(int userID, String filePath, UpCompletionHandler upCompletionHandler) {
        String userIdMD5 = MD5Util.md5(String.valueOf(userID + System.currentTimeMillis()));
        return uploadImgs(userID, userIdMD5, filePath, upCompletionHandler);
    }

    public static String uploadImgs(int userID, String key, String filePath, UpCompletionHandler upCompletionHandler) {
        UploadManager manager = new UploadManager();
        manager.put(filePath, key, getUpLoadToken(), upCompletionHandler, null);
        return QINIU_SPACE + "_" + key;
    }

    public static String uploadVideo(int userID, String filePath, UpCompletionHandler upCompletionHandler, UpProgressHandler upProgressHandler) {
        String userIdMD5 = MD5Util.md5(String.valueOf(userID + System.currentTimeMillis()));
        UploadManager manager = new UploadManager();
        UploadOptions uploadOptions = new UploadOptions(null, null, false, upProgressHandler, null);
        manager.put(filePath, userIdMD5, getUpLoadToken(), upCompletionHandler, uploadOptions);
        return QINIU_SPACE + "_" + userIdMD5;
    }

    public static String getVideoPlayUrl(String qiniu_url){
        return getQiNiuDownLoadUrl(qiniu_url);
    }

    public static String getVideoAndThumbnai(String key){
        return QINIU_SPACE + "_" + key;
    }

    public static void bindVideoThumbnaiImage(String qiniu_url,ImageView view){
        String downUrl = getVideoThumbnailUrl(qiniu_url);
        ImageLoader.getInstance().displayImage(downUrl, view, options, null);
    }

    public static void bindLocalImage(String filePath, ImageView view) {
        ImageLoader.getInstance().displayImage("file://" + filePath, view, options);
    }


    public static String getUpLoadToken() {
        Auth auth = Auth.create(QINIU_ACCESS_KEY, QINIU_SECRET_KEY);
        return auth.uploadToken(QINIU_SPACE);
    }

    public static String getUpLoadToken(String fileName) {
        Auth auth = Auth.create(QINIU_ACCESS_KEY, QINIU_SECRET_KEY);
        return auth.uploadToken(QINIU_SPACE, fileName);
    }


    public static String getSignDownLoadUrl(String url) {
        Auth auth = Auth.create(QINIU_ACCESS_KEY, QINIU_SECRET_KEY);
        return auth.privateDownloadUrl(url);
    }

    public static void bingImageType(String qiniu_url, final ImageView view) {
        String[] imageType = qiniu_url.split("-WH-");
        if (imageType.length > 2) {
            int viewWidth = Integer.parseInt(imageType[1]);
            int viewHeight = Integer.parseInt(imageType[2]);
            int myPhotoWidth = Screen.getScreenWidth() - Screen.dip2px(24);
            viewHeight = (int) (((double) myPhotoWidth / viewWidth) * viewHeight);
            viewWidth = Screen.getScreenWidth();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = viewHeight;
            layoutParams.width = myPhotoWidth;
//            Log.i("xianrui", "bingImageType width:" + viewWidth + " height:" + viewHeight);
            view.setLayoutParams(layoutParams);
//            if (view != null && view instanceof TestImageView) {
//            if (viewHeight > viewWidth * 2.5) {
//                view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
//            } else {
//                view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
//            }
//            }
        }
        bindImage(qiniu_url, view);
    }

    public static void bindAvatarImage(String url, final ImageView view) {
        if (TextUtils.isEmpty(url)) {
            bindLocalImage(R.mipmap.ic_launcher, view);
        } else {
            bindImage(url, view);
        }
    }

    public static void bindChannelImage(String url, final ImageView view) {
        if (TextUtils.isEmpty(url)) {
            bindLocalImage(R.mipmap.channel_icon, view);
        } else {
            bindImage(url, view);
        }
    }

    public static void bindImage(String qiniu_url, final ImageView view) {
        downLoadImage(qiniu_url, view, null);
    }

    public static void bindImageByUrl(String url, final ImageView view) {
        ImageLoader.getInstance().displayImage(url, view, options);
    }

    public static void bindDrawableImage(String drawableName, ImageView view) {
        int resId = iconNameToResId(drawableName);
        if (resId != 0) {
            bindLocalImage(resId, view);
        }
    }


    public static int iconNameToResId(String name) {
        if (TextUtils.isEmpty(name)) return 0;
        return MyApplication.getApplication().getResources()
                .getIdentifier(name, "mipmap", MyApplication.getApplication().getPackageName());
    }


    private static void downLoadImage(String qiniu_url, final ImageView view, ImageLoadingListener imageLoadingListener) {
        String downUrl = getQiNiuDownLoadUrl(qiniu_url);
        ImageLoader.getInstance().displayImage(downUrl, view, options, imageLoadingListener);
    }

    public static String getVideoThumbnailUrl(String qiniu_url){
        String downUrl = getSignDownLoadUrl(QINIU_PIC_HOST + qiniu_url.replaceAll(QINIU_SPACE + "_", "")+"?vframe/png/offset/0/w/480/h/320");
        Log.d(QiniuHelper.class.getSimpleName(), "downUrl : " + downUrl);
        picUrlMap.put(qiniu_url, downUrl);
        return downUrl;
    }

    public static String getQiNiuDownLoadUrl(String qiniu_url) {
//        int viewWidth = 0;
//        int viewHeight = 0;

//        if (view != null) {
//            viewWidth = view.getMeasuredWidth();
//            viewHeight = view.getMeasuredHeight();
//        }

//        if (viewWidth > 0 && viewHeight > 0) {
//            qiniu_url += "?imageView2/1/w/" + viewWidth + "/h/" + viewHeight;
//        }
        String downUrl = getSignDownLoadUrl(QINIU_PIC_HOST + qiniu_url.replaceAll(QINIU_SPACE + "_", ""));
        Log.d(QiniuHelper.class.getSimpleName(), "downUrl : " + downUrl);
        picUrlMap.put(qiniu_url, downUrl);
        return downUrl;
    }


    public static void bindLocalImage(int drawableId, ImageView view) {
        ImageLoader.getInstance().displayImage("drawable://" + drawableId, view, options);
    }

    public static void bindBlurImage(String qiniu_url, final ImageView imageView) {
        if (TextUtils.isEmpty(qiniu_url)) {
            return;
        }
        String downUrl = getQiNiuDownLoadUrl(qiniu_url);
        ImageLoader.getInstance().loadImage(downUrl, options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                float radius = 50;
                Log.i("xianrui", "onLoadingComplete getWidth" + imageView.getMeasuredWidth() + " getHeight" + imageView.getMeasuredHeight());
                long time = System.currentTimeMillis();
//                Bitmap smallBitmap = Bitmap.createScaledBitmap(loadedImage, imageView.getMeasuredWidth(), imageView.getMeasuredHeight(), true);
                Log.i("xianrui", "1 time : " + ((float) (System.currentTimeMillis() - time) / 1000));
                time = System.currentTimeMillis();
                FastBlur.doBlur(loadedImage, imageView);
                Log.i("xianrui", "2 time : " + ((float) (System.currentTimeMillis() - time) / 1000));
//                imageView.setImageBitmap(smallBitmap);
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

    }

    public static String getPicCacheUrl(String qiniu_url) {
        if (picUrlMap != null) {
            String url = picUrlMap.get(qiniu_url);
            return url;
        }
        return "";
    }

    public static String saveShareBitmap(Bitmap bitmap) {
        String localPath = Environment.getExternalStorageDirectory()
                + "/fitGroup/Image/";
        String localFile = localPath + getNowTimeAccurate() + ".jpg";
        File f = new File(localPath);
        if (!f.exists()) {
            f.mkdirs();
        }
        File imageFile = new File(localFile);
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return localFile;
    }

    public static String getNowTimeAccurate() {

        SimpleDateFormat sdFormat = new SimpleDateFormat("yyyyMMddhhmmssSSS");
        String time = sdFormat.format(new Date());

        return time;
    }
}

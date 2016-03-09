package com.gezhii.fitgroup.tools;

import android.content.Context;
import android.widget.Toast;

import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.R;
import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.onekeyshare.ShareContentCustomizeCallback;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by xianrui on 15/6/9.
 */
public class ShareHelper {


    public static void shareWechatMoment(Context context, String shareUrl, File image) {
        share(context, WechatMoments.NAME, shareUrl, image);
    }

    public static void shareWechatMoment(Context context, ShareParams shareParams) {
        share(context, WechatMoments.NAME, shareParams);
    }

    public static void shareWechat(Context context, String shareUrl, File image) {
        share(context, Wechat.NAME, shareUrl, image);
    }

    public static void shareWechat(Context context, ShareParams shareParams) {
        share(context, Wechat.NAME, shareParams);
    }

    public static void shareSina(Context context, ShareParams shareParams) {
        share(context, SinaWeibo.NAME, shareParams);
    }

    public static void shareQQ(Context context, ShareParams shareParams) {
        share(context, QQ.NAME, shareParams);
    }

    public static void share(Context context, String platform, final String shareUrl, File image) {
        ShareSDK.initSDK(context);
        final OnekeyShare oks = new OnekeyShare();

//        File file = new File(FileUtils.getImagePath("icon" + ".jpg"));
//        BitmapUtil.saveBitmap(file, BitmapUtil.drawableToBitmap(context.getResources().getDrawable(R.drawable.ic_launcher)));

        oks.setTitle(MyApplication.getApplication().getResources().getString(R.string.app_name));
        oks.setText(MyApplication.getApplication().getResources().getString(R.string.share_app_text));
        oks.setImagePath(image.getAbsolutePath());
        oks.setTitleUrl(shareUrl);
        oks.setUrl(shareUrl);
        oks.setSiteUrl(shareUrl);
        oks.setSite(MyApplication.getApplication().getResources().getString(R.string.app_name));

        oks.setDialogMode();
        oks.disableSSOWhenAuthorize();
        if (platform != null) {
            oks.setPlatform(platform);
        }
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(cn.sharesdk.framework.Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {

            }
        });
        oks.show(context);
    }

    public static void share(final Context context, String platform, ShareParams shareParams) {
        ShareSDK.initSDK(context);
        final OnekeyShare oks = new OnekeyShare();
        if (shareParams.getTitle() != null) {
            oks.setTitle(shareParams.getTitle());
        }
        if (shareParams.getText() != null) {
            oks.setText(shareParams.getText());
        }
        if (shareParams.getImagePath() != null) {
            oks.setImagePath(shareParams.getImagePath());
        }
        if (shareParams.getShareUrl() != null) {
            oks.setTitleUrl(shareParams.getShareUrl());
            oks.setUrl(shareParams.getShareUrl());
            oks.setSiteUrl(shareParams.getShareUrl());
        }
        oks.setSite(MyApplication.getApplication().getResources().getString(R.string.app_name));

        oks.setDialogMode();
        oks.disableSSOWhenAuthorize();
        if (platform != null) {
            oks.setPlatform(platform);
        }
        oks.setShareContentCustomizeCallback(new ShareContentCustomizeCallback() {
            @Override
            public void onShare(cn.sharesdk.framework.Platform platform, cn.sharesdk.framework.Platform.ShareParams paramsToShare) {

            }
        });
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                MobclickAgent.onEvent(context, "signin_edit", UmengEvents.getEventMap("share", "sucess"));
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                MobclickAgent.onEvent(context, "signin_edit", UmengEvents.getEventMap("share", "share fail"));
            }

            @Override
            public void onCancel(Platform platform, int i) {

            }
        });
        oks.show(context);
    }

    public static class ShareParams {
        String text;//待分享内容
        String imagePath;//待分享的本地图片。如果目标平台使用客户端分享，此路径不可以在/data/data下面
        String filePath;//待分享的文件路径。这个用在Dropbox和Wechat中
        String title;//分享内容的标题
        String shareUrl;

        public String getImagePath() {
            return imagePath;
        }

        public void setImagePath(String imagePath) {
            this.imagePath = imagePath;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }

        public String getShareUrl() {
            return shareUrl;
        }

        public void setShareUrl(String shareUrl) {
            this.shareUrl = shareUrl;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }

    public static String getString(int id) {
        return MyApplication.getApplication().getString(id);
    }

    public static void showToast(String msg) {
        Toast.makeText(MyApplication.getApplication(), msg, Toast.LENGTH_SHORT).show();
    }


}

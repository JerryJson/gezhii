package com.gezhii.fitgroup.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.tools.ImageFormat;
import com.gezhii.fitgroup.tools.ShareHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.gezhii.fitgroup.ui.fragment.signin.SignCardDetailFragment;
import com.gezhii.fitgroup.ui.fragment.signin.SignCardShareFragment;
import com.xianrui.lite_common.litesuits.android.log.Log;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by fantasy on 15/11/24.
 */
public class ShareToThirdDialog extends Dialog {
    SignCardDetailFragment signCardDetailFragment;
    Activity mContext;
    @InjectView(R.id.share_to)
    TextView shareTo;
    @InjectView(R.id.wechat)
    TextView wechat;
    @InjectView(R.id.wechat_moment)
    TextView wechatMoment;
    @InjectView(R.id.weibo)
    TextView weibo;
    @InjectView(R.id.qq)
    TextView qq;
    @InjectView(R.id.cancel)
    TextView cancel;

    int mSignId = -1;
    String mHuanxinId;
    Signin signin;


    public ShareToThirdDialog(Activity context, int theme) {
        super(context, theme);
        this.mContext = context;
    }

    public ShareToThirdDialog(Activity context, SignCardDetailFragment signCardDetailFragment) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.signCardDetailFragment = signCardDetailFragment;
    }

    public ShareToThirdDialog(Activity context, int sign_id, String huanxin_id) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.mSignId = sign_id;
        this.mHuanxinId = huanxin_id;
    }

    public ShareToThirdDialog(Activity context, Signin signin) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar);
        this.signin = signin;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_to_third_dialog_ui);
        ButterKnife.inject(this);
        wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.util.Log.i("ycl", "share sign_id = " + mSignId);

                if (signCardDetailFragment == null) {
                    Log.i("mSignId", mSignId);
                    Log.i("mHuanxinId", mHuanxinId);
                    if (null == signin) {
                        //分享卡片来自打卡历史列表,所以新建一个Fragment,分享完该Fragment立即消失
                        SignCardShareFragment.start(mContext, mSignId, mHuanxinId, "tag_wechat");
                    } else {
                        SignCardShareFragment.start(mContext, signin, "tag_wechat");
                    }
                } else {
                    //分享卡片来自打卡详情页面,所以直接截屏.截屏完再恢复
                    signCardDetailFragment.shareBottomLayout.setVisibility(View.VISIBLE);
                    signCardDetailFragment.shareBtnLayout.setVisibility(View.GONE);

                    Bitmap bitmap = ImageFormat.getViewCache(signCardDetailFragment.shareToThirdLayout);
                    String filePath = QiniuHelper.saveShareBitmap(bitmap);
                    ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                    shareParams.setImagePath(filePath);
                    ShareHelper.shareWechat(getContext(), shareParams);

                    signCardDetailFragment.shareBottomLayout.setVisibility(View.GONE);
                    signCardDetailFragment.shareBtnLayout.setVisibility(View.VISIBLE);
                }
                dismiss();
            }
        });
        wechatMoment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signCardDetailFragment == null) {
                    Log.i("mSignId", mSignId);
                    Log.i("mHuanxinId", mHuanxinId);
                    if (null == signin) {
                        SignCardShareFragment.start(mContext, mSignId, mHuanxinId, "tag_wechat_moment");
                    } else {
                        SignCardShareFragment.start(mContext, signin, "tag_wechat_moment");
                    }
                } else {
                    signCardDetailFragment.shareBottomLayout.setVisibility(View.VISIBLE);
                    Bitmap bitmap = ImageFormat.getViewCache(signCardDetailFragment.shareToThirdLayout);
                    String filePath = QiniuHelper.saveShareBitmap(bitmap);
                    ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                    shareParams.setImagePath(filePath);
                    ShareHelper.shareWechatMoment(getContext(), shareParams);
                    signCardDetailFragment.shareBottomLayout.setVisibility(View.INVISIBLE);
                }
                dismiss();
            }
        });
        weibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signCardDetailFragment == null) {
                    Log.i("mSignId", mSignId);
                    Log.i("mHuanxinId", mHuanxinId);
                    if (null == signin) {
                        SignCardShareFragment.start(mContext, mSignId, mHuanxinId, "tag_sina_weibo");
                    } else {
                        SignCardShareFragment.start(mContext, signin, "tag_sina_weibo");
                    }
                } else {
                    signCardDetailFragment.shareBottomLayout.setVisibility(View.VISIBLE);
                    Bitmap bitmap = ImageFormat.getViewCache(signCardDetailFragment.shareToThirdLayout);
                    String filePath = QiniuHelper.saveShareBitmap(bitmap);
                    ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                    shareParams.setImagePath(filePath);
                    shareParams.setText("运动咖 http://dl.qing.am");
                    ShareHelper.shareSina(getContext(), shareParams);
                    signCardDetailFragment.shareBottomLayout.setVisibility(View.INVISIBLE);
                }
                dismiss();
            }
        });
        qq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (signCardDetailFragment == null) {
                    Log.i("mSignId", mSignId);
                    Log.i("mHuanxinId", mHuanxinId);
                    if (null == signin) {
                        SignCardShareFragment.start(mContext, mSignId, mHuanxinId, "tag_qq");
                    } else {
                        SignCardShareFragment.start(mContext, signin, "tag_qq");
                    }
                } else {
                    signCardDetailFragment.shareBottomLayout.setVisibility(View.VISIBLE);
                    Bitmap bitmap = ImageFormat.getViewCache(signCardDetailFragment.shareToThirdLayout);
                    String filePath = QiniuHelper.saveShareBitmap(bitmap);
                    ShareHelper.ShareParams shareParams = new ShareHelper.ShareParams();
                    shareParams.setImagePath(filePath);
                    ShareHelper.shareQQ(getContext(), shareParams);
                    signCardDetailFragment.shareBottomLayout.setVisibility(View.INVISIBLE);
                }
                dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

}

package com.gezhii.fitgroup.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/9/26.
 */
public class BigPhotoDialog extends Dialog {
    @InjectView(R.id.big_photo)
    ImageView bigPhoto;

    String remoteUrl;
    String localUrl;
    String qiniuUrl;

    public BigPhotoDialog(Context context) {
        this(context, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public BigPhotoDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.big_photo_dialog_ui);
        ButterKnife.inject(this);
        bigPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public void show() {
        super.show();
        if (!TextUtils.isEmpty(remoteUrl)) {
            QiniuHelper.bindImageByUrl(remoteUrl, bigPhoto);
        }
        if (!TextUtils.isEmpty(localUrl)) {
            QiniuHelper.bindLocalImage(localUrl, bigPhoto);
        }
        if (!TextUtils.isEmpty(qiniuUrl)) {
            QiniuHelper.bindImage(qiniuUrl, bigPhoto);
        }
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public String getLocalUrl() {
        return localUrl;
    }

    public void setLocalUrl(String localUrl) {
        remoteUrl = "";
        qiniuUrl = "";
        this.localUrl = localUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        localUrl = "";
        qiniuUrl = "";
        this.remoteUrl = remoteUrl;
    }

    public String getQiniuUrl() {
        return qiniuUrl;
    }

    public void setQiniuUrl(String qiniuUrl) {
        remoteUrl = "";
        localUrl = "";
        this.qiniuUrl = qiniuUrl;
    }
}

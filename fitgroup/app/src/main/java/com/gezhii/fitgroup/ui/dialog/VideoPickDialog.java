package com.gezhii.fitgroup.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.ResInformationDto;
import com.xianrui.lite_common.litesuits.common.io.FileUtils;
import com.xianrui.lite_common.litesuits.common.utils.FileUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;

public class VideoPickDialog extends Dialog {

    public static final int RETURN_VIDEO = 1;
    public static final String TAG_CACHE_VIDEO_FILE_NAME = "tag_cache_video_file_name";

    Fragment mFragment;
    Activity mActivity;
    public TextView fromPhoto;
    public TextView cancel;
    Callback callback;


    public VideoPickDialog(Fragment fragment, int theme) {
        super(fragment.getActivity(), theme);
        this.mFragment = fragment;
    }

    public VideoPickDialog(Fragment fragment) {
        this(fragment, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public VideoPickDialog(Activity activity, int theme) {
        super(activity, theme);
        this.mActivity = activity;
    }

    public VideoPickDialog(Activity activity,Fragment fragment) {
        this(activity, android.R.style.Theme_Translucent_NoTitleBar);
        this.mFragment = fragment;
        this.mActivity = activity;
    }

    public VideoPickDialog(Activity activity) {
        this(activity, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public Callback getCallback() {
        return callback;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i("xianrui", "onActivityResult  " + " requestCode : " + requestCode + " resultCode : " + resultCode);
        switch (requestCode) {
            case RETURN_VIDEO:
                onPhotoRequest(resultCode, data);
                break;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_pick_dialog_ui);
        fromPhoto = (TextView) findViewById(R.id.from_photo);
        cancel = (TextView) findViewById(R.id.cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        //打开系统相册读取视频
        fromPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");//视频类型
                if (mFragment != null) {
                    mFragment.startActivityForResult(intent, RETURN_VIDEO);
                } else {
                    mActivity.startActivityForResult(intent, RETURN_VIDEO);
                }

                dismiss();
            }
        });

    }


    //从视频相册返回
    public void onPhotoRequest(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String filePath = FileUtils.getRealFilePath(mActivity,uri);

            Log.i("logger","filepath:"+filePath);
            ResInformationDto resInformationDto = new ResInformationDto();
            resInformationDto.setFilePath(filePath);
            resInformationDto.setDuration(MediaPlayer.create(mActivity, uri).getDuration() / 1000);

            if (getCallback() != null) {
                callback.onImageBack(resInformationDto);
            }

        }
    }

//    public Bitmap getBitmap(String imgPath) {
//
//        Bitmap bp = ThumbnailUtils.createVideoThumbnail(imgPath,
//                MediaStore.Video.Thumbnails.MINI_KIND);
//        return bp;
//
//    }

    public interface Callback {
        void onImageBack(ResInformationDto resInformationDto);
    }


}

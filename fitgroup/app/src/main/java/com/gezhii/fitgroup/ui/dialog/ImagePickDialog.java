package com.gezhii.fitgroup.ui.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.dto.ResInformationDto;
import com.gezhii.fitgroup.model.UserModel;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.ImageFormat;
import com.xianrui.lite_common.litesuits.android.log.Log;
import com.xianrui.lite_common.litesuits.common.io.FileUtils;
import com.xianrui.lite_common.litesuits.common.utils.MD5Util;

import java.io.File;
import java.io.IOException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xianrui on 15/9/14.
 */
public class ImagePickDialog extends Dialog {

    public static final int RETURN_CAMERA = 1;
    public static final int RETURN_PHOTO = 2;
    public static final String TAG_CACHE_CAMERA_FILE_NAME = "tag_cache_camera_file_name";

    Fragment mFragment;
    Activity mActivity;
    @InjectView(R.id.from_photo)
    public TextView fromPhoto;
    @InjectView(R.id.from_camera)
    public TextView fromCamera;
    @InjectView(R.id.cancel)
    TextView cancel;

    String mFileName;

    Callback callback;


    public ImagePickDialog(Fragment fragment, int theme) {
        super(fragment.getActivity(), theme);
        this.mFragment = fragment;
    }

    public ImagePickDialog(Fragment fragment) {
        this(fragment, android.R.style.Theme_Translucent_NoTitleBar);
    }

    public ImagePickDialog(Activity activity, int theme) {
        super(activity, theme);
        this.mActivity = activity;
    }

    public ImagePickDialog(Activity activity) {
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
            case RETURN_CAMERA:
                onCameraRequest(resultCode);
                break;
            case RETURN_PHOTO:
                onPhotoRequest(resultCode, data);
                break;
        }
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_pick_dialog_ui);
        ButterKnife.inject(this);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        //打开相册
        fromPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");//相片类型
                if (mFragment != null) {
                    mFragment.startActivityForResult(intent, RETURN_PHOTO);
                } else {
                    mActivity.startActivityForResult(intent, RETURN_PHOTO);
                }

                dismiss();
            }
        });

        //打开相机
        fromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileName = getFileName();
                DataKeeperHelper.getInstance().getDataKeeper().put(TAG_CACHE_CAMERA_FILE_NAME, mFileName);
                File mImage = new File(FileUtils.getImagePath(getContext(), mFileName));
                if (!mImage.exists()) {
                    File vDirPath = mImage.getParentFile();
                    if (vDirPath == null) {
                        return;
                    } else {
                        vDirPath.mkdirs();
                    }
                }
                Uri uri = Uri.fromFile(mImage);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                if (mFragment != null) {
                    mFragment.startActivityForResult(intent, RETURN_CAMERA);
                } else {
                    mActivity.startActivityForResult(intent, RETURN_CAMERA);
                }
                dismiss();
            }
        });
    }


    //从相册返回
    public void onPhotoRequest(int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            mFileName = getFileName();
            File mImage = new File(FileUtils.getImagePath(getContext(), mFileName));
            try {
                FileUtils.copyFile(new File(FileUtils.getRealFilePath(getContext(), uri)), mImage);
            } catch (IOException e) {
                e.printStackTrace();
            }
            ResInformationDto resInformationDto = new ResInformationDto();
            resInformationDto.setFileName(mFileName);
            resInformationDto.setFilePath(FileUtils.getImagePath(getContext(), mFileName));
            resInformationDto.initImageSize(true);
            resInformationDto.setType(ResInformationDto.TAG_TYPE_IMAGE);
            compressImage(FileUtils.getImagePath(getContext(), mFileName));

            if (getCallback() != null) {
                callback.onImageBack(resInformationDto);
            }

        }
    }

    //从相机返回
    public void onCameraRequest(int resultCode) {

        if (resultCode == Activity.RESULT_OK) {

            if (TextUtils.isEmpty(mFileName)) {
                mFileName = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_CACHE_CAMERA_FILE_NAME, "");
            }
            if (!TextUtils.isEmpty(mFileName)) {
//                showLoading();
//                new Thread(compressRunnable).start();
//                File mImage;
                Log.i("xianrui", "camera callback");
                ResInformationDto resInformationDto = new ResInformationDto();
                resInformationDto.setFileName(mFileName);
                resInformationDto.setFilePath(FileUtils.getImagePath(getContext(), mFileName));
                resInformationDto.initImageSize(true);
                resInformationDto.setType(ResInformationDto.TAG_TYPE_IMAGE);
//                mImage = new File(imageInformationDto.getFilePath());

//                int degree = ImageFormat.readPictureDegree(mImage.getAbsolutePath());
//                Bitmap toBitmap = ImageFormat.fileToBitmap(mImage);
//                if (isCameraModel)
//                    toBitmap = ImageFormat.rotaingImageView(degree, toBitmap);
//                ImageFormat.saveBitmap(mImage, toBitmap);
                compressImage(FileUtils.getImagePath(getContext(), mFileName));
                if (getCallback() != null) {
                    callback.onImageBack(resInformationDto);
                }
            }
        }
    }

    public static String getFileName() {
        String userID = "";
        if (UserModel.getInstance().isLogin()) {
            userID = String.valueOf(UserModel.getInstance().getUserId());
        }
        return MD5Util.md5(userID + System.currentTimeMillis()) + ".jpg";
    }

    public void compressImage(String mFileName) {
        long starttime = System.currentTimeMillis();
        ImageFormat.compressImage(mFileName);
        Log.i("xianrui", "4 time " + (System.currentTimeMillis() - starttime));
    }


    public interface Callback {
        public void onImageBack(ResInformationDto resInformationDto);
    }


}

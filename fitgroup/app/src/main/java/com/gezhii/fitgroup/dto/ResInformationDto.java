package com.gezhii.fitgroup.dto;

import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xianrui on 15/6/30.
 */
public class ResInformationDto {
    public static final String TAG_TYPE_IMAGE = "IMAGE";
    public static final String TAG_TYPE_SOUND = "SOUND";
    public static final String TAG_TYPE_TEXT = "TEXT";
    public static final String TAG_TYPE_VIDEO = "VIDEO";


    private String filePath;
    private String fileName;
    private int width;
    private int height;
    private boolean isPushed;
    private String remoteUrl;
    private String type;

    private int duration;

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRemoteUrl() {
        return remoteUrl;
    }

    public void setRemoteUrl(String remoteUrl) {
        this.remoteUrl = remoteUrl;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }


    public void initImageSize(boolean isScale) {
        if (!TextUtils.isEmpty(filePath)) {
            BitmapFactory.Options options = new BitmapFactory.Options();
//            if (isScale) {
//                int degree = ImageFormat.readPictureDegree(filePath);
//                Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
//                bitmap = ImageFormat.rotaingImageView(degree, bitmap);
//                double scale = Math.sqrt((double) (1000 * 1000) / (double) (bitmap.getWidth() * bitmap.getHeight()));
//                int widthN = (int) (bitmap.getWidth() * scale);
//                int heightN = (int) (bitmap.getHeight() * scale);
//
//                if (heightN > widthN * 2.5 && widthN > 320) {
//                    widthN = 320;
//                    scale = 320 / bitmap.getWidth();
//                    heightN = (int) (bitmap.getHeight() * scale);
//                }
//                Bitmap smallBitmap = Bitmap.createScaledBitmap(bitmap, widthN, heightN, true);
//                ImageFormat.saveBitmap(new File(filePath), smallBitmap);
//            }
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            this.height = options.outHeight;
            this.width = options.outWidth;
        }
    }


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isPushed() {
        return isPushed;
    }

    public void setIsPushed(boolean isPushed) {
        this.isPushed = isPushed;
    }

    public String getPushFileName() {
        String name = "";
        if (!TextUtils.isEmpty(fileName) && width > 0 && height > 0) {
            name = fileName + "-WH-" + width + "-WH-" + height;
        }
        return name;
    }

    public static ResInformationDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (ResInformationDto) gson.fromJson(jsonString, new TypeToken<ResInformationDto>() {
        }.getType());
    }
}

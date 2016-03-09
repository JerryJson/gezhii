package com.gezhii.fitgroup.tools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Picture;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.xianrui.lite_common.litesuits.android.log.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ImageFormat {

    public static BitmapFactory.Options getOptions() {

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        return options;

    }

    public static Drawable filePathToDrawable(String umdFilepath) {
        try {
            File fileboot = new File(umdFilepath);
            if (fileboot.exists()) {
                return new BitmapDrawable(fileboot.getPath());
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static Bitmap filePathToBitmap(String umdFilepath) {
        try {
            File fileboot = new File(umdFilepath);
            if (fileboot.exists()) {
                return BitmapFactory.decodeFile(fileboot.getPath(),
                        getOptions());
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static Bitmap fileToBitmap(File fileboot) {
        try {
            if (fileboot.exists()) {
                return BitmapFactory.decodeFile(fileboot.getPath(),
                        getOptions());
            }
        } catch (Exception e) {

        }
        return null;
    }

    public static Bitmap bitmapToIcon(Context context, Bitmap bmp) {

        int bmpWidth = bmp.getWidth();
        int bmpHeight = bmp.getHeight();

        final Resources r = context.getResources();
        int base = (int) r.getDimension(android.R.dimen.app_icon_size);// 获得当前系统快捷方式的图片的限制大小;
        int move_x = 5;
        int move_y = 0;
        int scaleWidth = base - 10;
        int scaleHeight = base;
        if (bmpWidth > bmpHeight) {
            scaleWidth = base;
            scaleHeight = base - 10;
            move_x = 0;
            move_y = 5;
        }

        // // if (bmpWidth > bmpHeight) {
        // // scaleWidth = bmpWidth * scaleWidth / bmpWidth;
        // // scaleHeight = bmpHeight * scaleHeight / bmpWidth;
        // // } else {
        // // scaleWidth = bmpWidth * scaleWidth / bmpHeight;
        // // scaleHeight = bmpHeight * scaleHeight / bmpHeight;
        // // }
        // /* 产生reSize后的Bitmap对象 */
        // Matrix matrix = new Matrix();
        // matrix.postScale(scaleWidth / bmpWidth, scaleHeight / bmpHeight);
        // Bitmap resizeBmp = Bitmap.createBitmap(bmp, 0, 0, bmpWidth,
        // bmpHeight,
        // matrix, true);
        //
        // Bitmap newb = Bitmap.createBitmap(base, base,
        // Bitmap.Config.ARGB_8888);//
        // // 创建一个新的和SRC长度宽度一样的位图
        // Canvas canvas = new Canvas(newb);
        // canvas.drawBitmap(resizeBmp, move_x, move_y, null);// 在 0，0坐标开始画入src
        // canvas.save(Canvas.ALL_SAVE_FLAG);// 保存
        // canvas.restore();// 存储

        Bitmap newb = Bitmap.createBitmap(base, base, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(newb);
        // Copy in the photo
        Paint photoPaint = new Paint(); // 建立画笔
        photoPaint.setDither(true); // 获取跟清晰的图像采样
        photoPaint.setFilterBitmap(true);// 过滤一些
        Rect src = new Rect(0, 0, bmp.getWidth(), bmp.getHeight());// 创建一个指定的新矩形的坐标
        Rect dst = new Rect(move_x, move_y, scaleWidth + move_x, scaleHeight
                + move_y);// 创建一个指定的新矩形的坐标
        canvas.drawBitmap(bmp, src, dst, photoPaint);// 将photo 缩放或则扩大到

        return newb;

    }

    public static Bitmap drawableToIconBitmap(Context context, Drawable drawable) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        final Resources r = context.getResources();
        int base = (int) r.getDimension(android.R.dimen.app_icon_size);// 获得当前系统快捷方式的图片的限制大小;
        int move_x = 0;
        int move_y = 0;
        int width = base;
        int height = base;
        if (drawable.getIntrinsicWidth() > drawable.getIntrinsicHeight()) {
            height = base * drawable.getIntrinsicHeight()
                    / drawable.getIntrinsicWidth();
            move_y = (base - height) / 2;
            height = height + move_y;
        } else {
            width = base * drawable.getIntrinsicWidth()
                    / drawable.getIntrinsicHeight();
            move_x = (base - width) / 2;
            width = width + move_x;
        }
        drawable.setBounds(move_x, move_y, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap resourceToIconBitmap(Context context, int id) {
        Drawable drawable = context.getResources().getDrawable(id);
        return drawableToIconBitmap(context, drawable);
    }

    public static Bitmap drawableToBitmap(Context context, Drawable drawable,
                                          int width_value, int height_value) {
        Bitmap bitmap = Bitmap
                .createBitmap(
                        drawable.getIntrinsicWidth(),
                        drawable.getIntrinsicHeight(),
                        drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                                : Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        int move_x = 0;
        int move_y = 0;
        int width = width_value;
        int height = height_value;
        if (drawable.getIntrinsicWidth() > drawable.getIntrinsicHeight()) {
            height = height_value * drawable.getIntrinsicHeight()
                    / drawable.getIntrinsicWidth();
            move_y = (height_value - height) / 2;
            height = height + move_y;
        } else {
            width = width_value * drawable.getIntrinsicWidth()
                    / drawable.getIntrinsicHeight();
            move_x = (width_value - width) / 2;
            width = width + move_x;
        }
        drawable.setBounds(move_x, move_y, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

    public static Bitmap resourceToBitmap(Context context, int id,
                                          int width_value, int height_value) {
        Drawable drawable = context.getResources().getDrawable(id);
        return drawableToBitmap(context, drawable, width_value, height_value);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth(); // 取drawable的长宽
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                : Bitmap.Config.RGB_565; // 取drawable的颜色格式
        Bitmap bitmap = Bitmap.createBitmap(width, height, config); // 建立对应bitmap
        Canvas canvas = new Canvas(bitmap); // 建立对应bitmap的画布
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas); // 把drawable内容画到画布中
        return bitmap;
    }

    /**
     * 不同等比缩放
     */
    public static BitmapDrawable zoomDrawable(Context mContext,
                                              Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);// drawable转换成bitmap
        Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
        float scaleWidth = ((float) w / width); // 计算缩放比例
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight); // 设置缩放比例
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        BitmapDrawable d = new BitmapDrawable(newbmp);
        d.setTargetDensity(mContext.getResources().getDisplayMetrics());
        return d; // 把bitmap转换成drawable并返回
    }

    /**
     * 同等比缩放
     */
    public static BitmapDrawable zoomSameDrawable(Context mContext,
                                                  Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);// drawable转换成bitmap
        Matrix matrix = new Matrix(); // 创建操作图片用的Matrix对象
        float scaleWidth = ((float) w / width); // 计算缩放比例
        float scaleHeight = ((float) h / height);
        float scale = scaleWidth;
        if (scaleWidth > scaleHeight) {
            scale = scaleHeight;
        }
        int xSplace = (int) ((scaleWidth - scale) * width);
        int ySplace = (int) ((scaleHeight - scale) * height);
        matrix.postScale(scale, scale); // 设置缩放比例
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,
                matrix, true); // 建立新的bitmap，其内容是对原bitmap的缩放后的图
        BitmapDrawable d = new BitmapDrawable(mContext.getResources(), newbmp);
        d.setTargetDensity(mContext.getResources().getDisplayMetrics());
        return d; // 把bitmap转换成drawable并返回
    }

    public static Bitmap resourceToBitmap(Context context, int id) {
        return BitmapFactory.decodeResource(context.getResources(), id);
    }

    public static Bitmap byteToBitmap(byte[] data) {

        return BitmapFactory
                .decodeByteArray(data, 0, data.length, getOptions());
    }

    public static Drawable bitmapToDrawable(Context context, Bitmap bitmap) {
        Drawable drawable = new BitmapDrawable(context.getResources(), bitmap);
        return drawable;
    }

    /**
     * 获取view当前的屏幕
     */
    public static Bitmap getViewCache(View v) {
        if (v == null) {
            return null;
        }
        if (v instanceof ScrollView) {
            return getViewCache((ScrollView) v);
        } else if (v instanceof WebView) {
            return getViewCache((WebView) v);
        } else if (v instanceof ListView) {
            return getViewCache((ListView) v);
        } else if (v instanceof ImageView) {
            return getViewCache((ImageView) v);
        }

//        v.setDrawingCacheEnabled(true);
//        v.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
//                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());


        Bitmap screenshot;
        screenshot = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(screenshot);
        c.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(c);

//        Context context = MyApplication.getApplication();
//
//        LinearLayout layout = new LinearLayout(context);
//
//        TextView textView = new TextView(context);
//        textView.setVisibility(View.VISIBLE);
//        textView.setText("Hello world");
//        layout.addView(textView);
//
//        layout.measure(c.getWidth(), c.getHeight());
//        layout.layout(0, 0, c.getWidth(), c.getHeight());
//
//// To place the text view somewhere specific:
////canvas.translate(0, 0);
//
//        layout.draw(c);


        return screenshot;

    }

    /**
     * 获取ImageView的裁图
     */
    public static Bitmap getViewCache(ImageView mImageView) {
        return ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
    }

    /**
     * 获取ListView的裁图
     */
    public static Bitmap getViewCache(ListView mListView) {

        if (mListView == null)
            return null;

        int h = 0;
        for (int i = 0; i < mListView.getChildCount(); i++) {
            h += mListView.getChildAt(i).getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(mListView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        // Bitmap bitmap = scrollView.getDrawingCache(true);
        final Canvas c = new Canvas(bitmap);
        mListView.draw(c);

        // ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        // final byte[] picture = stream.toByteArray();
        return bitmap;
    }

    /**
     * 获取ScrollView的裁图
     */
    public static Bitmap getViewCache(ScrollView mScrollView) {

        if (mScrollView == null)
            return null;

        int h = 0;
        for (int i = 0; i < mScrollView.getChildCount(); i++) {
            h += mScrollView.getChildAt(i).getHeight();
        }
        Bitmap bitmap = Bitmap.createBitmap(mScrollView.getWidth(), h,
                Bitmap.Config.ARGB_8888);
        // Bitmap bitmap = scrollView.getDrawingCache(true);
        final Canvas c = new Canvas(bitmap);
        mScrollView.draw(c);

        // ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        // final byte[] picture = stream.toByteArray();
        return bitmap;
    }

    /**
     * 获取WebView的裁图
     */
    public static Bitmap getViewCache(WebView v) {

        if (v == null)
            return null;

        Picture picture = v.capturePicture();
        int width = picture.getWidth();
        int height = picture.getHeight();
        if (width > 0 && height > 0) {
            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inSampleSize = 2; // 将图片设为原来宽高的1/2，防止内存溢出
            Bitmap bmp = Bitmap.createBitmap(width, height,
                    Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bmp);
            picture.draw(canvas);
            return bmp;
        }
        return null;
    }

    // 转换多媒体控件
    public static Bitmap getBitmapForObject(Context mContext, Object mImage) {

        if (mImage instanceof File) {
            // 文件方式
            return fileToBitmap((File) mImage);

        } else if (mImage instanceof String) {
            String mPath = (String) mImage;
            if (mPath.toLowerCase().indexOf("http://") != -1) {
                // 链接
                return null;
            } else {
                // 绝对路径方式
                return filePathToBitmap((String) mImage);
            }

        } else if (mImage instanceof Integer) {
            // 本地资源方式
            return resourceToBitmap(mContext, (Integer) mImage);

        } else if (mImage instanceof byte[]) {
            // 资源方式
            return byteToBitmap((byte[]) mImage);

        } else if (mImage instanceof Bitmap) {
            // 图片方式
            return (Bitmap) mImage;

        } else if (mImage instanceof Drawable) {
            // 图片方式
            BitmapDrawable bd = (BitmapDrawable) mImage;
            Bitmap bm = bd.getBitmap();
            return bm;

        } else if (mImage instanceof View) {
            // 控件方式
            return getViewCache((View) mImage);

        }

        return null;
    }

    /**
     * 获取长和宽放大缩小的比例图片
     */
    public static Bitmap getScaleBitmap(Bitmap bitmap, float mScale) {
        Matrix matrix = new Matrix();
        matrix.postScale(mScale, mScale); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 保存图片
     */
    public static void saveBitmap(File f, Bitmap mBitmap) {
        try {
            f.createNewFile();
            FileOutputStream fOut = null;
            fOut = new FileOutputStream(f);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (IOException e) {

        }
    }

    /**
     * 压缩图片
     */
    public static void compressBmpToFile(Bitmap bmp, File file) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int options = 100;
        bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        while (baos.toByteArray().length / 1024 > 200) {
            baos.reset();
            options -= 10;
            if (options <= 0) {
                break;
            }
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(baos.toByteArray());
            fos.flush();
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

        Matrix m = new Matrix();
        m.setRotate(orientationDegree, (float) bm.getWidth() / 2, (float) bm.getHeight() / 2);
        float targetX, targetY;
        if (orientationDegree == 90) {
            targetX = bm.getHeight();
            targetY = 0;
        } else {
            targetX = bm.getHeight();
            targetY = bm.getWidth();
        }
        final float[] values = new float[9];
        m.getValues(values);

        float x1 = values[Matrix.MTRANS_X];
        float y1 = values[Matrix.MTRANS_Y];

        m.postTranslate(targetX - x1, targetY - y1);

        Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(), Bitmap.Config.ARGB_8888);
        Paint paint = new Paint();
        Canvas canvas = new Canvas(bm1);
        canvas.drawBitmap(bm, m, paint);

        return bm1;
    }


    public static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }


    public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
        //旋转图片 动作
        Matrix matrix = new Matrix();
        ;
        matrix.postRotate(angle);
        // 创建新的图片
        Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizedBitmap;
    }

    public static String compressImage(String filePath) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(filePath, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = Screen.getScreenHeight();
        float maxWidth = Screen.getScreenWidth();
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

        int inSampleSize = 1;

        if (actualWidth > Screen.getScreenWidth() / 4) {
            final int halfWidth = actualWidth / 2;
            while (halfWidth / inSampleSize > Screen.getScreenWidth() / 4) {
                inSampleSize *= 2;
            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = inSampleSize;

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(filePath, options);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();

        }

        int canvasW = actualWidth - 2;
        int canvasH = actualHeight - 2;

        try {
            scaledBitmap = Bitmap.createBitmap(canvasW, canvasH, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            exception.printStackTrace();
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = canvasW / 2.0f;
        float middleY = canvasH / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(filePath);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);

//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return filePath;

    }


}

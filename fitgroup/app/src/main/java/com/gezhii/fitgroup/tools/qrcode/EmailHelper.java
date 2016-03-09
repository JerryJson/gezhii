package com.gezhii.fitgroup.tools.qrcode;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.gezhii.fitgroup.R;


/**
 * Created by xianrui on 15/5/25.
 */
public class EmailHelper {

    public static void sendEmail(Context context, String title) {
        Intent data = new Intent(Intent.ACTION_SENDTO);
        data.setData(Uri.parse(context.getString(R.string.feedback_email)));
        data.putExtra(Intent.EXTRA_SUBJECT, title);
        data.putExtra(Intent.EXTRA_TEXT, getBody(context));
        context.startActivity(data);
    }

    private static String getBody(Context context) {
        String versionName = null;
        try {
            String pkName = context.getPackageName();
            versionName = context.getPackageManager().getPackageInfo(pkName, 0).versionName;
        } catch (Exception e) {

        }
        String body = "\n\n\n\n\n";
        body += "手机型号:" + Build.MANUFACTURER + " " + Build.MODEL + "\n";
        body += "版本:" + versionName + "\n";
        body += "系统版本:" + Build.VERSION.RELEASE + "\n";
        return body;
    }


}

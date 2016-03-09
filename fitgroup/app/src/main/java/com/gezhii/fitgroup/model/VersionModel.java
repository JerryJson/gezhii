package com.gezhii.fitgroup.model;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.R;
import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.VersionDto;
import com.gezhii.fitgroup.tools.SystemUtils;
import com.gezhii.fitgroup.ui.view.AlertHelper;

/**
 * Created by xianrui on 15/8/26.
 */
public class VersionModel {

    public String version = SystemUtils.getAppVersion(MyApplication.getApplication());
    public String lastVersion = null;
    public String minimumVersion = null;
    public String apkUrl = null;
    public boolean hintUpdate = false;
    public String hintString = null;

    private static class VersionModelHolder {
        public final static VersionModel sington = new VersionModel();
    }

    private VersionModel() {

    }

    public static VersionModel getInstance() {
        return VersionModelHolder.sington;
    }

    public void getVersion() {
        getVersion(null);
    }


    public void getVersion(final Context context) {

        API.checkAndroidVersion(new APICallBack() {
            @Override
            public void subRequestSuccess(String response) throws NoSuchFieldException {
//                JSONObject jsonObject = null;
//                try {
//                    jsonObject = new JSONObject(response);
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//                if (jsonObject != null) {
//                    Log.d("xianrui", "updateMessage " + jsonObject);
//                    try {
////                        lastVersion = jsonObject.getString("lastVersion");
//                        lastVersion = "1.1";
//                        minimumVersion = jsonObject.getString("minimumVersion");
//                        apkUrl = jsonObject.getString("apkUrl");
//                        hintUpdate = jsonObject.getBoolean("hintUpdate");
//                        hintString = jsonObject.getString("hintString");
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                    if (context != null) {
//                        checkVersion(context);
//                    }
//                }
                VersionDto versionDto = VersionDto.parserJson(response);

                if (versionDto != null) {
                    lastVersion = versionDto.getVersion().getLastVersion();
                    minimumVersion = versionDto.getVersion().getMinimumVersion();
                    apkUrl = versionDto.getVersion().getApkUrl();
                    hintUpdate = versionDto.getVersion().isHintUpdate();
                    hintString = versionDto.getVersion().getHintString();
                }
                if (context != null) {
                    checkVersion(context);
                }
            }
        });
    }

    private void checkVersion(final Context context) {
        if (!TextUtils.isEmpty(version) && !TextUtils.isEmpty(minimumVersion)) {
            if (SystemUtils.compareVersion(minimumVersion, version) > 0) {
                AlertHelper.AlertParams params = new AlertHelper.AlertParams();
                params.setTitle(getString(R.string.forced_update_hint));
                params.setConfirmString(getString(R.string.download));
                params.setConfirmListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downLoadApp(context);
                        MyApplication.getApplication().finish();
                    }
                });
                params.setCancelString(getString(R.string.exit));
                params.setCancelListener(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MyApplication.getApplication().finish();
                    }
                });
                params.setIsBlock(true);
                AlertHelper.showAlert(context, params);
                return;
            }
        }

        if (!TextUtils.isEmpty(version) && !TextUtils.isEmpty(lastVersion)) {
            if (SystemUtils.compareVersion(version, lastVersion) < 0) {
                if (hintUpdate) {
                    AlertHelper.AlertParams params = new AlertHelper.AlertParams();
                    params.setTitle(hintString);
                    params.setConfirmString(getString(R.string.download));
                    final String finalApkUrl = apkUrl;
                    params.setConfirmListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            downLoadApp(context);
                        }
                    });
                    params.setCancelString(getString(R.string.cancel));
                    params.setCancelListener(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertHelper.showAlert(context, params);
                }
            }
        }
    }

    public boolean hasNewVersion() {
        if (TextUtils.isEmpty(lastVersion)) {
            getVersion();
            return false;
        } else
            return SystemUtils.compareVersion(version, lastVersion) < 0;
    }

    public String getString(int id) {
        return MyApplication.getApplication().getString(id);
    }

    //下载新版本的 App；

    public void downLoadApp(Context context) {
        Uri uri = Uri.parse(apkUrl);
        Intent downloadIntent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(downloadIntent);
    }

}

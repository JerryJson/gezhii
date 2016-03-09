package com.gezhii.fitgroup.api;

import android.widget.Toast;

import com.android.volley.VolleyError;
import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.event.CloseLoadingEvent;
import com.gezhii.fitgroup.network.OnRequestEnd;
import com.xianrui.lite_common.litesuits.android.log.Log;

import org.json.JSONException;
import org.json.JSONObject;

import de.greenrobot.event.EventBus;

/**
 * Created by y on 2015/10/20.
 */
public abstract class APICallBack implements OnRequestEnd {
    public void onRequestSuccess(String response) {
        int result = 0;
        String error_msg = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            result = jsonObject.getInt("result");
            if (result > 0) {
                error_msg = jsonObject.getString("error_msg");
//                Toast.makeText(MyApplication.getApplication(), error_msg, Toast.LENGTH_SHORT).show();
//                EventBus.getDefault().post(new CloseLoadingEvent());
                subRequestFail(error_msg);
            } else {
                subRequestSuccess(response);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            EventBus.getDefault().post(new CloseLoadingEvent());
            Log.d(getClass(), "error message is null");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public void onRequestFail(VolleyError error) {
        Log.i("VolleyError", error.toString());
        Toast.makeText(MyApplication.getApplication(), error.toString(), Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new CloseLoadingEvent());
    }

    public abstract void subRequestSuccess(String response) throws NoSuchFieldException;

    public void subRequestFail(String error_msg) throws NoSuchFieldException {
        Toast.makeText(MyApplication.getApplication(), error_msg, Toast.LENGTH_SHORT).show();
        EventBus.getDefault().post(new CloseLoadingEvent());
    }
}

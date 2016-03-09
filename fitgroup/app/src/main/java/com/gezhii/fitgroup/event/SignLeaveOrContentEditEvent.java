package com.gezhii.fitgroup.event;

import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;

/**
 * Created by fantasy on 16/1/4.
 */
public class SignLeaveOrContentEditEvent {
    HashMap<String, String> params;

    public SignLeaveOrContentEditEvent(HashMap<String, String> params) {
        this.params = params;
    }

    public HashMap<String, String> getParams() {
        Log.i("darren","event--------");
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }
}

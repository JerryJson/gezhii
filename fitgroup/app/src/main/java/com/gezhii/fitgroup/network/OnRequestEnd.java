package com.gezhii.fitgroup.network;

import com.android.volley.VolleyError;

/**
 * Created by xianrui on 15/10/19.
 */
public interface OnRequestEnd {
    void onRequestSuccess(String response);
    void onRequestFail(VolleyError error);
}

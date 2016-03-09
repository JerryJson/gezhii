package com.gezhii.fitgroup.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.squareup.okhttp.OkHttpClient;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xianrui on 15/4/20.
 */
public class BaseHttp {
    public final String TAG = getClass().getSimpleName();

    public static RequestQueue sRequestQueue;
    public static OkHttpClient sOKHttpClient;

    private String mUrl;
    private Map<String, String> mParams;
    private Map<String, String> mHeaders;

    private OnRequestEnd onRequestEnd;

    public static void initContext(Context context) {
        if (sOKHttpClient == null) {
            sOKHttpClient = new OkHttpClient();
            sOKHttpClient.networkInterceptors().add(new StethoInterceptor());
        }
        if (sRequestQueue == null) {
            sRequestQueue = Volley.newRequestQueue(context, new OkHttpStack(sOKHttpClient));
        }
    }

    public void setOnRequestEnd(OnRequestEnd onRequestEnd) {
        this.onRequestEnd = onRequestEnd;
    }

    public static void cancelAll(String tag) {
        sRequestQueue.cancelAll(tag);
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public void setParams(HashMap<String, String> mParams) {
        this.mParams = mParams;
    }

    public void setHeaders(HashMap<String, String> headers) {
        this.mHeaders = headers;
    }

    public void postString() {
        Log.d(TAG, "RequestUrl=========>" + mUrl + "\n" + "params : " + GsonHelper.getInstance().getGson().toJson(mParams) + "\n" +
                "header : " + GsonHelper.getInstance().getGson().toJson(mHeaders));
        StringRequest mStringRequest = new StringRequest(Request.Method.POST, mUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                response = response.replaceAll("\n", "");
                Log.d(TAG, "RequestSuccess=======>" + mUrl + "\n" + response);
                OnRequestEnd onRequestEnd = BaseHttp.this.onRequestEnd;
                if (onRequestEnd != null) {
                    onRequestEnd.onRequestSuccess(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                OnRequestEnd onRequestEnd = BaseHttp.this.onRequestEnd;
                if (onRequestEnd != null) {
                    onRequestEnd.onRequestFail(error);
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return mParams;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return mHeaders;
            }
        };
        sRequestQueue.add(mStringRequest);
    }


    //    private static OkHttpClient client = BaseHttp.getOkHttpClient();
//    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");

    //    public static void uploadFile(String url, String type, final File file, Callback callback) {
//        RequestBody requestBody = new MultipartBuilder().
//                type(MultipartBuilder.FORM)
//                .addPart(Headers.of(
//                        "Content-Disposition",
//                        "form-data; name=\"type\""), RequestBody.create(null, type))
//                .addPart(Headers.of(
//                        "Content-Disposition",
//                        "form-data; name=\"file\" ; filename=" + file.getName() + ".jpg"), RequestBody.create(MEDIA_TYPE_PNG, file))
//                .build();
//        final Request request = new Request.Builder().url(url).
//                post(requestBody).
//                build();
//        client.newCall(request).enqueue(callback);
//    }
}


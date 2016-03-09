package com.gezhii.fitgroup.api;

import com.gezhii.fitgroup.MyApplication;
import com.gezhii.fitgroup.network.BaseHttp;
import com.gezhii.fitgroup.network.OnRequestEnd;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by y on 2015/10/20.
 */
public class CommonAPI {
    private static final String Sign_KEY = "f*(dDfF3=@YNOPd298&lih#2$";
    private static final String DEF_TOKEN = "fitany0123456789";

    public static void postString(String url, HashMap<String, String> params, OnRequestEnd onRequestEnd) {
        BaseHttp http = new BaseHttp();
        http.initContext(MyApplication.getApplication());
        http.setUrl(url);
        http.setParams(params);
        http.setOnRequestEnd(onRequestEnd);

        HashMap<String, String> headers = new HashMap<>();
//        if (!UserModel.getInstance().isLogin()) {
//            headers.put("token", DEF_TOKEN);
//        } else {
//            headers.put("token", UserModel.getInstance().getUserDto().getUser().
//        }

        headers.put("sign", calcMd5Sign(params));
        http.setHeaders(headers);

        http.postString();
    }


    private static String calcMd5Sign(Map<String, String> params) {
        if (params != null && params.size() > 0) {
            List<Map.Entry<String, String>> infoIds = new ArrayList<>(params.entrySet());

            Collections.sort(infoIds, new Comparator<Map.Entry<String, String>>() {
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    //return (o2.getValue() - o1.getValue());
                    return (o1.getKey()).compareTo(o2.getKey());
                }
            });

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < infoIds.size(); i++) {
                String name = infoIds.get(i).getKey();
                sb.append(name);
                sb.append("=");
                sb.append(infoIds.get(i).getValue());
                sb.append("&");
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);

            String calcSign = md5(sb.toString() + Sign_KEY);

            return calcSign;
        }

        return md5(Sign_KEY);
    }

    private static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(input.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String md5 = number.toString(16);
            while (md5.length() < 32) {
                md5 = "0" + md5;
            }
            return md5;
        } catch (NoSuchAlgorithmException e) {
        }
        return null;
    }
}

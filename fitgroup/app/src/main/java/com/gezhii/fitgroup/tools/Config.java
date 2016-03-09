package com.gezhii.fitgroup.tools;

/**
 * Created by xianrui on 15/8/19.
 */
public class Config {

    public static final int loadMessageCount = 10; //默认从聊天记录中拉出的条数
    public static final boolean isDebug = true; //调试模式
    public static final int loadPageCount = 10; //分页每页个数
    public static String hostUrl; //服务器地址
    public static String debugHostUrl = "http://115.29.142.79:8080/fitgroup-api/";//测试服务器地址
    public static String productionHostUrl = "http://api.qing.am/fitgroup-api/";//生产服务器地址

    static {
        if (isDebug) {
            hostUrl = debugHostUrl;
        } else {
            hostUrl = productionHostUrl;
        }
    }


}

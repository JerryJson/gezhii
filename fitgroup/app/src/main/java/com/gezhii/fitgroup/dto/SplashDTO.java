package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Splash;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by fantasy on 15/11/20.
 */
public class SplashDTO extends BaseDto{
    public Splash splash;

    public Splash getSplash() {
        return splash;
    }

    public void setSplash(Splash splash) {
        this.splash = splash;
    }

    public static SplashDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (SplashDTO) gson.fromJson(jsonString, new TypeToken<SplashDTO>() {
        }.getType());
    }
}

package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xianrui on 15/10/20.
 */
public class BaseDto {

    int result;

    public int getResult() {
        return result;
    }

    public static BaseDto parserJson1(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (BaseDto) gson.fromJson(jsonString, new TypeToken<BaseDto>() {
        }.getType());
    }




}

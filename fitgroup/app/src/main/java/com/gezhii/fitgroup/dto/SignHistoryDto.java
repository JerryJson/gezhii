package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by xianrui on 15/11/6.
 */
public class SignHistoryDto {
    @SerializedName("signins")
    public List<Signin> data_list;

    public static SignHistoryDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (SignHistoryDto) gson.fromJson(jsonString, new TypeToken<SignHistoryDto>() {
        }.getType());
    }

}

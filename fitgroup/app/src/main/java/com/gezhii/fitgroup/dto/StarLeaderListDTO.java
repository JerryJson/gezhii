package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.StarLeaderBanner;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by ycl on 15/10/22.
 */
public class StarLeaderListDTO extends BaseDto{
    @SerializedName("banners")
    public List<StarLeaderBanner> data_list;

    public static StarLeaderListDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (StarLeaderListDTO) gson.fromJson(jsonString, new TypeToken<StarLeaderListDTO>() {
        }.getType());
    }

}

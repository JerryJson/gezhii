package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by fantasy on 16/2/19.
 */
public class FollowersDTO extends BaseDto {
    @SerializedName("users")
    public List<User> data_list;

    public static FollowersDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (FollowersDTO) gson.fromJson(jsonString, new TypeToken<FollowersDTO>() {
        }.getType());
    }

}

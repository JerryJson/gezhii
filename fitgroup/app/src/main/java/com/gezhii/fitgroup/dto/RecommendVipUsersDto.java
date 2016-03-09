package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by zj on 16/2/22.
 */
public class RecommendVipUsersDto extends BaseDto {
    @SerializedName("users")
    List<User> data_list;

    public List<User> getData_list() {
        return data_list;
    }

    public void setData_list(List<User> data_list) {
        this.data_list = data_list;
    }

    public static RecommendVipUsersDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (RecommendVipUsersDto)gson.fromJson(jsonString, new TypeToken<RecommendVipUsersDto>() {
        }.getType());
    }
}

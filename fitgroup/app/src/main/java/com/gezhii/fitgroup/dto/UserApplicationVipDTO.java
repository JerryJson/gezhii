package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.UserApplicationVip;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by fantasy on 16/2/19.
 */
public class UserApplicationVipDTO extends BaseDto {
    UserApplicationVip user_application_vip;

    public UserApplicationVip getUser_application_vip() {
        return user_application_vip;
    }

    public void setUser_application_vip(UserApplicationVip user_application_vip) {
        this.user_application_vip = user_application_vip;
    }

    public static UserApplicationVipDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (UserApplicationVipDTO) gson.fromJson(jsonString, new TypeToken<UserApplicationVipDTO>() {
        }.getType());
    }
}

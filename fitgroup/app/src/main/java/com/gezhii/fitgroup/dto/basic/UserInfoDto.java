package com.gezhii.fitgroup.dto.basic;

import com.gezhii.fitgroup.dto.BaseDto;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by xianrui on 15/10/28.
 */
public class UserInfoDto extends BaseDto {
    List<UserInfo> group_members;

    public List<UserInfo> getGroup_members() {
        return group_members;
    }

    public void setGroup_members(List<UserInfo> group_members) {
        this.group_members = group_members;
    }


    public static UserInfoDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (UserInfoDto) gson.fromJson(jsonString, new TypeToken<UserInfoDto>() {
        }.getType());
    }
}

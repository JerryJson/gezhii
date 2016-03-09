package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by fantasy on 16/2/19.
 */
public class VipUsersDTO extends BaseDto {
    List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public static VipUsersDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (VipUsersDTO) gson.fromJson(jsonString, new TypeToken<VipUsersDTO>() {
        }.getType());
    }
}

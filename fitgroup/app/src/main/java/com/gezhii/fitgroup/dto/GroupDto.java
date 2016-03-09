package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xianrui on 15/10/21.
 */
public class GroupDto extends BaseDto {
    Group Group;

    public Group getGroup() {
        return Group;
    }

    public void setGroup(Group Group) {
        this.Group = Group;
    }

    public static GroupDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupDto) gson.fromJson(jsonString, new TypeToken<GroupDto>() {
        }.getType());
    }


}

package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.GroupMember;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by fantasy on 15/12/9.
 */
public class GroupTaskDailySigninDetailDTO extends BaseDto {
    public List<GroupMember> signin_members;
    public List<GroupMember> unsignin_members;

    public static GroupTaskDailySigninDetailDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return gson.fromJson(jsonString, new TypeToken<GroupTaskDailySigninDetailDTO>() {
        }.getType());
    }
}

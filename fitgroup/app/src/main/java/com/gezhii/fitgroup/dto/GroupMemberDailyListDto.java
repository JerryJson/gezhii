package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.GroupMember;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by xianrui on 15/11/6.
 */
public class GroupMemberDailyListDto extends BaseDto {
    public List<GroupMember> members;

    public static GroupMemberDailyListDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupMemberDailyListDto) gson.fromJson(jsonString, new TypeToken<GroupMemberDailyListDto>() {
        }.getType());
    }
}

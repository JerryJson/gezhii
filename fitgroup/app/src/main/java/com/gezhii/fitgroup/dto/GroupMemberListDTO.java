package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.BaseDto;
import com.gezhii.fitgroup.dto.basic.GroupMember;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by ycl on 15/10/30.
 */
public class GroupMemberListDTO extends BaseDto {
    @SerializedName("group_members")
    public List<GroupMember> data_list;
    public int my_contribution_sort;

    public static GroupMemberListDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupMemberListDTO) gson.fromJson(jsonString, new TypeToken<GroupMemberListDTO>() {
        }.getType());
    }
}

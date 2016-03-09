package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.GroupMemberApplication;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

/**
 * Created by ycl on 15/10/28.
 */
public class GroupMemberApplicationDTO extends BaseDto{
    @SerializedName("group_member_application")
    public List<GroupMemberApplication> data_list;

    public static GroupMemberApplicationDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupMemberApplicationDTO) gson.fromJson(jsonString, new TypeToken<GroupMemberApplicationDTO>() {
        }.getType());
    }

}

package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.GroupProfile;
import com.gezhii.fitgroup.dto.basic.GroupSimpleProfile;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by ycl on 15/10/23.
 */
public class GroupProfileDTO extends BaseDto{
    public GroupProfile group;


    public static GroupProfileDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupProfileDTO) gson.fromJson(jsonString, new TypeToken<GroupProfileDTO>() {
        }.getType());
    }
}

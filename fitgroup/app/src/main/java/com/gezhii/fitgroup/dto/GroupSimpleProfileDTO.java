package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.dto.basic.GroupDailyStatistic;
import com.gezhii.fitgroup.dto.basic.GroupNotice;
import com.gezhii.fitgroup.dto.basic.GroupSimpleProfile;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by ycl on 15/10/23.
 */
public class GroupSimpleProfileDTO extends BaseDto{
    public GroupSimpleProfile group;


    public static GroupSimpleProfileDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupSimpleProfileDTO) gson.fromJson(jsonString, new TypeToken<GroupSimpleProfileDTO>() {
        }.getType());
    }
}

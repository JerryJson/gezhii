package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by ycl on 15/10/22.
 */
public class GroupSquareDTO extends BaseDto{
    public List<Group> recommend_groups;
    public List<Group> active_groups;
    public List<Group> sort_groups;

    public static GroupSquareDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupSquareDTO) gson.fromJson(jsonString, new TypeToken<GroupSquareDTO>() {
        }.getType());
    }


}

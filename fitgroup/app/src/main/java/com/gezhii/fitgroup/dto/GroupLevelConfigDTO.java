package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.GroupLevelConfig;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by ycl on 15/10/28.
 */
public class GroupLevelConfigDTO extends BaseDto{
    public List<GroupLevelConfig> group_level_configs;

    public static GroupLevelConfigDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupLevelConfigDTO) gson.fromJson(jsonString, new TypeToken<GroupLevelConfigDTO>() {
        }.getType());
    }

}

package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.GroupTagConfig;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by fantasy on 15/12/8.
 */
public class GroupTagDTO extends BaseDto {
    List<GroupTagConfig> group_tag_configs;

    public List<GroupTagConfig> getGroup_tag_configs() {
        return group_tag_configs;
    }

    public void setGroup_tag_configs(List<GroupTagConfig> group_tag_configs) {
        this.group_tag_configs = group_tag_configs;
    }

    public static GroupTagDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return gson.fromJson(jsonString, new TypeToken<GroupTagDTO>() {
        }.getType());
    }
}

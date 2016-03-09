package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.GroupTask;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by fantasy on 15/12/9.
 */
public class GroupTaskDTO extends BaseDto {
    List<GroupTask> group_tasks;

    public List<GroupTask> getGroup_tasks() {
        return group_tasks;
    }

    public void setGroup_tasks(List<GroupTask> group_tasks) {
        this.group_tasks = group_tasks;
    }

    public static GroupTaskDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return gson.fromJson(jsonString, new TypeToken<GroupTaskDTO>() {
        }.getType());
    }
}

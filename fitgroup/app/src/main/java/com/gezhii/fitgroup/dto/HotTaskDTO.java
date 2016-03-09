package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.HotTask;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by fantasy on 15/12/28.
 */
public class HotTaskDTO extends BaseDto {
    List<HotTask> tasks;

    public List<HotTask> getTasks() {
        return tasks;
    }

    public void setTasks(List<HotTask> tasks) {
        this.tasks = tasks;
    }

    public static HotTaskDTO parseJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return gson.fromJson(jsonString, new TypeToken<HotTaskDTO>() {
        }.getType());
    }
}

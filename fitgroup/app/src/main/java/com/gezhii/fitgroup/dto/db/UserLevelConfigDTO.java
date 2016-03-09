package com.gezhii.fitgroup.dto.db;

import com.gezhii.fitgroup.dto.BaseDto;
import com.gezhii.fitgroup.dto.basic.UserLevelConfig;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by fantasy on 15/11/9.
 */
public class UserLevelConfigDTO extends BaseDto{
    public List<UserLevelConfig> level_configs;

    public List<UserLevelConfig> getUserLevelConfig() {
        return level_configs;
    }

    public void setUserLevelConfig(List<UserLevelConfig> userLevelConfig) {
        this.level_configs = userLevelConfig;
    }

    public static UserLevelConfigDTO parserJson(String jsonString){
        Gson gson = GsonHelper.getInstance().getGson();
        return (UserLevelConfigDTO) gson.fromJson(jsonString,new TypeToken<UserLevelConfigDTO>(){}.getType());
    }
}

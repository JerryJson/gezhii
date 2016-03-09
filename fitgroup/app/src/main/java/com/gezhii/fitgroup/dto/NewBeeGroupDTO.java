package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xianrui on 15/11/19.
 */
public class NewBeeGroupDTO {
    public Group new_bie_group;


    public static NewBeeGroupDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (NewBeeGroupDTO) gson.fromJson(jsonString, new TypeToken<NewBeeGroupDTO>() {
        }.getType());
    }
}

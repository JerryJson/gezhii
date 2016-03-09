package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Badge;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by xianrui on 15/11/16.
 */
public class PushExperienceDto {
    public int experience_inr;
    public double progress;
    public int level;
    public int level_upgrade;
    public List<Badge> badge;

    public static List<Badge> parserBadgeListJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (List<Badge>) gson.fromJson(jsonString, new TypeToken<List<Badge>>() {
        }.getType());
    }


}

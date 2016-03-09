package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Badge;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by xianrui on 15/10/22.
 */
public class BadgesDto extends BaseDto {
    public List<Badge> badges;

    public List<Badge> getBadges() {
        return badges;
    }

    public void setBadges(List<Badge> badges) {
        this.badges = badges;
    }

    public static BadgesDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (BadgesDto) gson.fromJson(jsonString, new TypeToken<BadgesDto>() {
        }.getType());
    }
}

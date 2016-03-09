package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Tag;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by zj on 16/2/19.
 */
public class HomePageDto extends BaseDto{
    private List<Tag> following_channels;
    private String newest_following_user_feeds;

    public String getNewest_following_user_feeds() {
        return newest_following_user_feeds;
    }

    public void setNewest_following_user_feeds(String newest_following_user_feeds) {
        this.newest_following_user_feeds = newest_following_user_feeds;
    }

    public List<Tag> getFollowing_channels() {
        return following_channels;
    }

    public void setFollowing_channels(List<Tag> following_channels) {
        this.following_channels = following_channels;
    }

    public static HomePageDto parseJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return gson.fromJson(jsonString, new TypeToken<HomePageDto>() {
        }.getType());
    }
}

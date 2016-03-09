package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.GeneralInfo;
import com.gezhii.fitgroup.dto.basic.Video;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by fantasy on 16/2/23.
 */
public class SigninRelativeInfoDTO extends BaseDto {
    int week_signin_count;
    int total_signin_count;
    SpecialInfo special_info;
    List<List<GeneralInfo>> general_info;
    int channel_id;
    List<Video> videos;

    public int getChannel_id() {
        return channel_id;
    }

    public void setChannel_id(int channel_id) {
        this.channel_id = channel_id;
    }

    public int getWeek_signin_count() {
        return week_signin_count;
    }

    public void setWeek_signin_count(int week_signin_count) {
        this.week_signin_count = week_signin_count;
    }

    public int getTotal_signin_count() {
        return total_signin_count;
    }

    public void setTotal_signin_count(int total_signin_count) {
        this.total_signin_count = total_signin_count;
    }

    public SpecialInfo getSpecial_info() {
        return special_info;
    }

    public void setSpecial_info(SpecialInfo special_info) {
        this.special_info = special_info;
    }

    public List<List<GeneralInfo>> getGeneral_info() {
        return general_info;
    }

    public void setGeneral_info(List<List<GeneralInfo>> general_info) {
        this.general_info = general_info;
    }

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public static SigninRelativeInfoDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (SigninRelativeInfoDTO) gson.fromJson(jsonString, new TypeToken<SigninRelativeInfoDTO>() {
        }.getType());
    }

}

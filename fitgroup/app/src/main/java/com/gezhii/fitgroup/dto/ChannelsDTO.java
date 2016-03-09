package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Tag;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by fantasy on 16/2/16.
 */
public class ChannelsDTO extends BaseDto {
    @SerializedName("channels")
    public List<Tag> data_list;


    public static ChannelsDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (ChannelsDTO) gson.fromJson(jsonString, new TypeToken<ChannelsDTO>() {
        }.getType());
    }
}

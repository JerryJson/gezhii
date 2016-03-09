package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.dto.basic.Tag;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zj on 16/2/17.
 */
public class ChannelSigninsDTO extends BaseDto implements Serializable{
    @SerializedName("signins")
    public List<Signin> data_list;
    public int channel_follow_flag;
    @SerializedName("channel")
    public Tag tag;

    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    public List<Signin> getData_list() {
        return data_list;
    }

    public void setData_list(List<Signin> data_list) {
        this.data_list = data_list;
    }

    public int getChannel_follow_flag() {
        return channel_follow_flag;
    }

    public void setChannel_follow_flag(int channel_follow_flag) {
        this.channel_follow_flag = channel_follow_flag;
    }

    public static ChannelSigninsDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (ChannelSigninsDTO) gson.fromJson(jsonString, new TypeToken<ChannelSigninsDTO>() {
        }.getType());
    }
}

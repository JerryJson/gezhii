package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by ycl on 15/10/22.
 */
public class TagGroupsDTO extends BaseDto{
    @SerializedName("groups")
    public List<Group> data_list;

    public static TagGroupsDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (TagGroupsDTO) gson.fromJson(jsonString, new TypeToken<TagGroupsDTO>() {
        }.getType());
    }


}

package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Tag;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by fantasy on 16/2/19.
 */
public class TagsDTO extends BaseDto {
    List<Tag> tags;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }
    public static TagsDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (TagsDTO) gson.fromJson(jsonString, new TypeToken<TagsDTO>() {
        }.getType());
    }
}

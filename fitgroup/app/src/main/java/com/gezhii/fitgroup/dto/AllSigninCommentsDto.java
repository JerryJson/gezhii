package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Comment;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by zj on 16/2/18.
 */
public class AllSigninCommentsDto extends BaseDto {
    @SerializedName("comments")
    public List<Comment> data_list;



    public static AllSigninCommentsDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return gson.fromJson(jsonString, new TypeToken<AllSigninCommentsDto>() {
        }.getType());
    }
}

package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.GroupNotice;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by ycl on 15/10/24.
 */
public class GroupNoticeListDTO {
    @SerializedName("group_notices")
    public List<GroupNotice> data_list;

    public static GroupNoticeListDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupNoticeListDTO) gson.fromJson(jsonString, new TypeToken<GroupNoticeListDTO>() {
        }.getType());
    }
}


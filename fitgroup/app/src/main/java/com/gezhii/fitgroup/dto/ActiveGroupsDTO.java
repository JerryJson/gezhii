package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.dto.basic.GroupDailyStatistic;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by ycl on 15/10/22.
 */
public class ActiveGroupsDTO extends BaseDto{
    public int my_group_activeness_sort;
    @SerializedName("active_groups")
    public List<Group> data_list;
    public GroupDailyStatistic my_group_yestoday_activeness;

    public static ActiveGroupsDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (ActiveGroupsDTO) gson.fromJson(jsonString, new TypeToken<ActiveGroupsDTO>() {
        }.getType());
    }


}

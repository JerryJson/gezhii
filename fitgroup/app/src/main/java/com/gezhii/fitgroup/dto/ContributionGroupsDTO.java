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
public class ContributionGroupsDTO extends BaseDto{
    public int my_group_contribution_sort=-1;
    @SerializedName("contribution_sort_groups")
    public List<Group> data_list;

    public static ContributionGroupsDTO parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (ContributionGroupsDTO) gson.fromJson(jsonString, new TypeToken<ContributionGroupsDTO>() {
        }.getType());
    }


}

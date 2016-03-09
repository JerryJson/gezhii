package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.GroupNotice;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xianrui on 15/10/27.
 */
public class GroupNoticeDto extends BaseDto {
    private GroupNotice group_notice;

    public GroupNotice getGroup_notice() {
        return group_notice;
    }

    public void setGroup_notice(GroupNotice group_notice) {
        this.group_notice = group_notice;
    }

    public static GroupNoticeDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (GroupNoticeDto) gson.fromJson(jsonString, new TypeToken<GroupNoticeDto>() {
        }.getType());
    }
}

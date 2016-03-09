package com.gezhii.fitgroup.model;

import com.gezhii.fitgroup.dto.GroupDto;

/**
 * Created by xianrui on 15/10/21.
 */
public class GroupModel {

    GroupDto mGroupDto;

    private static class GroupModelHolder {
        public final static GroupModel sington = new GroupModel();
    }

    public static GroupModel getInstance() {
        return GroupModelHolder.sington;
    }

    public void setGroupDto(GroupDto mGroupDto) {
        this.mGroupDto = mGroupDto;
    }

    public GroupDto getGroupDto() {
        return mGroupDto;
    }
}

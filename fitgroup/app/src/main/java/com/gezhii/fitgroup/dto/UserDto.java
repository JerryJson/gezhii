package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Badge;
import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.dto.basic.GroupMember;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.dto.basic.UserWeight;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xianrui on 15/10/20.
 */
public class UserDto extends BaseDto implements Serializable {
    private User user;
    private Group group;
    private GroupMember group_member;
    private List<UserWeight> weight_list;

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public GroupMember getGroup_member() {
        return group_member;
    }

    public void setGroup_member(GroupMember group_member) {
        this.group_member = group_member;
    }

    public List<UserWeight> getWeight_list() {
        return weight_list;
    }

    public void setWeight_list(List<UserWeight> weight_list) {
        this.weight_list = weight_list;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static UserDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (UserDto) gson.fromJson(jsonString, new TypeToken<UserDto>() {
        }.getType());
    }
}

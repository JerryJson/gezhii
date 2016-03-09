package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Group;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.dto.basic.UserWeight;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by xianrui on 15/12/1.
 */
public class OtherUserProfileDto extends BaseDto {
    User user;
    List<UserWeight> weight_list;
    Group group;
    int flag;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<UserWeight> getWeight_list() {
        return weight_list;
    }

    public void setWeight_list(List<UserWeight> weight_list) {
        this.weight_list = weight_list;
    }

    public Group getGroup(){
        return group;
    }

    public void setGroup(Group group){
        this.group = group;
    }

    public int getFlag(){
        return flag;
    }

    public void setFlag(int flag){
        this.flag = flag;
    }

    public static OtherUserProfileDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (OtherUserProfileDto) gson.fromJson(jsonString, new TypeToken<OtherUserProfileDto>() {
        }.getType());
    }

}

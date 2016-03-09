package com.gezhii.fitgroup.dto.basic;

import java.util.Date;

/**
 * Created by xianrui on 15/10/22.
 */
public class GroupMember {

    private int id;
    private int group_id;
    private int user_id;
    private String nick_name;
    private int user_type;
    private int contribution_value;
    private int user_sort_value;
    private Date sign_time;
    private int sign_count;


    private User user;

    public void setId(int id) {
        this.id = id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public void setContribution_value(int contribution_value) {
        this.contribution_value = contribution_value;
    }

    public void setUser_sort_value(int user_sort_value) {
        this.user_sort_value = user_sort_value;
    }

    public int getId() {
        return id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public int getUser_type() {
        return user_type;
    }

    public int getContribution_value() {
        return contribution_value;
    }

    public int getUser_sort_value() {
        return user_sort_value;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getSign_time() {
        return sign_time;
    }

    public void setSign_time(Date sign_time) {
        this.sign_time = sign_time;
    }

    public int getSign_count() {
        return sign_count;
    }

    public void setSign_count(int sign_count) {
        this.sign_count = sign_count;
    }
}

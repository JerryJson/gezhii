package com.gezhii.fitgroup.dto.basic;

import java.util.List;

/**
 * Created by xianrui on 15/10/28.
 */
public class UserInfo {

    private int user_type;
    private String huanxin_id;
    private int user_id;
    private List<Badge> badges_list;

    public int getUser_type() {
        return user_type;
    }

    public void setUser_type(int user_type) {
        this.user_type = user_type;
    }

    public String getHuanxin_id() {
        return huanxin_id;
    }

    public void setHuanxin_id(String huanxin_id) {
        this.huanxin_id = huanxin_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public List<Badge> getBadges_list() {
        return badges_list;
    }

    public void setBadges_list(List<Badge> badges_list) {
        this.badges_list = badges_list;
    }
}

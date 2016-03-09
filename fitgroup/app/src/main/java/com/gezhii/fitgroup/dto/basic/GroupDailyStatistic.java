package com.gezhii.fitgroup.dto.basic;

import java.util.Date;

/**
 * Created by ycl on 15/10/22.
 */
public class GroupDailyStatistic {
    private int id;
    private int group_id;
    private Date created_time;
    private int signin_user_count;
    private float activeness;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public int getSignin_user_count() {
        return signin_user_count;
    }

    public void setSignin_user_count(int signin_user_count) {
        this.signin_user_count = signin_user_count;
    }

    public float getActiveness() {
        return activeness;
    }

    public void setActiveness(float activeness) {
        this.activeness = activeness;
    }
}

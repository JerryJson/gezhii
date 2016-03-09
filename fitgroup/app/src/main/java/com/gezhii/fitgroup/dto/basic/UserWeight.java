package com.gezhii.fitgroup.dto.basic;

import java.util.Date;

/**
 * Created by xianrui on 15/10/22.
 */
public class UserWeight {

    private int id;
    private int user_id;
    private float weight;
    private Date created_time;

    public void setId(int id) {
        this.id = id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getId() {
        return id;
    }

    public int getUser_id() {
        return user_id;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }
}

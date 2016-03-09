package com.gezhii.fitgroup.dto.basic;

import java.io.Serializable;

/**
 * Created by fantasy on 15/12/9.
 */
public class GroupTask implements Serializable {
    private int id;
    private int group_id;
    private String task_name;
    private String created_time;
    private int count;
    private String task_info;
    private int signin;

    public int getSignin() {
        return signin;
    }

    public void setSignin(int signin) {
        this.signin = signin;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTask_info() {
        return task_info;
    }

    public void setTask_info(String task_info) {
        this.task_info = task_info;
    }
}

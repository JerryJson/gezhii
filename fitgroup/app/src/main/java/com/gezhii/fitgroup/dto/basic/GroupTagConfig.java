package com.gezhii.fitgroup.dto.basic;

import java.io.Serializable;

/**
 * Created by fantasy on 15/12/8.
 */
public class GroupTagConfig implements Serializable {
    private int id;
    private String name;
    private String img;
    private int flag;
    private int group_count;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getGroup_count() {
        return group_count;
    }

    public void setGroup_count(int group_count) {
        this.group_count = group_count;
    }
}

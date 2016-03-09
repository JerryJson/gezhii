package com.gezhii.fitgroup.dto.basic;

import java.io.Serializable;

/**
 * Created by fantasy on 16/2/16.
 */
public class Tag implements Serializable {
    int id;
    String name;
    String img;
    String type;
    int flag;
    String tag_description;
    int content_count;
    String banner_img;
    String banner_link;
    int last_signin_id;
    int unread_count;

    public String getBanner_img() {
        return banner_img;
    }

    public void setBanner_img(String banner_img) {
        this.banner_img = banner_img;
    }

    public String getBanner_link() {
        return banner_link;
    }

    public void setBanner_link(String banner_link) {
        this.banner_link = banner_link;
    }

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getTag_description() {
        return tag_description;
    }

    public void setTag_description(String tag_description) {
        this.tag_description = tag_description;
    }

    public int getLast_signin_id() {
        return last_signin_id;
    }

    public void setLast_signin_id(int last_signin_id) {
        this.last_signin_id = last_signin_id;
    }

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
        this.unread_count = unread_count;
    }

    public int getContent_count() {
        return content_count;
    }

    public void setContent_count(int content_count) {
        this.content_count = content_count;
    }
}

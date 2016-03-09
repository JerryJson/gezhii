package com.gezhii.fitgroup.dto.basic;

import java.io.Serializable;

/**
 * Created by zj on 16/2/22.
 */
public class AboutMeMessage implements Serializable {
    String sender_nick_name;
    String sender_icon;
    String send_user_id;
    int signin_id;
    int type;
    String content;
    String time;
    int sender_user_vip;

    public int getSender_user_vip() {
        return sender_user_vip;
    }

    public void setSender_user_vip(int sender_user_vip) {
        this.sender_user_vip = sender_user_vip;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSender_nick_name() {
        return sender_nick_name;
    }

    public void setSender_nick_name(String sender_nick_name) {
        this.sender_nick_name = sender_nick_name;
    }

    public String getSender_icon() {
        return sender_icon;
    }

    public void setSender_icon(String sender_icon) {
        this.sender_icon = sender_icon;
    }

    public String getSend_user_id() {
        return send_user_id;
    }

    public void setSend_user_id(String send_user_id) {
        this.send_user_id = send_user_id;
    }

    public int getSignin_id() {
        return signin_id;
    }

    public void setSignin_id(int signin_id) {
        this.signin_id = signin_id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

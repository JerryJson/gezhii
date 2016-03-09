package com.gezhii.fitgroup.dto.basic;

import java.util.Date;

/**
 * Created by zj on 16/2/18.
 */
public class Comment {
    public int id;
    public int signin_id;
    public int comment_user_id;
    public String comment;
    public Date created_time;
    public User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSignin_id() {
        return signin_id;
    }

    public void setSignin_id(int signin_id) {
        this.signin_id = signin_id;
    }

    public int getComment_user_id() {
        return comment_user_id;
    }

    public void setComment_user_id(int comment_user_id) {
        this.comment_user_id = comment_user_id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }
}

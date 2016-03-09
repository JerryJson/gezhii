package com.gezhii.fitgroup.dto.basic;

import java.util.Date;
import java.util.List;

/**
 * Created by xianrui on 15/10/26.
 */
public class Signin {

    private int id;
    private int user_id;
    private String description;
    private String img;
    private String address;
    private double lng;
    private double lat;
    private Date created_time;
    private String signin_info;
    private int signin_type;
    private int like_count;
    private int is_share;
    private int is_default_img;
    private int flag;  //自己是否已经对该卡片点赞(这个字段是服务器动态计算的,不在数据库表中)

    private String task_name;          //1.1新加的2个字段
    private int task_continue_days;

    private User user; //1.2新增字段，用与公会打卡

    private int post_type; //1.3新增两个字段 0,1打卡，发帖
    private int comment_count;

    private List<Comment> comments;//1.3新增评论列表

    private int hasVideo; //该卡片是否有教程视频,(该字段是本地维护用于显示判断的，不在数据库表中)

    public int getHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(int hasVideo) {
        this.hasVideo = hasVideo;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public void setPost_type(int post_type) {
        this.post_type = post_type;
    }

    public int getPost_type() {
        return post_type;
    }

    public void setComment_count(int comment_count) {
        this.comment_count = comment_count;
    }

    public int getComment_count() {
        return comment_count;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public String getSignin_info() {
        return signin_info;
    }

    public void setSignin_info(String signin_info) {
        this.signin_info = signin_info;
    }

    public int getLike_count() {
        return like_count;
    }

    public void setLike_count(int like_count) {
        this.like_count = like_count;
    }

    public int getSignin_type() {
        return signin_type;
    }

    public void setSignin_type(int signin_type) {
        this.signin_type = signin_type;
    }

    public int getIs_share() {
        return is_share;
    }

    public void setIs_share(int is_share) {
        this.is_share = is_share;
    }

    public int getIs_default_img() {
        return is_default_img;
    }

    public void setIs_default_img(int is_default_img) {
        this.is_default_img = is_default_img;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public int getTask_continue_days() {
        return task_continue_days;
    }

    public void setTask_continue_days(int task_continue_days) {
        this.task_continue_days = task_continue_days;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

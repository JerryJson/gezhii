package com.gezhii.fitgroup.dto.basic;

import java.util.List;

/**
 * Created by xianrui on 15/10/20.
 */
public class User {
    private int id;
    private String login_type;
    private String third_party_id;
    private String nick_name;
    private String icon;
    private int gender;
    private int height;
    private float goal_weight;
    private int level;
    private int experience;
    private String huanxin_id;
    private String huanxin_password;
    private int signin_count;
    private int continue_signin_days;
    private String guide_info;
    private int isChecking;

    private int type; //type: -1,0,1,2,3  机器人，微氧君，普通用户（未入群），普通用户（已加群），会长（会长专访，url)

    private List<UserBadge> badges;

    private String url;

    private int vip;
    private String self_description;
    private int mentor_adoption_count;
    private int following_count;
    private int followed_count;
    private int is_following;

    private String age_description;
    private String job;
    private String sport_frequency;
    private String sport_way;

    private int mentor_id;

    private List<Tag> tags;

    private List<Video> videos;

    public List<Video> getVideos() {
        return videos;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
    }

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public int getIs_following() {
        return is_following;
    }

    public void setIs_following(int is_following) {
        this.is_following = is_following;
    }


    public int getMentor_id() {
        return mentor_id;
    }

    public void setMentor_id(int mentor_id) {
        this.mentor_id = mentor_id;
    }

    public String getAge_description() {
        return age_description;
    }

    public void setAge_description(String age_description) {
        this.age_description = age_description;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getSport_frequency() {
        return sport_frequency;
    }

    public void setSport_frequency(String sport_frequency) {
        this.sport_frequency = sport_frequency;
    }

    public String getSport_way() {
        return sport_way;
    }

    public void setSport_way(String sport_way) {
        this.sport_way = sport_way;
    }

    public int getVip() {
        return vip;
    }

    public void setVip(int vip) {
        this.vip = vip;
    }

    public String getSelf_description() {
        return self_description;
    }

    public void setSelf_description(String self_description) {
        this.self_description = self_description;
    }

    public int getMentor_adoption_count() {
        return mentor_adoption_count;
    }

    public void setMentor_adoption_count(int mentor_adoption_count) {
        this.mentor_adoption_count = mentor_adoption_count;
    }

    public int getFollowing_count() {
        return following_count;
    }

    public void setFollowing_count(int following_count) {
        this.following_count = following_count;
    }

    public int getFollowed_count() {
        return followed_count;
    }

    public void setFollowed_count(int followed_count) {
        this.followed_count = followed_count;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLogin_type() {
        return login_type;
    }

    public void setLogin_type(String login_type) {
        this.login_type = login_type;
    }

    public String getThird_party_id() {
        return third_party_id;
    }

    public void setThird_party_id(String third_party_id) {
        this.third_party_id = third_party_id;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public float getGoal_weight() {
        return goal_weight;
    }

    public void setGoal_weight(int goal_weight) {
        this.goal_weight = goal_weight;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getHuanxin_id() {
        return huanxin_id;
    }

    public void setHuanxin_id(String huanxin_id) {
        this.huanxin_id = huanxin_id;
    }

    public String getHuanxin_password() {
        return huanxin_password;
    }

    public void setHuanxin_password(String huanxin_password) {
        this.huanxin_password = huanxin_password;
    }

    public int getSignin_count() {
        return signin_count;
    }

    public void setSignin_count(int signin_count) {
        this.signin_count = signin_count;
    }

    public int getContinue_signin_days() {
        return continue_signin_days;
    }

    public void setContinue_signin_days(int continue_signin_days) {
        this.continue_signin_days = continue_signin_days;
    }

    public String getGuide_info() {
        return guide_info;
    }

    public void setGuide_info(String guide_info) {
        this.guide_info = guide_info;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public List<UserBadge> getBadges() {
        return badges;
    }

    public void setBadges(List<UserBadge> badges) {
        this.badges = badges;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIsChecking() {
        return isChecking;
    }

    public void setIsChecking(int isChecking) {
        this.isChecking = isChecking;
    }
}

package com.gezhii.fitgroup.dto.basic;

import java.util.Date;
import java.util.List;

/**
 * Created by xianrui on 15/10/21.
 */
public class Group {
    private int id;
    private String group_number;
    private String group_name;
    private String slogan;
    private int level;
    private String description;
    private String tags;
    private int member_count;
    private Date created_time;
    private String group_huanxin_id;
    private int continue_days;
    private int total_contribution_value;
    private int type;
    private String group_huanxin_name;
    private float total_weight_reduction;
    private User leader;
    public GroupDailyStatistic yesterdayGroupDailyStatistics;
    public GroupDailyStatistic todayGroupDailyStatistics;
    //1.1之后有的字段
    public List<GroupTask> group_tasks;
    public int need_check;  //0, 1: 添加公会是否需要审核

    public GroupDailyStatistic getYesterdayGroupDailyStatistics() {
        return yesterdayGroupDailyStatistics;
    }

    public void setYesterdayGroupDailyStatistics(GroupDailyStatistic yesterdayGroupDailyStatistics) {
        this.yesterdayGroupDailyStatistics = yesterdayGroupDailyStatistics;
    }

    public GroupDailyStatistic getTodayGroupDailyStatistics() {
        return todayGroupDailyStatistics;
    }

    public void setTodayGroupDailyStatistics(GroupDailyStatistic todayGroupDailyStatistics) {
        this.todayGroupDailyStatistics = todayGroupDailyStatistics;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getGroup_number() {
        return group_number;
    }

    public void setGroup_number(String group_number) {
        this.group_number = group_number;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getSlogan() {
        return slogan;
    }

    public void setSlogan(String slogan) {
        this.slogan = slogan;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public int getMember_count() {
        return member_count;
    }

    public void setMember_count(int member_count) {
        this.member_count = member_count;
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Date created_time) {
        this.created_time = created_time;
    }

    public String getGroup_huanxin_id() {
        return group_huanxin_id;
    }

    public void setGroup_huanxin_id(String group_huanxin_id) {
        this.group_huanxin_id = group_huanxin_id;
    }

    public int getContinue_days() {
        return continue_days;
    }

    public void setContinue_days(int continue_days) {
        this.continue_days = continue_days;
    }

    public int getTotal_contribution_value() {
        return total_contribution_value;
    }

    public void setTotal_contribution_value(int total_contribution_value) {
        this.total_contribution_value = total_contribution_value;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGroup_huanxin_name() {
        return group_huanxin_name;
    }

    public void setGroup_huanxin_name(String group_huanxin_name) {
        this.group_huanxin_name = group_huanxin_name;
    }

    public float getTotal_weight_reduction() {
        return total_weight_reduction;
    }

    public void setTotal_weight_reduction(float total_weight_reduction) {
        this.total_weight_reduction = total_weight_reduction;
    }

    public User getLeader() {
        return leader;
    }

    public void setLeader(User leader) {
        this.leader = leader;
    }

    public int getNeed_check() {
        return need_check;
    }

    public void setNeed_check(int need_check) {
        this.need_check = need_check;
    }
}

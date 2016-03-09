package com.gezhii.fitgroup.dto.basic;

/**
 * Created by fantasy on 15/11/9.
 */
public class UserLevelConfig {
    public int id;
    public int level;
    public int expericence;
    public String rules_description;
    public String permission_description;

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getExpericence() {
        return expericence;
    }

    public void setExpericence(int expericence) {
        this.expericence = expericence;
    }

    public String getRules_description() {
        return rules_description;
    }

    public void setRules_description(String rules_description) {
        this.rules_description = rules_description;
    }

    public String getPermission_description() {
        return permission_description;
    }

    public void setPermission_description(String permission_description) {
        this.permission_description = permission_description;
    }
}

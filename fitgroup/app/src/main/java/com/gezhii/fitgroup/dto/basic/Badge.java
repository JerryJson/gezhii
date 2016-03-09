package com.gezhii.fitgroup.dto.basic;

import java.io.Serializable;

/**
 * Created by xianrui on 15/10/22.
 */
public class Badge implements Serializable {

    private int id;
    private String name;
    private String icon;
    private String background;
    private String description;
    private String unlock_condition;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getBackground() {
        return background;
    }

    public void setBackground(String background) {
        this.background = background;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUnlock_condition() {
        return unlock_condition;
    }

    public void setUnlock_condition(String unlock_condition) {
        this.unlock_condition = unlock_condition;
    }



}

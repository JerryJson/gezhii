package com.gezhii.fitgroup.dto;

import java.io.Serializable;

/**
 * Created by fantasy on 16/1/22.
 */
public class UserStepDTO extends BaseDto implements Serializable {
    private String date;//日期
    private int stepNums;//步数

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStepNums() {
        return stepNums;
    }

    public void setStepNums(int stepNums) {
        this.stepNums = stepNums;
    }
}

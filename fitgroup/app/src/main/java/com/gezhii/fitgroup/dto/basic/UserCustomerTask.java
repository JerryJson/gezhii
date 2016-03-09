package com.gezhii.fitgroup.dto.basic;

import java.io.Serializable;

/**
 * Created by fantasy on 15/12/11.
 */
public class UserCustomerTask implements Serializable {
    private String task_id;
    private String task_name;
    private String finish_date;
    public boolean is_finish;
    private String sign_type;
    private int step_limit;
    private float weight;

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public boolean is_finish() {
        return is_finish;
    }

    public void setIs_finish(boolean is_finish) {
        this.is_finish = is_finish;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public String getFinish_date() {
        return finish_date;
    }

    public void setFinish_date(String finish_date) {
        this.finish_date = finish_date;
    }

    public String getSign_type() {
        return sign_type;
    }

    public void setSign_type(String sign_type) {
        this.sign_type = sign_type;
    }

    public int getStep_limit() {
        return step_limit;
    }

    public void setStep_limit(int step_limit) {
        this.step_limit = step_limit;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}

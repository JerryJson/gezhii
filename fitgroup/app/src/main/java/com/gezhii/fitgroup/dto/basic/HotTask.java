package com.gezhii.fitgroup.dto.basic;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fantasy on 15/12/28.
 */
public class HotTask implements Serializable {
    private int id;
    private String task_name;
    private String task_info;
    private List<GroupTagConfig> task_tags;
    private String input;
    private int step;
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTask_info() {
        return task_info;
    }

    public void setTask_info(String task_info) {
        this.task_info = task_info;
    }

    public String getTask_name() {
        return task_name;
    }

    public void setTask_name(String task_name) {
        this.task_name = task_name;
    }

    public List<GroupTagConfig> getTask_tags() {
        return task_tags;
    }

    public void setTask_tags(List<GroupTagConfig> task_tags) {
        this.task_tags = task_tags;
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }
}

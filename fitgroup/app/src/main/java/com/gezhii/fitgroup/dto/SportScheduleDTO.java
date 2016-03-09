package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Signin;

import java.util.Date;
import java.util.List;

/**
 * Created by fantasy on 16/2/18.
 */
public class SportScheduleDTO extends BaseDto {
    int the_day_number;
    Date date;
    List<Signin> list;

    public int getThe_day_number() {
        return the_day_number;
    }

    public void setThe_day_number(int the_day_number) {
        this.the_day_number = the_day_number;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public List<Signin> getList() {
        return list;
    }

    public void setList(List<Signin> list) {
        this.list = list;
    }
}

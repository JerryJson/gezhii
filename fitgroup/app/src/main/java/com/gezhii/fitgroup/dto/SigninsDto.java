package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.List;

/**
 * Created by xianrui on 15/11/26.
 */
public class SigninsDto extends BaseDto {
    private Date end_date;
    private Date begin_date;
    private List<Integer> flags;

    public Date getEnd_date() {
        return end_date;
    }

    public void setEnd_date(Date end_date) {
        this.end_date = end_date;
    }

    public Date getBegin_date() {
        return begin_date;
    }

    public void setBegin_date(Date begin_date) {
        this.begin_date = begin_date;
    }

    public List<Integer> getFlags() {
        return flags;
    }

    public void setFlags(List<Integer> flags) {
        this.flags = flags;
    }

    public static SigninsDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (SigninsDto) gson.fromJson(jsonString, new TypeToken<SigninsDto>() {
        }.getType());
    }
}

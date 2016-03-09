package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by zj on 16/2/19.
 */
public class MentorSignHistoryDto extends BaseDto {
    List<Signin> mentor_signin_history;
    List<Signin> user_signin_history;
    int mentor_state;
    int has_more;
    int days;
    User mentor;
    String user_begin_signin_date;

    HashMap<String,Integer> mentor_yestoday_task_videos;

    public static MentorSignHistoryDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return gson.fromJson(jsonString, new TypeToken<MentorSignHistoryDto>() {
        }.getType());
    }

    public HashMap<String, Integer> getMentor_yestoday_task_videos() {
        return mentor_yestoday_task_videos;
    }

    public void setMentor_yestoday_task_videos(HashMap<String, Integer> mentor_yestoday_task_videos) {
        this.mentor_yestoday_task_videos = mentor_yestoday_task_videos;
    }

    public List<Signin> getMentor_signin_history() {
        return mentor_signin_history;
    }

    public void setMentor_signin_history(List<Signin> mentor_signin_history) {
        this.mentor_signin_history = mentor_signin_history;
    }

    public List<Signin> getUser_signin_history() {
        return user_signin_history;
    }

    public void setUser_signin_history(List<Signin> user_signin_history) {
        this.user_signin_history = user_signin_history;
    }

    public int getMentor_state() {
        return mentor_state;
    }

    public void setMentor_state(int mentor_state) {
        this.mentor_state = mentor_state;
    }

    public int getHas_more() {
        return has_more;
    }

    public void setHas_more(int has_more) {
        this.has_more = has_more;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public User getMentor() {
        return mentor;
    }

    public void setMentor(User mentor) {
        mentor = mentor;
    }

    public String getUser_begin_signin_date() {
        return user_begin_signin_date;
    }

    public void setUser_begin_signin_date(String user_begin_signin_date) {
        this.user_begin_signin_date = user_begin_signin_date;
    }
}

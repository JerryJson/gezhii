package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.dto.basic.User;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by zj on 16/2/20.
 */
public class MentorAndMePlanDto {
    int day_number;
    boolean isToday;
    boolean mentor_is_rest;
    boolean user_is_rest;
    String user_date;
    String mentor_date;
    List<Signin> mentor_signin;
    List<Signin> user_signin;
    User mentor;

    List<String> all_done;
    List<String> only_mentor_done;
    List<String> only_user_done;

    boolean isChangeMentor;

    public boolean getIsChangeMentor() {
        return isChangeMentor;
    }

    public void setIsChangeMentor(boolean isChangeMentor) {
        this.isChangeMentor = isChangeMentor;
    }

    public boolean isToday() {
        return isToday;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;
    }

    public int getDay_number() {
        return day_number;
    }

    public void setDay_number(int day_number) {
        this.day_number = day_number;
    }

    public String getUser_date() {
        return user_date;
    }

    public void setUser_date(String user_date) {
        this.user_date = user_date;
    }

    public String getMentor_date() {
        return mentor_date;
    }

    public void setMentor_date(String mentor_date) {
        this.mentor_date = mentor_date;
    }

    public List<Signin> getMentor_signin() {
        return mentor_signin;
    }

    public void setMentor_signin(List<Signin> mentor_signin) {
        this.mentor_signin = mentor_signin;
    }

    public List<Signin> getUser_signin() {
        return user_signin;
    }

    public void setUser_signin(List<Signin> user_signin) {
        this.user_signin = user_signin;
    }

    public boolean isMentor_is_rest() {
        return mentor_is_rest;
    }

    public void setMentor_is_rest(boolean mentor_is_rest) {
        this.mentor_is_rest = mentor_is_rest;
    }

    public boolean isUser_is_rest() {
        return user_is_rest;
    }

    public void setUser_is_rest(boolean user_is_rest) {
        this.user_is_rest = user_is_rest;
    }

    public List<String> getAll_done() {
        return all_done;
    }

    public void setAll_done(List<String> all_done) {
        this.all_done = all_done;
    }

    public List<String> getOnly_mentor_done() {
        return only_mentor_done;
    }

    public void setOnly_mentor_done(List<String> only_mentor_done) {
        this.only_mentor_done = only_mentor_done;
    }

    public List<String> getOnly_user_done() {
        return only_user_done;
    }

    public void setOnly_user_done(List<String> only_user_done) {
        this.only_user_done = only_user_done;
    }

    public User getMentor() {
        return mentor;
    }

    public void setMentor(User mentor) {
        this.mentor = mentor;
    }
}

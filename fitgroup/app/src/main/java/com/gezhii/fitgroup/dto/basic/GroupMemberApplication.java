package com.gezhii.fitgroup.dto.basic;

import com.gezhii.fitgroup.dto.basic.User;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Date;

/**
 * Created by ycl on 15/10/28.
 */
public class GroupMemberApplication{
    public int id;
    public int group_id;
    public int user_id;
    public String description;
    public Date created_time;
    public User user;

}

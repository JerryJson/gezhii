package com.gezhii.fitgroup.dto.basic;

import java.util.List;

/**
 * Created by ycl on 15/10/28.
 */
public class GroupSimpleProfile extends Group{
    public GroupDailyStatistic yestoday_group_statistic;
    public List<UserInfo> all_members;


    public static class UserInfo{
        public int id;
        public String icon;
    }
}

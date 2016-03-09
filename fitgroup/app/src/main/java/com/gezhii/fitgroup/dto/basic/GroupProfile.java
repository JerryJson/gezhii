package com.gezhii.fitgroup.dto.basic;

import java.util.List;

/**
 * Created by ycl on 15/10/28.
 */
public class GroupProfile extends Group {
    public GroupNotice recent_notice;
    public GroupDailyStatistic today_group_statistic;
    public List<GroupMember> group_members;

}

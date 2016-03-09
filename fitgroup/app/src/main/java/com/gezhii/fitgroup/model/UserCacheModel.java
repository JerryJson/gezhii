package com.gezhii.fitgroup.model;

import com.gezhii.fitgroup.dto.basic.Badge;
import com.gezhii.fitgroup.tools.DataKeeperHelper;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xianrui on 15/10/23.
 */
public class UserCacheModel {

    Map<String, UserCacheInfo> userMap = new HashMap<>();


    private static class UserCacheModelHolder {
        public final static UserCacheModel sington = new UserCacheModel();
    }

    public static UserCacheModel getInstance() {
        return UserCacheModelHolder.sington;
    }

    public UserCacheInfo getUserInfo(String huanxin_id) {
        UserCacheInfo userCacheInfo = userMap.get(huanxin_id);
        if (userCacheInfo == null) {
            userCacheInfo = (UserCacheInfo) DataKeeperHelper.getInstance().getUserCacheDataKeeper().get(huanxin_id);
            if (userCacheInfo != null) {
                userMap.put(huanxin_id, userCacheInfo);
            }
        }
        return userCacheInfo;
    }

    public void setUserInfo(String huanxin_id, UserCacheInfo userCacheInfo) {
        userMap.put(huanxin_id, userCacheInfo);
        DataKeeperHelper.getInstance().getUserCacheDataKeeper().put(huanxin_id, userCacheInfo);
    }


    public void setUserNickName(String huanxin_id, String nick_name) {
        UserCacheInfo userCacheInfo = getUserInfo(huanxin_id);
        if (userCacheInfo == null) {
            userCacheInfo = new UserCacheInfo();
        }
        userCacheInfo.nickName = nick_name;
        userMap.put(huanxin_id, userCacheInfo);
        DataKeeperHelper.getInstance().getUserCacheDataKeeper().put(huanxin_id, userCacheInfo);
    }

    public void setUserIcon(String huanxin_id, String icon) {
        UserCacheInfo userCacheInfo = getUserInfo(huanxin_id);
        if (userCacheInfo == null) {
            userCacheInfo = new UserCacheInfo();
        }
        userCacheInfo.icon = icon;
        userMap.put(huanxin_id, userCacheInfo);
        DataKeeperHelper.getInstance().getUserCacheDataKeeper().put(huanxin_id, userCacheInfo);
    }

    public void setUserBadgeList(String huanxin_id, List<Badge> badgeIconList) {
        UserCacheInfo userCacheInfo = getUserInfo(huanxin_id);
        if (userCacheInfo == null) {
            userCacheInfo = new UserCacheInfo();
        }
        userCacheInfo.badgeIconList = badgeIconList;
        userCacheInfo.lastCacheBadgeTime = System.currentTimeMillis() / 1000;
        userMap.put(huanxin_id, userCacheInfo);
        DataKeeperHelper.getInstance().getUserCacheDataKeeper().put(huanxin_id, userCacheInfo);
    }

    public void setLastCacheBadgeTime(String huanxin_id, long time) {
        UserCacheInfo userCacheInfo = getUserInfo(huanxin_id);
        if (userCacheInfo == null) {
            userCacheInfo = new UserCacheInfo();
        }
        userCacheInfo.lastCacheBadgeTime = time;
        userMap.put(huanxin_id, userCacheInfo);
        DataKeeperHelper.getInstance().getUserCacheDataKeeper().put(huanxin_id, userCacheInfo);
    }


    public static class UserCacheInfo implements Serializable {
        public String icon;
        public String nickName;
        public List<Badge> badgeIconList;
        public long lastCacheBadgeTime;
    }

}

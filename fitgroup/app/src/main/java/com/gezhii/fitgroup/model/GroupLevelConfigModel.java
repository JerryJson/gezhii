package com.gezhii.fitgroup.model;

import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.GroupLevelConfigDTO;
import com.gezhii.fitgroup.dto.basic.GroupLevelConfig;

import java.util.List;

/**
 * Created by ycl on 15/10/28.
 */
public class GroupLevelConfigModel {
    public List<GroupLevelConfig> group_level_config = null;

    private static class GroupLevelConfigModelHolder {
        public final static GroupLevelConfigModel sington = new GroupLevelConfigModel();
    }

    public static GroupLevelConfigModel getInstance() {
        return GroupLevelConfigModelHolder.sington;
    }


    public List<GroupLevelConfig> getGroupLevelConfig() {
        if (group_level_config == null) {
            API.getAllGroupLevelConfig(new APICallBack() {
                @Override
                public void subRequestSuccess(String response) {
                    GroupLevelConfigDTO groupLevelConfigDTO = GroupLevelConfigDTO.parserJson(response);
                    group_level_config = groupLevelConfigDTO.group_level_configs;
                }
            });
        }

        return group_level_config;
    }

    public boolean isGroupFull(int level, int member_count) {
        if (group_level_config == null) return false;

        for (int i = 0; i < group_level_config.size(); i++) {
            if (group_level_config.get(i).level == level && group_level_config.get(i).max_member_count <= member_count)
                return true;
        }

        return false;
    }

    public int getLevelMaxCount(int level) {
        if (group_level_config == null) return 0;

        for (int i = 0; i < group_level_config.size(); i++) {
            if (group_level_config.get(i).level == level)
                return group_level_config.get(i).max_member_count;
        }

        return 0;
    }

    public int getLevelSignCount(int level) {
        if (group_level_config == null) return 0;
        for (int i = 0; i < group_level_config.size(); i++) {
            if (group_level_config.get(i).level == level)
                return group_level_config.get(i).signin_count;
        }
        return 0;
    }

}

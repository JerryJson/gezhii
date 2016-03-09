package com.gezhii.fitgroup.model;

import android.text.TextUtils;
import android.view.View;

import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xianrui on 15/11/16.
 */
public class GroupTagsModel {

    public static final String TAG_GROUP_TAGS = "tag_group_tags";

    List<GroupTags> groupTagsList;


    public GroupTagsModel() {
        String groupTagsString = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_GROUP_TAGS, "");
        if (TextUtils.isEmpty(groupTagsString)) {
            createGroupTagsList();
        } else {
            groupTagsList = GsonHelper.getInstance().getGson().fromJson(groupTagsString, new TypeToken<List<GroupTags>>() {
            }.getType());
        }
    }

    private void createGroupTagsList() {
        groupTagsList = new ArrayList<>();
        String[] nameList = new String[]{"10级及以上", "只限女生", "只限男生", "运动+食物打卡", "运动打卡", "高级工会（长期健身）", "连续打卡21天以上"};
        for (String name : nameList) {
            GroupTags groupTags = new GroupTags();
            groupTags.name = name;
            groupTagsList.add(groupTags);
        }
    }

    public void addTags(String name) {
        GroupTags groupTags = new GroupTags();
        groupTags.name = name;
        groupTags.isSelect = true;
        groupTagsList.add(groupTags);
        save();
    }

    public List<String> getSelectList() {
        List<String> selectList = new ArrayList<>();
        for (GroupTags groupTags : groupTagsList) {
            if (groupTags.isSelect) {
                selectList.add(groupTags.name);
            }
        }
        return selectList;
    }

    public void mergeSelectList(List<String> tagList) {
        for (GroupTags groupTags : groupTagsList) {
            groupTags.isSelect = false;
        }

        List<GroupTags> cacheList = new ArrayList<>();
        for (String tag : tagList) {
            boolean hasTag = false;
            for (GroupTags groupTags : groupTagsList) {
                if (tag.equals(groupTags.name)) {
                    groupTags.name = tag;
                    groupTags.isSelect = true;
                    hasTag = true;
                }
            }
            if (!hasTag) {
                GroupTags tags = new GroupTags();
                tags.name = tag;
                tags.isSelect = true;
                if (!cacheList.contains(tags)){
                    cacheList.add(tags);
                }
            }
        }
        if (cacheList.size() > 0) {
            groupTagsList.addAll(cacheList);
        }

    }

    private void save() {
        DataKeeperHelper.getInstance().getDataKeeper().put(TAG_GROUP_TAGS, GsonHelper.getInstance().getGson().toJson(groupTagsList));
    }

    public List<GroupTags> getGroupTagsList() {
        return groupTagsList;
    }

    public void clear() {
        groupTagsList.clear();
        DataKeeperHelper.getInstance().getDataKeeper().put(TAG_GROUP_TAGS, "");
    }

    private static class GroupTagsModelHolder {
        public final static GroupTagsModel sington = new GroupTagsModel();
    }

    public static GroupTagsModel getInstance() {
        return GroupTagsModelHolder.sington;
    }


    public class GroupTags {
        public String name;
        public boolean isSelect;
        public View.OnClickListener onClickListener;
    }

}

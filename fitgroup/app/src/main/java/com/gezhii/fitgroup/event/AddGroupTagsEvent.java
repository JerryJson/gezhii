package com.gezhii.fitgroup.event;

import java.util.List;

/**
 * Created by xianrui on 15/11/16.
 */
public class AddGroupTagsEvent {
    List<String> tagsList;

    public AddGroupTagsEvent(List<String> tagsList) {
        this.tagsList = tagsList;
    }

    public List<String> getTagsList() {
        return tagsList;
    }
}

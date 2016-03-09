package com.gezhii.fitgroup.model;

import com.gezhii.fitgroup.tools.DataKeeperHelper;

/**
 * Created by fantasy on 15/12/14.
 */
public class GroupTaskModel {

    private static final String TAG_CREATE_IS_SHOW_LONG_CLICK_DELETE_GROUP_TASK = "tag_create_is_show_long_click_delete_group_task";//长按删除某个公会任务（创建公会时公会任务的提示） 0为显示，1为不显示
    private static final String TAG_DETAILIS_SHOW_LONG_CLICK_DELETE_GROUP_TASK = "tag_detailis_show_long_click_delete_group_task";//长按删除某个自定义任务（公会详细里的公会任务提示） 0为显示，1为不显示

    private static class GroupTaskModelHolder {
        public final static GroupTaskModel sington = new GroupTaskModel();
    }

    public static GroupTaskModel getInstance() {
        return GroupTaskModelHolder.sington;
    }

    public void hideCreateLongClickDeleteCustomerTask() {
        DataKeeperHelper.getInstance().getTagGroupTaskCache().put(TAG_CREATE_IS_SHOW_LONG_CLICK_DELETE_GROUP_TASK, 1);
    }

    public boolean isShowCreateLongClickDeleteCustomerTask() {
        if (DataKeeperHelper.getInstance().getTagGroupTaskCache().get(TAG_CREATE_IS_SHOW_LONG_CLICK_DELETE_GROUP_TASK, 0) == 0) {
            return true;
        } else {
            return false;
        }

    }

    public void hideDetailLongClickDeleteCustomerTask() {
        DataKeeperHelper.getInstance().getTagGroupTaskCache().put(TAG_DETAILIS_SHOW_LONG_CLICK_DELETE_GROUP_TASK, 1);
    }

    public boolean isShowDetailLongClickDeleteCustomerTask() {
        if (DataKeeperHelper.getInstance().getTagGroupTaskCache().get(TAG_DETAILIS_SHOW_LONG_CLICK_DELETE_GROUP_TASK, 0) == 0) {
            return true;
        } else {
            return false;
        }

    }
}

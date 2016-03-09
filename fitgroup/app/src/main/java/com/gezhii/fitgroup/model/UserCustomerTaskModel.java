package com.gezhii.fitgroup.model;

import com.gezhii.fitgroup.dto.UserCustomerTaskDTO;
import com.gezhii.fitgroup.dto.basic.UserCustomerTask;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.xianrui.lite_common.litesuits.android.log.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * Created by fantasy on 15/12/11.
 */
public class UserCustomerTaskModel {

    public boolean isFromUserCustomerTask = false;
    private static final String TAG_USER_CUSTOMER_TASK_DTO = "tag_user_customer_task_dto";
    private static final String TAG_IS_SHOW_LONG_CLICK_DELETE_CUSTOMER_TASK = "tag_is_show_long_click_delete_customer_task";//长按删除某个自定义任务 0为显示，1为不显示
    private UserCustomerTaskDTO userCustomerTaskDTO;
    List<UserCustomerTask> userCustomerTaskList;

    private static class UserCustomerTaskModelHolder {
        public final static UserCustomerTaskModel sington = new UserCustomerTaskModel();
    }

    public static UserCustomerTaskModel getInstance() {
        return UserCustomerTaskModelHolder.sington;
    }

    private UserCustomerTaskModel() {
        tryLoadLocal();
    }

    private boolean tryLoadLocal() {
        userCustomerTaskDTO = (UserCustomerTaskDTO) DataKeeperHelper.getInstance().getUserCustomerTaskCacheDataKeeper().get(TAG_USER_CUSTOMER_TASK_DTO);
        return userCustomerTaskDTO != null;
    }

    public List<UserCustomerTask> getUserCustomerTaskListModel() {
        if (tryLoadLocal()) {
            return userCustomerTaskDTO.getCustomerTaskList();
        }
        return null;
    }

    public void addUserCustomerTask(UserCustomerTask userCustomerTask) {
        userCustomerTaskList = new ArrayList<UserCustomerTask>();
        userCustomerTask.setTask_id(UUID.randomUUID().toString());
        if (tryLoadLocal()) {
            userCustomerTaskList = userCustomerTaskDTO.getCustomerTaskList();
            userCustomerTaskList.add(userCustomerTask);
        } else {
            userCustomerTaskDTO = new UserCustomerTaskDTO();
            userCustomerTaskList.add(userCustomerTask);
        }

        userCustomerTaskDTO.setCustomerTaskList(userCustomerTaskList);
        DataKeeperHelper.getInstance().getUserCustomerTaskCacheDataKeeper().put(TAG_USER_CUSTOMER_TASK_DTO, userCustomerTaskDTO);
    }

    public void hideLongClickDeleteCustomerTask() {
        DataKeeperHelper.getInstance().getUserCustomerTaskCacheDataKeeper().put(TAG_IS_SHOW_LONG_CLICK_DELETE_CUSTOMER_TASK, 1);
    }

    public boolean isShowLongClickDeleteCustomerTask() {
        if (DataKeeperHelper.getInstance().getUserCustomerTaskCacheDataKeeper().get(TAG_IS_SHOW_LONG_CLICK_DELETE_CUSTOMER_TASK, 0) == 0) {
            return true;
        } else {
            return false;
        }

    }

    public void editUserCustomerTask(String task_id) {
        if (tryLoadLocal()) {
            userCustomerTaskList = userCustomerTaskDTO.getCustomerTaskList();
            Iterator<UserCustomerTask> iterator = userCustomerTaskList.iterator();
            while (iterator.hasNext()) {
                UserCustomerTask userCustomerTask = iterator.next();
                if (task_id.equals(userCustomerTask.getTask_id())) {
                    //userCustomerTask.setIs_finish(true);
                    userCustomerTask.setFinish_date(TimeHelper.getInstance().getTodayString());
                }
            }
            userCustomerTaskDTO.setCustomerTaskList(userCustomerTaskList);
            DataKeeperHelper.getInstance().getUserCustomerTaskCacheDataKeeper().put(TAG_USER_CUSTOMER_TASK_DTO, userCustomerTaskDTO);
        }
    }

    public void deleteUserCustomerTask(String task_id) {
        Log.i("darren", task_id);
        if (tryLoadLocal()) {
            userCustomerTaskList = userCustomerTaskDTO.getCustomerTaskList();
            Iterator<UserCustomerTask> iterator = userCustomerTaskList.iterator();
            while (iterator.hasNext()) {
                UserCustomerTask userCustomerTask = iterator.next();
                if (task_id.equals(userCustomerTask.getTask_id())) {
                    iterator.remove();
                }
            }
            userCustomerTaskDTO.setCustomerTaskList(userCustomerTaskList);
            DataKeeperHelper.getInstance().getUserCustomerTaskCacheDataKeeper().put(TAG_USER_CUSTOMER_TASK_DTO, userCustomerTaskDTO);
        }

    }

    public boolean isExistCustomerTask(String task_name) {
        if (tryLoadLocal()) {
            userCustomerTaskList = userCustomerTaskDTO.getCustomerTaskList();
            Iterator<UserCustomerTask> iterator = userCustomerTaskList.iterator();
            while (iterator.hasNext()) {
                UserCustomerTask userCustomerTask = iterator.next();
                if (task_name.equals(userCustomerTask.getTask_name())) {
                    return true;
                }
            }
        }
        return false;
    }
    public void clear(){
        userCustomerTaskDTO=null;
        DataKeeperHelper.getInstance().getUserCustomerTaskCacheDataKeeper().put(TAG_USER_CUSTOMER_TASK_DTO, userCustomerTaskDTO);
    }
}

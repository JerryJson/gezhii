package com.gezhii.fitgroup.tools;

import com.gezhii.fitgroup.MyApplication;
import com.xianrui.lite_common.litesuits.common.data.DataKeeper;

/**
 * Created by xianrui on 15/8/20.
 */
public class DataKeeperHelper {

    private static final String TAG_SIGN_CACHE = "tag_sign_cache";
    private static final String TAG_USER_CACHE = "tag_user_cache";
    private static final String TAG_USER_CUSTOMER_TASK_CACHE = "tag_user_customer_task_cache";
    private static final String TAG_GROUP_TASK_CACHE = "tag_group_task_cache";
    private static final String TAG_USER_MESSAGE_CACHE = "tag_user_message_cache";
    private static final String TAG_USER_STEP_CACHE = "tag_user_step_cache";
    DataKeeper dataKeeper;
    DataKeeper SignCacheDataKeeper;
    DataKeeper userCacheDataKeeper;
    DataKeeper userCustomerTaskCacheDataKeeper;
    DataKeeper groupTaskCacheDataKeeper;
    DataKeeper userMessageCacheDataKeeper;
    DataKeeper userStepDataKeeper;

    private DataKeeperHelper() {
        dataKeeper = new DataKeeper(MyApplication.getApplication(), MyApplication.getApplication().getPackageName());
        SignCacheDataKeeper = new DataKeeper(MyApplication.getApplication(), TAG_SIGN_CACHE);
        userCacheDataKeeper = new DataKeeper(MyApplication.getApplication(), TAG_USER_CACHE);
        userCustomerTaskCacheDataKeeper = new DataKeeper(MyApplication.getApplication(), TAG_USER_CUSTOMER_TASK_CACHE);
        groupTaskCacheDataKeeper = new DataKeeper(MyApplication.getApplication(), TAG_GROUP_TASK_CACHE);
        userMessageCacheDataKeeper = new DataKeeper(MyApplication.getApplication(), TAG_USER_MESSAGE_CACHE);
        userStepDataKeeper = new DataKeeper(MyApplication.getApplication(), TAG_USER_STEP_CACHE);
    }

    private static class DataKeeperHolder {
        public final static DataKeeperHelper sington = new DataKeeperHelper();
    }

    public static DataKeeperHelper getInstance() {
        return DataKeeperHolder.sington;
    }


    public DataKeeper getDataKeeper() {
        return dataKeeper;
    }

    public DataKeeper getSignCacheDataKeeper() {
        return SignCacheDataKeeper;
    }

    public DataKeeper getUserCacheDataKeeper() {
        return userCacheDataKeeper;
    }

    public DataKeeper getUserCustomerTaskCacheDataKeeper() {
        return userCustomerTaskCacheDataKeeper;
    }

    public DataKeeper getTagGroupTaskCache() {
        return groupTaskCacheDataKeeper;
    }

    public DataKeeper getUserMessageCacheDataKeeper() {
        return userMessageCacheDataKeeper;
    }

    public DataKeeper getUserStepCache() {
        return userStepDataKeeper;
    }
}

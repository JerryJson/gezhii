package com.gezhii.fitgroup.model;

import android.database.Cursor;
import android.os.Environment;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.gezhii.fitgroup.db.TaskDBHelper;
import com.gezhii.fitgroup.dto.db.DBTask;
import com.gezhii.fitgroup.dto.db.DBTaskCategory;
import com.gezhii.fitgroup.network.BaseHttp;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.xianrui.lite_common.litesuits.android.log.Log;
import com.xianrui.lite_common.litesuits.common.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by fantasy on 15/12/9.
 */
public class TaskDBModel {

    public static final String TAG_TASK_DB_LAST_UPDATE_TIME = "tag_task_db_last_update_time";

    List<DBTask> mDBTaskList;
    List<DBTaskCategory> mDBTaskCategoryList;

    private static class TaskDBModelHolder {
        public final static TaskDBModel sington = new TaskDBModel();
    }

    public static TaskDBModel getInstance() {
        return TaskDBModelHolder.sington;
    }

    public TaskDBModel() {
        mDBTaskList = new ArrayList<>();
        mDBTaskCategoryList = new ArrayList<>();

        long lastDBUpdateTime = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_TASK_DB_LAST_UPDATE_TIME, 0);
        if (System.currentTimeMillis() - lastDBUpdateTime > 24 * 60 * 60 * 1000) {
            updateDB();
        }
    }

    private void updateDB() {
        Log.i("update" + DataKeeperHelper.getInstance().getDataKeeper().get(TAG_TASK_DB_LAST_UPDATE_TIME, 0));
        String url = QiniuHelper.getSignDownLoadUrl(
                "http://7xij1s.com2.z0.glb.qiniucdn.com/fitgroup_task1.2.sqlite?time=" + System.currentTimeMillis());
        Log.i("fantasy", "DataBaseUrl " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();
        if (BaseHttp.sOKHttpClient == null) {
            BaseHttp.sOKHttpClient = new OkHttpClient();
            BaseHttp.sOKHttpClient.networkInterceptors().add(new StethoInterceptor());
        }
        BaseHttp.sOKHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {
                Log.i("darren", Environment.getExternalStorageState());
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    DataKeeperHelper.getInstance().getDataKeeper().put(TAG_TASK_DB_LAST_UPDATE_TIME, System.currentTimeMillis());
                    FileUtils.copyInputStreamToFile(response.body().byteStream(), new File(TaskDBHelper.getDataBasePath()));
                    clear();
                    TaskDBHelper.resetDB();
                }
            }
        });
    }

    public void updateTaskList() {
        String sql = "select * from " + TaskDBHelper.Tables.T_TASKS;
        Cursor cursor = TaskDBHelper.getInstance().getDb().rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            mDBTaskList.clear();
            cursor.moveToFirst();
            do {
                DBTask dbTask = new DBTask();
                dbTask.id = cursor.getInt(0);
                dbTask.name = cursor.getString(1);
                dbTask.category_id = cursor.getInt(2);
                dbTask.parameters = cursor.getString(3);
                dbTask.duration = cursor.getInt(4);
                dbTask.count = cursor.getInt(5);
                dbTask.group_count = cursor.getInt(6);
                dbTask.distance = cursor.getDouble(7);
                dbTask.weight = cursor.getDouble(8);
                dbTask.tag = cursor.getInt(9);
                dbTask.step = cursor.getInt(10);
                mDBTaskList.add(dbTask);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public List<DBTask> getDBTaskList() {
        List<DBTask> dbTaskList = new ArrayList<>();
        if (mDBTaskList.size() == 0) {
            updateTaskList();
        }
        dbTaskList.addAll(mDBTaskList);
        return dbTaskList;
    }

    private void updateTaskCategoryList() {
        String sql = "select * from " + TaskDBHelper.Tables.T_TASK_CATEGORY;
        Cursor cursor = TaskDBHelper.getInstance().getDb().rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            mDBTaskCategoryList.clear();
            cursor.moveToFirst();
            do {
                DBTaskCategory dbTaskCategory = new DBTaskCategory();
                dbTaskCategory.id = cursor.getInt(0);
                dbTaskCategory.name = cursor.getString(1);
                mDBTaskCategoryList.add(dbTaskCategory);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public List<DBTask> findDBTaskByName(String name) {
        List<DBTask> dbTaskList = new ArrayList<>();
        for (DBTask dbTask : getDBTaskList()) {
            if (dbTask.name.contains(name)) {
                dbTaskList.add(dbTask);
            }
        }
        return dbTaskList;
    }


    public String getSigninTypeByName(String name) {
        updateTaskList();
        String signinType = "";
        for (DBTask dbTask : getDBTaskList()) {
            if (dbTask.name.equals(name)) {
                return dbTask.parameters;
            }
        }
        return signinType;
    }

    public int getStepsByName(String name) {
        updateTaskList();
        int steps = 0;
        for (DBTask dbTask : getDBTaskList()) {
            if (dbTask.name.equals(name)) {
                return dbTask.step;
            }
        }
        return steps;
    }

    public List<DBTaskCategory> getDBTaskCategoryList() {
        if (mDBTaskCategoryList.size() == 0) {
            updateTaskCategoryList();
        }
        return mDBTaskCategoryList;
    }

    public List<DBTask> getDBTaskByCategoryId(Integer category_id) {
        String sql = "select * from " + TaskDBHelper.Tables.T_TASKS + " where category_id = " + category_id;
        Cursor cursor = TaskDBHelper.getInstance().getDb().rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            mDBTaskList.clear();
            cursor.moveToFirst();
            do {
                DBTask dbTask = new DBTask();
                dbTask.id = cursor.getInt(0);
                dbTask.name = cursor.getString(1);
                dbTask.category_id = cursor.getInt(2);
                dbTask.parameters = cursor.getString(3);
                dbTask.duration = cursor.getInt(4);
                dbTask.count = cursor.getInt(5);
                dbTask.group_count = cursor.getInt(6);
                dbTask.distance = cursor.getDouble(7);
                dbTask.weight = cursor.getDouble(8);
                dbTask.tag = cursor.getInt(9);
                dbTask.step = cursor.getInt(10);
                mDBTaskList.add(dbTask);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mDBTaskList;
    }

    public void clear() {
        mDBTaskList.clear();
        mDBTaskCategoryList.clear();
    }
}

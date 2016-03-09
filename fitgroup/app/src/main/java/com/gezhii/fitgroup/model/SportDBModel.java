package com.gezhii.fitgroup.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.text.TextUtils;

import com.facebook.stetho.okhttp.StethoInterceptor;
import com.gezhii.fitgroup.db.DBHelper;
import com.gezhii.fitgroup.dto.db.DBDiet;
import com.gezhii.fitgroup.dto.db.DBSport;
import com.gezhii.fitgroup.dto.db.DBSportCategory;
import com.gezhii.fitgroup.network.BaseHttp;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.qiniu.QiniuHelper;
import com.google.gson.reflect.TypeToken;
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
 * Created by xianrui on 15/10/30.
 */
public class SportDBModel {

    public static final String TAG_SPORT_DB_LAST_UPDATE_TIME = "tag_sport_db_last_update_time";
    public static final String TAG_CACHE_CUSTOM_DB_SPORT_LIST = "tag_cache_custom_db_sport_list";

    List<DBSport> mDBSportList;
    List<DBSportCategory> mDBSportCategoryList;
    List<DBDiet> mDBDietList;
    List<DBSport> mCacheCustomDBSportList;


    private static class SportDBModelHolder {
        public final static SportDBModel sington = new SportDBModel();
    }

    public static SportDBModel getInstance() {
        return SportDBModelHolder.sington;
    }

    public SportDBModel() {
        mDBSportList = new ArrayList<>();
        mDBSportCategoryList = new ArrayList<>();
        mDBDietList = new ArrayList<>();
        String mCacheCustomDBSportListString = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_CACHE_CUSTOM_DB_SPORT_LIST, "");
        if (!TextUtils.isEmpty(mCacheCustomDBSportListString)) {
            mCacheCustomDBSportList = GsonHelper.getInstance().getGson().fromJson(mCacheCustomDBSportListString, new TypeToken<List<DBSport>>() {
            }.getType());
        } else {
            mCacheCustomDBSportList = new ArrayList<>();
        }

        long lastDBUpdateTime = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_SPORT_DB_LAST_UPDATE_TIME, 0);
        if (System.currentTimeMillis() - lastDBUpdateTime > 24 * 60 * 60 * 1000) {
            updateDB();
        }

    }

    private void updateDB() {
        String url = QiniuHelper.getSignDownLoadUrl(
                "http://7xij1s.com2.z0.glb.qiniucdn.com/fitgroup_sports.sqlite?time=" + System.currentTimeMillis());
        Log.i("xianrui", "DataBaseUrl " + url);
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
                DataKeeperHelper.getInstance().getDataKeeper().put(TAG_SPORT_DB_LAST_UPDATE_TIME, System.currentTimeMillis());
                FileUtils.copyInputStreamToFile(response.body().byteStream(), new File(DBHelper.getDataBasePath()));
                clear();
                DBHelper.resetDB();
                if (mCacheCustomDBSportList.size() > 0) {
                    for (DBSport dbSport : mCacheCustomDBSportList) {
//                        insertSport(dbSport);
                        ContentValues values = new ContentValues();
                        values.put(DBHelper.Columns.SportsColumn._CATEGORY_ID, dbSport.category_id);
                        values.put(DBHelper.Columns.SportsColumn._NAME, dbSport.name);
                        values.put(DBHelper.Columns.SportsColumn._PARAMETERS, dbSport.parameters);
                        int result;
                        result = (int) DBHelper.getInstance().getDb().insert(DBHelper.Tables.T_SPORTS, null, values);
                    }
                    updateSportList();
                }
            }
        });
    }


    public List<DBSport> getDefSportList() {
        List<DBSport> dbSportList = new ArrayList<>();
        for (DBSport dbSport : getDBSportList()) {
            if (dbSport.tag > 0 && !dbSport.name.equals("今日步数")) {
                dbSportList.add(dbSport);
            }
        }
        return dbSportList;
    }


    public void updateSportList() {
        String sql = "select * from " + DBHelper.Tables.T_SPORTS;
        Cursor cursor = DBHelper.getInstance().getDb().rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            mDBSportList.clear();
            cursor.moveToFirst();
            do {
                DBSport dbSport = new DBSport();
                dbSport.id = cursor.getInt(0);
                dbSport.name = cursor.getString(1);
                dbSport.category_id = cursor.getInt(2);
                dbSport.parameters = cursor.getString(3);
                dbSport.duration = cursor.getInt(4);
                dbSport.count = cursor.getInt(5);
                dbSport.group_count = cursor.getInt(6);
                dbSport.distance = cursor.getDouble(7);
                dbSport.weight = cursor.getDouble(8);
                dbSport.tag = cursor.getInt(9);
                mDBSportList.add(dbSport);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public int insertSport(DBSport dbSport) {
        ContentValues values = new ContentValues();
        values.put(DBHelper.Columns.SportsColumn._CATEGORY_ID, dbSport.category_id);
        values.put(DBHelper.Columns.SportsColumn._NAME, dbSport.name);
        values.put(DBHelper.Columns.SportsColumn._PARAMETERS, dbSport.parameters);
        int result;
        result = (int) DBHelper.getInstance().getDb().insert(DBHelper.Tables.T_SPORTS, null, values);
        if (result > 0) {
            updateSportList();
            mCacheCustomDBSportList.add(dbSport);
            DataKeeperHelper.getInstance().getDataKeeper().put(TAG_CACHE_CUSTOM_DB_SPORT_LIST, GsonHelper.getInstance().getGson().toJson(mCacheCustomDBSportList));
        }
        return result;
    }


    public int updateSport(DBSport dbSport) {
        ContentValues values = new ContentValues();
        if (dbSport.category_id > 0)
            values.put(DBHelper.Columns.SportsColumn._CATEGORY_ID, dbSport.category_id);
        if (!TextUtils.isEmpty(dbSport.name))
            values.put(DBHelper.Columns.SportsColumn._NAME, dbSport.name);
        if (!TextUtils.isEmpty(dbSport.parameters))
            values.put(DBHelper.Columns.SportsColumn._PARAMETERS, dbSport.parameters);
        if (dbSport.duration > 0)
            values.put(DBHelper.Columns.SportsColumn._DURATION, dbSport.duration);
        if (dbSport.count > 0)
            values.put(DBHelper.Columns.SportsColumn._COUNT, dbSport.count);
        if (dbSport.group_count > 0)
            values.put(DBHelper.Columns.SportsColumn._GROUP_COUNT, dbSport.group_count);
        if (dbSport.distance > 0)
            values.put(DBHelper.Columns.SportsColumn._DISTANCE, dbSport.distance);
        if (dbSport.weight > 0)
            values.put(DBHelper.Columns.SportsColumn._WEIGHT, dbSport.weight);
        if (dbSport.tag > 0)
            values.put(DBHelper.Columns.SportsColumn._TAG, dbSport.tag);

        int result;
        result = DBHelper.getInstance().getDb().update(DBHelper.Tables.T_SPORTS, values, "id=?", new String[]{String.valueOf(dbSport.id)});
        if (result > 0) {
            updateSportList();
        }
        return result;
    }


    private void updateSportCategoryList() {
        String sql = "select * from " + DBHelper.Tables.T_SPORT_CATEGORY;
        Cursor cursor = DBHelper.getInstance().getDb().rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            mDBSportCategoryList.clear();
            cursor.moveToFirst();
            do {
                DBSportCategory dbSportCategory = new DBSportCategory();
                dbSportCategory.id = cursor.getInt(0);
                dbSportCategory.icon = cursor.getString(1);
                dbSportCategory.img = cursor.getString(2);
                dbSportCategory.name = cursor.getString(3);
                mDBSportCategoryList.add(dbSportCategory);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    private void updateDietsList() {
        String sql = "select * from " + DBHelper.Tables.T_DIETS;
        Cursor cursor = DBHelper.getInstance().getDb().rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            mDBDietList.clear();
            cursor.moveToFirst();
            do {
                DBDiet dbDiet = new DBDiet();
                dbDiet.id = cursor.getInt(0);
                dbDiet.name = cursor.getString(1);
                dbDiet.icon = cursor.getString(2);
                dbDiet.des = cursor.getString(3);
                mDBDietList.add(dbDiet);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }

    public List<DBSport> getDBSportList() {
        List<DBSport> dbSportList = new ArrayList<>();
        if (mDBSportList.size() == 0) {
            updateSportList();
        }
        dbSportList.addAll(mDBSportList);
        return dbSportList;
    }

    public DBSport findDBSportById(int sport_id) {
        for (DBSport dbSport : getDBSportList()) {
            if (dbSport.id == sport_id) {
                return dbSport;
            }
        }
        return null;
    }

    public List<DBSport> getDBSportList(int category_id) {
        List<DBSport> dbSportList = new ArrayList<>();
        for (DBSport dbSport : getDBSportList()) {
            if (dbSport.category_id == category_id) {
                dbSportList.add(dbSport);
            }
        }
        return dbSportList;
    }

    public List<DBSport> findDBSportByName(String name) {
        List<DBSport> dbSportList = new ArrayList<>();
        for (DBSport dbSport : getDBSportList()) {
            if (dbSport.name.contains(name)) {
                dbSportList.add(dbSport);
            }
        }
        return dbSportList;
    }

    public DBSportCategory findDBSportCategoryById(int id) {
        List<DBSportCategory> dbSportCategoryList = new ArrayList<>();
        for (DBSportCategory dbSportCategory : getDBSportCategoryList()) {
            if (dbSportCategory.id == id) {
                return dbSportCategory;
            }
        }
        return null;
    }

    public List<DBSportCategory> getDBSportCategoryList() {
        if (mDBSportCategoryList.size() == 0) {
            updateSportCategoryList();
        }
        return mDBSportCategoryList;
    }


    public List<DBDiet> getDBDietList() {
        if (mDBDietList.size() == 0) {
            updateDietsList();
        }
        return mDBDietList;
    }

    public void clear() {
        mDBDietList.clear();
        mDBSportCategoryList.clear();
        mDBSportList.clear();
    }
}

package com.gezhii.fitgroup.model;

import android.text.TextUtils;

import com.gezhii.fitgroup.api.API;
import com.gezhii.fitgroup.api.APICallBack;
import com.gezhii.fitgroup.dto.SigninsDto;
import com.gezhii.fitgroup.event.SignRecordDataChangeEvent;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.gezhii.fitgroup.tools.TimeHelper;
import com.google.gson.reflect.TypeToken;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import de.greenrobot.event.EventBus;

/**
 * Created by xianrui on 15/11/26.
 */
public class SignRecordModel {

    public static final String TAG_SIGN_RECORD_MODEL_CACHE = "tag_sign_record_model_cache";


    boolean isLoading;
    SignRecordCache mSignRecordCache;

    private static class SignRecordModelHolder {
        public final static SignRecordModel sington = new SignRecordModel();
    }

    public static SignRecordModel getInstance() {
        return SignRecordModelHolder.sington;
    }

    private SignRecordModel() {
        String mSignRecordCacheString = DataKeeperHelper.getInstance().getDataKeeper().get(TAG_SIGN_RECORD_MODEL_CACHE, "");
        if (TextUtils.isEmpty(mSignRecordCacheString)) {
            Calendar beginCalendar = Calendar.getInstance();
            beginCalendar.add(Calendar.YEAR, -1);
            API.getSigninsHttp(UserModel.getInstance().getUserId(), beginCalendar.getTime(), Calendar.getInstance().getTime(), new APICallBack() {
                @Override
                public void subRequestSuccess(String response) throws NoSuchFieldException {
                    setValue(SigninsDto.parserJson(response));
                }
            });
            mSignRecordCache = new SignRecordCache();
            mSignRecordCache.signRecordHashMap = new HashMap<>();
        } else {
            mSignRecordCache = GsonHelper.getInstance().getGson().fromJson(mSignRecordCacheString, new TypeToken<SignRecordCache>() {
            }.getType());
        }
    }

    public boolean getIsSign(Date date) {
        Integer i = mSignRecordCache.signRecordHashMap.get(TimeHelper.dateFormat.format(date.getTime()));
        if (i == null) {
            if (!isLoading) {
                API.getSigninsHttp(UserModel.getInstance().getUserId(), date, date, new APICallBack() {
                    @Override
                    public void subRequestSuccess(String response) throws NoSuchFieldException {
                        setValue(SigninsDto.parserJson(response));
                        isLoading = false;
                    }
                });
                isLoading = true;
            }

            return false;
        } else {
            return i != 0;
        }
    }


    private void setValue(SigninsDto signinsDto) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(signinsDto.getBegin_date().getTime());
        for (Integer isSignin : signinsDto.getFlags()) {
            mSignRecordCache.signRecordHashMap.put(TimeHelper.dateFormat.format(calendar.getTime()), isSignin);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
        }
        EventBus.getDefault().post(new SignRecordDataChangeEvent());
        save();
    }

    public void setDateSignin(Date date) {
        mSignRecordCache.signRecordHashMap.put(TimeHelper.dateFormat.format(date), 1);
        save();
    }


    private void save() {
        DataKeeperHelper.getInstance().getDataKeeper()
                .put(TAG_SIGN_RECORD_MODEL_CACHE, GsonHelper.getInstance().getGson().toJson(mSignRecordCache));
    }

    public void clear() {
        mSignRecordCache = new SignRecordCache();
        mSignRecordCache.signRecordHashMap = new HashMap<>();
        DataKeeperHelper.getInstance().getDataKeeper()
                .put(TAG_SIGN_RECORD_MODEL_CACHE, "");
    }


    private static class SignRecordCache {
        public HashMap<String, Integer> signRecordHashMap;
    }
}

package com.gezhii.fitgroup.model;

import com.gezhii.fitgroup.dto.SigninDto;
import com.gezhii.fitgroup.tools.DataKeeperHelper;
import com.gezhii.fitgroup.tools.GsonHelper;

/**
 * Created by xianrui on 15/10/26.
 */
public class SignCacheModel {

    private static class SignCacheModelHolder {
        public final static SignCacheModel sington = new SignCacheModel();
    }

    public static SignCacheModel getInstance() {
        return SignCacheModelHolder.sington;
    }

    public void putSign(SigninDto signinDto) {
        DataKeeperHelper.getInstance().getSignCacheDataKeeper().put(String.valueOf(signinDto.getSignin().getId()), GsonHelper.getInstance().getGson().toJson(signinDto));
    }

    public SigninDto getSign(int sigin_id) {
        return SigninDto.parserJson(DataKeeperHelper.getInstance().getSignCacheDataKeeper().get(String.valueOf(sigin_id), ""));
    }


}

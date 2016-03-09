package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.Signin;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by xianrui on 15/10/26.
 */
public class SigninDto extends BaseDto {
    private Signin signin;
    private boolean isLiked;

    public Signin getSignin() {
        return signin;
    }

    public void setSignin(Signin signin) {
        this.signin = signin;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setIsLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public static SigninDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (SigninDto) gson.fromJson(jsonString, new TypeToken<SigninDto>() {
        }.getType());
    }
}

package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.PushArticleAndSignin;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by isansmith on 15/12/30.
 */
public class PushArticleAndSigninDto {
    public List<PushArticleAndSignin> content;

    public List<PushArticleAndSignin> getContent() {
        return content;
    }

    public static PushArticleAndSigninDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (PushArticleAndSigninDto) gson.fromJson(jsonString, new TypeToken<PushArticleAndSigninDto>() {
        }.getType());
    }

}

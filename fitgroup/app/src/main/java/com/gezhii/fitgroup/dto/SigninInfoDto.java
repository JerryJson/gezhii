package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.SigninInfoDiet;
import com.gezhii.fitgroup.dto.basic.SigninInfoSport;
import com.gezhii.fitgroup.tools.GsonHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * Created by xianrui on 15/10/23.
 */
public class SigninInfoDto {
    List<SigninInfoSport> sports;
    List<SigninInfoDiet> diets;
    String content;
    String image;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<SigninInfoSport> getSports() {
        return sports;
    }

    public void setSports(List<SigninInfoSport> sports) {
        this.sports = sports;
    }

    public List<SigninInfoDiet> getDiets() {
        return diets;
    }

    public void setDiets(List<SigninInfoDiet> diets) {
        this.diets = diets;
    }

    public static SigninInfoDto parserJson(String jsonString) {
        Gson gson = GsonHelper.getInstance().getGson();
        return (SigninInfoDto) gson.fromJson(jsonString, new TypeToken<SigninInfoDto>() {
        }.getType());
    }


}

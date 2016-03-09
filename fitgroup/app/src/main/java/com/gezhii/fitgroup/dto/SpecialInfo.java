package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.SameSigninUserInfoDTO;

import java.util.List;

/**
 * Created by fantasy on 16/2/23.
 */
public class SpecialInfo {
    int same_signin_user_count;
    List<SameSigninUserInfoDTO> same_signin_user_info;

    public int getSame_signin_user_count() {
        return same_signin_user_count;
    }

    public void setSame_signin_user_count(int same_signin_user_count) {
        this.same_signin_user_count = same_signin_user_count;
    }

    public List<SameSigninUserInfoDTO> getSame_signin_user_info() {
        return same_signin_user_info;
    }

    public void setSame_signin_user_info(List<SameSigninUserInfoDTO> same_signin_user_info) {
        this.same_signin_user_info = same_signin_user_info;
    }
}

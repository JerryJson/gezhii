package com.gezhii.fitgroup.event;

/**
 * Created by fantasy on 16/2/23.
 */
public class JustSigninEvent { //只打卡，没有发表图片或文字
    int signin_id;

    public JustSigninEvent(int signin_id) {
        this.signin_id = signin_id;
    }

    public int getSignin_id() {
        return signin_id;
    }

    public void setSignin_id(int signin_id) {
        this.signin_id = signin_id;
    }

}

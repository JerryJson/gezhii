package com.gezhii.fitgroup.event;

import com.gezhii.fitgroup.dto.basic.SigninInfoContent;

/**
 * Created by xianrui on 15/11/2.
 */
public class AddSignContentEvent {
    SigninInfoContent signinInfoContent;

    public AddSignContentEvent(SigninInfoContent signinInfoContent) {
        this.signinInfoContent = signinInfoContent;
    }

    public SigninInfoContent getSigninInfoContent() {
        return signinInfoContent;
    }
}

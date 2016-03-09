package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by xianrui on 15/10/22.
 */
public class SignMessageExt extends MessageExt {

    private String signin_id;

    public SignMessageExt(String name, String icon, String signin_id, String user_id) {
        super(name, icon, user_id);
        this.signin_id = signin_id;
        type = 10;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", type);
        emMessage.setAttribute("signin_id", signin_id);
    }
}

package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by xianrui on 15/10/22.
 */
public class LikeSigninMessageExt extends MessageExt {
    private String signin_id;

    public LikeSigninMessageExt(String name, String icon, String user_id, String signin_id) {
        super(name, icon, user_id);
        this.signin_id = signin_id;
        this.type = 11;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", type);
        emMessage.setAttribute("signin_id", signin_id);
    }
}

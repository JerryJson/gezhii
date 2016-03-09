package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by xianrui on 15/10/28.
 */
public class ApplyJoinMessageExt extends MessageExt {
    public ApplyJoinMessageExt(String sender_nick_name, String sender_icon, String user_id) {
        super(sender_nick_name, sender_icon, user_id);
        this.type=42;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", 42);
    }
}

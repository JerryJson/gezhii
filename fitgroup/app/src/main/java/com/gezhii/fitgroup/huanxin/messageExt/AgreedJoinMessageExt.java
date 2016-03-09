package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by xianrui on 15/10/28.
 */
public class AgreedJoinMessageExt extends MessageExt {
    public AgreedJoinMessageExt(String sender_nick_name, String sender_icon, String user_id) {
        super(sender_nick_name, sender_icon, user_id);
        this.type = 40;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", 40);
    }
}

package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by fantasy on 16/1/5.
 */
public class ATailMessageExt extends MessageExt {

    public ATailMessageExt(String sender_nick_name, String sender_icon, String user_id) {
        super(sender_nick_name, sender_icon, user_id);
        this.type = 46;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", this.type);
    }
}

package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by isansmith on 15/12/30.
 */
public class PushArticleSigninMessageExt extends MessageExt {

    public PushArticleSigninMessageExt(String sender_nick_name, String sender_icon, String user_id) {
        super(sender_nick_name, sender_icon, user_id);
        this.type = 23;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", type);
    }
}

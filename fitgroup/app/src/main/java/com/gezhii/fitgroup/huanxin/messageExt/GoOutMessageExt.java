package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by fantasy on 15/11/24.
 */
public class GoOutMessageExt extends MessageExt{

    private String group_name;

    public GoOutMessageExt(String sender_nick_name, String sender_icon, String user_id,String group_name) {
        super(sender_nick_name, sender_icon, user_id);
        this.group_name = group_name;
        this.type=43;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", 43);
        emMessage.setAttribute("group_name",group_name);
    }
}

package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by xianrui on 15/10/22.
 */
public class NewMemberMessageExt extends MessageExt {
    String new_member_nick_name;

    public NewMemberMessageExt(String name, String icon, String user_id, String nick_name) {
        super(name, icon, user_id);
        this.new_member_nick_name = nick_name;
        type = 13;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", type);
        emMessage.setAttribute("new_member_nick_name", new_member_nick_name);
    }
}

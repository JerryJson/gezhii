package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by xianrui on 15/10/22.
 */
public class MessageExt {
    String sender_nick_name;
    String sender_icon;
    String user_id;
    int type;

    public MessageExt(String sender_nick_name, String sender_icon, String user_id) {
        this.sender_nick_name = sender_nick_name;
        this.sender_icon = sender_icon;
        this.user_id = user_id;
    }

    public void createExt(EMMessage emMessage) {
        emMessage.setAttribute("sender_nick_name", sender_nick_name);
        emMessage.setAttribute("sender_icon", sender_icon);
        emMessage.setAttribute("user_id", user_id);
        // emMessage.setAttribute("type",1);//0文字,1图片
//        emMessage.setAttribute("em_apns_ext", );
    }

    public int getType() {
        return type;
    }
}




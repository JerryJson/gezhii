package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by xianrui on 15/10/28.
 */
public class EmojiMessageExt extends MessageExt {
    private String emoji_code;

    public EmojiMessageExt(String sender_nick_name, String sender_icon, String user_id, String emoji_code) {
        super(sender_nick_name, sender_icon, user_id);
        this.type = 47;
        this.emoji_code = emoji_code;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", this.type);
        emMessage.setAttribute("emoji_code",this.emoji_code);
    }
}

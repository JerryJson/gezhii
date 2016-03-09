package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by xianrui on 15/10/28.
 */
public class EmojiAndTextMessageExt extends MessageExt {
    private String emoji_and_text;

    public EmojiAndTextMessageExt(String sender_nick_name, String sender_icon, String user_id, String emoji_and_text) {
        super(sender_nick_name, sender_icon, user_id);
        this.type = 48;
        this.emoji_and_text = emoji_and_text;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", this.type);
        emMessage.setAttribute("emoji_and_text",this.emoji_and_text);
    }
}

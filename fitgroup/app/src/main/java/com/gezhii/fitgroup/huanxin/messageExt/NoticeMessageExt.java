package com.gezhii.fitgroup.huanxin.messageExt;

import com.easemob.chat.EMMessage;

/**
 * Created by xianrui on 15/10/22.
 */
public class NoticeMessageExt extends MessageExt {
    String notice_id;

    public NoticeMessageExt(String name, String icon, String user_id, String notice_id) {
        super(name, icon, user_id);
        this.notice_id = notice_id;
        type = 12;
    }

    @Override
    public void createExt(EMMessage emMessage) {
        super.createExt(emMessage);
        emMessage.setAttribute("type", type);
        emMessage.setAttribute("notice_id", notice_id);
    }
}

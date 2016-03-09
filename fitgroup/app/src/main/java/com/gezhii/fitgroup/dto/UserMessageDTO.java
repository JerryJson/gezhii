package com.gezhii.fitgroup.dto;

import com.gezhii.fitgroup.dto.basic.AboutMeMessage;

import java.io.Serializable;
import java.util.List;

/**
 * Created by fantasy on 16/2/22.
 */
public class UserMessageDTO implements Serializable {
    List<AboutMeMessage> aboutMeMessageList;

    public boolean getHasNewMessage() {
        return hasNewMessage;
    }

    public void setHasNewMessage(boolean hasNewMessage) {
        this.hasNewMessage = hasNewMessage;
    }

    boolean hasNewMessage;

    public List<AboutMeMessage> getAboutMeMessageList() {
        return aboutMeMessageList;
    }

    public void setAboutMeMessageList(List<AboutMeMessage> aboutMeMessageList) {
        this.aboutMeMessageList = aboutMeMessageList;
    }
}

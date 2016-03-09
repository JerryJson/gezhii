package com.gezhii.fitgroup.event;

/**
 * Created by fantasy on 16/2/23.
 */
public class CommentATailEvent {
    String userName;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public CommentATailEvent(String userName) {
        this.userName = userName;
    }

}

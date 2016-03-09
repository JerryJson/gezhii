package com.gezhii.fitgroup.dto.basic;

import java.io.Serializable;

/**
 * Created by fantasy on 15/12/10.
 */
public class SignLeaveOrContent implements Serializable{
    private Boolean isLeaveSign;
    private String content;
    public String img;
    public Boolean getIsLeaveSign() {
        return isLeaveSign;
    }

    public void setIsLeaveSign(Boolean isLeaveSign) {
        this.isLeaveSign = isLeaveSign;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}

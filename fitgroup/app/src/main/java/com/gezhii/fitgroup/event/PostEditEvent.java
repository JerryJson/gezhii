package com.gezhii.fitgroup.event;

/**
 * Created by fantasy on 16/3/2.
 */
public class PostEditEvent {
    String post_content;

    public String getPost_content() {
        return post_content;
    }

    public void setPost_content(String post_content) {
        this.post_content = post_content;
    }

    public PostEditEvent(String content) {
        this.post_content = content;
    }
}

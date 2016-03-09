package com.gezhii.fitgroup.event;

/**
 * Created by zj on 16/2/21.
 */
public class FollowStateChangeEvent {
    int followOrCancle;//0,1,关注，取消

    public int getFollowOrCancle() {
        return followOrCancle;
    }

    public void setFollowOrCancle(int followOrCancle) {
        this.followOrCancle = followOrCancle;
    }

    public FollowStateChangeEvent(int followOrCancle) {
        this.followOrCancle = followOrCancle;
    }
}

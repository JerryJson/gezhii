package com.gezhii.fitgroup.event;

import java.util.HashMap;

/**
 * Created by fantasy on 15/12/29.
 */
public class JumpToGroupApplicationEvent {
    HashMap<String, Object> params;

    public JumpToGroupApplicationEvent(HashMap<String, Object> params) {
        this.params = params;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }
}

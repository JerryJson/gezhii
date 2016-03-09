package com.gezhii.fitgroup.event;

import java.util.HashMap;

/**
 * Created by fantasy on 15/12/30.
 */
public class ATailEvent {
    HashMap<String, String> params;

    public ATailEvent(HashMap<String, String> params) {
        this.params = params;
    }

    public HashMap<String, String> getParams() {
        return params;
    }

    public void setParams(HashMap<String, String> params) {
        this.params = params;
    }
}

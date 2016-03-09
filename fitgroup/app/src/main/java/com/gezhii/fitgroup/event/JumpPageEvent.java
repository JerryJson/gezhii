package com.gezhii.fitgroup.event;

import com.gezhii.fitgroup.ui.fragment.BaseFragment;

import java.util.HashMap;

/**
 * Created by xianrui on 15/11/16.
 */
public class JumpPageEvent {

    Class<? extends BaseFragment> c;
    HashMap<String, Object> params;

    public JumpPageEvent(Class<? extends BaseFragment> c) {
        this.c = c;
    }

    public JumpPageEvent(Class<? extends BaseFragment> c, HashMap<String, Object> params) {
        this.c = c;
        this.params = params;
    }

    public Class<?> getJumpClass() {
        return c;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void setParams(HashMap<String, Object> params) {
        this.params = params;
    }
}

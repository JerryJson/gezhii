package com.gezhii.fitgroup.event;

/**
 * Created by xianrui on 15/11/17.
 */
public class ScannerQRCodeResultEvent  {
    String code;

    public ScannerQRCodeResultEvent(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}

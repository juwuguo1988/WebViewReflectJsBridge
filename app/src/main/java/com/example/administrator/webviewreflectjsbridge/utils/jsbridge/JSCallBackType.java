package com.example.administrator.webviewreflectjsbridge.utils.jsbridge;

/**
 * Created by Juwuguo on 2017/5/5.
 */

public enum JSCallBackType {
    SUCCESS(0), ERROR(1);

    private int value;

    JSCallBackType(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }
}

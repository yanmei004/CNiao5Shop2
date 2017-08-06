package com.cniao5.cniao5shop.msg;

import java.io.Serializable;

/**
 * 响应基类
 */
public class BaseResMsg implements Serializable{
    public final static int STATUS_SUCCESS = 1;
    public final static int STATUS_ERROR = 2;
    public final static String MSG_SUCCESS = "success";

    protected int status = STATUS_SUCCESS;
    protected String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

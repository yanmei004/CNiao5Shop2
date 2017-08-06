package com.cniao5.cniao5shop.msg;

public class LoginRespMsg<T> extends BaseResMsg {

    private String token;

    private T data;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}

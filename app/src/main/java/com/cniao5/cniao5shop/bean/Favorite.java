package com.cniao5.cniao5shop.bean;

import java.io.Serializable;

/**
 * 收藏
 */
public class Favorite implements Serializable{

    private Long id;
    private Long userId;
    private String createTime;
    private Wares wares;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Wares getWares() {
        return wares;
    }

    public void setWares(Wares wares) {
        this.wares = wares;
    }
}

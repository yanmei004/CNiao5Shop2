package com.cniao5.cniao5shop.bean;

import java.io.Serializable;

/**
 * banner轮播
 */
public class Banner implements Serializable{

    private Long id;
    private String name;//广告名称
    private String imgUrl;//图片url地址
    private String description;//广告描述
    public int type;//类型

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

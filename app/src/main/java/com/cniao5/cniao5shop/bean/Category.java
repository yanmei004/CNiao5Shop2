package com.cniao5.cniao5shop.bean;

/**
 * 分类页左部导航数据
 */
public class Category{


    private Long id;
    private String name;
    private int sort;

    public Category(Long id, String name, int sort) {
        this.id = id;
        this.name = name;
        this.sort = sort;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}

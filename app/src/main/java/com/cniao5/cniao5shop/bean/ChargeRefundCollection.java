package com.cniao5.cniao5shop.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Charge支付
 */
public class ChargeRefundCollection implements Serializable{

    private String object;
    private String url;
    private Boolean has_more;
    private List<?> data;

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public List<?> getData() {
        return data;
    }

    public void setData(List<?> data) {
        this.data = data;
    }

    public Boolean getHas_more() {
        return has_more;
    }

    public void setHas_more(Boolean has_more) {
        this.has_more = has_more;
    }

    public String getURL() {
        return url;
    }

    public void setURL(String url) {
        this.url = url;
    }
}

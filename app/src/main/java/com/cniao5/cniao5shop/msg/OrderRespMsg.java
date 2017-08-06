package com.cniao5.cniao5shop.msg;

import com.cniao5.cniao5shop.bean.Charge;

/**
 * 订单数据返还
 */
public class OrderRespMsg {
    private String orderNum;
    private Charge charge;


    public String getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(String orderNum) {
        this.orderNum = orderNum;
    }

    public Charge getCharge() {
        return charge;
    }

    public void setCharge(Charge charge) {
        this.charge = charge;
    }
}

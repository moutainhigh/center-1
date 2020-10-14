package com.cmall.groupcenter.homehas.model;

public class RsyncModelCancelReturnOrder {

    //因为接口过来的串的名字就是ORDERID，所以此处的属性名称为了在不
    //在不修改内部代码的前提下能够注入成功才使用了大写
    private String ORDERID;
    //获取的订单id
    public String getORDERID() {
        return ORDERID;
    }public void setORDERID(String ORDERID) {
        this.ORDERID = ORDERID;
    }
}

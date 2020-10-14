package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询取消销退订单返回接口
 * 
 * @author srnpr
 * 
 */
public class RsyncResponseDoOrderStatus extends RsyncResponseBase {

    private List<RsyncModelShipmentStat> result = new ArrayList<RsyncModelShipmentStat>();

    public List<RsyncModelShipmentStat> getResult() {
        return result;
    }

    public void setResult(List<RsyncModelShipmentStat> result) {
        this.result = result;
    }

}

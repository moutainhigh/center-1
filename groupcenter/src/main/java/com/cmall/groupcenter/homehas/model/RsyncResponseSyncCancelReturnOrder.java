package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 查询取消销退订单返回接口
 * 
 * @author srnpr
 * 
 */
public class RsyncResponseSyncCancelReturnOrder extends RsyncResponseBase {

    //	订单总条数
    private String totalCounts;


    private List<RsyncModelCancelReturnOrder> result = new ArrayList<RsyncModelCancelReturnOrder>();

    public List<RsyncModelCancelReturnOrder> getResult() {
        return result;
    }

    public void setResult(List<RsyncModelCancelReturnOrder> result) {
        this.result = result;
    }
}

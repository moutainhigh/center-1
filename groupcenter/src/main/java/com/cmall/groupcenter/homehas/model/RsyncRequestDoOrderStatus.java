package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 查询取消销退订单
 * @author lipengfei
 * @date 2015-09-10
 * @time 14:49
 */
public class RsyncRequestDoOrderStatus implements IRsyncRequest {

    private String start_time = "";

    private String end_time = "";



    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }


}

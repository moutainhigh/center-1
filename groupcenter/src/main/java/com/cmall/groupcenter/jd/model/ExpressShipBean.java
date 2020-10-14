package com.cmall.groupcenter.jd.model;

import java.util.List;

public class ExpressShipBean {
	private long jdOrderId;
    private List<OrderTrackBean> orderTrack;
    private List<WaybillCodeBean> waybillCode;

    public long getJdOrderId() {
        return jdOrderId;
    }

    public void setJdOrderId(long jdOrderId) {
        this.jdOrderId = jdOrderId;
    }

    public List<OrderTrackBean> getOrderTrack() {
        return orderTrack;
    }

    public void setOrderTrack(List<OrderTrackBean> orderTrack) {
        this.orderTrack = orderTrack;
    }

    public List<WaybillCodeBean> getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(List<WaybillCodeBean> waybillCode) {
        this.waybillCode = waybillCode;
    }

    public static class OrderTrackBean {
        /**
         * content : 您提交了订单，请等待系统确认
         * msgTime : 2019-05-27 13:45:42
         * operator : 客户
         */

        private String content;
        private String msgTime;
        private String operator;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getMsgTime() {
            return msgTime;
        }

        public void setMsgTime(String msgTime) {
            this.msgTime = msgTime;
        }

        public String getOperator() {
            return operator;
        }

        public void setOperator(String operator) {
            this.operator = operator;
        }
    }

    public static class WaybillCodeBean {
        /**
         * parentId : 0
         * deliveryOrderId : 96379292647
         * carrier : 北京杜仲公园营业部
         * orderId : 96379292647
         */

        private String parentId;
        private String deliveryOrderId;
        private String carrier;
        private String orderId;

        public String getParentId() {
            return parentId;
        }

        public void setParentId(String parentId) {
            this.parentId = parentId;
        }

        public String getDeliveryOrderId() {
            return deliveryOrderId;
        }

        public void setDeliveryOrderId(String deliveryOrderId) {
            this.deliveryOrderId = deliveryOrderId;
        }

        public String getCarrier() {
            return carrier;
        }

        public void setCarrier(String carrier) {
            this.carrier = carrier;
        }

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }
    }
}

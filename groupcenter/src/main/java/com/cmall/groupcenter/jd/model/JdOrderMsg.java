package com.cmall.groupcenter.jd.model;

import java.util.List;

public class JdOrderMsg {
	private int pOrder;
    private int orderState;
    private int orderType;
    private long jdOrderId;
    private int state;
    private int submitState;
    private int type;
    private int jdOrderState;
    private int freight;
    private double orderPrice;
    private double orderNakedPrice;
    private double orderTaxPrice;
    private List<SkuBean> sku;

    public int getPOrder() {
        return pOrder;
    }

    public void setPOrder(int pOrder) {
        this.pOrder = pOrder;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public long getJdOrderId() {
        return jdOrderId;
    }

    public void setJdOrderId(long jdOrderId) {
        this.jdOrderId = jdOrderId;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getSubmitState() {
        return submitState;
    }

    public void setSubmitState(int submitState) {
        this.submitState = submitState;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getJdOrderState() {
        return jdOrderState;
    }

    public void setJdOrderState(int jdOrderState) {
        this.jdOrderState = jdOrderState;
    }

    public int getFreight() {
        return freight;
    }

    public void setFreight(int freight) {
        this.freight = freight;
    }

    public double getOrderPrice() {
        return orderPrice;
    }

    public void setOrderPrice(double orderPrice) {
        this.orderPrice = orderPrice;
    }

    public double getOrderNakedPrice() {
        return orderNakedPrice;
    }

    public void setOrderNakedPrice(double orderNakedPrice) {
        this.orderNakedPrice = orderNakedPrice;
    }

    public double getOrderTaxPrice() {
        return orderTaxPrice;
    }

    public void setOrderTaxPrice(double orderTaxPrice) {
        this.orderTaxPrice = orderTaxPrice;
    }

    public List<SkuBean> getSku() {
        return sku;
    }

    public void setSku(List<SkuBean> sku) {
        this.sku = sku;
    }

    public static class SkuBean {

        private int category;
        private int num;
        private double price;
        private int tax;
        private int oid;
        private String name;
        private double taxPrice;
        private String skuId;
        private double nakedPrice;
        private int type;

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public int getTax() {
            return tax;
        }

        public void setTax(int tax) {
            this.tax = tax;
        }

        public int getOid() {
            return oid;
        }

        public void setOid(int oid) {
            this.oid = oid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getTaxPrice() {
            return taxPrice;
        }

        public void setTaxPrice(double taxPrice) {
            this.taxPrice = taxPrice;
        }

        public String getSkuId() {
            return skuId;
        }

        public void setSkuId(String skuId) {
            this.skuId = skuId;
        }

        public double getNakedPrice() {
            return nakedPrice;
        }

        public void setNakedPrice(double nakedPrice) {
            this.nakedPrice = nakedPrice;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}

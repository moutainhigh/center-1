package com.cmall.ordercenter.familyhas.active;

/**
 * 活动输入参数类
 * @author jlin
 *
 */
public class ActiveReq {

	private String buyer_code;
	private String sku_code;
	private int sku_num;
	private String product_code;
	private Integer isPurchase = 0;
	
	public Integer getIsPurchase() {
		return isPurchase;
	}
	public void setIsPurchase(Integer isPurchase) {
		this.isPurchase = isPurchase;
	}
	public String getBuyer_code() {
		return buyer_code;
	}
	public void setBuyer_code(String buyer_code) {
		this.buyer_code = buyer_code;
	}
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	public int getSku_num() {
		return sku_num;
	}
	public void setSku_num(int sku_num) {
		this.sku_num = sku_num;
	}
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	} 
	
}

package com.cmall.ordercenter.model;

import java.util.ArrayList;
import java.util.List;


/**
 *优惠券类型限制
 * 
 */
public class CouponTypeLimitDTO {
	
	private List<BrandBaseInfo> brandInfoList = new ArrayList<BrandBaseInfo>();
	private List<ProductBaseInfo> productInfoList = new ArrayList<ProductBaseInfo>();
	private List<CategoryBaseInfo> categoryInfoList = new ArrayList<CategoryBaseInfo>();
	private List<String> channelCodeList = new ArrayList<String>();
	private CouponTypeLimitBaseInfo couponTypeLimit = new CouponTypeLimitBaseInfo();
	private String sellerLimit = "";
	private String paymentTypeLimit = "";
	private List<String> allowedActivityTypeList = new ArrayList<String>();
	
	public List<String> getAllowedActivityTypeList() {
		return allowedActivityTypeList;
	}
	public void setAllowedActivityTypeList(List<String> allowedActivityTypeList) {
		this.allowedActivityTypeList = allowedActivityTypeList;
	}
	public String getPaymentTypeLimit() {
		return paymentTypeLimit;
	}
	public void setPaymentTypeLimit(String paymentTypeLimit) {
		this.paymentTypeLimit = paymentTypeLimit;
	}
	public List<BrandBaseInfo> getBrandInfoList() {
		return brandInfoList;
	}
	public void setBrandInfoList(List<BrandBaseInfo> brandInfoList) {
		this.brandInfoList = brandInfoList;
	}
	public List<ProductBaseInfo> getProductInfoList() {
		return productInfoList;
	}
	public void setProductInfoList(List<ProductBaseInfo> productInfoList) {
		this.productInfoList = productInfoList;
	}
	public List<CategoryBaseInfo> getCategoryInfoList() {
		return categoryInfoList;
	}
	public void setCategoryInfoList(List<CategoryBaseInfo> categoryInfoList) {
		this.categoryInfoList = categoryInfoList;
	}
	public CouponTypeLimitBaseInfo getCouponTypeLimit() {
		return couponTypeLimit;
	}
	public void setCouponTypeLimit(CouponTypeLimitBaseInfo couponTypeLimit) {
		this.couponTypeLimit = couponTypeLimit;
	}
	public List<String> getChannelCodeList() {
		return channelCodeList;
	}
	public void setChannelCodeList(List<String> channelCodeList) {
		this.channelCodeList = channelCodeList;
	}
	public String getSellerLimit() {
		return sellerLimit;
	}
	public void setSellerLimit(String sellerLimit) {
		this.sellerLimit = sellerLimit;
	}
	
}

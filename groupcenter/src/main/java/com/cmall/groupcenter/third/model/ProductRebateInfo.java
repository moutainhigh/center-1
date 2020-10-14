package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ProductRebateInfo {

	@ZapcomApi(value="返现比例",remark = "返现比例")
	private String rebateScale="";
	
	@ZapcomApi(value="返现范围",remark = "本人:4497472500020001;一度社友:4497472500020002;二度社友:4497472500020003")
	private String rebateRange="";
	
	@ZapcomApi(value="商品编号",remark = "商品编号")
	private String productCode="";
	
	@ZapcomApi(value="sku编号",remark = "sku编号")
	private String skuCode="";
	
	
	@ZapcomApi(value="开始时间",remark = "开始时间")
	private String startTime="";
	
	@ZapcomApi(value="结束时间",remark = "结束时间")
	private String endTime="";
	
	@ZapcomApi(value="规则编号",remark = "规则编号")
	private String ruleCode="";


	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getRebateScale() {
		return rebateScale;
	}

	public void setRebateScale(String rebateScale) {
		this.rebateScale = rebateScale;
	}

	public String getRebateRange() {
		return rebateRange;
	}

	public void setRebateRange(String rebateRange) {
		this.rebateRange = rebateRange;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getRuleCode() {
		return ruleCode;
	}

	public void setRuleCode(String ruleCode) {
		this.ruleCode = ruleCode;
	}

	
	
}

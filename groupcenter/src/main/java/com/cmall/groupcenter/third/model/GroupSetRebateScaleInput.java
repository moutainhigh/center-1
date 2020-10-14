package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class GroupSetRebateScaleInput extends RootInput{

	@ZapcomApi(value = "商品编号",remark = "商品编号", require = 1)
	String productCode="";
	
	@ZapcomApi(value = "sku编号",remark = "sku编号", require = 1)
	String skuCode="";
	
	@ZapcomApi(value = "返现比例",remark = "返现比例,示例：10,20,30,40", require = 1)
	String rebateScale="";
	
	@ZapcomApi(value = "开始时间",remark = "开始时间,2015-04-20 11:23:23", require = 1,verify="base=datetime")
	String startTime="";
	
	@ZapcomApi(value = "结束时间",remark = "结束时间,2015-05-01 10:23:23", require = 1,verify="base=datetime")
	String endTime="";

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
	
	
}

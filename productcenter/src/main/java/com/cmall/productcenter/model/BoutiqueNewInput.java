package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class BoutiqueNewInput  extends RootInput{
	/**
	 * 开始时间 
	 */
	@ZapcomApi(value="开始时间")
	private String startPrice= "";
	/**
	 * 结束时间
	 */
	@ZapcomApi(value="结束时间")
	private String endPrice ="";
	/**
	 * 分类编码
	 */
	@ZapcomApi(value="分类编码")
	private String categoryCode = "";
	
	/**
	 * 价格排序
	 */
	@ZapcomApi(value="价格排序")
	private String price = "";

	public String getStartPrice() {
		return startPrice;
	}

	public void setStartPrice(String startPrice) {
		this.startPrice = startPrice;
	}

	public String getEndPrice() {
		return endPrice;
	}

	public void setEndPrice(String endPrice) {
		this.endPrice = endPrice;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
}

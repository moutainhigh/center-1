package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 
 * 
 * 项目名称：productcenter 
 * 类名称：     HighendProductInfo 
 * 类描述：     高端商品信息
 * 创建人：     GaoYang
 * 创建时间：2013年12月4日上午10:38:48 
 * 修改人：     GaoYang
 * 修改时间：2013年12月4日上午10:38:48
 * 修改备注：  
 * @version
 *
 */
public class HighendProductInfo extends BaseClass{
	
	/**
	 * 高端商品编码
	 */
	private String productCode = "";

	/**
	 * 高端商品名称
	 */
	private String highendProductName = "";
	
	/**
	 * 最低销售价格
	 */
	private String minSellPrice = "";
	
	/**
	 * 商品主图URL
	 */
	private String mainpicUrl = "";

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getHighendProductName() {
		return highendProductName;
	}

	public void setHighendProductName(String highendProductName) {
		this.highendProductName = highendProductName;
	}

	public String getMinSellPrice() {
		return minSellPrice;
	}

	public void setMinSellPrice(String minSellPrice) {
		this.minSellPrice = minSellPrice;
	}

	public String getMainpicUrl() {
		return mainpicUrl;
	}

	public void setMainpicUrl(String mainpicUrl) {
		this.mainpicUrl = mainpicUrl;
	}
	
}

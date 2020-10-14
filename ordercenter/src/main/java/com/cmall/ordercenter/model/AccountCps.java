package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 商品的商城分成比例(结算时使用)
 * Date:     2013-10-30 下午2:31:31 <br/>
 * @author   jack
 * @version  1.0
 */
public class AccountCps extends BaseClass{
	/**
	 *商品编号 
	 */
	private String product_code = "";
	
	 /**
	  *店铺编号
	  */
	private String seller_code = "";
	
	/**
	 *商品默认cps 
	 */
	private String product_cps = "";

	/**
	 *所属品牌编号 
	 */
	private String brand_code = "";
	
	/**
	 * 所属品牌cps
	 */
	private String brand_cps = "";
	
	/**
	 *所属类目编号
	 */
	private String category_code = "";
	
	/**
	 *所属类目cps
	 */
	private String category_cps = "";
	
	/**
	 *已维护类目cps 
	 */
	private String recategory_cps = "";

	/**
	 * 获取所属品牌编号.
	 * @return  product_code
	 */
	public String getProduct_code() {
		return product_code;
	}

	/**
	 * 设置所属品牌编号.
	 * @param   product_code
	 */
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	/**
	 * 获取店铺编号.
	 * @return  seller_code
	 */
	public String getSeller_code() {
		return seller_code;
	}

	/**
	 * 设置店铺编号.
	 * @param   seller_code
	 */
	public void setSeller_code(String seller_code) {
		this.seller_code = seller_code;
	}

	/**
	 * 获取商品默认cps .
	 * @return  product_cps
	 */
	public String getProduct_cps() {
		return product_cps;
	}

	/**
	 * 设置商品默认cps .
	 * @param   product_cps
	 */
	public void setProduct_cps(String product_cps) {
		this.product_cps = product_cps;
	}

	/**
	 * 获取所属品牌编号.
	 * @return  brand_code
	 */
	public String getBrand_code() {
		return brand_code;
	}

	/**
	 * 设置所属品牌编号.
	 * @param   brand_code
	 */
	public void setBrand_code(String brand_code) {
		this.brand_code = brand_code;
	}

	/**
	 * 获取所属品牌cps.
	 * @return  brand_cps
	 */
	public String getBrand_cps() {
		return brand_cps;
	}

	/**
	 * 设置所属品牌cps.
	 * @param   brand_cps
	 */
	public void setBrand_cps(String brand_cps) {
		this.brand_cps = brand_cps;
	}

	/**
	 * 获取所属类目编号.
	 * @return  category_code
	 */
	public String getCategory_code() {
		return category_code;
	}

	/**
	 * 设置所属类目编号.
	 * @param   category_code
	 */
	public void setCategory_code(String category_code) {
		this.category_code = category_code;
	}

	/**
	 * 获取所属类目cps.
	 * @return  category_cps
	 */
	public String getCategory_cps() {
		return category_cps;
	}

	/**
	 * 设置所属类目cps.
	 * @param   category_cps
	 */
	public void setCategory_cps(String category_cps) {
		this.category_cps = category_cps;
	}

	/**
	 * 获取已维护类目cps.
	 * @return  recategory_cps
	 */
	public String getRecategory_cps() {
		return recategory_cps;
	}

	/**
	 * 设置已维护类目cps.
	 * @param   recategory_cps
	 */
	public void setRecategory_cps(String recategory_cps) {
		this.recategory_cps = recategory_cps;
	}
	
}


package com.cmall.productcenter.model;

import java.math.BigDecimal;

/**
 * 商品价格流程信息
 * @author pang_jhui
 *
 */
public class PcProductSkuPriceFlow {
	
	/*主键标识*/
	private String zid = "";
	
	/*唯一标识*/
	private String uid = "";
	
	/*流程编号*/
	private String flow_code = "";
	
	/*商品编号*/
	private String product_code = "";
	
	/*sku编码*/
	private String sku_code = "";
	
	/*成本价*/
	private BigDecimal cost_price =  BigDecimal.ZERO;
	
	/*销售价*/
	private BigDecimal sell_price = BigDecimal.ZERO;

	/**
	 * 获取主键标识
	 * @return
	 */
	public String getZid() {
		return zid;
	}

	/**
	 * 设置主键标识
	 * @param zid
	 */
	public void setZid(String zid) {
		this.zid = zid;
	}

	/**
	 * 获取唯一标识
	 * @return
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * 设置唯一标识
	 * @param uid
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * 获取流程编号
	 * @return
	 */
	public String getFlow_code() {
		return flow_code;
	}

	/**
	 * 设置流程编号
	 * @param flow_code
	 */
	public void setFlow_code(String flow_code) {
		this.flow_code = flow_code;
	}

	/**
	 * 获取产品编号
	 * @return
	 */
	public String getProduct_code() {
		return product_code;
	}

	/**
	 * 设置产品编号
	 * @param product_code
	 */
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	/**
	 * 获取sku编号
	 * @return
	 */
	public String getSku_code() {
		return sku_code;
	}

	/**
	 * 设置sku编号
	 * @param sku_code
	 */
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	/**
	 * 获取成本价
	 * @return
	 */
	public BigDecimal getCost_price() {
		return cost_price;
	}

	/**
	 * 设置成本价
	 * @param cost_price
	 */
	public void setCost_price(BigDecimal cost_price) {
		this.cost_price = cost_price;
	}

	/**
	 * 获取销售价
	 * @return
	 */
	public BigDecimal getSell_price() {
		return sell_price;
	}

	/**
	 * 设置销售价
	 * @param sell_price
	 */
	public void setSell_price(BigDecimal sell_price) {
		this.sell_price = sell_price;
	}

}

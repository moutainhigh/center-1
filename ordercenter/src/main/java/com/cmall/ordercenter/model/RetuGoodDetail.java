package com.cmall.ordercenter.model;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 
 * @author:     何旭东
 * Date:        2013年9月12日
 * project_name:ordercenter
 */
public class RetuGoodDetail extends BaseClass
{
	
	/**
	 * 商品编号
	 */
	private String sku_code =  "";
	/**
	 * 数量
	 */
	private Integer count = 0 ;
	/**
	 * 商品名称
	 */
	private String sku_name = "";
	/**
	 * 当时价格
	 */
	private BigDecimal  current_price = new BigDecimal(0.00);
	
	/**
	 * 流水号
	 */
	private String  serial_number="";
	
	
	/**
	 * sku状态
	 */
	
	private String sku_status = "";
	
	/**
	 * 商品主图
	 */
	private String product_picurl = "";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public String getSku_name() {
		return sku_name;
	}

	public void setSku_name(String sku_name) {
		this.sku_name = sku_name;
	}

	public BigDecimal getCurrent_price() {
		return current_price;
	}

	public void setCurrent_price(BigDecimal current_price) {
		this.current_price = current_price;
	}

	public String getSerial_number() {
		return serial_number;
	}

	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}

	public String getSku_status() {
		return sku_status;
	}

	public void setSku_status(String sku_status) {
		this.sku_status = sku_status;
	}

	public String getProduct_picurl() {
		return product_picurl;
	}

	public void setProduct_picurl(String product_picurl) {
		this.product_picurl = product_picurl;
	}

	public RetuGoodDetail(String sku_code, Integer count, String sku_name,
			BigDecimal current_price, String serial_number, String sku_status,
			String product_picurl) {
		super();
		this.sku_code = sku_code;
		this.count = count;
		this.sku_name = sku_name;
		this.current_price = current_price;
		this.serial_number = serial_number;
		this.sku_status = sku_status;
		this.product_picurl = product_picurl;
	}

	public RetuGoodDetail() {
		
		super();
		// TODO Auto-generated constructor stub
		
	}
	
}

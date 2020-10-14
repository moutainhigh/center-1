package com.cmall.ordercenter.model;


/**
 * 
 * @author:     何旭东
 * Date:        2013年9月12日
 * project_name:ordercenter
 */
public class RetuGoodDetailChild 
{

	/**
	 * 退货单号
	 */
	private String return_code =  "";
	
	
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
	private float  current_price = 0;
	
	/**
	 * 流水号
	 */
	private String  serial_number="";
	/**
	 * url
	 */
	private String url = "";
	public String getReturn_code() {
		return return_code;
	}
	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}
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
	public float getCurrent_price() {
		return current_price;
	}
	public void setCurrent_price(float current_price) {
		this.current_price = current_price;
	}
	public String getSerial_number() {
		return serial_number;
	}
	public void setSerial_number(String serial_number) {
		this.serial_number = serial_number;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public RetuGoodDetailChild(String return_code, String sku_code,
			Integer count, String sku_name, float current_price,
			String serial_number, String url) {
		super();
		this.return_code = return_code;
		this.sku_code = sku_code;
		this.count = count;
		this.sku_name = sku_name;
		this.current_price = current_price;
		this.serial_number = serial_number;
		this.url = url;
	}
	public RetuGoodDetailChild() {
		
		super();
		// TODO Auto-generated constructor stub
		
	}

	
	
}

package com.cmall.ordercenter.familyhas.active;

import java.math.BigDecimal;

/**
 * 活动执行结果类
 * @author jlin
 *
 */
public class ActiveReturn {
	
	private boolean use_activity = true ;
	
	private String activity_code = "";
	private String activity_type = "";
	
	private String start_time = "";
	private String end_time = "";
	private String eventType=  "";
	
	/**
	 * 活动计算所得价格
	 */
	private BigDecimal activity_price = BigDecimal.ZERO ;

	private String sku_code = "";
	private String product_code = "";
	
	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public String getActivity_code() {
		return activity_code;
	}

	public void setActivity_code(String activity_code) {
		this.activity_code = activity_code;
	}

	public String getActivity_type() {
		return activity_type;
	}

	public void setActivity_type(String activity_type) {
		this.activity_type = activity_type;
	}

	public BigDecimal getActivity_price() {
		return activity_price;
	}

	public void setActivity_price(BigDecimal activity_price) {
		this.activity_price = activity_price;
	}

	public boolean isUse_activity() {
		return use_activity;
	}

	public void setUse_activity(boolean use_activity) {
		this.use_activity = use_activity;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public String getStart_time() {
		return start_time;
	}

	public void setStart_time(String start_time) {
		this.start_time = start_time;
	}

	public String getEnd_time() {
		return end_time;
	}

	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
	
}

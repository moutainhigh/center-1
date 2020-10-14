package com.cmall.groupcenter.active;

import java.math.BigDecimal;

/**
 * 活动信息基类
 * @author jlin
 *
 */
public class BaseActive {

	private String activity_code;
	private String activity_type_code;
	private String activity_title;
	private String start_time;
	private String end_time;
	private String pri_sort;
	private String create_time;
	private String create_user;
	private String update_time;
	private String update_user;
	private String remark;
	private String app_code;
	
	/**
	 * 活动计算所得价格
	 */
	private BigDecimal activePrice = new BigDecimal(0.00);
	
	/** 外部活动编号*/
	private String outer_activity_code ;
	
	public String getActivity_code() {
		return activity_code;
	}
	public void setActivity_code(String activity_code) {
		this.activity_code = activity_code;
	}
	public String getActivity_type_code() {
		return activity_type_code;
	}
	public void setActivity_type_code(String activity_type_code) {
		this.activity_type_code = activity_type_code;
	}
	public String getActivity_title() {
		return activity_title;
	}
	public void setActivity_title(String activity_title) {
		this.activity_title = activity_title;
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
	public String getPri_sort() {
		return pri_sort;
	}
	public void setPri_sort(String pri_sort) {
		this.pri_sort = pri_sort;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getCreate_user() {
		return create_user;
	}
	public void setCreate_user(String create_user) {
		this.create_user = create_user;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}
	public String getUpdate_user() {
		return update_user;
	}
	public void setUpdate_user(String update_user) {
		this.update_user = update_user;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public BigDecimal getActivePrice() {
		return activePrice;
	}
	public void setActivePrice(BigDecimal activePrice) {
		this.activePrice = activePrice;
	}
	public String getApp_code() {
		return app_code;
	}
	public void setApp_code(String app_code) {
		this.app_code = app_code;
	}
	public String getOuter_activity_code() {
		return outer_activity_code;
	}
	public void setOuter_activity_code(String outer_activity_code) {
		this.outer_activity_code = outer_activity_code;
	}
	
}

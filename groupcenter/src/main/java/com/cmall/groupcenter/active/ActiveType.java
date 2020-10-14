package com.cmall.groupcenter.active;

/**
 * 活动类型
 * @author jlin
 *
 */
public class ActiveType {

	private String type_code;	
	private String type_name;
	private String pri_sort;
	private String mutex_type_code;
	private String handle_class;
	private String remark;
	
	
	
	public String getType_code() {
		return type_code;
	}
	public void setType_code(String type_code) {
		this.type_code = type_code;
	}
	public String getType_name() {
		return type_name;
	}
	public void setType_name(String type_name) {
		this.type_name = type_name;
	}
	public String getPri_sort() {
		return pri_sort;
	}
	public void setPri_sort(String pri_sort) {
		this.pri_sort = pri_sort;
	}
	public String getMutex_type_code() {
		return mutex_type_code;
	}
	public void setMutex_type_code(String mutex_type_code) {
		this.mutex_type_code = mutex_type_code;
	}
	public String getHandle_class() {
		return handle_class;
	}
	public void setHandle_class(String handle_class) {
		this.handle_class = handle_class;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	
	
}

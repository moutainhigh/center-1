package com.cmall.groupcenter.mq.model;

/**
 * 
 * @remark 活动状态
 * @author 任宏斌
 * @date 2018年10月25日
 */
public class ActivityTypeListenModel {

	/**
	 * 活动编号
	 */
	private String event_id;
	
	/**
	 * 折扣类型
	 */
	private String dis_type;
	
	/**
	 * 错误、异常信息
	 */
	private String message;

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public String getDis_type() {
		return dis_type;
	}

	public void setDis_type(String dis_type) {
		this.dis_type = dis_type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}

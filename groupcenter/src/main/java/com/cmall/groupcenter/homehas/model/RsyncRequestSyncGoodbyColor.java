package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 按商品编号查看颜色款式接口的请求参数
 * 
 * @author jl
 * 
 */
public class RsyncRequestSyncGoodbyColor implements IRsyncRequest {

	private String start_day = "";

	private String end_day = "";
	
	private String good_id="";//当传入该参数时，查询该商品所有的属性信息

	public String getStart_day() {
		return start_day;
	}

	public void setStart_day(String start_day) {
		this.start_day = start_day;
	}

	public String getEnd_day() {
		return end_day;
	}

	public void setEnd_day(String end_day) {
		this.end_day = end_day;
	}

	public String getGood_id() {
		return good_id;
	}

	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}
	
	
}

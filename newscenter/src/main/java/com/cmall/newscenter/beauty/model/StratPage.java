package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 惠美丽—启动页类
 * @author yangrong
 * date: 2014-09-10
 * @version1.0
 */
public class StratPage {

	@ZapcomApi(value = "图片地址")
	private String url = "";
	
	@ZapcomApi(value = "开始时间")
	private String start_time = "";
	
	@ZapcomApi(value = "结束时间")
	private String end_time = "";

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

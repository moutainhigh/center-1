package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicAllInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;


/**
 * 活动类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class Activity {

	@ZapcomApi(value = "活动图片")
	private String photo = "";
	
	@ZapcomApi(value = "活动名称")
	private String name = "";
	
	@ZapcomApi(value = "活动开始日期")
	private String start_time = "";
	
	@ZapcomApi(value = "活动结束日期")
	private String end_time = "";
	
	@ZapcomApi(value = "活动详情链接")
	private String url = "";
	
	@ZapcomApi(value ="分享图片")
	private String share_pic = "";
	
	@ZapcomApi(value = "分享内容")
	private String info_content = "";
	

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getShare_pic() {
		return share_pic;
	}

	public void setShare_pic(String share_pic) {
		this.share_pic = share_pic;
	}

	public String getInfo_content() {
		return info_content;
	}

	public void setInfo_content(String info_content) {
		this.info_content = info_content;
	}

	

	
	
	
	
}

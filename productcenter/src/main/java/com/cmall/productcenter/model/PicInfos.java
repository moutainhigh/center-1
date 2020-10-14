package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;


/**
 * 图片信息类
 * @author houwen
 * date 2014-12-3
 * @version 1.0
 */
public class PicInfos{

	//图片地址
	@ZapcomApi(value = "图片url")
	private String picUrl = "" ;

	//图片宽度
	@ZapcomApi(value = "图片宽度")
	private Integer width = new Integer(0);

	//图片高度
	@ZapcomApi(value = "图片高度")
	private Integer height = new Integer(0);

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}
	
}

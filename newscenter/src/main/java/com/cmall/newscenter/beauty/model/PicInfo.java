package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**
 * 图片信息类
 * @author houwen
 * date 2014-10-10
 * @version 1.0
 */
public class PicInfo {
	
	//原图片地址
	@ZapcomApi(value="图片url",remark="图片url")
	private String picOldUrl = ""  ;

	public String getPicOldUrl() {
		return picOldUrl;
	}

	public void setPicOldUrl(String picOldUrl) {
		this.picOldUrl = picOldUrl;
	}

}

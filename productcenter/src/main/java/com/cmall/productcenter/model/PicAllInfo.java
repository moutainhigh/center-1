package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**
 * 图片信息类
 * @author 图片
 * date 2014-10-10
 * @version 1.0
 */
public class PicAllInfo {
	
	//大图
	@ZapcomApi(value = "大图")
	private PicInfos bigPicInfo = new PicInfos()  ;

	//原图
	@ZapcomApi(value = "原图")
	private PicInfos picInfo = new PicInfos()  ;
		
	//小图
	@ZapcomApi(value = "小图")
	private PicInfos smallPicInfo = new PicInfos()  ;

	public PicInfos getBigPicInfo() {
		return bigPicInfo;
	}

	public void setBigPicInfo(PicInfos bigPicInfo) {
		this.bigPicInfo = bigPicInfo;
	}


	public PicInfos getPicInfo() {
		return picInfo;
	}

	public void setPicInfo(PicInfos picInfo) {
		this.picInfo = picInfo;
	}

	public PicInfos getSmallPicInfo() {
		return smallPicInfo;
	}

	public void setSmallPicInfo(PicInfos smallPicInfo) {
		this.smallPicInfo = smallPicInfo;
	}

}

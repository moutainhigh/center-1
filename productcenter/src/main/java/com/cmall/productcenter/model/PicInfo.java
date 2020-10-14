package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 图片信息类
 * @author 李国杰
 * date 2014-10-10
 * @version 1.0
 */
public class PicInfo {
	
	@ZapcomApi(value="原图片地址" , remark="") 
	private String picOldUrl = ""  ;

	@ZapcomApi(value="现图片地址" , remark="")
	private String picNewUrl = "" ;

	@ZapcomApi(value="图片宽度" , remark="")
	private Integer width = new Integer(0);

	@ZapcomApi(value="图片高度" , remark="")
	private Integer height = new Integer(0);

	@ZapcomApi(value="原图片宽度" , remark="")
	private Integer oldWidth = new Integer(0);

	@ZapcomApi(value="原图片高度" , remark="")
	private Integer oldHeight = new Integer(0);
	
	@ZapcomApi(value = "数据库中图片宽度")
	private int originWidth = 0;
	@ZapcomApi(value = "数据库中图片高度")
	private int originHeight = 0;
	

	 
	
	public int getOriginHeight() {
		return originHeight;
	}

	public void setOriginHeight(int originHeight) {
		this.originHeight = originHeight;
	}

	public int getOriginWidth() {
		return originWidth;
	}

	public void setOriginWidth(int originWidth) {
		this.originWidth = originWidth;
	}


	public String getPicOldUrl() {
		return picOldUrl;
	}

	public void setPicOldUrl(String picOldUrl) {
		this.picOldUrl = picOldUrl;
	}

	public String getPicNewUrl() {
		return picNewUrl;
	}

	public void setPicNewUrl(String picNewUrl) {
		this.picNewUrl = picNewUrl;
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

	public Integer getOldWidth() {
		return oldWidth;
	}

	public void setOldWidth(Integer oldWidth) {
		this.oldWidth = oldWidth;
	}

	public Integer getOldHeight() {
		return oldHeight;
	}

	public void setOldHeight(Integer oldHeight) {
		this.oldHeight = oldHeight;
	}
	
}

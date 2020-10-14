package com.cmall.ordercenter.model;



/**
 * 图片信息类 
 * @author zhaoxq 
 * @version 1.0
 */
public class PicInfo {
	
	//原图片地址
	private String picOldUrl = ""  ;

	//现图片地址
	private String picNewUrl = "" ;

	//图片宽度
	private Integer width = new Integer(0);

	//图片高度
	private Integer height = new Integer(0);

	//原图片宽度
	private Integer oldWidth = new Integer(0);

	//原图片高度
	private Integer oldHeight = new Integer(0);


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

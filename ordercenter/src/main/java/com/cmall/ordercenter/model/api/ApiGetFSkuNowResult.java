package com.cmall.ordercenter.model.api;

import java.util.LinkedList;
import java.util.List;

import com.cmall.ordercenter.model.FlashsalesSkuInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 返回闪购商品简单信息
 * @author jl
 *
 */
public class ApiGetFSkuNowResult extends RootResultWeb {

	@ZapcomApi(value="闪购商品SKU集合",demo="[]")
	private List<FlashsalesSkuInfo> list=new LinkedList<FlashsalesSkuInfo>();
	
	@ZapcomApi(value="系统时间",demo="2014-12-17 00:00:00")
	private String systemTime = "";
	
	@ZapcomApi(value = "栏目图片",demo="http://qhbeta-cfiles.qhw.srnpr.com/cfiles/staticfiles/upload/22676/2bf6697a4c1a4536bcec0b0c417a7356.jpg")
	private String banner_img = "";
	
	@ZapcomApi(value = "栏目名称",demo="品牌")
	private String banner_name = "";
	
	@ZapcomApi(value = "链接地址",demo="www.baidu.com")
	private String banner_link = "";

	public List<FlashsalesSkuInfo> getList() {
		return list;
	}

	public void setList(List<FlashsalesSkuInfo> list) {
		this.list = list;
	}

	public String getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(String systemTime) {
		this.systemTime = systemTime;
	}

	public String getBanner_img() {
		return banner_img;
	}

	public void setBanner_img(String banner_img) {
		this.banner_img = banner_img;
	}

	public String getBanner_name() {
		return banner_name;
	}

	public void setBanner_name(String banner_name) {
		this.banner_name = banner_name;
	}

	public String getBanner_link() {
		return banner_link;
	}

	public void setBanner_link(String banner_link) {
		this.banner_link = banner_link;
	}
	
	
}

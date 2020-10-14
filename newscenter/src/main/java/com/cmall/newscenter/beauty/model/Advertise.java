package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PicAllInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 广告列表类
 * @author houwen
 * date 2014-08-25
 * @version 1.0
 */
public class Advertise {
	
	@ZapcomApi(value="广告id")
	private String ad_code  = "";

	@ZapcomApi(value="广告名称",demo="惠美丽广告")
	private String ad_name ;
	
	@ZapcomApi(value="链接地址",remark="商品ID类型：以code@@开头；url类型:以url@@开头",demo="商品ID类型：code@@2223243;url类型：url@@http://baidu.com",require = 1)
	private String adImg_url = "";
	
	@ZapcomApi(value="图片",demo="http://aa.com/cfiles/upload/22613/68667b0853.jpg",require = 1)
	private String adImg = "";

	@ZapcomApi(value="版位名称",demo="AdP140728100001",require = 1)
	private String place_code = "";

	@ZapcomApi(value="排序",demo="1")
	private int ad_sort;

	@ZapcomApi(value="状态",demo="449746690001")
	private String status = "";
	
	@ZapcomApi(value = "商品类型",remark="明确商品类型的列表不返回     0：普通商品  1：限购商品   2：试用商品")
	private String productType = "";
	
	@ZapcomApi(value = "分享标题")
	private String share_title = "";
	
	@ZapcomApi(value = "分享内容")
	private String share_cotent = "";
	
	@ZapcomApi(value = "分享图片")
	private String share_pic = "";

	public String getAd_code() {
		return ad_code;
	}

	public void setAd_code(String ad_code) {
		this.ad_code = ad_code;
	}

	public String getAd_name() {
		return ad_name;
	}

	public void setAd_name(String ad_name) {
		this.ad_name = ad_name;
	}

	public String getAdImg_url() {
		return adImg_url;
	}

	public void setAdImg_url(String adImg_url) {
		this.adImg_url = adImg_url;
	}

	public String getAdImg() {
		return adImg;
	}

	public void setAdImg(String adImg) {
		this.adImg = adImg;
	}

	public String getPlace_code() {
		return place_code;
	}

	public void setPlace_code(String place_code) {
		this.place_code = place_code;
	}

	public int getAd_sort() {
		return ad_sort;
	}

	public void setAd_sort(int ad_sort) {
		this.ad_sort = ad_sort;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getProductType() {
		return productType;
	}

	public void setProductType(String productType) {
		this.productType = productType;
	}

	public String getShare_title() {
		return share_title;
	}

	public void setShare_title(String share_title) {
		this.share_title = share_title;
	}

	public String getShare_cotent() {
		return share_cotent;
	}

	public void setShare_cotent(String share_cotent) {
		this.share_cotent = share_cotent;
	}

	public String getShare_pic() {
		return share_pic;
	}

	public void setShare_pic(String share_pic) {
		this.share_pic = share_pic;
	}

	

	
	
	
	
}

package com.cmall.newscenter.group.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 *微公社广告
 * @author jlin
 *
 */
public class ApiForAdvertiseMoreResult extends RootResultWeb {
	
	@ZapcomApi(value = "广告信息",remark="key 为位置信息， value 为具体的广告信息;" +
			"AdP150525100001:轮播"
			)
	
	private Map<String,List<Advertise>> advertiseMap = new HashMap<String, List<Advertise>>();

	public Map<String, List<Advertise>> getAdvertiseMap() {
		return advertiseMap;
	}

	public void setAdvertiseMap(Map<String, List<Advertise>> advertiseMap) {
		this.advertiseMap = advertiseMap;
	}

	public static class Advertise {
		
		@ZapcomApi(value = "广告名称",demo="热销")
		private String ad_name = "";
//		@ZapcomApi(value = "广告编号",demo="Ad140724100019")
//		private String ad_code = "";
//		@ZapcomApi(value = "广告类型",remark="Adc140724100019")
//		private String genre_code = "";
		@ZapcomApi(value = "广告位编码",remark="广告位确定唯一位置信息")
		private String place_code = "";
		
//		@ZapcomApi(value = "页面名称编码",remark="所属页面")
//		private String page_code = "";
//		@ZapcomApi(value = "栏目编码",remark="所属栏目")
//		private String column_code = "";
		
		@ZapcomApi(value = "广告图",remark="广告图片")
		private String adImg = "";
		
		@ZapcomApi(value = "点击图片链接地址",remark="点击图片链接地址")
		private String adImg_url = "";

		public String getAd_name() {
			return ad_name;
		}

		public void setAd_name(String ad_name) {
			this.ad_name = ad_name;
		}

		public String getPlace_code() {
			return place_code;
		}

		public void setPlace_code(String place_code) {
			this.place_code = place_code;
		}

		public String getAdImg() {
			return adImg;
		}

		public void setAdImg(String adImg) {
			this.adImg = adImg;
		}

		public String getAdImg_url() {
			return adImg_url;
		}

		public void setAdImg_url(String adImg_url) {
			this.adImg_url = adImg_url;
		}
		
//		@ZapcomApi(value = "广告标题",remark="广告标题")
//		private String ad_title = "";
//		@ZapcomApi(value = "链接提示",remark="链接提示")
//		private String ad_prompt = "";
		
	}
	
	
}

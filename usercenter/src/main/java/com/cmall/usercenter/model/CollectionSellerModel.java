package com.cmall.usercenter.model;

import java.util.List;

import com.cmall.ordercenter.model.OcActivity;
import com.cmall.productcenter.model.Category;
import com.cmall.productcenter.model.PcProductInfoForI;


public class CollectionSellerModel {
	
	/**
	 * 卖家编号
	 */
	private String seller_code ;
	/**
	 * 卖家名称
	 */
	private String seller_name;
	
	
	/**
	 * 卖家logo
	 */
	private String seller_logo;
	
	
	/**
	 * 商家二级域名
	 */
	private String second_level_domain;
	
	
	/**
	 * 商家二维码图片链接
	 */
	private String qrcode_link;
	
	/**
	 * 商家的 最新上架的四个商品
	 */
	private List<PcProductInfoForI> productList=null;
	
	
	/**
	 * 商家活动 只有满减和免运费
	 */
	private List<OcActivity> activityList = null;
	
	
	/**
	 * 获取该店家的主营分类
	 */
	private List<Category> categoryList = null;
	
	
	
	
	public List<Category> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<Category> categoryList) {
		this.categoryList = categoryList;
	}

	public List<OcActivity> getActivityList() {
		return activityList;
	}

	public void setActivityList(List<OcActivity> activityList) {
		this.activityList = activityList;
	}


	

	
	public String getSecond_level_domain() {
		return second_level_domain;
	}

	public void setSecond_level_domain(String second_level_domain) {
		this.second_level_domain = second_level_domain;
	}

	public String getQrcode_link() {
		return qrcode_link;
	}

	public void setQrcode_link(String qrcode_link) {
		this.qrcode_link = qrcode_link;
	}

	public String getSeller_logo() {
		return seller_logo;
	}

	public void setSeller_logo(String seller_logo) {
		this.seller_logo = seller_logo;
	}

	public String getSeller_name() {
		return seller_name;
	}

	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}

	public String getSeller_code() {
		return seller_code;
	}

	public void setSeller_code(String seller_code) {
		this.seller_code = seller_code;
	}
	

	public List<PcProductInfoForI> getProductList() {
		return productList;
	}

	public void setProductList(List<PcProductInfoForI> productList) {
		this.productList = productList;
	}
}



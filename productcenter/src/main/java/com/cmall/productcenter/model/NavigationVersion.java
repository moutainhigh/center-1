package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class NavigationVersion {
	
	@ZapcomApi(value="首页版本号")
	private String firstPageVersion = "";
	
	@ZapcomApi(value="分类版本号")
	private String assortmentVersion = "";
	
	@ZapcomApi(value="购物车版本号")
	private String shoppingCartVersion = "";
	
	@ZapcomApi(value="我的版本号")
	private String mineVersion = "";
	
	@ZapcomApi(value="背景图片版本号")
	private String backgroundVersion = "";
	
	public String getFirstPageVersion() {
		return firstPageVersion;
	}
	public void setFirstPageVersion(String firstPageVersion) {
		this.firstPageVersion = firstPageVersion;
	}
	public String getAssortmentVersion() {
		return assortmentVersion;
	}
	public void setAssortmentVersion(String assortmentVersion) {
		this.assortmentVersion = assortmentVersion;
	}
	public String getShoppingCartVersion() {
		return shoppingCartVersion;
	}
	public void setShoppingCartVersion(String shoppingCartVersion) {
		this.shoppingCartVersion = shoppingCartVersion;
	}
	public String getMineVersion() {
		return mineVersion;
	}
	public void setMineVersion(String mineVersion) {
		this.mineVersion = mineVersion;
	}
	public String getBackgroundVersion() {
		return backgroundVersion;
	}
	public void setBackgroundVersion(String backgroundVersion) {
		this.backgroundVersion = backgroundVersion;
	}
	
	
}

package com.cmall.productcenter.model;

import java.util.List;
import java.util.ArrayList;

import com.srnpr.zapcom.baseclass.BaseClass;

public class SellerCategory extends BaseClass {

	private String  sellerCode = "";
	private String categoryCode = "";
	private String categoryName = "";
	private String parentCode = "";
	private String sort = "";
	private String flaginable = "";
	private String level = "";
	private List children = new ArrayList();
	public String getSellerCode() {
		return sellerCode;
	}
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public String getSort() {
		return sort;
	}
	public void setSort(String sort) {
		this.sort = sort;
	}
	public String getFlaginable() {
		return flaginable;
	}
	public void setFlaginable(String flaginable) {
		this.flaginable = flaginable;
	}
	public String getLevel() {
		return level;
	}
	public void setLevel(String level) {
		this.level = level;
	}
	public List getChildren() {
		return children;
	}
	public void setChildren(List children) {
		this.children = children;
	}
	
}

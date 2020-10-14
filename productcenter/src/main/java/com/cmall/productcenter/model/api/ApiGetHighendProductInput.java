package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 获取高端商品信息的输入参数
 * @author GaoYang
 *
 */
public class ApiGetHighendProductInput extends RootInput{
	
	/**
	 * 高端商品名称（名称与分类同时只能一个设置值,便于区分是搜索商品还是进入到精品汇或是好物产子系统）
	 */
	@ZapcomApi(value="高端商品名称",remark="名称与分类同时只能一个设置值,便于区分是搜索商品还是进入到精品汇或是好物产子系统")
	private String highendProductName = "";
	/**
	 * 高端商品分类（分类与名称同时只能一个设置值,便于区分是搜索商品还是进入到精品汇或是好物产子系统）
	 */
	@ZapcomApi(value="高端商品分类",remark="分类与名称同时只能一个设置值,便于区分是搜索商品还是进入到精品汇或是好物产子系统")
	private String highendType = "";
	
	/**
	 * 排序类型(1:ZID ASC;2:ZID DESC;3:PRICE ASC;4:PRICE DESE )
	 */
	@ZapcomApi(value="排序类型",remark="1:ZID ASC;2:ZID DESC;3:PRICE ASC;4:PRICE DESE")
	private String sortType = "1";
	/**
	 * 当前页
	 */
	@ZapcomApi(value="当前页")
	private int pageIndex = 1;
	
	/**
	 * 每页数量
	 */
	@ZapcomApi(value="每页数量")
	private int pageSize = 10;

	public String getHighendProductName() {
		return highendProductName;
	}

	public void setHighendProductName(String highendProductName) {
		this.highendProductName = highendProductName;
	}

	public String getHighendType() {
		return highendType;
	}

	public void setHighendType(String highendType) {
		this.highendType = highendType;
	}

	public String getSortType() {
		return sortType;
	}

	public void setSortType(String sortType) {
		this.sortType = sortType;
	}

	public int getPageIndex() {
		return pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	
}

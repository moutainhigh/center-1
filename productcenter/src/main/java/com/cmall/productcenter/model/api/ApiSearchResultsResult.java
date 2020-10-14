package com.cmall.productcenter.model.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.BigContent;
import com.cmall.productcenter.model.CategoryBrand;
import com.cmall.productcenter.model.Item;
import com.cmall.productcenter.model.Pager;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 搜索输入参数
 * @author zhouguohui
 *
 */
public class ApiSearchResultsResult extends RootResultWeb{
	@ZapcomApi(value = "结果类型",remark="1、搜索结果；2、推荐结果")
	private int number=0;
	@ZapcomApi(value = "显示样式",remark="1、小图列表；2、大图列表")
	private int styleValue=1;
	@ZapcomApi(value="搜索关键字", remark="用户搜索关键词  用于输入框显示")
	private String keyWord="";
	@ZapcomApi(value = "搜索最低价格", remark="用户搜索最低价格")
	private BigDecimal minPrice =BigDecimal.ZERO ;
	@ZapcomApi(value = "搜索最高价格", remark="用户搜索最高价格")
	private BigDecimal maxPrice = BigDecimal.ZERO;
	@ZapcomApi(value="商品信息")
	private List<Item> item = new ArrayList<Item>();
	@ZapcomApi(value="分类和品牌", remark="分类和品牌的集合对象")
	private CategoryBrand categoryBrand=null;
	@ZapcomApi(value="分页信息")
	private Pager pager = null;
	@ZapcomApi(value = "筛选下的内容",remark="筛选下的内容")
	private List<BigContent> lists ;
	@ZapcomApi(value = "优惠券类型编号", remark="优惠券类型编号")
	private String couponTypeCode = "";
	public List<BigContent> getLists() {
		return lists;
	}
	public void setLists(List<BigContent> lists) {
		this.lists = lists;
	}
	/**
	 * @return the number
	 */
	public int getNumber() {
		return number;
	}
	/**
	 * @param number the number to set
	 */
	public void setNumber(int number) {
		this.number = number;
	}
	/**
	 * @return the item
	 */
	public List<Item> getItem() {
		return item;
	}
	/**
	 * @param item the item to set
	 */
	public void setItem(List<Item> item) {
		this.item = item;
	}
	/**
	 * @return the pager
	 */
	public Pager getPager() {
		return pager;
	}
	/**
	 * @param pager the pager to set
	 */
	public void setPager(Pager pager) {
		this.pager = pager;
	}
	/**
	 * @return the styleValue
	 */
	public int getStyleValue() {
		return styleValue;
	}
	/**
	 * @param styleValue the styleValue to set
	 */
	public void setStyleValue(int styleValue) {
		this.styleValue = styleValue;
	}
	/**
	 * @return the keyWord
	 */
	public String getKeyWord() {
		return keyWord;
	}
	/**
	 * @param keyWord the keyWord to set
	 */
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	/**
	 * @return the minPrice
	 */
	public BigDecimal getMinPrice() {
		return minPrice;
	}
	/**
	 * @param minPrice the minPrice to set
	 */
	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}
	/**
	 * @return the maxPrice
	 */
	public BigDecimal getMaxPrice() {
		return maxPrice;
	}
	/**
	 * @param maxPrice the maxPrice to set
	 */
	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}
	/**
	 * @return the categoryBrand
	 */
	public CategoryBrand getCategoryBrand() {
		return categoryBrand;
	}
	/**
	 * @param categoryBrand the categoryBrand to set
	 */
	public void setCategoryBrand(CategoryBrand categoryBrand) {
		this.categoryBrand = categoryBrand;
	}
	public String getCouponTypeCode() {
		return couponTypeCode;
	}
	public void setCouponTypeCode(String couponTypeCode) {
		this.couponTypeCode = couponTypeCode;
	}
	
}

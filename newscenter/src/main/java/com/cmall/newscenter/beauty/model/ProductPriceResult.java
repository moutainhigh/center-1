package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 商品价格  输出类
 * @author yangrong
 * date 2014-9-20
 * @version 1.0
 */
public class ProductPriceResult extends RootResultWeb {
	
	@ZapcomApi(value="评价总数")
	private String commentCount = "";
	
	@ZapcomApi(value="原价")
	private String oldPrice = "";
	
	@ZapcomApi(value="现价")
	private String newPrice = "";
	
	@ZapcomApi(value="活动开始时间")
	private String startTime = "";
	
	@ZapcomApi(value="活动结束时间")
	private String endTime = "";
	
	@ZapcomApi(value="库存")
	private String stock = "";
	
	@ZapcomApi(value="折扣")
	private String rebate = "";

	public String getCommentCount() {
		return commentCount;
	}

	public void setCommentCount(String commentCount) {
		this.commentCount = commentCount;
	}

	public String getOldPrice() {
		return oldPrice;
	}

	public void setOldPrice(String oldPrice) {
		this.oldPrice = oldPrice;
	}

	public String getNewPrice() {
		return newPrice;
	}

	public void setNewPrice(String newPrice) {
		this.newPrice = newPrice;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getRebate() {
		return rebate;
	}

	public void setRebate(String rebate) {
		this.rebate = rebate;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	

}

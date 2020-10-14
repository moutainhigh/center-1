package com.cmall.newscenter.beauty.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 付邮试用商品类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class PayTryOutGood {
	
	@ZapcomApi(value = "商品图片")
	private String photo = "";
	
	@ZapcomApi(value = "试用商品邮费")
	private String postage = "";
	
	@ZapcomApi(value = "商品名称")
	private String name = "";
	
	@ZapcomApi(value = "商品价值")
	private String price = "";
	
	@ZapcomApi(value = "商品sku编码")
	private String id = "";
	
	@ZapcomApi(value = "商品数量")
	private String count = "";
	
	@ZapcomApi(value = "商品申请人数")
	private String tryout_count = "";

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getPostage() {
		return postage;
	}

	public void setPostage(String postage) {
		this.postage = postage;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCount() {
		return count;
	}

	public void setCount(String count) {
		this.count = count;
	}

	public String getTryout_count() {
		return tryout_count;
	}

	public void setTryout_count(String tryout_count) {
		this.tryout_count = tryout_count;
	}
	
	
	
	
	
}

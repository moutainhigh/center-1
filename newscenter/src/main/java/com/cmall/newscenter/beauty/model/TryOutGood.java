package com.cmall.newscenter.beauty.model;


import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.helper.MoneyHelper;


/**
 * 免费试用商品类
 * @author yangrong
 * date: 2014-09-16
 * @version1.0
 */
public class TryOutGood {
	
	@ZapcomApi(value = "商品图片")
	private String photo = "";
	
	@ZapcomApi(value = "试用商品结束时间")
	private String time = "";
	
	@ZapcomApi(value = "试用商品当前系统时间")
	private String system_time = "";
	
	@ZapcomApi(value = "商品名称")
	private String name = "";
	
	@ZapcomApi(value = "商品价值")
	private String price = "";
	
	@ZapcomApi(value = "商品sku编码")
	private String id = "";
	
	@ZapcomApi(value = "商品数量")
	private String count = "";
	
	@ZapcomApi(value = "商品剩余件数")
	private String surplus_count = "";
	
	@ZapcomApi(value = "商品申请人数")
	private String tryout_count = "";

	@ZapcomApi(value = "试用商品类型",remark="付邮试用：449746930002;免费试用：449746930003")
	private String is_freeShipping = "";
	
	@ZapcomApi(value = "邮费")
	private BigDecimal postage ;
	
	@ZapcomApi(value = "活动ID",remark="")
	private String activity_code = "" ;
	
	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
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

	public String getSystem_time() {
		return system_time;
	}

	public void setSystem_time(String system_time) {
		this.system_time = system_time;
	}

	public String getIs_freeShipping() {
		return is_freeShipping;
	}

	public void setIs_freeShipping(String is_freeShipping) {
		this.is_freeShipping = is_freeShipping;
	}

	public BigDecimal getPostage() {
		return this.postage;
	}

	public void setPostage(BigDecimal postage) {
		this.postage = new BigDecimal(MoneyHelper.format(postage));
	}

	public String getSurplus_count() {
		return surplus_count;
	}

	public void setSurplus_count(String surplus_count) {
		this.surplus_count = surplus_count;
	}

	public String getActivity_code() {
		return activity_code;
	}

	public void setActivity_code(String activity_code) {
		this.activity_code = activity_code;
	}
	
}

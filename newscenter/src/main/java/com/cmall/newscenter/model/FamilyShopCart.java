package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**   
 * 	购物车
*    syz
*/
public class FamilyShopCart  {
	
	@ZapcomApi(value = "uid", remark = "uid", demo = "hhh4535g43hj534")
	private String uid = "";
	
	@ZapcomApi(value = "sku编号", remark = "sku编号", demo = "8019")
	private String sku_code = "";
	
	@ZapcomApi(value = "商品编号", remark = "商品编号", demo = "8016")
	private String product_code = "";
	
	@ZapcomApi(value = "买家编号", remark = "买家编号", demo = "1111")
	private String buyer_code = "";
	
	@ZapcomApi(value = "添加时间", remark = "添加时间", demo = "2008-08-08 12:12:12")
	private String add_time = "";
	
	@ZapcomApi(value = "商品数量", remark = "商品数量", demo = "数量")
	private int sku_num = 0;

	@ZapcomApi(value = "是否结算", remark = "是否结算", demo = "是否结算")
	private int account_flag = 0;
	
	@ZapcomApi(value = "地区编号", remark = "地区编号", demo = "地区编号")
	private String area_code = "";
	
	@ZapcomApi(value = "创建时间", remark = "创建时间", demo = "2008-08-08 12:12:12")
	private String create_time = "";
	
	@ZapcomApi(value = "更新时间", remark = "更新时间", demo = "2008-08-08 12:12:12")
	private String update_time = "";
	
	@ZapcomApi(value = "商品类型", remark = "商品类型", demo = "闪购")
	private int shop_type = 0;

	@ZapcomApi(value = "商品价格", remark = "商品价格", demo = "11.22")
	private double sku_price = 0.00;
	
	@ZapcomApi(value = "是否有效", remark = "是否有效", demo = "1或者0")
	private int sell_flag = 1;
	
	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getBuyer_code() {
		return buyer_code;
	}

	public void setBuyer_code(String buyer_code) {
		this.buyer_code = buyer_code;
	}

	public String getAdd_time() {
		return add_time;
	}

	public void setAdd_time(String add_time) {
		this.add_time = add_time;
	}

	public int getSku_num() {
		return sku_num;
	}

	public void setSku_num(int sku_num) {
		this.sku_num = sku_num;
	}

	public int getAccount_flag() {
		return account_flag;
	}

	public void setAccount_flag(int account_flag) {
		this.account_flag = account_flag;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public int getShop_type() {
		return shop_type;
	}

	public void setShop_type(int shop_type) {
		this.shop_type = shop_type;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public double getSku_price() {
		return sku_price;
	}

	public void setSku_price(double sku_price) {
		this.sku_price = sku_price;
	}

	public int getSell_flag() {
		return sell_flag;
	}

	public void setSell_flag(int sell_flag) {
		this.sell_flag = sell_flag;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

}


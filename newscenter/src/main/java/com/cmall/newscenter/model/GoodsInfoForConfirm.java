package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PcPropertyinfoForFamily;
import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**   
 * 	购物车查询的商品信息对象
*    xiegj
*/
public class GoodsInfoForConfirm  {
	@ZapcomApi(value = "促销种类", remark = "促销种类", demo = "123456")
	private String sales_type = "";
	
	@ZapcomApi(value = "促销活动编号", remark = "促销活动编号", demo = "123456")
	private String sales_code = "";
	
	@ZapcomApi(value = "sku编号", remark = "sku编号",require = 1, demo = "8019123456")
	private String sku_code = "";
	
	@ZapcomApi(value = "商品编号", remark = "商品编号",require = 1, demo = "8016123456")
	private String product_code = "";
	
	@ZapcomApi(value = "当前库存量", remark = "当前库存量", demo = "当前库存量")
	private int now_stock = 0;
	
	@ZapcomApi(value = "促销描述", remark = "促销描述", demo = "促销描述")
	private String sales_info = "";
	
	@ZapcomApi(value = "商品图片链接", remark = "商品图片链接", demo = "http:~~~")
	private String pic_url = "";
	
	@ZapcomApi(value = "商品名称", remark = "商品名称",require = 1, demo = "花露水")
	private String sku_name = "";
	
	@ZapcomApi(value = "商品属性", remark = "商品规格,商品款式",require = 1, demo = "商品规格,商品款式")
	private List<PcPropertyinfoForFamily> sku_property = new ArrayList<PcPropertyinfoForFamily>();
	
	@ZapcomApi(value = "商品价格", remark = "商品价格",require = 1, demo = "")
	private Double sku_price = 0.00;
	
	@ZapcomApi(value = "商品数量", remark = "商品数量",require = 1, demo = "123456")
	private int sku_num = 0;
	
	@ZapcomApi(value = "仓储地区", remark = "仓储地区",require = 1, demo = "123456")
	private String area_code = "";

	@ZapcomApi(value = "赠品列表", remark = "赠品列表",require = 1, demo = "赠品列表")
	private List<Gift> giftList = new ArrayList<Gift>();

	public String getSales_type() {
		return sales_type;
	}

	public void setSales_type(String sales_type) {
		this.sales_type = sales_type;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public String getSales_info() {
		return sales_info;
	}

	public void setSales_info(String sales_info) {
		this.sales_info = sales_info;
	}

	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public String getSku_name() {
		return sku_name;
	}

	public void setSku_name(String sku_name) {
		this.sku_name = sku_name;
	}

	public List<PcPropertyinfoForFamily> getSku_property() {
		return sku_property;
	}

	public void setSku_property(List<PcPropertyinfoForFamily> sku_property) {
		this.sku_property = sku_property;
	}

	public Double getSku_price() {
		return sku_price;
	}

	public void setSku_price(Double sku_price) {
		this.sku_price = sku_price;
	}

	public int getSku_num() {
		return sku_num;
	}

	public void setSku_num(int sku_num) {
		this.sku_num = sku_num;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public List<Gift> getGiftList() {
		return giftList;
	}

	public void setGiftList(List<Gift> giftList) {
		this.giftList = giftList;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public int getNow_stock() {
		return now_stock;
	}

	public void setNow_stock(int now_stock) {
		this.now_stock = now_stock;
	}

	public String getSales_code() {
		return sales_code;
	}

	public void setSales_code(String sales_code) {
		this.sales_code = sales_code;
	}
	
}


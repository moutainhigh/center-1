package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PcPropertyinfoForFamily;
import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**   
 * 	购物车查询的商品信息对象
*    xiegj
*/
public class GoodsInfoForQuery  {
	@ZapcomApi(value = "促销种类", remark = "促销种类", demo = "123456")
	private String sales_type = "";
	
	@ZapcomApi(value = "商品编号", remark = "商品编号",require = 1, demo = "8016123456")
	private String product_code = "";
	
	@ZapcomApi(value = "sku编号", remark = "sku编号",require = 1, demo = "8019123456")
	private String sku_code = "";
	
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
	
	@ZapcomApi(value = "每单限购数量", remark = "每单限购数量",require = 1, demo = "123456")
	private int limit_order_num = 0;
	
	@ZapcomApi(value = "地区编号", remark = "地区编号",require = 1, demo = "123456")
	private String area_code = "";
	
	@ZapcomApi(value = "库存", remark = "库存",require = 1, demo = "123456")
	private int sku_stock = 0;

	@ZapcomApi(value="活动",remark="如闪购活动")
	private List<Activity> activitys = new ArrayList<Activity>();
	
	@ZapcomApi(value = "库存是否足够", remark = "1:足够，0:不足",require = 1, demo = "1")
	private String flag_stock = "1";
	
	@ZapcomApi(value = "是否有效商品", remark = "1:有效商品，0:无效商品",require = 1, demo = "1")
	private String flag_product = "1";
	
	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

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

	public List<PcPropertyinfoForFamily> getSku_property() {
		return sku_property;
	}

	public void setSku_property(List<PcPropertyinfoForFamily> sku_property) {
		this.sku_property = sku_property;
	}

	public int getLimit_order_num() {
		return limit_order_num;
	}

	public void setLimit_order_num(int limit_order_num) {
		this.limit_order_num = limit_order_num;
	}

	public List<Activity> getActivitys() {
		return activitys;
	}

	public void setActivitys(List<Activity> activitys) {
		this.activitys = activitys;
	}

	public String getFlag_stock() {
		return flag_stock;
	}

	public void setFlag_stock(String flag_stock) {
		this.flag_stock = flag_stock;
	}

	public String getFlag_product() {
		return flag_product;
	}

	public void setFlag_product(String flag_product) {
		this.flag_product = flag_product;
	}

	public int getSku_stock() {
		return sku_stock;
	}

	public void setSku_stock(int sku_stock) {
		this.sku_stock = sku_stock;
	}

}


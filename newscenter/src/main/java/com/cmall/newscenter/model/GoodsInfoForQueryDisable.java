package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PcPropertyinfoForFamily;
import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**   
 * 	购物车查询的商品信息对象
*    xiegj
*/
public class GoodsInfoForQueryDisable  {
	
	@ZapcomApi(value = "商品编号", remark = "商品编号",require = 1, demo = "8019123456")
	private String sku_code = "";
	
	@ZapcomApi(value = "商品图片链接", remark = "商品图片链接", demo = "http:~~~")
	private String pic_url = "";
	
	@ZapcomApi(value = "商品名称", remark = "商品名称",require = 1, demo = "花露水")
	private String sku_name = "";
	
	@ZapcomApi(value = "商品属性", remark = "商品规格,商品款式",require = 1, demo = "商品规格,商品款式")
	private List<PcPropertyinfoForFamily> sku_property = new ArrayList<PcPropertyinfoForFamily>();
	
	@ZapcomApi(value = "商品数量", remark = "商品数量",require = 1, demo = "123456")
	private int sku_num = 0;
	
	@ZapcomApi(value = "地区", remark = "地区",require = 1, demo = "123456")
	private String area_code = "";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
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

}


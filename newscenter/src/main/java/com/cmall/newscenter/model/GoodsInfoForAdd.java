package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**   
 * 	购物车添加的商品信息对象
*   syz
*/
public class GoodsInfoForAdd  {
	@ZapcomApi(value = "sku编号", remark = "sku编号",require = 1, demo = "8019123456")
	private String sku_code = "";
	
	@ZapcomApi(value = "商品数量", remark = "商品数量",require = 1, demo = "123456")
	private int sku_num = 0;
	
	@ZapcomApi(value = "地区编号", remark = "可不填写，添加购物车不再需要区域编号", demo = "123456")
	private String area_code = "";
	
	@ZapcomApi(value = "商品编号", remark = "商品编号", demo = "8016123456")
	private String product_code = "";

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
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

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
}


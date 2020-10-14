package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**   
 * 	赠品
*    xiegj
*/
public class Gift  {
	@ZapcomApi(value = "商品编号", remark = "商品编号", demo = "8019")
	private String sku_code = "";
	
	@ZapcomApi(value = "商品图片链接", remark = "商品图片链接", demo = "http:~~")
	private String pic_url = "";
	
	@ZapcomApi(value = "商品名称", remark = "商品名称", demo = "名称")
	private String sku_name = "";
	
	@ZapcomApi(value = "商品数量", remark = "商品数量", demo = "数量")
	private String sku_num = "";

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

	public String getSku_num() {
		return sku_num;
	}

	public void setSku_num(String sku_num) {
		this.sku_num = sku_num;
	}
	
}


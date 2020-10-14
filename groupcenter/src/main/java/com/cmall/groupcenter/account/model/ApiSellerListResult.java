package com.cmall.groupcenter.account.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 商品信息
 * @author wz
 *
 */
public class ApiSellerListResult{
	@ZapcomApi(value="商品编号")
	private String product_code = "";
	@ZapcomApi(value="sku编号")
	private String sku_code = "";
	@ZapcomApi(value="商品图片链接")
	private String mainpic_url = "";
	@ZapcomApi(value="商品名称")
	private String product_name = "";
	@ZapcomApi(value="规格/款式")
	private List<ApiSellerStandardAndStyleResult> standardAndStyleList = new ArrayList<ApiSellerStandardAndStyleResult>();
	
	
	public String getProduct_code() {
		return product_code;
	}
	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}
	public String getMainpic_url() {
		return mainpic_url;
	}
	public void setMainpic_url(String mainpic_url) {
		this.mainpic_url = mainpic_url;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getSku_code() {
		return sku_code;
	}
	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}
	public List<ApiSellerStandardAndStyleResult> getStandardAndStyleList() {
		return standardAndStyleList;
	}
	public void setStandardAndStyleList(List<ApiSellerStandardAndStyleResult> standardAndStyleList) {
		this.standardAndStyleList = standardAndStyleList;
	}
}

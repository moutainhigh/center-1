package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 商品信息
 * @author yangrong
 *
 */
public class ApiSellerListResult {
	
	@ZapcomApi(value="商品编号")
	private String product_code = "";
	
	@ZapcomApi(value="商品图片链接")
	private String mainpic_url = "";
	
	@ZapcomApi(value="商品名称")
	private String product_name = "";
	
	@ZapcomApi(value="商品数量")
	private String product_number = "";
	
	@ZapcomApi(value="活动标签")
	private String labels = "";
	
	@ZapcomApi(value="规格/款式")
	private List<ApiSellerStandardAndStyleResult> StandardAndStyleList = new ArrayList<ApiSellerStandardAndStyleResult>();
	
	@ZapcomApi(value="仓储城市")
	private String warehouseCity = "";
	
	@ZapcomApi(value="商品单价")
	private String sell_price = "";
	
	@ZapcomApi(value = "商品类型",remark="明确商品类型的列表不返回     0：普通商品  1：限购商品   2：试用商品")
	private String productType = "";
	
	@ZapcomApi(value="商品名称简介")
	private String productShortName = "";
	
	@ZapcomApi(value="是否评价",remark="是：true  否：false")
	private String ifEvaluate = "";

	
	public String getProductShortName() {
		return productShortName;
	}
	public void setProductShortName(String productShortName) {
		this.productShortName = productShortName;
	}
	public String getIfEvaluate() {
		return ifEvaluate;
	}
	public void setIfEvaluate(String ifEvaluate) {
		this.ifEvaluate = ifEvaluate;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
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
	public String getProduct_number() {
		return product_number;
	}
	public void setProduct_number(String product_number) {
		this.product_number = product_number;
	}
	public String getLabels() {
		return labels;
	}
	public void setLabels(String labels) {
		this.labels = labels;
	}
	public String getWarehouseCity() {
		return warehouseCity;
	}
	public void setWarehouseCity(String warehouseCity) {
		this.warehouseCity = warehouseCity;
	}
	public String getSell_price() {
		return sell_price;
	}
	public void setSell_price(String sell_price) {
		this.sell_price = sell_price;
	}
	public List<ApiSellerStandardAndStyleResult> getStandardAndStyleList() {
		return StandardAndStyleList;
	}
	public void setStandardAndStyleList(
			List<ApiSellerStandardAndStyleResult> standardAndStyleList) {
		StandardAndStyleList = standardAndStyleList;
	}
}

package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 订单商品列表
 * @author wz
 *
 */
public class ApiOrderSellerDetailsResult{
	@ZapcomApi(value="sku编号")
	private String productCode = "";
	@ZapcomApi(value="促销种类")
	private String promotionType = "";
	@ZapcomApi(value="促销描述")
	private String promotionDescribe = "";
	@ZapcomApi(value="商品图片链接")
	private String mainpicUrl = "";
	@ZapcomApi(value="商品名称")
	private String productName = "";
	@ZapcomApi(value="商品名称简介")
	private String productShortName = "";
	@ZapcomApi(value="是否评价",remark="是：true  否：false")
	private String ifEvaluate = "";
	@ZapcomApi(value="价格")
	private Double price = 0.00;
	@ZapcomApi(value="数量")
	private String number = "";
	@ZapcomApi(value="地区")
	private String region = "";
	@ZapcomApi(value = "商品类型",remark="明确商品类型的列表不返回     0：普通商品  1：限购商品   2：试用商品")
	private String productType = "";
	@ZapcomApi(value = "结束时间",remark="试用商品才会返回结束时间")
	private String end_time = "";
	
	@ZapcomApi(value="规格/款式")
	private List<ApiSellerStandardAndStyleResult> StandardAndStyleList = new ArrayList<ApiSellerStandardAndStyleResult>();
	@ZapcomApi(value="赠品列表")
	private List<ApiOrderDonationDetailsResult> detailsList = new ArrayList<ApiOrderDonationDetailsResult>();

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
	public String getProductShortName() {
		return productShortName;
	}
	public void setProductShortName(String productShortName) {
		this.productShortName = productShortName;
	}
	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	public String getPromotionType() {
		return promotionType;
	}
	public void setPromotionType(String promotionType) {
		this.promotionType = promotionType;
	}
	public String getPromotionDescribe() {
		return promotionDescribe;
	}
	public void setPromotionDescribe(String promotionDescribe) {
		this.promotionDescribe = promotionDescribe;
	}
	public String getMainpicUrl() {
		return mainpicUrl;
	}
	public void setMainpicUrl(String mainpicUrl) {
		this.mainpicUrl = mainpicUrl;
	}
	public String getProductName() {
		return productName;
	}
	public void setProductName(String productName) {
		this.productName = productName;
	}
	public List<ApiSellerStandardAndStyleResult> getStandardAndStyleList() {
		return StandardAndStyleList;
	}
	public void setStandardAndStyleList(
			List<ApiSellerStandardAndStyleResult> standardAndStyleList) {
		StandardAndStyleList = standardAndStyleList;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public List<ApiOrderDonationDetailsResult> getDetailsList() {
		return detailsList;
	}
	public void setDetailsList(List<ApiOrderDonationDetailsResult> detailsList) {
		this.detailsList = detailsList;
	}
	public String getEnd_time() {
		return end_time;
	}
	public void setEnd_time(String end_time) {
		this.end_time = end_time;
	}
	
	
}

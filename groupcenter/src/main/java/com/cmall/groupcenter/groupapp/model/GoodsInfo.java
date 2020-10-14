package com.cmall.groupcenter.groupapp.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/***
 * 微公社app用户实体类
 * @author fengl
 * date 2015-11-6
 * @version 2.0
 */
public class GoodsInfo  {

	
	@ZapcomApi(value = "商品图片")
	private String goodsIcon;
	
	@ZapcomApi(value = "商品名称")
	private String goodsName;
	
	@ZapcomApi(value = "商品编号")
	private String goodsCode;
	
	@ZapcomApi(value = "当前价格")
	private String currentPrice;
	
	@ZapcomApi(value = "原价")
	private String originalPrice;
	
	@ZapcomApi(value = "销量")
	private String salesCount;
	
	//排序用
	private Double discountPrice;
	//排序用
	private Double rebateScale;
	
	@ZapcomApi(value = "商品来源",remark = "对应applogo链接")
	private String goodsSourceUrl;
	
	@ZapcomApi(value = "返利的金额")
	private String rebateMoney;
	
	@ZapcomApi(value = "商品详情url")
	private String goodDetailUrl;
	
	@ZapcomApi(value = "剩余促销库存")
	private String limitStock;
	
	@ZapcomApi(value = "商品上下架状态",remark="已下架:4497153900060003")
	private String productStatus;
	
	@ZapcomApi(value = "是否参加本人返利 ",remark="1本人参加返利、 2本人不参加返利",demo="1")
	private String isSelfRebate="2";
	
	
	@ZapcomApi(value = "规格参数",remark="json字符串")
	private List propertyList=new ArrayList();
	
	@ZapcomApi(value = "图文",remark="json字符串")
	private List discriptPicList=new ArrayList();
	
	@ZapcomApi(value = "商品是可用",remark="1可用 pc端需要此属性(超级饭)")
	private int flagEnable;
	
	public int getFlagEnable() {
		return flagEnable;
	}

	public void setFlagEnable(int flagEnable) {
		this.flagEnable = flagEnable;
	}
	
	public List getDiscriptPicList() {
		return discriptPicList;
	}

	public void setDiscriptPicList(List discriptPicList) {
		this.discriptPicList = discriptPicList;
	}

	public List getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List propertyList) {
		this.propertyList = propertyList;
	}


	
	

	public String getIsSelfRebate() {
		return isSelfRebate;
	}

	public void setIsSelfRebate(String isSelfRebate) {
		this.isSelfRebate = isSelfRebate;
	}

	public Double getRebateScale() {
		return rebateScale;
	}

	public void setRebateScale(Double rebateScale) {
		this.rebateScale = rebateScale;
	}

	public void setDiscountPrice(Double discountPrice) {
		this.discountPrice = discountPrice;
	}

	public Double getDiscountPrice(){
		return Double.parseDouble(this.getCurrentPrice())/Double.parseDouble(this.getOriginalPrice());
	}
	
	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	
	

	public String getGoodsIcon() {
		return goodsIcon;
	}

	public void setGoodsIcon(String goodsIcon) {
		this.goodsIcon = goodsIcon;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getGoodsCode() {
		return goodsCode;
	}

	public void setGoodsCode(String goodsCode) {
		this.goodsCode = goodsCode;
	}

	public String getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(String currentPrice) {
		this.currentPrice = currentPrice;
	}

	public String getOriginalPrice() {
		return originalPrice;
	}

	public void setOriginalPrice(String originalPrice) {
		this.originalPrice = originalPrice;
	}

	public String getSalesCount() {
		if(StringUtils.isBlank(salesCount)){
			return "0";
		}
		return salesCount;
	}

	public void setSalesCount(String salesCount) {
		this.salesCount = salesCount;
	}

	public String getGoodsSourceUrl() {
		return goodsSourceUrl;
	}

	public void setGoodsSourceUrl(String goodsSourceUrl) {
		this.goodsSourceUrl = goodsSourceUrl;
	}

	public String getRebateMoney() {
		return rebateMoney;
	}

	public void setRebateMoney(String rebateMoney) {
		this.rebateMoney = rebateMoney;
	}
	public String getLimitStock() {
		return limitStock;
	}

	public void setLimitStock(String limitStock) {
		this.limitStock = limitStock;
	}

	public String getGoodDetailUrl() {
		return goodDetailUrl;
	}

	public void setGoodDetailUrl(String goodDetailUrl) {
		this.goodDetailUrl = goodDetailUrl;
	}

	


}

package com.cmall.groupcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.active.BaseActive;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 购物车sku信息对象
 * @author xiegj
 */
public class SkuInfoForShopCart {
	
	@ZapcomApi(value = "商品编号",remark = "商品编号", demo = "80161234")
	private String productCode = "";
	
	@ZapcomApi(value = "sku编号",remark = "Sku编号", demo = "80191234")
	private String skuCode = "";
	
	@ZapcomApi(value = "sku名称",remark = "sku名称",demo = "蓝翔牌挖掘机")
	private String skuName = "";
	
	@ZapcomApi(value = "sku数量",remark = "skuNum",demo = "8")
	private Integer skuNum = 0;
	
	@ZapcomApi(value = "sku主图",remark = "skuPic",demo = "8")
	private String skuPic = "";
	
	@ZapcomApi(value = "sku价格",remark = "sku价格",demo = "88.88")
	private Double skuPrice = 0.00;
	
	@ZapcomApi(value = "skuKey属性",remark = "skuKey属性",demo = "颜色=黑色&尺码=1号")
	private String skuKey = "";
	
	@ZapcomApi(value = "skuValue属性",remark = "skuValue属性",demo = "color=black&size=one")
	private String skuValue = "";
	
	@ZapcomApi(value = "sku活动List",remark = "sku所参与的活动的List",demo = "")
	private List<BaseActive> activityForShopCarts = new ArrayList<BaseActive>();
	
	@ZapcomApi(value = "商品是否在售",remark = "0:下架，1:上架",demo = "1")
	private String flagSell = "";
	
	@ZapcomApi(value = "库存是否足够",remark = "0:库存不足，1:库存足够",demo = "1")
	private String flagStock = "";
	
	
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getSkuPic() {
		return skuPic;
	}

	public void setSkuPic(String skuPic) {
		this.skuPic = skuPic;
	}

	public String getFlagSell() {
		return flagSell;
	}

	public void setFlagSell(String flagSell) {
		this.flagSell = flagSell;
	}

	public String getFlagStock() {
		return flagStock;
	}

	public void setFlagStock(String flagStock) {
		this.flagStock = flagStock;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public Integer getSkuNum() {
		return skuNum;
	}

	public void setSkuNum(Integer skuNum) {
		this.skuNum = skuNum;
	}

	public Double getSkuPrice() {
		return skuPrice;
	}

	public void setSkuPrice(Double skuPrice) {
		this.skuPrice = skuPrice;
	}

	public String getSkuKey() {
		return skuKey;
	}

	public void setSkuKey(String skuKey) {
		this.skuKey = skuKey;
	}

	public String getSkuValue() {
		return skuValue;
	}

	public void setSkuValue(String skuValue) {
		this.skuValue = skuValue;
	}

	public List<BaseActive> getActivityForShopCarts() {
		return activityForShopCarts;
	}

	public void setActivityForShopCarts(List<BaseActive> activityForShopCarts) {
		this.activityForShopCarts = activityForShopCarts;
	}
}

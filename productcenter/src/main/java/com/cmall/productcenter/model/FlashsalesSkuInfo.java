package com.cmall.productcenter.model;

import java.math.BigDecimal;
/**
 * 闪购商品
 * @author 李国杰
 *
 */
public class FlashsalesSkuInfo {

	/**
	 * 
	 */
	private int zid = 0;
	/**
	 * 
	 */
	private String uid = "";
	/**
	 * 活动编号
	 */
	private String activityCode = "";
	/**
	 * 活动名称
	 */
	private String activityName = "";
	/**
	 * 产品编号
	 */
	private String skuCode = "";
	/**
	 * 产品名称
	 */
	private String skuName = "";
	/**
	 * 库存数
	 */
	private BigDecimal stockNum = new BigDecimal(0);
	/**
	 * 促销库存
	 */
	private BigDecimal salesNum = new BigDecimal(0);
	/**
	 * 活动剩余件数
	 */
	private BigDecimal surplusNum = new BigDecimal(0);
	/**
	 * 销售价
	 */
	private BigDecimal sellPrice = new BigDecimal(0.00);
	/**
	 * 优惠价
	 */
	private BigDecimal vipPrice = new BigDecimal(0.00);
	/**
	 * 每会员限购数
	 */
	private BigDecimal purchaseLimitVipNum = new BigDecimal(0);
	/**
	 *每单限购数量
	 */
	private BigDecimal purchase_limit_order_num = new BigDecimal(0);
	/**
	 *每日限购数量
	 */
	private BigDecimal purchase_limit_day_num = new BigDecimal(0);
	/**
	 * 位置
	 */
	private int location = 0;

	/**
	 * 活动描述
	 */
	private String remark = "";
	/**
	 * 更新时间
	 */
	private String updateTime = "";

	/**
	 * 更新人
	 */
	private String updateUser = "";

	/**
	 * 状态
	 */
	private String status = "";
	
	
	/**
	 * 活动开始时间
	 */
	private String startTime = "";
	/**
	 * 活动结束时间
	 */
	private String endTime = "";
	/**
	 * 商品信息
	 */
	private PcProductinfo product = new PcProductinfo();
	
	/**
	 * 限时抢购列表图片
	 * */
	private String skuImgReplace="";
	
	public int getZid() {
		return zid;
	}

	public void setZid(int zid) {
		this.zid = zid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public BigDecimal getPurchase_limit_day_num() {
		return purchase_limit_day_num;
	}

	public void setPurchase_limit_day_num(BigDecimal purchase_limit_day_num) {
		this.purchase_limit_day_num = purchase_limit_day_num;
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

	public BigDecimal getStockNum() {
		return stockNum;
	}

	public void setStockNum(BigDecimal stockNum) {
		this.stockNum = stockNum;
	}

	public BigDecimal getSalesNum() {
		return salesNum.setScale(0, BigDecimal.ROUND_FLOOR);
	}

	public void setSalesNum(BigDecimal salesNum) {
		this.salesNum = salesNum;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public BigDecimal getVipPrice() {
		return vipPrice;
	}

	public void setVipPrice(BigDecimal vipPrice) {
		this.vipPrice = vipPrice;
	}

	public BigDecimal getPurchaseLimitVipNum() {
		return purchaseLimitVipNum;
	}

	public void setPurchaseLimitVipNum(BigDecimal purchaseLimitVipNum) {
		this.purchaseLimitVipNum = purchaseLimitVipNum;
	}

	public BigDecimal getPurchase_limit_order_num() {
		return purchase_limit_order_num;
	}

	public void setPurchase_limit_order_num(BigDecimal purchase_limit_order_num) {
		this.purchase_limit_order_num = purchase_limit_order_num;
	}

	public int getLocation() {
		return location;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getUpdateUser() {
		return updateUser;
	}

	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public PcProductinfo getProduct() {
		return product;
	}

	public void setProduct(PcProductinfo product) {
		this.product = product;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public BigDecimal getSurplusNum() {
		return surplusNum.setScale(0, BigDecimal.ROUND_FLOOR);
	}

	public void setSurplusNum(BigDecimal surplusNum) {
		this.surplusNum = surplusNum;
	}

	public String getSkuImgReplace() {
		return skuImgReplace;
	}

	public void setSkuImgReplace(String skuImgReplace) {
		this.skuImgReplace = skuImgReplace;
	}

	
}

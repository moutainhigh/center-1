package com.cmall.ordercenter.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class CouponForGetInfo {
	@ZapcomApi(value="优惠劵类型UID",remark="唯一编号")
	private String uid="";
	
	@ZapcomApi(value="优惠劵类型编号",remark="优惠劵类型编号")
	private String couponTypeCode="";
	
	@ZapcomApi(value="优惠劵编号",remark="优惠劵编号")
	private String couponCode="";
	
	@ZapcomApi(value="优惠劵类型名称",remark="优惠劵类型名称")
	private String couponTypeName="";
	
	@ZapcomApi(value="开始时间",remark="开始时间")
	private String startTime="";
	
	@ZapcomApi(value="结束时间",remark="结束时间")
	private String endTime="";
	
	@ZapcomApi(value="优惠劵状态",remark="0：未领取；1：已领取",demo="1")
	private int status=0;
	
	@ZapcomApi(value="优惠券是否抢光",remark="0：抢光了；1：有剩余",demo="1")
	private int ifStore=1;
	
	@ZapcomApi(value="使用说明",remark="使用说明")
	private String limitExplain="";
	
	@ZapcomApi(value="使用范围",remark="全场通用，促销商品除外")
	private String limitScope="";
	
	@ZapcomApi(value="限制条件",remark="无门槛、满X元可用、")
	private String limitCondition="";

	@ZapcomApi(value="最小限额",remark="最小限额")
	private BigDecimal limitMoney=new BigDecimal(0);
	
	@ZapcomApi(value="面额",remark="优惠券面值")
	private BigDecimal money=new BigDecimal(0);
	
	@ZapcomApi(value="优惠券金额类型",remark="449748120001: 金额券，449748120002: 折扣券，449748120003:礼金券")
	private String moneyType = "449748120001";
	
	@ZapcomApi(value="活动编号",remark="活动编号")
	private String activityCode="";	
	
	@ZapcomApi(value="活动类型",remark="活动编类型号")
	private String activityType="";	
	
	@ZapcomApi(value="优惠券按钮",remark="buttonCode（4497477800080018：领取），（4497477800080019：立即使用）")
	private List<Button> buttons = new ArrayList<Button>();

	@ZapcomApi(value="点击立即使用跳转类型",remark="URL:4497471600280002,其他搜索页面：4497471600280001")
	private String actionType="";
	
	@ZapcomApi(value="跳转地址",remark="需要判断actionType，如果为4497471600280002，点击立即使用需要跳转此URL地址")
	private String actionValue="";
	
	@ZapcomApi(value="渠道限制",demo="4497471600070001不指定，4497471600070002指定")
	private String channelLimit="";
	
	@ZapcomApi(value="绑定渠道code",demo="449747430001,449747430002")
	private String channelCodes="";
	
	@ZapcomApi(value="商品限制条件（分销判断使用）",remark="4497471600070002指定,4497471600070001不指定")
	private String productLimit="";
	
	@ZapcomApi(value="商品条件限制标识",remark="0:只针对productCodes,1:排除productCodes中指定的商品")
	private String exceptProduct="";
	
	@ZapcomApi(value="商品限制条件（分销判断使用）",remark="4497471600070002指定,4497471600070001不指定")
	private String productCodes="";
	
	@ZapcomApi(value="券标识(区分小程序分销使用),1为分销券 0为普通配置的券")
	private String  ifDistributionCoupon ="0";
	
	@ZapcomApi(value="券自己的开始时间",remark="分销券有领取完之后会立即使用,所以该字段作为分销券的筛选条件")
	private String  couponStartTime ="";
	
	@ZapcomApi(value="券自己的结束时间",remark="分销券有领取完之后会立即使用,所以该字段作为分销券的筛选条件")
	private String  couponEndTime ="";
	
	@ZapcomApi(value="有效天数",remark="分销券有领取完之后会立即使用,所以该字段作为分销券的筛选条件")
	private String  validDay ="";
	
	
	
	public String getActivityType() {
		return activityType;
	}

	public void setActivityType(String activityType) {
		this.activityType = activityType;
	}

	public String getValidDay() {
		return validDay;
	}

	public void setValidDay(String validDay) {
		this.validDay = validDay;
	}

	public String getCouponEndTime() {
		return couponEndTime;
	}

	public void setCouponEndTime(String couponEndTime) {
		this.couponEndTime = couponEndTime;
	}
	
	public String getCouponStartTime() {
		return couponStartTime;
	}

	public void setCouponStartTime(String couponStartTime) {
		this.couponStartTime = couponStartTime;
	}

	public String getChannelLimit() {
		return channelLimit;
	}

	public void setChannelLimit(String channelLimit) {
		this.channelLimit = channelLimit;
	}

	public String getIfDistributionCoupon() {
		return ifDistributionCoupon;
	}

	public void setIfDistributionCoupon(String ifDistributionCoupon) {
		this.ifDistributionCoupon = ifDistributionCoupon;
	}

	public String getExceptProduct() {
		return exceptProduct;
	}

	public void setExceptProduct(String exceptProduct) {
		this.exceptProduct = exceptProduct;
	}

	public String getProductCodes() {
		return productCodes;
	}

	public void setProductCodes(String productCodes) {
		this.productCodes = productCodes;
	}

	public String getProductLimit() {
		return productLimit;
	}

	public void setProductLimit(String productLimit) {
		this.productLimit = productLimit;
	}

	public String getChannelCodes() {
		return channelCodes;
	}

	public void setChannelCodes(String channelCodes) {
		this.channelCodes = channelCodes;
	}
	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getActionType() {
		return actionType;
	}

	public void setActionType(String actionType) {
		this.actionType = actionType;
	}

	public String getActionValue() {
		return actionValue;
	}

	public void setActionValue(String actionValue) {
		this.actionValue = actionValue;
	}

	public String getLimitCondition() {
		return limitCondition;
	}

	public void setLimitCondition(String limitCondition) {
		this.limitCondition = limitCondition;
	}

	public String getCouponTypeName() {
		return couponTypeName;
	}

	public void setCouponTypeName(String couponTypeName) {
		this.couponTypeName = couponTypeName;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public int getIfStore() {
		return ifStore;
	}

	public void setIfStore(int ifStore) {
		this.ifStore = ifStore;
	}

	public BigDecimal getMoney() {
		return money;
	}

	public void setMoney(BigDecimal money) {
		this.money = money;
	}

	public String getCouponTypeCode() {
		return couponTypeCode;
	}

	public void setCouponTypeCode(String couponTypeCode) {
		this.couponTypeCode = couponTypeCode;
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

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getLimitExplain() {
		return limitExplain;
	}

	public void setLimitExplain(String limitExplain) {
		this.limitExplain = limitExplain;
	}


	public String getLimitScope() {
		return limitScope;
	}

	public void setLimitScope(String limitScope) {
		this.limitScope = limitScope;
	}

	public BigDecimal getLimitMoney() {
		return limitMoney;
	}

	public void setLimitMoney(BigDecimal limitMoney) {
		this.limitMoney = limitMoney;
	}

	public String getMoneyType() {
		return moneyType;
	}

	public void setMoneyType(String moneyType) {
		this.moneyType = moneyType;
	}

	public String getActivityCode() {
		return activityCode;
	}

	public void setActivityCode(String activityCode) {
		this.activityCode = activityCode;
	}

	public List<Button> getButtons() {
		return buttons;
	}

	public void setButtons(List<Button> buttons) {
		this.buttons = buttons;
	}
	
	

}

package com.cmall.ordercenter.model;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.helper.MoneyHelper;

public class CouponInfo{
	
	@ZapcomApi(value="优惠劵编号",remark="优惠劵编号")
	private String couponCode="";
	
	@ZapcomApi(value="剩余金额",remark="剩余金额")
	private BigDecimal surplusMoney=new BigDecimal(0);

	@ZapcomApi(value="开始时间",remark="开始时间")
	private String startTime="";
	
	@ZapcomApi(value="结束时间",remark="结束时间")
	private String endTime="";
	
	@ZapcomApi(value="优惠劵状态",remark="0：未使用；1：已使用；2：已过期  5:未激活",demo="1")
	private int status=0;
	
	@ZapcomApi(value="优惠劵过期提醒",remark="还剩X天")
	private String deadline = "";
	
	@ZapcomApi(value="使用说明",remark="使用说明")
	private String limitExplain="";
	
	@ZapcomApi(value="是否限制平台",remark="0：否，1是")
	private String channelLimit="0";
	
	@ZapcomApi(value="使用范围",remark="使用范围")
	private String useLimit="";

	@ZapcomApi(value="最小限额",remark="最小限额")
	private BigDecimal limitMoney=new BigDecimal(0);
	
	@ZapcomApi(value="优惠券数量",remark="优惠劵数量")
	private int count=1;
		
	@ZapcomApi(value="初始金额",remark="初始金额")
	private BigDecimal initialMoney=new BigDecimal(0);
	
	@ZapcomApi(value="触发订单",remark="生成此优惠券的大订单号")
	private String bigOrderCode = "";
	
	@ZapcomApi(value="是否已查看",remark="0：否，1是")
	private String isSee = "1";
	
	@ZapcomApi(value="点击优惠券跳转类型",remark="点击优惠券的跳转类型 目前只有跳转到详情页类型3.9.2")
	private String actionType="";
	
	@ZapcomApi(value="跳转值",remark="点击优惠券的跳转值 目前只有一个商品的时候才会跳转到详情页3.9.2,4497471600280001:商品详情页，4497471600280002：wap页")
	private String actionValue="";
	
	@ZapcomApi(value="优惠券金额类型",remark="449748120001: 金额卷，449748120002: 折扣券")
	private String moneyType = "449748120001";
	
	@ZapcomApi(value="活动编号",remark="活动编号")
	private String activityCode="";	
	
	@ZapcomApi(value="是否可以叠加使用",remark="Y可以叠加使用 / N不可用叠加使用")
	private String is_multi_use="";	
	
	@ZapcomApi(value="是否显示快过期",remark="1显示 0不显示")
	private String isShowDue = "0";
	
	@ZapcomApi(value="优惠券类型编号",remark="优惠券类型编号")
	private String couponTypeCode = "";
	
	@ZapcomApi(value="限制条件",remark="限制条件")
	private String limitCondition = "";
	
	@ZapcomApi(value="是否允许找零",remark="是否允许找零")
	private String is_change = "";
	
	@ZapcomApi(value="优惠劵到期时间戳",remark="毫秒数")
	private long expireTime = 0l;
	
	@ZapcomApi(value="系统时间戳",remark="毫秒数")
	private long systemTime = 0l;
	
	public String getLimitExplain() {
		return limitExplain;
	}

	public void setLimitExplain(String limitExplain) {
		this.limitExplain = limitExplain;
	}

	public BigDecimal getInitialMoney() {
		return initialMoney;
	}

	public void setInitialMoney(BigDecimal initialMoney) {
		this.initialMoney = initialMoney;
	}

	public String getUseLimit() {
		return useLimit;
	}

	public void setUseLimit(String useLimit) {
		this.useLimit = useLimit;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}



	public BigDecimal getSurplusMoney() {
		return this.surplusMoney;
	}

	public void setSurplusMoney(BigDecimal surplusMoney) {
		this.surplusMoney = new BigDecimal(MoneyHelper.format(surplusMoney));
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public BigDecimal getLimitMoney() {
		return this.limitMoney;
	}

	public void setLimitMoney(BigDecimal limitMoney) {
		this.limitMoney = new BigDecimal(MoneyHelper.format(limitMoney));;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getChannelLimit() {
		return channelLimit;
	}

	public void setChannelLimit(String channelLimit) {
		this.channelLimit = channelLimit;
	}

	public String getIsSee() {
		return isSee;
	}

	public void setIsSee(String isSee) {
		this.isSee = isSee;
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

	public String getDeadline() {
		return deadline;
	}

	public void setDeadline(String deadline) {
		this.deadline = deadline;
	}

	public String getBigOrderCode() {
		return bigOrderCode;
	}

	public void setBigOrderCode(String bigOrderCode) {
		this.bigOrderCode = bigOrderCode;
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

	public String getIs_multi_use() {
		return is_multi_use;
	}

	public void setIs_multi_use(String is_multi_use) {
		this.is_multi_use = is_multi_use;
	}

	public String getIsShowDue() {
		return isShowDue;
	}

	public void setIsShowDue(String isShowDue) {
		this.isShowDue = isShowDue;
	}

	public String getCouponTypeCode() {
		return couponTypeCode;
	}

	public void setCouponTypeCode(String couponTypeCode) {
		this.couponTypeCode = couponTypeCode;
	}

	public String getLimitCondition() {
		return limitCondition;
	}

	public void setLimitCondition(String limitCondition) {
		this.limitCondition = limitCondition;
	}

	public String getIs_change() {
		return is_change;
	}

	public void setIs_change(String is_change) {
		this.is_change = is_change;
	}

	public long getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	public long getSystemTime() {
		return systemTime;
	}

	public void setSystemTime(long systemTime) {
		this.systemTime = systemTime;
	}
	
}

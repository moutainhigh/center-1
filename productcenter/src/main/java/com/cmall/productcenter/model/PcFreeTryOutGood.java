package com.cmall.productcenter.model;

import java.math.BigDecimal;

import com.srnpr.zapweb.helper.MoneyHelper;

/**
 * 试用商品实体
 * @author 李国杰
 *
 */
public class PcFreeTryOutGood  {
    
    /**
     * 
     */
    private Integer zid   = 0 ;
    /**
     * 
     */
    private String uid  = ""  ;
    /**
     * 活动编号
     */
    private String activityCode  = ""  ;
    /**
     * 试用商品ID
     */
    private String skuCode  = ""  ;
    /**
     * 商品名称
     */
    private String skuName  = ""  ;
    /**
     * APP名称
     */
    private String appCode  = ""  ;
    /**
     * 试用价(积分:个)
     */
    private String tryoutPrice  = ""  ;
    /**
     * 等级限制
     */
    private String levelCode  = ""  ;
    /**
     *是否付邮
     */
    private String isFreeShipping  = ""  ;
    /**
     *邮费
     */
    private BigDecimal postage  = new BigDecimal(0)  ;
    /**
     * 试用库存
     */
    private int tryoutInventory  = 0  ;
    /**
     * 初始库存
     */
    private int initInventory  = 0  ;
    /**
     * 试用须知
     */
    private String notice  = "" ;
    
    /**
     * 开始时间
     */
    private String startTime  = ""  ;
    /**
     * 结束时间
     */
    private String endTime  = ""  ;
    /**
     * 创建人
     */
    private String createUser  = ""  ;
    /**
     * 创建时间
     */
    private String createTime  = ""  ;
    /**
     * 更新人
     */
    private String updateUser  = ""  ;
    /**
     * 更新时间
     */
    private String updateTime  = ""  ;

    /**
     * 商品product信息
     */
    private PcProductinfo pInfo = new PcProductinfo(); 

    /**
     * 试用状态
     */
    private String tryoutStatus = "";
    /**
     * 申请人数
     */
    private int applyNum = 0;
    

    /**
     * 结束时间（排序用）
     */
    private String sortEndTime  = ""  ;
	public Integer getZid() {
		return zid;
	}
	public void setZid(Integer zid) {
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
	public String getAppCode() {
		return appCode;
	}
	public void setAppCode(String appCode) {
		this.appCode = appCode;
	}
	public String getTryoutPrice() {
		return tryoutPrice;
	}
	public void setTryoutPrice(String tryoutPrice) {
		this.tryoutPrice = tryoutPrice;
	}
	public String getLevelCode() {
		return levelCode;
	}
	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}
	public String getIsFreeShipping() {
		return isFreeShipping;
	}
	public void setIsFreeShipping(String isFreeShipping) {
		this.isFreeShipping = isFreeShipping;
	}
	public int getTryoutInventory() {
		return tryoutInventory;
	}
	public void setTryoutInventory(int tryoutInventory) {
		this.tryoutInventory = tryoutInventory;
	}
	public int getInitInventory() {
		return initInventory;
	}
	public void setInitInventory(int initInventory) {
		this.initInventory = initInventory;
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
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getUpdateUser() {
		return updateUser;
	}
	public void setUpdateUser(String updateUser) {
		this.updateUser = updateUser;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public PcProductinfo getpInfo() {
		return pInfo;
	}
	public void setpInfo(PcProductinfo pInfo) {
		this.pInfo = pInfo;
	}
	public BigDecimal getPostage() {
		return this.postage;
	}
	public void setPostage(BigDecimal postage) {
		this.postage = new BigDecimal(MoneyHelper.format(postage));
	}
	public String getTryoutStatus() {
		return tryoutStatus;
	}
	public void setTryoutStatus(String tryoutStatus) {
		this.tryoutStatus = tryoutStatus;
	}
	public String getNotice() {
		return notice;
	}
	public void setNotice(String notice) {
		this.notice = notice;
	}
	public int getApplyNum() {
		return applyNum;
	}
	public void setApplyNum(int applyNum) {
		this.applyNum = applyNum;
	}
	public String getSortEndTime() {
		return sortEndTime;
	}
	public void setSortEndTime(String sortEndTime) {
		this.sortEndTime = sortEndTime;
	}

}


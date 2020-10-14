package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 闪购活动
 * @author 李国杰
 *
 */
public class FlashsalesActivity {

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
	 * 活动开始时间
	 */
	private String startTime = "";
	/**
	 * 活动结束时间
	 */
	private String endTime = "";

	/**
	 * 状态
	 */
	private String status = "";	
	
	/**
	 * 描述
	 */
	private String remark = "";

	/**
	 * 创建时间
	 */
	private String createTime = "";
	/**
	 * 更新时间
	 */
	private String updateTime = "";
	/**
	 * 更新人
	 */
	private String updateUser = "";
	/**
	 * 创建人
	 */
	private String createUser = "";

	/**
	 * 闪购商品
	 */
	private List<FlashsalesSkuInfo> product = new ArrayList<FlashsalesSkuInfo>();
	
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

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
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

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public List<FlashsalesSkuInfo> getProduct() {
		return product;
	}

	public void setProduct(List<FlashsalesSkuInfo> product) {
		this.product = product;
	}

	
}

package com.cmall.groupcenter.coupon;

/**
 * 该model单独用于导入xmls使用
 * @author LHY
 * 2015年7月30日 下午3:02:13
 */
public class CouponImportModel {
	/**
	 * 主键：自增ID
	 */
	private int zid;
	/**
	 * 扩展ID
	 */
	private String uid;
	/**
	 * 来源：第三方合作应用名称
	 */
	private String source;
	/**
	 * 优惠劵名称：第三方应用中导入
	 */
	private String name;
	/**
	 * 优惠劵类型名：例如：满1999减299.
	 */
	private String typeName;
	/**
	 * 优惠劵面额：如 50元
	 */
	private String couponAmount;
	/**
	 * 使用下限金额：如果满减的话，使用下限值不为0。
	 */
	private String limitDown;
	/**
	 * 开始时间：有效时间开始时间。
	 */
	private String startTime;
	/**
	 * 结束时间：有效时间结束时间。
	 */
	private String endTime;
	/**
	 * 限制：简单描述
	 */
	private String limitDesc;
	/**
	 * 优惠码：第三方应用中导入。
	 */
	private String couponCode;
	/**
	 * 使用限制：限制的详细说明
	 */
	private String limitDescription;
	/**
	 * 是否独享：0：否，1：安卓，2：苹果
	 */
	private String isExclusive;
	/**
	 * 导入时间
	 */
	private String importTime;
	/**
	 * 修改时间
	 */
	private String updateTime;
	/**
	 * 导入操作人
	 */
	private String creator;
	/**
	 * 修改操作人
	 */
	private String editor;
	/**
	 * 外键：关联gc_coupon_import_manage表
	 */
	private String fkeyId;

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

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getCouponAmount() {
		return couponAmount;
	}

	public void setCouponAmount(String couponAmount) {
		this.couponAmount = couponAmount;
	}

	public String getLimitDown() {
		return limitDown;
	}

	public void setLimitDown(String limitDown) {
		this.limitDown = limitDown;
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

	public String getLimitDesc() {
		return limitDesc;
	}

	public void setLimitDesc(String limitDesc) {
		this.limitDesc = limitDesc;
	}

	public String getCouponCode() {
		return couponCode;
	}

	public void setCouponCode(String couponCode) {
		this.couponCode = couponCode;
	}

	public String getLimitDescription() {
		return limitDescription;
	}

	public void setLimitDescription(String limitDescription) {
		this.limitDescription = limitDescription;
	}

	public String getIsExclusive() {
		return isExclusive;
	}

	public void setIsExclusive(String isExclusive) {
		this.isExclusive = isExclusive;
	}

	public String getImportTime() {
		return importTime;
	}

	public void setImportTime(String importTime) {
		this.importTime = importTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public String getEditor() {
		return editor;
	}

	public void setEditor(String editor) {
		this.editor = editor;
	}

	public String getFkeyId() {
		return fkeyId;
	}

	public void setFkeyId(String fkeyId) {
		this.fkeyId = fkeyId;
	}
}
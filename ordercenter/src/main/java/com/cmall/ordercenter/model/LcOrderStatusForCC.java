package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 日志流水  
 * @author zhaoxq
 *
 */
public class LcOrderStatusForCC{
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String code = "";
	
	/**
	 * 日志信息
	 */
	@ZapcomApi(value="日志信息")
	private String info = "";
	
	/**
	 * 创建时间
	 */
	@ZapcomApi(value="创建时间")
	private String createTime = "";
	
	/**
	 * 操作人
	 */
	@ZapcomApi(value="操作人")
	private String createUser = "";
	
	/**
	 * 原先状态
	 */
	@ZapcomApi(value="原先状态",remark="4497153900010001:下单成功-未付款<br/>"
									+"4497153900010002:下单成功-未发货<br/>"
									+"4497153900010003:已发货<br/>"
									+"4497153900010004:已收货<br/>"
									+"4497153900010005:交易成功<br/>"
									+"4497153900010006:交易失败")
	private String oldStatus = "";
	
	/**
	 * 当前状态
	 */
	@ZapcomApi(value="当前状态",remark="4497153900010001:下单成功-未付款<br/>"
									+"4497153900010002:下单成功-未发货<br/>"
									+"4497153900010003:已发货<br/>"
									+"4497153900010004:已收货<br/>"
									+"4497153900010005:交易成功<br/>"
									+"4497153900010006:交易失败")
	private String nowStatus = "";

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}

	public String getOldStatus() {
		return oldStatus;
	}

	public void setOldStatus(String oldStatus) {
		this.oldStatus = oldStatus;
	}

	public String getNowStatus() {
		return nowStatus;
	}

	public void setNowStatus(String nowStatus) {
		this.nowStatus = nowStatus;
	}
}

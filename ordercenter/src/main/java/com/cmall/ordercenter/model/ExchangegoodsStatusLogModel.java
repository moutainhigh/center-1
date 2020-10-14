package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 换货日志信息
 * @author gaoy
 *
 */
public class ExchangegoodsStatusLogModel extends BaseClass{

	/**
	 * 换货单号
	 */
	private String exchangeNo = "";
	
	/**
	 * 日志信息
	 */
	private String info = "";
	
	/**
	 * 创建时间
	 */
	private String createTime = "";
	
	/**
	 * 创建人
	 */
	private String createUser = "";
	
	/**
	 * 原先状态
	 */
	private String oldStatus = "";
	
	/**
	 * 现在状态
	 */
	private String nowStatus = "";
	
	public String getExchangeNo() {
		return exchangeNo;
	}

	public void setExchangeNo(String exchangeNo) {
		this.exchangeNo = exchangeNo;
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

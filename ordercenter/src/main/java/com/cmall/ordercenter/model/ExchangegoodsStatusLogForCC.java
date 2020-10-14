package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 换货日志信息  
 * @author zhaoxq
 *
 */
public class ExchangegoodsStatusLogForCC{

	/**
	 * 换货单号
	 */
	@ZapcomApi(value="换货单号")
	private String exchangeNo = "";
	
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
	 * 创建人
	 */
	@ZapcomApi(value="创建人")
	private String createUser = "";
	
	/**
	 * 原先状态
	 */
	@ZapcomApi(value="原先状态",remark="4497153900020002:待审核<br/>"
									+"4497153900020003:审核失败<br/>"
									+"4497153900020004:通过审核")
	private String oldStatus = "";
	
	/**
	 * 现在状态
	 */
	@ZapcomApi(value="现在状态",remark="4497153900020002:待审核<br/>"
									+"4497153900020003:审核失败<br/>"
									+"4497153900020004:通过审核")
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

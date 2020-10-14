package com.cmall.ordercenter.model;

import com.srnpr.zapcom.topapi.RootInput;

/**
 * 
 * 查询换货信息输入参数
 * 项目名称：ordercenter 
 * 类名称：     ApiModExchangegoodsInput 
 * 类描述：     换货信息对象
 * 创建人：     gaoy  
 * 创建时间：2013年9月18日下午1:04:23 
 * 修改人：     gaoy
 * 修改时间：2013年9月18日下午1:04:23
 * 修改备注：  
 * @version
 *
 */
public class ApiModExchangegoodsInput  extends RootInput{
	
	/**
	 * 换货单号
	 */
	private String exchangeNo = "";
	
	/**
	 * 换货状态
	 */
	private String status = "";

	public String getExchangeNo() {
		return exchangeNo;
	}

	public void setExchangeNo(String exchangeNo) {
		this.exchangeNo = exchangeNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
}

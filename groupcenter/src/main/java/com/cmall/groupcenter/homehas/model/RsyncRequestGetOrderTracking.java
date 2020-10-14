package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;
/**
 * 订单配送轨迹查询接口传递参数
 * @author wz
 *
 */
public class RsyncRequestGetOrderTracking implements IRsyncRequest{
//	private String account;   //调用用户
//	private String password;   //调用密码
	private String ord_id;   //订单号
	
	
//	public String getAccount() {
//		return account;
//	}
//	public void setAccount(String account) {
//		this.account = account;
//	}
//	public String getPassword() {
//		return password;
//	}
//	public void setPassword(String password) {
//		this.password = password;
//	}
	public String getOrd_id() {
		return ord_id;
	}
	public void setOrd_id(String ord_id) {
		this.ord_id = ord_id;
	}
}

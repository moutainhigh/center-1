package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 用户订单轨迹
 * @author wz
 *
 */
public class ApiHomeMessageCntResult extends RootResultWeb{
	@ZapcomApi(value="客户端消息是否开启：Y 开启  N 未开启")
	private String is_flag="";
	@ZapcomApi(value="用户未读消息数量")
	private String message_cnt="";
	
	public String getIs_flag() {
		return is_flag;
	}
	public void setIs_flag(String is_flag) {
		this.is_flag = is_flag;
	}
	public String getMessage_cnt() {
		return message_cnt;
	}
	public void setMessage_cnt(String message_cnt) {
		this.message_cnt = message_cnt;
	}	
	
	

}

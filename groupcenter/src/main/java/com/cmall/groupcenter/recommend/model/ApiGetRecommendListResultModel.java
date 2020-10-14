package com.cmall.groupcenter.recommend.model;

import java.util.Date;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

public class ApiGetRecommendListResultModel {

	@ZapcomApi(value="被推荐的手机号") 
	private String mobile = "";
	
	@ZapcomApi(value="推荐人状态",remark="BH1001:已注册; BH1002:已下单")
	private String status = "";
	
	//拓展添加
	@ZapcomApi(value="被推荐人头像")
	private String avatar = "";
	@ZapcomApi(value="被推荐人昵称")
	private String nickName;
	
	@ZapcomApi(value="被推荐人注册时间")
	private String dateTime;
	
	
	
	public String getDateTime() {
		return dateTime;
	}
	public void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	
}

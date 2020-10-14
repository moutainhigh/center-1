package com.cmall.groupcenter.account.model;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class PersonalResult extends RootResultWeb{

	@ZapcomApi(value = "手机号码", remark = "手机号码")
	private String  mobile ="";
	
	@ZapcomApi(value = "级别名称")
	private String levelName = "";
	
	@ZapcomApi(value = "加入时间")
	private String createTime ="";
	
	@ZapcomApi(value = "邀请人")
	private String inviter = "";
	
	@ZapcomApi(value = "我的消息数量")
	private long MessageSize =0;
	
	@ZapcomApi(value = "我的收藏数量")
	private long favorites =0;
	
	@ZapcomApi(value = "是否推动", remark = "默认推送")
	private String isPush = "";
	
	@ZapcomApi(value = "是否激活产品列表",remark = "看是否有微公社账号")
	private List<String>  activationList  = new ArrayList<String>();
	
	@ZapcomApi(value = "个人头像url")
	private String headIconUrl = ""; 
	
	@ZapcomApi(value = "邀请人昵称")
	private String inviterNickName = ""; 
	
	@ZapcomApi(value = "昵称")
	private String nickName = ""; 
	
	@ZapcomApi(value = "证件名称")
	private String papersName = "";
	
	@ZapcomApi(value = "银行卡数量")
	private String bankCardsCount = "";
	
	@ZapcomApi(value = "账户的资金信息")
	public AccountModel accountProperty = new AccountModel();
	
	@ZapcomApi(value = "优惠券数量")
	private String couponCount = "";
	
	
	public String getCouponCount() {
		return couponCount;
	}

	public void setCouponCount(String couponCount) {
		this.couponCount = couponCount;
	}

	public AccountModel getAccountProperty() {
		return accountProperty;
	}

	public void setAccountProperty(AccountModel accountProperty) {
		this.accountProperty = accountProperty;
	}

	public String getBankCardsCount() {
		return bankCardsCount;
	}

	public void setBankCardsCount(String bankCardsCount) {
		this.bankCardsCount = bankCardsCount;
	}


	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getInviterNickName() {
		return inviterNickName;
	}

	public void setInviterNickName(String inviterNickName) {
		this.inviterNickName = inviterNickName;
	}
	public String getHeadIconUrl() {
		return headIconUrl;
	}

	public void setHeadIconUrl(String headIconUrl) {
		this.headIconUrl = headIconUrl;
	}

	
	
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getInviter() {
		return inviter;
	}

	public void setInviter(String inviter) {
		this.inviter = inviter;
	}



	public long getMessageSize() {
		return MessageSize;
	}

	public void setMessageSize(long messageSize) {
		MessageSize = messageSize;
	}

	public String getIsPush() {
		return isPush;
	}

	public void setIsPush(String isPush) {
		this.isPush = isPush;
	}

	public long getFavorites() {
		return favorites;
	}

	public void setFavorites(long favorites) {
		this.favorites = favorites;
	}

	public String getPapersName() {
		return papersName;
	}

	public void setPapersName(String papersName) {
		this.papersName = papersName;
	}

	public List<String> getActivationList() {
		return activationList;
	}

	public void setActivationList(List<String> activationList) {
		this.activationList = activationList;
	}
	
	
}

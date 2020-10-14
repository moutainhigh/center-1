package com.cmall.groupcenter.weixin.model;

import java.util.Date;




/**
 * 用户绑定信息
 * @author OFFICE
 *
 */
public class UserBindInfo {
	
	//绑定状态
	//绑定
	public static final int BIND=1;
	//未绑定
	public static final int UNBIND=0;
	
	//关注状态
	//关注
	public static final int SUBSCRIBE=1;
	//未关注
	public static final int UNSUBSCRIBE=0;
	
	
	//用户Id
	private long userId;
	
	
	//绑定账号Id
	private String openId;
	//微信昵称
	private String nickName;
	//性别(1、男性；2、女性；0、未知)
	private int sex;
	//用户所在城市
	private String city;
	//用户所在国家
	private String country;
	//用户所在省份
	private String province;
	//语言
	private String lang;
	//头像
	private String headImgUrl;
	//关注状态:1、关注；2、未关注
	private int subscribe;
	//关注时间
	private Date subscribeTime;
	//是否绑定 0、未绑定；1、绑定
	private int bind;
	// 只有在用户将公众号绑定到微信开放平台帐号后，才会出现该字段。详见：获取用户个人信息（UnionID机制）
	private String unionId; 
	//备注名
	private String remark;
	
	public long getUserId() {
		return userId;
	}
	public void setUserId(long userId) {
		this.userId = userId;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	public int getSex() {
		return sex;
	}
	public void setSex(int sex) {
		this.sex = sex;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getProvince() {
		return province;
	}
	public void setProvince(String province) {
		this.province = province;
	}
	public String getHeadImgUrl() {
		return headImgUrl;
	}
	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}
	public int getSubscribe() {
		return subscribe;
	}
	public void setSubscribe(int subscribe) {
		this.subscribe = subscribe;
	}
	public int getBind() {
		return bind;
	}
	public void setBind(int bind) {
		this.bind = bind;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public Date getSubscribeTime() {
		return subscribeTime;
	}
	public void setSubscribeTime(Date subscribeTime) {
		this.subscribeTime = subscribeTime;
	}

	public String getUnionId() {
		return unionId;
	}

	public void setUnionId(String unionId) {
		this.unionId = unionId;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	public String getAddress(){
		return this.country+this.province+this.city;
	}
	public String getOpenId() {
		return openId;
	}
	public void setOpenId(String openId) {
		this.openId = openId;
	}
}

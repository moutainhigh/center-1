package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiWechatProcessResult extends RootResultWeb{
	@ZapcomApi(value = "公众号 id")
	private String appid="";
	@ZapcomApi(value="商户生成的随机字符串")
	private String noncestr="";
	@ZapcomApi(value = "订单详情")
	private String packageValue="";
	@ZapcomApi(value = "财付通商户号")
	private String partnerid="";
	@ZapcomApi(value = "prepayid")
	private String prepayid="";
	@ZapcomApi(value = "签名")
	private String sign="";
	@ZapcomApi(value = "时间")
	private String timestamp="";
	
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getNoncestr() {
		return noncestr;
	}
	public void setNoncestr(String noncestr) {
		this.noncestr = noncestr;
	}
	public String getPackageValue() {
		return packageValue;
	}
	public void setPackageValue(String packageValue) {
		this.packageValue = packageValue;
	}
	public String getPartnerid() {
		return partnerid;
	}
	public void setPartnerid(String partnerid) {
		this.partnerid = partnerid;
	}
	public String getPrepayid() {
		return prepayid;
	}
	public void setPrepayid(String prepayid) {
		this.prepayid = prepayid;
	}
	public String getSign() {
		return sign;
	}
	public void setSign(String sign) {
		this.sign = sign;
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}
	
	
}

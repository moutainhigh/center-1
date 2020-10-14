package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiVersionAppInput  extends RootInput  {

	@ZapcomApi(value="手机号",remark="可以不用输入，不是必须")
	private String phone = "";
	@ZapcomApi(value="流水号")
	private String serialNumber = "";
	@ZapcomApi(value="渠道号")
	private String channelNumber = "";
	@ZapcomApi(value="手机型号",remark="对应手机当前手机类型，如：1代表ios,2代表andriod")
	private String iosAndriod = "";
	@ZapcomApi(value="版本号",remark="对应App版本添加时的版本号，区分大小写。如：V1.0、V1.1、V1.3等",require=1)
	private String versionApp = "";
	@ZapcomApi(value="平台代码",remark="对应每个系统的App的App_code值，如：刘嘉玲App的Code值是SI2001",require=1)
	private String versionCode = "";
	/**
	 * @return the phone
	 */
	public String getPhone() {
		return phone;
	}
	/**
	 * @param phone the phone to set
	 */
	public void setPhone(String phone) {
		this.phone = phone;
	}
	/**
	 * @return the versionCode
	 */
	public String getVersionCode() {
		return versionCode;
	}
	/**
	 * @param versionCode the versionCode to set
	 */
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	/**
	 * @return the versionApp
	 */
	public String getVersionApp() {
		return versionApp;
	}
	/**
	 * @param versionApp the versionApp to set
	 */
	public void setVersionApp(String versionApp) {
		this.versionApp = versionApp;
	}
	/**
	 * @return the iosAndriod
	 */
	public String getIosAndriod() {
		return iosAndriod;
	}
	/**
	 * @param iosAndriod the iosAndriod to set
	 */
	public void setIosAndriod(String iosAndriod) {
		this.iosAndriod = iosAndriod;
	}
	/**
	 * @return the serialNumber
	 */
	public String getSerialNumber() {
		return serialNumber;
	}
	/**
	 * @param serialNumber the serialNumber to set
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	/**
	 * @return the channelNumber
	 */
	public String getChannelNumber() {
		return channelNumber;
	}
	/**
	 * @param channelNumber the channelNumber to set
	 */
	public void setChannelNumber(String channelNumber) {
		this.channelNumber = channelNumber;
	}
	
}

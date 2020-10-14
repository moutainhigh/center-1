package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiVersionAppResult  extends RootResultWeb{
	
	@ZapcomApi(value="升级方式",remark="参数说明：0、代表调用失败，1、代表强制升级，2、代表不强制升级，3、代表不用升级，4、代表静默升级")
	private  String  upgradeSelect ="";
	@ZapcomApi(value="升级版本号",remark="用户设定升级到的版本号")
	private  String  appVersion ="";
	@ZapcomApi(value="升级连接",remark="App下载链接地址")
	private  String  appUrl="";
	@ZapcomApi(value="升级内容",remark="升级内容信息描述")
	private  String  upgradeContent="";
	@ZapcomApi(value="是否添加ifda",remark="449746250001代表是，449746250002代表否,该字段只为ios提供")
	private String ifda = "";
	@ZapcomApi(value="文件替换",remark="替换已发布的js文件，用于生产bug解决，如果后台系统没有配置相应的数据，该字段为null,该字段只为ios提供")
	private String fileUrl = "";
	@ZapcomApi(value="zip文件替换",remark="如果后台系统没有配置相应的数据，该字段为null,该字段只为ios提供")
	private String zipUrl = "";
	@ZapcomApi(value="广告图",remark="如果后台系统没有配置相应的数据，该字段为null,该字段只为ios提供")
	private String imgUrl = "";
	@ZapcomApi(value="图片连接地址",remark="如果后台系统没有配置相应的数据，该字段为null,该字段只为ios提供")
	private String imgHrefUrl = "";
	@ZapcomApi(value="安卓文件地址",remark="如果后台系统没有配置相应的数据，该字段为null,该字段只为andriod提供")
	private String plugUrl = "";
	@ZapcomApi(value="MD5值",remark="如果后台系统没有配置相应的数据，该字段为null,该字段只为andriod提供")
	private String mdFive="";
	@ZapcomApi(value="提示次数",remark="提示次数: 0为首次启动提醒  1为每次启动提醒")
	private String remindCounts="";
	
	
	
	public String getRemindCounts() {
		return remindCounts;
	}
	public void setRemindCounts(String remindCounts) {
		this.remindCounts = remindCounts;
	}
	/**
	 * @return the mdFive
	 */
	public String getMdFive() {
		return mdFive;
	}
	/**
	 * @param mdFive the mdFive to set
	 */
	public void setMdFive(String mdFive) {
		this.mdFive = mdFive;
	}
	/**
	 * @return the upgradeSelect
	 */
	public String getUpgradeSelect() {
		return upgradeSelect;
	} 
	/**
	 * @param upgradeSelect the upgradeSelect to set
	 */
	public void setUpgradeSelect(String upgradeSelect) {
		this.upgradeSelect = upgradeSelect;
	}
	/**
	 * @return the appVersion
	 */
	public String getAppVersion() {
		return appVersion;
	}
	/**
	 * @param appVersion the appVersion to set
	 */
	public void setAppVersion(String appVersion) {
		this.appVersion = appVersion;
	}
	/**
	 * @return the appUrl
	 */
	public String getAppUrl() {
		return appUrl;
	}
	/**
	 * @param appUrl the appUrl to set
	 */
	public void setAppUrl(String appUrl) {
		this.appUrl = appUrl;
	}
	/**
	 * @return the upgradeContent
	 */
	public String getUpgradeContent() {
		return upgradeContent;
	}
	/**
	 * @param upgradeContent the upgradeContent to set
	 */
	public void setUpgradeContent(String upgradeContent) {
		this.upgradeContent = upgradeContent;
	}
	/**
	 * @return the ifda
	 */
	public String getIfda() {
		return ifda;
	}
	/**
	 * @param ifda the ifda to set
	 */
	public void setIfda(String ifda) {
		this.ifda = ifda;
	}
	/**
	 * @return the fileUrl
	 */
	public String getFileUrl() {
		return fileUrl;
	}
	/**
	 * @param fileUrl the fileUrl to set
	 */
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}
	/**
	 * @param imgUrl the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	/**
	 * @return the imgHrefUrl
	 */
	public String getImgHrefUrl() {
		return imgHrefUrl;
	}
	/**
	 * @param imgHrefUrl the imgHrefUrl to set
	 */
	public void setImgHrefUrl(String imgHrefUrl) {
		this.imgHrefUrl = imgHrefUrl;
	}
	/**
	 * @return the zipUrl
	 */
	public String getZipUrl() {
		return zipUrl;
	}
	/**
	 * @param zipUrl the zipUrl to set
	 */
	public void setZipUrl(String zipUrl) {
		this.zipUrl = zipUrl;
	}
	/**
	 * @return the plugUrl
	 */
	public String getPlugUrl() {
		return plugUrl;
	}
	/**
	 * @param plugUrl the plugUrl to set
	 */
	public void setPlugUrl(String plugUrl) {
		this.plugUrl = plugUrl;
	}
	

}

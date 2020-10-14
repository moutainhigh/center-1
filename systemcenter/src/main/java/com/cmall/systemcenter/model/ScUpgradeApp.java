package com.cmall.systemcenter.model;

import java.io.Serializable;

/**
 * APP版本升级提示
 * @author zhough
 * 创建时间：2014-09-20
 * @version 1.0
 */
public class ScUpgradeApp implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 主键ID
	 */
	private int zid;
	/**
	 * 系统需要的ID
	 */
	private String uid;
	/**
	 * appid
	 */
	private int appId;
	/**
	 * 版本号
	 */
	private String versinId;
	/**
	 * 创建人
	 */
	private String founder;
	/**
	 * 创建时间
	 */
	private String createTime;
	/**
	 * 升级地址URL
	 */
	private String url;
	/**
	 * 升级方式  1强制升级，2 不强制升级，3不用升级
	 */
	private int upgradeSelect;
	/**
	 * 升级内容描述
	 */
	private String upgradeContent;
	
	/**
	 * 无参构造方法
	 */
	public ScUpgradeApp() {
		super();
	}

	/**全部参数构造方法
	 * @param zid
	 * @param uid
	 * @param appId
	 * @param versinId
	 * @param founder
	 * @param createTime
	 * @param url
	 * @param upgradeSelect
	 * @param upgradeContent
	 */
	public ScUpgradeApp(int zid, String uid, int appId, String versinId,
			String founder, String createTime, String url, int upgradeSelect,
			String upgradeContent) {
		super();
		this.zid = zid;
		this.uid = uid;
		this.appId = appId;
		this.versinId = versinId;
		this.founder = founder;
		this.createTime = createTime;
		this.url = url;
		this.upgradeSelect = upgradeSelect;
		this.upgradeContent = upgradeContent;
	}

	/**
	 * @return the zid
	 */
	public int getZid() {
		return zid;
	}

	/**
	 * @param zid the zid to set
	 */
	public void setZid(int zid) {
		this.zid = zid;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param uid the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

	/**
	 * @return the appId
	 */
	public int getAppId() {
		return appId;
	}

	/**
	 * @param appId the appId to set
	 */
	public void setAppId(int appId) {
		this.appId = appId;
	}

	/**
	 * @return the versinId
	 */
	public String getVersinId() {
		return versinId;
	}

	/**
	 * @param versinId the versinId to set
	 */
	public void setVersinId(String versinId) {
		this.versinId = versinId;
	}

	/**
	 * @return the founder
	 */
	public String getFounder() {
		return founder;
	}

	/**
	 * @param founder the founder to set
	 */
	public void setFounder(String founder) {
		this.founder = founder;
	}

	/**
	 * @return the createTime
	 */
	public String getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime the createTime to set
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param url the url to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * @return the upgradeSelect
	 */
	public int getUpgradeSelect() {
		return upgradeSelect;
	}

	/**
	 * @param upgradeSelect the upgradeSelect to set
	 */
	public void setUpgradeSelect(int upgradeSelect) {
		this.upgradeSelect = upgradeSelect;
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
	
}

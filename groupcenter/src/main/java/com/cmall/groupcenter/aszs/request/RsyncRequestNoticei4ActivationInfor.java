package com.cmall.groupcenter.aszs.request;

import com.cmall.groupcenter.groupface.IRsyncRequest;


/** 
* @ClassName: RsyncRequestNoticei4ActivationInfor 
* @Description: 通知爱思助手用户激活的请求参数
* @author 张海生
* @date 2016-3-3 下午3:30:03 
*  
*/
public class RsyncRequestNoticei4ActivationInfor implements IRsyncRequest {

	
	/**
	 * 
	 */
	private String aisicid = "";

	/**
	 * 
	 */
	private String aisi = "";
	
	/**
	 * 应用唯一标识
	 */
	private String appid = "";
	/**
	 * WIFI MAC 地址(全小写,去掉分隔符)
	 */
	private String mac = "";
	/**
	 * 用户设备标识(iOS7.0 以上系统)
	 */
	private String idfa = "";
	/**
	 * 用户设备 iOS 系统版本
	 */
	private String os = "";
	/**
	 * 接口返回值类型(提供两种返回格式)
	 * 1. JSON : {“success” : ”true/false”, ”message” : ”返回消息”}
	 * 2. 字符串 : 1:成功 0:失败
	 */
	private String rt = "";
	public String getAisicid() {
		return aisicid;
	}
	public void setAisicid(String aisicid) {
		this.aisicid = aisicid;
	}
	public String getAisi() {
		return aisi;
	}
	public void setAisi(String aisi) {
		this.aisi = aisi;
	}
	public String getAppid() {
		return appid;
	}
	public void setAppid(String appid) {
		this.appid = appid;
	}
	public String getMac() {
		return mac;
	}
	public void setMac(String mac) {
		this.mac = mac;
	}
	public String getIdfa() {
		return idfa;
	}
	public void setIdfa(String idfa) {
		this.idfa = idfa;
	}
	public String getOs() {
		return os;
	}
	public void setOs(String os) {
		this.os = os;
	}
	public String getRt() {
		return rt;
	}
	public void setRt(String rt) {
		this.rt = rt;
	}
}

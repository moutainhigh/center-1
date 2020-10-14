package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**   
*    
* 项目名称：ordercenter   
* 类名称：OrderStatusLog   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-2 下午7:41:52   
* 修改人：yanzj
* 修改时间：2013-9-2 下午7:41:52   
* 修改备注：   
* @version    
*    
*/
public class OrderStatusLog extends BaseClass {
	
	/**
	 * 编码
	 */
	private String code="";
	/**
	 * 描述信息
	 */
	private String info="";
	/**
	 * 创建时间
	 */
	private String createTime="";
	/**
	 * 创建人
	 */
	private String createUser ="system";
	/**
	 * 原先状态
	 */
	private String oldStatus = "";
	/**
	 * 当前状态
	 */
	private String nowStatus = "";
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getInfo() {
		return info;
	}
	public void setInfo(String info) {
		this.info = info;
	}
	public String getCreateTime() {
		return createTime;
	}
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
	public String getCreateUser() {
		return createUser;
	}
	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
	public String getOldStatus() {
		return oldStatus;
	}
	public void setOldStatus(String oldStatus) {
		this.oldStatus = oldStatus;
	}
	public String getNowStatus() {
		return nowStatus;
	}
	public void setNowStatus(String nowStatus) {
		this.nowStatus = nowStatus;
	}
	
}

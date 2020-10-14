package com.cmall.groupcenter.behavior.config;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 百分点相关配置信息
 * @author pang_jhui
 *
 */
public class BfdRecResultConfig extends BaseClass{
	
	/**
	 * 获取请求路径
	 * @return
	 */
	public String getRequestUrl(){
		
		return bConfig("groupcenter.bfd_rec_result_url");
		
	}	
	
	/**
	 * 获取请求路径
	 * @return
	 */
	public String getRequestPath(){
		
		return getRequestUrl()+"?cid="+getUserName();
		
	}
	
	/**
	 * 百分点推荐栏ID
	 * @return
	 */
	public String getRecId(String operFlag){
		
		return bConfig("groupcenter."+operFlag+"_bfd_rec_id");
		
	}
	
	/**
	 * 百分点请求推荐栏ID
	 * @return
	 */
	public String getReqRecId(String operFlag){
		
		return bConfig("groupcenter."+operFlag+"_bfd_req_rec_id");
		
	}
	
	/**
	 * 获取格式化字符串
	 * @return
	 */
	public String getFmt(){
		
		return bConfig("groupcenter.bfd_rec_fmt");
		
	}
	
	/**
	 * 获取用户名
	 * @return
	 */
	public String getUserName(){
		
		return bConfig("groupcenter.bfd_user_name");
		
	}

}

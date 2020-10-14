package com.cmall.groupcenter.behavior.config;

import com.srnpr.zapcom.baseclass.BaseClass;

public class BfdUnderProductConfig extends BaseClass {
	
	/**
	 * 获取请求路径
	 * @return
	 */
	public String getRequestUrl(){
		
		return bConfig("groupcenter.bfd_under_product_url");
		
	}	
	
	/**
	 * 获取请求路径
	 * @return
	 */
	public String getRequestPath(){
		
		return getRequestUrl()+"?cid="+getUserName();
		
	}
	
	/**
	 * 获取用户名
	 * @return
	 */
	public String getUserName(){
		
		return bConfig("groupcenter.bfd_user_name");
		
	}

}

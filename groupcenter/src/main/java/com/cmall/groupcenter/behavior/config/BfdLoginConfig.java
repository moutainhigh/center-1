package com.cmall.groupcenter.behavior.config;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 百分点登录配置信息
 * @author pang_jhui
 *
 */
public class BfdLoginConfig extends BaseClass {
	
	/**
	 * 获取请求路径
	 * @return
	 */
	public String getRequestUrl(){
		
		return bConfig("groupcenter.bfd_login_rul");
		
	}
	
	/**
	 * 获取用户名
	 * @return
	 */
	public String getUserName(){
		
		return bConfig("groupcenter.bfd_user_name");
		
	}
	
	/**
	 * 获取用户登录密码
	 * @return
	 */
	public String getUserPwd(){
		
		return bConfig("groupcenter.bfd_user_pwd");
		
	}

}

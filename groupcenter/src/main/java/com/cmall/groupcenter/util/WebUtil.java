package com.cmall.groupcenter.util;

import javax.servlet.http.HttpServletRequest;


/**
 * web的帮助类
 * 
 * @author lipengfei
 * @date 2015-5-21
 * email:lipf@ichsy.com
 *
 */
public class WebUtil {
		
	
	/**
	 * 不带端口号
	 * @author lipengfei
	 * @date 2015-5-21
	 * @param request
	 * @return
	 */
	public static String getAppBaseUrl(HttpServletRequest request){
		String appName = request.getContextPath()+"/";
		String basePath = request.getScheme()+"://"+request.getServerName()+appName;
		return basePath;
	}

	/**
	 * 带有端口号
	 * @author lipengfei
	 * @date 2015-5-21
	 * @param request
	 * @return
	 */
	public static String getAppBaseUrlWithPort(HttpServletRequest request){
		String appName = request.getContextPath()+"/";
		String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+appName;
		return basePath;
	}
	
	
	/**
	 * 通过乾和晟云系统消息接口的值来获取对外接口的值
	 * 如通过消息类型 44974720000400010001 来获得消息类型 1.
	 * @author lipengfei
	 * @date 2015-6-18
	 * @param systemCode
	 * @return
	 */
	public static String getMessageCodeBySystemCode(String systemCode){
		
		String messageCode = "";
		
		if("44974720000400010001".equals(systemCode)){
			
			messageCode = "1";
			
		}else if("44974720000400010002".equals(systemCode)){
			
			messageCode = "2";
			
		}else if("44974720000400010003".equals(systemCode)){
			
			messageCode = "3";
			
		}
		
		return messageCode;
	}
	
	/**
	 * 通过乾和晟云外接口的值来获取来获取对系统消息类型的值
	 * 如通过消息类型 1 来获得系统的消息类型 44974720000400010001.
	 * @author lipengfei
	 * @date 2015-6-18
	 * @param systemCode
	 * @return
	 */
	public static String getSystemCodeByMessageCode(String messageCode){
		
		String systemCode = "";
		
		if("1".equals(messageCode)){
			
			systemCode = "44974720000400010001";
			
		}else if("2".equals(messageCode)){
			
			systemCode = "44974720000400010002";
			
		}else if("3".equals(messageCode)){
			
			systemCode = "44974720000400010003";
			
		}else if("4".equals(messageCode)){
			systemCode = "4";
		}
		
		return systemCode;
	}
	
}

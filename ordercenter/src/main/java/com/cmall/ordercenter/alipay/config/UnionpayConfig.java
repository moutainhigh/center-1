package com.cmall.ordercenter.alipay.config;
/**
 * 和包支付协议参数
 * @author wz
 *
 */
public class UnionpayConfig {
	//本地应用上下文地址
	public static String localAddr = "http://222.240.192.206:8080/S2";
		//编码格式
	public static String characterSet = "02"; //00--GBK;01--GB2312;02--UTF-8
		//页面通知地址
	public static String callbackUrl = localAddr + "/callback.jsp";
		//后台通知地址
	public static String notifyUrl = localAddr + "/notify_url.jsp";
		//获取用户的IP地址，作为防钓鱼的一种方法
//		String clientIp = Request.getHeader("x-forwarded-for");
//		if ((clientIp == null) || (clientIp.length() == 0)
//				|| ("unknown".equalsIgnoreCase(clientIp))) {
//			clientIp = Request.getHeader("Proxy-Client-IP");
//		}
//		if ((clientIp == null) || (clientIp.length() == 0)
//				|| ("unknown".equalsIgnoreCase(clientIp))) {
//			clientIp = Request.getHeader("WL-Proxy-Client-IP");
//		}
//		if ((clientIp == null) || (clientIp.length() == 0)
//				|| ("unknown".equalsIgnoreCase(clientIp))) {
//			clientIp = Request.getRemoteAddr();
//		}
//		String ipAddress = clientIp;
	public static String ipAddress = "172.18.80.235";
	//接口类型
	public static String type = "DirectPayConfirm";  
		//商户请求编号
	public static String requestId = String.valueOf(System.currentTimeMillis());
	public static String signType = "MD5";
	//版本号
	public static String version = "2.0.0";
		//商户编号
	public static String merchantId = "888009951990006";
	//public static String merchantId = "888073157340001";
		//商户密钥
	public static String signKey = "62OMAg9um3LFLuRtQ1wEsVErBh7HOdqU2fpFBrJ329S7lmDOtJuR3riYCW4wsiQ9";
	//public static String signKey = "0HgNAeOyG0B3DkDuzLUwC4AYW1GX1BtEOPIlKnMl0EVYMYXweQxMUiXs1hNnbptJ";
	public static String req_url = "https://ipos.10086.cn/ips/cmpayService";
}

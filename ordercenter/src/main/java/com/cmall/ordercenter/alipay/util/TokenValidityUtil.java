package com.cmall.ordercenter.alipay.util;

import com.cmall.ordercenter.alipay.config.ConstantUtil;
import com.cmall.ordercenter.alipay.util.httpClient.TenpayHttpClient;
import com.cmall.ordercenter.alipay.wechat.PrepayIdRequestHandler;
import com.srnpr.zapcom.baseclass.BaseClass;

public class TokenValidityUtil extends BaseClass{
	
	public static String access_token = "";
	/**
	 * 判断传递过来的参数access_token是否过期
	 * @param access_token
	 * @return
	 */
	public  boolean tokenIsExpire(String access_token) {
		boolean flag = false;
		PrepayIdRequestHandler wxReqHandler = new PrepayIdRequestHandler(null, null);
		wxReqHandler.setParameter("appid", bConfig("ordercenter.APP_ID")); //ConstantUtil.APP_ID
		wxReqHandler.setParameter("appkey",bConfig("ordercenter.APP_KEY"));   //ConstantUtil.APP_KEY
		wxReqHandler.setParameter("noncestr", WXUtil.getNonceStr());
		wxReqHandler.setParameter("package", ConstantUtil.packageValue);    //ConstantUtil.packageValue
		wxReqHandler.setParameter("timestamp", WXUtil.getTimeStamp());
		wxReqHandler.setParameter("traceid", ConstantUtil.traceid);

		// 生成支付签名
		String sign = wxReqHandler.createSHA1Sign();
		wxReqHandler.setParameter("app_signature", sign);
		wxReqHandler.setParameter("sign_method", ConstantUtil.SIGN_METHOD);
		String gateUrl = ConstantUtil.GATEURL + access_token;
		wxReqHandler.setGateUrl(gateUrl);

		// 发送请求
		String accesstoken = wxReqHandler.sendAccessToken();
		if (ConstantUtil.EXPIRE_ERRCODE.equals(accesstoken) || ConstantUtil.FAIL_ERRCODE.equals(accesstoken))
			flag = true;
		return flag;
	}
	
	
	/**
	 * 实际获取access_token的方法
	 * @return
	 */
	public  String getTokenReal() {
		String requestUrl = ConstantUtil.TOKENURL + "?grant_type=" + ConstantUtil.GRANT_TYPE + "&appid="
				+ bConfig("ordercenter.APP_ID") + "&secret=" + bConfig("ordercenter.APP_SECRET");//ConstantUtil.TOKENURL + "?grant_type=" + ConstantUtil.GRANT_TYPE + "&appid="+ ConstantUtil.APP_ID + "&secret=" + ConstantUtil.APP_SECRET;
		
		String resContent = "";
		TenpayHttpClient httpClient = new TenpayHttpClient();
		httpClient.setMethod("GET");
		httpClient.setReqContent(requestUrl);
		if (httpClient.call()) {
			resContent = httpClient.getResContent();
			if (resContent.indexOf(ConstantUtil.ACCESS_TOKEN) > 0) {
				access_token = JsonUtil.getJsonValue(resContent, ConstantUtil.ACCESS_TOKEN);
			} else {
				//System.out.println("获取access_token值返回错误！！！");
			}
		} else {
			//System.out.println("后台调用通信失败");
			//System.out.println(httpClient.getResponseCode());
			//System.out.println(httpClient.getErrInfo());
			// 有可能因为网络原因，请求已经处理，但未收到应答。
		}

		return access_token;
	}
}

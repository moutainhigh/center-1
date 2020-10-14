package com.cmall.systemcenter.util;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.Map.Entry;
import org.apache.http.client.utils.DateUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.apache.http.HttpEntity;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPObject;
import com.cmall.systemcenter.common.DateUtil;
import com.google.gson.JsonObject;
import com.srnpr.xmaspay.util.RSA;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.ALibabaJsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;

import kafka.utils.Json;

/**
 * 短信发送工具类
 * @author jlin
 *
 */
public class SmsUtil extends BaseClass {

	/***
	 * 发送短信<br>
	 * <h1>发送相同信息时，请务循环每个手机号码发送，把手机号码以 , 分割发送即可<h1>
	 * @param mobiles 手机号码以 , 分割 限量 100个
	 * @param message
	 * @param error  错误信息
	 * @return
	 */
	public boolean sendSms(String mobiles,String message,StringBuffer error){
		
		String userId=bConfig("systemcenter.sms_userId");//用户名
		String password=bConfig("systemcenter.sms_password");//密码
		String url=bConfig("systemcenter.sms_send_url");//请求连接
		String sms_sign=bConfig("systemcenter.sms_sign");//签名
		
		return sendSms(sms_sign, userId, password, url, mobiles, message, error);
	}
	
	/***
	 * 发送短信-营销短信渠道<br>
	 * <h1>发送相同信息时，请务循环每个手机号码发送，把手机号码以 , 分割发送即可<h1>
	 * @param mobiles 手机号码以 , 分割 限量 100个
	 * @param message
	 * @param error  错误信息
	 * @return
	 */
	public boolean sendSms2(String mobiles,String message,StringBuffer error){
		
		String userId=bConfig("systemcenter.sms_userId2");//用户名
		String password=bConfig("systemcenter.sms_password2");//密码
		String url=bConfig("systemcenter.sms_send_url2");//请求连接
		String sms_sign=bConfig("systemcenter.sms_sign2");//签名
		
		return sendSms(sms_sign, userId, password, url, mobiles, message, error);
	}
	
	/***
	 * 发送短信-嘉玲国际短信渠道<br>
	 * <h1>发送相同信息时，请务循环每个手机号码发送，把手机号码以 , 分割发送即可<h1>
	 * @param mobiles 手机号码以 , 分割 限量 100个
	 * @param message. 
	 * @param error  错误信息
	 * @return
	 */
	public boolean sendSmsForCapp(String mobiles,String message,StringBuffer error){
		
		String userId=bConfig("systemcenter.sms_userId3");//用户名
		String password=bConfig("systemcenter.sms_password3");//密码
		String url=bConfig("systemcenter.sms_send_url3");//请求连接
		String sms_sign=bConfig("systemcenter.sms_sign3");//签名
		
		return sendSms(sms_sign, userId, password, url, mobiles, message, error);
	}
	
	
	/***
	 * 惠家有新短信渠道<br>
	 * <h1>发送相同信息时，请务循环每个手机号码发送，把手机号码以 , 分割发送即可<h1>
	 * @param mobiles 手机号码以 , 分割 限量 100个
	 * @param message
	 * @param error  错误信息
	 * @return
	 */
	public boolean sendSms4(String mobiles,String message,StringBuffer error){
		
		String userId=bConfig("systemcenter.sms_userId4");//用户名
		String password=bConfig("systemcenter.sms_password4");//密码
		String url=bConfig("systemcenter.sms_send_url4");//请求连接
		String sms_sign=bConfig("systemcenter.sms_sign4");//签名
		
		return sendSms(sms_sign, userId, password, url, mobiles, message, error);
	}
	
	
	/***
	 * 微公社短信渠道<br>
	 * <h1>发送相同信息时，请务循环每个手机号码发送，把手机号码以 , 分割发送即可<h1>
	 * @param mobiles 手机号码以 , 分割 限量 100个
	 * @param message
	 * @param error  错误信息
	 * @return
	 */
	public boolean sendSms5(String mobiles,String message,StringBuffer error){
		
		String userId=bConfig("systemcenter.sms_userId5");//用户名
		String password=bConfig("systemcenter.sms_password5");//密码
		String url=bConfig("systemcenter.sms_send_url5");//请求连接
		String sms_sign=bConfig("systemcenter.sms_sign5");//签名
		
		return sendSms(sms_sign, userId, password, url, mobiles, message, error);
	}
	
	/***
	 * 最新惠家有通道<br>
	 * <h1>发送相同信息时，请务循环每个手机号码发送，把手机号码以 , 分割发送即可<h1>
	 * @param mobiles 手机号码以 , 分割 限量 100个
	 * @param message
	 * @param error  错误信息
	 * @return
	 */
	public boolean sendSms6(String mobiles,String message,StringBuffer error){
		
		String userId=bConfig("systemcenter.sms_userId6");//用户名
		String password=bConfig("systemcenter.sms_password6");//密码
		String url=bConfig("systemcenter.sms_send_url6");//请求连接
		String sms_sign=bConfig("systemcenter.sms_sign6");//签名
		
		return sendSms1(sms_sign, userId, password, url, mobiles, message, error, "1");
	}
	
	/***
	 * 最新惠家有通道<br>
	 * <h1>发送相同信息时，请务循环每个手机号码发送，把手机号码以 , 分割发送即可  --添加sender 针对营销短信时sender=4 <h1>
	 * @param mobiles 手机号码以 , 分割 限量 100个
	 * @param message
	 * @param error  错误信息
	 * @return
	 */
	public boolean sendSmsBySender(String mobiles,String message,StringBuffer error,String sender){
		
		String userId=bConfig("systemcenter.sms_userId6");//用户名
		String password=bConfig("systemcenter.sms_password6");//密码
		String url=bConfig("systemcenter.sms_send_url6");//请求连接
		String sms_sign=bConfig("systemcenter.sms_sign6");//签名
		
		return sendSms1(sms_sign, userId, password, url, mobiles, message, error, sender);
	}
	
	/***
	 * 沙皮狗通道<br>
	 * <h1>发送相同信息时，请务循环每个手机号码发送，把手机号码以 , 分割发送即可<h1>
	 * @param mobiles 手机号码以 , 分割 限量 100个
	 * @param message
	 * @param error  错误信息
	 * @return
	 */
	public boolean sendSms7(String mobiles,String message,StringBuffer error){
		
		String userId=bConfig("systemcenter.sms_userId7");//用户名
		String password=bConfig("systemcenter.sms_password7");//密码
		String url=bConfig("systemcenter.sms_send_url7");//请求连接
		String sms_sign=bConfig("systemcenter.sms_sign7");//签名
		
		return sendSms1(sms_sign, userId, password, url, mobiles, message, error, "2");
	}
	
	
	private boolean sendSms(String sms_sign,String userId,String password,String url,String mobiles,String message,StringBuffer error){
		
		try {
			message=URLEncoder.encode(message, "UTF-8");
			String surl=url+"?userId="+userId+"&password="+password+"&pszMobis="+mobiles+"&pszMsg="+message+sms_sign+"&iMobiCount="+mobiles.split(",").length+"&pszSubPort=*";
			String response=WebClientSupport.create().doGet(surl);
			
			Document doc = DocumentHelper.parseText(response);
			response=doc.getStringValue();
			
			error.append(response);
			if(response.length()>10&&response.length()<25){
				bLogInfo(0, "短信发送成功["+response+"]:"+mobiles+"="+message);
				return true;
			}else{
				bLogInfo(0, "短信发送失败["+response+"]:"+mobiles+"="+message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private boolean sendSms1(String sms_sign, String userId, String password,
			String url, String mobiles, String message, StringBuffer error, String sender) {

		try {
			//message = URLEncoder.encode(message, "UTF-8");
			String channelid = "6";
			String tradetype = "sendmessage";
			String tradetime = DateUtil.toString(new Date(), DateUtil.sdfDateTimeTamp);
			Set<String> randomCdkey = StringUtility.randomString(8, 1, "zhs");
			String orderno = "";
			for (String cdkeystr : randomCdkey) {
				orderno = cdkeystr;
				break;
			}
			String sign = userId + tradetype + orderno + tradetime + sender + mobiles + message  + channelid + password;
			String signString = MD5Code.encode(sign);
			MDataMap mp = new MDataMap();
			
			mp.put("merchantid", userId);
			mp.put("tradetype", tradetype);
			mp.put("orderno", orderno);
			mp.put("tradetime", tradetime);
			mp.put("receivers", mobiles);
			mp.put("sender", sender);
			mp.put("channelid", channelid);
			mp.put("message", message+sms_sign);
			mp.put("mac", signString);
			String response = WebClientSupport.upPost(url, mp);
			String resArr[] = response.split("&");
			error.append(response);
			if(resArr != null && resArr.length > 0){
				String codeArr[] = resArr[0].split("=");
				if("0".equals(codeArr[1])){
					bLogInfo(0, "短信发送成功[" + response + "]:" + mobiles + "="
							+ message);
					return true;
				}
				bLogError(0, "短信发送失败[" + response + "]:" + mobiles + "="
						+ message);
			}else {
				bLogError(0, "短信发送失败[" + response + "]:" + mobiles + "="
				+ message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 实时获取上行短信
	 */
	public void upSms(){
		
		String userId=bConfig("systemcenter.sms_userId2");//用户名
		String password=bConfig("systemcenter.sms_password2");//密码
		String url=bConfig("systemcenter.sms_up_url2");//请求连接
		
		try {
			String surl=url+"?userId="+userId+"&password="+password;
			String response=WebClientSupport.create().doGet(surl);
			
			//System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 亨通互联-营销短信<br>
	 * <h1>营销短信专用方法调用<h1>
	 * @param phoneNum 手机号码
	 * @param content  内容
	 */
	public boolean sendSmsForYX(String phoneNum,String  content) {
		String sms_yx_appId=bConfig("systemcenter.sms_yx_appId");
		String sms_yx_version=bConfig("systemcenter.sms_yx_version");
		String sms_yx_signType=bConfig("systemcenter.sms_yx_signType");
		String sms_yx_rsaPrivateKey=bConfig("systemcenter.sms_yx_rsaPrivateKey");
		String sms_yx_merchantCode=bConfig("systemcenter.sms_yx_merchantCode");
		String sms_yx_channelCode=bConfig("systemcenter.sms_yx_channelCode");
		String sms_yx_sendUrl=bConfig("systemcenter.sms_yx_sendUrl");

		//接口报文
		Map<String, Object> apiInput = new HashMap<String, Object>();
		apiInput.put("app_id", sms_yx_appId);
		apiInput.put("charset", "UTF-8");
		apiInput.put("sign_type", sms_yx_signType);
		apiInput.put("timestamp", DateUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss").toString());
		apiInput.put("version", sms_yx_version);
		Map<String, Object> inputBody = new HashMap<String, Object>();
		inputBody.put("mobile", phoneNum);
		inputBody.put("content", content);
		inputBody.put("merchant_code", sms_yx_merchantCode);
		inputBody.put("sms_channel_code", sms_yx_channelCode);
		apiInput.put("body", inputBody);
		apiInput.put("sign", RSA.sign(getParamsSignChar(inputBody), sms_yx_rsaPrivateKey, "UTF-8", "MD5withRSA"));//验签
		 try {
			String json = ALibabaJsonHelper.toJson(apiInput);
			String retunStr = WebClientSupport.upPostJson(sms_yx_sendUrl, json);
			JSONObject parseObject = JSONObject.parseObject(JSON.toJSON(retunStr).toString());
			String status = parseObject.get("status").toString();
			//System.out.println(retunStr);
			if("1".equals(status)) {
				return true;
			}else {
				System.out.println("sendSmsForYX|"+retunStr);
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 集团短信通路<br>
	 * @param phoneNum 手机号码
	 * @param content  内容
	 */
	public boolean sendSmsForCompany(String phoneNum,String  content) {
		String sms_yx_appId=bConfig("systemcenter.sms_yx_appId");
		String sms_yx_version=bConfig("systemcenter.sms_yx_version");
		String sms_yx_signType=bConfig("systemcenter.sms_yx_signType");
		String sms_yx_rsaPrivateKey=bConfig("systemcenter.sms_yx_rsaPrivateKey");
		String sms_yx_merchantCode=bConfig("systemcenter.sms_jtyx_merchantCode");
		String sms_yx_channelCode=bConfig("systemcenter.sms_jtyx_channelCode");
		String sms_yx_sendUrl=bConfig("systemcenter.sms_yx_sendUrl");

		//接口报文
		Map<String, Object> apiInput = new HashMap<String, Object>();
		apiInput.put("app_id", sms_yx_appId);
		apiInput.put("charset", "UTF-8");
		apiInput.put("sign_type", sms_yx_signType);
		apiInput.put("timestamp", DateUtils.formatDate(new Date(),"yyyy-MM-dd HH:mm:ss").toString());
		apiInput.put("version", sms_yx_version);
		Map<String, Object> inputBody = new HashMap<String, Object>();
		inputBody.put("mobile", phoneNum);
		inputBody.put("content", content);
		inputBody.put("merchant_code", sms_yx_merchantCode);
		inputBody.put("sms_channel_code", sms_yx_channelCode);
		apiInput.put("body", inputBody);
		apiInput.put("sign", RSA.sign(getParamsSignChar(inputBody), sms_yx_rsaPrivateKey, "UTF-8", "MD5withRSA"));//验签
		 try {
			String json = ALibabaJsonHelper.toJson(apiInput);
			String retunStr = WebClientSupport.upPostJson(sms_yx_sendUrl, json);
			JSONObject parseObject = JSONObject.parseObject(JSON.toJSON(retunStr).toString());
			String status = parseObject.get("status").toString();
			//System.out.println(retunStr);
			if("1".equals(status)) {
				return true;
			}else {
				System.out.println("sendSmsForYX|"+retunStr);
				return false;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}
	
	/**
	 * 获取参数加密串，将业务参数拼接起来，示例：a=b&b=c，参数为null不参与验签，空字符串参与验签
	 */
	private String getParamsSignChar(Map<String, Object> map) {
		JSONObject jsonObject = new JSONObject(map);
		Map<String, Object> jsonMap = new TreeMap<String, Object>();
		for(String k : jsonObject.keySet()) {
			jsonMap.put(k, jsonObject.get(k) == null ? "" : jsonObject.get(k).toString());
		}
		
		StringBuilder sb = new StringBuilder();
		Set<Entry<String, Object>> jsonMapEntry = jsonMap.entrySet();
		for(Entry<String, Object> entry : jsonMapEntry) {
			if(entry.getValue() == null) {
				continue;
			}
			sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}

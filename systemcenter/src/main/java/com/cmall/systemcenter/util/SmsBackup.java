package com.cmall.systemcenter.util;

import java.net.URLEncoder;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basesupport.WebClientSupport;

/**
 * 备用的短信平台
 * @author jlin
 *
 */
public class SmsBackup extends BaseClass{

	
	/**
	 * 发送短信
	 * @param mobile
	 * @param message
	 * @param errorCode 返回码
	 * @return
	 * @throws Exception
	 */
	public boolean send(String mobile,String message,StringBuffer errorCode)  {
		
		String OperID= bConfig("systemcenter.sms_back_OperID");
		String OperPass= bConfig("systemcenter.sms_back_OperPass");
		String url= bConfig("systemcenter.sms_back_url");
		String AppendID= bConfig("systemcenter.sms_AppendID");
		String sign= bConfig("systemcenter.sms_back_sign");
		
		return sendSms(sign, OperID, OperPass, url, mobile, AppendID, message, errorCode);
	}
	
	/**
	 * 发送短信
	 * @param sign 自定义签名
	 * @param mobile
	 * @param message
	 * @param errorCode
	 * @return
	 */
	public boolean send(String sign,String mobile,String message,StringBuffer errorCode)  {
		
		String OperID= bConfig("systemcenter.sms_back_OperID");
		String OperPass= bConfig("systemcenter.sms_back_OperPass");
		String url= bConfig("systemcenter.sms_back_url");
		String AppendID= bConfig("systemcenter.sms_AppendID");
		if(StringUtils.isBlank(sign)){//如果传入的签名为空，则使用默认签名
			sign= bConfig("systemcenter.sms_back_sign");
		}
		
		return sendSms(sign, OperID, OperPass, url, mobile, AppendID, message, errorCode);
	}
	
	private boolean sendSms(String sign,String OperID,String OperPass,String url,String mobile,String AppendID,String message,StringBuffer errorCode){
		
		try {
			
			message=URLEncoder.encode(sign+message, "GBK");
			String surl=url+"?OperID="+OperID+"&OperPass="+OperPass+"&SendTime=&ValidTime=&AppendID="+AppendID+"&DesMobile="+mobile+"&Content="+message+"&ContentType=8";
			
			String response=WebClientSupport.create().doGet(surl);

			Document doc = DocumentHelper.parseText(response);
			String responseCode=doc.getRootElement().node(0).getText();
			
			errorCode.append(responseCode);
			if("00,01,03".indexOf(responseCode)>=0){
				bLogInfo(0, "短信发送成功["+responseCode+"]:"+mobile+"="+message);
				return true;
			}else{
				bLogInfo(0, "短信发送失败["+responseCode+"]:"+mobile+"="+message);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}

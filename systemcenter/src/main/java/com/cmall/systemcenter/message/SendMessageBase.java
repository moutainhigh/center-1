package com.cmall.systemcenter.message;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapweb.webmodel.MMessage;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.websupport.MessageSupport;

public class SendMessageBase extends BaseClass {

	/**
	 * @param sManageCode
	 * @return
	 */
	public String upSendSourceByManageCode(String sManageCode) {
		String sReturn = "4497467200020001";

		// 嘉玲国际的通道编号
		if (sManageCode.equals("SI2001")) {
			sReturn = "4497467200020003";
		} //else if (sManageCode.equals("SI2009")) {// 家有汇的渠道发送短信
		//	sReturn = "4497467200020004";
		//} 
		else if (sManageCode.equals("SI2011")) {// 微公社的渠道发送短信
			sReturn = "4497467200020005";
		}else if(sManageCode.equals("SI2003") || sManageCode.equals("SI2009")){
			sReturn = "4497467200020006";
		}else if(sManageCode.equals("SI3003")){
			sReturn="4497467200020007";
		}

		return sReturn;
	}

	/**
	 * 发送验证码
	 * 
	 * @param sVerifyType
	 * @param sMobile
	 * @return
	 */
	public MWebResult sendVerifyCode(
			EVerifyCodeTypeEnumer eVerifyCodeTypeEnumer, String sMobile,
			String sMangeCode) {
		return sendVerifyCode(eVerifyCodeTypeEnumer, sMobile, sMangeCode, 6);
	}

	/**
	 * 发送指定长度验证码 最长10位
	 * 
	 * @param eVerifyCodeTypeEnumer
	 * @param sMobile
	 * @param sMangeCode
	 * @param iLength
	 * @return
	 */
	public MWebResult sendVerifyCode(
			EVerifyCodeTypeEnumer eVerifyCodeTypeEnumer, String sMobile,
			String sMangeCode, int iLength) {

		String sRandom = StringUtils.leftPad(
				String.valueOf(RandomUtils.nextInt((int)
						 Math.pow(10, iLength) - 1)), iLength, "0");

		VerifySupport verifySupport = new VerifySupport();

		String content=null;
		
		if(sMangeCode.equals("SI2011")&&(EVerifyCodeTypeEnumer.Binding==eVerifyCodeTypeEnumer)){
			//微公社短信模板特殊，在这里从新定义
			content=bInfo(949702108, sRandom);
		}else if(sMangeCode.equals("SI3003")){
			content=bInfo(949702109, sRandom);
		}else if(sMangeCode.equals("SI2003")&&(EVerifyCodeTypeEnumer.MemberReginster==eVerifyCodeTypeEnumer||EVerifyCodeTypeEnumer.ForgetPassword==eVerifyCodeTypeEnumer)){
			content=bInfo(949702110, sRandom);
		}
		else{
			content=bInfo(949702100, sRandom);
		}
		
		return verifySupport.sendVerifyCode(
				VerifySupport.upVerifyTypeByEnumer(eVerifyCodeTypeEnumer),
				sMobile, 300, upSendSourceByManageCode(sMangeCode), sRandom,
				content);
	}
	/**
	 * 发送指定长度验证码 最长10位 以及超时时间
	 * 
	 * @param eVerifyCodeTypeEnumer
	 * @param sMobile
	 * @param sMangeCode
	 * @param iLength
	 * @param iActiveSecond 有效秒数
	 * @return
	 */
	public MWebResult sendVerifyCode(
			EVerifyCodeTypeEnumer eVerifyCodeTypeEnumer, String sMobile,
			String sMangeCode, int iLength,int iActiveSecond) {

		String sRandom = StringUtils.leftPad(
				String.valueOf(RandomUtils.nextInt((int)
						 Math.pow(10, iLength) - 1)), iLength, "0");

		VerifySupport verifySupport = new VerifySupport();

		String content=null;
		
		if(sMangeCode.equals("SI2011")&&(EVerifyCodeTypeEnumer.Binding==eVerifyCodeTypeEnumer)){
			//微公社短信模板特殊，在这里从新定义
			content=bInfo(949702108, sRandom);
		}else if(sMangeCode.equals("SI3003")){
			content=bInfo(949702109, sRandom);
		}else if(sMangeCode.equals("SI2003")&&(EVerifyCodeTypeEnumer.MemberReginster==eVerifyCodeTypeEnumer||EVerifyCodeTypeEnumer.ForgetPassword==eVerifyCodeTypeEnumer)){
			content=bInfo(949702110, sRandom);
		}
		else{
			content=bInfo(949702100, sRandom);
		}
		
		return verifySupport.sendVerifyCode(
				VerifySupport.upVerifyTypeByEnumer(eVerifyCodeTypeEnumer),
				sMobile, iActiveSecond, upSendSourceByManageCode(sMangeCode), sRandom,
				content);
	}
	/**
	 * 发送短信
	 * 
	 * @param sMobile
	 * @param sContent
	 * @return
	 */
	public MWebResult sendMessage(String sMobile, String sContent,
			String send_source) {
		MessageSupport messageSupport = new MessageSupport();
		MMessage message = new MMessage();

		message.setMessageContent(sContent);
		message.setMessageReceive(sMobile);
		if (send_source == null || "".equals(send_source)) { // 默认为家有渠道发送
			message.setSendSource("4497467200020001");
		} else {
			message.setSendSource(send_source);
		}

		messageSupport.sendMessage(message);

		return new MWebResult();
	}

}

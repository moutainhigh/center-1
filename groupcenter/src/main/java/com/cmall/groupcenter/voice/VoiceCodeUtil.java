package com.cmall.groupcenter.voice;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.http.ParseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONObject;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.support.VerifySupport;
import com.cmall.systemcenter.util.SystemCenterConst;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 语音验证码
 * @author panwei
 *
 */
public class VoiceCodeUtil extends BaseClass{
	
	
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

		String content=null;
		
		content=bInfo(949702116, sRandom);
		
		return sendVerifyCode(
				VerifySupport.upVerifyTypeByEnumer(eVerifyCodeTypeEnumer),
				sMobile, 300,sRandom,
				content);
	}
	
	/**
	 * 发送验证码
	 * 
	 * @param sVerifyType
	 *            验证类型
	 * @param sMobile
	 *            手机号码
	 * @param iActiveSecond
	 *            有效秒数
	 * @param sVerifyCode
	 *            验证码
	 * @param sMessageContent
	 *            语音内容
	 * @return
	 */
	public MWebResult sendVerifyCode(String sVerifyType, String sMobile,
			int iActiveSecond,String sVerifyCode,
			String sMessageContent) {

		MWebResult mWebResult = new MWebResult();
		if(sMobile.indexOf("_"+EVerifyCodeTypeEnumer.verifyCodeLogin)!=-1){
			mWebResult.setResultCode(1234);
		}
		if (mWebResult.upFlagTrue()) {
			//取出最近1小时的短信验证信息，如果大于5条 则返回错误 频率太高
			if((sVerifyType.equals(VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.voiceCodeForgetPassword)) || 
					sVerifyType.equals(VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.voiceCodeMemberReginster)))){
				String	verifySql ="SELECT zid FROM `sc_verify_code` where mobile_phone=:mobile " +
						"and verify_type=:verifyType and create_time >'"+DateHelper.upDateTimeAdd(SystemCenterConst.REGISTER_AND_FORGETPASSWORD_MIN_VERIFY_TIME_STEP)+"' and create_time<'"+DateHelper.upNow()+"'";
				MDataMap pmap = new MDataMap("mobile",sMobile,"verifyType",sVerifyType);
				List<Map<String, Object>> verifyCodeList = DbUp.upTable("sc_verify_code").dataSqlList(verifySql , pmap);
				if (verifyCodeList != null && verifyCodeList.size() > 5) {
				 	mWebResult.inErrorMessage(949702111);
				}
				
				if (mWebResult.upFlagTrue()){
					//每天只允许最多发送10条短信
					verifySql ="SELECT zid FROM `sc_verify_code` where mobile_phone=:mobile " +
						"and verify_type=:verifyType and create_time >'"+DateHelper.upDateTimeAdd(SystemCenterConst.WGS_DAY_VERIFY_TIME_STEP)+"' and create_time<'"+DateHelper.upNow()+"'";
					pmap = new MDataMap("mobile",sMobile,"verifyType",sVerifyType);
					verifyCodeList = DbUp.upTable("sc_verify_code").dataSqlList(verifySql , pmap);
					if (verifyCodeList != null && verifyCodeList.size() > 10) {
						mWebResult.inErrorMessage(949702114);
					}
				}
				
				if (mWebResult.upFlagTrue()){
					//微公社获取验证码，一分钟最多一次
					MDataMap mCheckMap = DbUp
							.upTable("sc_verify_code")
							.oneWhere(
									"zid",
									"",
									"mobile_phone=:mobile_phone and create_time>:min_time and verify_type=:verifyType ",
									"mobile_phone",sMobile,"verifyType",sVerifyType,
									"min_time",
									DateHelper
											.upDateTimeAdd(SystemCenterConst.WGS_MIN_VERIFY_TIME_STEP));
		
					if (mCheckMap != null && mCheckMap.size() > 0) {
						mWebResult.inErrorMessage(949702112);
					}				
				}
			}
			
			
		}
		
		if(mWebResult.getResultCode()==1234){
			mWebResult.setResultCode(1);
			sMobile = sMobile.replace("_"+EVerifyCodeTypeEnumer.verifyCodeLogin, "");
		}
		
		if(mWebResult.upFlagTrue()){
			VoiceSender voiceSender=new VoiceSender();
			String result=voiceSender.sendVoiceMessage(sMobile, sMessageContent);
			//9020:号码段错误;108:手机号码格式错误;109:手机号码个数错误
			if(!result.equals("0")&&result.equals("9020")&&result.equals("108")&&
					result.equals("109")){
				mWebResult.inErrorMessage(918580101);
			}
		}
		
		if (mWebResult.upFlagTrue()) {

			// 插入验证码表
			DbUp.upTable("sc_verify_code").insert(
					"verify_type",
					sVerifyType,
					"mobile_phone",
					sMobile,
					"create_time",
					FormatHelper.upDateTime(),
					"verify_code",
					sVerifyCode,
					"active_time",
					DateHelper.upDateTimeAdd(String.valueOf(iActiveSecond)
							+ "s"));
		}
		return mWebResult;

	}
	
	
}

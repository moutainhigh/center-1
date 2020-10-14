package com.cmall.systemcenter.support;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.RandomUtils;

import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.util.SystemCenterConst;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webclass.WarnCount;
import com.srnpr.zapweb.webmodel.MMessage;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.srnpr.zapweb.websupport.MessageSupport;

/**
 * 验证相关
 * 
 * @author srnpr
 * 
 */
public class VerifySupport extends BaseClass {

	/**
	 * 获取验证码编号根据类型
	 * 
	 * @param eVerifyCodeTypeEnumer
	 * @return
	 */
	public static String upVerifyTypeByEnumer(
			EVerifyCodeTypeEnumer eVerifyCodeTypeEnumer) {

		String sReturn = "";

		switch (eVerifyCodeTypeEnumer) {
		case MemberLogin:
			sReturn = "4497467200010001";
			break;
		case MemberReginster:
			sReturn = "4497467200010002";
			break;
		case ForgetPassword:
			sReturn = "4497467200010003";
			break;
		case UpdateMemInfor:
			sReturn = "4497467200010004";
			break;
		case Binding:
			sReturn = "4497467200010005";
			break;
		case WeiXinBind:
			sReturn = "4497467200010006";
			break;
		case verifyCodeLogin:
			sReturn = "4497467200010007";
			break;
		case agentPassWord:
			sReturn = "4497467200010008";
			break;	
		case huiCoinsWithdraw:
			sReturn = "4497467200010009";
			break;	
		case voiceCodeMemberReginster:
			sReturn = "4497467300010001";
			break;
		case voiceCodeForgetPassword:
			sReturn = "4497467300010002";
			break;	
		default:
			break;
		}

		return sReturn;

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
	 * @param sSendSource
	 *            发送通道
	 * @param sVerifyCode
	 *            验证码
	 * @param sMessageContent
	 *            短信内容
	 * @return
	 */
	public MWebResult sendVerifyCode(String sVerifyType, String sMobile,
			int iActiveSecond, String sSendSource, String sVerifyCode,
			String sMessageContent) {

		MWebResult mWebResult = new MWebResult();
		if(sMobile.indexOf("_"+EVerifyCodeTypeEnumer.verifyCodeLogin)!=-1){
			mWebResult.setResultCode(1234);
		}
		if (mWebResult.upFlagTrue()) {
			// (pc微公社找回密码、注册、短信验证码登陆)取出最近1小时的短信验证信息，如果大于10条 则返回错误 频率太高
			if((sVerifyType.equals(VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.ForgetPassword)) || sVerifyType.equals(VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.MemberReginster))) || sVerifyType.equals(VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.verifyCodeLogin))  && sSendSource.equals("4497467200020005")){
				String	verifySql ="SELECT zid FROM `sc_verify_code` where mobile_phone=:mobile " +
						"and verify_type=:verifyType and create_time >'"+DateHelper.upDateTimeAdd(SystemCenterConst.REGISTER_AND_FORGETPASSWORD_MIN_VERIFY_TIME_STEP)+"' and create_time<'"+DateHelper.upNow()+"'";
				MDataMap pmap = new MDataMap("mobile",sMobile,"verifyType",sVerifyType);
				List<Map<String, Object>> verifyCodeList = DbUp.upTable("sc_verify_code").dataSqlList(verifySql , pmap);
				if (verifyCodeList != null && verifyCodeList.size() > 10) {
				 	mWebResult.inErrorMessage(949702111);
				}
				
				if (mWebResult.upFlagTrue()){
					//每天只允许最多发送20条短信
					verifySql ="SELECT zid FROM `sc_verify_code` where mobile_phone=:mobile " +
						"and verify_type=:verifyType and create_time >'"+DateHelper.upDateTimeAdd(SystemCenterConst.WGS_DAY_VERIFY_TIME_STEP)+"' and create_time<'"+DateHelper.upNow()+"'";
					pmap = new MDataMap("mobile",sMobile,"verifyType",sVerifyType);
					verifyCodeList = DbUp.upTable("sc_verify_code").dataSqlList(verifySql , pmap);
					if (verifyCodeList != null && verifyCodeList.size() > 20) {
						mWebResult.inErrorMessage(949702114);
					}
				}
				
			}
			
			// 惠币提现验证码
			if(sVerifyType.equals(VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.huiCoinsWithdraw))) {
				// 验证当前用户一小时内不能超过4条，一天不能超过20条
				String	verifySql ="SELECT zid FROM `sc_verify_code` where mobile_phone=:mobile " +
						"and verify_type=:verifyType and create_time >'"+DateHelper.upDateTimeAdd(SystemCenterConst.REGISTER_AND_FORGETPASSWORD_MIN_VERIFY_TIME_STEP)+"' and create_time<'"+DateHelper.upNow()+"'";
				MDataMap pmap = new MDataMap("mobile",sMobile,"verifyType",sVerifyType);
				List<Map<String, Object>> verifyCodeList = DbUp.upTable("sc_verify_code").dataSqlList(verifySql , pmap);
				if (verifyCodeList != null && verifyCodeList.size() >= 4) {
				 	mWebResult.inErrorMessage(949702111);
				}
				
				if (mWebResult.upFlagTrue()){
					//每天只允许最多发送20条短信
					verifySql ="SELECT zid FROM `sc_verify_code` where mobile_phone=:mobile " +
						"and verify_type=:verifyType and create_time >'"+DateHelper.upDateTimeAdd(SystemCenterConst.WGS_DAY_VERIFY_TIME_STEP)+"' and create_time<'"+DateHelper.upNow()+"'";
					pmap = new MDataMap("mobile",sMobile,"verifyType",sVerifyType);
					verifyCodeList = DbUp.upTable("sc_verify_code").dataSqlList(verifySql , pmap);
					if (verifyCodeList != null && verifyCodeList.size() > 20) {
						mWebResult.inErrorMessage(949702114);
					}
				}
			}

			if (mWebResult.upFlagTrue()){
				//微公社获取验证码，一分钟最多一次
				if(sSendSource.equals("4497467200020005")){
					// 取出最近的一条1分钟内的数据 如果有这样的数据 则返回错误 频率太高
					MDataMap mCheckMap = DbUp
							.upTable("sc_verify_code")
							.oneWhere(
									"zid",
									"",
									"mobile_phone=:mobile_phone and create_time>:min_time ",
									"mobile_phone",
									sMobile,
									"min_time",
									DateHelper
											.upDateTimeAdd(SystemCenterConst.WGS_MIN_VERIFY_TIME_STEP));
		
					if (mCheckMap != null && mCheckMap.size() > 0) {
		
						mWebResult.inErrorMessage(949702112);
					}				
				}
			}

			if (mWebResult.upFlagTrue()){
				//微公社 短信验证码登陆 增加1小时最多5条，如果>5条增加图片验证码
				if(sVerifyType.equals(VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.verifyCodeLogin))  && sSendSource.equals("4497467200020005")){
					String	verifySql ="SELECT zid FROM `sc_verify_code` where mobile_phone=:mobile " +
							"and verify_type=:verifyType and create_time >'"+DateHelper.upDateTimeAdd(SystemCenterConst.REGISTER_AND_FORGETPASSWORD_MIN_VERIFY_TIME_STEP)+"' and create_time<'"+DateHelper.upNow()+"'";
					MDataMap pmap = new MDataMap("mobile",sMobile,"verifyType",sVerifyType);
					List<Map<String, Object>> verifyCodeList = DbUp.upTable("sc_verify_code").dataSqlList(verifySql , pmap);
					if (verifyCodeList != null && verifyCodeList.size() > 4) {// 第六条出现
						mWebResult.inErrorMessage(949702113);
					}
				}
			}
				
			if (mWebResult.upFlagTrue()) {
				MDataMap mCheckMap = null;
				if(sVerifyType.equals(VerifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.huiCoinsWithdraw))) {
					// 惠币提现短信验证码70s内只能获取一次
					mCheckMap = DbUp
							.upTable("sc_verify_code")
							.oneWhere(
									"zid",
									"",
									"verify_type=:verify_type and mobile_phone=:mobile_phone and create_time>:min_time ",
									"verify_type",
									sVerifyType,
									"mobile_phone",
									sMobile,
									"min_time",
									DateHelper
									.upDateTimeAdd(SystemCenterConst.HUI_COINS_MIN_VERIFY_TIME_STEP));
				}else {
					// 取出最近的一条30秒内的数据 如果有这样的数据 则返回错误 频率太高
					mCheckMap = DbUp
							.upTable("sc_verify_code")
							.oneWhere(
									"zid",
									"",
									"verify_type=:verify_type and mobile_phone=:mobile_phone and create_time>:min_time ",
									"verify_type",
									sVerifyType,
									"mobile_phone",
									sMobile,
									"min_time",
									DateHelper
									.upDateTimeAdd(SystemCenterConst.MIN_VERIFY_TIME_STEP));
				}
	
				if (mCheckMap != null && mCheckMap.size() > 0) {
	
					mWebResult.inErrorMessage(949702103);
				}
			}
		}
		
		if(mWebResult.getResultCode()==1234){
			mWebResult.setResultCode(1);
			sMobile = sMobile.replace("_"+EVerifyCodeTypeEnumer.verifyCodeLogin, "");
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

			MessageSupport messageSupport = new MessageSupport();
			MMessage message = new MMessage();

			message.setMessageContent(sMessageContent);
			message.setMessageReceive(sMobile);
			message.setSendSource(sSendSource);

			messageSupport.sendMessage(message);
		}
		return mWebResult;

	}

	/**
	 * 获取最新的验证码根据类型
	 * 
	 * @param eVerifyCodeTypeEnumer
	 * @param sMobile
	 * @return
	 */
	public String upLastVerifyCode(EVerifyCodeTypeEnumer eVerifyCodeTypeEnumer,
			String sMobile) {

		String sReturnString = "";
		MDataMap mDataMap = DbUp.upTable("sc_verify_code").oneWhere(
				"verify_code,zid,verify_sum",
				"-create_time",
				"verify_type=:verify_type and mobile_phone=:mobile_phone and verify_sum<"
						+ SystemCenterConst.MAX_VERIFY_CODE_SUM
						+ " and flag_verify=0 and active_time>:current_time",
				"verify_type", upVerifyTypeByEnumer(eVerifyCodeTypeEnumer),
				"mobile_phone", sMobile, "current_time",
				FormatHelper.upDateTime());

		if (mDataMap != null && mDataMap.size() > 0) {
			sReturnString = mDataMap.get("verify_code");
		}

		return sReturnString;

	}

	/**
	 * 检查验证码根据类型
	 * 
	 * @param eVerifyCodeTypeEnumer
	 * @param sMobile
	 * @param sVerifyCode
	 * @return
	 */
	public MWebResult checkVerifyCodeByType(
			EVerifyCodeTypeEnumer eVerifyCodeTypeEnumer, String sMobile,
			String sVerifyCode) {

		MWebResult mWebResult = new MWebResult();

		if (mWebResult.upFlagTrue()) {

			MDataMap mDataMap = DbUp
					.upTable("sc_verify_code")
					.oneWhere(
							"verify_code,zid,verify_sum",
							"-create_time",
							"verify_type=:verify_type and mobile_phone=:mobile_phone"
									+ " and flag_verify=0 and active_time>:current_time",
							"verify_type",
							upVerifyTypeByEnumer(eVerifyCodeTypeEnumer),
							"mobile_phone", sMobile, "current_time",
							FormatHelper.upDateTime());

			// 判断如果有
			if (mDataMap != null && mDataMap.size() > 0) {

				if(Integer.parseInt((String)mDataMap.get("verify_sum")) >= Integer.parseInt(SystemCenterConst.MAX_VERIFY_CODE_SUM)){
					mWebResult.inErrorMessage(949702107);
				}
				if(mWebResult.upFlagTrue()){
					// 判断如果编号一致
					if (sVerifyCode.equals(mDataMap.get("verify_code"))) {
						mDataMap.put("flag_verify", "1");
						mDataMap.put("verify_time", FormatHelper.upDateTime());
						DbUp.upTable("sc_verify_code").dataUpdate(mDataMap,
								"verify_time,flag_verify", "zid");

					} else {

						// 如果验证码不正确 则将验证次数增加1 以防止暴力验证
						mDataMap.put("verify_sum", String.valueOf(Integer
								.valueOf(mDataMap.get("verify_sum")) + 1));

						DbUp.upTable("sc_verify_code").dataUpdate(mDataMap,
								"verify_sum", "zid");

						mWebResult.inErrorMessage(949702102);
					}
				}

			} else {
				mWebResult.inErrorMessage(949702101);
			}
		}

		return mWebResult;

	}
	
	/**
	 * 检查验证码根据类型(只增加验证次数，不修改验证码记录状态)
	 * 
	 * @param eVerifyCodeTypeEnumer
	 * @param sMobile
	 * @param sVerifyCode
	 * @return
	 */
	public MWebResult checkVerifyCodeByType(
			EVerifyCodeTypeEnumer eVerifyCodeTypeEnumer, String sMobile,
			String sVerifyCode,String clientSource) {

		MWebResult mWebResult = new MWebResult();

		if (mWebResult.upFlagTrue()) {

			MDataMap mDataMap = DbUp
					.upTable("sc_verify_code")
					.oneWhere(
							"verify_code,zid,verify_sum",
							"-create_time",
							"verify_type=:verify_type and mobile_phone=:mobile_phone"
									+ " and flag_verify=0 and active_time>:current_time",
							"verify_type",
							upVerifyTypeByEnumer(eVerifyCodeTypeEnumer),
							"mobile_phone", sMobile, "current_time",
							FormatHelper.upDateTime());

			// 判断如果有
			if (mDataMap != null && mDataMap.size() > 0) {

				if(Integer.parseInt((String)mDataMap.get("verify_sum")) >= Integer.parseInt(SystemCenterConst.MAX_VERIFY_CODE_SUM)){
					mWebResult.inErrorMessage(949702107);
				}
				if(mWebResult.upFlagTrue()){
					// 判断如果编号一致
					if (sVerifyCode.equals(mDataMap.get("verify_code"))) {
						if("site".equals(clientSource)){
							mDataMap.put("verify_sum", String.valueOf(Integer
									.valueOf(mDataMap.get("verify_sum")) + 1));

							DbUp.upTable("sc_verify_code").dataUpdate(mDataMap,
									"verify_sum", "zid");
						}else{
							mDataMap.put("flag_verify", "1");
							mDataMap.put("verify_time", FormatHelper.upDateTime());
							DbUp.upTable("sc_verify_code").dataUpdate(mDataMap,
									"verify_time,flag_verify", "zid");

						}
					} else {
						// 如果验证码不正确 则将验证次数增加1 以防止暴力验证
						mDataMap.put("verify_sum", String.valueOf(Integer
								.valueOf(mDataMap.get("verify_sum")) + 1));

						DbUp.upTable("sc_verify_code").dataUpdate(mDataMap,
								"verify_sum", "zid");

						mWebResult.inErrorMessage(949702102);
					}
				}

			} else {
				mWebResult.inErrorMessage(949702101);
			}
		}

		return mWebResult;

	}
	
	/**
	 * 检查验证码根据类型(多个类型)
	 * 
	 * @param eVerifyCodeTypeEnumer
	 * @param sMobile
	 * @param sVerifyCode
	 * @return
	 */
	public MWebResult checkVerifyCodeByMoreType(
			String eVerifyCodeTypeEnumer, String sMobile,
			String sVerifyCode) {

		MWebResult mWebResult = new MWebResult();

		if (mWebResult.upFlagTrue()) {

			MDataMap mDataMap = DbUp
					.upTable("sc_verify_code")
					.oneWhere(
							"verify_code,zid,verify_sum",
							"-create_time",
							"verify_type IN("+eVerifyCodeTypeEnumer+") and mobile_phone=:mobile_phone"
									+ " and flag_verify=0 and active_time>:current_time",
							"verify_type",
							eVerifyCodeTypeEnumer,
							"mobile_phone", sMobile, "current_time",
							FormatHelper.upDateTime());

			// 判断如果有
			if (mDataMap != null && mDataMap.size() > 0) {

				if(Integer.parseInt((String)mDataMap.get("verify_sum")) >= Integer.parseInt(SystemCenterConst.MAX_VERIFY_CODE_SUM)){
					mWebResult.inErrorMessage(949702107);
				}
				if(mWebResult.upFlagTrue()){
					// 判断如果编号一致
					if (sVerifyCode.equals(mDataMap.get("verify_code"))) {
						mDataMap.put("flag_verify", "1");
						mDataMap.put("verify_time", FormatHelper.upDateTime());
						DbUp.upTable("sc_verify_code").dataUpdate(mDataMap,
								"verify_time,flag_verify", "zid");

					} else {

						// 如果验证码不正确 则将验证次数增加1 以防止暴力验证
						mDataMap.put("verify_sum", String.valueOf(Integer
								.valueOf(mDataMap.get("verify_sum")) + 1));

						DbUp.upTable("sc_verify_code").dataUpdate(mDataMap,
								"verify_sum", "zid");

						mWebResult.inErrorMessage(949702102);
					}
				}

			} else {
				mWebResult.inErrorMessage(949702101);
			}
		}

		return mWebResult;

	}
	
	public MWebResult sendVerifyCodeForWeChat(String sVerifyType, String openid,
			int iActiveSecond, int iLength) {
		
		MWebResult mWebResult = new MWebResult();
		
		String sVerifyCode = StringUtils.leftPad(
				String.valueOf(RandomUtils.nextInt((int)
						Math.pow(10, iLength) - 1)), iLength, "0");
		
		if (mWebResult.upFlagTrue()) {
				// 取出最近的一条30秒内的数据 如果有这样的数据 则返回错误 频率太高
				MDataMap mCheckMap = DbUp
						.upTable("sc_verify_code")
						.oneWhere(
								"zid",
								"",
								"verify_type=:verify_type and wechat_openid=:wechat_openid and create_time>:min_time ",
								"verify_type",
								sVerifyType,
								"wechat_openid",
								openid,
								"min_time",
								DateHelper
										.upDateTimeAdd(SystemCenterConst.MIN_VERIFY_TIME_STEP));
	
				if (mCheckMap != null && mCheckMap.size() > 0) {
	
					mWebResult.inErrorMessage(949702103);
				}
		}
		
		if (mWebResult.upFlagTrue()) {

			// 插入验证码表
			DbUp.upTable("sc_verify_code").insert(
					"verify_type",
					sVerifyType,
					"wechat_openid",
					openid,
					"create_time",
					FormatHelper.upDateTime(),
					"verify_code",
					sVerifyCode,
					"active_time",
					DateHelper.upDateTimeAdd(String.valueOf(iActiveSecond)
							+ "s"));

			new WarnCount().sendWxVerifyCode(openid, bInfo(949702100, sVerifyCode));
		}
		return mWebResult;

	}
	
	/**
	 * 检查验证码根据类型(多个类型)(验证微信通道)
	 * 
	 * @param eVerifyCodeTypeEnumer
	 * @param wechatOpenid
	 * @param sVerifyCode
	 * @return
	 */
	public MWebResult checkVerifyCodeByMoreTypeForWeChat(
			String eVerifyCodeTypeEnumer, String wechatOpenid,
			String sVerifyCode) {

		MWebResult mWebResult = new MWebResult();

		if (mWebResult.upFlagTrue()) {

			MDataMap mDataMap = DbUp
					.upTable("sc_verify_code")
					.oneWhere(
							"verify_code,zid,verify_sum",
							"-create_time",
							"verify_type IN("+eVerifyCodeTypeEnumer+") and wechat_openid=:wechat_openid"
									+ " and flag_verify=0 and active_time>:current_time",
							"verify_type",
							eVerifyCodeTypeEnumer,
							"wechat_openid", wechatOpenid, "current_time",
							FormatHelper.upDateTime());

			// 判断如果有
			if (mDataMap != null && mDataMap.size() > 0) {

				if(Integer.parseInt((String)mDataMap.get("verify_sum")) >= Integer.parseInt(SystemCenterConst.MAX_VERIFY_CODE_SUM)){
					mWebResult.inErrorMessage(949702107);
				}
				if(mWebResult.upFlagTrue()){
					// 判断如果编号一致
					if (sVerifyCode.equals(mDataMap.get("verify_code"))) {
						mDataMap.put("flag_verify", "1");
						mDataMap.put("verify_time", FormatHelper.upDateTime());
						DbUp.upTable("sc_verify_code").dataUpdate(mDataMap,
								"verify_time,flag_verify", "zid");

					} else {

						// 如果验证码不正确 则将验证次数增加1 以防止暴力验证
						mDataMap.put("verify_sum", String.valueOf(Integer
								.valueOf(mDataMap.get("verify_sum")) + 1));

						DbUp.upTable("sc_verify_code").dataUpdate(mDataMap,
								"verify_sum", "zid");

						mWebResult.inErrorMessage(949702102);
					}
				}

			} else {
				mWebResult.inErrorMessage(949702101);
			}
		}

		return mWebResult;

	}
}

package com.cmall.groupcenter.userinfo.api;


import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.support.GroupAccountSupport;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.UserRegisterForGroupInput;
import com.cmall.membercenter.model.UserRegisterForGroupResult;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.membercenter.txservice.TxMemberForGroupService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.common.CouponConst;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.cmall.systemcenter.service.StartPageService;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 微工社注册
 * 
 * @author chenxk
 *
 */
public class UserRegisterForHjyApi extends
	RootApiForManage<UserRegisterForGroupResult, UserRegisterForGroupInput> {

	public UserRegisterForGroupResult Process(
			UserRegisterForGroupInput inputParam, MDataMap mRequestMap) {
		
		UserRegisterForGroupResult groupRegisterResult = new UserRegisterForGroupResult();
		
		String referrerMobile = inputParam.getReferrerMobile().trim();
		Map<?, ?> loginInfoMap = null;
		//判断是否有绑定上下级需求
		if(!StringUtils.isEmpty(referrerMobile)){
			loginInfoMap = DbUp.upTable("mc_login_info").one("login_name",referrerMobile);
			if(null == loginInfoMap){
				groupRegisterResult.setResultCode(0);
				groupRegisterResult.setResultMessage("推荐人不存在");
			}
		}
		if (groupRegisterResult.upFlagTrue()){
			groupRegisterResult =doGroupRegister(inputParam, getManageCode());
		}
		//有绑定需求的，开始绑定上下级
		if (groupRegisterResult.upFlagTrue() && !StringUtils.isEmpty(referrerMobile)) {
			 String memberCode=groupRegisterResult.getMemberCode();
			 String parentMemberCode=DbUp.upTable("mc_login_info").one("login_name",referrerMobile).get("member_code");
			 String accountCode=DbUp.upTable("mc_member_info").one("member_code",memberCode).get("account_code");
			 String parentAccountCode=DbUp.upTable("mc_member_info").one("member_code",parentMemberCode).get("account_code");
			 
			 GroupAccountSupport groupAccountSupport=new GroupAccountSupport();
			 groupAccountSupport.createRelation(accountCode,parentAccountCode, "",FormatHelper.upDateTime());
		} else {
			//判断是否有有效的推荐记录
			MDataMap isHasRcd = DbUp.upTable("gc_recommend_info").one("is_usable_send_link","1","flag_establishment_rel","0","recommended_mobile",inputParam.getMobile());
			if(null != isHasRcd) {
				groupRegisterResult.setFlagRelation(0);
			}
			
		}
		
		if(groupRegisterResult.upFlagTrue()&&(AppConst.MANAGE_CODE_HOMEHAS.equals(getManageCode()) || AppConst.MANAGE_CODE_CDOG.equals(getManageCode()))){//惠家有或沙皮狗注册送优惠券
			JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnDistributeCoupon,
					CouponConst.register_coupon,new MDataMap("mobile", inputParam.getMobile(),"manage_code", getManageCode()));
		}
		//发送欢迎消息
		if (groupRegisterResult.upFlagTrue() && "SI2011".equals(getManageCode())) {
			new MemberLoginSupport().insertWeblcomeMsg(groupRegisterResult.getMemberCode(),getManageCode());
		}
		return groupRegisterResult;
	}
	
	/**
	 * 微公社用户注册
	 * 
	 * @param groupRegisterInput
	 * @param sManageCode
	 * @return
	 */
	public UserRegisterForGroupResult doGroupRegister(UserRegisterForGroupInput groupRegisterInput,
			String sManageCode) {

		UserRegisterForGroupResult groupRegisterResult = new UserRegisterForGroupResult();

		String mobile = groupRegisterInput.getMobile(); // 手机号
		
		// 判断验证码对不对
		if (groupRegisterResult.upFlagTrue()) {
			VerifySupport verifySupport = new VerifySupport();
			String verifyType=verifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.MemberReginster)+","+
					verifySupport.upVerifyTypeByEnumer(EVerifyCodeTypeEnumer.voiceCodeMemberReginster);
			groupRegisterResult.inOtherResult(verifySupport.checkVerifyCodeByMoreType(
					verifyType, mobile,groupRegisterInput.getVerifyCode())
			);
		}
		// 开始调用注册流程
		if (groupRegisterResult.upFlagTrue()) {
			TxMemberForGroupService memberService = BeansHelper
					.upBean("bean_com_cmall_membercenter_txservice_TxMemberForGroupService");
			
			MLoginInput mLoginInput = new MLoginInput();

			mLoginInput.setLoginName(mobile);
			mLoginInput.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
			mLoginInput.setLoginPassword(groupRegisterInput.getPassword());
			mLoginInput.setSerialNumber(groupRegisterInput.getSerialNumber());
			mLoginInput.setManageCode(sManageCode);
			
			groupRegisterResult = memberService.createMemberInfoForGroup(mLoginInput);
			// 设置相关信息
			if (groupRegisterResult.upFlagTrue()) {
				// 判断是否可绑定上线 如果关系表中有则不可绑定
				groupRegisterResult
						.setFlagRelation(Integer
								.parseInt(DbUp
										.upTable("gc_member_relation")
										.dataGet(
												" count(1) ",
												"account_code=:account_code or parent_code=:account_code",
												new MDataMap("account_code",
														groupRegisterResult.getAccountCode())).toString()) > 0 ? 0
								: 1);
			}
		}
		//绑定用户流水号
		if (groupRegisterResult.upFlagTrue()) {
			new StartPageService().updateLsh(groupRegisterInput.getSerialNumber(), groupRegisterResult.getMemberCode());
		}
		
		// 开始返回用户的登录信息
		if (groupRegisterResult.upFlagTrue()) {
			String sAuthCode = new MemberLoginSupport().memberLogin(groupRegisterResult.getMemberCode(), sManageCode, groupRegisterResult.getLoginName());

			if (StringUtils.isNotEmpty(sAuthCode)) {
				groupRegisterResult.setUserToken(sAuthCode);
			} else {
				groupRegisterResult.inErrorMessage(934105102);
			}
		}
		return groupRegisterResult;

	}

	
}

package com.cmall.membercenter.support;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.RecommendUserRegisterInput;
import com.cmall.membercenter.model.RecommendUserRegisterResult;
import com.cmall.membercenter.model.UserRegisterForGroupInput;
import com.cmall.membercenter.model.UserRegisterForGroupResult;
import com.cmall.membercenter.txservice.TxMemberForGroupService;
import com.cmall.systemcenter.enumer.EVerifyCodeTypeEnumer;
import com.cmall.systemcenter.service.StartPageService;
import com.cmall.systemcenter.support.VerifySupport;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class MemberRegisterSupport extends BaseClass {
	
	/** 
	* @Description:惠家有推荐用户注册
	* @param input 输入参数
	* @param sManageCode
	* @author 张海生
	* @date 2015-8-13 下午5:06:14
	* @return UserRegisterForGroupResult 
	* @throws 
	*/
	public RecommendUserRegisterResult doHomHasRegister(RecommendUserRegisterInput input,
			String sManageCode) {
		RecommendUserRegisterResult result = new RecommendUserRegisterResult();
		UserRegisterForGroupInput groupRegisterInput = new UserRegisterForGroupInput();
		groupRegisterInput.setMobile(input.getMobile());
		groupRegisterInput.setPassword(input.getPassword());
		groupRegisterInput.setReferrerMobile(input.getReferrerMobile());
		groupRegisterInput.setSerialNumber(input.getSerialNumber());
		groupRegisterInput.setVerifyCode(input.getVerifyCode());
		UserRegisterForGroupResult groupResult = this.doGroupRegister(groupRegisterInput, sManageCode);
		result.setAccountCode(groupResult.getAccountCode());
		result.setFlagRelation(groupResult.getFlagRelation());
		result.setIsNoPassword(groupResult.getIsNoPassword());
		result.setLoginName(groupResult.getLoginName());
		result.setMemberCode(groupResult.getMemberCode());
		result.setResultCode(groupResult.getResultCode());
		result.setResultMessage(groupResult.getResultMessage());
		result.setSerialNumber(groupResult.getSerialNumber());
		result.setUserToken(groupResult.getUserToken());
		return result;
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
			groupRegisterResult.inOtherResult(verifySupport.checkVerifyCodeByType(
					EVerifyCodeTypeEnumer.MemberReginster, mobile,
					groupRegisterInput.getVerifyCode())
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
	
	public RootResultWeb checkLoginName(String loginName) {
		RootResultWeb result = new RootResultWeb();
		int count = DbUp.upTable("mc_login_info").dataCount(null,
				new MDataMap("login_name", loginName));
		if(count > 0){
			result.inErrorMessage(934105104);//登录名已经存在
		}
		return result;
	}
	
}

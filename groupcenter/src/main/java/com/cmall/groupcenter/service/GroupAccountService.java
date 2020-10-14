package com.cmall.groupcenter.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.third.model.GroupAccountInfoInput;
import com.cmall.groupcenter.third.model.GroupAccountInfoResult;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.websupport.ApiCallSupport;

public class GroupAccountService extends BaseClass{

	/**
	 * 通过memberCode获取accountCode
	 * @param memberCode
	 * @return
	 */
	public String getAccountCodeByMemberCode(String memberCode){
		String accountCode=null;
		MDataMap memberMap=DbUp.upTable("mc_member_info").one("member_code",memberCode,"flag_enable","1");
		if(memberMap!=null){
			accountCode=memberMap.get("account_code");
		}
		return accountCode;
	}
	
	/**
	 * 账户信息
	 * @param accountCode
	 * @return
	 */
	public GroupAccountInfoResult getAccountInfo(String accountCode){
		GroupAccountInfoResult groupAccountInfoResult=new GroupAccountInfoResult();
		MDataMap accountMap=DbUp.upTable("gc_group_account").one("account_code",accountCode);
		if(accountMap!=null){
			groupAccountInfoResult.setWithdrawMoney(accountMap.get("account_withdraw_money"));
			groupAccountInfoResult.setRebateMoney(accountMap.get("account_rebate_money"));
			groupAccountInfoResult.setRelationLevel(accountMap.get("account_level"));
		}
		else{
			groupAccountInfoResult.setWithdrawMoney("0");
			groupAccountInfoResult.setRebateMoney("0");
			groupAccountInfoResult.setRelationLevel(GroupConst.DEFAULT_LEVEL_CODE);
		}
		GroupCommonService groupCommonService=new GroupCommonService();
		String mobileNo=groupCommonService.getMobileByAccountCode(accountCode);
		if(StringUtils.isNotBlank(mobileNo)){
			if(DbUp.upTable("gc_account_blacklist").count("mobile_no",mobileNo)>0){
				groupAccountInfoResult.setFlagEnable("0");//黑名单，不可用
			}
		}
		return groupAccountInfoResult;
	}
	
	/**
	 * 通过api接口查看账户信息
	 * @param accountCode
	 * @return
	 */
	public GroupAccountInfoResult getAccountInfoByApi(String memberCode){
		
		GroupAccountInfoInput accountInfoInput = new GroupAccountInfoInput();
		accountInfoInput.setMemberCode(memberCode);
		accountInfoInput.setReckonOrderCode("");
		ApiCallSupport<GroupAccountInfoInput, GroupAccountInfoResult> apiCallSupport = new ApiCallSupport<GroupAccountInfoInput, GroupAccountInfoResult>();
		
		GroupAccountInfoResult accountInfoResult = new GroupAccountInfoResult();
		
		try {
			accountInfoResult = apiCallSupport.doCallApi(
					bConfig("xmassystem.group_pay_url"),
					bConfig("xmassystem.group_pay_accountInfo_face"),
					bConfig("xmassystem.group_pay_key"),
					bConfig("xmassystem.group_pay_pass"), accountInfoInput,
					accountInfoResult);
		} catch (Exception e) {
			
			accountInfoResult.setWithdrawMoney("0.00");
			accountInfoResult.setRebateMoney("0.00");
			e.printStackTrace();
		}
		
		if(!accountInfoResult.upFlagTrue()){
			accountInfoResult.setWithdrawMoney("0.00");
			accountInfoResult.setRebateMoney("0.00");
		}
		
		return accountInfoResult;
	}
	
}

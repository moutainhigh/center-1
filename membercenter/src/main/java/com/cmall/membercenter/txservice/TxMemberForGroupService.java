package com.cmall.membercenter.txservice;

import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.MReginsterResult;
import com.cmall.membercenter.model.UserRegisterForGroupInput;
import com.cmall.membercenter.model.UserRegisterForGroupResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 微工社注册
 * 
 * @author chenxk
 * 
 */
public class TxMemberForGroupService extends TxMemberBase {

	/**
	 * @param mLoginInput
	 * @return
	 */
	public UserRegisterForGroupResult createMemberInfoForGroup(MLoginInput mLoginInput) {
		UserRegisterForGroupResult mReginsterResult = new UserRegisterForGroupResult();
		
		if (mReginsterResult.upFlagTrue()) {

			MDataMap mLoginMap = DbUp.upTable("mc_login_info").one("login_name",mLoginInput.getLoginName());
			if (null != mLoginMap) {
				mReginsterResult.inErrorMessage(934105104);
				mReginsterResult.setIsNoPassword(StringUtils.isBlank(mLoginMap.get("login_pass").trim()) ? "1" : "0");
			}
		}
		if (mReginsterResult.upFlagTrue()) {
			MReginsterResult result = doUserReginster(mLoginInput);
			if(result.upFlagTrue()){
				mReginsterResult.setMemberCode(result.getMemberInfo().getMemberCode());
				mReginsterResult.setAccountCode(result.getMemberInfo().getAccountCode());
				mReginsterResult.setLoginName(mLoginInput.getLoginName());
			}else{
				mReginsterResult.setResultCode(result.getResultCode());
				mReginsterResult.setResultMessage(result.getResultMessage());
			}
		}
		
		return mReginsterResult;
		
	}

}

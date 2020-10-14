package com.cmall.membercenter.support;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 登陆信息支持类
 * 
 * @author srnpr
 *
 */
public class LoginInfoSupport extends BaseClass {

	/**
	 * 根据用户编号返回账户编号
	 * 
	 * @param sMemberCode
	 * @return
	 */
	public String upAccountCodeByMemberCode(String sMemberCode) {

		return DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", sMemberCode)
				.get("account_code");

	}

}

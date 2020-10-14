package com.cmall.membercenter.txservice;

import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.membercenter.model.MLoginInput;
import com.cmall.membercenter.model.MReginsterResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * @ClassName: TxMemberForWx
 * @Description: 来自微信商城用户管理
 * @author 张海生
 * @date 2015-4-2 下午1:55:58
 * 
 */
public class TxMemberForWx extends TxMemberBase {

	/**
	 * @Description:用户注册
	 * @param input
	 *            注册实体
	 * @author 张海生
	 * @date 2015-4-2 下午2:26:41
	 * @return RootResultWeb
	 * @throws
	 */
	public MReginsterResult insertUserReg(MLoginInput input) {

		MReginsterResult userRegResult = new MReginsterResult();
		// 通行证账号
		input.setLoginGroup(MemberConst.LOGIN_GROUP_DEFAULT);
		userRegResult = doUserReginster(input);
		return userRegResult;
	}

	/**
	 * @Description: 更新密码
	 * @param sMemberCode
	 *            用户编号
	 * @param newPassWord
	 *            新密码
	 * @author 张海生
	 * @date 2015-4-2 下午2:37:25
	 * @return RootResultWeb
	 * @throws
	 */
	public RootResultWeb changePassword(String sMemberCode, String newPassWord) {

		RootResultWeb rootResultWeb = new RootResultWeb();

		if (rootResultWeb.upFlagTrue()) {

			// 判断如果该账户能用该密码登陆 则修改密码
			MDataMap mUpdateMap = new MDataMap();

			mUpdateMap.put("login_pass", newPassWord);
			mUpdateMap.put("member_code", sMemberCode);
			// 根据用户code更新密码
			int count = DbUp.upTable("mc_login_info").dataUpdate(mUpdateMap,
					"login_pass", "member_code");
			if (count <= 0) {
				rootResultWeb.inErrorMessage(934105144);// 更新密码失败
				return rootResultWeb;
			}
		}

		return rootResultWeb;
	}

}

package com.cmall.newscenter.api;

import com.cmall.membercenter.model.MemberInfo;
import com.cmall.membercenter.model.ScoredChange;
import com.cmall.newscenter.model.UserChangeMemberInfoInput;
import com.cmall.newscenter.model.UserChangeMemberInfoResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 用户 - 修改资料
 * 
 * @author liqiang date 2014-7-23
 * @version 1.0
 */
public class UserChangeMemberInfoApi extends
		RootApiForManage<UserChangeMemberInfoResult, UserChangeMemberInfoInput> {

	public UserChangeMemberInfoResult Process(
			UserChangeMemberInfoInput inputParam, MDataMap mRequestMap) {
		UserChangeMemberInfoResult result = new UserChangeMemberInfoResult();
		if (result.upFlagTrue()) {
			ScoredChange scored = new ScoredChange();
			MemberInfo user = new MemberInfo();

			scored.setLevel_name("银牌会员");
			scored.setScore_unit("积分单位");
			scored.setScore(2000);

			user.setNickname("日日");
			user.setCreate_time("2009/07/07 21:51:22");
		}
		return result;
	}

}

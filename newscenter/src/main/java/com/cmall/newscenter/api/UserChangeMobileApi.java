package com.cmall.newscenter.api;

import com.cmall.membercenter.model.MemberInfo;
import com.cmall.membercenter.model.ScoredChange;
import com.cmall.newscenter.model.UserChangeMobileInput;
import com.cmall.newscenter.model.UserChangeMobileResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 用户 - 修改手机号
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserChangeMobileApi extends RootApiForManage<UserChangeMobileResult, UserChangeMobileInput> {

	public UserChangeMobileResult Process(UserChangeMobileInput inputParam,
			MDataMap mRequestMap) {
		UserChangeMobileResult result = new UserChangeMobileResult();
		if(result.upFlagTrue()){
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

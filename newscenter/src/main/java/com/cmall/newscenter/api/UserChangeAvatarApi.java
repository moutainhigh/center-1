package com.cmall.newscenter.api;

import java.util.Map;

import com.cmall.membercenter.memberdo.ScoredEnumer;
import com.cmall.membercenter.model.ScoredChange;
import com.cmall.membercenter.support.MemberInfoSupport;
import com.cmall.membercenter.support.ScoredSupport;
import com.cmall.newscenter.model.UserChangeAvatarInput;
import com.cmall.newscenter.model.UserChangeAvatarResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 用户 - 修改头像
 * @author shiyz
 * date 2014-8-5
 * @version 1.0
 */
public class UserChangeAvatarApi extends RootApiForToken<UserChangeAvatarResult, UserChangeAvatarInput> {

	public UserChangeAvatarResult Process(UserChangeAvatarInput inputParam,
			MDataMap mRequestMap) {
		
		UserChangeAvatarResult result = new UserChangeAvatarResult();
		
		MemberInfoSupport memberInfoSupport=new MemberInfoSupport();
		
		if(result.upFlagTrue()){
			
		MDataMap mDataMap =	DbUp.upTable("mc_extend_info_star").one("member_code",getUserCode());
			
		if(mDataMap!=null){
			
			/*获取用户头像*/
			mDataMap.put("member_avatar", inputParam.getAvatar());
			
			DbUp.upTable("mc_extend_info_star").dataUpdate(mDataMap, "member_avatar", "zid");
									
															
			result.setUser(memberInfoSupport.upMemberInfo(getUserCode()));
			
			result.setScored(new ScoredSupport().ChangeScored(getUserCode(), ScoredEnumer.MemberChangeInfo));
			
		}
		
		}
		
		return result;
	}

}

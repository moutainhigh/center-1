package com.cmall.groupcenter.userinfo.api;

import com.cmall.groupcenter.userinfo.model.WxBindInput;
import com.cmall.groupcenter.userinfo.model.WxBindResult;
import com.cmall.groupcenter.util.DataQueryUtil;
import com.cmall.groupcenter.weixin.WebchatConstants;
import com.cmall.membercenter.support.MemberLoginSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

public class WxIFBindApi extends RootApiForManage<WxBindResult, WxBindInput>{

	public WxBindResult Process(WxBindInput inputParam, MDataMap mRequestMap) {
		
		
		MDataMap bindInfo = DataQueryUtil.getBindInfoByOpenId(inputParam.getOpenId());
		
		WxBindResult bind=new WxBindResult();
		
		if(bindInfo!=null&&bindInfo.get("member_code")!=null&&!bindInfo.get("member_code").trim().isEmpty()){
			MDataMap userInfoMap=DbUp.upTable("mc_login_info").one("member_code",bindInfo.get("member_code"),"manage_code",WebchatConstants.CGROUP_MANAGE_CODE);
			String sAuthCode = new MemberLoginSupport().memberLogin(bindInfo.get("member_code"), getManageCode(), userInfoMap.get("login_name"));
			bind.setMemberCode(bindInfo.get("member_code"));
			bind.setResultCode(1);
			bind.setUserToken(sAuthCode);
			bind.setLoginName(userInfoMap.get("login_name"));
		}else{
			bind.setResultCode(918506015);
			bind.setResultMessage(bInfo(918506015));
		}
		return bind;
	}
 
	
}

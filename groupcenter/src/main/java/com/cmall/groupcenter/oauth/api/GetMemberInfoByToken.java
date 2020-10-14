package com.cmall.groupcenter.oauth.api;


import java.util.Map;

import com.cmall.groupcenter.oauth.model.GetMemberInfoByTokenInput;
import com.cmall.groupcenter.oauth.model.GetMemberInfoByTokenResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webmodel.MOauthInfo;
import com.srnpr.zapweb.websupport.OauthSupport;

public class GetMemberInfoByToken extends
		RootApiForManage<GetMemberInfoByTokenResult, GetMemberInfoByTokenInput> {

	public GetMemberInfoByTokenResult Process(
			GetMemberInfoByTokenInput inputParam, MDataMap mRequestMap) {

		GetMemberInfoByTokenResult result = new GetMemberInfoByTokenResult();
		MOauthInfo mOauthInfo=new MOauthInfo();
		if (result.upFlagTrue()) {

			OauthSupport oauthSupport = new OauthSupport();

			mOauthInfo = oauthSupport.upOauthInfo(inputParam
					.getAccessToken());

			if (mOauthInfo != null) {

				result.setMemberCode(mOauthInfo.getUserCode());

				result.setLoginName(mOauthInfo.getLoginName());

			} else {
				result.inErrorMessage(969905917);
			}

		}

		if (result.upFlagTrue()) {
			String sAccountCode = DbUp
					.upTable("mc_member_info")
					.oneWhere("account_code", "", "", "member_code",
							result.getMemberCode()).get("account_code");

			// 判断是否可绑定上线 如果关系表中有则不可绑定
			result.setFlagRelation(Integer
					.parseInt(DbUp
							.upTable("gc_member_relation")
							.dataGet(
									" count(1) ",
									"account_code=:account_code or parent_code=:account_code",
									new MDataMap("account_code", sAccountCode))
							.toString()) > 0 ? 0 : 1);
			//获取上线对应用户编号
			if(result.getFlagRelation()==0){
				MDataMap relationMap=DbUp.upTable("gc_member_relation").one("account_code",sAccountCode,"flag_enable","1");
				if(relationMap!=null){
					MDataMap memberMap=DbUp.upTable("mc_member_info").one("account_code",relationMap.get("parent_code"),"manage_code",mOauthInfo.getManageCode(),"flag_enable","1");
					if(memberMap!=null){
						result.setParentMemberCode(memberMap.get("member_code"));
					}
				}
			}
			
			//用户等级如果获取不到默认最低
			MDataMap accountMap = DbUp.upTable("gc_group_account").one("account_code",sAccountCode);
			if(accountMap!=null){
				MDataMap levelMap=DbUp.upTable("gc_group_level").one("level_code",accountMap.get("account_level"));
				result.setLevel_name(levelMap.get("level_name"));
			}else{
				String sql="select level_name from gc_group_level where flag_enable=1 order by zid limit 0,1";
				Map<String,Object> levelMap=DbUp.upTable("gc_group_level").dataSqlOne(sql,null);
				result.setLevel_name(levelMap.get("level_name").toString());
			}
			
		}
		
		
		return result;

	}
}

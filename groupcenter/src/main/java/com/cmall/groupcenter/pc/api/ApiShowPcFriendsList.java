package com.cmall.groupcenter.pc.api;

import com.cmall.groupcenter.pc.model.PcFriendsListInput;
import com.cmall.groupcenter.pc.model.PcFriendsListResult;
import com.cmall.groupcenter.service.GroupPcService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 
 * PC版本获取好友列表信息
 * @author GaoYang
 * @CreateDate 2015年8月10日下午2:03:04
 *
 */
public class ApiShowPcFriendsList extends RootApiForToken<PcFriendsListResult,PcFriendsListInput>{

	@Override
	public PcFriendsListResult Process(PcFriendsListInput inputParam,
			MDataMap mRequestMap) {
		
		PcFriendsListResult fridensListResult = new PcFriendsListResult();
		String accountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		GroupPcService pcService = new GroupPcService();
		fridensListResult = pcService.ShowPcFriendsList(accountCode, inputParam);
		return fridensListResult;
	}

}

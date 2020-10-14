package com.cmall.groupcenter.groupapp.api;

import org.jsoup.helper.StringUtil;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 微公社APP2.0用于消息设置中的清除系统消息
 * 
 * @author fengl
 * @date 2015-12-1
 * 
 *
 */
public class ApiClearSystemMessage extends RootApiForToken<RootResultWeb,RootInput>{

	public RootResultWeb Process(RootInput inputParam,
			MDataMap mRequestMap) {
		
		RootResultWeb result = new RootResultWeb();
		
		String sAccountCode = DbUp.upTable("mc_member_info")
				.oneWhere("account_code", "", "", "member_code", getUserCode())
				.get("account_code");
		if(StringUtil.isBlank(sAccountCode)){ //找不到这个用户
			result.inErrorMessage(918519024);
		}else{
			MDataMap mDataMap = new MDataMap();
//			DbUp.upTable("sc_comment_push_single").dataExec("update sc_comment_push_single set is_read='4497465200180002' where user_code=:user_code and type in('44974720000400010001','44974720000400010002','44974720000400010003')",mDataMap);
			mDataMap.put("account_code", sAccountCode);
			mDataMap.put("is_clear", "0");
			mDataMap.put("is_read", "4497465200180002");
			DbUp.upTable("sc_comment_push_single").dataUpdate(mDataMap, "is_clear", "account_code,is_read");
			result.setResultCode(1);
			result.setResultMessage("清除系统消息成功");
		}
		return result;
	}
	

}

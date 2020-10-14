package com.cmall.groupcenter.message.api;

import com.cmall.groupcenter.message.model.UserAdviceFeedbackInput;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 模块:个人中心->用户反馈
 * 功能:用户发送反馈消息
 * @author LHY
 * 2015年1月15日 下午3:44:07
 */
public class ApiUserFeedback extends RootApiForToken<RootResultWeb, UserAdviceFeedbackInput> {

	public MWebResult Process(UserAdviceFeedbackInput inputParam, MDataMap mDataMap) {
		MWebResult result = new MWebResult();
		MDataMap map = new MDataMap();
		map.put("app_code", getManageCode());
		map.put("user_code", getUserCode());
		map.put("description", inputParam.getDescription());
		map.put("create_time", DateUtil.getSysDateTimeString());
		DbUp.upTable("sc_advice_feedback").dataInsert(map);
		result.setResultCode(1);
		result.setResultMessage("成功");
		return result;
	}

}

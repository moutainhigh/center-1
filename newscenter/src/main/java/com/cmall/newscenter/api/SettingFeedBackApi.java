package com.cmall.newscenter.api;

import com.cmall.newscenter.model.SettingFeedBackInput;
import com.cmall.newscenter.model.SettingFeedBackResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 设置 - 意见反馈
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class SettingFeedBackApi extends RootApiForToken<SettingFeedBackResult, SettingFeedBackInput> {

	public SettingFeedBackResult Process(SettingFeedBackInput inputParam,
			MDataMap mRequestMap) {
		
		SettingFeedBackResult result = new SettingFeedBackResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mDataMap = new MDataMap();
			
			
			mDataMap.put("feedback", inputParam.getText());
			
			mDataMap.put("member_code", getUserCode());
			
			mDataMap.put("creat_time", FormatHelper.upDateTime());
			
			
			DbUp.upTable("nc_feedback").dataInsert(mDataMap);
			
		}
		return result;
	}

}

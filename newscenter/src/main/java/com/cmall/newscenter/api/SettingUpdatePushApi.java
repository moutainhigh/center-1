package com.cmall.newscenter.api;


import com.cmall.newscenter.model.Config;
import com.cmall.newscenter.model.SettingUpdatePushInput;
import com.cmall.newscenter.model.SettingUpdatePushResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 设置 - 更新设备ID
 * @author liqiang
 * date 2014-7-22
 * @author shiyz
 * date 2014-8-5
 * @version 2.0
 */
public class SettingUpdatePushApi extends RootApiForToken<SettingUpdatePushResult, SettingUpdatePushInput> {

	public SettingUpdatePushResult Process(SettingUpdatePushInput inputParam,
			MDataMap mRequestMap) {
		
		SettingUpdatePushResult result = new SettingUpdatePushResult();
		
		MDataMap mDataMap = new MDataMap();
		
		
		if(result.upFlagTrue()){
			
			
			mDataMap = DbUp.upTable("nc_push").one("uuid",inputParam.getUuid());
			
			if(mDataMap!=null){
				/*平台*/
				mDataMap.put("platform", inputParam.getPlatform());
				
				/*推送TOKEN*/
				mDataMap.put("token", inputParam.getToken());
				
				/*uuid*/
				mDataMap.put("uuid", inputParam.getUuid());
				
				/*用户编号*/
				mDataMap.put("member_code", getUserCode());
				
				/*如果存在更新*/
				DbUp.upTable("nc_push").update(mDataMap);
			}
			else 
			{
				MDataMap mwDataMap = new MDataMap();	
				/*平台*/
				mwDataMap.put("platform", inputParam.getPlatform());
				
				/*推送TOKEN*/
				mwDataMap.put("token", inputParam.getToken());
				
				/*uuid*/
				mwDataMap.put("uuid", inputParam.getUuid());
				
				/**/
				mwDataMap.put("member_code", getUserCode());
				
				/*如果不存在插入*/
				DbUp.upTable("nc_push").dataInsert(mwDataMap);
				
			}
			
			
			Config config = new Config();
			
			config.setPush(Integer.valueOf(mDataMap.get("push_switch")));
			
			result.setConfig(config);
		}
		return result;
	}

}

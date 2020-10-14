package com.cmall.newscenter.api;

import com.cmall.newscenter.model.Config;
import com.cmall.newscenter.model.SettingSwitchPushInput;
import com.cmall.newscenter.model.SettingSwitchPushResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 设置 - 推送开关
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class SettingSwitchPushApi extends RootApiForToken<SettingSwitchPushResult, SettingSwitchPushInput> {

	public SettingSwitchPushResult Process(SettingSwitchPushInput inputParam,
			MDataMap mRequestMap) {
		
		SettingSwitchPushResult result = new SettingSwitchPushResult();
		
		MDataMap mDataMap = new MDataMap();
		
		String push = "";
		
		Config config = new Config();
		
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
				
				/*是否关闭*/
				mDataMap.put("push_switch", String.valueOf(inputParam.getPush_switch()));
				
				/*如果存在更新*/
				DbUp.upTable("nc_push").update(mDataMap);
				
				
				push =  mDataMap.get("push_switch");
				
				config.setPush(Integer.valueOf(push));
				
			    }else {
				
			    	MDataMap mwDataMap = new MDataMap();	
			    	
				/*平台*/
			    	mwDataMap.put("platform", inputParam.getPlatform());
				
				/*推送TOKEN*/
			    	mwDataMap.put("token", inputParam.getToken());
				
				/*uuid*/
			    	mwDataMap.put("uuid", inputParam.getUuid());
				
				/*用户编号*/
			    	mwDataMap.put("member_code", getUserCode());
				
				/*是否关闭*/
			    	mwDataMap.put("push_switch", String.valueOf(inputParam.getPush_switch()));
				
					/*如果不存在插入*/
					DbUp.upTable("nc_push").dataInsert(mwDataMap);
					
					config.setPush(Integer.valueOf(push));
					
			}
			
			result.setConfig(config);
			
		}
		return result;
	}

}

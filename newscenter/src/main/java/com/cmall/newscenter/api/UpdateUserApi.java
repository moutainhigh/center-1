package com.cmall.newscenter.api;

import com.cmall.newscenter.model.UpdateUserInput;
import com.cmall.newscenter.model.UpdateUserResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 修改用户 
 * @author shiyz	
 * date 2014-11-03
 * @version 1.0
 */
public class UpdateUserApi extends RootApiForToken<UpdateUserResult, UpdateUserInput> {

	public UpdateUserResult Process(UpdateUserInput inputParam,
			MDataMap mRequestMap) {
		
		UpdateUserResult result = new UpdateUserResult();
		
		MDataMap mWhereMap = new MDataMap();
		
		RootResultWeb rootResultWeb = new RootResultWeb();
		
		if(result.upFlagTrue()){
			
			MDataMap mDataMap = DbUp.upTable("mc_extend_info_star").one("member_code",getUserCode());
			
			if(mDataMap!=null){
			
			if(inputParam.getFlag()==0){

				result.setAge(Integer.valueOf(mDataMap.get("member_age")));
				
				result.setGender(mDataMap.get("member_sex"));
				
				result.setName(mDataMap.get("member_name"));
				
			}else {
				
				mWhereMap.put("member_sex", inputParam.getGender());
				
				mWhereMap.put("member_name", inputParam.getName());
				
				mWhereMap.put("member_age", String.valueOf(inputParam.getAge()));
				
				mWhereMap.put("zid", mDataMap.get("zid"));
				
				DbUp.upTable("mc_extend_info_star").dataUpdate(mWhereMap, "member_sex,member_name,member_age", "zid");
				
				
				result.setAge(inputParam.getAge());
				
				result.setGender(inputParam.getGender());
				
				result.setName(inputParam.getName());
				
			}
			}else {
				
				rootResultWeb.inErrorMessage(934105101);
				
				
			}
		}
		
		return result;
	}
}

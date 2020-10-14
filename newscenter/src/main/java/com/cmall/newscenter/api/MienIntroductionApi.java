package com.cmall.newscenter.api;

import java.util.Map;

import com.cmall.newscenter.model.MienIntroductionApplyInput;
import com.cmall.newscenter.model.MienIntroductionApplyResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 栏目 - 获取栏目详情（url）
 * @author guz
 * date 2014-9-15
 * @version 1.0
 */
public class MienIntroductionApi extends RootApiForManage<MienIntroductionApplyResult, MienIntroductionApplyInput>{

	public MienIntroductionApplyResult Process(
			MienIntroductionApplyInput inputParam, MDataMap mRequestMap) {
		
		MienIntroductionApplyResult result = new MienIntroductionApplyResult();
		//设置相关信息
				if(result.upFlagTrue()){
					
					MDataMap mDataMap = new MDataMap();
					
					mDataMap.put("category_code", inputParam.getColumn());
					
					MDataMap mMemberMap = DbUp.upTable("nc_category").one("category_code",inputParam.getColumn());
				    
				    if(mMemberMap !=null){
				    	
				    	result.setColumn_detail_url(String.valueOf(mMemberMap.get("line_head")));
				    	
				    }
				}
				
		return result;
	}

}

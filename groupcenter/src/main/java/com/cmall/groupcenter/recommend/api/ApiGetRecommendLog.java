package com.cmall.groupcenter.recommend.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.recommend.model.ApiGetRecommendLogInput;
import com.cmall.groupcenter.recommend.model.ApiGetRecommendLogResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 获取推荐过的联系人
 * @author fq
 *
 */
public class ApiGetRecommendLog extends RootApiForManage<ApiGetRecommendLogResult, ApiGetRecommendLogInput>{

	public ApiGetRecommendLogResult Process(ApiGetRecommendLogInput inputParam,
			MDataMap mRequestMap) {
		
		ApiGetRecommendLogResult result = new ApiGetRecommendLogResult();
		List<String> recommendMobile = new ArrayList<String>();
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("mobile",inputParam.getMobile());
		//mDataMap.put("flag_establishment_rel","0");
//		mDataMap.put("app_code", getManageCode());
		List<MDataMap> recommendLogList = DbUp.upTable("gc_recommend_info").query("recommended_mobile", "", "mobile=:mobile", mDataMap, -1, -1);
		for (MDataMap recommendLog : recommendLogList) {
			recommendMobile.add(recommendLog.get("recommended_mobile"));
		}
		if(recommendMobile.size() > 0) {
			result.setBound_status("1");
		} else {
			result.setBound_status("0");
		}
		result.setMobile(recommendMobile);
		return result;
	}
	
}

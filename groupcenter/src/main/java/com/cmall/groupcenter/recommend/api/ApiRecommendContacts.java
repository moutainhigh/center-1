package com.cmall.groupcenter.recommend.api;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cmall.groupcenter.recommend.RecommendUtil;
import com.cmall.groupcenter.recommend.model.ApiRecommendContactsInput;
import com.cmall.groupcenter.recommend.model.ApiRecommendContactsResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

/**
 * 推荐联系人
 * @author fq
 *
 */
public class ApiRecommendContacts extends RootApiForManage<ApiRecommendContactsResult, ApiRecommendContactsInput>{

	public ApiRecommendContactsResult Process(
			ApiRecommendContactsInput inputParam, MDataMap mRequestMap) {
		
		ApiRecommendContactsResult result = new ApiRecommendContactsResult();
		
				//添加测试校验的方法sendLinkWithCheck
				RecommendUtil util = new RecommendUtil();
				Map<String, List<String>> sendLink = util.sendLink(inputParam.getMobile(), inputParam.getTels(), getManageCode());
				result.setSuccess_tels(sendLink.get("success"));
				result.setError_tels(sendLink.get("error"));
		
		
		return result;
	}
	
}

package com.cmall.usercenter.service.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.usercenter.model.api.ApiGetCategroyNameInput;
import com.cmall.usercenter.model.api.ApiGetCategroyNameResult;
import com.cmall.usercenter.service.SellercategoryService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetCategroyName extends RootApi<ApiGetCategroyNameResult,ApiGetCategroyNameInput> {
/**
 * 获取分类名称（后台js调用）
 */
	public ApiGetCategroyNameResult Process(ApiGetCategroyNameInput api, MDataMap mRequestMap) {
		ApiGetCategroyNameResult result = new ApiGetCategroyNameResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			SellercategoryService sis  = new SellercategoryService();
			MDataMap categoryNameMap = sis.getCateGoryNmaes(api.getCategoryCodes(), api.getSellerCode());
			String categoryName = categoryNameMap.get("categoryName");
			if (StringUtils.isNotBlank(categoryName) && !categoryName.startsWith("<span class='w_regex_need'>")) {
				result.setCategoryName(categoryNameMap.get("categoryName"));
			}
		}
		return result;
	}
}
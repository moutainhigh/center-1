package com.cmall.productcenter.service.api;

import com.cmall.productcenter.model.GetTemplateNameInput;
import com.cmall.productcenter.model.GetTemplateNameResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class getTemplateNameApi extends RootApi<GetTemplateNameResult, GetTemplateNameInput> {
	/**
	 * 根据地区code获取限制地区名称
	 */
	public GetTemplateNameResult Process(GetTemplateNameInput inputParam, MDataMap mRequestMap){
		GetTemplateNameResult result = new GetTemplateNameResult();
		String templateCode = inputParam.getTemplateCode();
		MDataMap mDataMap = DbUp.upTable("sc_area_template").oneWhere("template_name", "", "", "template_code",templateCode);
		result.setTemplateName(mDataMap.get("template_name"));
		return result;
	}
}

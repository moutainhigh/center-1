package com.cmall.productcenter.service.api;

import java.util.Map;

import com.cmall.productcenter.model.GetFreightNameInput;
import com.cmall.productcenter.model.GetFreightNameResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class getFreightNameApi extends RootApi<GetFreightNameResult, GetFreightNameInput> {

	/**
	 *根据uid获取运费模板名称 
	 * 
	 */
	public GetFreightNameResult Process(GetFreightNameInput inputParam, MDataMap mRequestMap) {
		GetFreightNameResult result = new GetFreightNameResult();
		String uid = inputParam.getUid();
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("uid", uid);
		Map<String, Object> map = DbUp.upTable("uc_freight_tpl").dataSqlOne("select tpl_name from uc_freight_tpl where uid=:uid", mWhereMap);
		String tplName = "";
		if(map!=null&&!map.isEmpty()){
			tplName = map.get("tpl_name").toString();
		}
		result.setTplName(tplName);
		return result;
	}
}

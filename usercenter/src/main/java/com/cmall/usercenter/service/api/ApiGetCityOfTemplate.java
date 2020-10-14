package com.cmall.usercenter.service.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.MapUtils;

import com.cmall.usercenter.model.api.ApiGetCityOfTemplateInput;
import com.cmall.usercenter.model.api.ApiGetCityOfTemplateResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiGetCityOfTemplate extends
		RootApi<ApiGetCityOfTemplateResult, ApiGetCityOfTemplateInput> {
	/**
	 * 获取模板对应的城市信息（后台js调用）
	 */
	public ApiGetCityOfTemplateResult Process(ApiGetCityOfTemplateInput api,
			MDataMap mRequestMap) {
		ApiGetCityOfTemplateResult result = new ApiGetCityOfTemplateResult();
		if (api == null) {
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		} else {
			String sSql = "SELECT ai.province_code,ai.city_code,st.name city_name,sv.name province_name" +
					" FROM sc_area_template_info ai LEFT JOIN sc_tmp st ON ai.city_code = st.code LEFT JOIN" +
					" sc_tmp sv ON ai.province_code = sv.code where template_code=:template_code order by ai.city_code";
			List<Map<String, Object>> tempMap = DbUp.upTable(
					"sc_area_template_info").dataSqlList(sSql,
					new MDataMap("template_code", api.getTemplateCode()));
			if(tempMap == null){
				tempMap = new ArrayList<Map<String,Object>>();
				result.setTempMap(tempMap);
			}else{
				List<Map<String, Object>> cityMapList = new ArrayList<Map<String,Object>>();
				Set<String> existSet = new HashSet<String>();
				for(Map<String, Object> cityMap : tempMap) {
					String cityTmpCode = MapUtils.getString(cityMap, "province_code", "") + "_" + MapUtils.getString(cityMap, "city_code", "");
					if(!existSet.contains(cityTmpCode)) {
						existSet.add(cityTmpCode);
						
						cityMapList.add(cityMap);
					}
				}
				
				result.setTempMap(cityMapList);
			}
		}
		return result;
	}
}
package com.cmall.ordercenter.service.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cmall.ordercenter.model.Express;
import com.cmall.ordercenter.model.api.ApiGetExpInput;
import com.cmall.ordercenter.model.api.ApiGetExpResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
/**
 * 
 * 快递100 接口
 * @author wangkecheng
 *
 */
public class ApiExp extends RootApi<ApiGetExpResult, ApiGetExpInput> {

	public ApiGetExpResult Process(ApiGetExpInput api, MDataMap mRequestMap) {
		ApiGetExpResult result = new ApiGetExpResult();
		if (api == null) {
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		} else {

			
			try {
				Express exp = null;
				List<Express> expressList = new ArrayList<Express>();
				DbTemplate dt = DbUp.upTable("oc_express_detail").upTemplate();
				Map<String,Object> paramMap = new HashMap<String,Object>();
				paramMap.put("order_code", api.getOrderCode());
				List<Map<String,Object>> list = dt.queryForList("select context,time from oc_express_detail where order_code = :order_code", paramMap);
				for(Map<String,Object> map : list){
					exp = new Express();
					exp.setContext(map.get("context").toString());
					exp.setTime(map.get("time").toString());
					
					expressList.add(exp);
				}
				result.setList(expressList);
				
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}

		}

		return result;
	}
}
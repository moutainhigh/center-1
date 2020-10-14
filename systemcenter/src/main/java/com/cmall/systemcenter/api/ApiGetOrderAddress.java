package com.cmall.systemcenter.api;

import java.util.List;
import java.util.Map;

import com.cmall.systemcenter.model.ApiGetOrderAddressInput;
import com.cmall.systemcenter.model.ApiGetOrderAddressResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForManage;

public class ApiGetOrderAddress extends RootApiForManage<ApiGetOrderAddressResult, ApiGetOrderAddressInput>{

	
	
	public ApiGetOrderAddressResult Process(ApiGetOrderAddressInput inputParam, MDataMap mRequestMap) {
		ApiGetOrderAddressResult result = new ApiGetOrderAddressResult();
		String sql = "";
		MDataMap dataMap = DbUp.upTable("oc_risk_setting").oneWhere("","", "", "api_class_name", "com.cmall.systemcenter.api.ApiGetOrderAddress");
		if(null != dataMap){
			switch (inputParam.getSqlType()) {
			case 1:
				sql = dataMap.get("query_sql").toString();
				List<Map<String, Object>> orderList = DbUp.upTable("oc_orderadress").dataSqlList(sql, new MDataMap("order_code",inputParam.getOrderCode()));
				result.setList(orderList);
				break;
			case 2:
				sql = dataMap.get("update_sql").toString();
				int res = DbUp.upTable("oc_orderinfo").dataExec(sql, new MDataMap("orderCode",inputParam.getOrderCode()));
				result.setResultCode(res);
				break;
			default:
				break;
			}
		}
		
		return result;
	}
	
}

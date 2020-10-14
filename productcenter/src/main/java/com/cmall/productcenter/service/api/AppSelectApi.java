package com.cmall.productcenter.service.api;

import java.util.List;

import com.cmall.productcenter.model.AppSelectInput;
import com.cmall.productcenter.model.AppSelectResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * app二级联动
 * 
 * @author 李国杰
 * @version 1.0
 */
public class AppSelectApi extends RootApi<AppSelectResult, AppSelectInput> {

	public AppSelectResult Process(AppSelectInput inputParam,
			MDataMap mRequestMap) {

		AppSelectResult result = new AppSelectResult();
		String appCode = inputParam.getAppCode(); // app编码
		MDataMap mWhereMap = new MDataMap();
		if(appCode!=null&&!"".equals(appCode)){
			mWhereMap.put("app_code", appCode);
			//查询结果列column_code,column_name，根据column_type_code排序，查询条件列为app_code
			List<MDataMap> list = DbUp.upTable("nc_app_column").queryAll("column_code,column_name", "column_type_code", "", mWhereMap);	
			result.setListColumn(list);
		}
		return result;
	}
}

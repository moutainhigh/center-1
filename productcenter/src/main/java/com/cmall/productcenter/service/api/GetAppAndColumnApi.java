package com.cmall.productcenter.service.api;

import java.util.List;

import com.cmall.productcenter.model.GetAppAndColumnInput;
import com.cmall.productcenter.model.GetAppAndColumnResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 根据广告位获取App与栏目
 * 
 * @author 李国杰
 * @version 1.0
 */
public class GetAppAndColumnApi extends RootApi<GetAppAndColumnResult, GetAppAndColumnInput> {

	public GetAppAndColumnResult Process(GetAppAndColumnInput inputParam,
			MDataMap mRequestMap) {

		GetAppAndColumnResult result = new GetAppAndColumnResult();
		String placeCode = inputParam.getPlaceCode(); // 广告位编码
		
		if(placeCode!=null&&!"".equals(placeCode)){
			//查询结果列column_code,column_name，app_code,app_name，查询条件列为place_code
			MDataMap resultMap = DbUp.upTable("nc_advertise_place").one("place_code",placeCode);
			
			String adWhere = " place_code = '"+placeCode+"' ";
			//得到该广告位下最大的权值+1
			List<MDataMap> adResultMap = DbUp.upTable("nc_advertise").queryAll("(MAX(sort_num)+1) as sort_num","",adWhere,null);
			
			if (null == adResultMap || 0 == adResultMap.size()) return result;
			String sortNum = adResultMap.get(0).get("sort_num");
			resultMap.put("sort_num", (sortNum == null || "".equals(sortNum)) ? "1" : sortNum);			//该广告位下最大的权值+1
			result.setResultMap(resultMap);
		}
		return result;
	}
}

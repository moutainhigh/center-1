package com.cmall.productcenter.service.api;

import java.util.HashMap;
import java.util.Map;

import com.cmall.productcenter.model.api.ApiGetAnnounceInput;
import com.cmall.productcenter.model.api.ApiGetAnnounceResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
/**
 * 店铺公告接口
 * 
 * @author wangkecheng
 *
 */

public class ApiAnnounce extends	RootApi<ApiGetAnnounceResult, ApiGetAnnounceInput> {

	public ApiGetAnnounceResult Process(ApiGetAnnounceInput api,MDataMap mRequestMap) {
		
		ApiGetAnnounceResult result = new ApiGetAnnounceResult();
		if (api == null) {
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		} else {
			if (api.getZid().equals("")) {
				return result;
			}
			Map<String,Object> paramMap = new HashMap<String,Object>();
			paramMap.put("zid", api.getZid());
			
			DbTemplate dt = DbUp.upTable("uc_seller_announce").upTemplate();
			//result = dt.queryForObject("select zid , title ,content from uc_seller_announce where zid = :zid", paramMap,ApiGetAnnounceResult.class);
			
			Map<String,Object> dbMap = dt.queryForMap("select title ,content from uc_seller_announce where zid = :zid", paramMap);
			result.setZid(api.getZid());
			result.setTitle(dbMap.get("title").toString());
			result.setContent(dbMap.get("content").toString());
		}

		return result;
	}

}

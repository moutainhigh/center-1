package com.cmall.ordercenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.ordercenter.model.OcTryoutProducts;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class TryoutProductsService extends BaseClass {
	
	/**
	 * 查询试用商品
	 * @param createTime   创建时间
	 * @param skuCode
	 * @param activityCode
	 * @return
	 */
	public List<Map<String, Object>> getTryoutProducts(String createTime, String skuCode,
			String activityCode) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		
		MDataMap mapParam = new MDataMap();
		mapParam.put("sku_code", skuCode);
		mapParam.put("activity_code", activityCode);
		mapParam.put("create_time", createTime);

		list =  DbUp.upTable("oc_tryout_products")
				.dataSqlList(
						"select * from oc_tryout_products where  activity_code=:activity_code and sku_code =:sku_code and start_time<:create_time and end_time>:create_time ",
						mapParam);
		
		return list;
	}
}

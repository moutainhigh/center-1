package com.cmall.productcenter.service.api;

import com.cmall.productcenter.model.api.ApiGetProductForhpInput;
import com.cmall.productcenter.model.api.ApiGetProductForhpResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
/***
 * 根据商品编码查询商品信息
 * @author jlin
 *
 */
public class ApiGetProductForhp extends RootApi<ApiGetProductForhpResult,ApiGetProductForhpInput>  {

	public ApiGetProductForhpResult Process(ApiGetProductForhpInput api,MDataMap mRequestMap) {
		ApiGetProductForhpResult result = new ApiGetProductForhpResult();
		String productCode=api.getProductCode();
		MDataMap dataMap = null ;
		try {
			dataMap = DbUp.upTable("pc_productinfo").oneWhere("product_code,product_name,product_shortname,min_sell_price,max_sell_price", "", "product_code=:product_code", "product_code",productCode);
		} catch (Exception e) {
			e.printStackTrace();
			result.setResultCode(941901105);
			result.setResultMessage(bInfo(941901105));
			return result;
		}
		
		result.setMax_sell_price(dataMap.get("max_sell_price"));
		result.setMin_sell_price(dataMap.get("min_sell_price"));
		result.setProduct_code(dataMap.get("product_code"));
		result.setProduct_name(dataMap.get("product_name"));
		result.setProduct_shortname(dataMap.get("product_shortname"));
		
		return result;
	}

}

package com.cmall.productcenter.service.api;

import com.cmall.productcenter.model.api.ApiGetProductNameInput;
import com.cmall.productcenter.model.api.ApiGetProductNameResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 根据商品code获取商品名称
 * @author 李国杰
 *
 */
public class ApiGetProductName extends RootApi<ApiGetProductNameResult,ApiGetProductNameInput> {

	public ApiGetProductNameResult Process(ApiGetProductNameInput inputParam,
			MDataMap mRequestMap) {
		ApiGetProductNameResult result = new ApiGetProductNameResult();
		if(inputParam == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			MDataMap resultMap = DbUp.upTable("pc_productinfo").one("product_code",inputParam.getProductStrs());
			result.setProductName( null == resultMap ? "" : resultMap.get("product_name"));
		}
		return result;
	}

}

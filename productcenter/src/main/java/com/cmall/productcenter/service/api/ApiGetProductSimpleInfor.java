package com.cmall.productcenter.service.api;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.productcenter.model.api.ApiGetProductSimpleInforInput;
import com.cmall.productcenter.model.api.ApiGetProductSimpleInforResult;
import com.cmall.productcenter.service.ProductCheck;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/** 
* @ClassName: ApiGetProductSimpleInfor 
* @Description: 获取商品相关信息
* @author 张海生
* @date 2015-9-19 下午1:33:58 
*  
*/
public class ApiGetProductSimpleInfor extends
		RootApi<ApiGetProductSimpleInforResult, ApiGetProductSimpleInforInput> {

	public ApiGetProductSimpleInforResult Process(
			ApiGetProductSimpleInforInput input, MDataMap mRequestMap) {
		ApiGetProductSimpleInforResult result = new ApiGetProductSimpleInforResult();
		String productCode = input.getProductCode();
		if (StringUtils.isNotEmpty(productCode)) {
			ProductCheck pc = new ProductCheck();
			Map<String, Object> proMap = pc.getProductRelaInfor(productCode);
			result.setProductMap(proMap);
		}
		return result;
	}
}

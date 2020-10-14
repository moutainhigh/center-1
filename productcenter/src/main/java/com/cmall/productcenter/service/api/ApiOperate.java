package com.cmall.productcenter.service.api;


import com.cmall.productcenter.model.api.ApiOperateInput;
import com.cmall.productcenter.model.api.ApiOperateResult;
import com.cmall.productcenter.service.ProductService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;

public class ApiOperate extends RootApi<ApiOperateResult,ApiOperateInput> {

	public ApiOperateResult Process(ApiOperateInput api, MDataMap mRequestMap) {
		ApiOperateResult result = new ApiOperateResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			ProductService os = new ProductService();
			
			try {
				
				RootResult rr = os.operate(api.getPpi(), api.getType());
				result.setResultCode(rr.getResultCode());
				result.setResultMessage(rr.getResultMessage());
				
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}
			
		}
		
		return result;
	}
}
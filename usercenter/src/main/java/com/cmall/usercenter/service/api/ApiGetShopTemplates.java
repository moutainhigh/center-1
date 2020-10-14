package com.cmall.usercenter.service.api;

import java.util.List;

import com.cmall.usercenter.model.ShopTemplateForI;
import com.cmall.usercenter.model.api.ApiGetShopTemplateInput;
import com.cmall.usercenter.model.api.ApiGetShopTemplateResult;
import com.cmall.usercenter.service.UcShopTemplateService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiGetShopTemplates extends RootApi<ApiGetShopTemplateResult,ApiGetShopTemplateInput> {

	public ApiGetShopTemplateResult Process(ApiGetShopTemplateInput api, MDataMap mRequestMap) {
		ApiGetShopTemplateResult result = new ApiGetShopTemplateResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			UcShopTemplateService sis  = new UcShopTemplateService();
			
			try {
				
				List<ShopTemplateForI> list =sis.getShopTemplate(api.getSellerCodes(), api.getType());
				
				result.setResultCode(1);
				result.setShopTemplateList(list);
				
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}
			
		}
		
		return result;
	}
}
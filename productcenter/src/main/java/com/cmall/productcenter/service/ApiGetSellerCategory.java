package com.cmall.productcenter.service;

import java.util.List;

import com.cmall.productcenter.model.ApiGetCategoryResult;
import com.cmall.productcenter.model.ApiGetSellerCategoryInput;
import com.cmall.productcenter.model.Category;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapweb.webfactory.UserFactory;

public class ApiGetSellerCategory  extends RootApi<ApiGetCategoryResult, ApiGetSellerCategoryInput> {
	
	public ApiGetCategoryResult Process(ApiGetSellerCategoryInput api, MDataMap mRequestMap) {
		ApiGetCategoryResult result = new ApiGetCategoryResult();
		
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			//如果当前的SellerCode传的为空，则需要取得当前的数据
			if(api.getSellerCode().equals(""))
			{
				if(UserFactory.INSTANCE.create()!=null){
					String manageCode = UserFactory.INSTANCE.create().getManageCode();
					api.setSellerCode(manageCode);
				}
			}
			
			CategoryService s = new CategoryService();
			List<Category> list = s.getCategoryListBySellerCode(api.getSellerCode(), api.getLevel(), api.getPid());
			result.setList(list);
		}
		
		return result;
	}
}

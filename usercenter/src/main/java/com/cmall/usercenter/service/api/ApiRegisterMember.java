package com.cmall.usercenter.service.api;

import java.util.List;

import com.cmall.usercenter.model.CollectionSellerModel;
import com.cmall.usercenter.model.api.ApiMemberRegisterInput;
import com.cmall.usercenter.model.api.ApiMemberRegisterResult;
import com.cmall.usercenter.service.SellerInfoService;
import com.cmall.usercenter.service.UcMemberInfoService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

public class ApiRegisterMember extends RootApi<ApiMemberRegisterResult,ApiMemberRegisterInput> {

	public ApiMemberRegisterResult Process(ApiMemberRegisterInput api, MDataMap mRequestMap) {
		ApiMemberRegisterResult result = new ApiMemberRegisterResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			
			UcMemberInfoService ums = new UcMemberInfoService();
			
			try {
				
				result = ums.RegisterMember(api.getUserId(), api.getUserCode());
				
			} catch (Exception e) {
				result.setResultCode(939301033);
				result.setResultMessage(bInfo(939301033));
			}
			
		}
		
		return result;
	}
}
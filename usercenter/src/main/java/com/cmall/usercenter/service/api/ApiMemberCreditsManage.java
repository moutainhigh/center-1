package com.cmall.usercenter.service.api;

import com.cmall.usercenter.model.api.ApiMemberCreditsManageInput;
import com.cmall.usercenter.model.api.ApiMemberCreditsManageResult;
import com.cmall.usercenter.service.MemberCreditsManageService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 
 * 项目名称：usercenter 
 * 类名称：     ApiMemberCreditsManage 
 * 类描述：     会员积分管理
 * 创建人：     GaoYang  
 * 创建时间：2013年11月9日下午1:52:48 
 * 修改人：     GaoYang
 * 修改时间：2013年11月9日下午1:52:48
 * 修改备注：  
 * @version
 * 
 */
public class ApiMemberCreditsManage extends RootApi<ApiMemberCreditsManageResult,ApiMemberCreditsManageInput>{

	public ApiMemberCreditsManageResult Process(
			ApiMemberCreditsManageInput inputParam, MDataMap mRequestMap) {
		
		ApiMemberCreditsManageResult result = new ApiMemberCreditsManageResult();
		if(inputParam == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			MemberCreditsManageService service = new MemberCreditsManageService();
			
			result = service.CreditsManage(inputParam);
		}
		
		return result;
	}



}

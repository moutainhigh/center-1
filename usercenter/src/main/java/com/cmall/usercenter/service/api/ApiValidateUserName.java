package com.cmall.usercenter.service.api;


import org.apache.commons.lang.StringUtils;

import com.cmall.usercenter.model.api.ApiUserInput;
import com.cmall.usercenter.model.api.ApiUserResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * @author hxd
 * 校验用户名的合法性
 */
public class ApiValidateUserName extends RootApi<ApiUserResult,ApiUserInput> {

	public ApiUserResult Process(ApiUserInput inputParam, MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		ApiUserResult rs = new ApiUserResult();
		if(inputParam == null)
		{
			rs.setResultMessage(bInfo(959701024));
		    rs.setResultCode(959701024);
		    return rs;
		}
		if(StringUtils.isBlank(inputParam.getUserName()))
		{
			rs.setResultMessage(bInfo(959701024));
		    rs.setResultCode(959701024);
		    return rs;
		}
		MDataMap mp = DbUp.upTable("za_userinfo").one("user_name",inputParam.getUserName().trim());
       if(null != mp)
       {
    	   rs.setResultMessage(bInfo(959701026));
		   rs.setResultCode(959701026);
		   return rs;
       }
		return rs;
	}


}
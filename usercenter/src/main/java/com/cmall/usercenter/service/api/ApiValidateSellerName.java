package com.cmall.usercenter.service.api;

import org.apache.commons.lang.StringUtils;

import com.cmall.usercenter.model.api.ApiSellerInput;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
public class ApiValidateSellerName extends RootApi<RootResult,ApiSellerInput> {
	public RootResult Process(ApiSellerInput inputParam, MDataMap mRequestMap) {
		RootResult rs = new RootResult();
		if(StringUtils.isBlank(inputParam.getSellerName()))
		{
			rs.setResultCode(959701030);
			rs.setResultMessage(bInfo(959701030));
			return rs;
		}
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("seller_name", inputParam.getSellerName().trim());
		int count = DbUp.upTable("uc_sellerinfo").dataCount("seller_name=:seller_name", mWhereMap);
		if(count >=1)
		{
			rs.setResultCode(959701031);
			rs.setResultMessage(bInfo(959701031));
			return rs;
		}
		return rs;
	}

	
}

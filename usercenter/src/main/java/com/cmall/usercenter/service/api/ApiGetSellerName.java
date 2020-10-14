package com.cmall.usercenter.service.api;

import java.util.List;

import com.cmall.usercenter.model.UcSellerInfoBaseInfo;
import com.cmall.usercenter.model.api.ApiGetSellerNameInput;
import com.cmall.usercenter.model.api.ApiGetSellerNameResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiGetSellerName extends RootApi<ApiGetSellerNameResult,ApiGetSellerNameInput> {
/**
 * 获取商户名称（后台js调用）
 */
	public ApiGetSellerNameResult Process(ApiGetSellerNameInput api, MDataMap mRequestMap) {
		ApiGetSellerNameResult result = new ApiGetSellerNameResult();
		if(api == null){
			result.setResultCode(941901019);
			result.setResultMessage(bInfo(941901019));
		}else{
			String sWhere = "small_seller_code in ('"+api.getSmallSellerCodes().replace(",", "','")+"')";
			String sFields = "small_seller_code,seller_name";
			List<MDataMap> map=DbUp.upTable("uc_sellerinfo").queryAll(sFields, "small_seller_code", sWhere,null);
			for (MDataMap mDataMap : map) {
				UcSellerInfoBaseInfo sInfo = new UcSellerInfoBaseInfo();
				sInfo.setSmallSellerCode(mDataMap.get("small_seller_code"));
				sInfo.setSellerName(mDataMap.get("seller_name"));
				result.getSellerList().add(sInfo);
			}
		}
		return result;
	}
}
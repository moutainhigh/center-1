package com.cmall.usercenter.service.api;

import java.util.List;

import com.cmall.usercenter.model.SellerDesc;
import com.cmall.usercenter.model.api.ApiGetSellerDescInput;
import com.cmall.usercenter.model.api.ApiGetSellerDescResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webfactory.UserFactory;
/**
 * 店铺介绍接口
 * @author wangkecheng
 *
 */
public class ApiSellerDesc  extends RootApi<ApiGetSellerDescResult,ApiGetSellerDescInput>{

	public ApiGetSellerDescResult Process(ApiGetSellerDescInput api, MDataMap mRequestMap) {
		ApiGetSellerDescResult result = new ApiGetSellerDescResult();
		
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
		}else{
			//String seller_code = UserFactory.INSTANCE.create().getManageCode();
			String seller_code = api.getSellerCode();
			
			SellerDesc sellerDesc = new SellerDesc();
			MDataMap m = DbUp.upTable("uc_seller_desc").one("seller_code",seller_code);
					//.oneWhere("seller_desc", "", "seller_code", seller_code);
			if(m != null){
				sellerDesc.setSellerCode(seller_code);
				sellerDesc.setSellerDesc(m.get("seller_desc"));
				sellerDesc.setSellerLog(m.get("seller_log"));
				result.setResultCode(1);
				result.setSellerDesc(sellerDesc);
			}else{
				//result.setResultCode(-1);
				result.setSellerDesc(null);
			}
//			this.bLogInfo(0, "seller_desc  :",m.get("seller_desc"));
//			this.bLogInfo(0, "seller_log  :",m.get("seller_log"));
			
		}
		return result;
	}

}

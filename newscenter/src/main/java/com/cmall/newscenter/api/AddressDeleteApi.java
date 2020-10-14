package com.cmall.newscenter.api;

import com.cmall.newscenter.model.AddressDeleteInput;
import com.cmall.newscenter.model.AddressDeleteResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 删除地址
 * @author shiyz
 * date 2014-8-4
 * @version 1.0 
 */


public class AddressDeleteApi extends RootApiForToken<AddressDeleteResult, AddressDeleteInput> {

	public AddressDeleteResult Process(AddressDeleteInput inputParam,
			MDataMap mRequestMap) {
		
		AddressDeleteResult result = new AddressDeleteResult();
		
		RootResultWeb rootResultWeb = new RootResultWeb();
		
		if(result.upFlagTrue()){
			
			MDataMap mDataMap = new MDataMap();
			
			mDataMap.put("address_code", getUserCode());
			
			mDataMap.put("address_id", inputParam.getAddress());
			try{
			/*删除地址信息*/
			DbUp.upTable("nc_address").dataDelete("", mDataMap, "");
			}catch(Exception e){
				
				rootResultWeb.inErrorMessage(934205102);
			}
		}
		
		return result;
	}

}

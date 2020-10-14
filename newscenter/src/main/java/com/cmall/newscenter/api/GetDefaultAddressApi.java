package com.cmall.newscenter.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.GetDefaultAddressInput;
import com.cmall.newscenter.model.GetDefaultAddressResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/***
 * 试用商品收货地址API
 * @author shiyz
 *
 */
public class GetDefaultAddressApi extends RootApiForToken<GetDefaultAddressResult, GetDefaultAddressInput> {

	public GetDefaultAddressResult Process(GetDefaultAddressInput inputParam,
			MDataMap mRequestMap) {
		
		GetDefaultAddressResult result = new GetDefaultAddressResult();
		
		if(result.upFlagTrue()){
			
			String sWhere = " address_code = '"+getUserCode()+"'  and app_code = '"+getManageCode()+"' ";
			
			List<MDataMap> mDataMaps = new ArrayList<MDataMap>();
			
			mDataMaps = DbUp.upTable("nc_address").queryAll("", "-address_id", sWhere, new MDataMap());
			
			boolean flag = true;
			
			if(mDataMaps!=null){
				
				for(int i = 0;i<mDataMaps.size();i++){
					
					MDataMap mDataMap = mDataMaps.get(i);
					
					if(mDataMap.get("address_default").equals("1")){
						
						flag = true;
						
						
					}else{
						
						flag = false;
						
					}
					if(flag){
						
                        result.getAddress().setCity(mDataMap.get("address_city"));
						
						result.getAddress().setCounty(mDataMap.get("address_county"));
						
						result.getAddress().setId(mDataMap.get("address_id"));
						
						result.getAddress().setIs_default(Integer.valueOf(mDataMap.get("address_default")));
						
						result.getAddress().setMobile(mDataMap.get("address_mobile"));
						
						result.getAddress().setName(mDataMap.get("address_name"));
						
						result.getAddress().setProvince(mDataMap.get("address_province"));
						
						result.getAddress().setStreet(mDataMap.get("address_street"));
						
						result.getAddress().setZipcode(mDataMap.get("address_postalcode"));
						
						result.getAddress().setCounty_code(mDataMap.get("area_code"));
						
						break;
						
					}else {
						
						MDataMap dzmDataMap = mDataMaps.get(0);
						
						 result.getAddress().setCity(dzmDataMap.get("address_city"));
							
						result.getAddress().setCounty(dzmDataMap.get("address_county"));
							
						result.getAddress().setId(dzmDataMap.get("address_id"));
							
						result.getAddress().setIs_default(Integer.valueOf(dzmDataMap.get("address_default")));
							
						result.getAddress().setMobile(dzmDataMap.get("address_mobile"));
							
						result.getAddress().setName(dzmDataMap.get("address_name"));
							
						result.getAddress().setProvince(dzmDataMap.get("address_province"));
							
						result.getAddress().setStreet(dzmDataMap.get("address_street"));
							
						result.getAddress().setZipcode(dzmDataMap.get("address_postalcode"));
						
						result.getAddress().setCounty_code(dzmDataMap.get("area_code"));
						
					}
					
					
				}
				
			}
			
		}
		
		
		return result;
	}

}

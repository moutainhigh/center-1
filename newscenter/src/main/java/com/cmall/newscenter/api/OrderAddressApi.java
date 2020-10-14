package com.cmall.newscenter.api;

import com.cmall.newscenter.model.Address;
import com.cmall.newscenter.model.OrderAdressInput;
import com.cmall.newscenter.model.OrderAdressResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 订单-配送地址
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class OrderAddressApi extends RootApiForToken<OrderAdressResult, OrderAdressInput> {
/**
 * shiyz
 */
	public OrderAdressResult Process(OrderAdressInput inputParam,
			MDataMap mRequestMap) {
		
		OrderAdressResult result = new OrderAdressResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mDataMap = DbUp.upTable("oc_orderadress").one("order_code",inputParam.getOrder());
			
			if(mDataMap!=null){
				
				/*订单地址*/
				String adressId = mDataMap.get("remark").substring(3);
				
				MDataMap adressMap = DbUp.upTable("nc_address").one("address_id",adressId);
				
				if(adressMap!=null){
					
					Address address = new Address();
					
					address.setCity(adressMap.get("address_city"));
					
					address.setCounty(adressMap.get("address_county"));
					
					address.setId(adressMap.get("address_id"));
					
					address.setIs_default(Integer.valueOf(adressMap.get("address_default")));
					
					address.setMobile(adressMap.get("address_mobile"));
					
					address.setName(adressMap.get("address_name"));
					
					address.setProvince(adressMap.get("address_province"));
					
					address.setStreet(adressMap.get("address_street"));
					
					address.setZipcode(adressMap.get("address_postalcode"));
					
					address.setCounty_code(adressMap.get("area_code"));
					
					result.setAddress(address);
					
				}
				
				
			}
			
//				
//				address.setId(mDataMap.get("remark").substring(0, 13));
//				
//				address.setMobile(mDataMap.get("telephone"));
//				
//				address.setIs_default(1);
//				
//				address.setName(mDataMap.get("receive_person"));
//				
//				address.setZipcode(mDataMap.get("postcode"));
//				
//				/*县级*/
//				String code = mDataMap.get("area_code");
//				/*市级*/
//				String provinceCode = code.substring(0, 4)+"00";
//				/*省级*/
//				String cityCode = code.substring(0, 2)+"0000";
//				
//				MDataMap mwhereMap = DbUp.upTable("sc_gov_district").one("code",code);
//				
//				if(mwhereMap!=null){
//					
//					address.setCounty(mwhereMap.get("name"));
//				}
//				
//				MDataMap mpmap = DbUp.upTable("sc_gov_district").one("code",provinceCode);
//				
//				if(mpmap!=null){
//					
//					address.setCity(mpmap.get("name"));
//				}
//				
//				MDataMap msMap = DbUp.upTable("sc_gov_district").one("code",cityCode);
//				
//				if(msMap!=null){
//					
//					address.setProvince(msMap.get("name"));
//					
//				}
//				
//				address.setStreet(mDataMap.get("address"));
//				
//			}
			
		}
		return result;
	}

}

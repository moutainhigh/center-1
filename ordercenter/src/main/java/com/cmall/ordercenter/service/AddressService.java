package com.cmall.ordercenter.service;

import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.model.AddressInformation;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 收货人地址信息
 * @author wz
 *
 */
public class AddressService extends BaseClass{
	
	/**
	 * 
	 * @param address_id  收货人地址id
	 * @param address_code   用户编号
	 * @return
	 */
	public AddressInformation getAddressOne(String address_id, String address_code,String... params){
		
		AddressInformation addressInformation = new AddressInformation();
		MDataMap map = new MDataMap();
		
		
		Map<String, Object> addressMap = null;
		
		if(address_id!=null && !"".equals(address_id)){
			map.put("address_id", address_id);
			addressMap = DbUp.upTable("nc_address").dataSqlOne("select * from nc_address where address_id=:address_id", map);
			
			if(addressMap!=null && !"".equals(addressMap) && addressMap.size()>0){
				addressInformation.setAddress_city(String.valueOf(addressMap.get("address_city")));
				addressInformation.setAddress_code(String.valueOf(addressMap.get("address_code")));
				addressInformation.setAddress_county(String.valueOf(addressMap.get("address_county")));
				addressInformation.setAddress_default(String.valueOf(addressMap.get("address_default")));
				addressInformation.setAddress_id(String.valueOf(addressMap.get("address_id")));
				addressInformation.setAddress_mobile(String.valueOf(addressMap.get("address_mobile")));
				
				addressInformation.setAddress_name(String.valueOf(addressMap.get("address_name")));
				addressInformation.setAddress_postalcode(String.valueOf(addressMap.get("address_postalcode")));
				addressInformation.setAddress_province(String.valueOf(addressMap.get("address_province")));
				addressInformation.setAddress_street(String.valueOf(addressMap.get("address_street")));
				addressInformation.setApp_code(String.valueOf(addressMap.get("app_code")));
				addressInformation.setArea_code(String.valueOf(addressMap.get("area_code")));
				addressInformation.setSort_num(String.valueOf(addressMap.get("sort_num")));
			}
		}else{
			map.put("address_code", address_code);
			map.put("app_code", params[0]);
			// 优先取设置的默认地址，其次是最后一次修改的地址
			addressMap = DbUp.upTable("nc_address").dataSqlOne("select * from nc_address where address_code=:address_code and app_code=:app_code ORDER BY address_default desc,update_time desc", map);
			
			if(addressMap!=null && !"".equals(addressMap) && addressMap.size()>0){
				addressInformation.setAddress_city(String.valueOf(addressMap.get("address_city")));
				addressInformation.setAddress_code(String.valueOf(addressMap.get("address_code")));
				addressInformation.setAddress_county(String.valueOf(addressMap.get("address_county")));
				addressInformation.setAddress_default(String.valueOf(addressMap.get("address_default")));
				addressInformation.setAddress_id(String.valueOf(addressMap.get("address_id")));
				addressInformation.setAddress_mobile(String.valueOf(addressMap.get("address_mobile")));
				
				addressInformation.setAddress_name(String.valueOf(addressMap.get("address_name")));
				addressInformation.setAddress_postalcode(String.valueOf(addressMap.get("address_postalcode")));
				addressInformation.setAddress_province(String.valueOf(addressMap.get("address_province")));
				addressInformation.setAddress_street(String.valueOf(addressMap.get("address_street")));
				addressInformation.setApp_code(String.valueOf(addressMap.get("app_code")));
				addressInformation.setArea_code(String.valueOf(addressMap.get("area_code")));
				addressInformation.setSort_num(String.valueOf(addressMap.get("sort_num")));
			}
			
		}
		
		
		if(addressInformation!=null&&StringUtils.isNotBlank(addressInformation.getAddress_code())&&StringUtils.isNotBlank(addressInformation.getAddress_id())){
			String idNumber = new MemberAuthInfoSupport().getAesIdNumber(addressInformation.getAddress_code(), addressInformation.getAddress_id());
		
			addressInformation.setIdNumber(idNumber);
		}
		return addressInformation;
	}	
	
}

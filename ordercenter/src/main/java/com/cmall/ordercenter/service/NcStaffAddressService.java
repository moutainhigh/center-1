package com.cmall.ordercenter.service;

import java.util.Map;

import org.apache.commons.collections.map.HashedMap;

import com.cmall.ordercenter.model.AddressInformation;
import com.cmall.ordercenter.model.NcStaffAddress;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 内部员工收货人地址信息
 * @author wz
 *
 */
public class NcStaffAddressService {
	/**
	 * 查询内部员工所有信息
	 * @param address_id
	 * @return
	 */
//	public NcStaffAddress nvStaffAddressValue(String address_id){
//		Map<String, Object>  map = new HashedMap();
//		NcStaffAddress ncStaffAddress = new NcStaffAddress();
//		
//		if("".equals(address_id) || address_id==null){
//			//获取员工默认地址
//			map = DbUp.upTable("nc_staff_address").dataSqlOne("select * from nc_staff_address where address_default=:address_default", new MDataMap("address_default","449746250001"));
//		}else{
//			map = DbUp.upTable("nc_staff_address").dataSqlOne("select * from nc_staff_address where address_id=:address_id", new MDataMap("address_id",address_id));
//		}
//		
//		if(map!=null && !"".equals(map) && map.size()>0){
//			ncStaffAddress.setAddress_default(String.valueOf(map.get("address_default")));
//			ncStaffAddress.setAddress_mobile(String.valueOf(map.get("address_mobile")));
//			ncStaffAddress.setAddress_name(String.valueOf(map.get("address_name")));
//			ncStaffAddress.setAddress_postalcode(String.valueOf(map.get("address_postalcode")));
//			ncStaffAddress.setAddress_street(String.valueOf(map.get("address_street")));
//			ncStaffAddress.setCreate_time(String.valueOf(map.get("create_time")));
//			ncStaffAddress.setEmail(String.valueOf(map.get("email")));
//			ncStaffAddress.setPrice(String.valueOf(map.get("price")));
//			ncStaffAddress.setUpdate_time(String.valueOf(map.get("update_time")));
//		}
//		return ncStaffAddress;
//	}
	
	
	public AddressInformation nvStaffAddressValue(String address_id){
		Map<String, Object>  map = new HashedMap();
		AddressInformation ncStaffAddress = new AddressInformation();
		
		if("".equals(address_id) || address_id==null){
			//获取员工默认地址
			map = DbUp.upTable("nc_staff_address").dataSqlOne("select * from nc_staff_address where address_default=:address_default", new MDataMap("address_default","449746250001"));
		}else{
			map = DbUp.upTable("nc_staff_address").dataSqlOne("select * from nc_staff_address where address_id=:address_id", new MDataMap("address_id",address_id));
		}
		
		if(map!=null && !"".equals(map) && map.size()>0){
			ncStaffAddress.setAddress_default(String.valueOf(map.get("address_default")));
			ncStaffAddress.setAddress_postalcode(String.valueOf(map.get("address_postalcode")));
			ncStaffAddress.setAddress_street(String.valueOf(map.get("address_street")));
			ncStaffAddress.setCreate_time(String.valueOf(map.get("create_time")));
			ncStaffAddress.setPrice(String.valueOf(map.get("price")));
			ncStaffAddress.setUpdate_time(String.valueOf(map.get("update_time")));
			ncStaffAddress.setAddress_default(String.valueOf(map.get("address_default")));
			ncStaffAddress.setArea_code(String.valueOf(map.get("area_code")));
			ncStaffAddress.setApp_code(String.valueOf(map.get("app_code")));
		}
		return ncStaffAddress;
	}
	
	
	
	
	
}

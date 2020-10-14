package com.cmall.newscenter.api;

import java.util.List;

import com.cmall.newscenter.model.Address;
import com.cmall.newscenter.model.AddressAddInput;
import com.cmall.newscenter.model.AddressAddResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 添加地址
 * @author shiyz
 * date 2014-08-04
 * @version 1.0
 */

public class AddressAddApi extends RootApiForToken<AddressAddResult, AddressAddInput> {

	public AddressAddResult Process(AddressAddInput inputParam,
			MDataMap mRequestMap) {

		AddressAddResult result = new AddressAddResult();
		
		if(result.upFlagTrue()){
			
			MDataMap mDataMap = new MDataMap();
			
			mDataMap.put("address_city", inputParam.getAddress().getCity());
			
			mDataMap.put("address_county", inputParam.getAddress().getCounty());
			
			/*地址编号*/
			String address_id = WebHelper.upCode("DZ");
			
			mDataMap.put("address_id", address_id);
			
			int is_default = inputParam.getAddress().getIs_default();
			
			/*判断前台传入地址是否为默认，如果是修改以前数据库中的默认地址*/
			if(is_default!=0){
				
				MDataMap mWhereMap = new MDataMap();
				
				mWhereMap.put("address_code", getUserCode());
				
				mWhereMap.put("address_default", "1");
				
				/*查询用户默认地址*/
				List<MDataMap> list = DbUp.upTable("nc_address").queryAll("", "", "", mWhereMap);
				
				if(list.size()>0){
					
					for(MDataMap mDataMap2:list){
						
						mDataMap2.put("address_default", "");
						
						
						/*更新原有用户默认地址*/
						DbUp.upTable("nc_address").dataUpdate(mDataMap2, "", "address_id");
						
					}
					
				}
				
			}
			/*默认地址*/
			mDataMap.put("address_default", String.valueOf(is_default));
			
			/*用户编号*/
			mDataMap.put("address_code", getUserCode());
			
			mDataMap.put("address_mobile", inputParam.getAddress().getMobile());
			
			mDataMap.put("address_name", inputParam.getAddress().getName());
			
			mDataMap.put("address_province", inputParam.getAddress().getProvince());
			
			mDataMap.put("address_street", inputParam.getAddress().getStreet());
			
			mDataMap.put("address_postalcode", inputParam.getAddress().getZipcode());
			
			mDataMap.put("address_code", getUserCode());
			
			mDataMap.put("area_code", inputParam.getAddress().getCounty_code());
			
			mDataMap.put("app_code", getManageCode());
			/*将地址信息放入数据库中*/
			DbUp.upTable("nc_address").dataInsert(mDataMap);
			
			
			MDataMap mqueryMap = new MDataMap();
			
			mqueryMap.put("address_code", getUserCode());
			mqueryMap.put("app_code", getManageCode());
			/*查询用户下所有地址*/
			List<MDataMap> listMaps = DbUp.upTable("nc_address").queryAll("", "-sort_num", "", mqueryMap);
			
			if(listMaps!=null){
				
				for(MDataMap mDataMap2:listMaps){
					
					Address address = new Address();
					
				    /*城市名称*/
					address.setCity(mDataMap2.get("address_city"));
					
					/*县区名称*/
					address.setCounty(mDataMap2.get("address_county"));
					
					/*地址ID*/
					address.setId(mDataMap2.get("address_id"));
					
					/*是否默认*/
					address.setIs_default(Integer.valueOf(mDataMap2.get("address_default")));
					
					/*手机号*/
					address.setMobile(mDataMap2.get("address_mobile"));
					
					/*名称*/
					address.setName(mDataMap2.get("address_name"));
					
					/*省名称*/
					address.setProvince(mDataMap2.get("address_province"));
					
					/*详细地址*/
					address.setStreet(mDataMap2.get("address_street"));
					
					/*邮政编码*/
					address.setZipcode(mDataMap2.get("address_postalcode"));
					
					address.setCounty_code(mDataMap2.get("area_code"));
					
					result.getAddress().add(address);
					
				}
				
			}			
			
		}
		
		return result;
	}

}

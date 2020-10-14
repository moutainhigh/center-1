package com.cmall.newscenter.api;


import com.cmall.newscenter.model.Address;
import com.cmall.newscenter.model.AddressListInput;
import com.cmall.newscenter.model.AddressListResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 地址列表类
 * @author shiyz	
 * date 2014-08-04
 * @version 1.0
 */
public class AddressListApi extends RootApiForToken<AddressListResult, AddressListInput> {

	public AddressListResult Process(AddressListInput inputParam,
			MDataMap mRequestMap) {
		
		AddressListResult result = new AddressListResult();
		
		MDataMap mWhereMap = new MDataMap();
		
		/*将当前用户编号放入map中*/
		mWhereMap.put("address_code", getUserCode());
		mWhereMap.put("app_code", getManageCode());
		if(result.upFlagTrue()){
			
			MPageData mPageData = new MPageData();
			
			/*查询用户下所有地址列表*/
			mPageData = DataPaging.upPageData("nc_address", "", "-sort_num", mWhereMap, inputParam.getPaging());
			
			if(mPageData.getListData().size()!=0){
			
			for(MDataMap mDataMap : mPageData.getListData()){
				
				Address address = new Address();
				
			    /*城市名称*/
				address.setCity(mDataMap.get("address_city"));
				
				/*县区名称*/
				address.setCounty(mDataMap.get("address_county"));
				
				/*地址ID*/
				address.setId(mDataMap.get("address_id"));
				
				/*是否默认*/
				address.setIs_default(Integer.valueOf(mDataMap.get("address_default")));
				
				/*手机号*/
				address.setMobile(mDataMap.get("address_mobile"));
				
				/*名称*/
				address.setName(mDataMap.get("address_name"));
				
				/*省名称*/
				address.setProvince(mDataMap.get("address_province"));
				
				/*详细地址*/
				address.setStreet(mDataMap.get("address_street"));
				
				/*邮政编码*/
				address.setZipcode(mDataMap.get("address_postalcode"));
				
				address.setCounty_code(mDataMap.get("area_code"));
				
				
				result.getAddress().add(address);
			}
			
			result.setPaged(mPageData.getPageResults());
		}
			
		}
		
		
		return result;
	}

}

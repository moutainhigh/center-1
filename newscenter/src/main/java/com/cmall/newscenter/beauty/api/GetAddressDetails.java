package com.cmall.newscenter.beauty.api;

import java.util.List;

import com.cmall.newscenter.beauty.model.BeautyAddress;
import com.cmall.newscenter.beauty.model.GetAddressDetailsIput;
import com.cmall.newscenter.beauty.model.GetAddressDetailsResult;
import com.cmall.newscenter.beauty.model.GetAddressInput;
import com.cmall.newscenter.beauty.model.GetAddressResult;
import com.cmall.newscenter.model.MPageData;
import com.cmall.newscenter.model.PageOption;
import com.cmall.newscenter.model.PageResults;
import com.cmall.newscenter.service.MemberAuthInfoService;
import com.cmall.newscenter.util.DataPaging;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForToken;

/**
 * 惠美丽—获取收货地址Api（默认地址排在第一 其他按更新时间排序 ）
 * 
 * @author yangrong date: 2014-08-20
 * @version1.0
 */
public class GetAddressDetails extends RootApiForToken<GetAddressDetailsResult, GetAddressDetailsIput> {

	public GetAddressDetailsResult Process(GetAddressDetailsIput inputParam,
			MDataMap mRequestMap) {

		GetAddressDetailsResult result = new GetAddressDetailsResult();
		

		// 设置相关信息
		if (result.upFlagTrue()) {
			// 查询地址信息
			MDataMap map = DbUp.upTable("nc_address").one("address_id",inputParam.getAddress_id(), "address_code", getUserCode());
			
			if (map != null && !map.isEmpty()) {
				BeautyAddress address = new BeautyAddress(); 

				address.setPostcode(map.get("address_postalcode"));
				address.setProvinces(map.get("address_province"));
				address.setId(map.get("address_id"));
				address.setName(map.get("address_name"));
				address.setMobile(map.get("address_mobile"));
				address.setStreet(map.get("address_street"));
				address.setAreaCode(map.get("area_code"));
				address.setEmail(map.get("email"));
				
				result.setAdress(address);
			} 
			
		}
		return result;
	}

}

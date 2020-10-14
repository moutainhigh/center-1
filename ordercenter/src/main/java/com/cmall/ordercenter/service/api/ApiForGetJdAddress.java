package com.cmall.ordercenter.service.api;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.cmall.ordercenter.model.api.GetJdAddressInput;
import com.cmall.ordercenter.model.api.GetJdAddressResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 获取京东收货地址
 */
public class ApiForGetJdAddress extends RootApi<GetJdAddressResult, GetJdAddressInput> {

	@Override
	public GetJdAddressResult Process(GetJdAddressInput inputParam, MDataMap mRequestMap) {
		GetJdAddressResult result = new GetJdAddressResult();
		MDataMap whereMap = new MDataMap();
		if(StringUtils.isNotBlank(inputParam.getCode())) {
			whereMap.put("p_code", inputParam.getCode());
		} else {
			whereMap.put("code_lvl", "1");
		}
		whereMap.put("use_yn", "Y");// wangmeng 5.6.7  2020-08-06
		List<MDataMap> mapList = DbUp.upTable("sc_jingdong_address").queryAll("code,name,code_lvl", "", "", whereMap);
		/*if((mapList==null || mapList.size()==0) && StringUtils.isBlank(inputParam.getCode())) {
			MDataMap whereToMap = new MDataMap();
			if(StringUtils.isNotBlank(inputParam.getCode())) {
				whereToMap.put("p_code", inputParam.getCode());
			} else {
				whereToMap.put("code_lvl", "1");
			}
			mapList = DbUp.upTable("sc_jingdong_address").queryAll("code,name,code_lvl", "", "", whereToMap);
		}*/
		System.out.print("ApiForGetJdAddress--------->>>>"+whereMap.toString());
		GetJdAddressResult.Address item;
		for(MDataMap map : mapList) {
			item = new GetJdAddressResult.Address();
			item.setName(map.get("name"));
			item.setCode(map.get("code"));
			item.setLevel(NumberUtils.toInt(map.get("code_lvl")));
			result.getAddressList().add(item);
		}
		
		return result;
	}
	
}
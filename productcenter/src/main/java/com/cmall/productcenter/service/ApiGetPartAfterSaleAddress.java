package com.cmall.productcenter.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.cmall.productcenter.model.ApiGetPartAfterSaleAddressInput;
import com.cmall.productcenter.model.ApiGetPartAfterSaleAddressResult;
import com.cmall.productcenter.model.OcAddressinfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 获取商户下的售后地址
 * @author lgx
 *
 */
public class ApiGetPartAfterSaleAddress extends RootApi<ApiGetPartAfterSaleAddressResult, ApiGetPartAfterSaleAddressInput> {

	
	public ApiGetPartAfterSaleAddressResult Process(ApiGetPartAfterSaleAddressInput inputParam, MDataMap mRequestMap) {
		ApiGetPartAfterSaleAddressResult result = new ApiGetPartAfterSaleAddressResult();
		String afterSaleAddressUid = inputParam.getAfterSaleAddressUid();
		String manageCode = inputParam.getManageCode();
		
		OcAddressinfo o = null;
		List<OcAddressinfo> addressList = new ArrayList<OcAddressinfo>();
		String sSql = "";
		if(null != afterSaleAddressUid && !"".equals(afterSaleAddressUid)) {
			// 查询其余售后地址
			sSql = "SELECT * FROM oc_address_info WHERE small_seller_code = '"+manageCode+"' AND uid <> '"+afterSaleAddressUid+"'";
		}else {
			// 查询所有售后地址
			sSql = "SELECT * FROM oc_address_info WHERE small_seller_code = '"+manageCode+"'";
		}
		List<Map<String, Object>> List = DbUp.upTable("oc_address_info").dataSqlList(sSql, new MDataMap());
		if(List != null && List.size() > 0) {
			for (Map<String, Object> map : List) {
				o = new OcAddressinfo();
				o.setUid((String) map.get("uid"));
				if(null == map.get("after_sale_address_name") || "".equals(map.get("after_sale_address_name"))) {
					// 售后地址名称为空,则展示售后地址信息
					o.setAfterSaleAddresName((String) map.get("after_sale_address"));
				}else {					
					o.setAfterSaleAddresName((String) map.get("after_sale_address_name"));
				}
				o.setSmallSellerCode((String) map.get("small_seller_code"));
				
				addressList.add(o);
			}
			result.setList(addressList);
		}
		return result;
	}

}

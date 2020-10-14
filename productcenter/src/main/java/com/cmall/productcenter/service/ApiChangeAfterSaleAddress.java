package com.cmall.productcenter.service;

import java.util.Map;

import com.cmall.productcenter.model.ApiChangeAfterSaleAddressInput;
import com.cmall.productcenter.model.ApiChangeAfterSaleAddressResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;
/**
 * 变更售后地址
 * @author lgx
 *
 */
public class ApiChangeAfterSaleAddress extends RootApi<ApiChangeAfterSaleAddressResult, ApiChangeAfterSaleAddressInput> {

	
	public ApiChangeAfterSaleAddressResult Process(ApiChangeAfterSaleAddressInput inputParam, MDataMap mRequestMap) {
		ApiChangeAfterSaleAddressResult result = new ApiChangeAfterSaleAddressResult();
		String beforeUid = inputParam.getBeforeUid();
		String changeUid = inputParam.getChangeUid();
		String manageCode = inputParam.getManageCode();
		String productUid = inputParam.getProductUid();
		
		String sSql = "SELECT * FROM oc_address_info WHERE small_seller_code = '"+manageCode+"' AND uid = '"+changeUid+"'";
		Map<String, Object> dataSqlOne = DbUp.upTable("oc_address_info").dataSqlOne(sSql, new MDataMap());
		if(dataSqlOne == null || dataSqlOne.size() <= 0) {
			result.setResultCode(-1);
			result.setResultMessage("对不起,您选择的售后地址不存在,请重新选择!");
			return result;
		}else {
			int count = 0;
			int dataExec = 0;
			if(null == productUid || "".equals(productUid)) {
				// 多个商品售后地址整体变更
				count = DbUp.upTable("pc_productinfo").count("after_sale_address_uid",beforeUid,"small_seller_code",manageCode);
				if(count > 0) {						
					String sql2 = "UPDATE pc_productinfo SET after_sale_address_uid = '"+changeUid+"' WHERE small_seller_code='"+manageCode+"' AND after_sale_address_uid = '"+beforeUid+"'";
					dataExec = DbUp.upTable("pc_productinfo").dataExec(sql2, new MDataMap());
				}else {
					result.setResultCode(-1);
					result.setResultMessage("没有可以变更地址的商品!");
					return result;
				}
			}else {
				// 单个商品售后地址变更
				count = DbUp.upTable("pc_productinfo").count("after_sale_address_uid",beforeUid,"small_seller_code",manageCode,"uid",productUid);
				if(count > 0) {						
					String sql3 = "UPDATE pc_productinfo SET after_sale_address_uid = '"+changeUid+"' WHERE uid = '"+productUid+"' AND small_seller_code='"+manageCode+"' AND after_sale_address_uid = '"+beforeUid+"'";
					dataExec = DbUp.upTable("pc_productinfo").dataExec(sql3, new MDataMap());
				}else {
					result.setResultCode(-1);
					result.setResultMessage("没有可以变更地址的商品!");
					return result;
				}
			}
			if(dataExec > 0) {				
				result.setResultMessage("变更售后地址成功!");
			}else {
				result.setResultCode(-1);
				result.setResultMessage("变更售后地址失败!");
				return result;
			}
		}
		
		return result;
	}

}

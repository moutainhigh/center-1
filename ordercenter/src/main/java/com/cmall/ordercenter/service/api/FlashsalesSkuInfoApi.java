package com.cmall.ordercenter.service.api;

import com.cmall.ordercenter.model.api.FlashsalesSkuInfoInput;
import com.cmall.ordercenter.model.api.FlashsalesSkuInfoResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 添加闪购和sku关联关系
 * @author jl
 *
 */
public class FlashsalesSkuInfoApi extends RootApi<FlashsalesSkuInfoResult,FlashsalesSkuInfoInput> {

	public FlashsalesSkuInfoResult Process(FlashsalesSkuInfoInput inputParam,MDataMap mRequestMap) {
		FlashsalesSkuInfoResult result = null;
		try {
			result = new FlashsalesSkuInfoResult();
			MDataMap minsert = new MDataMap();
			minsert.put("activity_code", inputParam.getActivity_code());
			String [] sku_codes = inputParam.getSku_code().split(",");
			for(int i=0;i<sku_codes.length;i++){
				String sku_code = sku_codes[i];
				
				if(sku_code!=null&&!"".equals(sku_code)){
					minsert.put("sku_code", sku_code);
					if(minsert.containsKey("uid")){
						minsert.remove("uid");
					}
					DbUp.upTable("oc_flashsales_skuInfo").dataInsert(minsert);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
		return result;
	}
}


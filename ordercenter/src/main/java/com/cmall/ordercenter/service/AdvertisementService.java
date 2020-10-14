package com.cmall.ordercenter.service;

import com.cmall.ordercenter.model.PayResult;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 广告
 * 
 * @author wz
 * 
 */
public class AdvertisementService extends BaseClass {
	/**
	 * 接收安沃广告信息
	 * @param insertDatamap
	 * @return
	 */
	public PayResult anwoInsert(MDataMap insertDatamap) {
		
		PayResult payResult = new PayResult();

		if (insertDatamap != null && !"".equals(insertDatamap)
				&& insertDatamap.size() > 0) {
			MDataMap map = new MDataMap();
			map.put("adalias", insertDatamap.get("adalias"));
			map.put("idfa", insertDatamap.get("idfa"));
			map.put("mac", insertDatamap.get("mac"));
			map.put("create_time", FormatHelper.upDateTime());
			map.put("param_value", insertDatamap.get("param_value"));
			map.put("status", "0");   //未匹配
			
			// 插入日志流水
			if (payResult.upFlagTrue()) {
				DbUp.upTable("lc_input_ad").dataInsert(map); // 插入支付日志流水信息
			}
		}else{
			payResult.inErrorMessage(939301300);
		}
		return payResult;
	}
	
	public static void main(String[] args) {
		MDataMap insertDatamap = new MDataMap();
		insertDatamap.put("adalias", "惠家有");
		insertDatamap.put("idfa", "111222333");
		insertDatamap.put("uniqid", "333222111");
		insertDatamap.put("param_value", "惠家有&dsadsa&sdadsa");
		
		AdvertisementService a = new AdvertisementService();
		a.anwoInsert(insertDatamap);
	}
}

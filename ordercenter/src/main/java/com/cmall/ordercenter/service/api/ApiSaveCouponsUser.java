package com.cmall.ordercenter.service.api;

import java.text.ParseException;

import com.cmall.ordercenter.model.api.ApiSaveCouponsUserInput;
import com.cmall.ordercenter.service.CouponsService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/** 
* @ClassName: ApiSaveCouponsUser 
* @Description: 根据优惠码领取优惠券
* @author 张海生
* @date 2015-5-22 下午2:56:31 
*  
*/
public class ApiSaveCouponsUser extends
		RootApi<RootResultWeb, ApiSaveCouponsUserInput> {

	public RootResultWeb Process(ApiSaveCouponsUserInput input,
			MDataMap mRequestMap) {
		RootResultWeb result = new RootResultWeb();
		CouponsService couponService = new CouponsService();
		try {
			result = couponService.saveCouponsUser(input.getMobile());
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//把手机号插入到待发放记录
		return result;
	}
}
package com.cmall.newscenter.api;

import com.cmall.newscenter.model.MobilePhoneCodeInput;
import com.cmall.newscenter.model.MobilePhoneCodeResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 用户 - 获取手机验证码api
 * @author shiyz
 * date 2014-7-18
 * @version 1.0
 */
public class MobilePhoneCodeApi extends RootApiForManage<MobilePhoneCodeResult, MobilePhoneCodeInput> {

	public MobilePhoneCodeResult Process(MobilePhoneCodeInput inputParam,
			MDataMap mRequestMap) {
		MobilePhoneCodeResult result = new MobilePhoneCodeResult();
		if(result.upFlagTrue()){
			
			result.setYzm("925631");
			
		}
		return result;
	}

}

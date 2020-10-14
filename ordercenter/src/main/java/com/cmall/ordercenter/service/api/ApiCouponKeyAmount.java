package com.cmall.ordercenter.service.api;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.model.api.ApiCouponKeyAmountInput;
import com.cmall.ordercenter.model.api.ApiCouponKeyAmountResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webapi.RootApiForVersion;

public class ApiCouponKeyAmount extends RootApiForVersion<ApiCouponKeyAmountResult, ApiCouponKeyAmountInput> {

	@Override
	public ApiCouponKeyAmountResult Process(ApiCouponKeyAmountInput inputParam, MDataMap mRequestMap) {
		ApiCouponKeyAmountResult result = new ApiCouponKeyAmountResult();
		String cdkey = inputParam.getCdkey();
		String activityCode = inputParam.getActivityCode();
		if(StringUtils.isEmpty(cdkey)) {
			result.setResultCode(939303112);
			result.setResultMessage(bInfo(939303112));
			return result;
		}
		if(StringUtils.isEmpty(activityCode)) {
			result.setResultCode(939303113);
			result.setResultMessage(bInfo(939303113));
			return result;
		}
		//不考虑单帐户或多帐户使用
		MDataMap cdkeyMap = DbUp.upTable("oc_coupon_cdkey").oneWhere("use_people", "", "", "cdkey", cdkey, "activity_code", activityCode);
		if(cdkeyMap != null && !cdkeyMap.isEmpty()) {
			int people = Integer.parseInt(cdkeyMap.get("use_people"));
			int exchanged = 0;
			String sSql = "SELECT distinct member_code FROM oc_coupon_info where cdkey=:cdkey and activity_code=:activityCode";
			List<Map<String, Object>> ciList = DbUp.upTable("oc_coupon_info").dataSqlList(sSql, new MDataMap("cdkey",cdkey, "activityCode",activityCode));
			if (ciList != null) {
				exchanged = ciList.size();
			}
			result.setAmount(people);
			result.setExchanged(exchanged);
			result.setRemain(people - exchanged); 
		}
		return result;
	}

}

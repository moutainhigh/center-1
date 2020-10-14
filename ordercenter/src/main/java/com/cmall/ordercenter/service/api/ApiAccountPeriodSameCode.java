package com.cmall.ordercenter.service.api;

import com.cmall.ordercenter.service.api.ApiAccountPeriodSameCode.ApiAccountPeriodSameCodeInput;
import com.cmall.ordercenter.service.api.ApiAccountPeriodSameCode.ApiAccountPeriodSameCodeResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapcom.topapi.RootInput;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 检查结算周期配置编号是否重复
 * @author zht
 *
 */
public class ApiAccountPeriodSameCode extends
	RootApi<ApiAccountPeriodSameCodeResult, ApiAccountPeriodSameCodeInput> {
	
	public static class ApiAccountPeriodSameCodeInput extends RootInput {
		private String code;

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}
	}

	public static class ApiAccountPeriodSameCodeResult extends RootResult {
		private int samed;

		public int getSamed() {
			return samed;
		}

		public void setSamed(int samed) {
			this.samed = samed;
		}
	}
	

	@Override
	public ApiAccountPeriodSameCodeResult Process(ApiAccountPeriodSameCodeInput input, MDataMap mRequestMap) {
		// TODO Auto-generated method stub
		ApiAccountPeriodSameCodeResult result = new ApiAccountPeriodSameCodeResult();
		String code = input.getCode();
		int count = DbUp.upTable("oc_bill_account_period").dataCount("code='" + code + "'", null);
		if(count > 0)
			result.setSamed(1);	//重复
		return result;
	} 

}

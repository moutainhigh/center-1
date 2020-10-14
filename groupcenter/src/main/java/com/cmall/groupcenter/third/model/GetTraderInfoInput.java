package com.cmall.groupcenter.third.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 微公社商户后台-获取商户信息的输入参数
 * @author huangsi
 */
public class GetTraderInfoInput extends RootInput{
	
	@ZapcomApi(value="商户编号",remark="商户编号",require = 1)
	private String trader_code="";


	public String getTrader_code() {
		return trader_code;
	}

	public void setTrader_code(String trader_code) {
		this.trader_code = trader_code;
	}
	
}

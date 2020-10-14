package com.cmall.ordercenter.service.api;

import com.cmall.ordercenter.model.api.ApiAlipayMoveProcessOrderInput;
import com.cmall.ordercenter.model.api.ApiAlipayMoveProcessOrderResult;
import com.cmall.ordercenter.service.ApiAlipayMoveProcessService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForToken;
/**
 * 支付宝移动支付(后台)
 * @author wz
 *
 */
public class ApiAlipayMoveProcess extends RootApiForToken<ApiAlipayMoveProcessOrderResult, ApiAlipayMoveProcessOrderInput>{

	public ApiAlipayMoveProcessOrderResult Process(
			ApiAlipayMoveProcessOrderInput inputParam, MDataMap mRequestMap) {
		
		ApiAlipayMoveProcessService apiAlipayMoveProcessService = new ApiAlipayMoveProcessService();
		ApiAlipayMoveProcessOrderResult result = new ApiAlipayMoveProcessOrderResult();
		
		String out_trade_no = inputParam.getOut_trade_no();  //订单编号
		String body = inputParam.getBody();   //商品详情
		String subject = inputParam.getSubject();   //商品名称
		
		result = apiAlipayMoveProcessService.alipayMoveProcessRequest(out_trade_no, body,subject);
		return result;
	}
}

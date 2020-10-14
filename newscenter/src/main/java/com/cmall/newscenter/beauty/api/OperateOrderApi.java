package com.cmall.newscenter.beauty.api;

import com.cmall.groupcenter.support.GroupReckonSupport;
import com.cmall.newscenter.beauty.model.OperateOrderInput;
import com.cmall.newscenter.beauty.model.OperateOrderResult;
import com.cmall.ordercenter.model.api.ApiOperateInput;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webapi.RootApiForManage;
/**
 * 确认收货-API
 * @author yangrong
 * date: 2014-10-09
 * @version1.0
 */
public class OperateOrderApi extends RootApiForManage<OperateOrderResult, OperateOrderInput> {
	

	public OperateOrderResult Process(OperateOrderInput inputParam,
			MDataMap mRequestMap) {
		
		OperateOrderResult result = new OperateOrderResult();
		
		// 设置相关信息
		if (result.upFlagTrue()) {
			
			OrderService orderService = new OrderService();
			
			ApiOperateInput input = new ApiOperateInput();
			//3为确认收货
			input.setType(3);
			
			input.setOrderCode(inputParam.getOrderCode());
			
			RootResult rootResult = orderService.operate(input,"");
			
			if(rootResult.getResultCode()==1){
				
				GroupReckonSupport  groupReckonSupport = new GroupReckonSupport();
				
				groupReckonSupport.initByErpOrder(inputParam.getOrderCode(), FormatHelper.upDateTime());
			}
			
			result.setResultCode(rootResult.getResultCode());
			
			result.setResultMessage(rootResult.getResultMessage());
			
		}
		
		return result;
	}
}





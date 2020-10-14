package com.cmall.groupcenter.account.api;

import com.cmall.groupcenter.account.model.OrderRebateInfo;
import com.cmall.groupcenter.account.model.OrderRebateResult;
import com.cmall.groupcenter.service.OrderRebateService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.webapi.RootApiForManage;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 创建订单返利接口
 * @author dyc
 * */
public class ApiOrderRebate extends RootApiForManage<OrderRebateResult, OrderRebateInfo> {

	public OrderRebateResult Process(OrderRebateInfo inputParam,
			MDataMap mRequestMap) {
		
		return new OrderRebateService().saveOrderStatusInfo(inputParam,getManageCode());
	}

}

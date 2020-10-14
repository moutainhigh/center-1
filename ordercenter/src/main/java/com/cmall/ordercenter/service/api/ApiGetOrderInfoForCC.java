package com.cmall.ordercenter.service.api;

import java.util.List;

import com.cmall.ordercenter.model.OrderInfoForCC;
import com.cmall.ordercenter.model.api.ApiGetOrdersForCCInput;
import com.cmall.ordercenter.model.api.ApiGetOrdersForCCResult;
import com.cmall.ordercenter.service.OrderInfoServiceForCC;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;

/**
 * 客服系统订单详情查询 
 * 
 * @author zhaoxq
 */
public class ApiGetOrderInfoForCC extends RootApi<ApiGetOrdersForCCResult,ApiGetOrdersForCCInput> {

	public ApiGetOrdersForCCResult Process(ApiGetOrdersForCCInput api, MDataMap mRequestMap) {
		ApiGetOrdersForCCResult result = new ApiGetOrdersForCCResult();
		if(api == null){
			result.setResultMessage(bInfo(941901019));
			result.setResultCode(941901019);
			return result;
		}
		//订单编号 
		String orderCode = api.getOrderCode();
		//外部订单编号
		String outOrderCode = api.getOutOrderCode();
		//支付订单编号
		String bigOrderCode = api.getBigOrderCode();
		//订单状态
		String orderStatus = api.getOrderStatus();
		//订单辅助状态
		String orderStatusExt = api.getOrderStatusExt();
		//创建时间开始
		String createTimeStart = api.getCreateTimeStart();
		//创建时间结束
		String createTimeEnd = api.getCreateTimeEnd();
		//注册手机号
		String registerMobile = api.getRegisterMobile();
		//收货人
		String receivePerson = api.getReceivePerson();
		//收货人手机号
		String mobilePhone = api.getMobilephone();
		//收货地址
		String address = api.getAddress();
		//商品名称
		String productName = api.getProductName();
		//物流单号
		String waybill = api.getWaybill();

			OrderInfoServiceForCC os = new OrderInfoServiceForCC();			
		try {			
			List<OrderInfoForCC> list = os.getOrderListForCC(orderCode,outOrderCode,bigOrderCode,
					orderStatus,orderStatusExt,createTimeStart,createTimeEnd,
					registerMobile,receivePerson,mobilePhone,address,
					productName,waybill);
			result.setList(list);
			result.setResultCode(1);
			
		} catch (Exception e) {
			result.setResultCode(939301033);
			result.setResultMessage(bInfo(939301033));
		}
		return result;
	}
}
package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.account.model.GetOrderDetailListResult;
import com.cmall.groupcenter.account.model.GetPayOrderListInput;
import com.cmall.groupcenter.account.model.OrderInfo;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiGetOrderDetailList extends RootApi<GetOrderDetailListResult, GetPayOrderListInput>{

	public GetOrderDetailListResult Process(GetPayOrderListInput inputParam,
			MDataMap mRequestMap) {
		GetOrderDetailListResult getOrderDetailListResult=new GetOrderDetailListResult();
		List<OrderInfo> orderList=new ArrayList<OrderInfo>();
		String mobile=inputParam.getMobile();
		MDataMap memberDataMap=DbUp.upTable("mc_login_info").one("login_name",mobile);
		
		
		
		if(memberDataMap==null){
			getOrderDetailListResult.inErrorMessage(915805218);
			return getOrderDetailListResult;
		}
		String memberCode=memberDataMap.get("member_code");
		MDataMap accountCodedDataMap=DbUp.upTable("mc_member_info").one("member_code",memberCode);
		if(accountCodedDataMap==null){
			getOrderDetailListResult.inErrorMessage(915805218);
			return getOrderDetailListResult; 
		}
		String accountCode=accountCodedDataMap.get("account_code");
		MDataMap mDataMap=new MDataMap();
		mDataMap.put("account_code", accountCode);
		List<MDataMap> reckonOrderList=DbUp.upTable("gc_reckon_log").queryAll("", "order_code", "account_code=:account_code and reckon_change_type in (4497465200030001,4497465200030002)", mDataMap);
		for(MDataMap reckonMap:reckonOrderList){
			OrderInfo orderInfo=new OrderInfo();
			orderInfo.setOrderCode(reckonMap.get("order_code"));
			orderInfo.setReckonTime(reckonMap.get("create_time"));
			orderInfo.setWithdrawTime(reckonMap.get("withdraw_time"));
			orderInfo.setReckonMoney(reckonMap.get("reckon_money"));
			orderInfo.setReckonType(reckonMap.get("reckon_change_type").equals("4497465200030001")?"正向清分":"逆向清分");
			
			MDataMap orderinfodDataMap = DbUp.upTable("oc_orderinfo").one("order_code","HH"+reckonMap.get("order_code"));
			
			//System.out.println(orderinfodDataMap);
			if(orderinfodDataMap==null){
				orderInfo.setOrderStatus("");
			}else{
				orderInfo.setOrderStatus(orderinfodDataMap.get("order_status"));
			}
			
		    orderList.add(orderInfo);
		}
		getOrderDetailListResult.setOrderList(orderList);
		return getOrderDetailListResult;
	}

}

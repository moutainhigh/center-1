package com.cmall.groupcenter.account.api;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.account.model.GetPayOrderListInput;
import com.cmall.groupcenter.account.model.GetPayOrderListResult;
import com.cmall.groupcenter.account.model.PayOrderInfo;
import com.cmall.productcenter.model.GetAppAndColumnResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.dbdo.DbUp;

public class ApiGetPayOrderList extends RootApi<GetPayOrderListResult, GetPayOrderListInput>{

	public GetPayOrderListResult Process(GetPayOrderListInput inputParam,
			MDataMap mRequestMap) {
		GetPayOrderListResult getPayOrderListResult=new GetPayOrderListResult();
		List<PayOrderInfo> list=new ArrayList<PayOrderInfo>();
		String mobile=inputParam.getMobile();
		MDataMap memberDataMap=DbUp.upTable("mc_login_info").one("login_name",mobile);
		if(memberDataMap==null){
			getPayOrderListResult.inErrorMessage(915805218);
			return getPayOrderListResult;
		}
		String memberCode=memberDataMap.get("member_code");
		MDataMap accountCodedDataMap=DbUp.upTable("mc_member_info").one("member_code",memberCode);
		if(accountCodedDataMap==null){
			getPayOrderListResult.inErrorMessage(915805218);
			return getPayOrderListResult;
		}
		String accountCode=accountCodedDataMap.get("account_code");
		MDataMap mDataMap=new MDataMap();
		mDataMap.put("account_code", accountCode);
		List<MDataMap> payList=DbUp.upTable("gc_pay_order_info").queryAll("", " create_time ", "account_code=:account_code",mDataMap );
		for(MDataMap payMap:payList){
			PayOrderInfo payOrderInfo=new PayOrderInfo();
			payOrderInfo.setWithdrawTime(payMap.get("create_time"));
			payOrderInfo.setWithdrawAccount(payMap.get("card_code"));
			payOrderInfo.setWithdrawMoney(payMap.get("withdraw_money"));
			payOrderInfo.setAfterWithdrawMoney(payMap.get("after_withdraw_money"));
			payOrderInfo.setOrderStatus(DbUp.upTable("sc_define").one("define_code",payMap.get("order_status")).get("define_name"));
			payOrderInfo.setPayStatus(DbUp.upTable("sc_define").one("define_code",payMap.get("pay_status")).get("define_name"));
			list.add(payOrderInfo);
		}
		getPayOrderListResult.setGetPayOrderInfoList(list);
		return getPayOrderListResult;
	}

}

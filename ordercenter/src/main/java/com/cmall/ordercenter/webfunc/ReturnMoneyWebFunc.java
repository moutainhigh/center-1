package com.cmall.ordercenter.webfunc;

import com.cmall.ordercenter.model.OcOrderShipments;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class ReturnMoneyWebFunc extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			
			OrderService os = new OrderService();
			Order order =new Order();
			MUserInfo userInfo = UserFactory.INSTANCE.create();
			
			String userCode=userInfo.getUserCode();
			order.setOrderCode(mSubMap.get("order_code"));
			
			
			RootResult rr = os.CancelOrderForReturnMoney(order, mSubMap.get("remark"), userCode);
			
			mResult.setResultCode(rr.getResultCode());
			mResult.setResultMessage(rr.getResultMessage());
		}

		return mResult;
	}


}

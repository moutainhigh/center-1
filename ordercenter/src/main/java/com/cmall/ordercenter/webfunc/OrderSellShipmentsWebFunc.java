package com.cmall.ordercenter.webfunc;

import com.cmall.ordercenter.model.OcOrderShipments;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;
/***
 * 
 * @author shiyz
 *
 */
public class OrderSellShipmentsWebFunc extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			
			OrderService os = new OrderService();
			OcOrderShipments oos = new OcOrderShipments();
			
			String logisticseCode = mSubMap.get("logisticse_code");
			
			if(logisticseCode.equals("define_self")){
				oos.setLogisticseName(mSubMap.get("define_self_name"));
			}else{
				oos.setLogisticseName(mSubMap.get("logisticse_name"));
			}
			
			oos.setLogisticseCode(logisticseCode);
			
			oos.setOrderCode(mSubMap.get("order_code"));
			oos.setRemark(mSubMap.get("remark"));
			oos.setWaybill(mSubMap.get("waybill"));
			
			RootResult rr = os.shipmentForOrder(oos);
			
			mResult.setResultCode(rr.getResultCode());
			mResult.setResultMessage(rr.getResultMessage());
		}

		return mResult;
	}

}


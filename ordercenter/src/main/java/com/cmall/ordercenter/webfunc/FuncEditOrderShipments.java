package com.cmall.ordercenter.webfunc;

import com.cmall.systemcenter.common.DateUtil;
import com.ordercenter.express.service.OrderShipmentsService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 编辑物流信息
 * @author jlin
 *
 */
public class FuncEditOrderShipments extends RootFunc {
	
	static OrderShipmentsService shipmentsService = new OrderShipmentsService();

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		String logisticseCode = mSubMap.get("logisticse_code");

		String logisticseName=mSubMap.get("logisticse_name");
		
		if (logisticseCode.equals("define_self")) {
			logisticseName=mSubMap.get("define_self_name");
		}
		
		MDataMap shipmentMap = DbUp.upTable("oc_order_shipments").one("uid", mSubMap.get("uid"));
		
		
		MDataMap updateData=new MDataMap();
		
		updateData.put("uid", mSubMap.get("uid"));
		updateData.put("logisticse_code", logisticseCode);
		updateData.put("logisticse_name", logisticseName);
		updateData.put("waybill", mSubMap.get("waybill"));
		
		String creator="";
		try {
			creator=UserFactory.INSTANCE.create().getLoginName();
		} catch (Exception e) {
		}
		
		updateData.put("update_time", creator);
		updateData.put("update_user", DateUtil.getSysDateTimeString());
		updateData.put("remark", mSubMap.get("remark"));
		updateData.put("is_send100_flag", "0");
		
		DbUp.upTable("oc_order_shipments").dataUpdate(updateData, "", "uid");
		
		if(!shipmentMap.get("logisticse_code").equals(logisticseCode) || !shipmentMap.get("waybill").equals(mSubMap.get("waybill"))) {
			shipmentsService.onChangeShipment(shipmentMap.get("order_code"), shipmentMap.get("logisticse_code"), shipmentMap.get("waybill"), logisticseCode, mSubMap.get("waybill"));
		}
		
		return mResult;
	}

}


package com.cmall.ordercenter.webfunc;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cmall.ordercenter.model.OcOrderShipments;
import com.cmall.ordercenter.service.OrderService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

public class OrderShipmentsWebFunc extends RootFunc {

	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {

		MWebResult mResult = new MWebResult();

		MDataMap mSubMap = mDataMap.upSubMap(WebConst.CONST_WEB_FIELD_NAME);

		if (mResult.upFlagTrue()) {
			
			OrderService os = new OrderService();
			OcOrderShipments oos = new OcOrderShipments();
			
			String logisticseCode = StringUtils.trimToEmpty(mSubMap.get("logisticse_code"));
			
			boolean flag = false;
			if(logisticseCode.equals("define_self")){
				oos.setLogisticseName(mSubMap.get("define_self_name"));
				// 查询所有物流编码对照表
				List<MDataMap> mpList = DbUp.upTable("sc_logisticscompany").queryAll("","","",null);
				String companyName = mSubMap.get("define_self_name");
				if(StringUtils.isNotBlank(companyName)){
					for (MDataMap d : mpList) {
						if (companyName.toLowerCase().indexOf(d.get("company_name").toLowerCase())>-1) {
							logisticseCode = d.get("company_code");
							flag = false;
							break;
						}else{
							flag = true;
						}
					}
				}
			}else{
				oos.setLogisticseName(mSubMap.get("logisticse_name"));
			}
			
			oos.setLogisticseCode(logisticseCode);
			
			oos.setOrderCode(mSubMap.get("order_code"));
			oos.setRemark(mSubMap.get("remark"));
			oos.setWaybill(StringUtils.trimToEmpty(mSubMap.get("waybill")));
			
			RootResult rr = os.shipmentForOrder(oos);
			
			String msg = "";
			if(flag){
				if(StringUtils.isBlank(rr.getResultMessage())){
					msg += "发货成功! </br>";
				}
				msg += "您所填写的物流公司名称【" + mSubMap.get("define_self_name") + "】在惠家有系统中不支持对其进行查询，用户可能无法跟踪物流信息!";
			}
			
			mResult.setResultCode(rr.getResultCode());
			mResult.setResultMessage(rr.getResultMessage() + msg);
		}
		return mResult;
	}

}


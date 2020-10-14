package com.cmall.groupcenter.jd.job;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.jd.model.ExpressShipBean;
import com.cmall.groupcenter.jd.model.ExpressShipBean.OrderTrackBean;
import com.cmall.groupcenter.jd.model.ExpressShipBean.WaybillCodeBean;
import com.srnpr.xmassystem.homehas.RsyncJingdongSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
/**
 * 更新京东物流状态接口
 * @author 张圣瑞
 *
 */
public class JdExpressShip extends RootJob{
	
	@Override
	public void doExecute(JobExecutionContext context) {
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("order_state", "1");
		String sqlStr = "select * from oc_order_jd where order_state =:order_state and jd_order_state IN(8,9,10,16)";
		//mWhereMap.put("state", "0");
		//String sqlStr = "select * from oc_order_jd where order_state =:order_state and state =:state";
		List<Map<String,Object>> dataSqlList = DbUp.upTable("oc_order_jd").dataSqlList(sqlStr, mWhereMap);
		mWhereMap.clear();
		mWhereMap.put("jd_order_state", "19");
		mWhereMap.put("state", "1");
		mWhereMap.put("jd_order_state1", "17");
		mWhereMap.put("state1", "2");
		sqlStr = "select * from oc_order_jd where (jd_order_state =:jd_order_state || state =:state || jd_order_state =:jd_order_state1 || state =:state1) and update_time >=  NOW() - interval 1 day";
		List<Map<String,Object>> dataSqlList1 = DbUp.upTable("oc_order_jd").dataSqlList(sqlStr, mWhereMap);
		handler(dataSqlList);
		handler(dataSqlList1);
	}
	
	public void handler(List<Map<String,Object>> dataSqlList) {
		MDataMap mWhereMap = new MDataMap();
		for(Map<String,Object> map : dataSqlList) {
			String jd_order_id = map.get("jd_order_id").toString();
			String order_code = map.get("order_code").toString();
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("jdOrderId", Long.parseLong(jd_order_id));
			paramMap.put("waybillCode", 1);
			String callGateway = RsyncJingdongSupport.callGateway("biz.order.orderTrack.query", paramMap);
			JSONObject parseObject = JSONObject.parseObject(callGateway);
			JSONObject jsonObject = parseObject.getJSONObject("biz_order_orderTrack_query_response");
			Boolean success = jsonObject.getBoolean("success");
//			String recode = jsonObject.get("code").toString();
//			String resultCode = jsonObject.get("resultCode").toString();
			if(success){
				ExpressShipBean result = JSONObject.parseObject(jsonObject.getString("result"),ExpressShipBean.class);
				mWhereMap.clear();
				mWhereMap.put("order_code", order_code);
				int dataCount = DbUp.upTable("oc_order_shipments").dataCount("order_code =:order_code", mWhereMap);
				WaybillCodeBean waybillCode = result.getWaybillCode().get(0);
				if(dataCount < 1) {
					mWhereMap.clear();
					mWhereMap.put("order_code", order_code);
					mWhereMap.put("logisticse_name", waybillCode.getCarrier());
					mWhereMap.put("waybill", waybillCode.getDeliveryOrderId());
					mWhereMap.put("creator", "system");
					mWhereMap.put("create_time", FormatHelper.upDateTime());
					mWhereMap.put("is_send100_flag", "1");
					DbUp.upTable("oc_order_shipments").dataInsert(mWhereMap);
				}
				String sqlStr = "select * from oc_express_detail where order_code =:order_code";
				mWhereMap.clear();
				mWhereMap.put("order_code", order_code);
				List<Map<String,Object>> oc_express_details = DbUp.upTable("oc_express_detail").dataSqlList(sqlStr, mWhereMap);
				List<String> times = new ArrayList<String>();
				for(Map<String,Object> map1 : oc_express_details) {
					times.add(map1.get("time").toString());
				}
				List<OrderTrackBean> orderTracks = result.getOrderTrack();
				for(OrderTrackBean orderTrack : orderTracks) {
					String msgTime = orderTrack.getMsgTime();
					if(!times.contains(msgTime)) {
						mWhereMap.clear();
						mWhereMap.put("order_code", order_code);
						mWhereMap.put("waybill", waybillCode.getDeliveryOrderId());
						mWhereMap.put("context", orderTrack.getContent());
						mWhereMap.put("time", orderTrack.getMsgTime());
						mWhereMap.put("logisticse_code", "jingdong");
						DbUp.upTable("oc_express_detail").dataExec("INSERT INTO `ordercenter`.`oc_express_detail` (`order_code`, `logisticse_code`, `waybill`, `context`, `time`) VALUES (:order_code, :logisticse_code, :waybill, :context, :time)", mWhereMap);
					}
				}
			}
		}
	}
}

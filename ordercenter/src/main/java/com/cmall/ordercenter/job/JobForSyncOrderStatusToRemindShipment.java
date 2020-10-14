package com.cmall.ordercenter.job;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 类:JobForSyncOrderStatusToRemindShipment
 * 描述:定时同步 待发货订单 状态到 提醒发货表
 * 时间:2019-06-13
 * 作者: lgx
 *
 */
public class JobForSyncOrderStatusToRemindShipment extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		try {
			// 查询订单状态是 待发货 的 提醒发货消息(只查惠家有订单)
			List<Map<String, Object>> list = DbUp.upTable("oc_order_remind_shipment").dataSqlList(
					"select * from ordercenter.oc_order_remind_shipment where order_status = '4497153900010002' and (order_code like 'DD%' OR order_code like 'HH%') ",
					null);
			if (list != null && list.size() > 0) {
				for (Map<String, Object> map : list) {
					MDataMap orderShipment = new MDataMap(map);
					String order_code = orderShipment.get("order_code");
					MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("order_code",order_code);
					if(null != orderInfo) {
						// 如果订单状态依然是待发货,则不处理
						if("4497153900010002".equals(orderInfo.get("order_status"))) {
							
						}else {
							// 否则说明订单状态改变,同步订单状态到提醒发货表
							orderShipment.put("order_status", orderInfo.get("order_status"));
							DbUp.upTable("oc_order_remind_shipment").dataUpdate(orderShipment, "order_status", "order_code");
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

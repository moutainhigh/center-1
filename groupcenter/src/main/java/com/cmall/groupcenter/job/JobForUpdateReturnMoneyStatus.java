package com.cmall.groupcenter.job;


import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExclusiveLock;

/**
 * 每日自动变更0元退款单状态为已退款
 * 暂时只变更取消单的退款单
 */
public class JobForUpdateReturnMoneyStatus extends RootJobForExclusiveLock {
	
	static FlowBussinessService service = new FlowBussinessService();
	
	public void doExecute(JobExecutionContext context) {
		// 查询待退款的0元退款单（仅限取消订单）
		String sql = "SELECT rm.uid,rm.return_money_code"
				+ " FROM ordercenter.oc_return_money rm,ordercenter.oc_orderinfo oi"
				+ " WHERE rm.order_code = oi.order_code"
				+ " AND oi.order_status = '4497153900010006' AND rm.status = '4497153900040003'"
				+ " AND rm.return_goods_code = ''"
				+ " AND rm.online_money = 0";
		
		List<Map<String, Object>> mapList = DbUp.upTable("oc_return_money").dataSqlList(sql, new MDataMap());
		for(Map<String, Object> map : mapList) {
			service.ChangeFlow(map.get("uid").toString(), "449715390004", "4497153900040003", "4497153900040001", "system", "系统自动变更状态", new MDataMap());
		}
	}

}

package com.cmall.ordercenter.service;

import java.util.Map;

import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 订单操作类
 * 
 * @author jl
 * 
 */
public class HOrderService {

	/**
	 * 更新订单状态
	 * 
	 * @param uid
	 * @param orderStatus
	 * @return
	 */
	public int updateState(String uid, Byte orderStatus) {

		Map<String, Object> map = DbUp
				.upTable("hc_order")
				.dataSqlOne(
						"select orderStatus,used_count,member_code from hc_order where uid=:uid",
						new MDataMap("uid", uid));

		int orderStatus1 = (Integer) map.get("orderStatus");
		int used_count = (Integer) map.get("used_count");
		String member_code = (String) map.get("member_code");

		// 操作完订单表后更新日志
		DbUp.upTable("lc_half_order").dataInsert(
				new MDataMap("orderStatus", orderStatus + "",
						"orderStatus_old", orderStatus1 + "", "used_count",
						String.valueOf(used_count), "used_count_old", String
								.valueOf(used_count), "create_time", DateUtil
								.getSysDateTimeString(), "create_user",
						member_code));
		int count = DbUp.upTable("hc_order").dataUpdate(
				new MDataMap("uid", uid, "orderStatus",
						String.valueOf(orderStatus), "update_time",
						DateUtil.getSysDateString(),"agio","0.00"),
				"orderStatus,update_time,agio", "uid");
		return count;
	}

}

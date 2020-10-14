package com.cmall.groupcenter.duohuozhu.job;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.duohuozhu.support.TrackForDuohuozhuSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时同步订单轨迹信息
 */
public class JobForRsyncOrderTrack extends RootJob {

	TrackForDuohuozhuSupport support = new TrackForDuohuozhuSupport();
	
	@Override
	public void doExecute(JobExecutionContext context) {
		// 订单状态是已发货或已签收
		// 配送状态是签收和拒收状态，或者更新时间在24小时内
		String sSql = "SELECT z.order_code FROM oc_order_duohz z, oc_orderinfo o WHERE z.order_code = o.order_code ";
				sSql += " AND o.order_status IN('4497153900010003','4497153900010005')";
				sSql += " AND (z.cod_status IN('P01','P02') OR (TIMESTAMPDIFF(HOUR,z.update_time,NOW()) <= 48) )";
			  
		List<Map<String, Object>> mapList = DbUp.upTable("oc_orderinfo").dataSqlList(sSql, new MDataMap());
		for(Map<String, Object> map : mapList) {
			support.rsyncOrderTrack(map.get("order_code")+"");
		}
		
	}

}

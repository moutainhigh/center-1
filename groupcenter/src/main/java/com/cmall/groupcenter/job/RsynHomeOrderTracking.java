package com.cmall.groupcenter.job;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.service.GetOrderTrackingService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 订单配送轨迹定时任务
 * @author wz
 *
 */
public class RsynHomeOrderTracking extends RootJob{

	public void doExecute(JobExecutionContext context) {
		
		//未发货(4497153900010002)  or 已发货 ( 4497153900010003)
		List<MDataMap> mDataMapsList = DbUp.upTable("oc_orderinfo").
				queryAll("out_order_code", "", "order_status in ('4497153900010002','4497153900010003') and delete_flag=0 and out_order_code!=''", new MDataMap());
		
		if(mDataMapsList!=null && !"".equals(mDataMapsList) && mDataMapsList.size()>0){
			for(MDataMap map : mDataMapsList){
				GetOrderTrackingService getOrderTrackingService = new GetOrderTrackingService();
				boolean bol = getOrderTrackingService.synchronizationGetOrderTracking(map.get("out_order_code"));   //从家有获取订单跟踪信息
			}
		}
	}
	
}

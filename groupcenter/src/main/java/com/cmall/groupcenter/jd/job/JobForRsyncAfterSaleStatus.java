package com.cmall.groupcenter.jd.job;

import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.jd.JdAfterSaleSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时刷新服务单状态
 */
public class JobForRsyncAfterSaleStatus extends RootJob{

	private static JdAfterSaleSupport jdAfterSaleSupport = new JdAfterSaleSupport();
	
	@Override
	public void doExecute(JobExecutionContext context) {
		synchronized (jdAfterSaleSupport) {
			
			List<MDataMap> list = DbUp.upTable("oc_order_jd_after_sale").queryAll("asale_code", "", "rsync_flag != 0 AND afs_service_step NOT IN(20,60,40)", new MDataMap());
			
			for(MDataMap map : list) {
				jdAfterSaleSupport.execServiceDetailInfoQuery(map.get("asale_code"));
			}
			
		}
	}
	
}

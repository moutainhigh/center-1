package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 订单把不可售后订单表示更新
 * @author jlin
 *
 */
public class JobForOrderAsaleFlag extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		
		String sql="SELECT d.zid from oc_orderdetail d LEFT JOIN oc_orderinfo o on o.order_code=d.order_code where d.flag_asale='0' and o.order_status='4497153900010005' and (UNIX_TIMESTAMP()-UNIX_TIMESTAMP(o.update_time))>1296000 ";
		
		List<Map<String, Object>> list=DbUp.upTable("oc_orderdetail").dataSqlList(sql, new MDataMap());
		if(list!=null&&!list.isEmpty()){
			DbUp.upTable("oc_orderdetail").dataExec("update oc_orderdetail set flag_asale='1' where zid in ("+StringUtils.join(list, ",")+") ", new MDataMap());
		}
		
	}
}

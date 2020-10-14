package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时执行sql 映射家有 本地买家编号
 * @author jlin
 *
 */
public class JobByerCodeMapper  extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		int co=DbUp.upTable("oc_orderinfo").dataExec("UPDATE ordercenter.oc_orderinfo s SET s.buyer_code = (SELECT member_code from membercenter.mc_extend_info_homehas WHERE homehas_code=SUBSTRING(s.buyer_code,3) LIMIT 0,1) WHERE LEFT(s.buyer_code,2)=:px ", new MDataMap("px","h_"));
		bLogInfo(0, "update oc_orderinfo buyer_code counnt:"+co);
	}

}

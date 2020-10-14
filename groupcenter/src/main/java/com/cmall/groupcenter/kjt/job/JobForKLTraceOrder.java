package com.cmall.groupcenter.kjt.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.kjt.KLTraceOrder;
import com.cmall.groupcenter.kjt.TraceOrder;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时同步订单状态
 * @author jlin
 *
 */
public class JobForKLTraceOrder extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
			
		KLTraceOrder kt= new KLTraceOrder();
		
		kt.doProcess();
		

	}

}

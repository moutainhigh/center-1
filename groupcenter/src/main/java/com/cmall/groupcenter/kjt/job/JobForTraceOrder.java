package com.cmall.groupcenter.kjt.job;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.kjt.TraceOrder;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时同步订单状态
 * @author jlin
 *
 */
public class JobForTraceOrder extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		
		List<MDataMap>  list = DbUp.upTable("oc_order_kjt_list").queryAll("order_code_out", "", "sostatus in (0,1,4,41,45) and order_code_out<>'' ", null);
		
//		String sql="SELECT DISTINCT order_code_out from oc_order_kjt_list k LEFT JOIN oc_orderinfo o on k.order_code=o.order_code where o.order_status in ('4497153900010002','4497153900010003','4497153900010004','4497153900010005') and  k.sostatus in (0,1,4,41,45) and k.order_code_out<>'' ";
//		List<Map<String, Object>>  list = DbUp.upTable("oc_order_kjt_list").dataSqlList(sql, null);
		
		List<Long> idList = new ArrayList<Long>(20);
		
		for (int i = 0; i < list.size(); i++) {
			String order_code_out=list.get(i).get("order_code_out");
			if(StringUtils.isNotBlank(order_code_out)){
				idList.add(Long.valueOf(order_code_out));
				if((i!=0&&(i+1)%20==0)||(list.size()-1==i)){
					TraceOrder traceOrder = new TraceOrder();
					traceOrder.upRsyncRequest().setSalesChannelSysNo(Long.valueOf(bConfig("groupcenter.rsync_kjt_SaleChannelSysNo")));
					traceOrder.upRsyncRequest().setOrderIds(idList);
					traceOrder.doRsync();
					idList.clear();
				}
			}
		}
	}

}

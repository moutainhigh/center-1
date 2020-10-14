package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncOrderStatus;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
/***
 * 同步家有订单状态
 * @author jlin
 *
 */
public class JobOrderStatus extends RootJob {

	public void doExecute(JobExecutionContext context) {
		
		RsyncOrderStatus rsyncOrderStatus=new RsyncOrderStatus();
		
		String sql="select out_order_code from oc_orderinfo where out_order_code <> '' and order_status not in('4497153900010005','4497153900010006')  and ( pay_type='449716200002' or (pay_type='449716200001' and order_code in (select order_code from oc_order_pay)))";
		//查询外部订单编号
		List<Map<String, Object>> list= DbUp.upTable("oc_orderinfo").dataSqlList(sql , null);
		if(list!=null&&list.size()>0){
			for (Map<String, Object> map : list) {
				String yc_orderform_num = (String)map.get("out_order_code"); 
				if(yc_orderform_num==null||"".equals(yc_orderform_num)){
					continue;
				}
				rsyncOrderStatus.upRsyncRequest().setYc_orderform_num(yc_orderform_num);
				rsyncOrderStatus.doRsync();
			}
		}
	}
	

}

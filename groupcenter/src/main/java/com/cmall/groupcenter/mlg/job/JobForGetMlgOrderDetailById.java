package com.cmall.groupcenter.mlg.job;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.mlg.RsyncGetMlgOrderDetailById;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;


/** 
* @ClassName: JobForGetMlgOrderDetailById 
* @Description: 定时同步订单状态
* @author 张海生
* @date 2015-12-30 上午11:03:36 
*  
*/
public class JobForGetMlgOrderDetailById extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		
		List<MDataMap>  list = DbUp.upTable("oc_orderinfo").queryAll("order_code", "", "order_status in ('4497153900010002','4497153900010003','4497153900010004') and small_seller_code='SF03MLG' ", null);
		RsyncGetMlgOrderDetailById od = new RsyncGetMlgOrderDetailById();
		for (MDataMap mDataMap : list) {
			String orderCode = mDataMap.get("order_code");
			if(StringUtils.isNotEmpty(orderCode)){
				od.upRsyncRequest().setOrder_id(orderCode);
				od.doRsync();
			}
		}
	}

}

package com.cmall.ordercenter.job.reconciliation;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 
 * 类: JobReconciliationCollect <br>
 * 描述: 汇总已生成的对账单 ，将<br>
 * 作者: zhy<br>
 * 时间: 2017年3月31日 下午2:43:07
 */
public class JobReconciliationCollect extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		try {
			MDataMap param = new MDataMap();
			String current_date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			param.put("reconciliation_time", current_date);
			List<Map<String, Object>> list = DbUp.upTable("oc_payment_reconciliation").dataSqlList(
					"SELECT pay_type,DATE(reconciliation_time) AS reconciliation_time,SUM(order_money) AS reconciliation_money FROM ordercenter.oc_payment_reconciliation WHERE reconciliation_status = '4497479900040001' and reconciliation_time<:reconciliation_time GROUP BY pay_type,DATE(reconciliation_time)",
					param);
			if (list.size() > 0) {
				String datetime = DateUtil.getSysDateTimeString();
				for (Map<String, Object> map : list) {
					MDataMap data = new MDataMap();
					data.put("pay_type", map.get("pay_type").toString());
					data.put("reconciliation_time", map.get("reconciliation_time").toString());
					data.put("reconciliation_money",
							BigDecimal.valueOf(Double.parseDouble(map.get("reconciliation_money").toString()))
									.setScale(2, BigDecimal.ROUND_HALF_UP).toString());
					data.put("create_time", datetime);
					data.put("create_user", "job");
					data.put("update_user", "job");
					data.put("update_time", datetime);
					int count = DbUp.upTable("oc_payment_reconciliation_collect")
							.dataCount("pay_type=:pay_type and reconciliation_time=:reconciliation_time", data);
					boolean flag = false;
					if (count > 0) {
						DbUp.upTable("oc_payment_reconciliation_collect").dataUpdate(data, "reconciliation_money",
								"pay_type,reconciliation_time");
						flag = true;
					} else {
						DbUp.upTable("oc_payment_reconciliation_collect").dataInsert(data);
						flag = true;
					}

					if (flag) {
						/**
						 * 修改对账单状态为已对账
						 */
						MDataMap update = new MDataMap();
						update.put("reconciliation_status", "4497479900040002");
						update.put("reconciliation_time", map.get("reconciliation_time").toString());
						update.put("pay_type", data.get("pay_type"));
						update.put("create_time", datetime);
						update.put("update_user", "job");
						update.put("update_time", DateUtil.getSysDateTimeString());
						DbUp.upTable("oc_payment_reconciliation").dataExec(
								"update ordercenter.oc_payment_reconciliation set reconciliation_status=:reconciliation_status,update_time=:update_time where pay_type=:pay_type and DATE(create_time)=:reconciliation_time",
								update);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

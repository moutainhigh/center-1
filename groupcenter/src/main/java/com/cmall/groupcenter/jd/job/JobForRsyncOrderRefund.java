package com.cmall.groupcenter.jd.job;

import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.jd.JdAfterSaleSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时同步京东售后退款信息
 */
public class JobForRsyncOrderRefund extends RootJob{

	private static JdAfterSaleSupport jdAfterSaleSupport = new JdAfterSaleSupport();
	
	@Override
	public void doExecute(JobExecutionContext context) {
		synchronized (jdAfterSaleSupport) {
			
			// 重试次数不超过500且创建时间在14天内
			List<MDataMap> list = DbUp.upTable("oc_order_jd_refund").queryAll("*", "", "pay_id = '' AND rsync_num < 500 AND create_time > DATE_ADD(NOW(),INTERVAL -14 DAY)", new MDataMap());
			
			for(MDataMap map : list) {
				jdAfterSaleSupport.execAfterSaleRefund(map.get("order_code"), map.get("after_sale_code"));
				DbUp.upTable("oc_order_jd_refund").dataExec("update oc_order_jd_refund set rsync_num = rsync_num + 1, exec_time = now() where zid = :zid", map);
				
				// 超过72小时未退款则发送异常通知
				if("0".equals(map.get("notice_flag")) && NumberUtils.toInt(map.get("rsync_num")) > 72) {
					sendNoticeMail(map);
					DbUp.upTable("oc_order_jd_refund").dataExec("update oc_order_jd_refund set notice_flag = 1,notice_time = now() where zid = :zid", map);
				}
			}
			
		}
	}
	
	private void sendNoticeMail(MDataMap map) {
		String noticeMail = TopConfig.Instance.bConfig("oneall.count_notice");
		String title = "京东长时间未退款-"+map.get("order_code");
		String content = String.format("订单号：%s，售后单号： %s", map.get("order_code"),map.get("after_sale_code"));
		MailSupport.INSTANCE.sendMail(noticeMail, title,content);
	}
	
}

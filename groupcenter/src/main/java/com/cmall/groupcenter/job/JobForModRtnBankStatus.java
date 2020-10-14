package com.cmall.groupcenter.job;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncModRtnBankStatus;
import com.cmall.groupcenter.homehas.model.RsyncRequestModRtnBankStatus;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJobForExclusiveLock;

/**
 * 更新LD退款单状态
 */
public class JobForModRtnBankStatus extends RootJobForExclusiveLock {

	public void doExecute(JobExecutionContext context) {
		// 查询所有未通知的订单
		List<MDataMap> orderList = DbUp.upTable("lc_return_money_ld_log").queryAll("", "", "notify_flag = 0 AND exec_num < 15", new MDataMap());
		
		for(MDataMap map : orderList) {
			RsyncModRtnBankStatus rsync = new RsyncModRtnBankStatus();
			RsyncRequestModRtnBankStatus req = rsync.upRsyncRequest();
			
			String ordId = map.get("ord_id");
			String ordSeq = map.get("ord_seq");
			
			req.setOrd_id(ordId);
			req.setOrd_seq(ordSeq);
			
			try {
				if(rsync.doRsync()) {
					// 如果调用成功则更改标识为1
					map.put("notify_flag", "1");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			map.put("exec_num", NumberUtils.toInt(map.get("exec_num")) + 1 + "");
			DbUp.upTable("lc_return_money_ld_log").dataUpdate(map, "notify_flag,exec_num", "zid");
			
			// 超过15此则发送异常通知邮件
			if(NumberUtils.toInt(map.get("exec_num")) >= 15) {
				String sErrorNotice = bConfig("zapweb.mail_notice").trim();
				if (StringUtils.isNotBlank(sErrorNotice)) {
					MailSupport.INSTANCE.sendMail(sErrorNotice,"LD订单自动退款更新状态执行失败通知","订单号： " + ordId + ", 序号： " + ordSeq);
				}
			}
		}
	}
}

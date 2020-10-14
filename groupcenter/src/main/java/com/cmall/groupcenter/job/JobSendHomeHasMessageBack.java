package com.cmall.groupcenter.job;

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.util.SmsBackup;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.websupport.MessageSupport;

/**
 * 备用短信任务
 * @author jlin
 *
 */
public class JobSendHomeHasMessageBack extends RootJob {

	public void doExecute(JobExecutionContext context) {

		MessageSupport messageSupport = new MessageSupport();
		Set<String> fail_zids=new HashSet<String>();
		
		SmsBackup smsBackup = new SmsBackup();
		
		for (MDataMap mDataMap : messageSupport.upSendListBySendSource()) {
			String mobile=mDataMap.get("msg_receive");
			String message= mDataMap.get("msg_content");
			String zid= mDataMap.get("zid");
			String send_source= mDataMap.get("send_source");
			
			String sign=bConfig("groupcenter.sendMess_sign_"+send_source);
			if(!smsBackup.send(sign, mobile, message, new StringBuffer())){
				fail_zids.add(zid);
			}
		}
		
		if(fail_zids.size()>0){
			DbUp.upTable("za_message").dataExec("update za_message set flag_finish=0  where zid in ("+ StringUtils.join(fail_zids,	WebConst.CONST_SPLIT_COMMA) + "); ",new MDataMap());
		}
	}
	
}

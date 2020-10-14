package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.util.SmsUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.websupport.MessageSupport;

/**
 * 发送营销短信
 * @author jlin
 *
 */
public class SendMessageMarketing extends RootJob {

	public void doExecute(JobExecutionContext context) {

		MessageSupport messageSupport = new MessageSupport();

		//RsyncEndMessageUseM rsyncEndMessage = new RsyncEndMessageUseM();
		SmsUtil smsUtil=new SmsUtil();

		for (MDataMap mDataMap : messageSupport.upSendListBySendSource("4497467200020002")) {
			
			/*
			rsyncEndMessage.upRsyncRequest().setHp_tel(
					mDataMap.get("msg_receive"));
			rsyncEndMessage.upRsyncRequest().setContent(
					mDataMap.get("msg_content"));

			rsyncEndMessage.doRsync();
			*/
			
			StringBuffer error= new StringBuffer();
			boolean b=smsUtil.sendSms2(mDataMap.get("msg_receive"), mDataMap.get("msg_content"),error);
			bLogInfo(0, "send message：",mDataMap,error);
			
		}
		
		//添加嘉玲国际的短信发送
		for (MDataMap mDataMap : messageSupport.upSendListBySendSource("4497467200020003")) {
			
			StringBuffer error= new StringBuffer();
			boolean b=smsUtil.sendSmsForCapp(mDataMap.get("msg_receive"), mDataMap.get("msg_content"),error);
			bLogInfo(0, "send message：",mDataMap,error);
		}
		
	}

}

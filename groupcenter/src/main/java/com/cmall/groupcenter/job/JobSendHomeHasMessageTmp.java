package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncEndMessage;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.websupport.MessageSupport;

/***
 * 临时所有短信都是用家有的接口发送
 * @author jlin
 *
 */
public class JobSendHomeHasMessageTmp  extends RootJob {

	public void doExecute(JobExecutionContext context) {

		MessageSupport messageSupport = new MessageSupport();

		RsyncEndMessage rsyncEndMessage = new RsyncEndMessage();

		for (MDataMap mDataMap : messageSupport.upSendListBySendSource()) {

			rsyncEndMessage.upRsyncRequest().setHp_tel(mDataMap.get("msg_receive"));
			rsyncEndMessage.upRsyncRequest().setContent(mDataMap.get("msg_content"));

			rsyncEndMessage.doRsync();

		}

	}

}

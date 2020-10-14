package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.listener.DistributeSendMessageJmsListener;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 微公社下单，退货时发送单聊消息
 * @author GaoYang
 *
 */
public class JobDistributeGroupSendMessage extends RootJob{

	private final static DistributeSendMessageJmsListener LISTENSER = new DistributeSendMessageJmsListener();
	@Override
	public void doExecute(JobExecutionContext context) {
		JmsNoticeSupport.INSTANCE.onReveiveQueue(JmsNameEnumer.OnGroupSendIM, LISTENSER);
	}

}

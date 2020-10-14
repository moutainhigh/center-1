package com.cmall.systemcenter.common;

import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.api.JmsKfkaServer;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.srnpr.zapweb.rootweb.RootJob;

public class KafkaDataJob extends RootJob{
	private final static JmsKfkaServer Jkafka = new JmsKfkaServer();
	public void doExecute(JobExecutionContext context) {
		JmsNoticeSupport.INSTANCE.onReveiveQueue(JmsNameEnumer.OnProductMonitor,Jkafka);
	}

}

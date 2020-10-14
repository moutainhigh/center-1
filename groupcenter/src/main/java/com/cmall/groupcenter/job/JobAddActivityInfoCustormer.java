package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;
import org.springframework.amqp.core.Queue;

import com.cmall.groupcenter.mq.custormers.ActivityCustormer;
import com.cmall.groupcenter.mq.model.ExchangeName;
import com.cmall.groupcenter.mq.model.QueueName;
import com.cmall.groupcenter.mq.service.MqService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 
 * @remark 项目启动时 动态绑定rabbitmq消费者(监听活动信息)
 * @author 任宏斌
 * @date 2018年9月14日
 * 
 * @modify  废弃此定时任务 改走接口
 * @author 任宏斌
 * @date 2020年05月15日
 */
@Deprecated
public class JobAddActivityInfoCustormer extends RootJob {

	public void doExecute(JobExecutionContext context) { 
		MqService mqService = new MqService();
		
		mqService.declareQueueAndBind(QueueName.activityQueueName,ExchangeName.topicExchange);
		
		Queue queue = new Queue(QueueName.activityQueueName, true, false, false);
		mqService.bindCustormer(new ActivityCustormer(), queue);
	}

}

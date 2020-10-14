package com.cmall.groupcenter.job;

import org.quartz.JobExecutionContext;
import org.springframework.amqp.core.Queue;

import com.cmall.groupcenter.mq.custormers.GiftVoucherCustormer;
import com.cmall.groupcenter.mq.model.ExchangeName;
import com.cmall.groupcenter.mq.model.QueueName;
import com.cmall.groupcenter.mq.service.MqService;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 
 * @remark 项目启动时 动态绑定rabbitmq消费者(监听礼金券详情)
 * @author 任宏斌
 * @date 2018年9月14日
 * 
 * @modify  废弃此定时任务 改走接口
 * @author 任宏斌
 * @date 2020年05月15日
 */
@Deprecated
public class JobAddGiftVoucherDetailCustormer extends RootJob {

	public void doExecute(JobExecutionContext context) {
		MqService mqService = new MqService();
		
		mqService.declareQueueAndBind(QueueName.giftVoucherQueueName, ExchangeName.topicExchange);
		
		Queue queue = new Queue(QueueName.giftVoucherQueueName, true, false, false);
		mqService.bindCustormer(new GiftVoucherCustormer(), queue);
	}

}

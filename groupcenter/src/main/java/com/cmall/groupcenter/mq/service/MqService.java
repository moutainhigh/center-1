package com.cmall.groupcenter.mq.service;

import java.io.IOException;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

import com.rabbitmq.client.Channel;


/**
 * 
 * @remark rabbitmq操作工具类
 * @author 任宏斌
 * @date 2018年9月14日
 */
public class MqService {

	/**
	 * 创建监听容器
	 * @param listener 监听器
	 * @param queues 消息队列
	 */
	public void bindCustormer(Object listener, Queue queues) { 
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		
		container.setConnectionFactory(MqClient.getConnectionFactory());
		container.setAcknowledgeMode(AcknowledgeMode.MANUAL);
		container.setQueues(queues);
		container.setMessageListener(listener);
		container.start();
	}
	
	/**
	 * 消息应答
	 * @param channel
	 * @param message
	 */
	public void ack(Channel channel, Message message) {
		try {
			channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 消息不应答 消息不重回队列
	 * @param channel
	 * @param message
	 */
	public void nackNoReQueue(Channel channel, Message message) {
		try {
			channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, false);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 声明队列并绑定到交换机
	 * @param activityqueuename 队列名称 TODO暂时路由key与交换机名称一致
	 * @param topicexchange 交换机名称
	 */
	public void declareQueueAndBind(String activityqueuename, String topicexchange) {
		Connection connection = MqClient.getConnection();
		Channel channel = connection.createChannel(false);
		try {
			channel.queueDeclare(activityqueuename, true, false, false, null);
			channel.queueBind(activityqueuename, topicexchange, activityqueuename);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				channel.close();
				connection.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}

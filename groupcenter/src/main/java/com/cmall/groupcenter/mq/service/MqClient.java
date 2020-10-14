package com.cmall.groupcenter.mq.service;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;

import com.srnpr.zapcom.topdo.TopUp;

public class MqClient {

	private static final CachingConnectionFactory connectionFactory;
	
	static {
		String host = TopUp.upConfig("groupcenter.rabbitmq.host");
		Integer port = Integer.parseInt(TopUp.upConfig("groupcenter.rabbitmq.port"));
		String username = TopUp.upConfig("groupcenter.rabbitmq.username");
		String password = TopUp.upConfig("groupcenter.rabbitmq.password");
		String vhost = TopUp.upConfig("groupcenter.rabbitmq.vhost");
		CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory();
		cachingConnectionFactory.setHost(host);
		cachingConnectionFactory.setPort(port);
		cachingConnectionFactory.setUsername(username);
		cachingConnectionFactory.setPassword(password);
		cachingConnectionFactory.setVirtualHost(vhost);
		connectionFactory = cachingConnectionFactory;
	}
	
	public static CachingConnectionFactory getConnectionFactory() {
		return connectionFactory;
	}
	
	public static Connection getConnection() {
		return connectionFactory.createConnection();
	}
	
}

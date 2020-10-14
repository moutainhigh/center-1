package com.cmall.systemcenter.util;

import java.util.Properties;

import com.cmall.systemcenter.enumer.KafkaNameEnumer;
import com.srnpr.zapcom.topdo.TopUp;

import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/***
 * 风控系统 kafka信息传递封装类
 * @author zhouguohui
 * @version 1.0
 */
public class KafkaUtil {
	/**
	 * 初始化kafka
	 */
	public static  Producer<String, String> producer;
  
    /**
     * 创建kafka的 Producer初始化参数
     * //创建KeyedMessage发送消息，参数1为topic名，参数2为分区名（若为null则随机发到一个分区），参数3为消息内容		
     * KafkaProperties.send(new KeyedMessage<String,String>("topic","partitionKey1","msg1"));		
     * KafkaProperties.close();
     */
	public static Producer<String, String> KafkaProperties(){
		if(null==producer){
			 synchronized(Producer.class) {
		           	if(null==producer){
		           		try {   
		           			Properties props = new Properties();
		        		    props.put("metadata.broker.list",TopUp.upConfig("systemcenter.kafka_list"));
		        	        props.put("serializer.class", "kafka.serializer.StringEncoder");
		        	        props.put("key.serializer.class", "kafka.serializer.StringEncoder");
		        	        props.put("partitioner.class", "com.cmall.systemcenter.util.SimplePartitioner");
		        	        props.put("request.required.acks", "1"); //0 代表异步提交   1 代表同步   
		        			ProducerConfig config = new ProducerConfig(props);
		        	        producer = new Producer<String, String>(config);
		           		}catch(Exception e) {  
		           			e.printStackTrace();                    
		           		} 
		           	}
		      }
		}
		    
	  return producer;
	}
	
	
	/**
	 * kafka发送格式定义
	 * @param topicName  发送到那个topic里面，必须从定义的枚举类型里面取值
	 * @param partitionKey    随机发送到那个分区  该字段为NULL 系统重写分区方法，默认的就ok
	 * @param messageStr 要发送的数据为json字符串
	 * @return
	 */
	public static KeyedMessage<String, String> SetMessage(KafkaNameEnumer topicName,String partitionKey,String messageStr){
		KeyedMessage<String, String> messageValue = new KeyedMessage<String, String>(
				topicName.toString(), partitionKey, messageStr);
		return messageValue;
	}
	
	public static void main(String[] args) {
		 KeyedMessage<String, String> data = new KeyedMessage<String, String>(
                 "OnOrderAddress", null, "DD28578104");
         KafkaProperties().send(data);
		
	}
	
	/*public static void main(String[] args) {
		 // 产生并发送消息
        long start=System.currentTimeMillis();
        for (long i = 0; i < 10; i++) {
            long runtime = new Date().getTime();
            String ip = "Q------wwwwwww----" + i;//rnd.nextInt(255);
            String msg = runtime + ",www.example.com," + ip;
            //如果topic不存在，则会自动创建，默认replication-factor为1，partitions为0
            KeyedMessage<String, String> data = new KeyedMessage<String, String>(
                    "zgh", null, msg);
            KafkaProperties().send(data);
        }
        System.out.println("耗时:" + (System.currentTimeMillis() - start));
        // 关闭producer
        KafkaPropertiesKey().close();
	}*/
	
	
	
	
	
}

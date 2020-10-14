package com.cmall.systemcenter.util;

import kafka.producer.Partitioner;
import kafka.utils.VerifiableProperties;

/**
 * 自定义kafka的分区函数
 * @author zhouguohui
 *
 */
public class SimplePartitioner  implements Partitioner {
	
/*	public SimplePartitioner (VerifiableProperties props) {
	}
	*//**
	* 确定生产者向broker的topic的那个partition中插入数据
	*//*
	 public int partition(Object key, int numPartitions) {
		 int partition = 0;
	     if (key instanceof String) {
	         String stringKey=(String)key;
	         int offset = stringKey.lastIndexOf('.');
	         if (offset > 0) {
	             partition = Integer.parseInt(stringKey.substring(offset + 1)) % numPartitions;
	         }
	     }else{
	         partition = key.toString().length() % numPartitions;
	     }
     return partition;
	}*/
	 
	  public SimplePartitioner(VerifiableProperties props) {
		  
	  }
	 
	    public int partition(Object obj, int numPartitions) {
	        int partition = 0;
	        if (obj instanceof String) {
	            String key=(String)obj;
	            int offset = key.lastIndexOf('.');
	            if (offset > 0) {
	                partition = Integer.parseInt(key.substring(offset + 1)) % numPartitions;
	            }
	        }else{
	            partition = obj.toString().length() % numPartitions;
	        }
	         
	        return partition;
	    }
	 
}

package com.cmall.productcenter.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.util.KafkaUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

import kafka.producer.KeyedMessage;

public class OrderAddressKafkaJob extends RootJob{

	
	public void doExecute(JobExecutionContext context) {
		MDataMap dataMap = DbUp.upTable("oc_risk_setting").oneWhere("","", "", "api_class_name", "com.cmall.productcenter.service.OrderAddressKafkaJob");
	    if(dataMap != null){
	    	String sql = dataMap.get("query_sql");
		    List<Map<String, Object>> list =  DbUp.upTable("oc_orderinfo").dataSqlList(sql, new MDataMap()); 
		    if(list.size()>0){
		    	for(Map<String, Object> map : list){
		    		if(map.get("order_code")!=null && !map.get("order_code").equals("")){
		    			KeyedMessage<String, String> data = new KeyedMessage<String, String>(
		    					"OnOrderAddress", null,map.get("order_code").toString());
		    			KafkaUtil.KafkaProperties().send(data);
		    		}
		    		
		    	}
		    }
	    }
	}

}

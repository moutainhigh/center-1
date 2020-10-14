package com.cmall.systemcenter.api;

import com.cmall.systemcenter.model.RedisOperationInput;
import com.cmall.systemcenter.model.RedisOperationResult;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootApi;
import com.srnpr.zapdata.kvsupport.KvFactory;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class ApiRedisOperation extends RootApi<RootResultWeb, RedisOperationInput>{

	@Override
	public RedisOperationResult Process(RedisOperationInput inputParam, MDataMap mRequestMap) {
		String type = inputParam.getType();
		RedisOperationResult result = new RedisOperationResult();
		if("string".equals(type)){//value值类型
			result = stringOperation(inputParam);
		}
		return result;
	}
	
	public RedisOperationResult stringOperation(RedisOperationInput input){
		RedisOperationResult result = new RedisOperationResult();
		String operation = input.getOperation();
		Integer time = input.getTime();
		String preKey = input.getPreKey();
		String key = input.getKey();
		String value = input.getValue();
		KvFactory kv = new KvFactory(preKey);
		StringBuffer message = new StringBuffer();
		if("set".equals(operation)){
			String r = "";
			if(kv.exists(key)){
				r = kv.get(key);
				kv.set(key, r+","+value);
			}else{
				r = kv.set(key, value);
			}
			message.append("set:");
			message.append(r);
			message.append(";");
		}else if("get".equals(operation)){
			String r = kv.get(key);
			result.setData(r);
			message.append("get:");
			message.append(r);
			message.append(";");
		}else if("delete".equals(operation)){
			Long r = kv.del(key);
			message.append("delete:");
			message.append(r);
			message.append(";");
		}else{
			
		}
		
		if(time!=null){
			Long r = kv.expire(key, time);
			message.append("time:");
			message.append(r);
			message.append(";");
		}
		result.setMessage(message.toString());
		result.setSuccess(true);
		return result;
	}

}

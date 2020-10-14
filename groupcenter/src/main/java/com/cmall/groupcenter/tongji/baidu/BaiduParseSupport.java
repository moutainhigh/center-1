package com.cmall.groupcenter.tongji.baidu;

import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.tongji.ResultData;
import com.cmall.groupcenter.tongji.ResultParse;

/**
 * 抽取出百度统计公共解析代码的抽象类
 * @param <T>
 */
public abstract class BaiduParseSupport<T extends ResultData> implements ResultParse<T>{

	private T obj;
	
	@Override
	public T parse(String body) {
		JSONObject jsonObj = null;
		T result = getObj();
		try {
			jsonObj = JSONObject.parseObject(body);
		} catch (Exception e) {
			result.setResultCode(0);
			result.setResultMessage("解析结果失败 -> " + e + "：["+body+"]");
			return result;
		}
		
		parseHeader(result, jsonObj);
		
		if(result.getResultCode() == 1){
			parseBody(result, jsonObj);
		}
		
		return result; 
	}

	/**
	 * 解析结果头判断是否有错误信息
	 * @param result
	 * @param jsonObj
	 */
	protected void parseHeader(T result, JSONObject jsonObj){
		JSONObject headerObj = jsonObj.getJSONObject("header");
		if(headerObj == null){
			result.setResultCode(0);
			result.setResultMessage("解析结果失败 -> 缺少header：["+jsonObj+"]");
		}
		
		if(headerObj.getIntValue("status") != 0){
			result.setResultCode(0);
			result.setResultMessage(headerObj.getString("failures"));
		}
	}
	
	/**
	 * 返回实体类对象，依赖子类实现方法getObjNew。
	 * 第一次调用时会创建实体类对象，以后再调用直接返回已经创建的对象
	 * @return 
	 * @see com.cmall.groupcenter.tongji.ResultParse#getObj()
	 */
	@Override
	public T getObj() {
		if(obj == null){
			obj = getObjNew();
		}
		return obj;
	}
	
	/**
	 * 子类需要实现此方法返回实体类对象
	 * @return
	 */
	public abstract T getObjNew();
	
	/**
	 * 各接口结果具体的解析逻辑
	 * @param result
	 * @param jsonObj
	 */
	protected abstract void parseBody(T result, JSONObject jsonObj);
}

package com.cmall.systemcenter.dcb;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSONObject;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class RecordInterfaceLogForDcb extends BaseClass{
	/**
	 * 
	 * @param url 请求连接
	 * @param request_time 请求时间
	 * @param response_time 响应时间
	 * @param original_input
	 * @param input 请求报文参数
	 * @param result 响应参数
	 * @param type 请求类型
	 * @param exceprion
	 */
	public void insertLogTable(String url,Date request_time,Date response_time,String original_input,String input,String result,String type,String exception){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MDataMap map = new MDataMap();
		map.put("url", url);
		map.put("request_time", sdf.format(request_time));
		map.put("create_time", sdf.format(response_time));
		map.put("input", input);
		map.put("result", result);
		map.put("exception", exception);
		map.put("original_input",StringUtils.trimToEmpty(original_input));
		if(!StringUtils.isBlank(result)&&"push".equals(type)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			map.put("code", jsonObject.getString("status"));
		}
		if("push".equals(type)){
			DbUp.upTable("lc_push_dcb_log").dataInsert(map);
		}else{
			DbUp.upTable("lc_dcb_query_log").dataInsert(map);
		}
	}
	
	/**
	 * 
	 * @param url 请求连接
	 * @param request_time 请求时间
	 * @param response_time 响应时间
	 * @param original_input
	 * @param input 请求报文参数
	 * @param result 响应参数
	 * @param type 请求类型
	 * @param exceprion
	 */
	public void insertLogTable(String url,Date request_time,Date response_time,String input,String result,String type,String exception){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		MDataMap map = new MDataMap();
		map.put("url", url);
		map.put("request_time", sdf.format(request_time));
		map.put("create_time", sdf.format(response_time));
		map.put("input", input);
		map.put("result", result);
		map.put("exception", exception);
		if(!StringUtils.isBlank(result)&&"push".equals(type)){
			JSONObject jsonObject = JSONObject.parseObject(result);
			map.put("code", jsonObject.getString("status"));
		}
		if("push".equals(type)){
			DbUp.upTable("lc_push_dcb_log").dataInsert(map);
		}else{
			DbUp.upTable("lc_dcb_query_log").dataInsert(map);
		}
	}
}

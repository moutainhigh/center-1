package com.cmall.groupcenter.jd.job;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.srnpr.xmassystem.homehas.RsyncJingdongSupport;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时同步京东省市区信息
 * https://jos.jd.com/api/detail.htm?apiName=biz.address.allProvinces.query&id=3875   sdk
 */
public class JobForRsyncJDAddress extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {
		System.out.println("定时同步京东省市区信息开始"+"\n");
		Long startTime = System.currentTimeMillis();
		rsyncProvinces();
		System.out.println("定时同步京东省信息结束"+"\n");
		rsyncCity();
		System.out.println("定时同步京东 市信息结束"+"\n");
		rsyncCountys();
		System.out.println("定时同步京东区信息结束"+"\n");
		rsyncTowns();
		System.out.println("定时同步京东街道信息结束"+"\n");
		Long endTime = System.currentTimeMillis();
		Long tempTime = (endTime - startTime);
		System.out.println("定时同步京东省市区信息--->>花费时间："+
				(((tempTime/86400000)>0)?((tempTime/86400000)+"d"):"")+
				((((tempTime/86400000)>0)||((tempTime%86400000/3600000)>0))?((tempTime%86400000/3600000)+"h"):(""))+
				((((tempTime/3600000)>0)||((tempTime%3600000/60000)>0))?((tempTime%3600000/60000)+"m"):(""))+
				((((tempTime/60000)>0)||((tempTime%60000/1000)>0))?((tempTime%60000/1000)+"s"):(""))+
				((tempTime%1000)+"ms"));
	}
	
	private void rsyncProvinces() {
		//5.6.7 需求 wangmeng
		String sql = "update sc_jingdong_address set use_yn='N'";
		DbUp.upTable("sc_jingdong_address").dataExec(sql, null);
		List<MDataMap> dataList = DbUp.upTable("sc_jingdong_address").queryByWhere("code_lvl","1");
		HashMap<String, Object> mDataMapparam = new HashMap<String, Object>();
		String resultText;
		for(MDataMap mData : dataList) {
			//mDataMapparam.put("id", mData.get("code"));
			resultText = RsyncJingdongSupport.callGateway("biz.address.allProvinces.query", mDataMapparam);
			if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
				JSONObject resultObj = JSON.parseObject(resultText);
				if(resultObj.containsKey("errorResponse")) {
					continue;
				}
				
				JSONObject response = resultObj.getJSONObject("biz_address_allProvinces_query_response");
				JSONObject dataMap = response.getJSONObject("result");
				if(dataMap == null) {
					continue;
				}
				String name,code;
				for(Entry<String, Object> entry : dataMap.entrySet()) {
					name = StringUtils.trimToEmpty(entry.getKey());
					code = StringUtils.trimToEmpty(entry.getValue().toString());
				
					if(code.length() > 12) continue;
					if(StringUtils.isBlank(name) || StringUtils.isBlank(code) ) {
						continue;
					}
					
					// 没有则插入
					if(DbUp.upTable("sc_jingdong_address").count("code",code) == 0) {
						MDataMap insertMap = new MDataMap();
						insertMap.put("code_lvl", "1");
						insertMap.put("code", code);
						insertMap.put("name", name);
						insertMap.put("p_code", mData.get("code"));
						insertMap.put("use_yn", "Y");
						DbUp.upTable("sc_jingdong_address").dataInsert(insertMap);
					}else {
						//wangmeng  5.6.7
						String sqlN = "update sc_jingdong_address set use_yn='Y' where code = '"+code+"'";
						DbUp.upTable("sc_jingdong_address").dataExec(sqlN, null);
					}
				}
			}
		}
	}
	
	private void rsyncCity() {
		//5.6.7 需求 wangmeng
		/*String sql = "update sc_jingdong_address set use_yn='N'";
		DbUp.upTable("sc_jingdong_address").dataExec(sql, null);*/
		List<MDataMap> dataList = DbUp.upTable("sc_jingdong_address").queryByWhere("code_lvl","1");
		HashMap<String, Object> mDataMapparam = new HashMap<String, Object>();
		String resultText;
		for(MDataMap mData : dataList) {
			mDataMapparam.put("id", mData.get("code"));
			resultText = RsyncJingdongSupport.callGateway("biz.address.citysByProvinceId.query", mDataMapparam);
			if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
				JSONObject resultObj = JSON.parseObject(resultText);
				if(resultObj.containsKey("errorResponse")) {
					continue;
				}
				
				JSONObject response = resultObj.getJSONObject("biz_address_citysByProvinceId_query_response");
				JSONObject dataMap = response.getJSONObject("result");
				if(dataMap == null) {
					continue;
				}
				String name,code;
				for(Entry<String, Object> entry : dataMap.entrySet()) {
					name = StringUtils.trimToEmpty(entry.getKey());
					code = StringUtils.trimToEmpty(entry.getValue().toString());
				
					if(code.length() > 12) continue;
					if(StringUtils.isBlank(name) || StringUtils.isBlank(code) ) {
						continue;
					}
					
					// 没有则插入
					if(DbUp.upTable("sc_jingdong_address").count("code",code) == 0) {
						MDataMap insertMap = new MDataMap();
						insertMap.put("code_lvl", "2");
						insertMap.put("code", code);
						insertMap.put("name", name);
						insertMap.put("p_code", mData.get("code"));
						insertMap.put("use_yn", "Y");
						DbUp.upTable("sc_jingdong_address").dataInsert(insertMap);
					}else {
						//wangmeng  5.6.7
						String sqlN = "update sc_jingdong_address set use_yn='Y' where code = '"+code+"'";
						DbUp.upTable("sc_jingdong_address").dataExec(sqlN, null);
					}
				}
			}
		}
	}
	
	private void rsyncCountys() {
		List<MDataMap> dataList = DbUp.upTable("sc_jingdong_address").queryByWhere("code_lvl","2");
		HashMap<String, Object> mDataMapparam = new HashMap<String, Object>();
		String resultText;
		for(MDataMap mData : dataList) {
			mDataMapparam.put("id", mData.get("code"));
			resultText = RsyncJingdongSupport.callGateway("biz.address.countysByCityId.query", mDataMapparam);
			if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
				JSONObject resultObj = JSON.parseObject(resultText);
				if(resultObj.containsKey("errorResponse")) {
					continue;
				}
				
				JSONObject response = resultObj.getJSONObject("biz_address_countysByCityId_query_response");
				JSONObject dataMap = response.getJSONObject("result");
				if(dataMap == null) {
					continue;
				}
				String name,code;
				for(Entry<String, Object> entry : dataMap.entrySet()) {
					name = StringUtils.trimToEmpty(entry.getKey());
					code = StringUtils.trimToEmpty(entry.getValue().toString());
					
					if(code.length() > 12) continue;
					if(StringUtils.isBlank(name) || StringUtils.isBlank(code) ) {
						continue;
					}
					
					// 没有则插入
					if(DbUp.upTable("sc_jingdong_address").count("code",code) == 0) {
						MDataMap insertMap = new MDataMap();
						insertMap.put("code_lvl", "3");
						insertMap.put("code", code);
						insertMap.put("name", name);
						insertMap.put("p_code", mData.get("code"));
						insertMap.put("use_yn", "Y");
						DbUp.upTable("sc_jingdong_address").dataInsert(insertMap);
					}else {
						//wangmeng  5.6.7
						String sqlN = "update sc_jingdong_address set use_yn='Y' where code = '"+code+"'";
						DbUp.upTable("sc_jingdong_address").dataExec(sqlN, null);
					}
				}
			}
		}
	}
	
	private void rsyncTowns() {
		List<MDataMap> dataList = DbUp.upTable("sc_jingdong_address").queryByWhere("code_lvl","3");
		HashMap<String, Object> mDataMapparam = new HashMap<String, Object>();
		String resultText;
		for(MDataMap mData : dataList) {
			mDataMapparam.put("id", mData.get("code"));
			resultText = RsyncJingdongSupport.callGateway("biz.address.townsByCountyId.query", mDataMapparam);
			if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
				JSONObject resultObj = JSON.parseObject(resultText);
				if(resultObj.containsKey("errorResponse")) {
					continue;
				}
				
				JSONObject response = resultObj.getJSONObject("biz_address_townsByCountyId_query_response");
				JSONObject dataMap = response.getJSONObject("result");
				if(dataMap == null) {
					continue;
				}
				String name,code;
				for(Entry<String, Object> entry : dataMap.entrySet()) {
					name = StringUtils.trimToEmpty(entry.getKey());
					code = StringUtils.trimToEmpty(entry.getValue().toString());
					
					if(code.length() > 12) continue;
					if(StringUtils.isBlank(name) || StringUtils.isBlank(code) ) {
						continue;
					}
					
					// 没有则插入
					if(DbUp.upTable("sc_jingdong_address").count("code",code) == 0) {
						MDataMap insertMap = new MDataMap();
						insertMap.put("code_lvl", "4");
						insertMap.put("code", code);
						insertMap.put("name", name);
						insertMap.put("p_code", mData.get("code"));
						insertMap.put("use_yn", "Y");
						DbUp.upTable("sc_jingdong_address").dataInsert(insertMap);
					}else {
						//wangmeng  5.6.7
						String sqlN = "update sc_jingdong_address set use_yn='Y' where code = '"+code+"'";
						DbUp.upTable("sc_jingdong_address").dataExec(sqlN, null);
					}
				}
			}
		}
	}
}

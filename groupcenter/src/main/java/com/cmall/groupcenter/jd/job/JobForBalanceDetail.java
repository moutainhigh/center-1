package com.cmall.groupcenter.jd.job;

import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.srnpr.xmassystem.homehas.RsyncJingdongSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webface.IWebStatic;

/**
 * 京东余额明细
 */
public class JobForBalanceDetail extends RootJob{

	@Override
	public void doExecute(JobExecutionContext context) {
		IWebStatic webStatic = getWebStatic();
		Date now = new Date();
		Date start = null,end = null;
		try {
			start = DateUtils.parseDate(WebHelper.upStaticValue(webStatic), new String[]{"yyyy-MM-dd HH:mm:ss"}) ;
		} catch (ParseException e) {
			e.printStackTrace();
			return;
		}
		
		while(start.before(now)) {
			// 每次往前推30分钟，兼容一下时间不一致的情况
			start = DateUtils.addMinutes(start, -30);
			// 每次查询一天内容的数据
			end = DateUtils.addDays(start, 1);
			// 结束时间不超过当前时间
			if(end.after(now)) {
				end = now;
			}
			
			Map<String, Object> paramJson = new HashMap<String, Object>();
			paramJson.put("pageSize", "50000"); // 一页取完
			paramJson.put("startDate", DateFormatUtils.format(start, "yyyyMMdd"));
			paramJson.put("endDate", DateFormatUtils.format(end, "yyyyMMdd"));
			String resultText = RsyncJingdongSupport.callGateway("biz.price.balancedetail.get", paramJson);
			
			if(StringUtils.isNotBlank(resultText) && resultText.startsWith("{")) {
				JSONObject resultObj = JSON.parseObject(resultText);
				JSONObject response = resultObj.getJSONObject("biz_price_balancedetail_get_response");
				Boolean success = response.getBoolean("success");
				
				if(!success) {
					break;
				}
				
				JSONArray data = response.getJSONObject("result").getJSONArray("data");
				JSONObject obj;
				MDataMap dataMap;
				for(int i = 0,j = data.size(); i < j; i++){
					obj = data.getJSONObject(i);
					dataMap = new MDataMap();
					dataMap.put("bid", obj.getString("id"));
					dataMap.put("account_type", obj.getString("accountType"));
					dataMap.put("amount", obj.getString("amount"));
					dataMap.put("pin", obj.getString("pin"));
					dataMap.put("order_id", obj.getString("orderId"));
					dataMap.put("trade_type", obj.getString("tradeType"));
					dataMap.put("trade_type_name", obj.getString("tradeTypeName"));
					dataMap.put("created_date", obj.getString("createdDate"));
					dataMap.put("note_pub", obj.getString("notePub"));
					dataMap.put("trade_no", obj.getString("tradeNo"));
					saveData(dataMap);
				}
				
				// 更新标量值
				start = end;
				WebHelper.updateStaticValue(webStatic, DateFormatUtils.format(end, "yyyy-MM-dd HH:mm:ss"));
			}
		}
	}
	
	private void saveData(MDataMap dataMap) {
		// 防止重复插入
		if(DbUp.upTable("sc_jingdong_balancedetail").count("bid", dataMap.get("bid")) == 0) {
			dataMap.put("create_time", FormatHelper.upDateTime());
			DbUp.upTable("sc_jingdong_balancedetail").dataInsert(dataMap);
		}
	}
	
	private IWebStatic getWebStatic() {
		return new IWebStatic() {
			
			@Override
			public String upDefault() {
				return "2019-05-01 00:00:00";
			}
			
			@Override
			public String upCode() {
				return JobForBalanceDetail.class.toString();
			}
		};
	}
	
}

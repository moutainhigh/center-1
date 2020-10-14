package com.cmall.groupcenter.tongji.baidu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.tongji.ResultData;

// 趋势分析 trend/time/a
public class ResultTrendTimeA extends ResultData{

	private List<TrendTimeA> list = new ArrayList<TrendTimeA>();
	
	public List<TrendTimeA> getList() {
		return list;
	}
	public void setList(List<TrendTimeA> list) {
		this.list = list;
	}

	public static class TrendTimeA {
		
		private String time;
		private String pvCount;
		private String pvRatio;
		private String visitCount;
		private String visitorCount;
		private String newVisitorCount;
		private String newVisitorRatio;
		private String ipCount;
		private String bounceRatio;
		private String avgVisitTime;
		private String avgVisitPages;
		
		public String getTime() {
			return time;
		}
		public void setTime(String time) {
			this.time = time;
		}
		public String getPvCount() {
			return pvCount;
		}
		public void setPvCount(String pvCount) {
			this.pvCount = pvCount;
		}
		public String getPvRatio() {
			return pvRatio;
		}
		public void setPvRatio(String pvRatio) {
			this.pvRatio = pvRatio;
		}
		public String getVisitCount() {
			return visitCount;
		}
		public void setVisitCount(String visitCount) {
			this.visitCount = visitCount;
		}
		public String getVisitorCount() {
			return visitorCount;
		}
		public void setVisitorCount(String visitorCount) {
			this.visitorCount = visitorCount;
		}
		public String getNewVisitorCount() {
			return newVisitorCount;
		}
		public void setNewVisitorCount(String newVisitorCount) {
			this.newVisitorCount = newVisitorCount;
		}
		public String getNewVisitorRatio() {
			return newVisitorRatio;
		}
		public void setNewVisitorRatio(String newVisitorRatio) {
			this.newVisitorRatio = newVisitorRatio;
		}
		public String getIpCount() {
			return ipCount;
		}
		public void setIpCount(String ipCount) {
			this.ipCount = ipCount;
		}
		public String getBounceRatio() {
			return bounceRatio;
		}
		public void setBounceRatio(String bounceRatio) {
			this.bounceRatio = bounceRatio;
		}
		public String getAvgVisitTime() {
			return avgVisitTime;
		}
		public void setAvgVisitTime(String avgVisitTime) {
			this.avgVisitTime = avgVisitTime;
		}
		public String getAvgVisitPages() {
			return avgVisitPages;
		}
		public void setAvgVisitPages(String avgVisitPages) {
			this.avgVisitPages = avgVisitPages;
		}
		
	}
	
	public static class ParseImpl extends BaiduParseSupport<ResultTrendTimeA> {

		@Override
		public ResultTrendTimeA getObjNew() {
			return new ResultTrendTimeA();
		}

		@Override
		protected void parseBody(ResultTrendTimeA result, JSONObject jsonObj) {
			// 一直找到数据节点   /body/data[0]/result
			JSONObject body = jsonObj.getJSONObject("body");
			JSONArray data = null;
			if(body != null){
				data = body.getJSONArray("data");
			}
			
			JSONObject resJsonObj = null;
			if(data != null && !data.isEmpty()){
				JSONObject o1 = data.getJSONObject(0);
				if(o1 != null){
					resJsonObj = o1.getJSONObject("result");
				}
			}
			
			if(resJsonObj == null){
				return;
			}
			
			// 查询字段
			// "fields": ["simple_date_title", "pv_count", "pv_ratio", "visit_count", "visitor_count", "new_visitor_count", "new_visitor_ratio", "ip_count", "bounce_ratio", "avg_visit_time", "avg_visit_pages"]
			String[] fields = resJsonObj.getJSONArray("fields").toArray(new String[0]);
			JSONArray items = resJsonObj.getJSONArray("items");
			JSONArray times = items.getJSONArray(0);
			JSONArray dataList = items.getJSONArray(1);
			
			TrendTimeA item;
			String v;
			JSONArray dataJson;
			
			for(int i = 0, j = dataList.size(); i < j; i++){
				dataJson = dataList.getJSONArray(i);
				
				item = new TrendTimeA();
				item.setTime(times.getJSONArray(i).getString(0));
				
				for(int n = 0, m = fields.length; n < m; n ++){
					if("pv_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setPvCount(v);
					}else if("pv_ratio".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toFloat(dataJson.getString(n - 1))+"";
						item.setPvRatio(v);
					}else if("visit_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setVisitCount(v);
					}else if("visitor_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setVisitorCount(v);
					}else if("new_visitor_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setNewVisitorCount(v);
					}else if("new_visitor_ratio".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toFloat(dataJson.getString(n - 1))+"";
						item.setNewVisitorRatio(v);
					}else if("ip_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setIpCount(v);
					}else if("bounce_ratio".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toFloat(dataJson.getString(n - 1))+"";
						item.setBounceRatio(v);
					}else if("avg_visit_time".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setAvgVisitTime(v);
					}else if("avg_visit_pages".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toFloat(dataJson.getString(n - 1))+"";
						item.setAvgVisitPages(v);
					}
				}
				
				result.list.add(item);
				
				// 默认数据是根据日期倒序排列，反转一下改成正向排序
				Collections.reverse(result.list);
			}
		}
		
	}
}

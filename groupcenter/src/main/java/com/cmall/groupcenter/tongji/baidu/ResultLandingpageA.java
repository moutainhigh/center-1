package com.cmall.groupcenter.tongji.baidu;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.tongji.ResultData;

// 入口页面 visit/landingpage/a
public class ResultLandingpageA extends ResultData{

	private List<LandingpageA> list = new ArrayList<LandingpageA>();
	
	public List<LandingpageA> getList() {
		return list;
	}
	public void setList(List<LandingpageA> list) {
		this.list = list;
	}

	public static class LandingpageA {
		
		private String url;
		private String visitCount ;
		private String visitorCount;
		private String newVisitorCount ;
		private String newVisitorRatio;
		private String ipCount;
		private String outPvCount;
		private String bounceRatio;
		private String avgVisitTime;
		private String avgVisitPages;
		
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
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
		public String getOutPvCount() {
			return outPvCount;
		}
		public void setOutPvCount(String outPvCount) {
			this.outPvCount = outPvCount;
		}
		
	}
	
	public static class ParseImpl extends BaiduParseSupport<ResultLandingpageA> {

		@Override
		public ResultLandingpageA getObjNew() {
			return new ResultLandingpageA();
		}

		@Override
		protected void parseBody(ResultLandingpageA result, JSONObject jsonObj) {
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
			// "fields": ["visit_page_title", "visit_count", "visitor_count", "new_visitor_count", "new_visitor_ratio", "ip_count", "out_pv_count", "bounce_ratio", "avg_visit_time", "avg_visit_pages", "trans_count", "trans_ratio"]
			String[] fields = resJsonObj.getJSONArray("fields").toArray(new String[0]);
			JSONArray items = resJsonObj.getJSONArray("items");
			JSONArray pages = items.getJSONArray(0);
			JSONArray dataList = items.getJSONArray(1);
			
			LandingpageA item;
			String v;
			JSONArray dataJson;
			
			for(int i = 0, j = dataList.size(); i < j; i++){
				dataJson = dataList.getJSONArray(i);
				
				item = new LandingpageA();
				item.setUrl(pages.getJSONArray(i).getJSONObject(0).getString("name"));
				
				for(int n = 0, m = fields.length; n < m; n ++){
					if("visit_count".equalsIgnoreCase(fields[n])){
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
					}else if("out_pv_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setOutPvCount(v);
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
			}
		}
		
	}
}

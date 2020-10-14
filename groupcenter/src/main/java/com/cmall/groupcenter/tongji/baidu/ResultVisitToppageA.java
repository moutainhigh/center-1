package com.cmall.groupcenter.tongji.baidu;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.tongji.ResultData;

// 受访页面 visit/toppage/a
public class ResultVisitToppageA extends ResultData{

	private List<VisitToppageA> list = new ArrayList<VisitToppageA>();
	
	public List<VisitToppageA> getList() {
		return list;
	}
	public void setList(List<VisitToppageA> list) {
		this.list = list;
	}

	public static class VisitToppageA {
		
		private String url;
		private String pvCount;
		private String visitorCount;
		private String ipCount;
		private String visit1Count ;
		private String outwardCount ;
		private String exitCount;
		private String averageStayTime;
		private String exitRatio;
		
		public String getUrl() {
			return url;
		}
		public void setUrl(String url) {
			this.url = url;
		}
		public String getPvCount() {
			return pvCount;
		}
		public void setPvCount(String pvCount) {
			this.pvCount = pvCount;
		}
		public String getVisitorCount() {
			return visitorCount;
		}
		public void setVisitorCount(String visitorCount) {
			this.visitorCount = visitorCount;
		}
		public String getIpCount() {
			return ipCount;
		}
		public void setIpCount(String ipCount) {
			this.ipCount = ipCount;
		}
		public String getVisit1Count() {
			return visit1Count;
		}
		public void setVisit1Count(String visit1Count) {
			this.visit1Count = visit1Count;
		}
		public String getOutwardCount() {
			return outwardCount;
		}
		public void setOutwardCount(String outwardCount) {
			this.outwardCount = outwardCount;
		}
		public String getExitCount() {
			return exitCount;
		}
		public void setExitCount(String exitCount) {
			this.exitCount = exitCount;
		}
		public String getAverageStayTime() {
			return averageStayTime;
		}
		public void setAverageStayTime(String averageStayTime) {
			this.averageStayTime = averageStayTime;
		}
		public String getExitRatio() {
			return exitRatio;
		}
		public void setExitRatio(String exitRatio) {
			this.exitRatio = exitRatio;
		}
		
	}
	
	public static class ParseImpl extends BaiduParseSupport<ResultVisitToppageA> {

		@Override
		public ResultVisitToppageA getObjNew() {
			return new ResultVisitToppageA();
		}

		@Override
		protected void parseBody(ResultVisitToppageA result, JSONObject jsonObj) {
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
			JSONArray pages = items.getJSONArray(0);
			JSONArray dataList = items.getJSONArray(1);
			
			VisitToppageA item;
			String v;
			JSONArray dataJson;
			
			for(int i = 0, j = dataList.size(); i < j; i++){
				dataJson = dataList.getJSONArray(i);
				
				item = new VisitToppageA();
				item.setUrl(pages.getJSONArray(i).getJSONObject(0).getString("name"));
				
				for(int n = 0, m = fields.length; n < m; n ++){
					if("pv_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setPvCount(v);
					}else if("visitor_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setVisitorCount(v);
					}else if("ip_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setIpCount(v);
					}else if("visit1_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setVisit1Count(v);
					}else if("outward_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setOutwardCount(v);
					}else if("exit_count".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setExitCount(v);
					}else if("average_stay_time".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toInt(dataJson.getString(n - 1))+"";
						item.setAverageStayTime(v);
					}else if("exit_ratio".equalsIgnoreCase(fields[n])){
						v = NumberUtils.toFloat(dataJson.getString(n - 1))+"";
						item.setExitRatio(v);
					}
				}
				
				result.list.add(item);
			}
		}
		
	}
}

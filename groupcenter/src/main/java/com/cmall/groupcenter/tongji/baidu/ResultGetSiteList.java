package com.cmall.groupcenter.tongji.baidu;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.tongji.ResultData;

public class ResultGetSiteList extends ResultData {

	private List<SiteInfo> list = new ArrayList<SiteInfo>();
	
	public List<SiteInfo> getList() {
		return list;
	}

	public void setList(List<SiteInfo> list) {
		this.list = list;
	}
	
	public static class SiteInfo {
		private String status;
		private String createTime;
		private String domain;
		private String siteId;
		
		public String getStatus() {
			return status;
		}
		public void setStatus(String status) {
			this.status = status;
		}
		public String getCreateTime() {
			return createTime;
		}
		public void setCreateTime(String createTime) {
			this.createTime = createTime;
		}
		public String getDomain() {
			return domain;
		}
		public void setDomain(String domain) {
			this.domain = domain;
		}
		public String getSiteId() {
			return siteId;
		}
		public void setSiteId(String siteId) {
			this.siteId = siteId;
		}
	}
	
	public static class ParseImpl extends BaiduParseSupport<ResultGetSiteList> {

		@Override
		public void parseBody(ResultGetSiteList result,JSONObject jsonObj) {
			// 一直找到数据节点   /body/data[0]/list
			JSONObject body = jsonObj.getJSONObject("body");
			JSONArray data = null;
			if(body != null){
				data = body.getJSONArray("data");
			}
			
			JSONArray list = null;
			if(data != null && !data.isEmpty()){
				JSONObject o1 = data.getJSONObject(0);
				if(o1 != null){
					list = o1.getJSONArray("list");
				}
			}
			
			if(list != null && !list.isEmpty()){
				SiteInfo siteInfo;
				for(JSONObject o : list.toArray(new JSONObject[0])){
					siteInfo = new SiteInfo();
					siteInfo.setSiteId(o.getString("site_id"));
					siteInfo.setStatus(o.getString("status"));
					siteInfo.setDomain(o.getString("domain"));
					siteInfo.setCreateTime(o.getString("create_time"));
					result.list.add(siteInfo);
				}
			}
		}

		@Override
		public ResultGetSiteList getObjNew() {
			return new ResultGetSiteList();
		}
	}
}

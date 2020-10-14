package com.cmall.groupcenter.tongji.umeng;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.tongji.ResultData;

/**
 * 获取所有App统计数据响应
 * 参考：https://developer.umeng.com/open-api/docs/com.umeng.uapp/umeng.uapp.getAllAppData/umeng.uapp.AllAppData[]/1/2#contHeader
 */
public class ResultGetAllAppData extends ResultData {

	private String todayActivityUsers;
	private String todayNewUsers;
	private String todayLaunches;
	private String yesterdayActivityUsers;
	private String yesterdayNewUsers;
	private String yesterdayLaunches;
	private String yesterdayUniqNewUsers;
	private String yesterdayUniqActiveUsers;
	private String totalUsers;
	
	public String getTodayActivityUsers() {
		return todayActivityUsers;
	}
	public void setTodayActivityUsers(String todayActivityUsers) {
		this.todayActivityUsers = todayActivityUsers;
	}
	public String getTodayNewUsers() {
		return todayNewUsers;
	}
	public void setTodayNewUsers(String todayNewUsers) {
		this.todayNewUsers = todayNewUsers;
	}
	public String getTodayLaunches() {
		return todayLaunches;
	}
	public void setTodayLaunches(String todayLaunches) {
		this.todayLaunches = todayLaunches;
	}
	public String getYesterdayActivityUsers() {
		return yesterdayActivityUsers;
	}
	public void setYesterdayActivityUsers(String yesterdayActivityUsers) {
		this.yesterdayActivityUsers = yesterdayActivityUsers;
	}
	public String getYesterdayNewUsers() {
		return yesterdayNewUsers;
	}
	public void setYesterdayNewUsers(String yesterdayNewUsers) {
		this.yesterdayNewUsers = yesterdayNewUsers;
	}
	public String getYesterdayLaunches() {
		return yesterdayLaunches;
	}
	public void setYesterdayLaunches(String yesterdayLaunches) {
		this.yesterdayLaunches = yesterdayLaunches;
	}
	public String getYesterdayUniqNewUsers() {
		return yesterdayUniqNewUsers;
	}
	public void setYesterdayUniqNewUsers(String yesterdayUniqNewUsers) {
		this.yesterdayUniqNewUsers = yesterdayUniqNewUsers;
	}
	public String getYesterdayUniqActiveUsers() {
		return yesterdayUniqActiveUsers;
	}
	public void setYesterdayUniqActiveUsers(String yesterdayUniqActiveUsers) {
		this.yesterdayUniqActiveUsers = yesterdayUniqActiveUsers;
	}
	public String getTotalUsers() {
		return totalUsers;
	}
	public void setTotalUsers(String totalUsers) {
		this.totalUsers = totalUsers;
	}
	
	public static class ParseImpl extends UMengParseSupport<ResultGetAllAppData> {

		@Override
		public ResultGetAllAppData getObjNew() {
			return new ResultGetAllAppData();
		}

		@Override
		protected void parseBody(ResultGetAllAppData result, JSONObject jsonObj) {
			JSONArray allAppData = jsonObj.getJSONArray("allAppData");
			if(allAppData.size() > 0){
				JSONObject appData = allAppData.getJSONObject(0);
				result.setYesterdayNewUsers(appData.getString("yesterdayNewUsers"));
				result.setYesterdayUniqNewUsers(appData.getString("yesterdayUniqNewUsers"));
				result.setTodayLaunches(appData.getString("todayLaunches"));
				result.setTotalUsers(appData.getString("totalUsers"));
				result.setTodayNewUsers(appData.getString("todayNewUsers"));
				result.setYesterdayUniqActiveUsers(appData.getString("yesterdayUniqActiveUsers"));
				result.setTodayActivityUsers(appData.getString("todayActivityUsers"));
				result.setYesterdayLaunches(appData.getString("yesterdayLaunches"));
				result.setYesterdayActivityUsers(appData.getString("yesterdayActivityUsers"));
			}
		}
		
	}
}

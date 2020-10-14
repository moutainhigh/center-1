package com.cmall.ordercenter.alipay.util;

import org.json.JSONObject;



public class JsonUtil {
	
	/**
	 * 获取JSON某一个key的值
	 * @param rescontent
	 * @param key
	 * @return
	 */
	public static String getJsonValue(String rescontent, String key) {
		JSONObject jsonObject;
		String v = null;
		try {
			jsonObject = new JSONObject(rescontent);
			v = jsonObject.getString(key);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return v;
	}
	
	/**
	 * 获取所有JSON数据
	 * @param rescontent
	 * @return
	 */
	public static JSONObject getJsonValues(String rescontent){
		JSONObject jsonObject;
		try {
			jsonObject = new JSONObject(rescontent);
			return jsonObject;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return null;
	}
}

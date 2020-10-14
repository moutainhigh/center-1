package com.cmall.ordercenter.alipay.wechat;

import java.net.URLDecoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.srnpr.zapcom.basemodel.MDataMap;

public class ClientRequestHandler extends PrepayIdRequestHandler {
	
	
	/**
	 * 自测
	 * @param request
	 * @param response
	 */
	public ClientRequestHandler() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ClientRequestHandler(HttpServletRequest request,
			HttpServletResponse response) {
		super(request, response);
		// TODO Auto-generated constructor stub
	}

	public String getXmlBody() {
		StringBuffer sb = new StringBuffer();
		Set es = super.getAllParameters().entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (!"appkey".equals(k)) {
				sb.append("<" + k + ">" + v + "<" + k + ">" + "\r\n");//\r\n
			}
		}
		return sb.toString();
	}
	
	
	public String getXmlBody(Map<String,String> map) {
		StringBuffer sb = new StringBuffer();
		sb.append("<xml>");
		for(Object oKey : map.keySet()){
			String k = oKey.toString();
			String v = map.get(k);
			if (!"appkey".equals(k)) {
				sb.append("<" + k + ">" + v + "</" + k + ">" + "\r\n");//\r\n
			}
		}
		sb.append("</xml>");
		return sb.toString();
	}
	/**
	 * 自测返回Map
	 * @return
	 */
	public MDataMap getMapBody(){
		MDataMap mDataMap = new MDataMap();
		StringBuffer sb = new StringBuffer();
		Set es = super.getAllParameters().entrySet();
		Iterator it = es.iterator();
		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			String k = (String) entry.getKey();
			String v = (String) entry.getValue();
			if (!"appkey".equals(k)) {
				mDataMap.put(k, v);
			}
		}
		
		return mDataMap;
	}
}

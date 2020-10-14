package com.cmall.groupcenter.service;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webdo.WebUp;
import com.srnpr.zapweb.webmodel.MPageData;
import com.srnpr.zapweb.webmodel.MWebPage;
import com.srnpr.zapweb.webpage.PageExec;


/**
 * 
 * 微公社商户后台-获取页面统计数据用
 * @author GaoYang
 * @CreateDate 2015年7月6日上午10:40:19
 *
 */
public class GetPageDataService extends BaseClass{

	@SuppressWarnings("unchecked")
	public MPageData upChartData(String operateId,Map params){
		
		MWebPage mPage = WebUp.upPage(operateId);//operateId：页面编号

//		HttpServletRequest request = WebSessionHelper.create().upHttpRequest();
//		Map mMaps = request.getParameterMap();
//		MDataMap mReqMap = convertRequest(WebSessionHelper.create().upHttpRequest());
		
		MDataMap mReqMap = new MDataMap();
//		Map <String, String> map = new HashMap<String, String>();
//		map = params;
		
		Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
		while (iterator.hasNext()){
			 Map.Entry<String, String> entry = iterator.next();
//			 System.out.println("key = " + entry.getKey() + " and value = " + entry.getValue());
			 mReqMap.put(entry.getKey(), entry.getValue());
		}
		 
//		for (Map.Entry<String, String> entry : map.entrySet()){
//			System.out.println("key = " + entry.getKey() + " and value = " + entry.getValue());
//			mReqMap = new MDataMap();
//			mReqMap.put(entry.getKey(), entry.getValue());
//		}
		

		PageExec pExec = new PageExec();
		
		MDataMap mOptionMap = new MDataMap();
		MPageData pageData = pExec.upChartData(mPage, mReqMap, mOptionMap);
		
		return pageData;
		
		
	}

	/**
	 * 转换reques的值
	 * 
	 * @param hRequest
	 * @return
	 */
	public MDataMap convertRequest(HttpServletRequest hRequest) {
		MDataMap mReqMap = new MDataMap();
		@SuppressWarnings("unchecked")
		Enumeration<String> eKey = hRequest.getParameterNames();

		while (eKey.hasMoreElements()) {
			String string = eKey.nextElement();
			mReqMap.put(string,
					StringUtils.join(hRequest.getParameterValues(string), ","));
		}

		return mReqMap;
	}
}

package com.cmall.groupcenter.tongji.baidu;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.cmall.groupcenter.tongji.ResultData;
import com.cmall.groupcenter.tongji.ResultParse;
import com.srnpr.zapcom.basehelper.ALibabaJsonHelper;

/**
 * 百度统计API调用主类
 */
public class BaiduTongjiApi {

	static Log LOG = LogFactory.getLog(BaiduTongjiApi.class);
	
	private AccountInfo accountInfo;
	private CloseableHttpClient httpClient;
	private String url = "https://api.baidu.com/json/tongji/v1/ReportService";
	
	public BaiduTongjiApi(AccountInfo accountInfo) {
		super();
		this.accountInfo = accountInfo;
		httpClient = HttpClients.createDefault();
		LOG.debug("Create TongjiApi !");
	}
	
	/**
	 * 获取用户的站点列表
	 */
	public ResultGetSiteList getSiteList(){
		LOG.debug("TongjiApi getSiteList start ...");
		
		String responseText = post(url + "/getSiteList", null);
		//System.out.println(responseText);
		
		ResultGetSiteList result = new ResultGetSiteList.ParseImpl().parse(responseText);
		
		LOG.debug("TongjiApi getSiteList end ...");
		return result;
	}
	
	/**
	 * 获取站点报告
	 */
	public <T extends ResultData> T getReportData(MetricsType type, Map<String, String> bodyParam, ResultParse<T> parse){
		Map<String, String> newParam = new HashMap<String, String>(bodyParam);
		newParam.put("method", type.getMethod());
		newParam.put("metrics", type.getFileds());
		return getReportData(newParam,parse);
	}
	
	/**
	 * 获取站点报告
	 */
	protected <T extends ResultData> T getReportData(Map<String, String> bodyParam, ResultParse<T> parse){
		LOG.debug("TongjiApi getData start ... " + "[" + bodyParam.get("site_id") + ", "+ bodyParam.get("method") + "]");
		
		String responseText = post(url + "/getData", bodyParam);
		T result = parse.parse(responseText);
		
		//System.out.println(responseText);
		
		LOG.debug("TongjiApi getData start ... " + "[" + bodyParam.get("site_id") + ", "+ bodyParam.get("method") + "]");
		return result;
	}
	
	protected String post(String url, Map<String, String> dataBody){
		Map<String, String> dataHeader = new HashMap<String, String>();
		dataHeader.put("username", accountInfo.getUsername());
		dataHeader.put("password", accountInfo.getPassword());
		dataHeader.put("token", accountInfo.getToken());
		dataHeader.put("account_type", accountInfo.getAccountType());
		return post(url, dataHeader, dataBody);
	}
	
	protected String post(String url, Map<String, String> dataHeader, Map<String, String> dataBody){
		Map<String, String> reqHeader = new HashMap<String, String>();
		reqHeader.put("UUID", accountInfo.getUuid());
		reqHeader.put("USERID", accountInfo.getUserId());
		reqHeader.put("Content-Type", "data/json;charset=UTF-8");
		return post(url, reqHeader, dataHeader, dataBody);
	}
	
	protected String post(String url, Map<String, String> reqHeader, Map<String, String> dataHeader, Map<String, String> dataBody){
		Map<String,Object> dataMap = new HashMap<String,Object>();
		dataMap.put("header", dataHeader);
		dataMap.put("body", dataBody);
		
		String jsonText = ALibabaJsonHelper.toJson(dataMap);
		
		byte[] b = doPost(url, reqHeader, jsonText);
		if(b == null) {
			return "";
		}
		
		String responseText = org.apache.commons.codec.binary.StringUtils.newStringUtf8(b);
		LOG.debug("POST Resp success text-> " + responseText + "\r\n");
		
		return responseText;
	}
	
	protected byte[] doPost(String url, Map<String, String> reqHeader, String body){
		byte[] bs = null;
		CloseableHttpResponse response = null;
		try {
			LOG.debug("POST Req  -> " + url + "\r\n:" + body + "\r\n");
			
			HttpPost request = new HttpPost(url);
			request.setEntity(new StringEntity(body,"UTF-8"));
			
			for (String sKey : reqHeader.keySet()) {
				request.setHeader(sKey, reqHeader.get(sKey));
			}
			
			response = httpClient.execute(request);
			
			if(response.getStatusLine().getStatusCode() == 200){
				bs = IOUtils.toByteArray(response.getEntity().getContent());
				LOG.debug("POST Resp success -> " + url + "\r\n");
			}else{
				LOG.warn("POST Resp error -> status code: " + response.getStatusLine().getStatusCode() + ", " + url + "\r\n");
			}
			return bs;
		} catch (Exception e) {
			LOG.warn("",e);
			return null;
		} finally {
			IOUtils.closeQuietly(response);
		}
	}
	
}

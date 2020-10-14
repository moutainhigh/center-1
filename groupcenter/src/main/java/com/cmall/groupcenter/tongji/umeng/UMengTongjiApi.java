package com.cmall.groupcenter.tongji.umeng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.crypto.Mac;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import com.cmall.groupcenter.tongji.ResultData;
import com.cmall.groupcenter.tongji.ResultParse;

/**
 * 友盟统计OPEN API文档： https://developer.umeng.com/open-api/ns/com.umeng.uapp/apply
 */
public class UMengTongjiApi {

	static Log LOG = LogFactory.getLog(UMengTongjiApi.class);
	
	// 5287104
	private String apikey;
	// asDLvLiBQb
	private String apiSecurity;
	
	private CloseableHttpClient httpClient;
	
	public UMengTongjiApi(String apikey, String apiSecurity) {
		super();
		this.apikey = apikey;
		this.apiSecurity = apiSecurity;
		httpClient = HttpClients.createDefault();
	}

	/**
	 * 获取统计数据
	 * @param apiName  例：com.umeng.uapp:umeng.uapp.getYesterdayData
	 * @param param
	 * @param parse
	 * @return
	 */
	public <T extends ResultData> T getReportData(String apiName, Map<String, String> param, ResultParse<T> parse){
		String urlPath = "param2/1/"+apiName.replace(":", "/")+"/"+apikey;
		
		setSignature(urlPath, param);
		
		String url = "https://gateway.open.umeng.com/openapi/" + urlPath;
		String responseText = doPost(url, param);
		//System.out.println(responseText);
		
		T result = parse.parse(responseText);
		
		return result;
	}
	
	/**
	 * 设置签名内容，签名规则：https://developer.umeng.com/docs/67641/detail/67643
	 * @param apiPath
	 * @param param
	 */
	private void setSignature(String urlPath, Map<String, String> param){
		List<String> paramList = new ArrayList<String>();
		for(Entry<String, String> entry : param.entrySet()){
			paramList.add(entry.getKey()+entry.getValue());
		}
		
		Collections.sort(paramList);
		
		String s = urlPath + StringUtils.join(paramList,"");
		String sign = null;
		try {
			Mac mac = HmacUtils.getHmacSha1(apiSecurity.getBytes("UTF-8"));
			byte[] b = mac.doFinal(s.getBytes("UTF-8"));
			sign = new String(Hex.encodeHex(b, false));
			
			LOG.debug("UMengApi Sign: ["+s+"]["+sign+"]");
		} catch (Exception e) {
			LOG.error("", e);
		}
		
		param.put("_aop_signature", sign);
	}
	
	protected String doPost(String url,Map<String, String> param){
		byte[] bs = null;
		CloseableHttpResponse response = null;
		try {
			LOG.debug("POST Req  -> " + url + "\r\n");
			
			HttpPost request = new HttpPost(url);
			
			List<NameValuePair> list = new ArrayList<NameValuePair>();
			for(Entry<String, String> entry : param.entrySet()){
				list.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			
			request.setEntity(new UrlEncodedFormEntity(list, "UTF-8"));
			response = httpClient.execute(request);
			
			if(response.getStatusLine().getStatusCode() == 200){
				bs = IOUtils.toByteArray(response.getEntity().getContent());
				LOG.debug("POST Resp success -> " + url + "\r\n");
			}else{
				LOG.warn("POST Resp error -> status code: " + response.getStatusLine().getStatusCode() + ", " + url + "\r\n");
			}
			return new String(bs,"UTF-8");
		} catch (Exception e) {
			LOG.warn("",e);
			return null;
		} finally {
			IOUtils.closeQuietly(response);
		}
	}
}

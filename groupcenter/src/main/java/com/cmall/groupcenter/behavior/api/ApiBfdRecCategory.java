package com.cmall.groupcenter.behavior.api;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.net.ssl.SSLContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.cmall.groupcenter.behavior.config.BfdRecResultConfig;
import com.cmall.groupcenter.behavior.request.BfdRecResultRequest;
import com.cmall.groupcenter.behavior.service.BfdRecResultInfoService;
import com.cmall.groupcenter.behavior.util.BeanCompenent;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapcom.topdo.TopUp;

/**
 * 获取百分点推荐分类
 */
public class ApiBfdRecCategory {
	
	public List<String> doProcess(String uid, String platform){
		BfdRecResultConfig config = new BfdRecResultConfig();
		BfdRecResultInfoService service = new BfdRecResultInfoService();
		
		String appkey;
		
		if("IOS".equalsIgnoreCase(platform)){
			appkey = TopUp.upConfig("groupcenter.ios");
		}else{
			appkey = TopUp.upConfig("groupcenter.android");
		}
		
		BfdRecResultRequest request = service.initBfdRecResultRquest(uid, "", "ios_maybelove",appkey,config);
		request.setFmt("");
		request.setBidlst(TopUp.upConfig("groupcenter.bfd_cat_rec_id_"+platform));
		request.setReq(TopUp.upConfig("groupcenter.bfd_cat_req_rec_id_"+platform));
		
		MDataMap mDataMap = BeanCompenent.objectTOMap(request);
		String content = null;
		JSONArray ja = null;
		try {
			content = request(config.getRequestPath(),mDataMap);
			
			ja = new JSONArray(content);
			if(ja.optInt(0) != 0){
				return new ArrayList<String>();
			}
			
			ja = ja.optJSONArray(2).optJSONArray(0);
			if(ja.optInt(0) != 0){
				return new ArrayList<String>();
			}
			
			ja = ja.optJSONArray(3);
		} catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<String>();
		}
		
		HashSet<String> vs = new HashSet<String>();
		if(ja != null){
			JSONArray arr;
			for(int i = 0; i< ja.length(); i++){
				arr = ja.optJSONArray(i);
				if(arr != null && arr.length() >= 4 && StringUtils.isNotBlank(arr.optString(arr.length()-1))){
					vs.add(arr.optString(arr.length()-1));
				}
			}
		}
		
		return new ArrayList<String>(vs);
	}
	
	private String request(String url,MDataMap mDataMap) throws Exception{
		HttpClientBuilder hClientBuilder = HttpClientBuilder.create();
		SSLContext sslContext = SSLContexts.custom().useTLS().loadTrustMaterial(null, new TrustStrategy() {
			@Override
			public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
				return true;
			}
		}).build();
		hClientBuilder.setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER));

		// 最多运行3秒的等待，防止阻塞时间过长导致API接口响应过慢
		RequestConfig requestConfig = RequestConfig.custom()  
                .setConnectionRequestTimeout(3000)  
                .setConnectTimeout(3000)  
                .setSocketTimeout(3000)
                .build(); 
		
		hClientBuilder.setDefaultRequestConfig(requestConfig);
		CloseableHttpClient httpclient = null;
		CloseableHttpResponse response = null;
		
		try{
			httpclient = hClientBuilder.build();
			
			List<NameValuePair> nvps = new ArrayList<NameValuePair>();
			for (String sKey : mDataMap.keySet()) {
				nvps.add(new BasicNameValuePair(sKey, mDataMap.get(sKey)));
			}

			HttpEntity httpEntity = new UrlEncodedFormEntity(nvps, TopConst.CONST_BASE_ENCODING);
			HttpPost httppost = new HttpPost(url);
			httppost.setHeader("Connection", "close");
			httppost.setEntity(httpEntity);
			
			response = httpclient.execute(httppost);
			if(response.getStatusLine().getStatusCode() != 200){
				LogFactory.getLog(getClass()).warn("请求百分点推荐分类失败: \n"+EntityUtils.toString(response.getEntity()));
				return null;
			}else{
				return EntityUtils.toString(response.getEntity());
			}
		} finally {
			if(response != null) response.close();
			if(httpclient != null) httpclient.close();
		}

	}

}

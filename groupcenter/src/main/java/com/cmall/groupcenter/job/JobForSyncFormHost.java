package com.cmall.groupcenter.job;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.WebClientRequest;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapcom.topdo.TopDir;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 每天凌晨5点同步节目主持人信息
 * @remark 
 * @author sunyan
 * @date 2020年5月28日
 */
public class JobForSyncFormHost extends RootJob {

	private final String JY_URL = bConfig("groupcenter.rsync_homehas_url");	
	
	public synchronized void doExecute(JobExecutionContext context) {
		//为避免重复同步，先删除当前日期的同步数据
		DbUp.upTable("pc_tv_host").dataDelete(" create_time >= DATE_FORMAT(NOW(),'%Y-%m-%d')",new MDataMap(),"");
		JSONObject infoMap = new JSONObject();
//		infoMap.put("start_time", "2020-05-11 00:00:00");
//		infoMap.put("end_time", "2020-05-28 00:00:00");
		try {
			String jyResult = getHttps(JY_URL + "getFormHostInfo", infoMap.toString());
			JSONObject jyObject = JSONObject.parseObject(jyResult);
			boolean success = jyObject.getBoolean("success");
			if(success) {
				JSONArray ja = jyObject.getJSONArray("custList");
				Iterator it = ja.iterator();
				while (it.hasNext()) {
					JSONObject jsonObject = (JSONObject) it.next();
					String form_id = jsonObject.getString("FORM_ID");
					String user_id = jsonObject.getString("USER_ID");
					String emp_nm = jsonObject.getString("EMP_NM");
					MDataMap upMap = new MDataMap();
					upMap.put("form_id", form_id);
					upMap.put("user_id", user_id);
					upMap.put("host_nm", emp_nm);
					DbUp.upTable("pc_tv_host").dataInsert(upMap);
					
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 			

	}
	
	private String getHttps(String sUrl, String sRequestString)
			throws ParseException, IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException {
		WebClientRequest webClientRequest = new WebClientRequest();

		String sDir = bConfig("groupcenter.homehas_key");

		if (StringUtils.isEmpty(sDir)) {
			TopDir topDir = new TopDir();
			sDir = topDir.upCustomPath("") + "tomcat.keystore";
		}
//		sDir = "C:/etc/zapsrnpr/c__users_bloodline/tomcat.keystore";
		
		webClientRequest.setFilePath(sDir);
		webClientRequest.setUrl(sUrl);

		HttpEntity httpEntity = new StringEntity(sRequestString,
				TopConst.CONST_BASE_ENCODING);

		webClientRequest.setConentType("application/json");

		webClientRequest
				.setPassword(bConfig("groupcenter.rsync_homehas_password"));


		webClientRequest.setHttpEntity(httpEntity);

		String sResponseString = WebClientSupport.upHttpsPost(webClientRequest);

		return sResponseString;
	}
	
}

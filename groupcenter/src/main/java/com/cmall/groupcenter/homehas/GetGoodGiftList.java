package com.cmall.groupcenter.homehas;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.homehas.model.ModelGoodGiftInfo;
import com.cmall.groupcenter.homehas.model.RequestGoodGift;
import com.cmall.groupcenter.homehas.model.ResponseGoodGift;
import com.srnpr.xmassystem.load.LoadGiftSkuInfo;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.WebClientRequest;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapcom.topdo.TopDir;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 获取家有赠品数据
 * 
 * @author xiegj
 */
public class GetGoodGiftList extends BaseClass{

	/**
	 * 调用处理逻辑 返回操作
	 * 
	 * @param sRequestString
	 * @return
	 * @throws ParseException
	 * @throws IOException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws CertificateException
	 */
	private String getHttps(String sUrl, String sRequestString)
			throws ParseException, IOException, KeyManagementException,
			NoSuchAlgorithmException, KeyStoreException, CertificateException {
		WebClientRequest webClientRequest = new WebClientRequest();

		String sDir = bConfig("groupcenter.homehas_key");

		if (StringUtils.isEmpty(sDir)) {
			TopDir topDir = new TopDir();
			sDir = topDir.upCustomPath("") + "tomcat.keystore";
		}
		webClientRequest.setFilePath(sDir);
		webClientRequest.setUrl(sUrl);

		HttpEntity httpEntity = new StringEntity(sRequestString,
				TopConst.CONST_BASE_ENCODING);

		webClientRequest.setConentType("application/json");

		webClientRequest
				.setPassword(bConfig("groupcenter.rsync_homehas_password"));

		bLogInfo(0, EntityUtils.toString(httpEntity));

		webClientRequest.setHttpEntity(httpEntity);

		String sResponseString = WebClientSupport.upHttpsPost(webClientRequest);

		return sResponseString;
	}

	/**
	 * 获取请求的url
	 * 
	 * @return
	 */
	private String upRequestUrl() {
		return bConfig("groupcenter.rsync_homehas_url")
				+ "getGoodGiftList";
	}


	public List<ModelGoodGiftInfo> doRsync(String goodId,String siteNo) {

		String sCode = WebHelper.upCode("LCRL");
		List<ModelGoodGiftInfo> resultList = new ArrayList<ModelGoodGiftInfo>();
		MDataMap addMap = new MDataMap();
		
		try {

			String sUrl = upRequestUrl();

			RequestGoodGift tRequest = new RequestGoodGift();
			tRequest.setSubsystem("app");
			tRequest.setGood_id(goodId);//要获取赠品的商品编号
			tRequest.setSite_no(siteNo);

			JsonHelper<IRsyncRequest> requestJsonHelper = new JsonHelper<IRsyncRequest>();
			String sRequest = requestJsonHelper.ObjToString(tRequest);

			MDataMap mInsertMap = new MDataMap();
			// 插入日志表调用的日志记录
			mInsertMap.inAllValues("code", sCode, "rsync_target", "getGoodGiftList", "rsync_url", sUrl, "request_data",
					sRequest, "request_time", FormatHelper.upDateTime());
			addMap.putAll(mInsertMap);
			
			// 插入日志记录表
			//DbUp.upTable("lc_rsync_log").dataInsert(mInsertMap);

			String sResponseString = getHttps(sUrl, sRequest);
			// bLogInfo(0, sResponseString);

			mInsertMap.inAllValues("response_time", FormatHelper.upDateTime(),
					"response_data", sResponseString);
			addMap.putAll(mInsertMap);
			
			// 更新响应内容和响应时间
			//DbUp.upTable("lc_rsync_log").dataUpdate(mInsertMap,"response_time,response_data", "code");

			// IRsyncResponse iRsyncResponse=null;
  
			JsonHelper<ResponseGoodGift> responseJsonHelper = new JsonHelper<ResponseGoodGift>();

			ResponseGoodGift tResponse = responseJsonHelper.GsonFromJson(sResponseString,
					new ResponseGoodGift());

			resultList = tResponse.getResult();
			
			// 更新处理完成时间
			mInsertMap.inAllValues("process_time", FormatHelper.upDateTime(),
					"process_data", sResponseString, "status_data",
					"ok", "flag_success",true ? "1" : "0", "process_num",String.valueOf(resultList.size()), "success_num",String.valueOf(resultList.size()));
			addMap.putAll(mInsertMap);
			
			/*
			DbUp.upTable("lc_rsync_log").dataUpdate(mInsertMap,
							"process_time,process_data,status_data,flag_success,process_num,success_num",
							"code");
			*/
			
			if(StringUtils.isNotBlank(goodId)){
				new LoadGiftSkuInfo().deleteInfoByCode(goodId);
			}
		} catch (Exception e) {
			e.printStackTrace();

			// 如果失败更新错误日志信息
			MDataMap mErrorMap = new MDataMap();
			mErrorMap.inAllValues("code", sCode, "flag_success", "0",
					"process_time", FormatHelper.upDateTime(),
					"error_expection", e.getMessage());
			addMap.putAll(mErrorMap);
			
			/*
			DbUp.upTable("lc_rsync_log").dataUpdate(mErrorMap,
					"process_time,error_expection,flag_success", "code");
			*/
			DbUp.upTable("lc_rsync_log").dataInsert(addMap);
		}
		return resultList;
	}

}

package com.cmall.groupcenter.aszs;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import org.apache.http.ParseException;
import org.json.JSONObject;

import com.cmall.groupcenter.aszs.config.RsyncConfigNoticei4ActivationInfor;
import com.cmall.groupcenter.aszs.request.RsyncRequestNoticei4ActivationInfor;
import com.cmall.groupcenter.aszs.response.RsyncResponseNoticei4ActivationInfor;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;



/** 
* @ClassName: RsyncNoticei4ActivationInfor 
* @Description: 通知爱思助手用户激活
* @author 张海生
* @date 2016-3-4 上午10:37:34 
*  
*/
public class RsyncNoticei4ActivationInfor extends Rsynci4<RsyncConfigNoticei4ActivationInfor, RsyncRequestNoticei4ActivationInfor, RsyncResponseNoticei4ActivationInfor>{

	private final static RsyncConfigNoticei4ActivationInfor RSYNC_CONFIG_I4_TRACE_ORDER = new RsyncConfigNoticei4ActivationInfor();
	
	private RsyncRequestNoticei4ActivationInfor requestI4 =new  RsyncRequestNoticei4ActivationInfor();
	
	private RsyncResponseNoticei4ActivationInfor processResult = null;
	
	
	@Override
	public RsyncConfigNoticei4ActivationInfor upConfig() {
		return RSYNC_CONFIG_I4_TRACE_ORDER;
	}

	@Override
	public RsyncRequestNoticei4ActivationInfor upRsyncRequest() {
		return requestI4;
	}
	
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
	private String getHttps(String sUrl)
			throws Exception {

		String mrequest=getsignMap();
		WebClientSupport support = new WebClientSupport();
		String sResponseString = support.doGet(sUrl+mrequest);
		return sResponseString;
	}

	private String getsignMap(){
		
		String param = "aisicid=" + requestI4.getAisi() + "&aisi="
				+ requestI4.getAisi() + "&appid=" + requestI4.getAppid()
				+ "&mac=" + requestI4.getMac() + "&idfa=" + requestI4.getIdfa()
				+ "&os=" + requestI4.getOs() + "&rt=" + requestI4.getRt();
//		try {
//			param = URLEncoder.encode(param, "UTF-8");
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//		}
		return param;
	}
	
	/**
	 * 获取请求的url
	 * 
	 * @return
	 */
	private String upRequestUrl() {
		return bConfig("groupcenter.rsync_i4_url");
	}
	
	/**
	 * 获取调用接口之后的结果
	 * 
	 * @return
	 */
	public RsyncResponseNoticei4ActivationInfor upProcessResult() {
		return processResult;
	}

	
	public boolean doRsync() {

		String sCode = WebHelper.upCode("I4");

		try {

			String sUrl = upRequestUrl();

			String sRequest = "";
			
			RsyncRequestNoticei4ActivationInfor tRequest = upRsyncRequest();

			MDataMap mInsertMap = new MDataMap();
			// 插入日志表调用的日志记录
			mInsertMap.inAllValues("code", sCode, "rsync_target", upConfig()
					.getRsyncTarget(), "rsync_url", sUrl, "request_data",
					sRequest, "request_time", FormatHelper.upDateTime());
			// 插入日志记录表
			DbUp.upTable("lc_rsync_i4_log").dataInsert(mInsertMap);

			String sResponseString = getHttps(sUrl);
			mInsertMap.inAllValues("response_time", FormatHelper.upDateTime(),
					"response_data", sResponseString);

			// 更新响应内容和响应时间
			DbUp.upTable("lc_rsync_i4_log").dataUpdate(mInsertMap,
					"response_time,response_data", "code");

			// IRsyncResponse iRsyncResponse=null;

			RsyncResponseNoticei4ActivationInfor tResponse = upResponseObject();

			JsonHelper<RsyncResponseNoticei4ActivationInfor> responseJsonHelper = new JsonHelper<RsyncResponseNoticei4ActivationInfor>();
//			JSONObject ob = new JSONObject(sResponseString);
//			sResponseString = String.valueOf(ob.get("Gou.Channel.Order.Response"));
			tResponse = responseJsonHelper.GsonFromJson(sResponseString,
					tResponse);

			processResult = tResponse;
			RsyncResult rsyncResult = doProcess(tRequest, tResponse);

			// 更新处理完成时间
			mInsertMap.inAllValues("process_time", FormatHelper.upDateTime(),
					"process_data", rsyncResult.upJson(), "status_data",
					rsyncResult.getStatusData(), "flag_success",
					rsyncResult.upFlagTrue() ? "1" : "0", "process_num",
					"1", "success_num", "1");
			DbUp.upTable("lc_rsync_i4_log")
					.dataUpdate(
							mInsertMap,
							"process_time,process_data,status_data,flag_success,process_num,success_num",
							"code");

			if (rsyncResult.getResultCode() == 1) {
//				Thread.sleep(20000);
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();

			// 如果失败更新错误日志信息
			MDataMap mErrorMap = new MDataMap();
			mErrorMap.inAllValues("code", sCode, "flag_success", "0",
					"process_time", FormatHelper.upDateTime(),
					"error_expection", e.getMessage());
			DbUp.upTable("lc_rsync_i4_log").dataUpdate(mErrorMap,
					"process_time,error_expection,flag_success", "code");
		}
		return false;
	}
	
	@Override
	public RsyncResult doProcess(RsyncRequestNoticei4ActivationInfor tRequest,RsyncResponseNoticei4ActivationInfor tResponse) {
		
		RsyncResult rsyncResult = new RsyncResult();
		
		if("false".equals(tResponse.getSuccess())) {
			rsyncResult.setResultCode(918519135);
			rsyncResult.setResultMessage(tResponse.getMessage());
			return rsyncResult;
		}
		
		return rsyncResult;
	}
	
	@Override
	public RsyncResponseNoticei4ActivationInfor upResponseObject() {
		return new RsyncResponseNoticei4ActivationInfor();
	}
}

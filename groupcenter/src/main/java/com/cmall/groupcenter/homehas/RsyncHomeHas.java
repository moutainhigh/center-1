package com.cmall.groupcenter.homehas;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Date;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;

import com.cmall.groupcenter.groupface.IRsyncConfig;
import com.cmall.groupcenter.groupface.IRsyncDateCheck;
import com.cmall.groupcenter.groupface.IRsyncDo;
import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncRequestAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncResponseAddOrder;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.WebClientRequest;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapcom.topdo.TopDir;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步家有接口的基类
 * 
 * @author srnpr
 * 
 * @param <TConfig>
 * @param <TRequest>
 * @param <TResponse>
 */
public abstract class RsyncHomeHas<TConfig extends IRsyncConfig, TRequest extends IRsyncRequest, TResponse extends IRsyncResponse>
		extends BaseClass implements IRsyncDo<TConfig, TRequest, TResponse> {

	private TResponse processResult = null;

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
		
		// 获取配置的接口超时时间
		String timeoutKey = "groupcenter.homehas_timeout_"+upConfig().getRsyncTarget();
		// 先判断一下是否存在，直接获取不存在的配置参数会引起系统不断重新加载配置文件
		if(TopConfig.Instance.containsKey(timeoutKey)){
			webClientRequest.setTimeout(NumberUtils.toInt(bConfig(timeoutKey)));
		}

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
				+ upConfig().getRsyncTarget();
	}

	/**
	 * 获取最近一次成功处理的状态数据
	 * 
	 * @return
	 */
	public String upLastSuccessStatusData() {

		MDataMap mLog = DbUp.upTable("lc_rsync_log").oneWhere("status_data",
				"-request_time", "", "rsync_target",
				upConfig().getRsyncTarget(), "flag_success", "1");

		String sReturn = "";

		if (mLog != null && mLog.containsKey("status_data")) {
			sReturn = mLog.get("status_data");
		}

		return sReturn;

	}

	public boolean doRsync() {

		String sCode = WebHelper.upCode("LCRL");

		MDataMap addMap = new MDataMap();
		
		try {

			String sUrl = upRequestUrl();

			TRequest tRequest = upRsyncRequest();

			JsonHelper<IRsyncRequest> requestJsonHelper = new JsonHelper<IRsyncRequest>();
			String sRequest = requestJsonHelper.ObjToString(tRequest);
//System.out.println("sRequest===="+sRequest);
			MDataMap mInsertMap = new MDataMap();
			// 插入日志表调用的日志记录
			mInsertMap.inAllValues("code", sCode, "rsync_target", upConfig()
					.getRsyncTarget(), "rsync_url", sUrl, "request_data",
					sRequest, "request_time", FormatHelper.upDateTime());
			
			addMap.putAll(mInsertMap);
			
			if(isSaveLog()){
				// 插入日志记录表
				DbUp.upTable("lc_rsync_log").dataInsert(mInsertMap);
			}

			String sResponseString = getHttps(sUrl, sRequest);
			//System.out.println("sResponseString-----------"+sResponseString);
			// bLogInfo(0, sResponseString);

			mInsertMap.inAllValues("response_time", FormatHelper.upDateTime(),
					"response_data", sResponseString);
			
			
			if(sResponseString.length()>1000000)
			{
				mInsertMap.inAllValues("response_data","RsyncHomeHas:too large for rsync homehas,not save ~");
			}
			
			addMap.putAll(mInsertMap);

			if(isSaveLog()){
				// 更新响应内容和响应时间
				DbUp.upTable("lc_rsync_log").dataUpdate(mInsertMap,
						"response_time,response_data", "code");
			}

			// IRsyncResponse iRsyncResponse=null;

			TResponse tResponse = upResponseObject();

			JsonHelper<TResponse> responseJsonHelper = new JsonHelper<TResponse>();

			tResponse = responseJsonHelper.GsonFromJson(sResponseString.trim(),tResponse);

			processResult = tResponse;

			RsyncResult rsyncResult = doProcess(tRequest, tResponse);

			// 更新处理完成时间
			mInsertMap.inAllValues("process_time", FormatHelper.upDateTime(),
					"process_data", rsyncResult.upJson(), "status_data",
					rsyncResult.getStatusData(), "flag_success",
					rsyncResult.upFlagTrue() ? "1" : "0", "process_num",
					String.valueOf(rsyncResult.getProcessNum()), "success_num",
					String.valueOf(rsyncResult.getSuccessNum()));
			
			addMap.putAll(mInsertMap);
			
			if(isSaveLog()){
				DbUp.upTable("lc_rsync_log")
				.dataUpdate(
						mInsertMap,
						"process_time,process_data,status_data,flag_success,process_num,success_num",
						"code");
			}

			if (rsyncResult.getResultCode() == 1) {
				
				if(isUpdateStaticValue()){
					//更新静态标量值
					RsyncStatic rStatic=new RsyncStatic();
					rStatic.setCodeValue(this.getClass().getName());
					
					WebHelper.updateStaticValue(rStatic, rsyncResult.getStatusData());
				}
				
				return true;
			}

		} catch (Exception e) {
			e.printStackTrace();

			// 如果失败更新错误日志信息
			MDataMap mErrorMap = new MDataMap();
			mErrorMap.inAllValues("code", sCode, "flag_success", "0",
					"process_time", FormatHelper.upDateTime(),
					"error_expection", String.valueOf(e));
			addMap.putAll(mErrorMap);
			
			if(isSaveLog()){
				DbUp.upTable("lc_rsync_log").dataUpdate(mErrorMap,
						"process_time,error_expection,flag_success", "code");
			}else{
				// 不记录日志的接口调用需要记录一下异常时的日志数据
				DbUp.upTable("lc_rsync_log").dataInsert(addMap);
			}
		}

		return false;
	}

	/**
	 * 获取日期的检查计算结果 该方法仅适用于传入的是时间范围的家有接口函数 会自动调整输入输出参数
	 * 
	 * @param iRsyncDateCheck
	 * @return
	 */
	public RsyncDateCheck upDateCheck(IRsyncDateCheck iRsyncDateCheck) {
		RsyncDateCheck rsyncDateCheck = new RsyncDateCheck();

		
		RsyncStatic rStatic=new RsyncStatic();
		rStatic.setCodeValue(this.getClass().getName());
		
		String sStatusDate = WebHelper.upStaticValue(rStatic);
		
		
		//如果是第一次  则从最后一次的日志中取出最后一次同步时间 以兼容历史处理逻辑
		if(StringUtils.isBlank(sStatusDate))
		{
			sStatusDate = upLastSuccessStatusData();
		}
		
		//String sStatusDate = upLastSuccessStatusData();
		// 判断如果没有最近一次成功的开始时间 则使用最最原始的开始时间
		if (StringUtils.isEmpty(sStatusDate)) {
			sStatusDate = iRsyncDateCheck.getBaseStartTime();
		}

		Date dStateDate = DateHelper.parseDate(sStatusDate);

		// 将开始时间减去回退时间 以兼容异常情况
		Date dStart = DateUtils.addSeconds(dStateDate,
				-iRsyncDateCheck.getBackSecond());
		rsyncDateCheck.setStartDate(DateHelper.upDate(dStart));

		Date dEnd = DateUtils.addSeconds(dStateDate,
				iRsyncDateCheck.getMaxStepSecond());

		Date dNowDate = new Date();
		// 判断如果结束时间晚于当前时间 则将结束时间设置为当前时间
		if (dEnd.after(dNowDate)) {
			dEnd = dNowDate;
		}

		rsyncDateCheck.setEndDate(DateHelper.upDate(dEnd));

		return rsyncDateCheck;

	}

	/**
	 * 获取调用接口之后的结果
	 * 
	 * @return
	 */
	public TResponse upProcessResult() {
		return processResult;
	}
	
	/**
	 * 执行完成后是否更新静态标量表，默认返回true<br>
	 * @return 
	 * 	  true 更新<br> 
	 * 	  false 不更新
	 */
	protected boolean isUpdateStaticValue(){
		return true;
	}
	
	/**
	 * 
	 * 是否需要记录日志到数据库
	 * @return
	 */
	protected boolean isSaveLog(){
		return true;
	}

	public MWebResult doRsync2() {
		
		MWebResult result = new MWebResult();

		String sCode = WebHelper.upCode("LCRL");

		MDataMap addMap = new MDataMap();
		
		try {

			String sUrl = upRequestUrl();

			TRequest tRequest = upRsyncRequest();

			JsonHelper<IRsyncRequest> requestJsonHelper = new JsonHelper<IRsyncRequest>();
			String sRequest = requestJsonHelper.ObjToString(tRequest);
//System.out.println("sRequest===="+sRequest);
			MDataMap mInsertMap = new MDataMap();
			// 插入日志表调用的日志记录
			mInsertMap.inAllValues("code", sCode, "rsync_target", upConfig()
					.getRsyncTarget(), "rsync_url", sUrl, "request_data",
					sRequest, "request_time", FormatHelper.upDateTime());
			
			addMap.putAll(mInsertMap);
			
			if(isSaveLog()){
				// 插入日志记录表
				DbUp.upTable("lc_rsync_log").dataInsert(mInsertMap);
			}

			String sResponseString = getHttps(sUrl, sRequest);
			//System.out.println("sResponseString-----------"+sResponseString);
			// bLogInfo(0, sResponseString);

			mInsertMap.inAllValues("response_time", FormatHelper.upDateTime(),
					"response_data", sResponseString);
			
			
			if(sResponseString.length()>1000000)
			{
				mInsertMap.inAllValues("response_data","RsyncHomeHas:too large for rsync homehas,not save ~");
			}
			
			addMap.putAll(mInsertMap);

			if(isSaveLog()){
				// 更新响应内容和响应时间
				DbUp.upTable("lc_rsync_log").dataUpdate(mInsertMap,
						"response_time,response_data", "code");
			}

			// IRsyncResponse iRsyncResponse=null;

			TResponse tResponse = upResponseObject();

			JsonHelper<TResponse> responseJsonHelper = new JsonHelper<TResponse>();

			tResponse = responseJsonHelper.GsonFromJson(sResponseString.trim(),tResponse);

			processResult = tResponse;

			RsyncResult rsyncResult = doProcess2((RsyncRequestAddOrder)tRequest, (RsyncResponseAddOrder)tResponse);
			

			// 更新处理完成时间
			mInsertMap.inAllValues("process_time", FormatHelper.upDateTime(),
					"process_data", rsyncResult.upJson(), "status_data",
					rsyncResult.getStatusData(), "flag_success",
					rsyncResult.upFlagTrue() ? "1" : "0", "process_num",
					String.valueOf(rsyncResult.getProcessNum()), "success_num",
					String.valueOf(rsyncResult.getSuccessNum()));
			
			addMap.putAll(mInsertMap);
			
			if(isSaveLog()){
				DbUp.upTable("lc_rsync_log")
				.dataUpdate(
						mInsertMap,
						"process_time,process_data,status_data,flag_success,process_num,success_num",
						"code");
			}

			if (rsyncResult.getResultCode() == 1) {
				
				if(isUpdateStaticValue()){
					//更新静态标量值
					RsyncStatic rStatic=new RsyncStatic();
					rStatic.setCodeValue(this.getClass().getName());
					
					WebHelper.updateStaticValue(rStatic, rsyncResult.getStatusData());
				}
				
				return result;
			}else {
				result.setResultCode(rsyncResult.getResultCode());
				result.setResultMessage(rsyncResult.getResultMessage());
			}

		} catch (Exception e) {
			e.printStackTrace();

			// 如果失败更新错误日志信息
			MDataMap mErrorMap = new MDataMap();
			mErrorMap.inAllValues("code", sCode, "flag_success", "0",
					"process_time", FormatHelper.upDateTime(),
					"error_expection", String.valueOf(e));
			addMap.putAll(mErrorMap);
			
			if(isSaveLog()){
				DbUp.upTable("lc_rsync_log").dataUpdate(mErrorMap,
						"process_time,error_expection,flag_success", "code");
			}else{
				// 不记录日志的接口调用需要记录一下异常时的日志数据
				DbUp.upTable("lc_rsync_log").dataInsert(addMap);
			}
			
			// 调用异常需要记录一下异常原因
			result.setResultMessage(mErrorMap.get("error_expection"));
			result.setResultCode(0);
		}

		return result;
	}
	
	
public RsyncResult doProcess2(RsyncRequestAddOrder tRequest, RsyncResponseAddOrder tResponse) {
		
		String statusCode=tResponse.getStatus();//状态码
		
		RsyncResult mWebResult = new RsyncResult();
		String Web_ord_id=tRequest.getWeb_ord_id();
		
		if(!tResponse.isSuccess()){
			mWebResult.setResultCode(918501004);
			mWebResult.setResultMessage(tResponse.getMessage());
			return mWebResult;
		}
		
		//把家有返回的数据保存一份
		String order_code = tRequest.getWeb_ord_id();
		String ord_id = tResponse.getOrd_id();
		String dlv_add_seq = tResponse.getDlv_add_seq();
		String cust_id = tResponse.getCust_id();
		String create_time = DateUtil.getSysDateTimeString();
		
		// 如果有返回订单号则更新到表中
		if(StringUtils.isNotBlank(ord_id)){
			//更新order表的out_order_code
			DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_code",order_code,"out_order_code",ord_id), "out_order_code", "order_code");
		}
		
		// 状态码非空且不是S00则表示失败，解决黑名单用户下单success返回true的问题
		if(StringUtils.isNotBlank(tResponse.getStatus()) && !"S00".equalsIgnoreCase(tResponse.getStatus())){
			mWebResult.setResultCode(918501004);
			mWebResult.setResultMessage(tResponse.getMessage());
			return mWebResult;
		}
		
		MDataMap dataMap=new MDataMap();
		dataMap.put("order_code", order_code);
		if(StringUtils.isEmpty(ord_id)) {
			ord_id = "BLANK-" + System.currentTimeMillis();
		}
		dataMap.put("ord_id", ord_id);
		if(cust_id!=null){
			dataMap.put("cust_id", cust_id);
		}
		if(dlv_add_seq!=null){
			dataMap.put("dlv_add_seq", dlv_add_seq);
		}
		
		dataMap.put("create_time", create_time);
		
		DbUp.upTable("oc_order_homehas").dataInsert(dataMap);
		
		MDataMap orderInfo=DbUp.upTable("oc_orderinfo").one("order_code",order_code);
		String buyer_code = orderInfo.get("buyer_code");
		
		//DbUp.upTable("mc_extend_info_homehas").dataExec("update mc_extend_info_homehas set homehas_code=:homehas_code where member_code=:buyer_code and homehas_code='' ", new MDataMap("homehas_code",cust_id,"buyer_code",buyer_code));
		//DbUp.upTable("mc_extend_info_homepool").dataExec("update mc_extend_info_homepool set old_code=:homehas_code where member_code=:buyer_code and old_code='' ", new MDataMap("homehas_code",cust_id,"buyer_code",buyer_code));
		//DbUp.upTable("mc_extend_info_star").dataExec("update mc_extend_info_star set old_code=:homehas_code where member_code=:buyer_code and old_code='' ", new MDataMap("homehas_code",cust_id,"buyer_code",buyer_code));
		
		//DbUp.upTable("mc_extend_info_homehas").oneWhere("", "", sWhere, sParams);
		
		// 保存返回的用户编号
		if(StringUtils.isNotBlank(buyer_code) && StringUtils.isNotBlank(cust_id)){
			if(DbUp.upTable("mc_extend_info_homehas").count("member_code", buyer_code, "homehas_code", cust_id) == 0){
				MDataMap homehas = new MDataMap("member_code", buyer_code, "homehas_code", cust_id);
				DbUp.upTable("mc_extend_info_homehas").dataInsert(homehas);
			}
		}
		
		return new RsyncResult();
	}
	
	
	
}

package com.cmall.groupcenter.kjt;

import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.http.ParseException;

import com.cmall.groupcenter.groupface.IRsyncConfig;
import com.cmall.groupcenter.groupface.IRsyncDateCheck;
import com.cmall.groupcenter.groupface.IRsyncDo;
import com.cmall.groupcenter.groupface.IRsyncRequest;
import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.systemcenter.bill.HexUtil;
import com.cmall.systemcenter.bill.MD5Util;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.WebClientSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 同步跨境通接口的基类
 * 
 * @author srnpr
 * 
 * @param <TConfig>
 * @param <TRequest>
 * @param <TResponse>
 */
public abstract class RsyncKjt<TConfig extends IRsyncConfig, TRequest extends IRsyncRequest, TResponse extends IRsyncResponse>
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
			throws Exception {

		MDataMap mrequest=getsignMap(sRequestString);

		String sResponseString = WebClientSupport.upPost(sUrl, mrequest);

		return sResponseString;
	}

	private MDataMap getsignMap(String requestStr){
		MDataMap dataMap = new MDataMap();
		dataMap.put("method", upConfig().getRsyncTarget());
		dataMap.put("format", "json");
		dataMap.put("version", bConfig("groupcenter.rsync_kjt_version"));
		dataMap.put("appid", bConfig("groupcenter.rsync_kjt_appid"));
		dataMap.put("timestamp", DateUtil.getSysDateTimeString(DateUtil.sdfDateTimeTamp));
		dataMap.put("nonce", String.valueOf(new Random().nextInt(10000000)));
		dataMap.put("data", requestStr);
		List<String> list = new ArrayList<String>();
		for (Iterator<String> iter = dataMap.keySet().iterator(); iter.hasNext();) {
			String name = iter.next();
			String value =  dataMap.get(name);

			if(name.equals("data")){
				value = URLEncoder.encode(requestStr);
			}
			
			list.add(name + "=" + value);
		}
		Collections.sort(list); // 对List内容进行排序
		StringBuffer str = new StringBuffer();
		for (String nameString : list) {
			str.append(nameString + "&");
		}
		dataMap.put("sign", HexUtil.toHexString(MD5Util.md5(str.substring(0, str.toString().length() - 1)+"&"+bConfig("groupcenter.rsync_kjt_password"))));
		return dataMap;
	}
	
	/**
	 * 获取请求的url
	 * 
	 * @return
	 */
	private String upRequestUrl() {
		return bConfig("groupcenter.rsync_kjt_url");
	}

	/**
	 * 获取最近一次成功处理的状态数据
	 * 
	 * @return
	 */
	public String upLastSuccessStatusData() {

		MDataMap mLog = DbUp.upTable("lc_rsync_kjt_log").oneWhere("status_data",
				"-zid", "", "rsync_target",
				upConfig().getRsyncTarget(), "flag_success", "1");

		String sReturn = "";

		if (mLog != null && mLog.containsKey("status_data")) {
			sReturn = mLog.get("status_data");
		}

		return sReturn;

	}

	public boolean doRsync() {

		String sCode = WebHelper.upCode("KCRL");

		try {

			String sUrl = upRequestUrl();

			TRequest tRequest = upRsyncRequest();

			JsonHelper<IRsyncRequest> requestJsonHelper = new JsonHelper<IRsyncRequest>();
			String sRequest = requestJsonHelper.ObjToString(tRequest);

			
			MDataMap mInsertMap = new MDataMap();
			// 插入日志表调用的日志记录
			mInsertMap.inAllValues("code", sCode, "rsync_target", upConfig()
					.getRsyncTarget(), "rsync_url", sUrl, "request_data",
					sRequest, "request_time", FormatHelper.upDateTime());
			// 插入日志记录表
			DbUp.upTable("lc_rsync_kjt_log").dataInsert(mInsertMap);

			String sResponseString = getHttps(sUrl, sRequest);
			mInsertMap.inAllValues("response_time", FormatHelper.upDateTime(),
					"response_data", sResponseString);

			// 更新响应内容和响应时间
			DbUp.upTable("lc_rsync_kjt_log").dataUpdate(mInsertMap,
					"response_time,response_data", "code");

			// IRsyncResponse iRsyncResponse=null;

			TResponse tResponse = upResponseObject();

			JsonHelper<TResponse> responseJsonHelper = new JsonHelper<TResponse>();

			tResponse = responseJsonHelper.GsonFromJson(sResponseString,
					tResponse);

			processResult = tResponse;
			RsyncResult rsyncResult = doProcess(tRequest, tResponse);

			// 更新处理完成时间
			mInsertMap.inAllValues("process_time", FormatHelper.upDateTime(),
					"process_data", rsyncResult.upJson(), "status_data",
					rsyncResult.getStatusData(), "flag_success",
					rsyncResult.upFlagTrue() ? "1" : "0", "process_num",
					String.valueOf(rsyncResult.getProcessNum()), "success_num",
					String.valueOf(rsyncResult.getSuccessNum()));
			DbUp.upTable("lc_rsync_kjt_log")
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
			DbUp.upTable("lc_rsync_kjt_log").dataUpdate(mErrorMap,
					"process_time,error_expection,flag_success", "code");
		}
//		try {
//			Thread.sleep(20000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		return false;
	}

	/**
	 * 获取日期的检查计算结果 该方法仅适用于传入的是时间范围的跨境通接口函数 会自动调整输入输出参数
	 * 
	 * @param iRsyncDateCheck
	 * @return
	 */
	public RsyncDateCheck upDateCheck(IRsyncDateCheck iRsyncDateCheck) {
		RsyncDateCheck rsyncDateCheck = new RsyncDateCheck();

		String sStatusDate = upLastSuccessStatusData();
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

	
	
}

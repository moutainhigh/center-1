package com.cmall.groupcenter.mlg;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
public abstract class RsyncMlg<TConfig extends IRsyncConfig, TRequest extends IRsyncRequest, TResponse extends IRsyncResponse>
		extends BaseClass implements IRsyncDo<TConfig, TRequest, TResponse> {


	/**
	 * 获取最近一次成功处理的状态数据
	 * 
	 * @return
	 */
	public String upLastSuccessStatusData() {

		MDataMap mLog = DbUp.upTable("lc_rsync_mlg_log").oneWhere("status_data",
				"-zid", "", "rsync_target",
				upConfig().getRsyncTarget(), "flag_success", "1");

		String sReturn = "";

		if (mLog != null && mLog.containsKey("status_data")) {
			sReturn = mLog.get("status_data");
		}

		return sReturn;

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

}

package com.cmall.groupcenter.homehas;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.atomikos.beans.PropertyException;
import com.atomikos.beans.PropertyUtils;
import com.cmall.groupcenter.homehas.config.RsyncConfigGetTVByDate;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncModelTVInfo;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetTVByDate;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetTVByDate;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步一定时间范围内的TV信息
 * 
 * @author xiegj
 * 
 */
public class RsyncGetTVByDate
		extends
		RsyncHomeHas<RsyncConfigGetTVByDate, RsyncRequestGetTVByDate, RsyncResponseGetTVByDate> {

	final static RsyncConfigGetTVByDate CONFIG_GET_TV_BY_DATE = new RsyncConfigGetTVByDate();

	public RsyncConfigGetTVByDate upConfig() {
		return CONFIG_GET_TV_BY_DATE;
	}

	public RsyncRequestGetTVByDate upRsyncRequest() {

		RsyncRequestGetTVByDate requestGetTVByDate = new RsyncRequestGetTVByDate();

		RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
		requestGetTVByDate.setStart_date(rsyncDateCheck.getStartDate()
				.substring(0, 10).replace("-", ""));
		requestGetTVByDate.setEnd_date(DateHelper.upDateTimeAdd(DateHelper.parseDate(rsyncDateCheck.getEndDate()), 5, 2)
				.substring(0, 10).replace("-", ""));
		
	

		return requestGetTVByDate;

	}

	public RsyncResult doProcess(RsyncRequestGetTVByDate tRequest,
			RsyncResponseGetTVByDate tResponse) {

		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getResult() != null) {
				result.setProcessNum(tResponse.getResult().size());
			} else {
				result.setProcessNum(0);

			}

		}

		// 开始循环处理结果数据
		if (result.upFlagTrue()) {

			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {

				// 设置预期处理数量
				result.setProcessNum(tResponse.getResult().size());

				for (RsyncModelTVInfo TVInfo : tResponse.getResult()) {
					MWebResult mResult = saveTVData(TVInfo);

					// 如果成功则将成功计数加1
					if (mResult.upFlagTrue()) {
						iSuccessSum++;

					} else {

						if (result.getResultList() == null) {
							result.setResultList(new ArrayList<Object>());
						}

						result.getResultList().add(mResult.getResultMessage());
					}

				}

				result.setProcessData(bInfo(918501102, result.getProcessNum(),
						iSuccessSum, result.getProcessNum() - iSuccessSum));

			}

		}

		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {
			// 设置处理成功数量
			result.setSuccessNum(iSuccessSum);
			// 特殊处理 由于时间格式不对 状态数据需要切换掉
			RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
			result.setStatusData(rsyncDateCheck.getEndDate());
		}

		return result;

	}

	private MWebResult saveTVData(RsyncModelTVInfo info) {
		MWebResult result = new MWebResult();
		try {
			
			
			//格式化部分数据
			if(info.getForm_fr_date()!=null&&info.getForm_fr_date().length()>=19){
				info.setForm_fr_date(StringUtils.left(info.getForm_fr_date(), 19));//去掉毫秒
			}
			if(info.getForm_end_date()!=null&&info.getForm_end_date().length()>=19){
				info.setForm_end_date(StringUtils.left(info.getForm_end_date(), 19));//去掉毫秒
			}
			String from_end_date="";
			try {
				int form_good_mis=Integer.valueOf(info.getForm_good_mis());
				Date form_fr_date=DateUtil.convertToDate(info.getForm_fr_date(), DateUtil.DATE_FORMAT_DATETIME);
				Calendar calendar=Calendar.getInstance();
				calendar.setTime(form_fr_date);
				calendar.add(Calendar.MINUTE, form_good_mis);
				from_end_date=DateUtil.toString(calendar.getTime(), DateUtil.DATE_FORMAT_DATETIME);
			} catch (Exception e) {
				bLogError(0, "convert from_end_date is fail:"+e.getMessage());
			} 
			
			
			//改用节目编号判断
			Map<String, Object> count = DbUp.upTable("pc_tv").dataSqlOne("select count(*) as sl from pc_tv where form_fr_date='" + info.getForm_fr_date() + "'", new MDataMap());
			if (Integer.valueOf(count.get("sl").toString()) == 0) {//如果没有则插入
				Map<String, Object> map = PropertyUtils.getProperties(info);
				map.put("create_time", DateUtil.getSysDateTimeString());
//				map.put("from_end_date", from_end_date);
				DbUp.upTable("pc_tv").dataInsert(new MDataMap(map));
			}else{//如果有则更新
				Map<String, Object> map = PropertyUtils.getProperties(info);
				map.put("create_time", DateUtil.getSysDateTimeString());
				DbUp.upTable("pc_tv").dataUpdate(new MDataMap(map), "", "form_fr_date");
			}
		} catch (PropertyException e) {
			result.inErrorMessage(918515401, info.toString());
			e.printStackTrace();
		}

		return result;
	}

	public RsyncResponseGetTVByDate upResponseObject() {

		return new RsyncResponseGetTVByDate();
	}
}

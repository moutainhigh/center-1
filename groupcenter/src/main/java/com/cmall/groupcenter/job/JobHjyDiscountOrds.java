package com.cmall.groupcenter.job;


import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncCouponInfo;
import com.cmall.groupcenter.homehas.RsyncStatic;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 定时4.57.查询惠家有折扣券订单接口
 */
public class JobHjyDiscountOrds extends RootJob {

	public void doExecute(JobExecutionContext context) {
		/** 活动时间的范围控制，只拉取固定时间的订单数据 */
		String startTime = (String)DbUp.upTable("zw_define").dataGet("define_remark", "", new MDataMap("define_dids","4699232800030007"));
		String endTime = (String)DbUp.upTable("zw_define").dataGet("define_remark", "", new MDataMap("define_dids","4699232800030008"));
		
		// 上次结束时间
		RsyncStatic rStatic=new RsyncStatic();
		rStatic.setCodeValue(RsyncCouponInfo.class.getName());
		String sStatusDate = WebHelper.upStaticValue(rStatic);
		
		Date start = null;
		Date end = null;
		Date sEnd = null;
		try {
			start = DateUtils.parseDate(startTime, new String[]{"yyyy-MM-dd HH:mm:ss"});
		} catch (Exception e) {}
		try {
			end = DateUtils.parseDate(endTime, new String[]{"yyyy-MM-dd HH:mm:ss"});
		} catch (Exception e) {}
		try {
			sEnd = DateUtils.parseDate(sStatusDate, new String[]{"yyyy-MM-dd HH:mm:ss"});
		} catch (Exception e) {}
		
		// 还没到有效开始时间时直接忽略
		if(start != null && start.compareTo(new Date()) > 0){
			return;
		}
		
		// 已经超过有效结束时间时直接忽略
		if(sEnd != null && end != null && sEnd.compareTo(end) >= 0){
			return;
		}
		
		new RsyncCouponInfo().doRsync();
		
	}
}

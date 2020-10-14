package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigRsyncCouponInfo;
import com.cmall.groupcenter.homehas.model.RsyncRequestSyncCouponInfo;
import com.cmall.groupcenter.homehas.model.RsyncResponseRsyncCouponInfo;
import com.cmall.groupcenter.homehas.model.RsyncResponseRsyncCouponInfo.CouponInfo;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;
import com.cmall.groupcenter.homehas.model.RsyncResult;

/**
 * 按时间定时同步折扣券信息
 * @author cc
 *
 */
public class RsyncCouponInfo extends RsyncHomeHas<RsyncConfigRsyncCouponInfo, RsyncRequestSyncCouponInfo, RsyncResponseRsyncCouponInfo>{

	final static RsyncConfigRsyncCouponInfo CONFIG_COUPON_INFO = new RsyncConfigRsyncCouponInfo();
	
	@Override
	public RsyncConfigRsyncCouponInfo upConfig() {
		return CONFIG_COUPON_INFO;
	}

	@Override
	public RsyncRequestSyncCouponInfo upRsyncRequest() {
		// 返回输入参数
		RsyncRequestSyncCouponInfo request = new RsyncRequestSyncCouponInfo();

		RsyncStatic rStatic=new RsyncStatic();
		rStatic.setCodeValue(this.getClass().getName());
		
		String sStatusDate = WebHelper.upStaticValue(rStatic);
		if(StringUtils.isBlank(sStatusDate))
		{
			sStatusDate = FormatHelper.upDateTime();
		}
		
		Date dStateDate = DateHelper.parseDate(sStatusDate);
		// 倒推15分钟，兼容服务器时间差的问题
		dStateDate = DateUtils.addMinutes(dStateDate, -15);
		
		// 从上次的日期开始查询30分钟内的数据
		Date dEnd = DateUtils.addSeconds(dStateDate, 1800);

		/** 活动时间的范围控制，只拉取固定时间的订单数据 */
		String startTime = (String)DbUp.upTable("zw_define").dataGet("define_remark", "", new MDataMap("define_dids","4699232800030007"));
		String endTime = (String)DbUp.upTable("zw_define").dataGet("define_remark", "", new MDataMap("define_dids","4699232800030008"));
		
		Date start = null;
		Date end = null;
		try {
			start = DateUtils.parseDate(startTime, new String[]{"yyyy-MM-dd HH:mm:ss"});
		} catch (Exception e) {}
		try {
			end = DateUtils.parseDate(endTime, new String[]{"yyyy-MM-dd HH:mm:ss"});
		} catch (Exception e) {}
		
		// 重置开始时间
		if(start != null && start.compareTo(dStateDate) > 0){
			dStateDate = start;
			dEnd = DateUtils.addSeconds(dStateDate, 1800);
		}
		
		Date dNowDate = new Date();
		// 判断如果结束时间晚于当前时间 则将结束时间设置为当前时间
		if (dEnd.after(dNowDate)) {
			dEnd = dNowDate;
		}
		
		// 重置结束时间
		if(end != null && end.compareTo(dEnd) < 0){
			dEnd = end;
		}
		
		request.setStartDate(DateHelper.upDate(dStateDate));
		request.setEndDate(DateHelper.upDate(dEnd));

		return request;
	}

	@Override
	public RsyncResult doProcess(RsyncRequestSyncCouponInfo tRequest, RsyncResponseRsyncCouponInfo tResponse) {
		RsyncResult result = new RsyncResult();
		// 定义成功的数量合计
		int iSuccessSum = 0;
		
		//获取同步家有折扣券信息信息失败
		if(tResponse != null && tResponse.getSuccess().equals("false")) {
			result.setProcessData(bInfo(918501103, FormatHelper.upDateTime(), tResponse.getMsg()));
		}
		
		if (tResponse != null && tResponse.getItems() != null && tResponse.getSuccess().equals("true")) {
			result.setProcessNum(tResponse.getItems().size());
		} else {
			result.setProcessNum(0);
		}
		
		// 判断有需要处理的数据才开始处理
		if (result.getProcessNum() > 0) {
			// 设置预期处理数量
			result.setProcessNum(tResponse.getItems().size());
			for(CouponInfo couponInfo : tResponse.getItems()) {				
				String lock_uid=WebHelper.addLock(1000*60, couponInfo.getOrderCode());
				MWebResult mResult = reginCouponInfo(couponInfo);
				WebHelper.unLock(lock_uid);
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
			result.setProcessData(bInfo(918501102, result.getProcessNum(),iSuccessSum, result.getProcessNum() - iSuccessSum));
		}
		
		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {
			// 设置处理成功数量
			result.setSuccessNum(iSuccessSum);
			result.setStatusData(tRequest.getEndDate());
		}

		return result;
	}

	@Override
	public RsyncResponseRsyncCouponInfo upResponseObject() {
		RsyncResponseRsyncCouponInfo responseCouponInfo = new RsyncResponseRsyncCouponInfo();
		return responseCouponInfo;
	}
	
	/**
	 * 同步家有折扣券信息
	 * @param couponInfo
	 * @return
	 */
	private MWebResult reginCouponInfo(CouponInfo couponInfo) {
		MWebResult mWebResult = new MWebResult();
		String orderCode = StringUtils.trimToEmpty(couponInfo.getOrderCode());
		String mobile = StringUtils.trimToEmpty(couponInfo.getPhone());
		String memberCode = StringUtils.trimToEmpty(couponInfo.getMemberCode());
		String custId = StringUtils.trimToEmpty(couponInfo.getCustId());
		String productCode = StringUtils.trimToEmpty(couponInfo.getProductCode());
		String productCount = StringUtils.trimToEmpty(couponInfo.getProductCount());
		String productPrice = StringUtils.trimToEmpty(couponInfo.getProductPrice());
		
		MDataMap whereMap=new MDataMap();
		whereMap.put("orderCode", orderCode);
		whereMap.put("productCode", productCode);
		int count = DbUp.upTable("oc_order_ld_coupon_task").dataCount("ld_order_code=:orderCode and product_code=:productCode", whereMap);
		/**
		 * 处理重复
		 */
		if(count > 0) {
			mWebResult.inErrorMessage(918505107, orderCode);
		}
		MDataMap insertMap=new MDataMap();
		
		if(mWebResult.upFlagTrue()){
			insertMap.inAllValues("phone",   mobile, "member_code",   memberCode,
								  "cust_id", custId, "ld_order_code", orderCode,
								  "product_code", productCode, "product_count", productCount,
								  "sell_price", productPrice, "coupon_type", "",
								  "status", "0", "exec_num", "0", "notify_flag", "0",
								  "remark", "",
								  "create_time",FormatHelper.upDateTime(),
								  "update_time",FormatHelper.upDateTime());
			DbUp.upTable("oc_order_ld_coupon_task").dataInsert(insertMap);
		}					
		
		return mWebResult;
	}
}

package com.cmall.groupcenter.homehas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigGiftVoucherStatus;
import com.cmall.groupcenter.homehas.model.RsyncModelGiftVoucherDetail;
import com.cmall.groupcenter.homehas.model.RsyncRequestGiftVoucherStatus;
import com.cmall.groupcenter.homehas.model.RsyncResponseGiftVoucherStatus;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步ld用户礼金券状态明细
 * @author cc
 *
 */
public class RsyncGiftVoucherStatus  extends RsyncHomeHas<RsyncConfigGiftVoucherStatus, RsyncRequestGiftVoucherStatus, RsyncResponseGiftVoucherStatus>{

	final static RsyncConfigGiftVoucherStatus rsyncConfigGiftVoucherStatus = new RsyncConfigGiftVoucherStatus();
	
	@Override
	public RsyncConfigGiftVoucherStatus upConfig() {
		return rsyncConfigGiftVoucherStatus;
	}

	@Override
	public RsyncRequestGiftVoucherStatus upRsyncRequest() {
		RsyncRequestGiftVoucherStatus request = new RsyncRequestGiftVoucherStatus();
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

		Date dNowDate = new Date();
		// 判断如果结束时间晚于当前时间 则将结束时间设置为当前时间
		if (dEnd.after(dNowDate)) {
			dEnd = dNowDate;
		}		
		
		request.setStart_date(DateHelper.upDate(dStateDate));
		request.setEnd_date(DateHelper.upDate(dEnd));

		return request;
	}

	@Override
	public RsyncResult doProcess(RsyncRequestGiftVoucherStatus tRequest, RsyncResponseGiftVoucherStatus tResponse) {
		RsyncResult result = new RsyncResult();
		// 定义成功的数量合计
		int iSuccessSum = 0;
			
		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getLjqList() != null) {
				result.setProcessNum(tResponse.getLjqList().size());
			} else {
				result.setProcessNum(0);
			}
		}
		
		// 开始循环处理结果数据
		if (result.upFlagTrue()) {
			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {
				
				// 设置预期处理数量
				result.setProcessNum(tResponse.getLjqList().size());
				
				for(RsyncModelGiftVoucherDetail detail : tResponse.getLjqList()) {
					String lock_uid=WebHelper.addLock(1000*60, detail.getLJ_CODE().toString());
					MWebResult mResult = reginRsyncGiftVoucherDetail(detail);
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
			}
		}
		
		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {
			// 设置处理成功数量
			result.setSuccessNum(iSuccessSum);
			result.setStatusData(tRequest.getEnd_date());
		}
		
		return result;
	}

	/**
	 * 同步惠家有礼金券明细
	 * @param detail
	 * @return
	 */
	private MWebResult reginRsyncGiftVoucherDetail(RsyncModelGiftVoucherDetail detail) {
		MWebResult mWebResult = new MWebResult();
		
		String initial_money = detail.getLJ_AMT() == null ? "0" : detail.getLJ_AMT().toString(); //初始金额
		String limit_money = detail.getORD_AMT() == null ? "0" : detail.getORD_AMT().toString(); //限定金额
		String status = detail.getSY_VL() == null ? "1" : (detail.getSY_VL().equals("Y") ? "1" : "0");  //是否已使用，1:是；0:否
		String start_time = convertDate(detail.getFR_DATE());  //开始时间
		String end_time = convertDate(detail.getEND_DATE());  //结束时间
		String create_time = convertDate(detail.getETR_DATE());  //创建时间
		String update_time = convertDate(detail.getMDF_DATE());  //更新时间
		String big_order_code = detail.getLJ_REL_ID() == null ? "" : detail.getLJ_REL_ID().toString();  //大订单编号
		String out_coupon_code = detail.getLJ_CODE() == null ? "" : detail.getLJ_CODE().toString(); //礼金券编号  格式：活动编号-客代-礼金券序号
		String cust_id = detail.getCUST_ID() == null ? "" : detail.getCUST_ID().toString(); //LD客户代码  根据用户编号查询客代号是否存在，不存在则进行关联
		String member_code = detail.getMEMBERCODE() == null ? "" : detail.getMEMBERCODE().toString(); //惠家有用户编号
		String hjy_ord_id = detail.getHJY_ORD_ID() == null ? "" : detail.getHJY_ORD_ID().toString(); //惠家有关联订单号
		String mobile = detail.getMOBILE() == null ? "" : detail.getMOBILE().toString(); //用户手机号
		if(cust_id == null || "".equals(cust_id)) {
			//同步礼金券明细失败，{0}的LD客户代码为空
			mWebResult.inErrorMessage(918505112, out_coupon_code);
			return mWebResult;
		}
		if(mobile == null || "".equals(mobile)) {
			//同步礼金券明细失败，{0}的手机号码为空
			mWebResult.inErrorMessage(918505113, out_coupon_code);
			return mWebResult;
		} else {
			// 过滤非手机号码的账户
			if(!mobile.matches("1\\d{10}")){
				mWebResult.inErrorMessage(918505114, out_coupon_code);
				return mWebResult;
			}
			
			MDataMap loginInfo = DbUp.upTable("mc_login_info").one("manage_code","SI2003","login_name",mobile);
			if(loginInfo != null) {
				member_code = loginInfo.get("member_code");
			} else {
				MWebResult result = new HomehasSupport().registerWithResult(mobile, RandomStringUtils.randomNumeric(8));
				
				if(result.getResultCode() != 1) {
					mWebResult.inErrorMessage(918505131, cust_id);
					return mWebResult;
				}
				member_code = ((MDataMap)result.getResultObject()).get("memberCode");
			}				
		}			
		
		String outActivityCode = "";
		String[] tmpArr = out_coupon_code.split("-");
		if(tmpArr.length == 3) {
			outActivityCode = tmpArr[0];
			MDataMap mActivityMap = DbUp.upTable("oc_activity").one("out_activity_code", outActivityCode);
			if(mActivityMap == null) {
				//同步礼金券明细失败，礼金券活动不存在
				mWebResult.inErrorMessage(918505110);
			}
			String activity_code = mActivityMap.get("activity_code").toString();
			MDataMap mCouponTypeMap = DbUp.upTable("oc_coupon_type").one("activity_code", activity_code);
			if(mCouponTypeMap == null) {
				//同步礼金券明细失败，礼金券类型不存在
				mWebResult.inErrorMessage(918505111);
			}
			
			String coupon_type_code = mCouponTypeMap.get("coupon_type_code").toString();
			MDataMap mCouponInfoMap = DbUp.upTable("oc_coupon_info").one("out_coupon_code", out_coupon_code, "member_code", member_code);
			if(mCouponInfoMap == null) {
				//新增
				String coupon_code = WebHelper.upCode("CP");
				String uid=UUID.randomUUID().toString().replace("-", "");
				MDataMap aCouponInfoMap = new MDataMap();
				aCouponInfoMap.put("uid", uid);
				aCouponInfoMap.put("coupon_type_code", coupon_type_code);
				aCouponInfoMap.put("activity_code", activity_code);
				aCouponInfoMap.put("coupon_code", coupon_code);
				aCouponInfoMap.put("member_code", member_code.toString());
				aCouponInfoMap.put("initial_money", initial_money);
				aCouponInfoMap.put("surplus_money", initial_money);
				aCouponInfoMap.put("limit_money", limit_money);
				aCouponInfoMap.put("status", status);
				aCouponInfoMap.put("start_time", start_time);
				aCouponInfoMap.put("end_time", end_time);
				aCouponInfoMap.put("create_time", create_time);
				aCouponInfoMap.put("update_time", update_time);
				aCouponInfoMap.put("big_order_code", big_order_code);
				aCouponInfoMap.put("out_coupon_code", out_coupon_code);
				DbUp.upTable("oc_coupon_info").dataInsert(aCouponInfoMap);				
			} else {
				//修改
				MDataMap eCouponInfoMap = new MDataMap();
				eCouponInfoMap.put("coupon_type_code", coupon_type_code);
				eCouponInfoMap.put("activity_code", activity_code);
				eCouponInfoMap.put("member_code", member_code.toString());
				eCouponInfoMap.put("initial_money", initial_money);
				eCouponInfoMap.put("surplus_money", initial_money);
				eCouponInfoMap.put("limit_money", limit_money);
				eCouponInfoMap.put("status", status);
				eCouponInfoMap.put("start_time", start_time);
				eCouponInfoMap.put("end_time", end_time);
				eCouponInfoMap.put("update_time", update_time);
				eCouponInfoMap.put("big_order_code", big_order_code);
				eCouponInfoMap.put("out_coupon_code", out_coupon_code);
				DbUp.upTable("oc_coupon_info").dataUpdate(eCouponInfoMap, "coupon_type_code,activity_code,member_code,initial_money,surplus_money,limit_money,status,start_time,end_time,update_time,big_order_code", "out_coupon_code,member_code");
			}			
		} else {
			mWebResult.inErrorMessage(918505108);
		}		
		
		return mWebResult;
	}
	
	@Override
	public RsyncResponseGiftVoucherStatus upResponseObject() {
		RsyncResponseGiftVoucherStatus responseGiftVoucherStatus = new RsyncResponseGiftVoucherStatus();
		return responseGiftVoucherStatus;
	}

	private String convertDate(String date) {
		SimpleDateFormat format = new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		if(date == null) {
			return format.format(new Date());
		}		
		return date;
	}
}

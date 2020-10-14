package com.cmall.groupcenter.homehas;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.homehas.config.RsyncConfigGiftVoucherActivity;
import com.cmall.groupcenter.homehas.model.RsyncModelActivity;
import com.cmall.groupcenter.homehas.model.RsyncRequestGiftVoucherActivity;
import com.cmall.groupcenter.homehas.model.RsyncResponseGiftVoucherActivity;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.membercenter.memberdo.MemberConst;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步LD礼金券活动
 * @author cc
 *
 */
public class RsyncGiftVoucherActivity extends RsyncHomeHas<RsyncConfigGiftVoucherActivity, RsyncRequestGiftVoucherActivity, RsyncResponseGiftVoucherActivity>{

	private final static RsyncConfigGiftVoucherActivity rsyncConfigGiftVoucherActivity = new RsyncConfigGiftVoucherActivity();
	
	@Override
	public RsyncConfigGiftVoucherActivity upConfig() {		
		return rsyncConfigGiftVoucherActivity;
	}

	private RsyncRequestGiftVoucherActivity rsyncRequestGiftVoucherActivity = new RsyncRequestGiftVoucherActivity();
	
	@Override
	public RsyncRequestGiftVoucherActivity upRsyncRequest() {

		return rsyncRequestGiftVoucherActivity;
	}

	@Override
	public RsyncResult doProcess(RsyncRequestGiftVoucherActivity tRequest, RsyncResponseGiftVoucherActivity tResponse) {
		RsyncResult result = new RsyncResult();
		// 定义成功的数量合计
		int iSuccessSum = 0;
			
		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getEventList() != null) {
				result.setProcessNum(tResponse.getEventList().size());
			} else {
				result.setProcessNum(0);
			}
		}
		
		// 开始循环处理结果数据
		if (result.upFlagTrue()) {
			
			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {
				
				// 设置预期处理数量
				result.setProcessNum(tResponse.getEventList().size());
				
				for(RsyncModelActivity activity : tResponse.getEventList()) {
					
					String lock_uid=WebHelper.addLock(1000*60, activity.getEVENT_ID().toString());
					MWebResult mResult = reginRsyncActivity(activity);
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
		}

		return result;
	}
	
	/**
	 * 同步礼金券活动
	 * @param activity
	 * @return
	 */
	private MWebResult reginRsyncActivity(RsyncModelActivity activity) {
		
		MWebResult mWebResult = new MWebResult();
		
		String out_activity_code = activity.getEVENT_ID() == null ? "" :activity.getEVENT_ID().toString();  //LD活动编号，同步优惠券明细时根据LD活动编号来获取活动和优惠券类型
		String activity_name = activity.getEVENT_NM() == null ? "" : activity.getEVENT_NM().toString();//活动名称
		String activity_type = "449715400007"; //活动类型：优惠券
		/** 店铺编号(空)，商品的单次最大购买数量0，是否单订单购买0，限时抢购单品活动的价格类型0，直减多少钱0 */
		String begin_time = convertDate(activity.getFR_DATE());//开始时间
		String end_time = convertDate(activity.getEND_DATE());//结束时间
		String flag = activity.getVL_YN() == null ? "0" : (activity.getVL_YN().toString().equals("Y") ? "1" : "0");//是否启用状态 
		/** 订单金额下限0，满减金额0，运费金额0， 优惠券发放类型(空)，发放限额0，发放条件(空)，解冻条件(空)，是否冻结(空)，仅限新用户发放(空)，优惠券发放份数0*/
		String remark = activity.getEVENT_DESC() == null ? "" : activity.getEVENT_DESC().toString();//活动描述
		String creator = activity.getETR_ID() == null ? "" : activity.getETR_ID().toString();//创建人
		String create_time = convertDate(activity.getETR_DATE());//创建时间
		String updator = activity.getMDF_ID() == null ? "" : activity.getMDF_ID().toString();//更新人
		String update_time = convertDate(activity.getMDF_DATE());//更新时间
		String provide_department = activity.getDEPT_EVENT() == null ? "" : activity.getDEPT_EVENT().toString(); //优惠券发放部门
		String isMultiUse = activity.getIS_SUPERPOSITION() == null ? "N" : activity.getIS_SUPERPOSITION().toString(); // 是否可叠加使用 “Y”可叠加 “N”不可
		String isHjy = activity.getHJY_EVENT_YN() == null ? "" : activity.getHJY_EVENT_YN().toString();
		String disAmt = activity.getDIS_AMT() == null ? "" : activity.getDIS_AMT().toString(); //活动赋予礼金券面额
		String lowAmt = activity.getLOW_AMT() == null ? "" : activity.getLOW_AMT().toString(); //活动赋予礼金券最低使用金额
		String ordFrDate = activity.getORD_FR_DATE() == null ? "" : activity.getORD_FR_DATE().toString(); //活动赋予礼金券使用开始时间
		String ordEndDate = activity.getORD_END_DATE() == null ? "" : activity.getORD_END_DATE().toString(); //活动赋予礼金券使用结束时间
		String goodlimit = activity.getGOODLIMIT() == null ? "" : activity.getGOODLIMIT().toString(); //限定的商品集合
		String classlimit = activity.getCLASSLIMIT() == null ? "" : activity.getCLASSLIMIT().toString(); //限定的品类集合
		String goodnojoin = activity.getGOODNOJOIN() == null ? "" : activity.getGOODNOJOIN().toString(); //禁止的商品集合
		String limit_type = activity.getGOOD_YN() == null ? "" : activity.getGOOD_YN().toString(); //商品限定类型 “01,02,03是限定，其他无限制”
		String limit_conition = "";
		if(limit_type.equals("01") || limit_type.equals("02") || limit_type.equals("03")) {
			limit_conition = limit_type;
		}
		//固定值
		String provide_type = "4497471600060002"; //优惠券发放类型
		
		//限定字段存放在表oc_coupon_type和oc_coupon_type_limit中
		MDataMap mActivityMap = DbUp.upTable("oc_activity").one("out_activity_code", out_activity_code); 		
		//新增
		if(mActivityMap == null) {
			String activity_code = WebHelper.upCode("AC");
			String activity_uid=UUID.randomUUID().toString().replace("-", "");
			MDataMap aActivityMap= new MDataMap();
			aActivityMap.put("uid", activity_uid);
			aActivityMap.put("activity_code", activity_code);
			aActivityMap.put("activity_name", activity_name);
			aActivityMap.put("activity_type", activity_type);
			aActivityMap.put("begin_time", begin_time);
			aActivityMap.put("end_time", end_time);
			aActivityMap.put("flag", flag);
			aActivityMap.put("remark", remark);
			aActivityMap.put("creator", creator);
			aActivityMap.put("create_time", create_time);
			aActivityMap.put("updator", updator);
			aActivityMap.put("update_time", update_time);
			aActivityMap.put("full_free_money", "0");
			aActivityMap.put("provide_type", provide_type);
			aActivityMap.put("provide_department", provide_department);
			aActivityMap.put("out_activity_code", out_activity_code);
			aActivityMap.put("is_multi_use", isMultiUse);
			aActivityMap.put("seller_code", MemberConst.MANAGE_CODE_HOMEHAS);
			
			DbUp.upTable("oc_activity").dataInsert(aActivityMap);
			
			//oc_coupon_type
			/** 规则：一个活动对应一个优惠券类型，每个优惠券类型可以有多张优惠券 */
			String coupon_type_code = insertCouponType(limit_conition, activity_name, activity_code, disAmt, lowAmt, ordFrDate, ordEndDate, creator, create_time, updator, update_time);			
			insertCouponTypeLimit(limit_conition, goodlimit, classlimit, goodnojoin, creator, create_time, updator, update_time, coupon_type_code, activity_code);			
		} else {
			String activity_code = mActivityMap.get("activity_code").toString();
			MDataMap mCouponTypeMap = DbUp.upTable("oc_coupon_type").one("activity_code", activity_code);
			//同步oc_coupon_type_limit
			if(mCouponTypeMap == null) {
				//同步失败，XX活动的优惠券类型不存在
				mWebResult.inErrorMessage(918505109, activity_code);
			} else {
				String coupon_type_code = mCouponTypeMap.get("coupon_type_code").toString();
				MDataMap mCouponTypeLimitMap = DbUp.upTable("oc_coupon_type_limit").one("activity_code", activity_code, "coupon_type_code", coupon_type_code);
				if(mCouponTypeLimitMap == null) {
					insertCouponTypeLimit(limit_conition, goodlimit, classlimit, goodnojoin, creator, create_time, updator, update_time, coupon_type_code, activity_code);
				} else {
					updateCouponTypeLimit(limit_conition, goodlimit, classlimit, goodnojoin, updator, update_time, coupon_type_code, activity_code);
				}
			}
			//同步oc_coupon_type
			updateCouponType(limit_conition, activity_name, activity_code, disAmt, lowAmt, begin_time, end_time, updator, update_time);
			
			//修改oc_activity
			MDataMap eActivityMap = new MDataMap();
			eActivityMap.put("activity_name", activity_name);
			eActivityMap.put("begin_time", begin_time);
			eActivityMap.put("end_time", end_time);
			eActivityMap.put("flag", flag);
			eActivityMap.put("remark", remark);	
			eActivityMap.put("updator", updator);
			eActivityMap.put("update_time", update_time);
			eActivityMap.put("full_free_money", "0");
			eActivityMap.put("provide_type", provide_type);
			eActivityMap.put("provide_department", provide_department);
			eActivityMap.put("seller_code", MemberConst.MANAGE_CODE_HOMEHAS);
			eActivityMap.put("out_activity_code", out_activity_code);
			DbUp.upTable("oc_activity").dataUpdate(eActivityMap, "activity_name,seller_code,begin_time,end_time,flag,remark,updator,update_time,full_free_money,provide_type,provide_department", "out_activity_code");
			
		}		
		
		return mWebResult;
	}
	
	/**
	 * 增加优惠券类型
	 * @param limit_type
	 * @param activity_name
	 * @param activity_code
	 * @param begin_time
	 * @param end_time
	 * @param creator
	 * @param create_time
	 * @param updator
	 * @param update_time
	 */
	private String insertCouponType(String limit_conition, String activity_name, String activity_code, String dis_amt, String low_amt, String begin_time, String end_time, String creator, String create_time, String updator, String update_time) {
		/** 规则：一个活动对应一个优惠券类型，每个优惠券类型可以有多张优惠券 */
		String coupon_type_uid=UUID.randomUUID().toString().replace("-", "");
		String coupon_type_code = WebHelper.upCode("CT");
		String coupon_type_name = activity_name;  //和活动名称一致
		/** 面值、使用下限金额、成本限额、已发放金额、剩余金额字段默认设置为0 */
		/** 开始时间和结束时间与活动的开始时间，结束时间保持一致 */
		String status = "4497469400030002";
		String produce_type = "4497471600040001";		
		/** 创建人，创建时间，更新人，更新时间与活动的保持一致 */
		String money_type = "449748120003"; //固定值：礼金券
		MDataMap aCouponTypeMap= new MDataMap();
		aCouponTypeMap.put("uid", coupon_type_uid);
		aCouponTypeMap.put("coupon_type_code", coupon_type_code);
		aCouponTypeMap.put("coupon_type_name", coupon_type_name);
		aCouponTypeMap.put("activity_code", activity_code);
		if(!StringUtils.isBlank(dis_amt)) {
			aCouponTypeMap.put("money", dis_amt);
		}
		if(!StringUtils.isBlank(low_amt)) {
			aCouponTypeMap.put("limit_money", low_amt);
		}
		if(!StringUtils.isBlank(begin_time)) {
			aCouponTypeMap.put("start_time", begin_time);
		}
		if(!StringUtils.isBlank(end_time)) {
			aCouponTypeMap.put("end_time", end_time);
		}		
		aCouponTypeMap.put("status", status);
		aCouponTypeMap.put("produce_type", produce_type);
		aCouponTypeMap.put("limit_condition", "".equals(limit_conition) ? "4497471600070001" : "4497471600070002");
		aCouponTypeMap.put("creater", creator);
		aCouponTypeMap.put("create_time", create_time);
		aCouponTypeMap.put("updater", updator);
		aCouponTypeMap.put("update_time", update_time);
		aCouponTypeMap.put("money_type", money_type);
		DbUp.upTable("oc_coupon_type").dataInsert(aCouponTypeMap);
		return coupon_type_code;
	}	
	
	/**
	 * 更新优惠券类型
	 * @param limit_conition
	 * @param activity_name
	 * @param activity_code
	 * @param begin_time
	 * @param end_time
	 * @param updator
	 * @param update_time
	 */
	private void updateCouponType(String limit_conition, String activity_name, String activity_code, String dis_amt, String low_amt, String begin_time, String end_time, String updator, String update_time) {
		//同步oc_coupon_type
		MDataMap eCouponTypeMap = new MDataMap();
		eCouponTypeMap.put("coupon_type_name", activity_name);
		eCouponTypeMap.put("activity_code", activity_code);
		eCouponTypeMap.put("money", dis_amt);
		eCouponTypeMap.put("limit_money", low_amt);
		eCouponTypeMap.put("start_time", begin_time);
		eCouponTypeMap.put("end_time", end_time);	
		eCouponTypeMap.put("limit_condition", "".equals(limit_conition) ? "4497471600070001" : "4497471600070002");
		eCouponTypeMap.put("updater", updator);
		eCouponTypeMap.put("update_time", update_time);
		DbUp.upTable("oc_coupon_type").dataUpdate(eCouponTypeMap, "coupon_type_name,start_time,end_time,limit_condition,updater,update_time", "activity_code");		
	}
	
	/**
	 * 增加优惠券类型限制
	 * @param limit_conition
	 * @param goodlimit
	 * @param classlimit
	 * @param goodnojoin
	 * @param creator
	 * @param create_time
	 * @param updator
	 * @param update_time
	 * @param coupon_type_code
	 * @param activity_code
	 */
	private void insertCouponTypeLimit(String limit_conition, String goodlimit, String classlimit, String goodnojoin, String creator, String create_time, String updator, String update_time, String coupon_type_code, String activity_code) {
		String coupon_type_limit_uid=UUID.randomUUID().toString().replace("-", "");
		/** 优惠券类型编号 取 优惠券类型的编号，活动编号取活动的编号 */
		String brand_limit = "4497471600070001";
		String product_limit = "4497471600070001";
		String category_limit = "4497471600070001";
		String channel_limit = "4497471600070001";
		String activity_limit = "449747110001";  //礼金券默认不参加活动
		String except_brand = "0";
		String except_product = "0";
		String except_category = "0";
		String except_channel = "0";
		String brand_codes = "";
		String product_codes= "";
		String category_codes = "";
		String channel_codes = "";		
		if(!"".equals(limit_conition) && limit_conition.equals("01")) {
			product_limit = "4497471600070002";	
			product_codes = goodlimit;
		}
		if(!"".equals(limit_conition) && limit_conition.equals("02")) {
			category_limit = "4497471600070002";
			category_codes = classlimit;
		}
		if(!"".equals(limit_conition) && limit_conition.equals("03")) {
			product_limit = "4497471600070002";
			except_product = "1";
			product_codes = goodnojoin;
		}
		MDataMap aCouponTypeLimitMap = new MDataMap();
		aCouponTypeLimitMap.put("uid", coupon_type_limit_uid);
		aCouponTypeLimitMap.put("coupon_type_code", coupon_type_code);
		aCouponTypeLimitMap.put("activity_code", activity_code);
		aCouponTypeLimitMap.put("brand_limit", brand_limit);
		aCouponTypeLimitMap.put("product_limit", product_limit);
		aCouponTypeLimitMap.put("category_limit", category_limit);
		aCouponTypeLimitMap.put("channel_limit", channel_limit);
		aCouponTypeLimitMap.put("activity_limit", activity_limit);
		aCouponTypeLimitMap.put("except_brand", except_brand);
		aCouponTypeLimitMap.put("except_product", except_product);
		aCouponTypeLimitMap.put("except_category", except_category);
		aCouponTypeLimitMap.put("except_channel", except_channel);
		aCouponTypeLimitMap.put("brand_codes", brand_codes);
		aCouponTypeLimitMap.put("product_codes", product_codes);
		aCouponTypeLimitMap.put("category_codes", category_codes);
		aCouponTypeLimitMap.put("channel_codes", channel_codes);
		aCouponTypeLimitMap.put("create_user", creator);
		aCouponTypeLimitMap.put("create_time", create_time);
		aCouponTypeLimitMap.put("update_user", updator);
		aCouponTypeLimitMap.put("update_time", update_time);
		DbUp.upTable("oc_coupon_type_limit").dataInsert(aCouponTypeLimitMap);
	}
	
	/**
	 * 修改优惠券类型限制
	 * @param limit_conition
	 * @param goodlimit
	 * @param classlimit
	 * @param goodnojoin
	 * @param updator
	 * @param update_time
	 * @param coupon_type_code
	 * @param activity_code
	 */
	private void updateCouponTypeLimit(String limit_conition, String goodlimit, String classlimit, String goodnojoin, String updator, String update_time, String coupon_type_code, String activity_code) {
		String product_limit = "4497471600070001";
		String category_limit = "4497471600070001";
		String except_product = "0";
		String except_category = "0";
		String product_codes= "";
		String category_codes = "";
		MDataMap eCouponTypeLimitMap = new MDataMap();
		if(!"".equals(limit_conition) && limit_conition.equals("01")) {
			product_limit = "4497471600070002";	
			product_codes = goodlimit;
		}
		if(!"".equals(limit_conition) && limit_conition.equals("02")) {
			category_limit = "4497471600070002";
			category_codes = classlimit;
		}
		if(!"".equals(limit_conition) && limit_conition.equals("03")) {
			product_limit = "4497471600070002";
			except_product = "1";
			product_codes = goodnojoin;
		}
		eCouponTypeLimitMap.put("product_limit", product_limit);
		eCouponTypeLimitMap.put("category_limit", category_limit);
		eCouponTypeLimitMap.put("except_product", except_product);
		eCouponTypeLimitMap.put("except_category", except_category);
		eCouponTypeLimitMap.put("product_codes", product_codes);
		eCouponTypeLimitMap.put("category_codes", category_codes);
		eCouponTypeLimitMap.put("update_user", updator);
		eCouponTypeLimitMap.put("update_time", update_time);
		eCouponTypeLimitMap.put("activity_code", activity_code);
		eCouponTypeLimitMap.put("coupon_type_code", coupon_type_code);
		DbUp.upTable("oc_coupon_type_limit").dataUpdate(eCouponTypeLimitMap, "product_limit,category_limit,except_product,except_category,product_codes,category_codes,update_user,update_time", "activity_code,coupon_type_code");
	}

	private RsyncResponseGiftVoucherActivity rsyncResponseGiftVoucherActivity = new RsyncResponseGiftVoucherActivity();
	
	@Override
	public RsyncResponseGiftVoucherActivity upResponseObject() {

		return rsyncResponseGiftVoucherActivity;
	}

	private String convertDate(Long date) {
		SimpleDateFormat format =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );
		if(date == null) {
			return format.format(new Date());
		}
		return format.format(date);
	}		
}

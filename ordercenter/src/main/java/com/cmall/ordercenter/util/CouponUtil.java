package com.cmall.ordercenter.util;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.cmall.ordercenter.model.CouponInfo;
import com.cmall.ordercenter.model.CouponInfoRootResult;
import com.cmall.ordercenter.model.api.GiftVoucherInfo;
import com.cmall.productcenter.common.DateUtil;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.load.LoadCouponExist;
import com.srnpr.xmassystem.load.LoadCouponGetUser;
import com.srnpr.xmassystem.modelbean.CouponExistInfo;
import com.srnpr.xmassystem.modelbean.CouponExistQuery;
import com.srnpr.xmassystem.modelbean.CouponGetUserInfo;
import com.srnpr.xmassystem.modelbean.CouponGetUserQuery;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.xmassystem.util.AppVersionUtils;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topcache.SimpleCache.Config;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;

/**
 * 优惠券相关操作
 * 
 * @author ligj
 *
 */
public class CouponUtil extends BaseClass {
	/**
	 * 发放优惠券
	 * 
	 * @param memberCode
	 *            用户编号
	 * @param couponTypeCode
	 *            优惠券类型
	 * @param cdkey
	 *            优惠码
	 * @return 1发放成功
	 */
	public int provideCoupon(String memberCode, String couponTypeCode, String cdkey) {
		final int FAIL = 0;
		final int SUCCESS = 1;
		if (StringUtils.isEmpty(memberCode) || StringUtils.isEmpty(couponTypeCode)) {
			return FAIL;
		}
		MDataMap couponTypeMap = DbUp.upTable("oc_coupon_type").one("coupon_type_code", couponTypeCode);
		if (null == couponTypeMap || couponTypeMap.isEmpty()) {
			return FAIL;
		}
		couponTypeMap.put("cdkey", cdkey);
		this.calculateTime(couponTypeMap);
		int k = 0;
		int accountUseTime = 1;// 默认发一张
		if (StringUtils.isNotBlank(cdkey)) {
			MDataMap cdkeyMap = DbUp.upTable("oc_coupon_cdkey").oneWhere("account_useTime", "", "", "cdkey", cdkey);
			if (cdkeyMap != null) {
				accountUseTime = Integer.parseInt(cdkeyMap.get("account_useTime"));
			}
		}
		for (int i = 1; i <= accountUseTime; i++) {
			k = saveCouponInfo(couponTypeMap, memberCode, "", "","");
		}
		if (k == 0) {
			return FAIL;
		} else {
			return SUCCESS;
		}
	}
	
	/**
	 * 发放优惠券
	 * 
	 * @param memberCode
	 *            用户编号
	 * @param couponTypeCode
	 *            优惠券类型
	 * @param cdkey
	 *            优惠码
	 * @return 1发放成功
	 */
	public int provideCoupon(String memberCode, String couponTypeCode, String blocked, String bigOrderCode,String cdkey) {
		final int FAIL = 0;
		final int SUCCESS = 1;
		if (StringUtils.isEmpty(memberCode) || StringUtils.isEmpty(couponTypeCode)) {
			return FAIL;
		}
		MDataMap couponTypeMap = DbUp.upTable("oc_coupon_type").one("coupon_type_code", couponTypeCode);
		if (null == couponTypeMap || couponTypeMap.isEmpty()) {
			return FAIL;
		}
		couponTypeMap.put("cdkey", cdkey);
		this.calculateTime(couponTypeMap);
		int k = 0;
		int accountUseTime = 1;// 默认发一张
		if (StringUtils.isNotBlank(cdkey)) {
			MDataMap cdkeyMap = DbUp.upTable("oc_coupon_cdkey").oneWhere("account_useTime", "", "", "cdkey", cdkey);
			if (cdkeyMap != null) {
				accountUseTime = Integer.parseInt(cdkeyMap.get("account_useTime"));
			}
		}
		for (int i = 1; i <= accountUseTime; i++) {
			k = saveCouponInfo(couponTypeMap, memberCode, blocked, bigOrderCode,"");
		}
		if (k == 0) {
			return FAIL;
		} else {
			return SUCCESS;
		}
	}
	
	
	/**
	 * 发放优惠券
	 * @param memberCode     用户编号
	 * @param couponTypeCode 优惠券类型编码
	 * @param blocked        是否锁定
	 * @param bigOrderCode   关联的大订单号
	 * @param cdkey          领取的优惠码
	 * @param count          领取数量
	 * @return
	 */
	public RootResult provideCoupon2(String memberCode, String couponTypeCode, String blocked, String bigOrderCode,String cdkey,int count,BigDecimal money) {
		RootResult result = new RootResult();
		CouponUtil couponUtil = new CouponUtil();
		if (StringUtils.isEmpty(memberCode) || StringUtils.isEmpty(couponTypeCode)) {
			result.setResultCode(0);
			result.setResultMessage("用户编号或优惠券类型编码不能为空");
			return result;
		}
		
		MDataMap couponTypeMap = DbUp.upTable("oc_coupon_type").one("coupon_type_code", couponTypeCode);
		if (couponTypeMap == null) {
			result.setResultCode(0);
			result.setResultMessage("优惠券类型编码不存在");
			return result;
		}
		
		if(!"4497469400030002".equals(couponTypeMap.get("status"))){
			result.setResultCode(0);
			result.setResultMessage("此优惠券类型未发布");
			return result;
		}
		boolean flagUser = couponUtil.checkUserLegal(memberCode,couponTypeCode);
		MDataMap acMap = DbUp.upTable("oc_activity").one("activity_code", couponTypeMap.get("activity_code"));
		if(!flagUser) {
			if(!"449715400008".equals(acMap.get("activity_type"))) {
				if(!"4497471600060005".equals(acMap.get("provide_type"))) {
					//非分销活动，且非系统返利活动
					result.setResultCode(0);
					result.setResultMessage("您已经领取过此优惠券！");
					return result;
				}

			}else {
				//分销券可以继续领，但是如果存在已经领取且未用的券，就不能再领了
				int dataCount = DbUp.upTable("oc_coupon_info").dataCount("member_code=:member_code and coupon_type_code=:coupon_type_code and status=0 and start_time<=now() and end_time>now() ", new MDataMap("member_code",memberCode,"coupon_type_code",couponTypeCode));
				if(dataCount>0) {
					result.setResultCode(0);
					result.setResultMessage("您已经领取过此优惠券！");
					return result;
				}
			}
			
		}
		BigDecimal surplusMoney = new BigDecimal(couponTypeMap.get("surplus_money")); 
		//分销券没有发放金额的限制
		if((surplusMoney.compareTo(BigDecimal.ZERO) != 1)&&(!"449715400008".equals(acMap.get("activity_type")))) {
			result.setResultCode(0);
			result.setResultMessage("抢光了");
			return result;
		}
		
		couponTypeMap.put("cdkey", cdkey);	
		if("449715400008".equals(acMap.get("activity_type"))) {
			//分销券走自己的时效时间计算
			List<Map<String, Object>> dataSqlList = DbUp.upTable("oc_activity_agent_product").dataSqlList("select * from oc_activity_agent_product  where  "
					+ " flag_enable=1 and coupon_type_code=:coupon_type_code", new MDataMap("coupon_type_code",couponTypeMap.get("coupon_type_code")));
		   if(dataSqlList!=null&&dataSqlList.size()>0) {
			   Map<String, Object> map = dataSqlList.get(0);
			   String timeString = DateUtil.getSysDateTimeString();
			   couponTypeMap.put("start_time", timeString);
			   String deadline = DateUtil.toString(DateUtil.addDays(new Date(), Integer.parseInt(TopConfig.Instance.bConfig("ordercenter.effective_time"))), "yyyy-MM-dd");
			   couponTypeMap.put("end_time", deadline+" 23:59:59");  
			   couponTypeMap.put("initial_money", map.get("coupon_money").toString());
			   couponTypeMap.put("surplus_money", map.get("coupon_money").toString());
			   
		   }else {
			    result.setResultCode(0);
				result.setResultMessage("此优惠券已失效");
				return result; 
		   }
		}else if("4497471600060005".equals(acMap.get("provide_type"))) {
			//系统返利优惠券活动
			couponTypeMap.put("start_time", DateUtil.getSysDateTimeString());
			String deadline = DateUtil.toString(DateUtil.addMinute(new Date(), Integer.parseInt(acMap.get("validate_time")==null?"0":acMap.get("validate_time"))), "yyyy-MM-dd HH:mm:ss");
			couponTypeMap.put("end_time", deadline);
			couponTypeMap.put("initial_money", money.toString());
			couponTypeMap.put("surplus_money",money.toString());
			couponTypeMap.put("money",money.toString());
			
		}else {
			this.calculateTime(couponTypeMap);
		}
		
		int success = 0;
		for (int i = 1; i <= count; i++) {
			// 保存优惠券
			if(saveCouponInfo(couponTypeMap, memberCode, blocked, bigOrderCode,"") == 1){
				success++;
			}
		}
		
		if(success > 0){
			// 更新优惠券已发数量 
			updateCouponType(success, couponTypeCode, "system");
		}else{
			result.setResultCode(0);
			result.setResultMessage("保存优惠券失败");
			return result;
		}
		return result;
	}
	
	/**
	 * 发放优惠券
	 * @param memberCode     用户编号
	 * @param couponTypeCode 优惠券类型编码
	 * @param blocked        是否锁定
	 * @param bigOrderCode   关联的大订单号
	 * @param cdkey          领取的优惠码
	 * @param count          领取数量
	 * @return
	 */
	public RootResult provideCoupon(String memberCode, String couponTypeCode, String blocked, String bigOrderCode,String cdkey,int count) {
		RootResult result = new RootResult();
		CouponUtil couponUtil = new CouponUtil();
		if (StringUtils.isEmpty(memberCode) || StringUtils.isEmpty(couponTypeCode)) {
			result.setResultCode(0);
			result.setResultMessage("用户编号或优惠券类型编码不能为空");
			return result;
		}
		
		MDataMap couponTypeMap = DbUp.upTable("oc_coupon_type").one("coupon_type_code", couponTypeCode);
		if (couponTypeMap == null) {
			result.setResultCode(0);
			result.setResultMessage("优惠券类型编码不存在");
			return result;
		}
		
		if(!"4497469400030002".equals(couponTypeMap.get("status"))){
			result.setResultCode(0);
			result.setResultMessage("此优惠券类型未发布");
			return result;
		}
		boolean flagUser = couponUtil.checkUserLegal(memberCode,couponTypeCode);
		MDataMap acMap = DbUp.upTable("oc_activity").one("activity_code", couponTypeMap.get("activity_code"));
		if(!flagUser) {
			if(!"449715400008".equals(acMap.get("activity_type"))) {
				if(!"4497471600060005".equals(acMap.get("provide_type"))) {
					//非分销活动，且非系统返利活动
					result.setResultCode(0);
					result.setResultMessage("您已经领取过此优惠券！");
					return result;
				}

			}else {
				//分销券可以继续领，但是如果存在已经领取且未用的券，就不能再领了
				int dataCount = DbUp.upTable("oc_coupon_info").dataCount("member_code=:member_code and coupon_type_code=:coupon_type_code and status=0 and start_time<=now() and end_time>now() ", new MDataMap("member_code",memberCode,"coupon_type_code",couponTypeCode));
				if(dataCount>0) {
					result.setResultCode(0);
					result.setResultMessage("您已经领取过此优惠券！");
					return result;
				}
			}
			
		}
		BigDecimal surplusMoney = new BigDecimal(couponTypeMap.get("surplus_money")); 
		//分销券没有发放金额的限制
		if((surplusMoney.compareTo(BigDecimal.ZERO) != 1)&&(!"449715400008".equals(acMap.get("activity_type")))) {
			result.setResultCode(0);
			result.setResultMessage("抢光了");
			return result;
		}
		
		couponTypeMap.put("cdkey", cdkey);	
		if("449715400008".equals(acMap.get("activity_type"))) {
			//分销券走自己的时效时间计算
			List<Map<String, Object>> dataSqlList = DbUp.upTable("oc_activity_agent_product").dataSqlList("select * from oc_activity_agent_product  where  "
					+ " flag_enable=1 and coupon_type_code=:coupon_type_code", new MDataMap("coupon_type_code",couponTypeMap.get("coupon_type_code")));
		   if(dataSqlList!=null&&dataSqlList.size()>0) {
			   Map<String, Object> map = dataSqlList.get(0);
			   String timeString = DateUtil.getSysDateTimeString();
			  //String effective_hours = map.get("effective_hours").toString();
			  //String deadline = DateUtil.addDateHour(timeString,Integer.parseInt(effective_hours));
			   couponTypeMap.put("start_time", timeString);
			   String deadline = DateUtil.toString(DateUtil.addDays(new Date(), Integer.parseInt(TopConfig.Instance.bConfig("ordercenter.effective_time"))), "yyyy-MM-dd");
			   couponTypeMap.put("end_time", deadline+" 23:59:59");  
			   couponTypeMap.put("initial_money", map.get("coupon_money").toString());
			   couponTypeMap.put("surplus_money", map.get("coupon_money").toString());
			   
		   }else {
			    result.setResultCode(0);
				result.setResultMessage("此优惠券已失效");
				return result; 
		   }
		}else if("4497471600060005".equals(acMap.get("provide_type"))) {
			//系统返利优惠券活动
			couponTypeMap.put("start_time", DateUtil.getSysDateTimeString());
			String deadline = DateUtil.toString(DateUtil.addMinute(new Date(), Integer.parseInt(acMap.get("validate_time")==null?"0":acMap.get("validate_time"))), "yyyy-MM-dd HH:mm:ss");
			couponTypeMap.put("end_time", deadline);
			
			
		}else {
			this.calculateTime(couponTypeMap);
		}
		
		int success = 0;
		for (int i = 1; i <= count; i++) {
			// 保存优惠券
			if(saveCouponInfo(couponTypeMap, memberCode, blocked, bigOrderCode,"") == 1){
				success++;
			}
		}
		
		if(success > 0){
			// 更新优惠券已发数量 
			updateCouponType(success, couponTypeCode, "system");
		}else{
			result.setResultCode(0);
			result.setResultMessage("保存优惠券失败");
			return result;
		}
		return result;
	}
	
	
	
	
	/**
	 * 发放优惠券
	 * @param memberCode     用户编号
	 * @param couponTypeCode 优惠券类型编码
	 * @param blocked        是否锁定
	 * @param bigOrderCode   关联的大订单号
	 * @param cdkey          领取的优惠码
	 * @param count          领取数量
	 * @param sourceId       来源id
	 * @return
	 */
	public RootResult provideCoupon(String memberCode, String couponTypeCode, String blocked, String bigOrderCode,String cdkey,int count,String sourceId) {
		RootResult result = new RootResult();
		CouponUtil couponUtil = new CouponUtil();
		if (StringUtils.isEmpty(memberCode) || StringUtils.isEmpty(couponTypeCode)) {
			result.setResultCode(0);
			result.setResultMessage("用户编号或优惠券类型编码不能为空");
			return result;
		}
		
		MDataMap couponTypeMap = DbUp.upTable("oc_coupon_type").one("coupon_type_code", couponTypeCode);
		if (couponTypeMap == null) {
			result.setResultCode(0);
			result.setResultMessage("优惠券类型编码不存在");
			return result;
		}
		
		if(!"4497469400030002".equals(couponTypeMap.get("status"))){
			result.setResultCode(0);
			result.setResultMessage("此优惠券类型未发布");
			return result;
		}
		boolean flagUser = couponUtil.checkUserLegal(memberCode,couponTypeCode);
		MDataMap acMap = DbUp.upTable("oc_activity").one("activity_code", couponTypeMap.get("activity_code"));
		if(!flagUser) {
			if(!"449715400008".equals(acMap.get("activity_type"))) {
				result.setResultCode(0);
				result.setResultMessage("您已经领取过此优惠券！");
				return result;
			}else {
				//分销券可以继续领，但是如果存在已经领取且未用的券，就不能再领了
				int dataCount = DbUp.upTable("oc_coupon_info").dataCount("member_code=:member_code and coupon_type_code=:coupon_type_code and status=0 and start_time<=now() and end_time>now() ", new MDataMap("member_code",memberCode,"coupon_type_code",couponTypeCode));
				if(dataCount>0) {
					result.setResultCode(0);
					result.setResultMessage("您已经领取过此优惠券！");
					return result;
				}
			}
			
		}
		BigDecimal surplusMoney = new BigDecimal(couponTypeMap.get("surplus_money")); 
		//分销券没有发放金额的限制
		if((surplusMoney.compareTo(BigDecimal.ZERO) != 1)&&(!"449715400008".equals(acMap.get("activity_type")))) {
			result.setResultCode(0);
			result.setResultMessage("抢光了");
			return result;
		}
		
		couponTypeMap.put("cdkey", cdkey);	
		if("449715400008".equals(acMap.get("activity_type"))) {
			//分销券走自己的时效时间计算
			List<Map<String, Object>> dataSqlList = DbUp.upTable("oc_activity_agent_product").dataSqlList("select * from oc_activity_agent_product  where  "
					+ " flag_enable=1 and coupon_type_code=:coupon_type_code", new MDataMap("coupon_type_code",couponTypeMap.get("coupon_type_code")));
		   if(dataSqlList!=null&&dataSqlList.size()>0) {
			   Map<String, Object> map = dataSqlList.get(0);
			   String timeString = DateUtil.getSysDateTimeString();
			  //String effective_hours = map.get("effective_hours").toString();
			  //String deadline = DateUtil.addDateHour(timeString,Integer.parseInt(effective_hours));
			   couponTypeMap.put("start_time", timeString);
			   String deadline = DateUtil.toString(DateUtil.addDays(new Date(), Integer.parseInt(TopConfig.Instance.bConfig("ordercenter.effective_time"))), "yyyy-MM-dd");
			   couponTypeMap.put("end_time", deadline+" 23:59:59");  
			   couponTypeMap.put("initial_money", map.get("coupon_money").toString());
			   couponTypeMap.put("surplus_money", map.get("coupon_money").toString());
			   
		   }else {
			    result.setResultCode(0);
				result.setResultMessage("此优惠券已失效");
				return result; 
		   }
		}else{//按分钟数計算
			this.calculateTime(couponTypeMap);
		}
		
		int success = 0;
		for (int i = 1; i <= count; i++) {
			// 保存优惠券
			if(saveCouponInfo(couponTypeMap, memberCode, blocked, bigOrderCode,sourceId) == 1){
				success++;
			}
		}
		
		if(success > 0){
			// 更新优惠券已发数量 
			updateCouponType(success, couponTypeCode, "system");
		}else{
			result.setResultCode(0);
			result.setResultMessage("保存优惠券失败");
			return result;
		}
		return result;
	}
	
	//根据优惠券类型计算时效时间
	private void calculateTime(MDataMap couponTypeMap) {
		couponTypeMap.put("start_time", DateUtil.getSysDateTimeString());
		if ("4497471600080001".equals(couponTypeMap.get("valid_type"))) {
			// 按天数计算有效期,将有效期延长到最后一天的最一秒之前
			String deadline = DateUtil.toString(DateUtil.addDays(new Date(), Integer.parseInt(couponTypeMap.get("valid_day"))), "yyyy-MM-dd");
			couponTypeMap.put("end_time", deadline+" 23:59:59");
		}else if("4497471600080003".equals(couponTypeMap.get("valid_type"))) {
			//按小時計算
			String deadline = DateUtil.toString(DateUtil.addMinute(new Date(), Integer.parseInt(couponTypeMap.get("valid_day")==null?"0":couponTypeMap.get("valid_day"))*60), "yyyy-MM-dd HH:mm:ss");
			couponTypeMap.put("end_time", deadline);
		}else if("4497471600080004".equals(couponTypeMap.get("valid_type"))) {
			//按分钟数计算
			String deadline = DateUtil.toString(DateUtil.addMinute(new Date(), Integer.parseInt(couponTypeMap.get("valid_day")==null?"0":couponTypeMap.get("valid_day"))), "yyyy-MM-dd HH:mm:ss");
			couponTypeMap.put("end_time", deadline);
		}
	}

	/**
	 * 发放优惠券
	 * 推广用户优惠券发放，请勿随便调用。
	 * @param memberCode     用户编号
	 * @param couponTypeCode 优惠券类型编码
	 * @param blocked        是否锁定
	 * @param bigOrderCode   关联的大订单号
	 * @param cdkey          领取的优惠码
	 * @param count          领取数量
	 * @param sourceId       来源id
	 * @return
	 */
	public RootResult provideCouponForShare(String memberCode, String couponTypeCode, String blocked, String bigOrderCode,String cdkey,int count,String money) {
		RootResult result = new RootResult();
		if (StringUtils.isEmpty(memberCode) || StringUtils.isEmpty(couponTypeCode)) {
			result.setResultCode(0);
			result.setResultMessage("用户编号或优惠券类型编码不能为空");
			return result;
		}
		
		MDataMap couponTypeMap = DbUp.upTable("oc_coupon_type").one("coupon_type_code", couponTypeCode);
		if (couponTypeMap == null) {
			result.setResultCode(0);
			result.setResultMessage("优惠券类型编码不存在");
			return result;
		}
		
		if(!"4497469400030002".equals(couponTypeMap.get("status"))){
			result.setResultCode(0);
			result.setResultMessage("此优惠券类型未发布");
			return result;
		}
		BigDecimal surplusMoney = new BigDecimal(couponTypeMap.get("surplus_money")); 
		//分销券没有发放金额的限制
		if((surplusMoney.compareTo(BigDecimal.ZERO) != 1)) {
			result.setResultCode(0);
			result.setResultMessage("抢光了");
			return result;
		}
		
		couponTypeMap.put("cdkey", cdkey);	
		couponTypeMap.put("money",money );
		this.calculateTime(couponTypeMap);
		
		int success = 0;
		for (int i = 1; i <= count; i++) {
			// 保存优惠券
			if(saveCouponInfo(couponTypeMap, memberCode, blocked, bigOrderCode,"") == 1){
				success++;
			}
		}
		
		if(success > 0){
			// 更新优惠券已发数量 
			updateCouponType(success, couponTypeCode, "system");
		}else{
			result.setResultCode(0);
			result.setResultMessage("保存优惠券失败");
			return result;
		}
		return result;
	}
	
	/**
	 * 扫码购召回发放优惠券
	 */
	public RootResult provideCouponForSmg(String memberCode, String couponTypeCode, BigDecimal money) {
		RootResult result = new RootResult();
		MDataMap couponTypeMap = DbUp.upTable("oc_coupon_type").one("coupon_type_code", couponTypeCode);
		if (couponTypeMap == null) {
			result.setResultCode(0);
			result.setResultMessage("优惠券类型编码不存在");
			return result;
		}
		
		MDataMap acMap = DbUp.upTable("oc_activity").one("activity_code", couponTypeMap.get("activity_code"));
		
		//系统返利优惠券活动
		couponTypeMap.put("start_time", DateUtil.getSysDateTimeString());
		String deadline = DateUtil.toString(DateUtil.addMinute(new Date(), Integer.parseInt(acMap.get("validate_time")==null?"0":acMap.get("validate_time"))), "yyyy-MM-dd HH:mm:ss");
		couponTypeMap.put("end_time", deadline);
		couponTypeMap.put("initial_money", money.toString());
		couponTypeMap.put("surplus_money",money.toString());
		couponTypeMap.put("money", money.toString());
		
		// 返回优惠券的过期时间，用于记录到优惠券的发放日志表中
		result.setResultMessage(deadline);
		
		String couponCode = saveCouponInfoForSmg(couponTypeMap, memberCode, "0", "","");
		updateCouponType(1, couponTypeCode, "system");
		result.setResultMessage(result.getResultMessage()+","+couponCode);
		return result;
	}

	/**
	 * 保存到oc_coupon_info表中的数据
	 * 
	 * @param couponTypeMap
	 *            获取oc_coupon_type的查询结果
	 * @param memberCode
	 *            用户编号
	 * @author liqt
	 */
	public int saveCouponInfo(MDataMap couponTypeMap, String memberCode, String blocked, String bigOrderCode,String sourceId) {
		MDataMap insertCouponMap = new MDataMap();
		insertCouponMap.put("coupon_type_code", couponTypeMap.get("coupon_type_code"));
		insertCouponMap.put("activity_code", couponTypeMap.get("activity_code"));
		insertCouponMap.put("coupon_code", WebHelper.upCode("CP"));
		insertCouponMap.put("member_code", memberCode);
		insertCouponMap.put("initial_money", couponTypeMap.get("money"));
		insertCouponMap.put("surplus_money", couponTypeMap.get("money"));
		insertCouponMap.put("limit_money", couponTypeMap.get("limit_money"));
		insertCouponMap.put("status", "0");
		insertCouponMap.put("start_time", couponTypeMap.get("start_time"));
		insertCouponMap.put("end_time", couponTypeMap.get("end_time"));
		insertCouponMap.put("create_time", DateUtil.getSysDateTimeString());
		insertCouponMap.put("update_time", DateUtil.getSysDateTimeString());
		insertCouponMap.put("cdkey", couponTypeMap.get("cdkey"));
		// 若有无cdkey则为系统发放优惠卷：cdog 1.02需求，系统发放优惠卷红点功能，需将优惠卷设置为未查看
		/*if (StringUtils.isBlank(couponTypeMap.get("cdkey")))
			insertCouponMap.put("is_see", "0");*/
		//566添加的需求,统一改为未查看,从而让领取红包之后能够提醒 
		insertCouponMap.put("is_see", "0");
		insertCouponMap.put("blocked", StringUtils.isNotEmpty(blocked) && blocked.equals("1") ? "1" : "0");
		insertCouponMap.put("big_order_code", bigOrderCode);
		insertCouponMap.put("produce_type", couponTypeMap.get("produce_type"));
		insertCouponMap.put("manage_code", couponTypeMap.get("manage_code"));
		insertCouponMap.put("last_mdf_id", "app");
		insertCouponMap.put("source_id", sourceId);
		String uid = DbUp.upTable("oc_coupon_info").dataInsert(insertCouponMap);
		if (StringUtils.isEmpty(uid)) {
			return 0;
		}
		return 1;
	}
	
	public String saveCouponInfoForSmg(MDataMap couponTypeMap, String memberCode, String blocked, String bigOrderCode,String sourceId) {
		MDataMap insertCouponMap = new MDataMap();
		String upCode = WebHelper.upCode("CP");
		insertCouponMap.put("coupon_type_code", couponTypeMap.get("coupon_type_code"));
		insertCouponMap.put("activity_code", couponTypeMap.get("activity_code"));
		insertCouponMap.put("coupon_code", upCode);
		insertCouponMap.put("member_code", memberCode);
		insertCouponMap.put("initial_money", couponTypeMap.get("money"));
		insertCouponMap.put("surplus_money", couponTypeMap.get("money"));
		insertCouponMap.put("limit_money", couponTypeMap.get("limit_money"));
		insertCouponMap.put("status", "0");
		insertCouponMap.put("start_time", couponTypeMap.get("start_time"));
		insertCouponMap.put("end_time", couponTypeMap.get("end_time"));
		insertCouponMap.put("create_time", DateUtil.getSysDateTimeString());
		insertCouponMap.put("update_time", DateUtil.getSysDateTimeString());
		insertCouponMap.put("cdkey", couponTypeMap.get("cdkey"));
		// 若有无cdkey则为系统发放优惠卷：cdog 1.02需求，系统发放优惠卷红点功能，需将优惠卷设置为未查看
		/*if (StringUtils.isBlank(couponTypeMap.get("cdkey")))
			insertCouponMap.put("is_see", "0");*/
		//566添加的需求,统一改为未查看,从而让领取红包之后能够提醒 
		insertCouponMap.put("is_see", "0");
		insertCouponMap.put("blocked", StringUtils.isNotEmpty(blocked) && blocked.equals("1") ? "1" : "0");
		insertCouponMap.put("big_order_code", bigOrderCode);
		insertCouponMap.put("produce_type", couponTypeMap.get("produce_type"));
		insertCouponMap.put("manage_code", couponTypeMap.get("manage_code"));
		insertCouponMap.put("last_mdf_id", "app");
		insertCouponMap.put("source_id", sourceId);
		String uid = DbUp.upTable("oc_coupon_info").dataInsert(insertCouponMap);
		if (StringUtils.isEmpty(uid)) {
			return "";
		}
		return upCode;
	}

	/**
	 * 发券完成修改优惠券剩余金额
	 * 
	 * @param provideCount
	 *            发券总数
	 * @param couponTypeCode
	 *            优惠券类型编号
	 * @param updater
	 *            修改人
	 * @return 1成功
	 */
	public int updateCouponType(int provideCount, String couponTypeCode, String updater) {
		if (StringUtils.isEmpty(couponTypeCode) || provideCount <= 0) {
			return 0;
		}
		MDataMap updateMap = new MDataMap();
		updateMap.put("coupon_type_code", couponTypeCode);
		updateMap.put("privide_count", provideCount + "");
		updateMap.put("update_time", DateUtil.getSysDateTimeString());
		updateMap.put("updater", (null == updater ? "" : updater));
		
		String sSql = null;
		if("449748120002".equals(DbUp.upTable("oc_coupon_type").dataGet("money_type", "", new MDataMap("coupon_type_code", couponTypeCode)))){
			// 折扣券更新剩余优惠券张数
			sSql = "update oc_coupon_type set privide_money=privide_money+:privide_count,surplus_money=surplus_money-:privide_count where coupon_type_code=:coupon_type_code";
		}else{
			sSql = "update oc_coupon_type set privide_money=privide_money+money*:privide_count,surplus_money=surplus_money-money*:privide_count where coupon_type_code=:coupon_type_code";
		}
		
		int result = DbUp.upTable("oc_coupon_type").dataExec(sSql, updateMap);
		return result;
	}

	/**
	 * 可用优惠券数量
	 * 
	 * @param channelId
	 *            渠道编号
	 * @param memberCode
	 *            用户编号
	 * @param useShouldPay
	 *            应付金额 （为-1时不加限额条件）
	 * @param flag
	 *            是否包含未来可使用的优惠券
	 * @param manageCode
	 *            应用编号
	 * @author ligj
	 */
	public int availableCouponList(String channelId, String memberCode, BigDecimal useShouldPay, boolean flag,
			String manageCode, String appVersion) {

		if (StringUtils.isEmpty(memberCode) || null == useShouldPay) {
			return 0;
		}
		int count = 0;
		String sql = "select ci.coupon_code,ci.surplus_money,ci.status,ci.end_time,ci.limit_money,ci.start_time,ci.coupon_type_code,ci.out_coupon_code "
				+ " from ordercenter.oc_coupon_info ci left join ordercenter.oc_activity oa on ci.activity_code=oa.activity_code ";
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("member_code", memberCode);
		mWhereMap.put("limit_money", useShouldPay.toString());
		mWhereMap.put("manage_code", manageCode);
		sql += " where ci.member_code=:member_code and (ci.status = 0 or (oa.is_change='Y' and ci.status = 1)) and ci.surplus_money>0 and ci.end_time>=now() and ci.manage_code=:manage_code";
		if (!flag) {
			sql += " and ci.start_time <= now()";
		}
		if (useShouldPay.compareTo(new BigDecimal(-1)) != 0) {
			sql += " and ci.limit_money <= :limit_money";
		}

		List<Map<String, Object>> listMaps = DbUp.upTable("oc_coupon_info").dataSqlList(sql, mWhereMap);

		List<String> codes = new ArrayList<String>();
		for(Map<String, Object> m : listMaps){
			//ld的优惠券 没有外部优惠券编号、版本低于5.2.8、活动未发布、活动类型未发布 任一情况 则不能使用此优惠券 -rhb 20181113
			String coupon_type_code = m.get("coupon_type_code")+"";
			String sSql = "select ot.creater,ot.status,oa.flag from ordercenter.oc_coupon_type ot,ordercenter.oc_activity oa where ot.activity_code=oa.activity_code and ot.coupon_type_code=:coupon_type_code";
			Map<String, Object> qResult = DbUp.upTable("oc_coupon_type").dataSqlOne(sSql, new MDataMap("coupon_type_code", coupon_type_code));
			if ("ld".equals(qResult.get("creater"))
					&& ((StringUtils.isNotBlank(appVersion) && AppVersionUtils.compareTo(appVersion, "5.2.8") < 0)
							|| !"1".equals(qResult.get("flag")+"") || !"4497469400030002".equals(qResult.get("status"))
							|| StringUtils.isBlank(m.get("out_coupon_code")+""))) {
				continue;
			}
			codes.add(m.get("coupon_type_code")+"");
		}
		
		Map<String, Map<String, Object>> couponTypeCodesMap = new HashMap<String, Map<String, Object>>();
		String couponTypeCodes = StringUtils.join(codes, "','");
		
		if (StringUtils.isNotEmpty(couponTypeCodes)) {
			String couponCodeTypeSql = "select ot.limit_scope,ot.limit_explain,ot.coupon_type_code,ot.limit_condition,otl.brand_limit,otl.product_limit,otl.category_limit,otl.channel_limit,otl.activity_limit,otl.except_brand,otl.except_product,otl.except_category,otl.except_channel,otl.brand_codes,otl.product_codes,otl.category_codes,otl.channel_codes from oc_coupon_type ot "
					+ "LEFT JOIN oc_coupon_type_limit otl ON ot.coupon_type_code = otl.coupon_type_code "
					+ "where ot.coupon_type_code in ('" + couponTypeCodes + "') ";
			
			// 低于5.1.4版本不显示折扣券
			if(StringUtils.isNotBlank(appVersion) && AppVersionUtils.compareTo(appVersion, "5.1.4") < 0){
				couponCodeTypeSql += " and ot.money_type = '449748120001' ";
			}

			List<Map<String, Object>> couponTypeMapList = DbUp.upTable("oc_coupon_type").dataSqlList(couponCodeTypeSql, null);
			for (Map<String, Object> couponTypeMap : couponTypeMapList) {
				couponTypeCodesMap.put(couponTypeMap.get("coupon_type_code").toString(), couponTypeMap);
			}
		}
		for (Map<String, Object> maps : listMaps) {
			//ld的优惠券 没有外部优惠券编号、版本低于5.2.8、活动未发布、活动类型未发布 任一情况 则不能使用此优惠券 -rhb 20181113
			String coupon_type_code = maps.get("coupon_type_code")+"";
			String sSql = "select ot.creater,ot.status,oa.flag from ordercenter.oc_coupon_type ot,ordercenter.oc_activity oa where ot.activity_code=oa.activity_code and ot.coupon_type_code=:coupon_type_code";
			Map<String, Object> qResult = DbUp.upTable("oc_coupon_type").dataSqlOne(sSql, new MDataMap("coupon_type_code", coupon_type_code));
			if ("ld".equals(qResult.get("creater"))
					&& ((StringUtils.isNotBlank(appVersion) && AppVersionUtils.compareTo(appVersion, "5.2.8") < 0)
							|| !"1".equals(qResult.get("flag")+"") || !"4497469400030002".equals(qResult.get("status"))
							|| StringUtils.isBlank(maps.get("out_coupon_code")+""))) {
				continue;
			}
			
			String couponType = StringUtils.isEmpty((String) maps.get("coupon_type_code")) ? "" : maps.get("coupon_type_code").toString();
			Map<String, Object> couponTypeMap = couponTypeCodesMap.get(couponType);
			if (null == couponTypeMap)
				continue;
			String channel_codes = StringUtils.isEmpty((String) couponTypeMap.get("channel_codes")) ? ""
					: couponTypeMap.get("channel_codes").toString();
			if (StringUtils.isNotEmpty(channelId) && StringUtils.isNotEmpty(channel_codes)) {
				// 如果传入的使用渠道不在指定限制的渠道内
				for (String channelCode : channel_codes.split(",")) {
					if (channelCode.equals(channelId)) {
						count++;
						break;
					}
				}
			} else {
				count++;
			}
		}
		return count;
	}
	
	/**
	 * 新优惠券提示及快过期提示
	 * 
	 * @param channelId
	 *            渠道编号
	 * @param memberCode
	 *            用户编号
	 * @param useShouldPay
	 *            应付金额 （为-1时不加限额条件）
	 * @param flag
	 *            是否包含未来可使用的优惠券
	 * @param manageCode
	 *            应用编号
	 * @author zht
	 */
	public MDataMap personalCouponPrompt(String channelId, String memberCode, boolean flag, String manageCode,String appVersion) 
	{
		MDataMap result = new MDataMap();
		result.put("newCoupon", "0");
		result.put("couponToDead", "0");
		if (StringUtils.isEmpty(memberCode)) {
			return result;
		}
		int count = 0;
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("member_code", memberCode);
		mWhereMap.put("manage_code", manageCode);
		String sql = "select ci.coupon_code,ci.surplus_money,ci.status,ci.end_time,ci.start_time,ci.coupon_type_code,ci.create_time"
				+ " from oc_coupon_info ci,oc_coupon_type ct,oc_activity oa"
				+ " where ci.coupon_type_code=ct.coupon_type_code and ci.activity_code=oa.activity_code"
				+ " and ci.member_code=:member_code and ci.status=0 and ci.surplus_money>0 and ci.end_time>=now() and ci.manage_code=:manage_code";
		
		//ld的优惠券 没有外部优惠券编号、版本低于5.2.8、活动未发布、活动类型未发布 任一情况 则不能使用此优惠券 -rhb 20181113
		if(StringUtils.isNotBlank(appVersion) && AppVersionUtils.compareTo(appVersion, "5.2.8") < 0) {
			sql += " and ct.creater!='ld'";
		}else {
			sql +=" and (ct.creater!='ld' or ( ct.status='4497469400030002' and oa.flag=1 and ci.out_coupon_code!='' and ci.out_coupon_code is not null))";
		}

		List<Map<String, Object>> listMaps = DbUp.upTable("oc_coupon_info").upTemplate().queryForList(sql, mWhereMap);
		if(null != listMaps && listMaps.size() > 0) {
			Map<String, Object> map = listMaps.get(0);
			String newTime = map.get("create_time")+"";
			Map<String, Object> remindMap = DbUp.upTable("oc_coupon_remind").dataSqlOne("select last_time from oc_coupon_remind where member_code=:member_code", new MDataMap("member_code", memberCode));
			if(remindMap != null && remindMap.size() > 0) {
				if(StringUtils.isNotEmpty((String) remindMap.get("last_time"))) {
					if(remindMap.get("last_time").toString().compareTo(newTime) < 0) {
						//DbUp.upTable("oc_coupon_remind").dataUpdate(new MDataMap("last_time",newTime, "member_code",memberCode, "update_time",DateHelper.upNow()), "last_time,update_time", "member_code");
						result.put("newCoupon", "1");
					}
				}
			} else {
				DbUp.upTable("oc_coupon_remind").dataInsert(new MDataMap("member_code",memberCode, "last_time",newTime, "update_time", DateHelper.upNow()));
			}
		}
		
		List<String> codes = new ArrayList<String>();
		for(Map<String, Object> m : listMaps){
			codes.add(m.get("coupon_type_code")+"");
		}

		Map<String, Map<String, Object>> couponTypeCodesMap = new HashMap<String, Map<String, Object>>();
		String couponTypeCodes = StringUtils.join(codes, "','");
		if (StringUtils.isNotEmpty(couponTypeCodes)) {
			String couponCodeTypeSql = "select ot.limit_scope,ot.limit_explain,ot.coupon_type_code,ot.limit_condition,otl.brand_limit,otl.product_limit,otl.category_limit,otl.channel_limit,otl.activity_limit,otl.except_brand,otl.except_product,otl.except_category,otl.except_channel,otl.brand_codes,otl.product_codes,otl.category_codes,otl.channel_codes from oc_coupon_type ot "
					+ "LEFT JOIN oc_coupon_type_limit otl ON ot.coupon_type_code = otl.coupon_type_code "
					+ "where ot.coupon_type_code in ('" + couponTypeCodes + "') ";

			// 低于5.1.4版本不显示折扣券
			if(StringUtils.isNotBlank(appVersion) && AppVersionUtils.compareTo(appVersion, "5.1.4") < 0){
				couponCodeTypeSql += " and ot.money_type = '449748120001' ";
			}
			
			List<Map<String, Object>> couponTypeMapList = DbUp.upTable("oc_coupon_type").dataSqlList(couponCodeTypeSql, null);
			for (Map<String, Object> couponTypeMap : couponTypeMapList) {
				couponTypeCodesMap.put(couponTypeMap.get("coupon_type_code").toString(), couponTypeMap);
			}
		}
		
		//优惠券快到期提醒天数
		double deadlineDay = Double.parseDouble(StringUtils.isEmpty(bConfig("ordercenter.COUPON_DEADLINE_DAY")) ? "2.0" : bConfig("ordercenter.COUPON_DEADLINE_DAY"));
		for (Map<String, Object> maps : listMaps) {
			String couponType = StringUtils.isEmpty((String) maps.get("coupon_type_code")) ? "" : maps.get("coupon_type_code").toString();
			Map<String, Object> couponTypeMap = couponTypeCodesMap.get(couponType);
			if (null == couponTypeMap)
				continue;
			String channel_codes = StringUtils.isEmpty((String) couponTypeMap.get("channel_codes")) ? "" : couponTypeMap.get("channel_codes").toString();
			if (StringUtils.isNotEmpty(channel_codes)) {
				//有渠道限制,判断传入的调用API渠道号是不是在使用渠道限制内
				for (String channelCode : channel_codes.split(",")) {
					if (channelCode.equals(channelId)) {
						String endTime = maps.get("end_time").toString();
						try {
							double diffDay = DateHelper.daysBetween(DateHelper.upNow(), endTime);
							if(diffDay>0.0 && diffDay <= deadlineDay)
								count++;
						} catch (ParseException e) {
							e.printStackTrace();
						}
						break;
					}
				}
			} else {
				//无渠道限制
				String endTime = maps.get("end_time").toString();
				try {
					double diffDay = DateHelper.daysBetween(DateHelper.upNow(), endTime);
					if(diffDay>0.0 && diffDay <= deadlineDay)
						count++;
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		result.put("couponToDead", String.valueOf(count));
		return result;
	}
	
	

	/**
	 * 传入用户编号，优惠券编号，金额查询出优惠券是否可用的状态
	 * 
	 * @param memberCode
	 * @param couponCode
	 * @param limitMoney
	 * @return resultCode为1表示可用，其他为不可用
	 */
	public CouponInfoRootResult getCouponInfo(String memberCode, String couponCode, BigDecimal money) {
		CouponInfoRootResult result = new CouponInfoRootResult();
		if (StringUtils.isEmpty(memberCode) || null == money || StringUtils.isEmpty(couponCode)) {
			result.setResultCode(939301304);
			result.setResultMessage(bInfo(939301304));
			return result;
		}
		MDataMap map = DbUp.upTable("oc_coupon_info").oneWhere(
				"coupon_code,status,surplus_money,limit_money,end_time,start_time", "",
				"coupon_code='" + couponCode + "' and member_code='" + memberCode + "'");
		if (null == map || map.isEmpty()) {
			result.setResultCode(939301307);
			result.setResultMessage(bInfo(939301307));
			return result;
		}
		result.setCouponCode(couponCode);
		result.setStatus(Integer.parseInt(map.get("status")));
		result.setSurplusMoney(
				new BigDecimal(Double.parseDouble(map.get("surplus_money"))).setScale(2, BigDecimal.ROUND_DOWN));
		result.setEndTime(map.get("end_time"));
		result.setStartTime(map.get("start_time"));
		result.setLimitMoney(
				new BigDecimal(Double.parseDouble(map.get("limit_money"))).setScale(2, BigDecimal.ROUND_DOWN));
		result.setCount(1);
		// 剩余金额小于等于0或者状态为已使用时
		if (result.getStatus() == 1 || result.getSurplusMoney().compareTo(BigDecimal.ZERO) <= 0) {
			result.setResultCode(939301305);
			result.setResultMessage(bInfo(939301305));
			return result;
		}
		// 状态为已使用并且结束时间小于等于当前时间，优惠券已过期
		if (result.getStatus() == 0 && result.getEndTime().compareTo(DateUtil.getSysDateTimeString()) <= 0) {
			result.setResultCode(939301306);
			result.setResultMessage(bInfo(939301306));
			return result;
		}
		// 开始时间大于当前时间，优惠券未生效
		if (result.getStartTime().compareTo(DateUtil.getSysDateTimeString()) > 0) {
			result.setResultCode(939301308);
			result.setResultMessage(bInfo(939301308));
			return result;
		}
		// 订单金额不满足优惠券最低限额，无法使用！
		if (result.getLimitMoney().compareTo(money) > 0) {
			result.setResultCode(939301309);
			result.setResultMessage(bInfo(939301309));
			return result;
		}
		return result;
	}

	/**
	 * 获取优惠券信息
	 * 
	 * @param couponCode
	 *            优惠券编号
	 * @return coupon
	 */
	public CouponInfo getCouponInfo(String couponCode) {
		CouponInfo info = new CouponInfo();
		if (StringUtils.isEmpty(couponCode)) {
			return info;
		}
		MDataMap couponMap = DbUp.upTable("oc_coupon_info").one("coupon_code", couponCode);
		if (null == couponMap || couponMap.isEmpty()) {
			return info;
		}
		info.setCouponCode(couponMap.get("coupon_code"));
		info.setStatus(Integer.parseInt(couponMap.get("status")));
		info.setStartTime(couponMap.get("start_time"));
		info.setEndTime(couponMap.get("end_time"));
		info.setLimitMoney(new BigDecimal(Double.parseDouble(couponMap.get("limit_money"))));
		info.setSurplusMoney(new BigDecimal(Double.parseDouble(couponMap.get("surplus_money"))));

		return info;
	}

	/**
	 * 使用优惠券
	 * 
	 * @param orderCode
	 *            订单编号
	 * @return 1成功
	 */
	public int useCoupon(String orderCode) {
		if (StringUtils.isEmpty(orderCode)) {
			return 0;
		}

		// OS开头的订单为大订单，需要获得其下的小订单编号，DD开头的为小订单
		if (orderCode.indexOf("OS") == 0) {
			List<MDataMap> orderCodeMapList = DbUp.upTable("oc_orderinfo").queryAll("order_code", "",
					"big_order_code='" + orderCode + "'", null);
			List<String> orderCodeList = new ArrayList<String>();
			for (MDataMap orderCodeMap : orderCodeMapList) {
				orderCodeList.add(orderCodeMap.get("order_code"));
			}

			orderCode = StringUtils.join(orderCodeList, "','");
		}
		// 获得使用的优惠券编号
		String sWhere = " order_code in ('" + orderCode + "') and pay_type='449746280002'";
		List<MDataMap> couponCodeList = DbUp.upTable("oc_order_pay").queryAll("order_code,pay_sequenceid,payed_money",
				"", sWhere, null);
		Map<String, BigDecimal> couponCodeMap = new HashMap<String, BigDecimal>(); // key:coupon_code;value:money
		for (MDataMap couponCode : couponCodeList) {

			BigDecimal money = couponCodeMap.get(couponCode.get("pay_sequenceid"));
			BigDecimal payedMoney = new BigDecimal(Double.parseDouble(couponCode.get("payed_money"))).setScale(2,
					BigDecimal.ROUND_DOWN);
			if (null != money && money.compareTo(BigDecimal.ZERO) >= 0) {
				payedMoney = money.add(payedMoney); // 所有优惠券的金额
			}
			couponCodeMap.put(couponCode.get("pay_sequenceid"), payedMoney);
		}
		for (String couponCode : couponCodeMap.keySet()) {
			MDataMap updateMap = new MDataMap();
			updateMap.put("coupon_code", couponCode);
			updateMap.put("status", "1");
			updateMap.put("surplus_money", couponCodeMap.get(couponCode) + "");
			updateMap.put("update_time", DateUtil.getSysDateTimeString());
			updateMap.put("last_mdf_id", "app");
			String sSql = "update oc_coupon_info set last_mdf_id=:last_mdf_id,status=:status,update_time=:update_time,surplus_money=(case when initial_money-:surplus_money < 0 then 0 else initial_money-:surplus_money end  ) where coupon_code=:coupon_code and status!=1";
			int result = DbUp.upTable("oc_coupon_info").dataExec(sSql, updateMap);
			return result;
		}
		return 0;
	}

	/**
	 * 检查是否可以还原优惠券，用于单个订单取消发货时判断。
	 * @param orderCode DD开头订单号
	 * @return  同一个大单下所有子单都取消时可以还原
	 */
	public boolean isCanRollbackCoupon(String bigOrderCode) {
		if(StringUtils.isNotBlank(bigOrderCode)) {
			int totalNum = DbUp.upTable("oc_orderinfo").count("big_order_code", bigOrderCode);
			int totalCancelNum = DbUp.upTable("oc_orderinfo").countPri("big_order_code", bigOrderCode,"order_status","4497153900010006");
			if(totalNum > 0 && totalNum == totalCancelNum) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 回滚优惠券
	 * 
	 * @param orderCode
	 *            订单编号
	 * @return 1成功
	 */
	public List<GiftVoucherInfo> rollbackCoupon(String orderCode) {
		if (StringUtils.isEmpty(orderCode)) {
			return null;
		}

		// OS开头的订单为大订单，需要获得其下的小订单编号，DD开头的为小订单
		if (orderCode.indexOf("OS") == 0) {
			List<MDataMap> orderCodeMapList = DbUp.upTable("oc_orderinfo").queryAll("order_code", "",
					"big_order_code='" + orderCode + "'", null);
			List<String> orderCodeList = new ArrayList<String>();
			for (MDataMap orderCodeMap : orderCodeMapList) {
				orderCodeList.add(orderCodeMap.get("order_code"));
			}

			orderCode = StringUtils.join(orderCodeList, "','");
		}

		// 获得使用的优惠券编号
		List<GiftVoucherInfo> reWriteLD = new ArrayList<GiftVoucherInfo>();
		String sWhere = " order_code in ('" + orderCode + "') and pay_type='449746280002'";		
		String sql = "select distinct pay_sequenceid from oc_order_pay where " + sWhere;
		List<Map<String,Object>> couponList = DbUp.upTable("oc_order_pay").dataSqlList(sql, null);
		for(Map<String,Object> map : couponList) {
			String pay_sequenceid = map.get("pay_sequenceid").toString();
			String csql = sWhere + " and pay_sequenceid = '" + pay_sequenceid + "'";
			List<MDataMap> couponCodeList = DbUp.upTable("oc_order_pay").queryAll("order_code,pay_sequenceid,payed_money",
					"", csql, null);
			Map<String, BigDecimal> couponCodeMap = new HashMap<String, BigDecimal>(); // key:coupon_code;value:money
			for (MDataMap couponCode : couponCodeList) {
				BigDecimal money = couponCodeMap.get(couponCode.get("pay_sequenceid"));
				BigDecimal payedMoney = new BigDecimal(couponCode.get("payed_money"));
				if (null != money && money.compareTo(BigDecimal.ZERO) >= 0) {
					payedMoney = money.add(payedMoney); // 所有优惠券的金额
				}
				
				//优惠券一体化的券 并且tv品订单 并且订单同步到ld 则跳过
				MDataMap couponInfo = DbUp.upTable("oc_coupon_info").oneWhere("out_coupon_code", "","coupon_code=:coupon_code", "coupon_code", couponCode.get("pay_sequenceid"));
				MDataMap orderInfo = DbUp.upTable("oc_orderinfo").oneWhere("out_order_code", "", "order_code=:order_code and small_seller_code='SI2003'", "order_code", couponCode.get("order_code"));
				if(null != couponInfo && null != orderInfo && StringUtils.isNotEmpty(couponInfo.get("out_coupon_code")) && StringUtils.isNotEmpty(orderInfo.get("out_order_code"))) {
					continue;
				}
				
				couponCodeMap.put(couponCode.get("pay_sequenceid"), payedMoney);
				
				
//				//如果是礼金券，取消订单则退还使用的礼金券
//				Map<String, Object> couponmap = DbUp.upTable("oc_coupon_info").dataSqlOne("select ct.money_type from oc_coupon_type ct,oc_coupon_info ci where ci.coupon_type_code = ct.coupon_type_code and ci.coupon_code = :couponCode", new MDataMap("couponCode",couponCode.get("pay_sequenceid")));
//				if(couponmap != null && "449748120003".equals(couponmap.get("money_type"))) {
//					GiftVoucherInfo couponToLD = new GiftVoucherInfo();
//					couponToLD.setHjy_ord_id(couponCode.get("order_code"));
//					couponToLD.setLj_code(couponCode.get("pay_sequenceid"));
//					reWriteLD.add(couponToLD);
//				}
			}
			
			for (String couponCode : couponCodeMap.keySet()) {
				MDataMap updateMap = new MDataMap();
				updateMap.put("coupon_code", couponCode);
				updateMap.put("status", "0");
				updateMap.put("surplus_money", couponCodeMap.get(couponCode) + "");
				updateMap.put("update_time", DateUtil.getSysDateTimeString());
				updateMap.put("last_mdf_id", "app");
				
				/**
				 * 金额券和礼金券的处理：money_type：449748120001||449748120003
				 * 560版本增加找零券 所以 剩余金额+返还金额=初始金额 则还原状态 
				 */
				String sSql = "update oc_coupon_info set last_mdf_id=:last_mdf_id,status=(case when initial_money-(:surplus_money+surplus_money) <= 0 then :status else '1' end  ),update_time=:update_time,surplus_money=(case when initial_money-(:surplus_money+surplus_money) < 0 then initial_money else (:surplus_money+surplus_money) end  ) where coupon_code=:coupon_code and status!=0";
				
				Map<String, Object> couponmap = DbUp.upTable("oc_coupon_info").dataSqlOne("select ct.money_type from oc_coupon_type ct,oc_coupon_info ci where ci.coupon_type_code = ct.coupon_type_code and ci.coupon_code = :couponCode", new MDataMap("couponCode",couponCode));
				if(couponmap != null && "449748120002".equals(couponmap.get("money_type"))){
					// 折扣券特殊处理，剩余金额值固定不变
					sSql = "update oc_coupon_info set last_mdf_id=:last_mdf_id,status=:status,update_time=:update_time where coupon_code=:coupon_code and status!=0";
				}
				
				DbUp.upTable("oc_coupon_info").dataExec(sSql, updateMap);
				
				//5.5.8 取消订单时 删除优惠券使用记录
				XmasKv.upFactory(EKvSchema.CouponUse).del(couponCode);
			}
		}
		return reWriteLD;
	}

	/**
	 * 获取活动的总成本限额(优惠券类型管理页面调用bookCouponActivity.ftl) 只获取优惠码的总限额
	 * 
	 * @return
	 */
	public BigDecimal getTotalMoney(String activityCode) {
		MDataMap totalMoneyMap = DbUp.upTable("oc_coupon_type").oneWhere("SUM(total_money) as total_money", "", "",
				"activity_code", activityCode, "produce_type", "4497471600040001");
		if (null == totalMoneyMap || totalMoneyMap.isEmpty() || null == totalMoneyMap.get("total_money")
				|| StringUtils.isEmpty(totalMoneyMap.get("total_money"))) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(Double.parseDouble(totalMoneyMap.get("total_money")));
	}

	/**
	 * 获取优惠券活动的状态
	 * 
	 * @param activityCode
	 * @return
	 */
	public String getActivityStatus(String activityCode) {
		MDataMap activityInfo = DbUp.upTable("oc_activity").oneWhere("flag", "", "", "activity_code", activityCode);
		if (null != activityInfo && !activityInfo.isEmpty()) {
			if ("0".equals(activityInfo.get("flag"))) {
				return "未发布";
			} else if ("1".equals(activityInfo.get("flag"))) {
				return "已发布";
			}
		}
		return "";
	}
	
	/**
	 * 获取优惠券活动的发放条件
	 * 
	 * @param activityCode
	 * @return
	 */
	public String getActivityAssignTrigger(String activityCode) {
		MDataMap activityInfo = DbUp.upTable("oc_activity").oneWhere("assign_trigger,assign_line", "", "", "activity_code", activityCode);
		if (null != activityInfo && !activityInfo.isEmpty()) {
			if ("4497471600340001".equals(activityInfo.get("assign_trigger"))) {
				String assign_line = activityInfo.get("assign_line");
				if(StringUtils.isEmpty(assign_line)) {
					assign_line = "0.0";
				}
				return "下单满" + assign_line + "元";
			} else if ("4497471600340002".equals(activityInfo.get("assign_trigger"))) {
				return "首次下单";
			}
		}
		return "";
	}
	
	/**
	 * 转换优惠券面额的显示方式 <br>
	 * 折扣券面额 88 转换为 8.8 <br>
	 * @param couponInfo
	 */
	public void convertMoneyShow(CouponInfo couponInfo){
		// 折扣卷
		if("449748120002".equals(couponInfo.getMoneyType())){
			BigDecimal initialMoney = couponInfo.getInitialMoney(); 
			
			// 折扣券需要把数据库里面的值除以10后显示
			initialMoney = initialMoney.divide(new BigDecimal(10),1,BigDecimal.ROUND_HALF_UP);
			// 如果没有小数的不再保留小数位，方面客户端显示
			if(initialMoney.setScale(0, BigDecimal.ROUND_UP).compareTo(initialMoney) == 0){
				initialMoney = initialMoney.setScale(0, BigDecimal.ROUND_UP);
			}
			// 折扣券的初始金额和剩余金额保持一致
			couponInfo.setInitialMoney(initialMoney);
			couponInfo.setSurplusMoney(initialMoney);
		}
	}
	
	/**
	 * 查询优惠券的已发放和剩余份数
	 * @param couponTypeCode
	 * @return {privide 已发放份数, surplus 剩余份数}
	 */
	public Map<String,Integer> getCouponTypeProvide(String couponTypeCode){
		MDataMap couponType = DbUp.upTable("oc_coupon_type").one("coupon_type_code", couponTypeCode);
		if(couponType == null){
			return Collections.emptyMap();
		}
		
		BigDecimal money = new BigDecimal(couponType.get("money"));
		BigDecimal privide = new BigDecimal(couponType.get("privide_money"));
		BigDecimal surplus = new BigDecimal(couponType.get("surplus_money"));
		
		// 非折扣券默认都当前金额券处理
		if(!"449748120002".equals(couponType.get("money_type"))){
			if(money.compareTo(BigDecimal.ZERO) > 0) {
				privide = privide.divide(money,0,BigDecimal.ROUND_HALF_UP);
				surplus = surplus.divide(money,0,BigDecimal.ROUND_HALF_UP);
			}
		}
		
		Map<String,Integer> resMap = new HashMap<String, Integer>();
		resMap.put("privideNum", privide.intValue());
		resMap.put("surplusNum", surplus.intValue());
		
		return resMap;
	}
	
	public static void main(String[] args) {
		String deadline = DateUtil.toString(DateUtil.addDays(new Date(), Integer.parseInt("1")), "yyyy-MM-dd");
		deadline += " 23:59:59";
		System.out.println(deadline);
	}

	/**
	 * 根据优惠券的uid以及用户编号校验优惠券是否存在
	 * @param uid
	 * @param couponTypeCode
	 * @return true 存在，可以领取
	 */
	public boolean checkCouponExist(String uid, String couponTypeCode) {
		CouponExistQuery query = new CouponExistQuery();
		query.setCode(uid+"-"+couponTypeCode);
		CouponExistInfo info = new LoadCouponExist().upInfoByCode(query);
		if(info!=null) {
			return true;
		}
		return false;
	}

	/**
	 * 校验用户是否已经领取过优惠券
	 * @param memberCode
	 * @param couponTypeCode
	 * @return false 领过了。
	 */
	public boolean checkUserLegal(String memberCode, String couponTypeCode) {
		CouponGetUserQuery  tQuery = new CouponGetUserQuery();
		tQuery.setCode(memberCode+"-"+couponTypeCode);
		CouponGetUserInfo info = new LoadCouponGetUser().upInfoByCode(tQuery);
		if(info != null) {
			return false;
		}
		return true;
	}
	
	/**
	 * 获取兑换码兑换列表页
	 * @param activityCode
	 * @return
	 */
	public List<MDataMap> getCouponRedeem(String activityCode){
		List<MDataMap> list = DbUp.upTable("oc_coupon_redeem").query("is_redeem,import_count,create_time", "import_count asc", 
				"activity_code=:activity_code", new MDataMap("activity_code", activityCode), -1, -1);
		if(null==list || list.isEmpty()) {
			return new ArrayList<MDataMap>();
		}
		Map<String, List<MDataMap>> todoMap = new HashMap<String, List<MDataMap>>();
		for (MDataMap mDataMap : list) {
			if(todoMap.containsKey(mDataMap.get("import_count"))) {
				todoMap.get(mDataMap.get("import_count")).add(mDataMap);
			}else {
				List<MDataMap> todoList = new ArrayList<MDataMap>();
				todoList.add(mDataMap);
				todoMap.put(mDataMap.get("import_count"), todoList);
			}
		}
		if(!todoMap.isEmpty()) {
			List<MDataMap> result = new ArrayList<MDataMap>();
			for (Map.Entry<String, List<MDataMap>> entry : todoMap.entrySet()) {
			   String key = entry.getKey();
			   List<MDataMap> value = entry.getValue();
			   MDataMap redeem = new MDataMap();
			   redeem.put("import_count", key);
			   redeem.put("send_count", value.size()+"");
			   int redeemCount = 0;
			   for (MDataMap mDataMap : value) {
				   if("1".equals(mDataMap.get("is_redeem"))) redeemCount++;
			   }
			   redeem.put("redeem_count", redeemCount+"");
			   redeem.put("create_time", value.get(0).get("create_time"));
			   result.add(redeem);
			}
			return result;
		}
		return new ArrayList<MDataMap>();
	}
	
	/**
	 * 根据优惠券编号判断是否为LD创建的活动（优惠券一体化）
	 * @param activityCode
	 * @return
	 */
	public boolean isLdCoupon(String activityCode) {
		String creator = DbUp.upTable("oc_activity").dataGet("creator", "activity_code=:activity_code", new MDataMap("activity_code", activityCode))+"";
		return "ld".equals(creator);
	}
	
	/**
	 * 根据优惠券编号获取叠加限制条件（优惠券一体化）
	 * @return 
	 */
	public MDataMap getPileInfo(String activityCode) {
		return DbUp.upTable("oc_activity").one("activity_code", activityCode);
	}
	
	/**
	 * 设置本次返回的为查看优惠卷为已查看 
	 * @param couponInfoList 本次返回的所有优惠卷
	 */
	public void setCouponInfoSee(List<CouponInfo> couponInfoList){
		String couponCodeString = "";
		for(CouponInfo couponInfo: couponInfoList){
			if("0".equals(couponInfo.getIsSee())){
				//记录未查看的优惠卷编号
				couponCodeString +="'"+couponInfo.getCouponCode()+"',";
			}
		}
		if(StringUtils.isNotBlank(couponCodeString)){
			couponCodeString = couponCodeString.substring(0,couponCodeString.length()-1);
			//设置未查看为已查看
			DbUp.upTable("oc_coupon_info").dataExec("update oc_coupon_info set is_see = 1 where coupon_code in("+couponCodeString+")",new MDataMap());
		}
	}
	
	/**
	 * 系统返利优惠券发放使用统计
	 */
	public List<MDataMap> getCouponSendInfos(String activityCode){
		
		ArrayList<MDataMap> arrayList = new ArrayList<MDataMap>();
		MDataMap mDataMap = new MDataMap();
		MDataMap one = DbUp.upTable("oc_activity").one("activity_code",activityCode);
		String sql = "select count(uid) allNum, IFNULL(sum((case  when status=1 then 1 else 0 end)),0) useNum from oc_coupon_info where activity_code='"+activityCode+"' ";
		List<Map<String, Object>> dataSqlList = DbUp.upTable("oc_coupon_info").dataSqlList(sql, null);
		if(dataSqlList!=null) {
			Map<String, Object> map = dataSqlList.get(0);
			mDataMap.put("send_count", map.get("allNum").toString());
			mDataMap.put("use_count", map.get("useNum").toString());
			mDataMap.put("create_time", one.get("create_time"));
		}
		arrayList.add(mDataMap);
		
        
		
		return arrayList;
	}
	
	
}

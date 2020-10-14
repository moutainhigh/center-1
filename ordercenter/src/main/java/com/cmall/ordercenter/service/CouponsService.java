package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSON;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.familyhas.active.ActiveForproduct;
import com.cmall.ordercenter.familyhas.active.ActiveReq;
import com.cmall.ordercenter.familyhas.active.ActiveResult;
import com.cmall.ordercenter.familyhas.active.ActiveReturn;
import com.cmall.ordercenter.model.BrandBaseInfo;
import com.cmall.ordercenter.model.Button;
import com.cmall.ordercenter.model.CategoryBaseInfo;
import com.cmall.ordercenter.model.CouponForGetInfo;
import com.cmall.ordercenter.model.CouponInfo;
import com.cmall.ordercenter.model.CouponTypeLimitBaseInfo;
import com.cmall.ordercenter.model.CouponTypeLimitDTO;
import com.cmall.ordercenter.model.GoodsInfoForAdd;
import com.cmall.ordercenter.model.ProductBaseInfo;
import com.cmall.ordercenter.util.CouponUtil;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.util.Base64Util;
import com.cmall.systemcenter.common.CouponConst;
import com.srnpr.xmassystem.load.LoadCouponGetUser;
import com.srnpr.xmassystem.load.LoadCouponListForProduct;
import com.srnpr.xmassystem.load.LoadProductInfo;
import com.srnpr.xmassystem.load.LoadSkuInfo;
import com.srnpr.xmassystem.modelbean.CouponGetUserInfo;
import com.srnpr.xmassystem.modelbean.CouponGetUserQuery;
import com.srnpr.xmassystem.modelevent.CouponListQuery;
import com.srnpr.xmassystem.modelevent.ModelCouponForGetInfo;
import com.srnpr.xmassystem.modelevent.PlusModelCouponListInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductQuery;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.support.PlusSupportMember;
import com.srnpr.xmassystem.util.AppVersionUtils;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.MoneyHelper;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webapi.RootResultWeb;
import com.srnpr.zapweb.webmodel.MPageData;
import com.srnpr.zapweb.webpage.ControlPage;

/**
 * @ClassName: CouponsService
 * @Description: 优惠券管理
 * @author 张海生
 * @date 2015-4-8 上午10:56:08
 * 
 */
public class CouponsService extends BaseClass {

	CouponUtil couponUtil = new CouponUtil();
	
	/**
	 * @throws ParseException
	 *             --沙皮狗
	 * @Description:插入优惠券
	 * @param mobile
	 *            手机号
	 * @param cdkey
	 *            优惠码
	 * @author 张海生
	 * @date 2015-4-8 上午11:01:01
	 * @return RootResultWeb
	 * @throws
	 */
	public RootResultWeb saveCouponsUserForDog(String mobile)
			throws ParseException {
		RootResultWeb result = new RootResultWeb();
		String cdkey = "520";// 优惠码为520
		String nowTime = DateUtil.getNowTime();
		MDataMap ctMap = DbUp.upTable("oc_coupon_type").oneWhere("activity_code,coupon_type_code", "", "", "cdkey", cdkey);
		String activityCode = ctMap.get("activity_code");
		MDataMap activityMap = DbUp.upTable("oc_activity").oneWhere("begin_time,end_time,flag", "", "", "activity_code", activityCode, "activity_type", "449715400007");
		if (activityMap == null) {
			result.inErrorMessage(939301301);// 活动已结束
			return result;
		}
		
		String startTime = activityMap.get("begin_time");
		String endTime = activityMap.get("end_time");
		if (DateUtil.compareTime(startTime, nowTime, "yyyy-MM-dd HH:mm:ss") > 0) {
			result.inErrorMessage(939301314);// 活动未开始
			return result;
		}
		if (DateUtil.compareTime(nowTime, endTime, "yyyy-MM-dd HH:mm:ss") > 0) {
			result.inErrorMessage(939301301);// 活动已结束
			return result;
		}
		// 查询是否已经领取
		int count1 = DbUp.upTable("oc_coupon_provide").count("mobile", mobile, "coupon_type_code", ctMap.get("coupon_type_code"));
		if (count1 > 0) {
			// 已经有此手机号的记录
			result.inErrorMessage(939301303);
			return result;
		}
		
		// 查询惠家友里对应的用户code
		MDataMap udata = DbUp.upTable("mc_login_info").oneWhere("member_code", null, null, "login_name", mobile, "manage_code", "SI3003");
		if (udata != null) {
			String memCode = udata.get("member_code");
			if (StringUtils.isNotEmpty(memCode)) {
				// 查询用户优惠券记录里是否有，防止通过兑换优惠码的地方兑换过
				int ciCount = DbUp.upTable("oc_coupon_info").count("cdkey",	cdkey, "member_code", memCode);
				if (ciCount > 0) {
					// 已经有此手机号的记录
					result.inErrorMessage(939301303);
					return result;
				}
			}
		}
		
		// 查询已经启用的优惠券类型信息
		List<MDataMap> mdataList = DbUp.upTable("oc_coupon_type").queryAll("total_money,money,start_time,end_time,activity_code,coupon_type_code", null,
						"cdkey=:cdkey and status='4497469400030002' and end_time>=now()", new MDataMap("cdkey", cdkey));
		if (mdataList != null && mdataList.size() > 0) {
			for (MDataMap mdata : mdataList) {
				String typeCode = mdata.get("coupon_type_code");
				MDataMap insertMap = new MDataMap();
				insertMap.put("uid", WebHelper.upUuid());
				insertMap.put("mobile", mobile);
				insertMap.put("status", "0");
				insertMap.put("coupon_type_code", typeCode);
				insertMap.put("uq_code", typeCode + "_" + mobile);
				insertMap.put("create_time", DateUtil.getNowTime());
				DbUp.upTable("oc_coupon_provide").dataInsert(insertMap);// 插入表中
			}
		} else {
			result.inErrorMessage(939301301);// 活动已结束
		}
		return result;
	}

	/**
	 * @throws ParseException
	 * @Description:插入优惠券
	 * @param mobile
	 *            手机号
	 * @param cdkey
	 *            优惠码
	 * @author 张海生
	 * @date 2015-4-8 上午11:01:01
	 * @return RootResultWeb
	 * @throws
	 */
	public RootResultWeb saveCouponsUser(String mobile) throws ParseException {
		RootResultWeb result = new RootResultWeb();
		// 优惠码为520
		String cdkey = "520";
		String nowTime = DateUtil.getNowTime();
		MDataMap ctMap = DbUp.upTable("oc_coupon_type").oneWhere("activity_code,coupon_type_code", "", "", "cdkey", cdkey);
		String activityCode = ctMap.get("activity_code");
		MDataMap activityMap = DbUp.upTable("oc_activity").oneWhere("begin_time,end_time,flag", "", "", "activity_code", activityCode, "activity_type", "449715400007");
		if (activityMap == null) {
			// 活动已结束
			result.inErrorMessage(939301301);
			return result;
		}
		
		String startTime = activityMap.get("begin_time");
		String endTime = activityMap.get("end_time");
		if (DateUtil.compareTime(startTime, nowTime, "yyyy-MM-dd HH:mm:ss") > 0) {
			// 活动未开始
			result.inErrorMessage(939301314);
			return result;
		}
		
		if (DateUtil.compareTime(nowTime, endTime, "yyyy-MM-dd HH:mm:ss") > 0) {
			// 活动已结束
			result.inErrorMessage(939301301);
			return result;
		}
		
		// 查询是否已经领取
		int count1 = DbUp.upTable("oc_coupon_provide").count("mobile", mobile, "coupon_type_code", ctMap.get("coupon_type_code"));
		if (count1 > 0) {
			result.inErrorMessage(939301303);// 已经有此手机号的记录
			return result;
		}
		
		// 查询惠家友里对应的用户code
		MDataMap udata = DbUp.upTable("mc_login_info").oneWhere("member_code", null, null, "login_name", mobile, "manage_code", "SI2003");
		if (udata != null) {
			String memCode = udata.get("member_code");
			if (StringUtils.isNotEmpty(memCode)) {
				// 查询用户优惠券记录里是否有，防止通过兑换优惠码的地方兑换过
				int ciCount = DbUp.upTable("oc_coupon_info").count("cdkey", cdkey, "member_code", memCode);
				if (ciCount > 0) {
					result.inErrorMessage(939301303);// 已经有此手机号的记录
					return result;
				}
			}
		}
		
		// 查询已经启用的优惠券类型信息
		List<MDataMap> mdataList = DbUp.upTable("oc_coupon_type").queryAll("total_money,money,start_time,end_time,activity_code,coupon_type_code",
						null, "cdkey=:cdkey and status='4497469400030002' and end_time>=now()",	new MDataMap("cdkey", cdkey));
		if (mdataList != null && mdataList.size() > 0) {
			for (MDataMap mdata : mdataList) {
				String typeCode = mdata.get("coupon_type_code");
				MDataMap insertMap = new MDataMap();
				insertMap.put("uid", WebHelper.upUuid());
				insertMap.put("mobile", mobile);
				insertMap.put("status", "0");
				insertMap.put("coupon_type_code", typeCode);
				insertMap.put("uq_code", typeCode + "_" + mobile);
				insertMap.put("create_time", DateUtil.getNowTime());
				DbUp.upTable("oc_coupon_provide").dataInsert(insertMap);// 插入表中
			}
		} else {
			result.inErrorMessage(939301301);// 活动已结束
		}
		return result;
	}

	/**
	 * @Description: 领取优惠券
	 * @param mobile
	 *            手机号
	 * @author 张海生
	 * @date 2015-5-22 下午2:51:50
	 * @return RootResultWeb
	 * @throws
	 */
	public RootResultWeb getCoupons(String mobile) {
		RootResultWeb result = new RootResultWeb();
		String activityCode = bConfig("familyhas.coupon_activity_code");
		MDataMap activtyMap = new MDataMap();
		if (VersionHelper.checkServerVersion("3.5.62.55")) {
			// 优惠券迭代三
			activtyMap = DbUp.upTable("oc_activity").oneWhere("activity_code,begin_time,end_time,provide_type,provide_num", "", "", "activity_type", 
					"449715400007", "flag", "1", "activity_code", activityCode);
		} else {
			activtyMap = DbUp.upTable("oc_activity").oneWhere("activity_code,begin_time,end_time", "", "", "activity_type", "449715400007", 
					"flag", "1", "activity_code", activityCode);
		}
		
		if (activtyMap != null) {
			String nowTime = DateUtil.getNowTime();
			String startTime = activtyMap.get("begin_time");
			String endTime = activtyMap.get("end_time");
			if (DateUtil.compareTime(startTime, nowTime, "yyyy-MM-dd HH:mm:ss") > 0) {
				result.inErrorMessage(939301314);// 活动未开始
				return result;
			}
			
			if (DateUtil.compareTime(nowTime, endTime, "yyyy-MM-dd HH:mm:ss") > 0) {
				result.inErrorMessage(939301301);// 活动已结束
				return result;
			}
			
			List<MDataMap> mdataList = new ArrayList<MDataMap>();
			if (VersionHelper.checkServerVersion("3.5.62.55")) {
				// 优惠券迭代三
				// 查询已经启用的优惠券类型信息
				mdataList = DbUp.upTable("oc_coupon_type").queryAll("total_money,money,start_time,end_time,activity_code,coupon_type_code",	null,
								"status='4497469400030002' and end_time>=now() and activity_code=:activity_code", new MDataMap("activity_code", activtyMap.get("activity_code")));
			} else {
				// 查询已经启用的优惠券类型信息
				mdataList = DbUp.upTable("oc_coupon_type").queryAll("total_money,money,start_time,end_time,activity_code,coupon_type_code", null,
								"produce_type='4497471600040001' and status='4497469400030002' and end_time>=now() and activity_code=:activity_code",
								new MDataMap("activity_code", activtyMap.get("activity_code")));
			}
			
			if (mdataList != null && mdataList.size() > 0) {
				for (MDataMap mdata : mdataList) {
					String typeCode = mdata.get("coupon_type_code");
					// 查询是否已经领取
					int count1 = DbUp.upTable("oc_coupon_provide").count("mobile", mobile, "coupon_type_code", typeCode);
					if (count1 > 0) {
						result.inErrorMessage(939301303);// 已经有此手机号的记录
						return result;
					}
					
					if (VersionHelper.checkServerVersion("3.5.62.55")) {
						// 优惠券迭代三
						if ("4497471600060002".equals(activtyMap.get("provide_type"))) {
							// 系统发放
							int totalNum = Integer.parseInt(activtyMap.get("provide_num"));   // 发放份数
							int provideCount = DbUp.upTable("oc_coupon_provide").count("mobile", mobile, "coupon_type_code", typeCode);
							if (provideCount >= totalNum) {
								result.inErrorMessage(939301316);// 优惠码已兑换完
								return result;
							}
						}
					}
					
					MDataMap insertMap = new MDataMap();
					insertMap.put("uid", WebHelper.upUuid());
					insertMap.put("mobile", mobile);
					insertMap.put("status", "0");
					insertMap.put("coupon_type_code", typeCode);
					insertMap.put("uq_code", typeCode + "_" + mobile);
					insertMap.put("create_time", DateUtil.getNowTime());
					DbUp.upTable("oc_coupon_provide").dataInsert(insertMap);// 插入表中
				}
			} else {
				result.inErrorMessage(939301301);// 活动已结束
				return result;
			} 
		} else {
			result.inErrorMessage(939301301);// 活动已结束
		}
		return result;
	}
	
	/**
	 * @Description: 用户优惠码兑换优惠券
	 * @param cdkey 优惠码
	 * @param userCode 用户code
	 * @param manageCode 应用编号
	 * @date 2015-4-18 下午12:32:53
	 * @return RootResultWeb
	 * @throws
	 */
	public RootResultWeb couponCodeExchange(String cdkey, String memberCode, String manageCode) {
		String discountCode = Base64Util.getFromBASE64(cdkey).trim();
		String lockId = WebHelper.addLock(discountCode + memberCode, 60);
		RootResultWeb result = new RootResultWeb();
		if (StringUtils.isEmpty(lockId)) {
			// 优惠码已被兑换过
			result.inErrorMessage(939301311);
			WebHelper.unLock(lockId);
			return result;
		} else if (StringUtils.isNotEmpty(lockId)) {
			if (VersionHelper.checkServerVersion("3.5.62.55")) {
				// 查询优惠码信息
				MDataMap cdkeyMap = DbUp.upTable("oc_coupon_cdkey").oneWhere("multi_account,account_useTime,use_people,activity_code,manage_code",
								"", "", "cdkey", discountCode, "manage_code", manageCode);
				
				MDataMap i = new MDataMap();
				i.put("member_code" , memberCode);
				i.put("manage_code", manageCode);
				i.put("discount_code", discountCode);
				i.put("create_time" , DateHelper.upDate(new Date())); 
				
				if (cdkeyMap == null) {
					i.put("db_manage_code", "");
					i.put("remark", "cdkeyMap == null");
					DbUp.upTable("lc_coupon_error_cdkey").dataInsert(i);
					// 优惠码错误
					result.inErrorMessage(939301310);
					WebHelper.unLock(lockId);
					return result;
				} else if (!manageCode.equals(cdkeyMap.get("manage_code"))) {
					i.put("db_manage_code", cdkeyMap.get("manage_code"));
					i.put("remark", "manage code 不一致！");
					DbUp.upTable("lc_coupon_error_cdkey").dataInsert(i);
					// 优惠码错误
					result.inErrorMessage(939301310);
					WebHelper.unLock(lockId);
					return result;
				}
				
				// 查询活动信息
				String activityCode = cdkeyMap.get("activity_code");
				MDataMap acMap = DbUp.upTable("oc_activity").oneWhere("flag,begin_time,end_time", "", "", "activity_code", activityCode);
				if (acMap == null) {
					// 活动已结束
					result.inErrorMessage(939301301);
					WebHelper.unLock(lockId);
					return result;
				}
				
				String startTime = acMap.get("begin_time");
				String endTime = acMap.get("end_time");
				if (DateUtil.compareTime(DateUtil.getNowTime(), endTime, "yyyy-MM-dd HH:mm:ss") > 0) {
					// 活动已结束
					result.inErrorMessage(939301301);
					WebHelper.unLock(lockId);
					return result;
				}
				
				if (DateUtil.compareTime(startTime, DateUtil.getNowTime(), "yyyy-MM-dd HH:mm:ss") > 0) {
					// 活动未开始
					result.inErrorMessage(939303111);
					WebHelper.unLock(lockId);
					return result;
				}
				
				if ("0".equals(acMap.get("flag"))) {
					// 活动未开始
					result.inErrorMessage(939303111);
					WebHelper.unLock(lockId);
					return result;
				}
				
				// 查询用户的兑换记录
				int chcount = DbUp.upTable("oc_coupon_info").count("member_code", memberCode, "cdkey", discountCode, "activity_code", activityCode);
				if (chcount > 0) {
					result.inErrorMessage(939301311);// 优惠码已被兑换过
					WebHelper.unLock(lockId);
					return result;
				}
				
				// 查询用户的本次活动领券记录
				// 1当在微信商城中分享页领券时调用ApiForActivityCoupon接口,滤重查询oc_coupon_provide表.JobUserCoupon异步发放oc_coupon_provide
				// 表中待发放的优惠券,在发放之前按membercode和coupontype滤重。
				// 2.在app中的优惠券领取h5页中,调ApiForCouponCodeExchange接口,则执行本方法,原本方法按member_code,cdkey,activity_code滤重.
				// 当方法1与方法2同时使用时，同一优惠券活动分为
				// A 如果用户在微信商城中通过h5领券,则插入oc_coupon_provide表后,job异步发放成功。此时，用户再通过手机app中的h5
				// 页领取,执行本方法,而本方法只有上面381-387行的滤重。job异步发的券是没有cdkey的，所以会对一种优惠券活动，造成重发现象。
				// B.若先从app领取，再从微信h5领取。会成功插入oc_coupon_provide表，而异步发券逻辑(JobUserCoupon)会直接去oc_coupon_info
				// 按membercode与activity_code滤重,所以不会出现发重现象。
				// 现对两种渠道发同一活动券,执行A方法两次领券进行fix bug.加入下面的判断. 
				// add by zht.2017-03-13
				
				// 产品要求支持同一个活动的不同兑换码 进行兑换 20200729
				//chcount = DbUp.upTable("oc_coupon_info").count("member_code", memberCode, "activity_code", activityCode);
				//if (chcount > 0) {
				//	result.inErrorMessage(939301303);// 优惠券已领取
				//	WebHelper.unLock(lockId);
				//	return result;
				//}
				
				//查询是否是仅新用户可兑换
				boolean firstOrder= new PlusSupportMember().upFlagFirstOrder(memberCode);
				MDataMap cMap = new MDataMap();
				cMap.put("cdkey", discountCode);
				String sql = "select activity_code,newer_given from oc_activity where activity_code =(select activity_code from oc_coupon_cdkey where cdkey=:cdkey limit 1)";
				Map<String, Object> activityMap = DbUp.upTable("oc_coupon_info").dataSqlOne(sql, cMap);
				if(null != activityMap) {
					String newerGiven = null == activityMap.get("newer_given") ? "" : activityMap.get("newer_given").toString();
					//当前活动配置为仅送新用户且当前用户不是新用户
					if(newerGiven.equals("449746250001") && !firstOrder) {
						result.inErrorMessage(939301320);
						WebHelper.unLock(lockId);
						return result;
					}
				}
				
				String multyAcount = cdkeyMap.get("multi_account");
				// 使用人数
				int pepleNum = Integer.parseInt(cdkeyMap.get("use_people"));
				if ("449746250001".equals(multyAcount)) {
					// 多账户使用
					MDataMap ciMap = new MDataMap();
					ciMap.put("cdkey", discountCode);
					ciMap.put("activityCode", activityCode);
					//String sSql = "SELECT distinct member_code FROM oc_coupon_info where cdkey=:cdkey and activity_code=:activityCode";
					//List<Map<String, Object>> ciList = DbUp.upTable("oc_coupon_info").dataSqlList(sSql, ciMap);
					String sSql = "SELECT COUNT(distinct member_code) num FROM oc_coupon_info where cdkey=:cdkey and activity_code=:activityCode";
					Map<String, Object> map = DbUp.upTable("oc_coupon_info").dataSqlOne(sSql, ciMap);
					int memberNum = NumberUtils.toInt(map.get("num")+"");
					if (memberNum >= pepleNum) {
						// 优惠码已兑换完
						result.inErrorMessage(939301316);
						WebHelper.unLock(lockId);
						return result;
					}
				} else if ("449746250002".equals(multyAcount)) {
					// 单账户使用
					int count = DbUp.upTable("oc_coupon_info").count("cdkey", discountCode, "activity_code", activityCode);
					if (count > 0) {
						// 优惠码已兑换完
						result.inErrorMessage(939301311);
						WebHelper.unLock(lockId);
						return result;
					}
					
					// 因原cdkey为单帐户使用时,是一组不同的随机字符串.出现运营批量泄漏优惠码的情况时,
					// 无法限制恶意兑换.兑券逻辑改为一个用户在一个活动中只能通过单帐户口令兑换一次该活动优惠券
					// Modified by zht 2016-11-26
					MDataMap ciMap = new MDataMap();
					ciMap.put("member_code", memberCode);
					ciMap.put("activity_code", activityCode);
					sql = "SELECT count(*) c FROM oc_coupon_info info,	oc_coupon_cdkey cdkey "
							+ "WHERE info.activity_code = cdkey.activity_code " 
							+ "AND info.cdkey=cdkey.cdkey "
							+ "AND info.activity_code =:activity_code " 
							+ "AND info.member_code =:member_code " 
							+ "AND cdkey.multi_account = '449746250002'";
					Map<String, Object> countMap = DbUp.upTable("oc_coupon_info").dataSqlOne(sql, ciMap);
					if(null != countMap && countMap.size() >0) {
						count = null == countMap.get("c") ? 0 : Integer.parseInt(countMap.get("c").toString());
					}
					if (count > 0) {
						// 优惠码已兑换
						result.inErrorMessage(939301311);
						WebHelper.unLock(lockId);
						return result;
					}
				}
				
				MDataMap whereMap = new MDataMap();
				whereMap.put("activity_code", activityCode);
				whereMap.put("status", "4497469400030002");// 已发布的
				List<MDataMap> couponTypeList = DbUp.upTable("oc_coupon_type").queryAll("money,status,coupon_type_code,surplus_money,manage_code", "",	
						"produce_type=4497471600040001 and (valid_type in ('4497471600080001','4497471600080003','4497471600080004') or (valid_type= 4497471600080002 and now() < end_time)) and activity_code=:activity_code and status=:status",
								whereMap);
				
				// 查询优惠券类型信息,开始发券
				if (couponTypeList != null && !couponTypeList.isEmpty()) {
					for (MDataMap ctMap : couponTypeList) {
						// 使用次数
						int useTimes = Integer.parseInt(cdkeyMap.get("account_useTime"));
						String typeCode = ctMap.get("coupon_type_code");
						CouponUtil cu = new CouponUtil();
						// 发放优惠券
						int k = cu.provideCoupon(memberCode, typeCode, discountCode);
						if (k == 1) {
							// 发券完成修改优惠券剩余金额
							cu.updateCouponType(useTimes, typeCode, memberCode);
						}
					}
				} else {
					// 优惠码已过期
					result.inErrorMessage(939301312);
					WebHelper.unLock(lockId);
					return result;
				}
			}
			WebHelper.unLock(lockId);
		}
		
		//增加兑换成功提示语 -rhb 20181113
		if(result.upFlagTrue()) {
			MDataMap mDataMap = new MDataMap();
			mDataMap.put("cdkey", discountCode);
			mDataMap.put("manage_code", manageCode);
			String create_user = DbUp.upTable("oc_coupon_cdkey").dataGet("create_user", "cdkey=:cdkey and manage_code=:manage_code", mDataMap) + "";
			if("ld".equals(create_user)) {
				result.setResultMessage(bConfig("ordercenter.ld_coupon_success"));
			}else {
				result.setResultMessage(bConfig("ordercenter.hjy_coupon_success"));
			}
		}
		return result;
	}
	

	/**
	 * @Description:根据不同来源发放优惠券(要保存到待发放表,用定时任务扫描发放)
	 * @param activityCode 活动编号
	 * @param mobile 手机号
	 * @param validateFlag 是否校验优惠券类型重复(1:校验，2:不校验)
	 * @author 张海生
	 * @date 2015-6-15 下午3:19:48
	 * @return RootResultWeb
	 * @throws
	 */
	public RootResultWeb distributeCoupons(String activityCode, String mobile, String validateFlag) 
	{
		RootResultWeb result = new RootResultWeb();
		if (StringUtils.isEmpty(activityCode) || StringUtils.isEmpty(mobile)) {
			result.inErrorMessage(939301317);// 活动编号和手机号都不能为空
			return result;
		}
		
		//加锁
		String lockId = WebHelper.addLock("distributeCoupons" + activityCode + mobile, 120);
		if (StringUtils.isNotEmpty(lockId)) {
			MDataMap activtyMap = DbUp.upTable("oc_activity").oneWhere("activity_code,begin_time,end_time,provide_type,"
					+ "provide_num,seller_code","", "", "flag", "1", "activity_code", activityCode);

			if (activtyMap != null) {
				String nowTime = DateUtil.getNowTime();
				String startTime = activtyMap.get("begin_time");
				String endTime = activtyMap.get("end_time");
				if (DateUtil.compareTime(startTime, nowTime, "yyyy-MM-dd HH:mm:ss") > 0) {
					// 活动未开始
					result.inErrorMessage(939301314);
					WebHelper.unLock(lockId);
					return result;
				}
				if (DateUtil.compareTime(nowTime, endTime, "yyyy-MM-dd HH:mm:ss") > 0) {
					// 活动已结束
					result.inErrorMessage(939301301);
					WebHelper.unLock(lockId);
					return result;
				}
				
				//查询已经启用的优惠券类型信息
				//status取值: 4497469400030002/已发布
				//valid_type取值: 4497471600080001/天数      4497471600080002/日期范围
				//surplus_money > 0 剩余金额大于0
				List<MDataMap> mdataList = DbUp.upTable("oc_coupon_type").queryAll("total_money,money,start_time,end_time,activity_code,coupon_type_code",
								null, "status='4497469400030002' and activity_code=:activity_code and (valid_type in ('4497471600080001','4497471600080003','4497471600080004') or (valid_type= 4497471600080002 and now() < end_time))",
								new MDataMap("activity_code", activityCode));
				
				if (mdataList != null && mdataList.size() > 0) {
					for (MDataMap mdata : mdataList) {
						String typeCode = mdata.get("coupon_type_code");
						if ("1".equals(validateFlag)) {
							// 查询是否已经领取
							int count1 = DbUp.upTable("oc_coupon_provide").count("mobile", mobile, "coupon_type_code", typeCode);
							if (count1 > 0) {
								// 已经有此手机号的记录
								result.inErrorMessage(939301303);
								WebHelper.unLock(lockId);
								return result;
							}
						}
						
						//系统发放
						if ("4497471600060002".equals(activtyMap.get("provide_type"))) {
							// 不再校验优惠券是否已经发完，真正生成优惠券的时候再做检查 JobUserCoupon
							//int totalNum = Integer.parseInt(activtyMap.get("provide_num"));  // 发放份数
							//int provideCount = DbUp.upTable("oc_coupon_provide").count("coupon_type_code", typeCode);
							
							//int startZid = NumberUtils.toInt(bConfig("ordercenter.oc_coupon_provide_start_zid"),0);
							//int provideCount = NumberUtils.toInt(DbUp.upTable("oc_coupon_provide").dataGet("count(zid)", "coupon_type_code = :coupon_type_code and zid >= :startZid", new MDataMap("coupon_type_code", typeCode, "startZid", startZid+""))+"");
							//if (provideCount >= totalNum) {
							//	result.inErrorMessage(939301319);// 优惠券已经发放完
							//	WebHelper.unLock(lockId);
							//	return result;
							//}
						}
						
						//插入待发放表
						MDataMap insertMap = new MDataMap();
						insertMap.put("uid", WebHelper.upUuid());
						insertMap.put("mobile", mobile);
						insertMap.put("status", "0");
						insertMap.put("coupon_type_code", typeCode);
						insertMap.put("uq_code", typeCode + "_" + mobile);
						insertMap.put("create_time", DateUtil.getNowTime());
						insertMap.put("manage_code", activtyMap.get("seller_code"));
						insertMap.put("validate_flag", validateFlag);
						DbUp.upTable("oc_coupon_provide").dataInsert(insertMap);
					}
				} else {
					result.inErrorMessage(939301301);// 活动已结束
					WebHelper.unLock(lockId);
					return result;
				}
			} else {
				result.inErrorMessage(939301301);// 活动已结束
			}
			WebHelper.unLock(lockId);
		}
		return result;
	}
	
	/**
	 * 如果为特定来源来的，红包雨未登录插入数据，status先置位1，当兑换之后再重新置位0
	 * @param couponCode
	 * @param mobile
	 * @param validateFlag validateFlag == 999 时，状态置位1
	 * @return
	 */
	public RootResultWeb distributeCouponsByCouponCode(String couponCode, String mobile, String validateFlag) 
	{
		RootResultWeb result = new RootResultWeb();
		if (StringUtils.isEmpty(couponCode) || StringUtils.isEmpty(mobile)) {
			result.inErrorMessage(939301317);// 活动编号和手机号都不能为空
			return result;
		}

		// 加锁
		String lockId = WebHelper.addLock("distributeCoupons" + couponCode + mobile, 120);
		if (StringUtils.isNotEmpty(lockId)) {
			MDataMap activtyMap = DbUp.upTable("oc_coupon_type").oneWhere(
					"total_money,money,start_time,end_time,activity_code,coupon_type_code,manage_code", "",
					"status='4497469400030002' and coupon_type_code=:coupon_type_code and (valid_type in ('4497471600080001','4497471600080003','4497471600080004') or (valid_type= 4497471600080002 and now() < end_time))",
					"coupon_type_code", couponCode);

			if (activtyMap != null) {
				String nowTime = DateUtil.getNowTime();
				String startTime = activtyMap.get("start_time");
				String endTime = activtyMap.get("end_time");
				if (!"".equals(startTime)) {
					if (DateUtil.compareTime(startTime, nowTime, "yyyy-MM-dd HH:mm:ss") > 0) {
						// 活动未开始
						result.inErrorMessage(939301314);
						WebHelper.unLock(lockId);
						return result;
					}
				}
				if (!"".equals(endTime)) {
					if (DateUtil.compareTime(nowTime, endTime, "yyyy-MM-dd HH:mm:ss") > 0) {
						// 活动已结束
						result.inErrorMessage(939301301);
						WebHelper.unLock(lockId);
						return result;
					}
				}

				if ("1".equals(validateFlag)) {
					// 查询是否已经领取
					int count1 = DbUp.upTable("oc_coupon_provide").count("mobile", mobile, "coupon_type_code",
							couponCode);
					if (count1 > 0) {
						// 已经有此手机号的记录
						result.inErrorMessage(939301303);
						WebHelper.unLock(lockId);
						return result;
					}
				}

				// 插入待发放表
				MDataMap insertMap = new MDataMap();
				insertMap.put("uid", WebHelper.upUuid());
				insertMap.put("mobile", mobile);
				if("999".equals(validateFlag)) {
					insertMap.put("status", "1");
				}else {
					insertMap.put("status", "0");
				}
				insertMap.put("coupon_type_code", couponCode);
				insertMap.put("uq_code", couponCode + "_" + mobile);
				insertMap.put("create_time", DateUtil.getNowTime());
				insertMap.put("manage_code", activtyMap.get("manage_code"));
				insertMap.put("validate_flag", validateFlag);
				DbUp.upTable("oc_coupon_provide").dataInsert(insertMap);
			} else {
				result.inErrorMessage(939301301);// 活动已结束
			}
			WebHelper.unLock(lockId);
		}
		return result;
	}
	
	
	
	/**
	 * @Description:处理下单支付送券类型,发放优惠券(要保存到待发放表,用定时任务扫描发放)
	 * @param activityCode 活动编号
	 * @param mobile 手机号
	 * @param bigOrderCode 大订单号
	 * @param validateFlag 是否校验优惠券类型重复(1:校验，2:不校验)
	 * @author zht
	 * @date 2016-8-29 下午3:19:48
	 * @return RootResultWeb
	 * @throws
	 */
	public RootResultWeb distributeCoupons(String activityCode, String mobile, String bigOrderCode, String memberCode, String validateFlag) 
	{
		RootResultWeb result = new RootResultWeb();
		if (StringUtils.isEmpty(activityCode) || StringUtils.isEmpty(mobile) 
				|| StringUtils.isEmpty(bigOrderCode) || StringUtils.isEmpty(memberCode)) {
			result.inErrorMessage(939301317);// 活动编号和手机号都不能为空
			return result;
		}
		
		//加锁
		String lockId = WebHelper.addLock("distributeCoupons" + activityCode + mobile, 120);
		if (StringUtils.isNotEmpty(lockId)) {
			try {
				MDataMap activityMap = DbUp.upTable("oc_activity").oneWhere("activity_code,begin_time,end_time,provide_type,"
						+ "provide_num,seller_code,assign_trigger,assign_line","", "", "flag", "1", "activity_code", activityCode);
	
				if (activityMap != null) {
					String nowTime = DateUtil.getNowTime();
					String startTime = activityMap.get("begin_time");
					String endTime = activityMap.get("end_time");
					if (DateUtil.compareTime(startTime, nowTime, "yyyy-MM-dd HH:mm:ss") > 0) {
						// 活动未开始
						result.inErrorMessage(939301314);
						WebHelper.unLock(lockId);
						return result;
					}
					if (DateUtil.compareTime(nowTime, endTime, "yyyy-MM-dd HH:mm:ss") > 0) {
						// 活动已结束
						result.inErrorMessage(939301301);
						WebHelper.unLock(lockId);
						return result;
					}
					
					String assignTrigger = activityMap.get("assign_trigger");
					if(StringUtils.isEmpty(assignTrigger)) {
						result.inErrorMessage(939301301);
						WebHelper.unLock(lockId);
						return result;
					}
					
					if("4497471600340001".equals(assignTrigger)) {
						//下单满X元
						String sql = "select payed_money from oc_orderinfo_upper_payment where big_order_code='" + bigOrderCode + "'";
						Map<String, Object> payMap = DbUp.upTable("oc_orderinfo_upper_payment").dataSqlOne(sql, new MDataMap());
						if(null != payMap && payMap.size() > 0) {
							double orderMoney =payMap.get("payed_money") == null ? 0.0 : Double.parseDouble(payMap.get("payed_money").toString());
							double lineMoney = StringUtils.isEmpty(activityMap.get("assign_line")) ? 0.0 : Double.parseDouble(activityMap.get("assign_line").toString());
							if(orderMoney >= lineMoney) {
								couponProvider(mobile, activityCode, validateFlag, activityMap, bigOrderCode, result);
							}
						}
						
						
					} else if("4497471600340002".equals(assignTrigger)) {
						//首次下单
						PlusSupportMember psm = new PlusSupportMember();
						if(psm.upFlagFirstOrderAfterBooking(memberCode)) {
							couponProvider(mobile, activityCode, validateFlag, activityMap, bigOrderCode, result);
						}
					}
				} else {
					result.inErrorMessage(939301301);// 活动已结束
				}
			} catch(Exception e) {
				e.printStackTrace();
			} finally {
				WebHelper.unLock(lockId);
			}
		}
		return result;
	}
	
	public RootResultWeb distributeCouponsForYQ(String activityCode, String mobile, String validateFlag) 
	{
		RootResultWeb result = new RootResultWeb();
		if (StringUtils.isEmpty(activityCode) || StringUtils.isEmpty(mobile)) {
			result.inErrorMessage(939301317);// 活动编号和手机号都不能为空
			return result;
		}
		
		//加锁
		String lockId = WebHelper.addLock("distributeCoupons" + activityCode + mobile, 120);
		if (StringUtils.isNotEmpty(lockId)) {
			MDataMap activtyMap = DbUp.upTable("oc_activity").oneWhere("activity_code,begin_time,end_time,provide_type,"
					+ "provide_num,seller_code","", "", "flag", "1", "activity_code", activityCode);

			if (activtyMap != null) {
				String nowTime = DateUtil.getNowTime();
				String startTime = activtyMap.get("begin_time");
				String endTime = activtyMap.get("end_time");
				if (DateUtil.compareTime(startTime, nowTime, "yyyy-MM-dd HH:mm:ss") > 0) {
					// 活动未开始
					result.inErrorMessage(939301314);
					WebHelper.unLock(lockId);
					return result;
				}
				if (DateUtil.compareTime(nowTime, endTime, "yyyy-MM-dd HH:mm:ss") > 0) {
					// 活动已结束
					result.inErrorMessage(939301301);
					WebHelper.unLock(lockId);
					return result;
				}
				
				//查询已经启用的优惠券类型信息
				//status取值: 4497469400030002/已发布
				//valid_type取值: 4497471600080001/天数 ,这个就不判断过期情况了？     4497471600080002/日期范围
				List<MDataMap> mdataList = DbUp.upTable("oc_coupon_type").queryAll("total_money,money,start_time,end_time,activity_code,coupon_type_code",
								null, "status='4497469400030002' and activity_code=:activity_code and (valid_type in ('4497471600080001','4497471600080003','4497471600080004') or (valid_type= 4497471600080002 and now() < end_time))",
								new MDataMap("activity_code", activityCode));
				
				if (mdataList != null && mdataList.size() > 0) {
					for (MDataMap mdata : mdataList) {
						String typeCode = mdata.get("coupon_type_code");
						if ("1".equals(validateFlag)) {
							// 查询是否已经领取
							int count1 = DbUp.upTable("oc_coupon_provide").count("mobile", mobile, "coupon_type_code", typeCode);
							if (count1 > 0) {
								// 已经有此手机号的记录
								result.inErrorMessage(939301303);
								WebHelper.unLock(lockId);
								return result;
							}
						}
						
						//插入待发放表
						MDataMap insertMap = new MDataMap();
						insertMap.put("uid", WebHelper.upUuid());
						insertMap.put("mobile", mobile);
						insertMap.put("status", "0");
						insertMap.put("coupon_type_code", typeCode);
						//添加邀请人发券后缀
						insertMap.put("uq_code", typeCode + "_" + mobile+CouponConst.postfix__coupon);
						insertMap.put("create_time", DateUtil.getNowTime());
						insertMap.put("manage_code", activtyMap.get("seller_code"));
						insertMap.put("validate_flag", validateFlag);
						DbUp.upTable("oc_coupon_provide").dataInsert(insertMap);
					}
				} else {
					result.inErrorMessage(939301301);// 活动已结束
					WebHelper.unLock(lockId);
					return result;
				}
			} else {
				result.inErrorMessage(939301301);// 活动已结束
			}
			WebHelper.unLock(lockId);
		}
		return result;
	}
	private void couponProvider(String mobile, String activityCode, String validateFlag, MDataMap activityMap, 
			String bigOrderCode, RootResultWeb result) {
		//查询已经启用的优惠券类型信息
		//status取值: 4497469400030002/已发布
		//valid_type取值: 4497471600080001/天数      4497471600080002/日期范围
		List<MDataMap> mdataList = DbUp.upTable("oc_coupon_type").queryAll("total_money,money,start_time,end_time,activity_code,coupon_type_code",
						null, "status='4497469400030002' and activity_code=:activity_code and (valid_type in ('4497471600080001','4497471600080003','4497471600080004') or (valid_type= 4497471600080002 and now() < end_time))",
						new MDataMap("activity_code", activityCode));
		
		if (mdataList != null && mdataList.size() > 0) {
			for (MDataMap mdata : mdataList) {
				String typeCode = mdata.get("coupon_type_code");
				if ("1".equals(validateFlag)) {
					// 查询是否已经领取
					int count1 = DbUp.upTable("oc_coupon_provide").count("mobile", mobile, "coupon_type_code", typeCode);
					if (count1 > 0) {
						// 已经有此手机号的记录
						result.inErrorMessage(939301303);
						return ;
					}
				}
				
				//插入待发放表
				MDataMap insertMap = new MDataMap();
				insertMap.put("uid", WebHelper.upUuid());
				insertMap.put("mobile", mobile);
				insertMap.put("status", "0");
				insertMap.put("coupon_type_code", typeCode);
				insertMap.put("uq_code", typeCode + "_" + mobile);
				insertMap.put("create_time", DateUtil.getNowTime());
				insertMap.put("manage_code", activityMap.get("seller_code"));
				insertMap.put("validate_flag", validateFlag);
				//表示要发放的优惠券是未激活的状态
				insertMap.put("blocked", "1");
				insertMap.put("big_order_code", bigOrderCode);
				DbUp.upTable("oc_coupon_provide").dataInsert(insertMap);
			}
		} else {
			result.inErrorMessage(939301301);// 活动已结束
		}
	}

	/**
	 * 获取优惠券类型限制
	 * 
	 * @return
	 */
	public String getCouponTypeLimit(String couponTypeCode, String sellerCode) {
		CouponTypeLimitDTO result = new CouponTypeLimitDTO();
		if (StringUtils.isEmpty(couponTypeCode)) {
			return "";
		}
		List<BrandBaseInfo> brandInfoList = new ArrayList<BrandBaseInfo>();
		List<ProductBaseInfo> productInfoList = new ArrayList<ProductBaseInfo>();
		List<CategoryBaseInfo> categoryInfoList = new ArrayList<CategoryBaseInfo>();
		List<String> channelCodeList = new ArrayList<String>();
		List<String> allowedActivityTypes = new ArrayList<String>();

		String brandCodes = "";
		String productCodes = "";
		String categoryCodes = "";
		String channelCodes = "";
		String sellerLimit = "";
		String paymentTypeLimit = "";
		String allowedActivityTypeLimit = "";
		MDataMap map = DbUp.upTable("oc_coupon_type_limit").oneWhere("brand_codes,product_codes,category_codes,channel_codes,seller_limit,allowed_activity_type,payment_type", "", "", "coupon_type_code", couponTypeCode);
		if (map == null || map.isEmpty()) {
			return "";
		}
		
		brandCodes = map.get("brand_codes");
		productCodes = map.get("product_codes");
		categoryCodes = map.get("category_codes");
		channelCodes = map.get("channel_codes");
		sellerLimit = map.get("seller_limit");
		paymentTypeLimit = map.get("payment_type");
		allowedActivityTypeLimit = map.get("allowed_activity_type");

		// 品牌基本信息
		if (StringUtils.isNotEmpty(brandCodes)) {
			String sWhere = "brand_code in ('" + brandCodes.replace(",", "','")
					+ "')";
			String sFields = "brand_code,brand_name,brand_name_en,brand_pic";
			List<MDataMap> productMap = DbUp.upTable("pc_brandinfo").queryAll(sFields, "", sWhere, null);
			for (MDataMap mDataMap : productMap) {
				BrandBaseInfo brandInfo = new BrandBaseInfo();
				brandInfo.setBrandCode(mDataMap.get("brand_code"));
				brandInfo.setBrandZNName(mDataMap.get("brand_name"));
				brandInfo.setBrandUNName(mDataMap.get("brand_name_en"));
				brandInfo.setBrandPic(mDataMap.get("brand_pic"));
				brandInfoList.add(brandInfo);
			}
		}
		// 商品基本信息
		if (StringUtils.isNotEmpty(productCodes)) {
			String sWhere = "product_code in ('" + productCodes.replace(",", "','") + "')";
			String sFields = "product_code,product_name";
			List<MDataMap> productMap = DbUp.upTable("pc_productinfo").queryAll(sFields, "", sWhere, null);
			for (MDataMap mDataMap : productMap) {
				ProductBaseInfo productInfo = new ProductBaseInfo();
				productInfo.setProductCode(mDataMap.get("product_code"));
				productInfo.setProductName(mDataMap.get("product_name"));
				productInfoList.add(productInfo);
			}
		}
		// 分类基本信息
		if (StringUtils.isNotEmpty(categoryCodes)) {
			Map<String, MDataMap> categoryObjMap = new HashMap<String, MDataMap>();
			List<MDataMap> listCategory = DbUp.upTable("uc_sellercategory").queryAll("", "", " seller_code=:seller_code ", new MDataMap("seller_code", sellerCode));
			for (MDataMap mDataMap : listCategory) {
				categoryObjMap.put(mDataMap.get("category_code"), mDataMap);
			}
			String sFields = "category_code,category_name,parent_code,sort";
			String sWhere = " category_code in ('" + categoryCodes.replace(",", "','") + "') and seller_code='" + sellerCode + "' ";
			List<MDataMap> categorytMap = DbUp.upTable("uc_sellercategory").query(sFields, "", sWhere, null, -1, -1);
			for (MDataMap mDataMap : categorytMap) {
				MDataMap categoryMap = categoryObjMap.get(mDataMap.get("category_code"));
				String name = "";
				if ("4".equals(categoryMap.get("level"))) {
					// 上一级别的分类信息
					MDataMap parentCategoryMap = categoryObjMap.get(categoryMap.get("parent_code")); 
					// 第一级别的分类信息
					MDataMap superParentCategoryMap = categoryObjMap.get(parentCategoryMap.get("parent_code")); 
					name = superParentCategoryMap.get("category_name") + "->"
							+ parentCategoryMap.get("category_name") + "->"
							+ categoryMap.get("category_name");
				} else if ("3".equals(categoryMap.get("level"))) {
					// 上一级别的分类信息
					MDataMap parentCategoryMap = categoryObjMap.get(categoryMap.get("parent_code")); 
					// 第一级别的分类信息
					name = parentCategoryMap.get("category_name") + "->"
							+ categoryMap.get("category_name");
				} else {
					name = categoryMap.get("category_name");
				}
				CategoryBaseInfo categoryInfo = new CategoryBaseInfo();
				categoryInfo.setCategoryCode(mDataMap.get("category_code"));
				categoryInfo.setCategoryName(name);
				categoryInfo.setParentCode(mDataMap.get("parent_code"));
				categoryInfo.setSort(mDataMap.get("sort"));
				categoryInfoList.add(categoryInfo);
			}
		}
		// 优惠券类型限制基本信息
		MDataMap couponTypeLimitMap = DbUp.upTable("oc_coupon_type_limit").one("coupon_type_code", couponTypeCode);
		CouponTypeLimitBaseInfo limitBaseInfo = new CouponTypeLimitBaseInfo();
		if (null != couponTypeLimitMap && !couponTypeLimitMap.isEmpty()) {
			limitBaseInfo.setZid(couponTypeLimitMap.get("zid"));
			limitBaseInfo.setUid(couponTypeLimitMap.get("uid"));
			limitBaseInfo.setCouponTypeCode(couponTypeLimitMap.get("coupon_type_code"));
			limitBaseInfo.setActivityCode(couponTypeLimitMap.get("activity_code"));
			limitBaseInfo.setBrandLimit(couponTypeLimitMap.get("brand_limit"));
			limitBaseInfo.setProductLimit(couponTypeLimitMap.get("product_limit"));
			limitBaseInfo.setCategoryLimit(couponTypeLimitMap.get("category_limit"));
			limitBaseInfo.setChannelLimit(couponTypeLimitMap.get("channel_limit"));
			limitBaseInfo.setActivityLimit(couponTypeLimitMap.get("activity_limit"));
			limitBaseInfo.setExceptBrand(couponTypeLimitMap.get("except_brand"));
			limitBaseInfo.setExceptCategory(couponTypeLimitMap.get("except_category"));
			limitBaseInfo.setExceptProduct(couponTypeLimitMap.get("except_product"));
			limitBaseInfo.setBrandCodes(couponTypeLimitMap.get("brand_codes"));
			limitBaseInfo.setProductCodes(couponTypeLimitMap.get("product_codes"));
			limitBaseInfo.setCategoryCodes(couponTypeLimitMap.get("category_codes"));
			limitBaseInfo.setChannelCodes(couponTypeLimitMap.get("channel_codes"));
			limitBaseInfo.setAllowedActivityTypeLimit(couponTypeLimitMap.get("allowed_activity_type"));
		}
		
		if (StringUtils.isNotEmpty(channelCodes)) {
			for (String string : channelCodes.split(",")) {
				channelCodeList.add(string);
			}
		}
		
		if (StringUtils.isNotEmpty(allowedActivityTypeLimit)) {
			for (String string : allowedActivityTypeLimit.split(",")) {
				allowedActivityTypes.add(string);
			}
		}

		result.setCouponTypeLimit(limitBaseInfo);
		result.setBrandInfoList(brandInfoList);
		result.setProductInfoList(productInfoList);
		result.setCategoryInfoList(categoryInfoList);
		result.setChannelCodeList(channelCodeList);
		result.setSellerLimit(sellerLimit);
		result.setPaymentTypeLimit(paymentTypeLimit);
		result.setAllowedActivityTypeList(allowedActivityTypes);

		return JSON.toJSONString(result);
	}

	/**
	 * 优惠券类型限制中分类限制选择调用
	 * 
	 * @author ligj
	 * @param categoryCodes
	 * @param couponTypeCode
	 *            当categoryCodes为空时此字段生效。查询出此优惠券类型下的分类限制
	 * @param seller_code
	 * @return
	 */
	public MDataMap getCateGoryByCouponTypeLimit(String categoryCodes,
			String couponTypeCode, String seller_code) {
		MDataMap ret = new MDataMap();
		Map<String, MDataMap> categoryObjMap = new HashMap<String, MDataMap>();

		if (StringUtils.isEmpty(categoryCodes) && StringUtils.isEmpty(couponTypeCode)) {
			return ret;
		}
		List<MDataMap> listCategory = DbUp.upTable("uc_sellercategory").queryAll("", "", " seller_code=:seller_code ", new MDataMap("seller_code", seller_code));
		for (MDataMap mDataMap : listCategory) {
			categoryObjMap.put(mDataMap.get("category_code"), mDataMap);
		}
		
		if (StringUtils.isEmpty(categoryCodes)) {
			MDataMap map = DbUp.upTable("oc_coupon_type_limit").oneWhere("category_codes", "", "", "coupon_type_code", couponTypeCode);
			if (null == map || map.isEmpty()) {
				return ret;
			}
			categoryCodes = map.get("category_codes");
		}
		
		for (String categoryCode : categoryCodes.split(",")) {
			MDataMap categoryMap = categoryObjMap.get(categoryCode);
			if (categoryMap != null && !categoryMap.isEmpty()) {
				// 防止变态的修改数据库
				String name = "";
				if ("3".equals(categoryMap.get("level"))) {
					// 上一级别的分类信息
					MDataMap parentCategoryMap = categoryObjMap.get(categoryMap.get("parent_code")); 
					name = parentCategoryMap.get("category_name") + "->" + categoryMap.get("category_name");
				} else if ("4".equals(categoryMap.get("level"))) {
					// 上一级别的分类信息
					MDataMap parentCategoryMap = categoryObjMap.get(categoryMap.get("parent_code")); 
					// 第一级别的分类信息
					MDataMap superParentCategoryMap = categoryObjMap.get(parentCategoryMap.get("parent_code")); 
					name = superParentCategoryMap.get("category_name") + "->"
							+ parentCategoryMap.get("category_name") + "->"
							+ categoryMap.get("category_name");
				} else {
					name = categoryMap.get("category_name");
				}
				ret.put(categoryCode, name);
			}
		}
		return ret;
	}

	/**
	 * 获取优惠券类型限制基本信息
	 * 
	 * @return
	 */
	public CouponTypeLimitBaseInfo getCouponTypeLimitBaseInfo(String couponTypeCode) {
		// 优惠券类型限制基本信息
		MDataMap couponTypeLimitMap = DbUp.upTable("oc_coupon_type_limit").one("coupon_type_code", couponTypeCode);
		CouponTypeLimitBaseInfo limitBaseInfo = new CouponTypeLimitBaseInfo();
		if (null != couponTypeLimitMap && !couponTypeLimitMap.isEmpty()) {
			limitBaseInfo.setZid(couponTypeLimitMap.get("zid"));
			limitBaseInfo.setUid(couponTypeLimitMap.get("uid"));
			limitBaseInfo.setCouponTypeCode(couponTypeLimitMap.get("coupon_type_code"));
			limitBaseInfo.setActivityCode(couponTypeLimitMap.get("activity_code"));
			limitBaseInfo.setBrandLimit(couponTypeLimitMap.get("brand_limit"));
			limitBaseInfo.setProductLimit(couponTypeLimitMap.get("product_limit"));
			limitBaseInfo.setCategoryLimit(couponTypeLimitMap.get("category_limit"));
			limitBaseInfo.setChannelLimit(couponTypeLimitMap.get("channel_limit"));
			limitBaseInfo.setActivityLimit(couponTypeLimitMap.get("activity_limit"));
			limitBaseInfo.setExceptBrand(couponTypeLimitMap.get("except_brand"));
			limitBaseInfo.setExceptCategory(couponTypeLimitMap.get("except_category"));
			limitBaseInfo.setExceptProduct(couponTypeLimitMap.get("except_product"));
			limitBaseInfo.setExceptChannel(couponTypeLimitMap.get("except_channel"));
			limitBaseInfo.setBrandCodes(couponTypeLimitMap.get("brand_codes"));
			limitBaseInfo.setProductCodes(couponTypeLimitMap.get("product_codes"));
			limitBaseInfo.setCategoryCodes(couponTypeLimitMap.get("category_codes"));
			limitBaseInfo.setChannelCodes(couponTypeLimitMap.get("channel_codes"));
			limitBaseInfo.setSellerLimit(couponTypeLimitMap.get("seller_limit"));
			limitBaseInfo.setPaymentTypeLimit(couponTypeLimitMap.get("payment_type"));
			limitBaseInfo.setAllowedActivityTypeLimit(couponTypeLimitMap.get("allowed_activity_type"));
		}

		return limitBaseInfo;
	}

	/**
	 * @Description:获取活动下的优惠券个数
	 * @param activityCode
	 * @author 张海生
	 * @date 2015-6-10 上午10:22:57
	 * @return int
	 * @throws
	 */
	public int getCouponTypeNum(String activityCode) {
		return DbUp.upTable("oc_coupon_type").count("activity_code", activityCode);
	}

	/**
	 * 是否可用优惠劵列表，根据版本号过滤
	 * 
	 * @param memberCode
	 *            用户编号
	 * @param useShouldPay
	 *            应付金额
	 * @param goodsList
	 *            商品一些信息
	 * 
	 * @author ligj
	 */
	public Map<String, List<CouponInfo>> couponList(String memberCode,
			String useShouldPay, List<GoodsInfoForAdd> goodsList,
			String manageCode, String channelCodeInput,String paymentType) 
	{
		return couponList(memberCode,useShouldPay,goodsList,manageCode,channelCodeInput,"",paymentType);
	}
	
	/**
	 * 是否可用优惠劵列表
	 * 
	 * @param memberCode
	 *            用户编号
	 * @param useShouldPay
	 *            应付金额
	 * @param goodsList
	 *            商品一些信息
	 * 
	 * @author ligj
	 */
	public Map<String, List<CouponInfo>> couponList(String memberCode,
			String useShouldPay, List<GoodsInfoForAdd> goodsList,
			String manageCode, String channelCodeInput,String version,String paymentType) 
	{
		if("449716200001".equals(paymentType)){//在线支付
			paymentType = "449748290001";
		}else if("449716200002".equals(paymentType)){//货到付款
			paymentType = "449748290002";
		}
		Map<String, Integer> productCodeMap = new HashMap<String, Integer>();
		// for (GoodsInfoForAdd goodInfo : goodsList) {
		// productCodeMap.put(goodInfo.getProduct_code(), 1);
		// }
		Map<String, List<CouponInfo>> map = new HashMap<String, List<CouponInfo>>();
		// 可用优惠劵列表
		List<CouponInfo> couponInfoList = new ArrayList<CouponInfo>();
		// 不可用优惠劵列表
		List<CouponInfo> disableCouponList = new ArrayList<CouponInfo>();

		ProductService ps = new ProductService();
		Map<String, GoodsInfoForAdd> skuGoodsMap = new HashMap<String, GoodsInfoForAdd>(); // key:skuCode,Value:productCode
		ActiveForproduct activeInfo = new ActiveForproduct();
		List<ActiveReq> activeRequests = new ArrayList<ActiveReq>();
		ActiveResult activeResult = new ActiveResult();
		if (goodsList == null || goodsList.isEmpty()) {
			map.put("available", couponInfoList);
			map.put("disable", disableCouponList);
			return map;
		}
		
//		map.put("available", couponInfoList);
//		map.put("disable", disableCouponList);
//		return map;
		
		for (GoodsInfoForAdd goodInfo : goodsList) {

			ActiveReq activeReq = new ActiveReq();
			activeReq.setIsPurchase(goodInfo.getIsPurchase());
			activeReq.setBuyer_code(memberCode);
			activeReq.setProduct_code(goodInfo.getProduct_code());
			activeReq.setSku_code(goodInfo.getSku_code());
			activeReq.setSku_num(goodInfo.getSku_num());
			activeRequests.add(activeReq);

			/**
			 * IC编号替换为正常skuCode与productCode
			 */
			goodInfo.setProduct_code(ps.getProductCodeForICcode(goodInfo.getProduct_code()));
			goodInfo.setSku_code(ps.getSkuCodeForICcode(goodInfo.getSku_code()));
			// 不知道是谁加的。不过看着没什么用处所以就给注释掉了。--li
			// PlusModelSkuInfo info = new
			// PlusSupportProduct().upSkuInfoBySkuCode(goodInfo.getSku_code(),memberCode);
			// if(info.getBuyStatus()==1&&StringUtility.isNotNull(info.getEventCode())){
			// skuGoodsMap.put(info.getSkuCode(), goodInfo);
			// }else {
			// skuGoodsMap.put(info.getSkuCode(), goodInfo);
			// }
			skuGoodsMap.put(goodInfo.getSku_code(), goodInfo);
			productCodeMap.put(goodInfo.getProduct_code(), 1);

		}
		Map<String, ActiveReturn> activeInfoMap = activeInfo.activeGallery(activeRequests, activeResult); // 判断sku参加的各种活动

		String productCodes = StringUtils.join(productCodeMap.keySet(), "','");
		String member_code = memberCode;
		String couponLimit = bConfig("familyhas.coupon_limit");
		BigDecimal shouldPay = new BigDecimal(useShouldPay);
		MDataMap mWhereMap = new MDataMap();
		mWhereMap.put("member_code", member_code);
		mWhereMap.put("manage_code", manageCode);
		// 查不限制金额时全部可用优惠劵
//		List<MDataMap> listMaps = DbUp
//				.upTable("oc_coupon_info")
//				.queryAll(
//						"coupon_code,surplus_money,status,end_time,limit_money,start_time,end_time,blocked,big_order_code,coupon_type_code",
//						"end_time asc",
//						"member_code=:member_code and status=0 and surplus_money>0 and end_time>now() and start_time<now() and manage_code=:manage_code",
//						mWhereMap);
		
		String moneyTypeWhere = "";
		// 小于5.1.4版本仅支持金额卷
		if(StringUtils.isNotBlank(version) && AppVersionUtils.compareTo(version, "5.1.4") < 0){
			moneyTypeWhere = "and ct.money_type = '449748120001' ";
		}
		
		String sql = "select ci.coupon_code,ci.initial_money,ci.surplus_money,ci.status,ci.end_time,ci.limit_money,ci.start_time,ci.end_time,ci.blocked,ci.big_order_code,ci.coupon_type_code,ct.money_type,ci.activity_code,ct.creater,ci.out_coupon_code "
				+ "from oc_coupon_info ci,oc_coupon_type ct where ci.coupon_type_code = ct.coupon_type_code and ci.member_code=:member_code and ci.status=0 and IFNULL(ci.blocked,0) = 0 and ci.surplus_money>0 and ci.end_time>now() and ci.start_time<now() and ci.manage_code=:manage_code "
				+ moneyTypeWhere
				+ " order by ct.money_type asc,ci.end_time asc,ci.initial_money desc,ci.limit_money asc,ci.coupon_code asc";
		List<Map<String,Object>> listMaps = DbUp.upTable("oc_coupon_info").upTemplate().queryForList(sql, mWhereMap);
				
		String couponTypeCodes = ""; // 结果集中所有优惠券类型编号
		Map<String, Map<String, Object>> couponTypeCodesMap = new HashMap<String, Map<String, Object>>();
		Map<String, String> productCode_brandCodeMap = new HashMap<String, String>(); // 订单中的商品与品牌对应map
		Map<String, String> productCode_categoryCodeMap = new HashMap<String, String>(); // 订单中的商品与分类对应map(如若为多个分类，分类中间用英文逗号隔开)
		if (VersionHelper.checkServerVersion("3.5.62.55")) {
			// 优惠券类型对象map信息
			for (Map<String,Object> mDataMap : listMaps) {
				couponTypeCodesMap.put(mDataMap.get("coupon_type_code").toString(), new HashMap<String, Object>());
			}
			
			couponTypeCodes = StringUtils.join(couponTypeCodesMap.keySet(), "','");
			if (StringUtils.isNotEmpty(couponTypeCodes)) {
				String couponCodeTypeSql = "select ot.limit_scope,ot.limit_explain,ot.coupon_type_code,ot.limit_condition,otl.brand_limit,otl.product_limit,otl.category_limit,otl.channel_limit,otl.activity_limit,otl.allowed_activity_type,otl.payment_type,otl.except_brand,otl.except_product,otl.except_category,otl.except_channel,otl.brand_codes,otl.product_codes,otl.category_codes,otl.channel_codes,otl.seller_limit from oc_coupon_type ot "
						+ "LEFT JOIN oc_coupon_type_limit otl ON ot.coupon_type_code = otl.coupon_type_code "
						+ "where ot.coupon_type_code in ('"
						+ couponTypeCodes
						+ "') ";

				List<Map<String, Object>> couponTypeMapList = DbUp.upTable(
						"oc_coupon_type").dataSqlList(couponCodeTypeSql, null);
				for (Map<String, Object> couponTypeMap : couponTypeMapList) {
					couponTypeCodesMap.put(couponTypeMap.get("coupon_type_code").toString(), couponTypeMap);
				}
			}

			// 查询出传入sku对应的商品编号,品牌,分类
			List<MDataMap> brandMapList = DbUp.upTable("pc_productinfo").queryAll("product_code,brand_code", "", "product_code in ('" + productCodes + "')", null);
			for (MDataMap mDataMap : brandMapList) {
				productCode_brandCodeMap.put(mDataMap.get("product_code"), mDataMap.get("brand_code"));
			}
			List<MDataMap> categoryMapList = DbUp.upTable("uc_sellercategory_product_relation").queryAll("category_code,product_code", "",
					"product_code in ('" + productCodes + "')", null);
			
			for (MDataMap mDataMap : categoryMapList) {
				String productCode = mDataMap.get("product_code");
				String categoryCode = mDataMap.get("category_code");
				if (StringUtils.isNotEmpty(productCode_categoryCodeMap.get(productCode))) {
					categoryCode += ("," + productCode_categoryCodeMap.get(productCode));
				}
				productCode_categoryCodeMap.put(productCode, categoryCode);
			}
		}

		// Map<String,CouponInfo> ableUseCoupon = new HashMap<String,
		// CouponInfo>(); //可以使用的优惠券类型，这个map中的优惠券类型无需再次进行判断，可以直接添加到可以使用的优惠券列表中
		// Map<String,CouponInfo> unAbleUseCoupon = new HashMap<String,
		// CouponInfo>(); //不可以使用的优惠券类型，这个map中的优惠券类型无需再次进行判断，可以直接添加到不可以使用的优惠券列表中
		double deadlineDay = Double.parseDouble(StringUtils.isEmpty(bConfig("ordercenter.COUPON_DEADLINE_DAY")) ? "2.0" : bConfig("ordercenter.COUPON_DEADLINE_DAY"));
		for (Map<String,Object> maps : listMaps) {
			// String couponTypeCode = maps.get("coupon_type_code");
			// if (ableUseCoupon.containsKey(couponTypeCode)) {
			// couponInfoList.add(ableUseCoupon.get(couponTypeCode));
			// continue;
			// }else if (unAbleUseCoupon.containsKey(couponTypeCode)) {
			// disableCouponList.add(unAbleUseCoupon.get(couponTypeCode));
			// continue;
			// }
			
			//ld的优惠券 没有外部优惠券编号、版本低于5.2.8、活动未发布、活动类型未发布 任一情况 则不能使用此优惠券 -rhb 20181113
			String creater = maps.get("creater") + "";
			String coupon_type_code = maps.get("coupon_type_code") + "";
			if(StringUtils.isNotBlank(creater) && StringUtils.isNotBlank(coupon_type_code) && "ld".equals(creater)) {
				if(StringUtils.isNotBlank(version) && AppVersionUtils.compareTo(version, "5.2.8") < 0) {
					continue;
				}
				if(StringUtils.isBlank(maps.get("out_coupon_code")+"")) {
					continue;
				}
				String sSql = "select ot.status,oa.flag from ordercenter.oc_coupon_type ot,ordercenter.oc_activity oa where ot.activity_code=oa.activity_code and ot.coupon_type_code=:coupon_type_code";
				Map<String, Object> qResult = DbUp.upTable("oc_coupon_type").dataSqlOne(sSql, new MDataMap("coupon_type_code", coupon_type_code));
				if(!"1".equals(qResult.get("flag")+"") || !"4497469400030002".equals(qResult.get("status"))) {
					continue;
				}
			}
			CouponInfo couponInfo = new CouponInfo();
			boolean flag = false; // 标志优惠券是否可用
			// 增加限平台字段，加这儿是最好不过了 -- 2015-09-18 14:31:22
			if (VersionHelper.checkServerVersion("3.5.72.55")) {
				Map<String, Object> couponTypeLimitMap01 = couponTypeCodesMap.get(maps.get("coupon_type_code"));
				String limitCondition01 = (null == couponTypeLimitMap01.get("limit_condition") ? "" : couponTypeLimitMap01.get("limit_condition").toString());
				String channel_limit01 = (null == couponTypeLimitMap01.get("channel_limit") ? "" : couponTypeLimitMap01.get("channel_limit").toString());
				if ("4497471600070002".equals(limitCondition01)	&& "4497471600070002".equals(channel_limit01)) {
					couponInfo.setChannelLimit("1");
				}
			}
			
			// 优惠券的金额类型： 449748120001: 金额卷，449748120002: 折扣券
			couponInfo.setMoneyType(maps.get("money_type")+"");
			
			// 限制金额时可用优惠劵
			String initial_money = (null == maps.get("initial_money") ? "" : maps.get("initial_money").toString());
			String limit_money = (null == maps.get("limit_money") ? "" : maps.get("limit_money").toString());
			String surplus_money = (null == maps.get("surplus_money") ? "" : maps.get("surplus_money").toString());
			String coupon_code = (null == maps.get("coupon_code") ? "" : maps.get("coupon_code").toString());
			String status = (null == maps.get("status") ? "0" : maps.get("status").toString());
			String start_time = (null == maps.get("start_time") ? "" : maps.get("start_time").toString());
			String end_time = (null == maps.get("end_time") ? "" : maps.get("end_time").toString());
			String activity_code = (null == maps.get("activity_code") ? "" : maps.get("activity_code").toString());
			
			//542版本返回优惠券类型编号 -rhb 20190423
			couponInfo.setCouponTypeCode(coupon_type_code);
			
			// 初始金额，页面显示使用
			couponInfo.setInitialMoney(new BigDecimal(initial_money));
			
			if (new BigDecimal(limit_money).compareTo(shouldPay) <= 0) {
				couponInfo.setCouponCode(coupon_code);
				couponInfo.setSurplusMoney(new BigDecimal(surplus_money));
				couponInfo.setStatus(Integer.parseInt(status));
				couponInfo.setEndTime(end_time);
				couponInfo.setStartTime(start_time);
				couponInfo.setLimitMoney(new BigDecimal(limit_money));
				couponInfo.setUseLimit(couponLimit);
				couponInfo.setActivityCode(activity_code);
				flag = true;
				// 限制金额时不可用优惠劵
			} else if (new BigDecimal(limit_money).compareTo(shouldPay) == 1) {
				couponInfo.setCouponCode(coupon_code);
				couponInfo.setSurplusMoney(new BigDecimal(surplus_money));
				couponInfo.setStatus(Integer.parseInt(status));
				couponInfo.setEndTime(end_time);
				couponInfo.setStartTime(start_time);
				couponInfo.setLimitMoney(new BigDecimal(limit_money));
				couponInfo.setUseLimit(couponLimit);
				couponInfo.setActivityCode(activity_code);
				flag = false;
			}
			
			if (VersionHelper.checkServerVersion("3.5.62.55")) {
				Map<String, Object> couponTypeLimitMap = couponTypeCodesMap.get(maps.get("coupon_type_code"));
				String limitCondition = (null == couponTypeLimitMap.get("limit_condition") ? "" : couponTypeLimitMap.get("limit_condition").toString());
				if (VersionHelper.checkServerVersion("3.5.72.55")) {
					if (couponTypeLimitMap.get("limit_scope") == null || StringUtils.isEmpty(couponTypeLimitMap.get("limit_scope").toString())) {
						couponInfo.setUseLimit(couponLimit);
					} else {
						couponInfo.setUseLimit(couponTypeLimitMap.get("limit_scope").toString());
					}
					couponInfo.setLimitExplain(null == couponTypeLimitMap.get("limit_explain") ? "" : couponTypeLimitMap.get("limit_explain").toString());
				}
				
				// 指定限制条件时
				List<String> productCodeTmp = new ArrayList<String>();
				// 品牌限制
				String brand_limit = (null == couponTypeLimitMap.get("brand_limit") ? "" : couponTypeLimitMap.get("brand_limit").toString());
				// 商品限制
				String product_limit = (null == couponTypeLimitMap.get("product_limit") ? "" : couponTypeLimitMap.get("product_limit").toString());
				// 分类限制
				String category_limit = (null == couponTypeLimitMap.get("category_limit") ? "" : couponTypeLimitMap.get("category_limit").toString());
				// 渠道限制
				String channel_limit = (null == couponTypeLimitMap.get("channel_limit") ? "" : couponTypeLimitMap.get("channel_limit").toString());
				// 商户限制
				String seller_limit = (null == couponTypeLimitMap.get("seller_limit") ? "" : couponTypeLimitMap.get("seller_limit").toString());

				// 是否可以参与活动
				String activity_limit = (null == couponTypeLimitMap.get("activity_limit") ? "" : couponTypeLimitMap.get("activity_limit").toString());
				//活动编码
				String allowed_activity_code = (null == couponTypeLimitMap.get("allowed_activity_type") ? "" : couponTypeLimitMap.get("allowed_activity_type").toString());
				// 品牌除外0:否;1是
				String except_brand = (null == couponTypeLimitMap.get("except_brand") ? "" : couponTypeLimitMap.get("except_brand").toString());
				// 商品除外0:否;1是
				String except_product = (null == couponTypeLimitMap.get("except_product") ? "" : couponTypeLimitMap.get("except_product").toString());
				// 分类除外0:否;1是
				String except_category = (null == couponTypeLimitMap.get("except_category") ? "" : couponTypeLimitMap.get("except_category").toString());

				// 品牌编码
				String brand_codes = (null == couponTypeLimitMap.get("brand_codes") ? "" : couponTypeLimitMap.get("brand_codes").toString());
				// 商品编码
				String product_codes = (null == couponTypeLimitMap.get("product_codes") ? "" : couponTypeLimitMap.get("product_codes").toString());
				// 分类编码
				String category_codes = (null == couponTypeLimitMap.get("category_codes") ? "" : couponTypeLimitMap.get("category_codes").toString());
				// 渠道编码
				String channel_codes = (null == couponTypeLimitMap.get("channel_codes") ? "" : couponTypeLimitMap.get("channel_codes").toString());
				//支付类型限制
				String payment_type_limit = (null == couponTypeLimitMap.get("payment_type") ? "" : couponTypeLimitMap.get("payment_type").toString());
				// 分类编码需要获取到子分类
				if (StringUtils.isNotEmpty(category_codes)) {
					List<Map<String, Object>> categoryLimitListMap = DbUp.upTable("uc_sellercategory").dataSqlList(
									"select category_code from uc_sellercategory where category_code in ('"
											+ category_codes.replace(",", "','")
											+ "') or parent_code in ('"
											+ category_codes.replace(",", "','")
											+ "')", null);
					
					if (null != categoryLimitListMap && !categoryLimitListMap.isEmpty()) {
						Map<String, Integer> categoryMap = new HashMap<String, Integer>();
						for (Map<String, Object> map1 : categoryLimitListMap) {
							categoryMap.put(map1.get("category_code").toString(), 1);
						}
						category_codes = StringUtils.join(categoryMap.keySet(),	",");
					}
				}
				
				/**
				 * 2015-06-16 11:30:00
				 */
				// List<String> productCodeArr = new ArrayList<String>();
				// //可用此优惠券的商品编号
				for (String productCode : productCodes.split("','")) {
					if (flag && "4497471600070002".equals(limitCondition)) {
						boolean flagUse = true; // 默认设置商品是否可用状态为true(可用)
						
						//检查商户限制 -rhb 20181017
						if(flagUse && "449748230002".equals(seller_limit)) {
							// 缓存获取商品信息
							PlusModelProductQuery plusModelProductQuery = new PlusModelProductQuery(productCode);
							PlusModelProductInfo plusModelProductinfo = new LoadProductInfo().upInfoByCode(plusModelProductQuery);
							String smallSellerCode = plusModelProductinfo.getSmallSellerCode();
							if(!"SI2003".equals(smallSellerCode) && !"SI2009".equals(smallSellerCode)) {
								flagUse = false;
							}
						}
						
						// 检查品牌限制
						if (flagUse && "4497471600070002".equals(brand_limit)) {
							if ("0".equals(except_brand) && (StringUtils.isEmpty(brand_codes) 
									|| null == productCode_brandCodeMap 
									|| StringUtils.isEmpty(productCode_brandCodeMap.get(productCode)))) 
							{ 
								// 指定品牌限制非除外，品牌限制列表为空或者传入商品所属品牌为空时商品不可用
								flagUse = false;
							}
							else if ("1".equals(except_brand) && (StringUtils.isEmpty(brand_codes) 
									|| null == productCode_brandCodeMap 
									|| StringUtils.isEmpty(productCode_brandCodeMap.get(productCode)))) 
							{
								// 指定品牌限制为除外，品牌限制列表为空时或者传入商品所属品牌为空商品全部可用
							} 
							else {
								boolean limitBrand = true;
								for (String brandCodeLimit : brand_codes.split(",")) {
									if ("0".equals(except_brand) && brandCodeLimit.equals(productCode_brandCodeMap.get(productCode))) {
										limitBrand = false;
									} else if ("1".equals(except_brand) && brandCodeLimit.equals(productCode_brandCodeMap.get(productCode))) {
										// 指定除外限制的品牌中包含该商品，表示该商品不可使用此优惠券，结束循环
										flagUse = false;
										break;
									}
								}
								// 指定限制的品牌中不包含该商品，表示该商品不可使用此优惠券
								if (limitBrand && "0".equals(except_brand)) {
									flagUse = false;
								}
							}
						}

						// 检查商品限制
						if (flagUse && "4497471600070002".equals(product_limit)) {
							if ("0".equals(except_product) && StringUtils.isEmpty(product_codes)) { 
								// 指定商品限制非除外，商品限制列表为空时商品不可用
								flagUse = false;
							} else if ("1".equals(except_product) && StringUtils.isEmpty(product_codes)) { 
								// 指定商品限制为除外，商品限制列表为空时商品全部可用
							} else {
								boolean limitProduct = true;
								for (String productCodeLimit : product_codes.split(",")) {
									if ("0".equals(except_product) && productCodeLimit.equals(productCode)) {
										limitProduct = false;
									} else if ("1".equals(except_product) && productCodeLimit.equals(productCode)) {
										// 指定除外限制的商品中包含该商品，表示该商品不可使用此优惠券，结束循环
										flagUse = false;
										break;
									}
								}
								// 指定限制的商品中不包含该商品，表示该商品不可使用此优惠券
								if (limitProduct && "0".equals(except_product)) {
									flagUse = false;
								}
							}
						}

						// 检查分类限制
						if (flagUse && "4497471600070002".equals(category_limit)) {
							if ("0".equals(except_category)	&& (StringUtils.isEmpty(category_codes)	
									|| null == productCode_categoryCodeMap 
									|| StringUtils.isEmpty(productCode_categoryCodeMap.get(productCode)))) 
							{ 
								// 指定分类限制非除外，分类限制列表为空或传入商品的所属分类为空时商品不可用
								flagUse = false;
							} 
							else if ("1".equals(except_category) && (StringUtils.isEmpty(category_codes) 
									|| null == productCode_categoryCodeMap 
									|| StringUtils.isEmpty(productCode_categoryCodeMap.get(productCode)))) 
							{ 
								// 指定分类限制为除外，分类限制列表为空或传入商品的所属分类为空时商品全部可用
							} 
							else 
							{
								boolean limitCategory = true;
								for (String categoryCodeLimit : category_codes.split(",")) {
									if ("0".equals(except_category)) {
										for (String categoryCode : productCode_categoryCodeMap.get(productCode).split(",")) {
											if (categoryCodeLimit.equals(categoryCode)) {
												limitCategory = false;
												break;
											}
										}
									} else if ("1".equals(except_category)) {
										// 指定分类限制为除外，分类限制列表为空时商品全部可用
										if (StringUtils.isEmpty(category_codes)) {
											break;
										}
										for (String categoryCode : productCode_categoryCodeMap.get(productCode).split(",")) {
											if (categoryCodeLimit.equals(categoryCode)) {
												// 指定除外限制的分类中包含该商品，表示该商品不可使用此优惠券，结束循环
												flagUse = false;
												break;
											}
										}
										if (!flagUse) {
											break;
										}
									}
								}
								// 指定限制的品牌中不包含该商品，表示该商品不可使用此优惠券
								if (limitCategory && "0".equals(except_category)) {
									flagUse = false;
								}
							}
						}
						
						// 检查是否可以参与活动限制
						if (flagUse && ("449747110001".equals(activity_limit) || "".equals(activity_limit))) {
							for (String skuCode : skuGoodsMap.keySet()) {
								if (skuGoodsMap.get(skuCode).getProduct_code().equals(productCode)) {
									if(activeInfoMap != null && !activeInfoMap.isEmpty()) {
										ActiveReturn activeReturn = activeInfoMap.get(skuCode + "_" + memberCode);
										if (activeReturn.isUse_activity()) {
											flagUse = false;
											break;
										}
									}
								}
							}
						}
						
						// 检查是否可以参与活动限制
						if (flagUse && ("449747110002".equals(activity_limit))) {//可以参与活动，需校验哪种活动类型的商品可以参与
							for (String skuCode : skuGoodsMap.keySet()) {
								if (skuGoodsMap.get(skuCode).getProduct_code().equals(productCode)) {
									if(activeInfoMap != null && !activeInfoMap.isEmpty()) {
										flagUse = false;
										ActiveReturn activeReturn = activeInfoMap.get(skuCode + "_" + memberCode);
										//压根没有参与活动的时候。直接返回可用优惠券
										if(!activeReturn.isUse_activity()){
											flagUse = true;
											break;
										}
										boolean limitActivity = false;
										// 如果传入的使用渠道在指定限制的渠道内则可以使用的优惠券。
										for (String activityCode : allowed_activity_code.split(",")) {
											if (activityCode.equals(activeReturn.getEventType())) {
												limitActivity = true;
												break;
											}
										}
										if(limitActivity){
											flagUse = true;
											break;
										}
									}
								}
							}
						}
						
						if (flagUse) {
							productCodeTmp.add(productCode);
						}
					} else if (flag) {
						// 默认为不参加活动
						for (String skuCode : skuGoodsMap.keySet()) {
							if (skuGoodsMap.get(skuCode).getProduct_code().equals(productCode)) {
								if(activeInfoMap != null && !activeInfoMap.isEmpty()) {
									ActiveReturn activeReturn = activeInfoMap.get(skuCode + "_" + memberCode);
									if (!"4497472600010006".equals(activeReturn.getEventType())) {
										boolean flagUse = true;
										//检查商户限制 -rhb 20181017
										if("449748230002".equals(seller_limit)) {
											// 缓存获取商品信息
											PlusModelProductQuery plusModelProductQuery = new PlusModelProductQuery(productCode);
											PlusModelProductInfo plusModelProductinfo = new LoadProductInfo().upInfoByCode(plusModelProductQuery);
											String smallSellerCode = plusModelProductinfo.getSmallSellerCode();
											if(!"SI2003".equals(smallSellerCode) && !"SI2009".equals(smallSellerCode)) {
												flagUse = false;
											}
										}
										if(flagUse) {
											productCodeTmp.add(productCode);
										}
									}
								}
							}
						}
					}
				}
				
				boolean add = true;
				
				// 检查渠道限制
				if ("4497471600070002".equals(channel_limit)) {
					boolean limitChannel = true;
					// 如果传入的使用渠道不在指定限制的渠道内
					for (String channelCode : channel_codes.split(",")) {
						if (channelCode.equals(channelCodeInput)) {
							limitChannel = false;
							break;
						}
					}
					if (limitChannel) {
						add = false;
					}
				}
				// 检查支付类型限制 无限制：449748290003，在线支付：449748290001，货到付款：449748290002
				if(flag) {//只有前边校验可用的时候再校验支付方式
					if ("449748290001".equals(payment_type_limit)||"449748290002".equals(payment_type_limit)) {//有限制的时候
						flag = false;//有限制时先将add置为false，当满足一下条件时才可添加，置为true。
						if(paymentType.equals(payment_type_limit)){//支付类型相同才可以用
							flag = true;
						}
					}
				}

				// 平台限制的优惠券不展示
				if(!add){
					continue;
				}
				
				BigDecimal usePrice = BigDecimal.ZERO;
				// 最后判断限制额度
				BigDecimal limitMoney = new BigDecimal(limit_money);
				for (String productCode : productCodeTmp) {
					for (String skuCode : skuGoodsMap.keySet()) {
						if (skuGoodsMap.get(skuCode).getProduct_code().equals(productCode)) {
							if(activeInfoMap != null && !activeInfoMap.isEmpty()) {
								ActiveReturn activeReturn = activeInfoMap.get(skuCode + "_" + memberCode);
								// if (!activeReturn.isUse_activity()) {
								BigDecimal skuNum = new BigDecimal(skuGoodsMap.get(skuCode).getSku_num());
								usePrice = usePrice.add(activeReturn.getActivity_price().multiply(skuNum));
							}
							
							// }
						}
					}
				}
				
				// 可以使用此类型优惠券的商品总额小于限制额度,或者可用商品数组为0时，标志此类型优惠券不可以使用
				if (usePrice.compareTo(limitMoney) < 0
						|| productCodeTmp.size() == 0
						|| usePrice.compareTo(BigDecimal.ZERO) <= 0) {
					flag = false;
				}
				
			}
			
			//判断增加未激活标志
			String blocked = (null == maps.get("blocked") ? "" : maps.get("blocked").toString());
			if(StringUtils.isNotEmpty(blocked) && blocked.equals("1")) {
				couponInfo.setStatus(5);
			}
			//判断增加送此优惠券的大订单号
			String big_order_code = (null == maps.get("big_order_code") ? "" : maps.get("big_order_code").toString());
			if(StringUtils.isNotEmpty(big_order_code)) {
				couponInfo.setBigOrderCode(big_order_code);
			}
			
			//判断增加即将过期天数
			String endTime = maps.get("end_time").toString();
			try {
				double diffDay = DateHelper.daysBetween(DateHelper.upNow(), endTime);
				if(diffDay>=1.0 && diffDay <= deadlineDay) {
					couponInfo.setDeadline("还剩" + (new Double(diffDay).intValue() + 1) + "天");
				} else if(diffDay >= 0.0 && diffDay < 1.0) {
					int diffHour = DateHelper.hoursBetween(DateHelper.upNow(), endTime);
					if(diffHour == 0) {
						diffHour = 1;
					}
					couponInfo.setDeadline("还剩" + diffHour + "小时");
				}
				
				//542版本增加快过期标签 -rhb 20190412
				if(diffDay>=0 && diffDay<=2.0) {
					couponInfo.setIsShowDue("1");
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			//判断是否可以叠加使用 -rhb 20181024
			String is_multi_use = "Y";
			if(StringUtils.isNotEmpty(activity_code)) {
				is_multi_use = DbUp.upTable("oc_activity").dataGet("is_multi_use", "activity_code=:activity_code", new MDataMap("activity_code",activity_code)) + "";
			}
			couponInfo.setIs_multi_use(is_multi_use);
			
			// 转换一下折扣卷的金额显示
			couponUtil.convertMoneyShow(couponInfo);
			
			// 小于520版本，礼金券当做金额券显示
			if(StringUtils.isNotBlank(version) 
					&& AppVersionUtils.compareTo(version, "5.2.0") < 0
					&& "449748120003".equals(couponInfo.getMoneyType())){
				couponInfo.setMoneyType("449748120001");
			}
			
			/**
			 * 542增加限制条件 -rhb 20190424
			 * 优惠券类型定义    使用下限金额     限制条件
			 * 无门槛                  0                       无限制
			 * 无金额限制           0                       指定
			 * 满X元可用             X             ——
			 */
			String limitCondition = "";
			if("0".equals(limit_money)) {
				if("4497471600070001".equals(couponTypeCodesMap.get(coupon_type_code).get("limit_condition"))) {
					limitCondition = bConfig("familyhas.no_threshold");
				}
				if("4497471600070002".equals(couponTypeCodesMap.get(coupon_type_code).get("limit_condition"))) {
					limitCondition = bConfig("familyhas.unlimited_amount");
				}
			}else {
				limitCondition = FormatHelper.formatString(bConfig("familyhas.with_x_available"),limit_money);
			}
			couponInfo.setLimitCondition(limitCondition);
			
			//判断优惠券是否已激活
			if(StringUtils.isNotEmpty(blocked) && maps.get("blocked").equals("1")) {
				disableCouponList.add(couponInfo);
			} else {
				if (flag) {
					couponInfoList.add(couponInfo);
					// ableUseCoupon.put(maps.get("coupon_type_code"), couponInfo);
					// //将可以使用的优惠券类型放到此map中，同样优惠券类型发多张时可以减少判断
				} else {
					disableCouponList.add(couponInfo);
					// unAbleUseCoupon.put(maps.get("coupon_type_code"),
					// couponInfo); //将不可以使用的优惠券类型放到此map中，同样优惠券类型发多张时可以减少判断
				}
			}
			
		}	
		map.put("available", couponInfoList);
		map.put("disable", disableCouponList);
		return map;
	}

	/**
	 * 获取可用优惠券的商品编号，map值为不为空的时候表示可用,key为sku_code
	 * 
	 * @return
	 */
	public Map<String, Double> getAvailableCouponProduct(String memberCode,
			List<String> couponCode, List<GoodsInfoForAdd> goodsList,
			String channelCodeInput) {

		Map<String, Double> result = new HashMap<String, Double>(); // key:skuCode,value:数量
		if (StringUtils.isEmpty(memberCode) || null == goodsList || goodsList.isEmpty()) {
			return result;
		}

		// List<String> productCodeArr = new ArrayList<String>(); //可用优惠券的商品编号
		Map<String, Integer> productCodeArr = new HashMap<String, Integer>();
		// Map<String,String> skuProductCodeMap = new HashMap<String, String>();
		// //key:skuCode,Value:productCode（用来计算商品限制额度）

		ProductService ps = new ProductService();
		Map<String, GoodsInfoForAdd> skuGoodsMap = new HashMap<String, GoodsInfoForAdd>(); // key:skuCode,Value:GoodsInfoForAdd（用来计算商品限制额度）
		Map<String, Integer> productCodeMap = new HashMap<String, Integer>(); // productCode去重(查询各种限制)

		ActiveForproduct activeInfo = new ActiveForproduct();
		List<ActiveReq> activeRequests = new ArrayList<ActiveReq>();
		ActiveResult activeResult = new ActiveResult();

		for (GoodsInfoForAdd goodInfo : goodsList) {

			ActiveReq activeReq = new ActiveReq();
			activeReq.setBuyer_code(memberCode);
			activeReq.setProduct_code(goodInfo.getProduct_code());
			activeReq.setSku_code(goodInfo.getSku_code());
			activeReq.setSku_num(goodInfo.getSku_num());
			activeRequests.add(activeReq);

			/**
			 * IC编号替换为正常skuCode与productCode
			 */
			goodInfo.setProduct_code(ps.getProductCodeForICcode(goodInfo.getProduct_code()));
			goodInfo.setSku_code(ps.getSkuCodeForICcode(goodInfo.getSku_code()));
			skuGoodsMap.put(goodInfo.getSku_code(), goodInfo);
			productCodeMap.put(goodInfo.getProduct_code(), 1);
		}

		Map<String, ActiveReturn> activeInfoMap = activeInfo.activeGallery(activeRequests, activeResult); // 判断sku参加的各种活动

		String productCodes = StringUtils.join(productCodeMap.keySet(), "','");
		String sWhere = "member_code='"	+ memberCode + "' and status=0 and surplus_money>0 and end_time>now() and start_time<now()";
		if (couponCode != null && couponCode.size() > 0) {
			sWhere += " and coupon_code in (" + getCouponCodes(couponCode) + ") ";
		}
		List<MDataMap> listMaps = DbUp.upTable("oc_coupon_info").queryAll("coupon_code,surplus_money,status,end_time,limit_money,"
				+ "start_time,coupon_type_code", "end_time asc", sWhere, null);
		
		String couponTypeCodes = ""; // 结果集中所有优惠券类型编号
		Map<String, Map<String, Object>> couponTypeCodesMap = new HashMap<String, Map<String, Object>>();
		Map<String, String> productCode_brandCodeMap = new HashMap<String, String>(); // 订单中的商品与品牌对应map
		Map<String, String> productCode_categoryCodeMap = new HashMap<String, String>(); // 订单中的商品与分类对应map(如若为多个分类，分类中间用英文逗号隔开)
		// 优惠券类型对象map信息
		for (MDataMap mDataMap : listMaps) {
			couponTypeCodesMap.put(mDataMap.get("coupon_type_code"), new HashMap<String, Object>());
		}
		couponTypeCodes = StringUtils.join(couponTypeCodesMap.keySet(), "','");
		if (StringUtils.isNotEmpty(couponTypeCodes)) {
			String couponCodeTypeSql = "select ot.limit_scope,ot.limit_explain,ot.coupon_type_code,ot.limit_condition,otl.brand_limit,otl.product_limit,otl.category_limit,otl.channel_limit,otl.activity_limit,otl.except_brand,otl.except_product,otl.except_category,otl.except_channel,otl.brand_codes,otl.product_codes,otl.category_codes,otl.channel_codes from oc_coupon_type ot "
					+ "LEFT JOIN oc_coupon_type_limit otl ON ot.coupon_type_code = otl.coupon_type_code "
					+ "where ot.coupon_type_code in ('"
					+ couponTypeCodes
					+ "') ";

			List<Map<String, Object>> couponTypeMapList = DbUp.upTable("oc_coupon_type").dataSqlList(couponCodeTypeSql, null);
			for (Map<String, Object> couponTypeMap : couponTypeMapList) {
				couponTypeCodesMap.put(couponTypeMap.get("coupon_type_code").toString(), couponTypeMap);
			}
		}

		// 分别查询出传入sku对应的商品编号,品牌,分类
		List<MDataMap> brandMapList = DbUp.upTable("pc_productinfo").queryAll("product_code,brand_code", "", "product_code in ('" + productCodes + "')", null);

		for (MDataMap mDataMap : brandMapList) {
			productCode_brandCodeMap.put(mDataMap.get("product_code"), mDataMap.get("brand_code"));
		}
		
		List<MDataMap> categoryMapList = DbUp.upTable("uc_sellercategory_product_relation").queryAll("category_code,product_code", "", "product_code in ('" + productCodes + "')", null);
		for (MDataMap mDataMap : categoryMapList) {
			String productCode = mDataMap.get("product_code");
			String categoryCode = mDataMap.get("category_code");
			if (StringUtils.isNotEmpty(productCode_categoryCodeMap.get(productCode))) {
				categoryCode += ("," + productCode_categoryCodeMap.get(productCode));
			}
			productCode_categoryCodeMap.put(productCode, categoryCode);
		}

		for (MDataMap maps : listMaps) {
			// 指定限制条件时
			Map<String, Object> couponTypeLimitMap = couponTypeCodesMap.get(maps.get("coupon_type_code"));
			String limitCondition = (null == couponTypeLimitMap.get("limit_condition") ? "" : couponTypeLimitMap.get("limit_condition").toString());
			List<String> productCodeTmp = new ArrayList<String>();

			// 品牌限制
			String brand_limit = (null == couponTypeLimitMap.get("brand_limit") ? "" : couponTypeLimitMap.get("brand_limit").toString());
			// 商品限制
			String product_limit = (null == couponTypeLimitMap.get("product_limit") ? "" : couponTypeLimitMap.get("product_limit").toString());
			// 分类限制
			String category_limit = (null == couponTypeLimitMap.get("category_limit") ? "" : couponTypeLimitMap.get("category_limit").toString());
			// 渠道限制
			String channel_limit = (null == couponTypeLimitMap.get("channel_limit") ? "" : couponTypeLimitMap.get("channel_limit").toString());
			// 是否可以参与活动
			String activity_limit = (null == couponTypeLimitMap.get("activity_limit") ? "" : couponTypeLimitMap.get("activity_limit").toString());

			// 品牌除外0:否;1是
			String except_brand = (null == couponTypeLimitMap.get("except_brand") ? "" : couponTypeLimitMap.get("except_brand").toString());
			// 商品除外0:否;1是
			String except_product = (null == couponTypeLimitMap.get("except_product") ? "" : couponTypeLimitMap.get("except_product").toString());
			// 分类除外0:否;1是
			String except_category = (null == couponTypeLimitMap.get("except_category") ? "" : couponTypeLimitMap.get("except_category").toString());

			// 品牌编码
			String brand_codes = (null == couponTypeLimitMap.get("brand_codes") ? "" : couponTypeLimitMap.get("brand_codes").toString());
			// 商品编码
			String product_codes = (null == couponTypeLimitMap.get("product_codes") ? "" : couponTypeLimitMap.get("product_codes").toString());
			// 分类编码
			String category_codes = (null == couponTypeLimitMap.get("category_codes") ? "" : couponTypeLimitMap.get("category_codes").toString());
			// 渠道编码
			String channel_codes = (null == couponTypeLimitMap.get("channel_codes") ? "" : couponTypeLimitMap.get("channel_codes").toString());

			// 分类编码需要获取到子分类
			if (StringUtils.isNotEmpty(category_codes)) {
				List<Map<String, Object>> categoryLimitListMap = DbUp.upTable("uc_sellercategory").dataSqlList(
						"select category_code from uc_sellercategory where category_code in ('"
								+ category_codes.replace(",", "','")
								+ "') or parent_code in ('"
								+ category_codes.replace(",", "','") + "')", null);
				
				if (null != categoryLimitListMap && !categoryLimitListMap.isEmpty()) {
					Map<String, Integer> categoryMap = new HashMap<String, Integer>();
					for (Map<String, Object> map : categoryLimitListMap) {
						categoryMap.put(map.get("category_code").toString(), 1);
					}
					category_codes = StringUtils.join(categoryMap.keySet(), ",");
				}
			}
			
			for (String productCode : productCodes.split("','")) {
				if ("4497471600070002".equals(limitCondition)) {
					boolean flagUse = true; // 默认设置商品是否可用状态为true(可用)
					// 检查品牌限制
					if (flagUse && "4497471600070002".equals(brand_limit)) {
						if ("0".equals(except_brand) && (StringUtils.isEmpty(brand_codes) 
								|| null == productCode_brandCodeMap 
								|| StringUtils.isEmpty(productCode_brandCodeMap.get(productCode)))) 
						{ 
							// 指定品牌限制非除外，品牌限制列表为空或者传入商品所属品牌为空时商品不可用
							flagUse = false;
						} else if ("1".equals(except_brand) && (StringUtils.isEmpty(brand_codes) 
								|| null == productCode_brandCodeMap 
								|| StringUtils.isEmpty(productCode_brandCodeMap.get(productCode)))) 
						{ 
							// 指定品牌限制为除外，品牌限制列表为空或者传入商品所属品牌为空时商品全部可用
						} else {
							boolean limitBrand = true;
							for (String brandCodeLimit : brand_codes.split(",")) {
								if ("0".equals(except_brand) && brandCodeLimit.equals(productCode_brandCodeMap.get(productCode))) {
									limitBrand = false;
								} else if ("1".equals(except_brand) && brandCodeLimit.equals(productCode_brandCodeMap.get(productCode))) {
									// 指定除外限制的品牌中包含该商品，表示该商品不可使用此优惠券，结束循环
									flagUse = false;
									break;
								}
							}
							// 指定限制的品牌中不包含该商品，表示该商品不可使用此优惠券
							if (limitBrand && "0".equals(except_brand)) {
								flagUse = false;
							}
						}
					}

					// 检查商品限制
					if (flagUse && "4497471600070002".equals(product_limit)) {
						if ("0".equals(except_product) && (StringUtils.isEmpty(product_codes))) { 
							// 指定商品限制非除外，商品限制列表为空时商品不可用
							flagUse = false;
						} else if ("1".equals(except_product) && StringUtils.isEmpty(product_codes)) { 
							// 指定商品限制为除外，商品限制列表为空时商品全部可用
						} else {
							boolean limitProduct = true;
							for (String productCodeLimit : product_codes.split(",")) {
								if ("0".equals(except_product) && productCodeLimit.equals(productCode)) {
									limitProduct = false;
								} else if ("1".equals(except_product) && productCodeLimit.equals(productCode)) {
									// 指定除外限制的商品中包含该商品，表示该商品不可使用此优惠券，结束循环
									flagUse = false;
									break;
								}
							}
							// 指定限制的商品中不包含该商品，表示该商品不可使用此优惠券
							if (limitProduct && "0".equals(except_product)) {
								flagUse = false;
							}
						}
					}

					// 检查分类限制
					if (flagUse && "4497471600070002".equals(category_limit)) {
						if ("0".equals(except_category) && (StringUtils.isEmpty(category_codes)
										|| null == productCode_categoryCodeMap 
										|| StringUtils.isEmpty(productCode_categoryCodeMap.get(productCode)))) 
						{ 
							// 指定分类限制非除外，分类限制列表为空或传入商品的所属分类为空时商品不可用
							flagUse = false;
						} else if ("1".equals(except_category) && (StringUtils.isEmpty(category_codes)
										|| null == productCode_categoryCodeMap 
										|| StringUtils.isEmpty(productCode_categoryCodeMap.get(productCode)))) 
						{ 
							// 指定分类限制为除外，分类限制列表为空或传入商品的所属分类为空时商品全部可用
						} else {
							boolean limitCategory = true;
							for (String categoryCodeLimit : category_codes.split(",")) {
								if ("0".equals(except_category)) {
									for (String categoryCode : productCode_categoryCodeMap.get(productCode).split(",")) {
										if (categoryCodeLimit.equals(categoryCode)) {
											limitCategory = false;
											break;
										}
									}
								} else if ("1".equals(except_category)) {
									// 指定分类限制为除外，分类限制列表为空时商品全部可用
									if (StringUtils.isEmpty(category_codes)) {
										break;
									}
									for (String categoryCode : productCode_categoryCodeMap.get(productCode).split(",")) {
										if (categoryCodeLimit.equals(categoryCode)) {
											// 指定除外限制的分类中包含该商品，表示该商品不可使用此优惠券，结束循环
											flagUse = false;
											break;
										}
									}
									if (!flagUse) {
										break;
									}
								}
							}
							// 指定限制的品牌中不包含该商品，表示该商品不可使用此优惠券
							if (limitCategory && "0".equals(except_category)) {
								flagUse = false;
							}
						}
					}

					// 检查渠道限制
					if (flagUse && "4497471600070002".equals(channel_limit)) {
						boolean limitChannel = true;
						// 如果传入的使用渠道不在指定限制的渠道内
						for (String channelCode : channel_codes.split(",")) {
							if (channelCode.equals(channelCodeInput)) {
								limitChannel = false;
								break;
							}
						}
						if (limitChannel) {
							flagUse = false;
						}
					}

					// 检查是否可以参与活动限制
					if (flagUse	&& ("449747110001".equals(activity_limit) || "".equals(activity_limit))) {
						for (String skuCode : skuGoodsMap.keySet()) {
							if (skuGoodsMap.get(skuCode).getProduct_code().equals(productCode)) {
								if(activeInfoMap != null && !activeInfoMap.isEmpty()) {
									ActiveReturn activeReturn = activeInfoMap.get(skuCode + "_" + memberCode);
									if (activeReturn.isUse_activity()) {
										flagUse = false;
										break;
									}
								}
							}
						}
					} else if (flagUse && "".equals(activity_limit)) {
						flagUse = false;
					}

					if (flagUse) {
						productCodeTmp.add(productCode);
					}
				} else {
					//542版本需求改为 无限制可以参加活动 -rhb 20190411
					productCodeTmp.add(productCode);
					// 默认为不参加活动
					/*for (String skuCode : skuGoodsMap.keySet()) {
						if (skuGoodsMap.get(skuCode).getProduct_code().equals(productCode)) {
							if(activeInfoMap != null && !activeInfoMap.isEmpty()) {
								ActiveReturn activeReturn = activeInfoMap.get(skuCode + "_" + memberCode);
								if (!activeReturn.isUse_activity()) {
									productCodeTmp.add(productCode);
								}
							}
						}
					}*/
				}
			}

			BigDecimal usePrice = BigDecimal.ZERO;
			// 最后判断限制额度
			BigDecimal limitMoney = new BigDecimal(maps.get("limit_money"));
			for (String productCode : productCodeTmp) {
				for (String skuCode : skuGoodsMap.keySet()) {
					if (skuGoodsMap.get(skuCode).getProduct_code().equals(productCode)) {
						if(activeInfoMap != null && !activeInfoMap.isEmpty()) {
							ActiveReturn activeReturn = activeInfoMap.get(skuCode + "_" + memberCode);
							// if (!activeReturn.isUse_activity()) {
							BigDecimal skuNum = new BigDecimal(skuGoodsMap.get(skuCode).getSku_num());
							usePrice = usePrice.add(activeReturn.getActivity_price().multiply(skuNum));
							// }
						}
					}
				}
			}
			
			// 可以使用此类型优惠券的商品总额大于等于限制额度，并且可以使用优惠券商品列表不为空，这些商品可以使用这个优惠券
			if (usePrice.compareTo(limitMoney) >= 0 && productCodeTmp.size() > 0 && usePrice.compareTo(BigDecimal.ZERO) > 0) {
				for (String productCode : productCodeTmp) {
					productCodeArr.put(productCode, 1);
				}
			}
		}
		for (String productCode : productCodeArr.keySet()) {
			for (String skuCode : skuGoodsMap.keySet()) {
				if (skuGoodsMap.get(skuCode).getProduct_code().equals(productCode)) {
					result.put(skuCode, Double.valueOf(skuGoodsMap.get(skuCode).getSku_num()));
				}
			}
		}
		return result;
	}
	
	/**
	 * @desc 根据商品编号查询可用优惠券列表
	 * @param memberCode
	 * @param productCode
	 * @param version
	 * @return
	 */
	public List<CouponForGetInfo> getCouponListForProduct(String memberCode, String productCode,String fxFlag){
		CouponListQuery tQuery = new CouponListQuery();
		tQuery.setCode(productCode);
		tQuery.setMemberCode(memberCode);
		tQuery.setFxFlag(fxFlag);
		PlusModelCouponListInfo listInfo = new LoadCouponListForProduct().upInfoByCode(tQuery);
		List<CouponForGetInfo> result = new ArrayList<CouponForGetInfo>();
		if(listInfo == null) {//判空处理
			return result;
		}
		List<ModelCouponForGetInfo> list = listInfo.getCouponList();
		for(ModelCouponForGetInfo info : list) {
			CouponForGetInfo couponInfo = new CouponForGetInfo();
			couponInfo.setUid(info.getUid());
			couponInfo.setActivityCode(info.getActivityCode());
			couponInfo.setCouponTypeCode(info.getCouponTypeCode());
			couponInfo.setCouponTypeName(info.getCouponTypeName());
			couponInfo.setEndTime(info.getEndTime());
			couponInfo.setIfStore(info.getIfStore());
			couponInfo.setLimitExplain(info.getLimitExplain());
			couponInfo.setLimitScope(info.getLimitScope());
			couponInfo.setLimitMoney(info.getLimitMoney());
			couponInfo.setMoney(info.getMoney());
			couponInfo.setMoneyType(info.getMoneyType());
			couponInfo.setStartTime(info.getStartTime());
			couponInfo.setActionType(info.getActionType());
			couponInfo.setActionValue(info.getActionValue());
			couponInfo.setChannelCodes(info.getChannelCodes());
			couponInfo.setProductLimit(info.getProductLimit());
			couponInfo.setExceptProduct(info.getExceptProduct());
			couponInfo.setProductCodes(info.getProductCodes());
			couponInfo.setChannelLimit(info.getChannelLimit());
			couponInfo.setCouponStartTime(info.getCouponStartTime());
			couponInfo.setCouponEndTime(info.getCouponEndTime());
			couponInfo.setValidDay(info.getValidDay());
			couponInfo.setActivityType(info.getActivityType());
			//获取是否已领取优惠券信息
			int status = 0;
			CouponGetUserQuery userQuery = new CouponGetUserQuery();
			userQuery.setCode(memberCode+"-"+info.getCouponTypeCode());
			CouponGetUserInfo userInfo = new LoadCouponGetUser().upInfoByCode(userQuery);
			if(userInfo != null) {
				status = 1;
			}
			int ifStore = info.getIfStore();
			couponInfo.setStatus(status);
			if(ifStore == 1 && status == 0) {//未领取
				Button button = new Button();
				button.setButtonCode("4497477800080018");
				button.setButtonTitle("点击领取");
				button.setButtonStatus(1);
				couponInfo.getButtons().add(button);
			}else if(status == 1) {//已经领取了
				Button button = new Button();
				button.setButtonCode("4497477800080019");
				button.setButtonTitle("立即使用");
				button.setButtonStatus(1);
				couponInfo.getButtons().add(button);
				//已经领取的优惠券有效期取优惠券列表中的时间
				couponInfo.setStartTime(userInfo.getStartTime());
				couponInfo.setEndTime(userInfo.getEndTime());
			}
			/**
			 * 542增加限制条件 -rhb 20190424
			 * 优惠券类型定义    使用下限金额     限制条件
			 * 无门槛                  0                       无限制
			 * 无金额限制           0                       指定
			 * 满X元可用             X             ——
			 */
			String limitCondition = "";
			if(info.getLimitMoney().compareTo(BigDecimal.ZERO) == 0) {
				if("4497471600070001".equals(info.getLimitCondition())) {
					limitCondition = bConfig("familyhas.no_threshold");
				}
				if("4497471600070002".equals(info.getLimitCondition())) {
					limitCondition = bConfig("familyhas.unlimited_amount");
				}
			}else {
				limitCondition = FormatHelper.formatString(bConfig("familyhas.with_x_available"),info.getLimitMoney());
			}
			couponInfo.setLimitCondition(limitCondition);

			result.add(couponInfo);
		}
		return result;
	}

	/**
	 * @Description:获取优惠券剩余金额
	 * @param couponTypeCode
	 *            优惠券编号
	 * @author 张海生
	 * @date 2015-6-18 下午5:29:30
	 * @return BigDecimal
	 * @throws
	 */
	public BigDecimal getCouponMoney(String couponCode) {
		if (StringUtils.isEmpty(couponCode)) {
			return new BigDecimal("0");
		} else {
			MDataMap couponMap = DbUp.upTable("oc_coupon_info").oneWhere("surplus_money", "", "coupon_code=:couponCode and start_time <= now() and end_time >= now()",
							"couponCode", couponCode);
			// 查询优惠券信息
			if (couponMap != null && StringUtils.isNotEmpty(couponMap.get("surplus_money"))) {
				return new BigDecimal(couponMap.get("surplus_money"));
			} else {
				return new BigDecimal("0");
			}
		}
	}
	
	/** 
	* @Description:任务执行状态
	* @param taskCode
	* @author 张海生
	* @date 2016-2-26 下午2:25:22
	* @return String 
	* @throws 
	*/
	public String getCouponTaskStatus(String taskCode){
		String taskStatus = "";
		List<MDataMap> checkListb = DbUp.upTable("oc_coupon_check").queryAll("mobile,distribute_status", "", "", new MDataMap("task_code", taskCode));
		for (MDataMap mDataMap : checkListb) {
			taskStatus = mDataMap.get("distribute_status");
			if("4497471600250003".equals(taskStatus)){
				//任务下只要有一个已执行即为已执行
				break;
			}
		}
		return taskStatus;
	}
	
	private String getCouponCodes(List<String> couponCodes) {
		String ret = "";
		for(String code : couponCodes) {
			ret += ",'" + code + "'";
		}
		return ret.substring(1);
	}
	
	public MDataMap getLdCategoryLimitName(String couponTypeCode) {
		MDataMap mDataMap = DbUp.upTable("oc_coupon_type_limit").one("coupon_type_code", couponTypeCode);
		if(null != mDataMap) {
			mDataMap.put("category_nm", mDataMap.get("category_nm").replaceAll(",", "&nbsp;&nbsp;&nbsp;&nbsp;"));
		}else {
			mDataMap = new MDataMap();
			mDataMap.put("create_user", "");
			mDataMap.put("category_limit", "");
			mDataMap.put("category_nm", "");
		}
		return mDataMap;
	}
	
	/**
	 * 查询优惠券迭代3列表，根据优惠券来源查询<br>
	 * 
	 */
	public MPageData upChartDataByCouponSource(ControlPage cp){
		String couponSource = cp.getReqMap().get("zw_f_coupon_source");
		
		// 设置符合商品编号的活动条件
		if(StringUtils.isNotBlank(couponSource)){			
			if(couponSource.equals("ld")){
				cp.getReqMap().put("sub_query", " creator = 'ld'");
			}else if(couponSource.equals("hjy")){
				cp.getReqMap().put("sub_query", " creator <> 'ld'");
			}
			
		}
		
		return cp.upChartData();
	}
	/**
	 * 判断小程序渠道配置优惠券是否满足分销券的条件设定:
        1、后台配置了商品详情页显示；
		2、后台配置了只有当前商品可以使用；
		3、当前商品sku的最低价格≥使用下限金额（使用下限金额≤优惠券面额）；
		4、优惠券活动已过期不显示；
		5、优惠券与当前商品任一sku参加的活动互斥不显示；
		6、如果商品详情显示了悬浮领取，则领取活动隐藏。
	 */
	public List<CouponForGetInfo> checkIfDistributionCoupon(String memberCode,String productCode,List<CouponForGetInfo> list,String channelId,RootResultWeb paramResult) {
		// TODO Auto-generated method stub
		String currentTime = DateUtil.getSysDateTimeString();
		List<CouponForGetInfo> resList = new ArrayList<CouponForGetInfo>();
		List<CouponForGetInfo> temResList = new ArrayList<CouponForGetInfo>();
		CouponForGetInfo maxCoup = new CouponForGetInfo();
		PlusModelProductInfo plusProductInfo = new LoadProductInfo().upInfoByCode(new PlusModelProductQuery(productCode));
		BigDecimal minSellPrice = plusProductInfo.getMinSellPrice();
		BigDecimal maxValue=new BigDecimal(0);
		String pl=bConfig("familyhas.no_threshold");
		BigDecimal temMoney=BigDecimal.ZERO;
		boolean flag = false;
		for (CouponForGetInfo couponForGetInfo : list) {
			BigDecimal money = couponForGetInfo.getMoney();
			String channelCodes = couponForGetInfo.getChannelCodes();
			String actionType = couponForGetInfo.getActionType();
			BigDecimal limitMoney = couponForGetInfo.getLimitMoney();
			String productLimit = couponForGetInfo.getProductLimit();
			String exceptProduct = couponForGetInfo.getExceptProduct();
			String productCodes = couponForGetInfo.getProductCodes();
        	String limitCondition = couponForGetInfo.getLimitCondition();
        	String channelLimit = couponForGetInfo.getChannelLimit();
        	String couponStartTime = couponForGetInfo.getCouponStartTime();
        	String couponEndTime = couponForGetInfo.getCouponEndTime();
        	if(StringUtils.isBlank(couponStartTime)&&StringUtils.isBlank(couponEndTime)) {
        		//此时按天数判断的过期时间
        		int validDay = Integer.parseInt(couponForGetInfo.getValidDay());
        		couponStartTime=currentTime;
        		couponEndTime = DateUtil.addDateHour(couponStartTime, validDay*24);
        	}
        	String moneyType=couponForGetInfo.getMoneyType();
			//小程序渠道
			//跳转页面为商品详情&&活动绑定了小程序渠道&&指定商品&&优惠金额最大&&满足使用下限金额&&只指定了一个商品
            if(StringUtils.equals(channelId, "449747430023")&&StringUtils.equals(actionType, "4497471600280001")&&StringUtils.contains(channelCodes, "449747430023")
					&&(money.compareTo(maxValue)>=0)&&minSellPrice.compareTo(limitMoney)>=0&&StringUtils.equals(exceptProduct, "0")&&StringUtils.equals(productLimit, "4497471600070002")
					&&StringUtils.equals(productCodes, productCode)&&DateUtil.compareTime(currentTime, couponStartTime,DateUtil.DATE_FORMAT_DATETIME)>=0&&DateUtil.compareTime(currentTime, couponEndTime,DateUtil.DATE_FORMAT_DATETIME)<0
					&&(StringUtils.equals("449748120001", moneyType))) {
				//判断是否领取
				CouponGetUserQuery userQuery = new CouponGetUserQuery();
				userQuery.setCode(memberCode+"-"+couponForGetInfo.getCouponTypeCode());
				CouponGetUserInfo userInfo = new LoadCouponGetUser().upInfoByCode(userQuery);
				if(userInfo!=null) {
					//查看领取后是否使用
					MDataMap info = DbUp.upTable("oc_coupon_info").onePriLib("member_code",memberCode,"coupon_type_code",couponForGetInfo.getCouponTypeCode(),"status","0");
					if(info==null) {
						//此时该用户已使用该券,但活动还在有效期
						if(money.compareTo(temMoney)>0) {
							temMoney = money;
						}
						continue;
					}else {
						couponForGetInfo.setCouponCode(info.get("coupon_code"));
						if(money.compareTo(temMoney)>0) {
							temMoney = money;
						}
					}
				}
				flag = true;
				maxValue=money;
				couponForGetInfo.setIfDistributionCoupon("1");
				maxCoup = couponForGetInfo;
				temResList.add(couponForGetInfo);
				if(money.compareTo(temMoney)>0) {
					temMoney = money;
				}

            }else {//557之后的券显示条件：在券类型做指定条件限制的前提下，勾选那个渠道，哪个渠道才显示；券类型未做条件限制，则不作校验
            	if(StringUtils.equals(limitCondition, pl)||StringUtils.equals(channelLimit, "4497471600070001")
            			||StringUtils.contains(channelCodes, channelId)){
            		resList.add(couponForGetInfo);
    				}
            }
		}
        if(flag&&StringUtils.equals(channelId, "449747430023")) {
        	resList.clear();
    		CouponForGetInfo cfg = maxCoup;
    		for (CouponForGetInfo couponForGetInfo : temResList) {
				if((couponForGetInfo.getMoney().compareTo(cfg.getMoney())>=0)
						&&(DateUtil.compareTime(cfg.getEndTime(), couponForGetInfo.getEndTime(),DateUtil.DATE_FORMAT_DATETIME)>0)) {
					cfg = couponForGetInfo;
				}
			}
        	resList.add(cfg);
        }
        if(temMoney.compareTo(BigDecimal.ZERO)>0) {
        	paramResult.setResultMessage(temMoney+"");
        }
		return resList;
	}	

	public List<CouponForGetInfo> checkIfDistributionCouponNew(String memberCode, String productCode,
			List<CouponForGetInfo> list, String channelId, RootResultWeb paramResult,String fxFlag) {
		// TODO Auto-generated method stub
		//小程序渠道，并且从优惠券模板进入详情
		List<CouponForGetInfo> resultList = new ArrayList<CouponForGetInfo>();
		if(StringUtils.equals(channelId, "449747430023")&&StringUtils.equals(fxFlag, "1")) {
			List<Map<String,Object>> dataSqlList = DbUp.upTable("oc_activity_agent_product").dataSqlList("select a.* from oc_activity_agent_product a,oc_activity b where a.activity_code=b.activity_code and b.flag=1 and b.activity_type ='449715400008' "
					+ "and b.begin_time<=now() and b.end_time>now()	and a.produt_code=:produt_code  and a.flag_enable=1 order by a.zid desc", new MDataMap("produt_code",productCode));
			if(dataSqlList!=null&&dataSqlList.size()>0) {
				String coupon_type_code = dataSqlList.get(0).get("coupon_type_code").toString();
				BigDecimal temMoney=MoneyHelper.roundHalfUp(new BigDecimal(dataSqlList.get(0).get("coupon_money").toString()));
				paramResult.setResultMessage(temMoney.toString());
				String currentTime = DateUtil.getSysDateTimeString();
				//判断是否领取并有效
				int dataCount = DbUp.upTable("oc_coupon_info").dataCount("member_code=:member_code and coupon_type_code=:coupon_type_code and status=0 and start_time<=now() and end_time>now() ", new MDataMap("member_code",memberCode,"coupon_type_code",coupon_type_code));
				if(dataCount==0) {
					//不存在，可领取
					for (CouponForGetInfo coup : list) {
						if(coupon_type_code.equals(coup.getCouponTypeCode())) {
							coup.setIfDistributionCoupon("1");
							coup.getButtons().get(0).setButtonCode("4497477800080018");
							coup.getButtons().get(0).setButtonStatus(1);
							coup.getButtons().get(0).setButtonTitle("点击领取");
							coup.setMoney(temMoney);
							resultList.add(coup);	
							break;
						}
					}
				}else {
					//已存在，倒计时
					//未过期做回显，做倒计时；
					for (CouponForGetInfo coup : list) {
						if(coupon_type_code.equals(coup.getCouponTypeCode())) {
							List<Map<String, Object>> sqlList = DbUp.upTable("oc_coupon_info").dataSqlList("select * from oc_coupon_info where member_code=:member_code and coupon_type_code=:coupon_type_code and status=0 and start_time<=now() and end_time>now() order by zid desc", new MDataMap("member_code",memberCode,"coupon_type_code",coupon_type_code));	
							coup.setIfDistributionCoupon("1");
							coup.setMoney(new BigDecimal(dataSqlList.get(0).get("coupon_money").toString()));
							coup.setCouponCode(sqlList.get(0).get("coupon_code").toString());
							coup.setStartTime(sqlList.get(0).get("start_time").toString());
							coup.setEndTime(sqlList.get(0).get("end_time").toString());
							resultList.add(coup);	
							break;
						}
					}
				}
			}
			//2020/10/12孙炳春强烈确信fxFlag字段没使用着，好按照他的要求改
			else {
				String pl=bConfig("familyhas.no_threshold");
				for (CouponForGetInfo couponForGetInfo : list) {
					String channelCodes = couponForGetInfo.getChannelCodes();
		        	String limitCondition = couponForGetInfo.getLimitCondition();
		        	String channelLimit = couponForGetInfo.getChannelLimit();
					if((StringUtils.equals(limitCondition, pl)||StringUtils.equals(channelLimit, "4497471600070001")
		        			||StringUtils.contains(channelCodes, channelId))&&!"449715400008".equals(couponForGetInfo.getActivityType())){
						resultList.add(couponForGetInfo);
						}
				}
				
			}
		}else {
			String pl=bConfig("familyhas.no_threshold");
			for (CouponForGetInfo couponForGetInfo : list) {
				String channelCodes = couponForGetInfo.getChannelCodes();
	        	String limitCondition = couponForGetInfo.getLimitCondition();
	        	String channelLimit = couponForGetInfo.getChannelLimit();
				if((StringUtils.equals(limitCondition, pl)||StringUtils.equals(channelLimit, "4497471600070001")
	        			||StringUtils.contains(channelCodes, channelId))&&!"449715400008".equals(couponForGetInfo.getActivityType())){
					resultList.add(couponForGetInfo);
					}
			}
			
		}
		return resultList;
	}
	

}

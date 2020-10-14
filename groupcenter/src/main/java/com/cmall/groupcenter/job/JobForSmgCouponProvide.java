package com.cmall.groupcenter.job;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.util.CouponUtil;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.service.ProductPriceService;
import com.srnpr.xmassystem.support.PlusSupportUser;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 	给直播品点击过“立即购买”的人发优惠券
 */
public class JobForSmgCouponProvide extends RootJob {
	
	ProductPriceService productPriceService = new ProductPriceService();
	CouponUtil couponUtil = new CouponUtil();
	
	static Map<String,Date> couponDateMap = new HashMap<String,Date>();
	static Map<String,String> couponTypeMap = new HashMap<String,String>();
	
	static ReentrantLock lock = new ReentrantLock();

	public void doExecute(JobExecutionContext context) {
		if(!lock.tryLock()) {
			return;
		}
		
		try {
			doWork();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}
	
	private void doWork() {
		Date now = new Date();
		
		// 90分钟前
		Date befor90 = DateUtils.addMinutes(now, -90);
		// 30分钟前
		Date time30 = DateUtils.addMinutes(now, -30);
		
		MDataMap param = new MDataMap();
		param.put("time90", DateFormatUtils.format(befor90, "yyyy-MM-dd HH:mm:ss"));
		param.put("time30", DateFormatUtils.format(time30, "yyyy-MM-dd HH:mm:ss"));
		
		// 查询90分钟内的扫码购点击购买数据
		// 时间在30分钟后90分钟内
		List<MDataMap> mapList = DbUp.upTable("fh_smg_click_detail").queryAll("zid,member_code,product_code,create_time", "zid desc", "create_time >= :time90 AND create_time <= :time30 AND flag = 0", param);

		String memberCode,productCode,createTime;
		for(MDataMap map : mapList) {
			memberCode = map.get("member_code");
			productCode = map.get("product_code");
			createTime = map.get("create_time");
			
			// 如果不能发券则更新标识状态，不能发券的情况：
			// 1.已经发过券
			// 2.商品下过单
			// 3.内购员工
			if(hasCoupon(memberCode, now, couponDateMap)
					|| hasOrder(memberCode, productCode, createTime)
					|| isNeigouUser(memberCode)) {
				map.put("flag", "2");
				DbUp.upTable("fh_smg_click_detail").dataUpdate(map, "flag", "zid");
				continue;
			}
			
			boolean flag = provideCoupon(memberCode,productCode,couponTypeMap);
			// 赋予成功记录下赋予时间
			if(flag) {
				couponDateMap.put(memberCode, new Date());
				
				map.put("flag", "1");
				DbUp.upTable("fh_smg_click_detail").dataUpdate(map, "flag", "zid");
			}
		}
	}
	
	/**
	 * 是否已经发过优惠券，或在上次发券12小时内
	 */
	private boolean hasCoupon(String memberCode, Date now, Map<String,Date> couponDateMap) {
		Date lastCouponDate = couponDateMap.get(memberCode);
		if(lastCouponDate == null) {
			// 查询最近的一次发券时间
			MDataMap map = DbUp.upTable("fh_smg_coupon").oneWhere("create_time", "zid desc", "", "member_code", memberCode);
			if(map != null) {
				try {
					lastCouponDate = DateUtils.parseDate(map.get("create_time"), "yyyy-MM-dd HH:mm:ss");
					couponDateMap.put(memberCode, lastCouponDate);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		
		return lastCouponDate != null 
				&& (now.getTime() - lastCouponDate.getTime()) < 12*3600000;
	}
	
	/**
	 * 是否已经购买过此商品
	 */
	private boolean hasOrder(String memberCode, String productCode, String createTime) {
		// 查询是否在点击商品后下单过
		String sSql = "SELECT o.order_code FROM oc_orderinfo o, oc_orderdetail d WHERE "
				+ "	o.order_code = d.order_code "
				+ "	and o.buyer_code = :buyer_code "
				+ " and d.product_code = :product_code"
				+ " and o.create_time >= :create_time limit 1";
		return DbUp.upTable("oc_orderinfo").dataSqlOne(sSql, new MDataMap("buyer_code", memberCode, "product_code", productCode, "create_time", createTime)) != null;
	}
	
	// 是否内购员工
	private boolean isNeigouUser(String memberCode) {
		return new PlusSupportUser().upVipType(memberCode);
	}
	
	// 发放优惠券
	private boolean provideCoupon(String memberCode, String productCode, Map<String,String> couponTypeMap) {
		// 查询配置的活动编号
		/*MDataMap defMap = DbUp.upTable("zw_define").one("define_dids", "4699233300060001", "parent_did", "469923330006");
		*/
		
		//569修改为从优惠券相关设置中取维护的活动
		MDataMap defMap = DbUp.upTable("oc_coupon_relative").one("relative_type", "32", "manage_code", "SI2003");
		if(defMap == null || StringUtils.isBlank(defMap.get("activity_code"))) {
			return false;
		}	
		String activityCode = StringUtils.trimToEmpty(defMap.get("activity_code"));
		// 查询活动信息
		MDataMap actMap = DbUp.upTable("oc_activity").oneWhere("", "", "activity_code = :activity_code AND provide_type = '4497471600060005' AND flag = 1 AND begin_time < now() AND end_time > now()", "activity_code", activityCode);
		if(actMap == null) {
			return false;
		}
		
		BigDecimal couponMoney = getCouponMoney(actMap, productCode);
		if(couponMoney == null || couponMoney.compareTo(BigDecimal.ZERO) <= 0) {
			return false;
		}
		
		String couponTypeCode = couponTypeMap.get(productCode);
		if(couponTypeCode == null) {
			couponTypeCode = getCouponType(actMap, productCode, couponMoney);
			
			if(StringUtils.isNotBlank(couponTypeCode)) {
				couponTypeMap.put(productCode, couponTypeCode);
			}
		}
		
		if(couponTypeCode == null) {
			return false;
		}
		
		RootResult res = couponUtil.provideCouponForSmg(memberCode, couponTypeCode, couponMoney);
		
		// 记录发放的优惠券
		String[] split = res.getResultMessage().split(",");
		DbUp.upTable("fh_smg_coupon").dataInsert(new MDataMap(
				"member_code", memberCode,
				"coupon_type_code", couponTypeCode,
				"product_code", productCode,
				"money", couponMoney.intValue()+"",
				"expired_time",split[0],
				"create_time", FormatHelper.upDateTime(),
				"coupon_code",split[1]
				));
		
		return true;
	}
	
	private BigDecimal getCouponMoney(MDataMap actMap,String productCode) {
		PlusModelSkuQuery skuQuery = new PlusModelSkuQuery();
		skuQuery.setCode(productCode);
		Map<String, PlusModelSkuInfo> skuMap = productPriceService.getProductMinPriceSkuInfo(skuQuery);
		PlusModelSkuInfo skuInfo = skuMap.get(productCode);
		
		if(skuInfo == null) {
			return null;
		}
		
		// 毛利 = 售价 - 成本 * 1.1 - 配送费 50 
		BigDecimal profit = skuInfo.getSellPrice().subtract(skuInfo.getCostPrice().multiply(new BigDecimal("1.1"))).subtract(new BigDecimal(50));
		if(profit.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}
		
		BigDecimal rebateRatio = new BigDecimal(actMap.get("rebate_ratio"));
		BigDecimal money = profit.multiply(rebateRatio).divide(new BigDecimal("100"),0,BigDecimal.ROUND_HALF_UP);
		if(money.compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}
		
		return money;
	}
	
	private String getCouponType(MDataMap actMap, String productCode, BigDecimal money) {
		String activityCode = StringUtils.trimToEmpty(actMap.get("activity_code"));
		
		// 查询是否存在类型编号
		String sSql = "SELECT t.coupon_type_code FROM oc_coupon_type t, oc_coupon_type_limit l"
				+ " WHERE t.coupon_type_code = l.coupon_type_code"
				+ " AND t.status = '4497469400030002' AND t.activity_code = :activity_code"
				+ " AND l.product_limit = '4497471600070002' AND l.product_codes = :product_code";
		Map<String, Object> couponTypeObjMap = DbUp.upTable("oc_coupon_type_limit").dataSqlOne(sSql, new MDataMap("activity_code", activityCode, "product_code", productCode));
		if(couponTypeObjMap != null) {
			return (String)couponTypeObjMap.get("coupon_type_code");
		}
		
		String now = FormatHelper.upDateTime();
		
		// 如果不存在则创建一个只能这个商品使用的类型编号
		MDataMap insertMap1 = new MDataMap();
		String coupon_type_code = WebHelper.upCode("CT");
		insertMap1.put("uid", WebHelper.upUuid());
		insertMap1.put("coupon_type_code",coupon_type_code);
		insertMap1.put("coupon_type_name","扫码购用户召回赠送");
		insertMap1.put("activity_code", activityCode);
		insertMap1.put("money", money.toString());
		insertMap1.put("start_time", now);
		insertMap1.put("end_time", now);
		insertMap1.put("status", "4497469400030002");//生成即生效
		insertMap1.put("produce_type", "4497471600040001");//优惠券
		insertMap1.put("limit_condition", "4497471600070002");//指定
		insertMap1.put("limit_scope", "指定商品可用");
		insertMap1.put("valid_type", "4497471600080002");//日期范围
		insertMap1.put("creater", "system");
		insertMap1.put("create_time", now);
		insertMap1.put("updater", "system");
		insertMap1.put("update_time", now);
		insertMap1.put("total_money", "999999999");
		insertMap1.put("surplus_money", "999999999");
		insertMap1.put("money_type", "449748120001");//金额券		
		insertMap1.put("exchange_type", "4497471600390001");
		DbUp.upTable("oc_coupon_type").dataInsert(insertMap1);
		
		MDataMap insertMap2 = new MDataMap();
		insertMap2.put("uid", WebHelper.upUuid());
		insertMap2.put("coupon_type_code", coupon_type_code);
		insertMap2.put("activity_code", activityCode);
		insertMap2.put("product_limit", "4497471600070002");
		insertMap2.put("channel_limit", "4497471600070002");
		insertMap2.put("product_codes", productCode);
		insertMap2.put("channel_codes", "449747430023,449747430003");
		insertMap2.put("create_user", "system");
		insertMap2.put("create_time", now);
		insertMap2.put("update_user", "system");
		insertMap2.put("update_time", now);
		insertMap2.put("brand_limit", "4497471600070001");
		insertMap2.put("category_limit", "4497471600070001");
		
		insertMap2.put("activity_limit", "449747110002");//是否可以参与活动  是
		insertMap2.put("allowed_activity_type", "4497472600010001,4497472600010002,4497472600010004,4497472600010005,4497472600010008,4497472600010015,4497472600010018,4497472600010024,4497472600010030");
		DbUp.upTable("oc_coupon_type_limit").dataInsert(insertMap2);
		
		return coupon_type_code;
	}

}

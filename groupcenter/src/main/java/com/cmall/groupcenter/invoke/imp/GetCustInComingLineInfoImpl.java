package com.cmall.groupcenter.invoke.imp;


import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.client.utils.URLEncodedUtils;

import com.cmall.groupcenter.homehas.HomehasSupport;
import com.cmall.groupcenter.homehas.RsyncGetCustInComingLineInfo;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.util.CouponUtil;
import com.cmall.systemcenter.util.AESUtil;
import com.cmall.systemcenter.util.SmsUtil;
import com.srnpr.xmassystem.invoke.ref.GetCustInComingLineInfo;
import com.srnpr.xmassystem.invoke.ref.model.GetCustInComingLineInfoResult;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelSkuQuery;
import com.srnpr.xmassystem.service.ProductPriceService;
import com.srnpr.xmassystem.service.ShortLinkService;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapcom.topdo.TopConst;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;


/**
 * 查询用户进线信息
 */
public class GetCustInComingLineInfoImpl implements GetCustInComingLineInfo{
	
	static Log log = LogFactory.getLog(GetCustInComingLineInfoImpl.class);
	ProductPriceService productPriceService = new ProductPriceService();
	CouponUtil couponUtil = new CouponUtil();
	static Map<String,String> couponTypeMap = new HashMap<String,String>();
	AESUtil aesUtil = new AESUtil();
	ShortLinkService shortLinkService = new ShortLinkService();
	SmsUtil smsUtil = new SmsUtil();
	@Override
	public GetCustInComingLineInfoResult sendCouponMessageForInComingLineUser() {
    
		GetCustInComingLineInfoResult result  = new GetCustInComingLineInfoResult();
		RsyncGetCustInComingLineInfo comingLineInfo = new RsyncGetCustInComingLineInfo();
		if(!comingLineInfo.doRsync()) return null;
		List<Map<String, String>> resultList = comingLineInfo.upProcessResult().getResultList();
		if(resultList==null||resultList.size()==0) return null;
		aesUtil.initialize();
		List<Map<String, Object>> dataSqlList = DbUp.upTable("oc_incoming_line_recode").dataSqlList("select * from oc_incoming_line_recode order by zid desc limit 0,1", null);
		String sms_channel = "";
		if(dataSqlList==null||dataSqlList.size()==0||dataSqlList.get(0).get("sms_channel").equals("449748740012")) {
			sms_channel = "449748740013";
		}else {
			sms_channel = "449748740012";
		}
		for (Iterator<Map<String, String>> iterator = resultList.iterator();iterator.hasNext();) {
			Map<String, String> map = iterator.next();
			String memberCode = getMemberCode(map.get("mobile"));
			int count2 = DbUp.upTable("oc_incoming_line_recode").count("form_id",map.get("FORM_ID"),"member_code",memberCode);
			if(count2==0) {
				//不存在重复数据，则发券并短信提醒
				MDataMap one = DbUp.upTable("oc_coupon_relative").one("relative_type","33","manage_code","SI2003");
				if(one!=null) {
					//配置了进线损耗的优惠券活动则做发券并提醒的操作
					if("449748740012".equals(sms_channel)){
						provideCouponsForInComingLineUsers(memberCode,map,one.get("activity_code"),couponTypeMap,aesUtil,shortLinkService,smsUtil,sms_channel);
						sms_channel="449748740013";
					}else {
						provideCouponsForInComingLineUsers(memberCode,map,one.get("activity_code"),couponTypeMap,aesUtil,shortLinkService,smsUtil,sms_channel);
						sms_channel="449748740012";
					}
				}
			}
		}
		return result;
	}

	private String getMemberCode(String phone) {
		List<Map<String, Object>> dataSqlList = DbUp.upTable("mc_login_info").dataSqlList("select * from mc_login_info where login_name=:login_name and  manage_code='SI2003' order by zid desc limit 0,1", new MDataMap("login_name",phone));
        if(dataSqlList!=null&&dataSqlList.size()>0) {
        	return dataSqlList.get(0).get("member_code").toString();
        }else {
        	//不存在，则创建
        	HomehasSupport homehasSupport = new HomehasSupport();
			String register = homehasSupport.register(phone,  RandomStringUtils.randomNumeric(8));
			if(StringUtils.isNotBlank(register)) {
				MDataMap map = DbUp.upTable("mc_login_info").one("login_name", phone, "manage_code", MemberConst.MANAGE_CODE_HOMEHAS);
				return map.get("member_code").toString();
			}
        }
		return null;
	}

	private boolean provideCouponsForInComingLineUsers(String memberCode, Map<String, String> map ,String activityCode,Map<String,String> couponTypeMapParam,AESUtil aesUtil,
			ShortLinkService shortLinkService,SmsUtil smsUtil,String  sms_channel) {
		String goodIds = map.get("GOOD_IDS");
		String phoneNum = map.get("mobile");
		Map<String,String> couponTypeMap = new HashMap<>();
		String[] productCodes = goodIds.split(",");
		MDataMap actMap = DbUp.upTable("oc_activity").oneWhere("", "", "activity_code = :activity_code AND provide_type = '4497471600060005' AND flag = 1 AND begin_time < now() AND end_time > now()", "activity_code", activityCode);
		BigDecimal bestCoupon = BigDecimal.ZERO;
		String bestProCode = "";
		BigDecimal totalCouponMoney = BigDecimal.ZERO;
		StringBuffer couponCodes = new StringBuffer();
		Map<String,String> proCouponMap = new HashMap<>();
		if(actMap != null) {
			for (String pCode : productCodes) {
				BigDecimal couponMoney = getCouponMoney(actMap, pCode);
				if(couponMoney != null && couponMoney.compareTo(BigDecimal.ZERO) > 0) {
					String couponTypeCode = couponTypeMap.get(pCode);
					if(couponTypeCode == null) {
						couponTypeCode = getCouponType(actMap, pCode, couponMoney);
						if(StringUtils.isNotBlank(couponTypeCode)) {
							couponTypeMap.put(pCode, couponTypeCode);
						}
					}
					if(couponTypeCode != null) {
						RootResult res = couponUtil.provideCouponForSmg(memberCode, couponTypeCode, couponMoney);
						if(couponMoney.compareTo(bestCoupon)>0) {
							bestCoupon = couponMoney;
							bestProCode = pCode;
						}
						totalCouponMoney = totalCouponMoney.add(couponMoney);
						String couponCode = res.getResultMessage().split(",")[1];
						proCouponMap.put(pCode, couponCode);
						couponCodes.append(couponCode+",");
					}				
				}
			}
			if(bestCoupon.compareTo(BigDecimal.ZERO) > 0) {
				String cCodes = couponCodes.toString().substring(0, couponCodes.toString().lastIndexOf(","));
				String productName = (String)DbUp.upTable("pc_productinfo").dataGet("product_name", "", new MDataMap("product_code", bestProCode));
				String phone_num = aesUtil.encrypt(phoneNum);
				try {
					String encode = URLEncoder.encode(phone_num, TopConst.CONST_BASE_ENCODING);
					
					boolean b=true;
					if("449748740013".equals(sms_channel)) {
						//惠家有短信通路
						String longLink = TopConfig.Instance.bConfig("groupcenter.wei_shop_url_new")+"zh.html?phone="+encode+"&osc=449715190025&pagetype=449748740013&ordertype=449715190053&couponCodes="+cCodes;
						String expireTime = DateUtil.addDateHour(DateUtil.getNowTime(), 24);
						String shortLink = shortLinkService.createShortLink(longLink, "system", expireTime);
						b = smsUtil.sendSmsForYX(phoneNum, FormatHelper.formatString(TopConfig.Instance.bConfig("groupcenter.smg_coupon_zhaohui_new"), totalCouponMoney, productName,bestCoupon,shortLink));
						if(b) {
							 for(Iterator<String> iterator = proCouponMap.keySet().iterator();iterator.hasNext();) {
								 String pCode = iterator.next();
								 String couponCode = proCouponMap.get(pCode);
								 DbUp.upTable("oc_incoming_line_recode").dataInsert(new MDataMap("uid",WebHelper.upUuid(),"form_id",map.get("FORM_ID"),"product_code",pCode,"member_code",memberCode,"coupon_code",couponCode,"sms_channel","449748740013"));
							 }
						}else {
							// 发送失败
							log.warn("GetCustInComingLineInfoImpl->smsUtil.sendSmsForYX -> failed!" + phoneNum);
						}	
					}else {
						//集团短信通路
						String longLink = TopConfig.Instance.bConfig("groupcenter.wei_shop_url_new")+"zh.html?phone="+encode+"&osc=449715190025&pagetype=449748740012&ordertype=449715190054&couponCodes="+cCodes;
						String expireTime = DateUtil.addDateHour(DateUtil.getNowTime(), 24);
						String shortLink = shortLinkService.createShortLink(longLink, "system", expireTime);
						b = smsUtil.sendSmsForCompany(phoneNum, FormatHelper.formatString(TopConfig.Instance.bConfig("groupcenter.smg_coupon_zhaohui_new"), totalCouponMoney, productName,bestCoupon,shortLink));
						if(b) {
							 for(Iterator<String> iterator = proCouponMap.keySet().iterator();iterator.hasNext();) {
								 String pCode = iterator.next();
								 String couponCode = proCouponMap.get(pCode);
								 DbUp.upTable("oc_incoming_line_recode").dataInsert(new MDataMap("uid",WebHelper.upUuid(),"form_id",map.get("FORM_ID"),"product_code",pCode,"member_code",memberCode,"coupon_code",couponCode,"sms_channel","449748740012"));
							 }
						}else {
							// 发送失败
							log.warn("GetCustInComingLineInfoImpl->smsUtil.sendSmsForCompany  -> failed!" + phoneNum );
						}	
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	
			}else {
				return false;
			}
		}else {
			return false;
		}
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
		insertMap1.put("coupon_type_name","进线损耗用户召回赠送");
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

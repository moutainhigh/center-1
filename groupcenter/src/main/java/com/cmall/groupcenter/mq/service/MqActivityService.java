package com.cmall.groupcenter.mq.service;

import java.math.BigDecimal;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.mq.model.ActivityListenModel;
import com.cmall.groupcenter.mq.model.ActivityTypeListenModel;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topdo.TopUp;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

public class MqActivityService {

	/**
	 * 同步活动
	 * @param activity
	 * @return
	 */
	public MWebResult reginRsyncActivity(ActivityListenModel activity) {
		
		MWebResult mWebResult = new MWebResult();
		//oc_activity
		String activity_name = activity.getEvent_nm() == null ? "" : activity.getEvent_nm().toString(); //活动名称
		String activity_type = "449715400007"; //活动类型：优惠券
		String seller_code = "SI2003"; //固定值
		String begin_time = activity.getFr_date() == null ? "" : activity.getFr_date().toString();//开始时间
		String end_time = activity.getEnd_date() == null ? "" : activity.getEnd_date().toString();//结束时间
		String flag = "0";//是否启用状态 
		String vl_yn = activity.getVl_yn() == null ? "" : activity.getVl_yn().toString();
		String event_cd = activity.getEvent_cd() == null ? "" : activity.getEvent_cd().toString();// LD活动类型
		String provide_type = "";//优惠券发放类型
		if(event_cd.equals("70") || event_cd.equalsIgnoreCase("ZD")) {
			provide_type = "4497471600060002"; //系统发放
		}else if(event_cd.equals("80")) {
			provide_type = "4497471600060001"; //人工发放
		}
		String assign_trigger = ""; //发放条件
		String break_blocked = "4497471600330001"; //解冻条件
		String blocked = "4497471600320002"; // 是否冻结
		String newer_given = "449746250002"; //仅新用户发放
		String provide_num = "9999999"/*activity.getOrd_cnt() == null ? "0" : activity.getOrd_cnt().toString()*/; //优惠券发放份数
		String remark = activity.getEvent_desc() == null ? "" : activity.getEvent_desc().toString(); //活动描述
		String creator = "ld";/*activity.getEtr_id() == null ? "" : activity.getEtr_id().toString();*/ //创建人
		String create_time = activity.getEtr_date() == null ? "" : activity.getEtr_date().toString(); //创建时间
		String updator = activity.getMdf_id() == null ? "" : activity.getMdf_id().toString(); // 修改人
		String update_time = activity.getMdf_date() == null ? "" : activity.getMdf_date().toString(); //修改日期
		String sku_pricepercent = "0"; //折扣百分比 写死0
		String provide_person = "ld"; //发放人员 写死
		String provide_department = activity.getDept_event() == null ? "" : activity.getDept_event();//发放部门
		String out_activity_code = activity.getEvent_id() == null ? "" : activity.getEvent_id().toString(); //LD活动编号，同步优惠券明细时根据LD活动编号来获取活动和优惠券类型
		String is_multi_use = activity.getIs_superposition() == null ? "N" : activity.getIs_superposition().toString().toUpperCase(); //是否可以叠加使用
		String disup_amt = activity.getDisup_amt() == null ? "0" : activity.getDisup_amt().toString(); //叠加使用上限
		String minlimit_tp = activity.getMinlimit_tp() == null ? "00" : activity.getMinlimit_tp().toString(); //最低金额限制方式
		String minlimit_amt = activity.getMinlimit_amt() == null ? "0" : activity.getMinlimit_amt().toString(); //礼金/商品金额占比
		String is_onelimit = activity.getIs_onelimit() == null ? "N" : "Y".equals(activity.getIs_onelimit().toString().toUpperCase()) ? "Y" : "N"; //单张使用时不受以上叠加规则限制
		String mindis_amt = activity.getMindis_amt() == null ? "0" : activity.getMindis_amt().toString(); //立减金额以下不受最低金额限制
		String is_change = activity.getIs_change() == null ? "N" : activity.getIs_change().toString().toUpperCase(); //是否允许找零
		
		//oc_coupon_type
		String dis_type = activity.getDis_type() == null ? "" : activity.getDis_type().toString();// LD优惠券类型
		String money_type = ""; //优惠券金额类型 改为异步补充
		String money = "0"; //面值
		String total_money = "99999999"; //成本限额
		String surplus_money = "99999999"; //剩余金额
		String limit_money = "0"; //使用下限金额
		String start_time = ""; //开始时间
		String type_end_time = ""; //结束时间
		if(event_cd.equals("80")) {
			String dis_money = activity.getDis_amt() == null ? "" : activity.getDis_amt().toString();
			if(StringUtils.isNotBlank(dis_money) && "10".equals(dis_type)) {
				money = dis_money;
				money_type = "449748120001"; //金额券
			}
			if(StringUtils.isNotBlank(dis_money) && "20".equals(dis_type)) {
				money = new BigDecimal(dis_money).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_HALF_DOWN).toString();
				money_type = "449748120002"; //折扣券
			}
			limit_money = activity.getLow_amt() == null ? "0" : activity.getLow_amt().toString();
			start_time = activity.getOrd_fr_date() == null ? "" : activity.getOrd_fr_date().toString();
			type_end_time = activity.getOrd_end_date() == null ? "" : activity.getOrd_end_date().toString();
		}else if(event_cd.equals("70") || event_cd.equalsIgnoreCase("ZD")) {
			start_time = begin_time;
			type_end_time = end_time;
		}
		String status = "4497469400030001"; //是否启用
		String produce_type = "4497471600040001"; // 生成类型 优惠券 优惠码  TODO 写死1  不然再兑换时会有问题
		String good_yn = activity.getGood_yn() == null ? "" : activity.getGood_yn().toString(); //商品限定类型 “01,02,10是限定，其他无限制”
		String limit_condition = "4497471600070002"; //条件限定 指定 TODO 因为LD同步过来的活动 都存在商户限制 所以限制条件都为指定
		String limit_scope = "自营品可用（促销商品除外）"; //使用范围
		String limit_explain = ""; //使用说明
		
		//oc_coupon_type_limit
		String goodlimit = activity.getGoodlimit() == null ? "" : activity.getGoodlimit().toString(); //限定的商品集合
		String category_nm = activity.getClasslimit() == null ? "" : activity.getClasslimit().toString(); //限定的品类集合
		String goodnojoin = activity.getGoodnojoin() == null ? "" : activity.getGoodnojoin().toString(); //禁止的商品集合
		String lrgn_codes = activity.getLrgn_codes() == null ? "" : activity.getLrgn_codes().toString();//区域限制编号
		String cust_lvl_codes = activity.getCust_lvl_codes() == null ? "" : activity.getCust_lvl_codes().toString();//客户等级允许编号
		String cust_codes = activity.getCust_codes() == null ? "" : activity.getCust_codes().toString();//客户限制编号
		String pay_codes = activity.getPay_codes() == null ? "" : activity.getPay_codes().toString();//支付方式限制编号
		String seller_limit = "449748230002"; //商户限制 仅LD品可用
		
		//oc_coupon_cdkey
		String multi_account = "449746250001";//允许多账户使用
		String cdkey = activity.getCoupus_no() == null ? "" : activity.getCoupus_no().toString();//优惠券码
		String use_people = provide_num; //订单最大数量 
		
		//oc_cdkey_provide
		String task_status = "2"; //状态 2已执行
		
		MDataMap aActivityMap= new MDataMap();
		aActivityMap.put("activity_name", activity_name);
		aActivityMap.put("activity_type", activity_type);
		aActivityMap.put("seller_code", seller_code);
		aActivityMap.put("begin_time", begin_time);
		aActivityMap.put("end_time", end_time);
		aActivityMap.put("flag", flag);
		aActivityMap.put("provide_type", provide_type);
		aActivityMap.put("assign_trigger", assign_trigger);
		aActivityMap.put("break_blocked", break_blocked);
		aActivityMap.put("blocked", blocked);
		aActivityMap.put("newer_given", newer_given);
		aActivityMap.put("provide_num", provide_num);
		aActivityMap.put("remark", remark);
		aActivityMap.put("creator", creator);
		aActivityMap.put("create_time", create_time);
		aActivityMap.put("updator", updator);
		aActivityMap.put("update_time", update_time);
		aActivityMap.put("sku_pricepercent", sku_pricepercent);
		aActivityMap.put("provide_person", provide_person);
		aActivityMap.put("provide_department", provide_department);
		aActivityMap.put("out_activity_code", out_activity_code);
		aActivityMap.put("is_multi_use", is_multi_use);
		aActivityMap.put("disup_amt", disup_amt);
		aActivityMap.put("minlimit_tp", minlimit_tp);
		aActivityMap.put("minlimit_amt", minlimit_amt);
		aActivityMap.put("is_onelimit", is_onelimit);
		aActivityMap.put("mindis_amt", mindis_amt);
		aActivityMap.put("is_change", is_change);
		
		//限定字段存放在表oc_coupon_type和oc_coupon_type_limit中
		MDataMap mActivityMap = DbUp.upTable("oc_activity").one("out_activity_code", out_activity_code); 		
		if(mActivityMap == null) {
			String activity_code = WebHelper.upCode("AC");
			aActivityMap.put("activity_code", activity_code);
			DbUp.upTable("oc_activity").dataInsert(aActivityMap);
			
			//oc_coupon_type  规则：一个活动对应一个优惠券类型，每个优惠券类型可以有多张优惠券
			String coupon_type_code = insertCouponType(activity_name, activity_code, money, limit_money, start_time, type_end_time, status, 
										produce_type, limit_condition, limit_scope, limit_explain, creator, create_time, updator, update_time, money_type,total_money,surplus_money);	
			//oc_coupon_type_limit
			insertCouponTypeLimit(good_yn, coupon_type_code, activity_code, goodlimit, category_nm, goodnojoin, lrgn_codes, cust_lvl_codes,
					cust_codes, pay_codes, creator, create_time, updator, update_time, seller_limit);		
			
			//oc_coupon_cdkey
			if("80".equals(activity.getEvent_cd())){
				insertCouponCdkey(multi_account, cdkey, use_people, activity_code, creator, create_time, updator, update_time);
				insertCouponCdkeyProvide(cdkey, use_people, activity_code, creator, create_time,task_status);
			}
		} else {
			
			DbUp.upTable("oc_activity").dataUpdate(aActivityMap,
					"activity_name,activity_type,seller_code,begin_time,end_time,"
							+ "flag,provide_type,provide_num,remark,updator,update_time,sku_pricepercent,provide_person,"
							+ "provide_department,is_multi_use,disup_amt,minlimit_tp,minlimit_amt,is_onelimit,mindis_amt,is_change","out_activity_code");
			String activity_code = mActivityMap.get("activity_code").toString();
			
			String title = "编号为:" + activity_code + "的活动存在变动";
			String content = "";
			if("N".equals(vl_yn)) {
				content += "\r\n<br/>•编号为:" + activity_code + "的活动在LD已失效,请确认失效原因后再进行后续操作!";
			}
			content += "\r\n<br/>•编号为:" + activity_code + "的活动存在变动，活动已下架，请及时作出相应修改后重新发布!";
			
			String coupon_type_code = "";
			MDataMap mCouponTypeMap = DbUp.upTable("oc_coupon_type").one("activity_code", activity_code, "creater", creator);
			if(mCouponTypeMap == null) {
				coupon_type_code = insertCouponType(activity_name, activity_code, money, limit_money, start_time, type_end_time, status, 
											produce_type, limit_condition, limit_scope, limit_explain, creator, create_time, updator, update_time, money_type,total_money,surplus_money);
			} else {
				coupon_type_code = mCouponTypeMap.get("coupon_type_code").toString();
				updateCouponType(coupon_type_code,activity_name, activity_code, money, limit_money, start_time, type_end_time,
						status, produce_type, limit_condition, updator, update_time, money_type,event_cd);
				content += "\r\n<br/>•编号为:" + coupon_type_code + "的优惠券类型存在变动，优惠券类型已下架，请及时作出相应修改后重新发布!";
			}
			
			MDataMap mCouponTypeLimitMap = DbUp.upTable("oc_coupon_type_limit").one("activity_code", activity_code, "coupon_type_code", coupon_type_code);
			if(mCouponTypeLimitMap == null) {
				insertCouponTypeLimit(good_yn, coupon_type_code, activity_code, goodlimit, category_nm, goodnojoin, lrgn_codes, cust_lvl_codes,
						cust_codes, pay_codes, creator, create_time, updator, update_time, seller_limit);	
			} else {
				//修改时 不修改seller_limit
				String limitContent = updateCouponTypeLimit(good_yn, coupon_type_code, activity_code, goodlimit, category_nm, goodnojoin, lrgn_codes, cust_lvl_codes,
						cust_codes, pay_codes, updator, update_time,mCouponTypeLimitMap.get("category_limit"), mCouponTypeLimitMap.get("category_nm"));
				if(StringUtils.isNotBlank(limitContent)) {
					content += limitContent;
				}
			}
			
			if("80".equals(event_cd)){
				MDataMap cdkeyMap = DbUp.upTable("oc_coupon_cdkey").one("activity_code", activity_code,"create_user",creator);
				if(null == cdkeyMap) {
					insertCouponCdkey(multi_account, cdkey, use_people, activity_code, creator, create_time, updator, update_time);
				}else {
					updateCouponCdkey(multi_account, cdkey, use_people, activity_code, creator, updator, update_time);
				}
				
				MDataMap cdkeyProvideMap = DbUp.upTable("oc_cdkey_provide").one("activity_code", activity_code,"create_user",creator);
				if(null == cdkeyProvideMap) {
					insertCouponCdkeyProvide(cdkey, use_people, activity_code, creator, create_time,task_status);
				}else {
					updateCouponCdkProvide(cdkey, use_people, activity_code, creator, task_status);
				}
			}
			
			//发送变更邮件 仅在存在优惠券类型时发送邮件 否则即使发送邮件也更改不了
			if(null != mCouponTypeMap && StringUtils.isNotBlank(mCouponTypeMap.get("money_type"))
					&& StringUtils.isNotEmpty(TopUp.upConfig("groupcenter.ld_activity_category_limit_change_email_addr"))) {
				MailSupport mailSupport = new MailSupport();
				mailSupport.sendMail(TopUp.upConfig("groupcenter.ld_activity_category_limit_change_email_addr"), title, content);
			}
			
		}		
		
		return mWebResult;
	}
	
	public MWebResult reginRsyncActivityType(ActivityTypeListenModel activityType) {
		MWebResult result = new MWebResult();
		
		String out_activity_code = activityType.getEvent_id() == null ? "" : activityType.getEvent_id().toString(); //外部活动编号
		String dis_type = activityType.getDis_type() == null ? "" : activityType.getDis_type().toString(); //折扣类型
		
		MDataMap mActivityMap = DbUp.upTable("oc_activity").one("out_activity_code", out_activity_code); 		
		if(mActivityMap == null || StringUtils.isBlank(mActivityMap.get("activity_code"))) {
			result.setResultCode(0);
			result.setResultMessage("活动不存在!");
		}
		
		String activity_code = mActivityMap.get("activity_code");
		String money_type = "10".equals(dis_type) ? "449748120001" : "449748120002";
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("activity_code", activity_code);
		mDataMap.put("money_type", money_type);
		DbUp.upTable("oc_coupon_type").dataUpdate(mDataMap, "money_type", "activity_code");
		
		if(StringUtils.isNotEmpty(TopUp.upConfig("groupcenter.ld_activity_category_limit_change_email_addr"))) {
			
			String title = "有新的LD同步到惠家有的活动" + activity_code + "";
			String content = "有新的LD同步到惠家有的活动，活动编号为：" + activity_code + "，可以登录惠家有后台进行配置并发布了！";
			MailSupport mailSupport = new MailSupport();
			mailSupport.sendMail(TopUp.upConfig("groupcenter.ld_activity_category_limit_change_email_addr"), title, content);
		}
		
		return result;
	}
	
	private void updateCouponCdkProvide(String cdkey, String use_people, String activity_code, String creator, String task_status) {
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("activity_code", activity_code);
		mDataMap.put("cdkey", cdkey);
		mDataMap.put("use_people", use_people);
		mDataMap.put("create_user", creator);
		mDataMap.put("task_status", task_status);
		DbUp.upTable("oc_cdkey_provide").dataUpdate(mDataMap, "cdkey,use_people,task_status", "activity_code,create_user");
	}

	private void insertCouponCdkeyProvide(String cdkey, String use_people, String activity_code, String creator,
			String create_time, String task_status) {
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("activity_code", activity_code);
		mDataMap.put("cdkey", cdkey);
		mDataMap.put("use_people", use_people);
		mDataMap.put("create_time", create_time);
		mDataMap.put("create_user", creator);
		mDataMap.put("task_status", task_status);
		DbUp.upTable("oc_cdkey_provide").dataInsert(mDataMap);
	}

	private void updateCouponType(String coupon_type_code, String activity_name, String activity_code, String money, String limit_money, String start_time, String type_end_time, 
			String status, String produce_type, String limit_condition, String updator, String update_time,String money_type, String event_cd) {
		
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("coupon_type_code", coupon_type_code);
		mDataMap.put("coupon_type_name", activity_name);
		mDataMap.put("activity_code", activity_code);
		mDataMap.put("money", money);
		mDataMap.put("limit_money", limit_money);
		mDataMap.put("start_time", start_time);
		mDataMap.put("end_time", type_end_time);
		mDataMap.put("status", status);
		mDataMap.put("produce_type", produce_type);
		mDataMap.put("limit_condition", limit_condition);
		mDataMap.put("updater", updator);
		mDataMap.put("update_time", update_time);
		
		if("80".equals(event_cd)) {//只有优惠券在修改活动时才更改券类型
			mDataMap.put("money_type", money_type);
			DbUp.upTable("oc_coupon_type").dataUpdate(mDataMap,"coupon_type_name,money,limit_money,start_time,end_time,status,produce_type,limit_condition,"
					+ "updater,update_time,money_type","coupon_type_code");
		}else {//礼金券修改活动时不修改券类型
			DbUp.upTable("oc_coupon_type").dataUpdate(mDataMap,"coupon_type_name,money,limit_money,start_time,end_time,status,produce_type,limit_condition,"
					+ "updater,update_time","coupon_type_code");
		}
		
		
	}

	private String updateCouponTypeLimit(String good_yn, String coupon_type_code, String activity_code, String goodlimit, String category_nm, String goodnojoin, 
			String lrgn_codes, String cust_lvl_codes, String cust_codes,String pay_codes, String updator, String update_time, String old_category_limit, String old_category_nm) {
		
		String content = "";
		
		String brand_limit = "4497471600070001";
		String product_limit = "4497471600070001";
		String category_limit = "4497471600070001";
		String channel_limit = "4497471600070001";
		String activity_limit = "449747110001";  //礼金券默认不参加活动
		String lrgn_limit = "4497471600070001";
		String cust_lvl_limit = "4497471600070001";
		String cust_limit = "4497471600070001";
		String pay_limit = "4497471600070001";
		String except_brand = "0";
		String except_product = "0";
		String except_category = "0";
		String except_channel = "0";
		String brand_codes = "";
		String product_codes= "";
		String category_codes = "";
		String channel_codes = "";
		if(good_yn.equals("01") && StringUtils.isNotBlank(goodlimit)) {
			product_limit = "4497471600070002";	
			product_codes = goodlimit;
		}else if(good_yn.equals("02") && StringUtils.isNotBlank(category_nm)) {
			category_limit = "4497471600070002";
		}else if(good_yn.equals("10") && StringUtils.isNotBlank(goodnojoin)) {
			product_limit = "4497471600070002";
			except_product = "1";
			product_codes = goodnojoin;
		}
		
		if(!"".equals(lrgn_codes)) {
			lrgn_limit = "4497471600070002";
		}
		if(!"".equals(cust_lvl_codes)) {
			cust_lvl_limit = "4497471600070002";
		}
		if(!"".equals(cust_codes)) {
			cust_limit = "4497471600070002";
		}
		if(!"".equals(pay_codes)) {
			pay_limit = "4497471600070002";
		}
		
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("coupon_type_code", coupon_type_code);
		mDataMap.put("activity_code", activity_code);
		mDataMap.put("brand_limit", brand_limit);
		mDataMap.put("product_limit", product_limit);
		mDataMap.put("category_limit", category_limit);
		mDataMap.put("channel_limit", channel_limit);
		mDataMap.put("activity_limit", activity_limit);
		mDataMap.put("except_brand", except_brand);
		mDataMap.put("except_product", except_product);
		mDataMap.put("except_category", except_category);
		mDataMap.put("except_channel", except_channel);
		mDataMap.put("brand_codes", brand_codes);
		mDataMap.put("product_codes", product_codes);
		mDataMap.put("channel_codes", channel_codes);
		mDataMap.put("update_user", updator);
		mDataMap.put("update_time", update_time);
		mDataMap.put("lrgn_limit", lrgn_limit);
		mDataMap.put("lrgn_codes", lrgn_codes);
		mDataMap.put("cust_lvl_limit", cust_lvl_limit);
		mDataMap.put("cust_lvl_codes", cust_lvl_codes);
		mDataMap.put("cust_limit", cust_limit);
		mDataMap.put("cust_codes", cust_codes);
		mDataMap.put("pay_limit", pay_limit);
		mDataMap.put("pay_codes", pay_codes);
		mDataMap.put("category_nm", category_nm);
		mDataMap.put("good_yn", good_yn);
		
		if("4497471600070002".equals(old_category_limit) && "4497471600070001".equals(category_limit)) {
			//限制类型变更为非品类限制 抹掉原有配置的品类限定
			mDataMap.put("category_codes", category_codes);
			DbUp.upTable("oc_coupon_type_limit").dataUpdate(mDataMap, "brand_limit,product_limit,category_limit,channel_limit,activity_limit,except_brand,except_product,"
					+ "except_category,except_channel,brand_codes,product_codes,category_codes,channel_codes,update_user,update_time,lrgn_limit,lrgn_codes,cust_lvl_limit,cust_lvl_codes,"
					+ "cust_limit,cust_codes,pay_limit,pay_codes,category_nm,good_yn", "activity_code,coupon_type_code");
		}else {
			DbUp.upTable("oc_coupon_type_limit").dataUpdate(mDataMap, "brand_limit,product_limit,category_limit,channel_limit,activity_limit,except_brand,except_product,"
					+ "except_category,except_channel,brand_codes,product_codes,channel_codes,update_user,update_time,lrgn_limit,lrgn_codes,cust_lvl_limit,cust_lvl_codes,"
					+ "cust_limit,cust_codes,pay_limit,pay_codes,category_nm,good_yn", "activity_code,coupon_type_code");
			
		}
		
		if("4497471600070001".equals(old_category_limit) && "4497471600070002".equals(category_limit)) {
			content = "\r\n<br/>•编号为:" + activity_code + "的活动限制类型变更为品类限定，类型变更为:\r\n<br/>" + category_nm + "\r\n<br/>请登录惠家有管理后台进行相应配置";
		}
		if("4497471600070002".equals(old_category_limit) && "4497471600070002".equals(category_limit) && !old_category_nm.equals(category_nm)) {
			content = "\r\n<br/>•编号为:" + activity_code + "的活动品类限定存在变动,类型变更为:\r\n<br/>" + category_nm + "\r\n<br/>请登录惠家有管理后台进行相应配置";
		}
		
		return content;
	}

	private void insertCouponTypeLimit(String good_yn, String coupon_type_code, String activity_code, String goodlimit,String category_nm, String goodnojoin, 
			String lrgn_codes, String cust_lvl_codes, String cust_codes, String pay_codes, String creator, String create_time, String updator, String update_time, String seller_limit) {
		String brand_limit = "4497471600070001";
		String product_limit = "4497471600070001";
		String category_limit = "4497471600070001";
		String channel_limit = "4497471600070001";
		String activity_limit = "449747110001";  //礼金券默认不参加活动
		String lrgn_limit = "4497471600070001";
		String cust_lvl_limit = "4497471600070001";
		String cust_limit = "4497471600070001";
		String pay_limit = "4497471600070001";
		String except_brand = "0";
		String except_product = "0";
		String except_category = "0";
		String except_channel = "0";
		String brand_codes = "";
		String product_codes= "";
		String category_codes = "";
		String channel_codes = "";
		if(good_yn.equals("01") && StringUtils.isNotBlank(goodlimit)) {
			product_limit = "4497471600070002";	
			product_codes = goodlimit;
		}else if(good_yn.equals("02") && StringUtils.isNotBlank(category_nm)) {
			category_limit = "4497471600070002";
		}else if(good_yn.equals("10") && StringUtils.isNotBlank(goodnojoin)) {
			product_limit = "4497471600070002";
			except_product = "1";
			product_codes = goodnojoin;
		}
		
		if(!"".equals(lrgn_codes)) {
			lrgn_limit = "4497471600070002";
		}
		if(!"".equals(cust_lvl_codes)) {
			cust_lvl_limit = "4497471600070002";
		}
		if(!"".equals(cust_codes)) {
			cust_limit = "4497471600070002";
		}
		if(!"".equals(pay_codes)) {
			pay_limit = "4497471600070002";
		}
		
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("coupon_type_code", coupon_type_code);
		mDataMap.put("activity_code", activity_code);
		mDataMap.put("brand_limit", brand_limit);
		mDataMap.put("product_limit", product_limit);
		mDataMap.put("category_limit", category_limit);
		mDataMap.put("channel_limit", channel_limit);
		mDataMap.put("activity_limit", activity_limit);
		mDataMap.put("except_brand", except_brand);
		mDataMap.put("except_product", except_product);
		mDataMap.put("except_category", except_category);
		mDataMap.put("except_channel", except_channel);
		mDataMap.put("brand_codes", brand_codes);
		mDataMap.put("product_codes", product_codes);
		mDataMap.put("category_codes", category_codes);
		mDataMap.put("channel_codes", channel_codes);
		mDataMap.put("create_user", creator);
		mDataMap.put("create_time", create_time);
		mDataMap.put("update_user", updator);
		mDataMap.put("update_time", update_time);
		mDataMap.put("lrgn_limit", lrgn_limit);
		mDataMap.put("lrgn_codes", lrgn_codes);
		mDataMap.put("cust_lvl_limit", cust_lvl_limit);
		mDataMap.put("cust_lvl_codes", cust_lvl_codes);
		mDataMap.put("cust_limit", cust_limit);
		mDataMap.put("cust_codes", cust_codes);
		mDataMap.put("pay_limit", pay_limit);
		mDataMap.put("pay_codes", pay_codes);
		mDataMap.put("category_nm", category_nm);
		mDataMap.put("good_yn", good_yn);
		mDataMap.put("seller_limit", seller_limit);
		
		DbUp.upTable("oc_coupon_type_limit").dataInsert(mDataMap);
	}

	private String insertCouponType(String activity_name, String activity_code, String money, String limit_money, String start_time, String type_end_time, String status, 
			String produce_type, String limit_condition, String limit_scope, String limit_explain, String creator, String create_time, String updator, String update_time, String money_type, String total_money, String surplus_money) {

		String coupon_type_code = WebHelper.upCode("CT");
		
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("coupon_type_code", coupon_type_code);
		mDataMap.put("coupon_type_name", activity_name);
		mDataMap.put("activity_code", activity_code);
		mDataMap.put("money", money);
		mDataMap.put("total_money", total_money);
		mDataMap.put("surplus_money", surplus_money);
		mDataMap.put("limit_money", limit_money);
		mDataMap.put("start_time", start_time);
		mDataMap.put("end_time", type_end_time);
		mDataMap.put("status", status);
		mDataMap.put("produce_type", produce_type);
		mDataMap.put("limit_condition", limit_condition);
		mDataMap.put("limit_scope", limit_scope);
		mDataMap.put("limit_explain", limit_explain);
		mDataMap.put("creater", creator);
		mDataMap.put("create_time", create_time);
		mDataMap.put("updater", updator);
		mDataMap.put("update_time", update_time);
		mDataMap.put("money_type", money_type);
		
		DbUp.upTable("oc_coupon_type").dataInsert(mDataMap);
		
		return coupon_type_code;
	}

	private void updateCouponCdkey(String multi_account, String cdkey, String use_people, String activity_code, String creator, String updator, String update_time) {
		MDataMap params = new MDataMap();
		params.put("multi_account", multi_account);
		params.put("cdkey", cdkey);
		params.put("use_people", use_people);
		params.put("activity_code", activity_code);
		params.put("create_user", creator);
		params.put("update_time", update_time);
		params.put("update_user", updator);
		DbUp.upTable("oc_coupon_cdkey").dataUpdate(params, "multi_account,cdkey,use_people,update_user,update_time", "activity_code,create_user");
	}

	private void insertCouponCdkey(String multi_account, String cdkey, 
			String use_people, String activity_code, String creator, String create_time,String updator, String update_time) {
		MDataMap params = new MDataMap();
		params.put("multi_account", multi_account);
		params.put("cdkey", cdkey);
		params.put("use_people", use_people);
		params.put("activity_code", activity_code);
		params.put("create_time", create_time);
		params.put("create_user", creator);
		params.put("update_user", updator);
		params.put("update_time", update_time);
		DbUp.upTable("oc_coupon_cdkey").dataInsert(params);
	}
}

package com.cmall.groupcenter.job;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.util.CouponUtil;
import com.cmall.systemcenter.common.CouponConst;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * @ClassName: JobUserCoupon
 * @Description: 定时扫描待发送优惠券用户发送优惠券
 * @author 张海生
 * @date 2015-4-9 下午2:46:24
 * 
 */
public class JobUserCoupon extends RootJob {
	
	static Object lock = new Object();

	public void doExecute(JobExecutionContext context) {
		// String Lockcode = WebHelper.addLock(10000,"coupon136586");
		synchronized (lock) {
			// 查询未发放优惠券的用户，只查询24小时内待发送优惠券信息
			int startZid = NumberUtils.toInt(bConfig("ordercenter.oc_coupon_provide_start_zid"),0);
			List<MDataMap> dataList = DbUp.upTable("oc_coupon_provide").queryAll("mobile,coupon_type_code,uq_code,manage_code,"
					+ "validate_flag,blocked,big_order_code,create_time","zid asc","status = 0 and create_time >= DATE_SUB(NOW(),INTERVAL 1 DAY) and zid >= :startZid", new MDataMap("startZid",startZid+""));
			
			CouponUtil up = new CouponUtil();
			int couponCount = Integer.parseInt(bConfig("groupcenter.coupon_count"));
			if (dataList != null && dataList.size() > 0) {
				//String sSql = "select distinct coupon_type_code from oc_coupon_provide where status = 0 and create_time >= DATE_SUB(NOW(),INTERVAL 1 DAY) and zid >= :startZid";
				//List<Map<String, Object>> listMap = DbUp.upTable("oc_coupon_provide").dataSqlList(sSql, new MDataMap("startZid", startZid+""));
				// 用于存放每种类型优惠券发放个数
				Map<String, Integer> typeMap = new HashMap<String, Integer>();
				//for (Map<String, Object> map : listMap) {
				//	typeMap.put((String) map.get("coupon_type_code"), 0);
				//}
				
				// 优惠券已发放数量
				Map<String, Integer> typeCountMap = new HashMap<String, Integer>();
				// 总发放张数
				Map<String, Integer> totalTypeCountMap = new HashMap<String, Integer>();
				try {
					Integer count = null;
					Integer provideNum = null;
					for (MDataMap mDataMap : dataList) {
						// 查询系统里是否有该用户
						String mobile = mDataMap.get("mobile");
						MDataMap udata = DbUp.upTable("mc_login_info").oneWhere("member_code", null, null, "login_name", mobile, 
								"manage_code", mDataMap.get("manage_code"));
						if (udata != null) {
							String memberCode = udata.get("member_code");
							String couponTypeCode = mDataMap.get("coupon_type_code");
							String blocked = mDataMap.get("blocked");
							String bigOrderCode = mDataMap.get("big_order_code");
							
							if("1".equals(mDataMap.get("validate_flag"))) {
								//查询优惠券表里是否已经有用户用户优惠券
								int pCount = DbUp.upTable("oc_coupon_info").count("member_code", memberCode, "coupon_type_code", couponTypeCode);
								if (pCount > 0)
									continue;
							}
							
							count = typeCountMap.get(couponTypeCode);
							provideNum = totalTypeCountMap.get(couponTypeCode);
							if(count == null){
								MDataMap typeCodeMap = DbUp.upTable("oc_coupon_type").one("coupon_type_code", couponTypeCode);
								if("449748120002".equals(typeCodeMap.get("money_type"))){
									// 折扣券直接取张数字段
									count = new BigDecimal(typeCodeMap.get("privide_money")+"").intValue();
								}else{
									// 其他券根据已发放金额除以面额计算出已发放张数
									count = new BigDecimal(typeCodeMap.get("privide_money")+"").divide(new BigDecimal(typeCodeMap.get("money")+""),0,BigDecimal.ROUND_HALF_UP).intValue();
								}
								typeCountMap.put(couponTypeCode, count);
								
								// 取活动上面设置的总张数
								MDataMap m = DbUp.upTable("oc_activity").oneWhere("provide_type,provide_num", "", "", "activity_code", typeCodeMap.get("activity_code"));
								if(m != null && "4497471600060002".equals(m.get("provide_type"))){ // 默认只对系统发送的类型做数量限制
									provideNum = NumberUtils.toInt(m.get("provide_num"));
								}else{
									provideNum = Integer.MAX_VALUE;
								}
								
								totalTypeCountMap.put(couponTypeCode, provideNum);
							}
							
							// 优惠券已经发完则不再下发
							if(count >= provideNum){
								continue;
							}
							
							int flag = 0;
							for (int i = 0; i < couponCount; i++) {
								flag = up.provideCoupon(memberCode, couponTypeCode, blocked, bigOrderCode, "");// 插入用户优惠记录
								if (flag == 0)
									break;
							}
							if (flag == 1) {
								// 设置初始值
								if(typeMap.get(couponTypeCode) == null){
									typeMap.put(couponTypeCode, 0);
								}
								
								typeMap.put(couponTypeCode,
										typeMap.get(couponTypeCode) + 1);
								MDataMap upData = new MDataMap();
								upData.put("status", "1");
								upData.put("provide_time", DateUtil.getNowTime());
								upData.put("uq_code", mDataMap.get("uq_code"));
								upData.put("manage_code", mDataMap.get("manage_code"));
								upData.put("create_time", mDataMap.get("create_time"));
								DbUp.upTable("oc_coupon_provide").dataUpdate(
										upData, "status,provide_time", "create_time,uq_code,manage_code");// 更新为已发放				
								// 更新优惠券剩余的数量
								typeCountMap.put(couponTypeCode, count + 1);
								//判断是否为邀请人券  
								if(StringUtils.endsWith(mDataMap.get("uq_code"),CouponConst.postfix__coupon)) {
									MDataMap map = new MDataMap();
									MDataMap couTypeMap = DbUp.upTable("oc_coupon_type").one("coupon_type_code", couponTypeCode);
									map.put("uid", WebHelper.upUuid());
									map.put("relative_type",CouponConst.referees__coupon);
									map.put("member_code",memberCode );
									map.put("coupon_type_code",couponTypeCode );
									//insertMap.put("coupon_code",WebHelper.upCode("CP"));
									map.put("initial_money",couTypeMap.get("money"));
									map.put("limit_money",couTypeMap.get("limit_money"));
									map.put("create_time", DateUtil.getNowTime());
									map.put("start_time",couTypeMap.get("start_time") );
									map.put("end_time",couTypeMap.get("end_time") );
									map.put("money_type",couTypeMap.get("money_type") );
									map.put("manage_code", "SI2003");
									map.put("valid_day",couTypeMap.get("valid_day"));
									map.put("valid_type", couTypeMap.get("valid_type"));
									String start_time = "";
									String end_time = "";
									//有效期为日期范围
									if("4497471600080002".equals((String)map.get("valid_type"))) {
										start_time = map.get("start_time");
										end_time = map.get("end_time");
									}
									else if("4497471600080001".equals((String)map.get("valid_type"))){//有效类型为天数
										start_time=DateUtil.getNowTime();
										end_time = DateUtil.getTimeCompareSomeDay(Integer.valueOf(map.get("valid_day").toString()));
									}else if("4497471600080003".equals((String)map.get("valid_type"))) {//有效类型为小时
										start_time=DateUtil.getNowTime();
										end_time = DateUtil.addMinute(Integer.valueOf(map.get("valid_day").toString())*60);
									}else if("4497471600080004".equals((String)map.get("valid_type"))) {//有效类型为分钟
										start_time=DateUtil.getNowTime();
										end_time = DateUtil.addMinute(Integer.valueOf(map.get("valid_day").toString()));
									}
									//DbUp.upTable("oc_coupon_member").dataInsert(map);
									DbUp.upTable("oc_coupon_member").insert("uid",WebHelper.upUuid(),"relative_type","7","member_code",memberCode,
											 "coupon_type_code",map.get("coupon_type_code").toString(),"initial_money",map.get("initial_money").toString(),
											 "limit_money",map.get("limit_money").toString(),"create_time",DateUtil.getNowTime(),"start_time",start_time,"end_time",end_time,"money_type",map.get("money_type").toString(),
											 "manage_code","SI2003");
								}	

							}
						}
					}
				} catch (Exception e) {
					LogFactory.getLog(getClass()).error("JobUserCoupon doExecute Error!", e);
				}
				Iterator<String> iter = typeMap.keySet().iterator();
				while (iter.hasNext()) {
					String upTypeCode = iter.next();
					Integer provideCount = typeMap.get(upTypeCode);
					up.updateCouponType(provideCount, upTypeCode,"system");// 更新优惠券发放数额
				}
				//System.out.println("优惠券发放扫描结束");
			}
		}
		// WebHelper.unLock(Lockcode);
	}
}

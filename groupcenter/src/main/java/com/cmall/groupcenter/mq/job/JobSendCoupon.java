package com.cmall.groupcenter.mq.job;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.helpers.LogLog;
import org.quartz.JobExecutionContext;

import com.alibaba.fastjson.JSON;
import com.cmall.groupcenter.homehas.RsyncCouponForYth;
import com.cmall.groupcenter.homehas.model.CouponYth;
import com.cmall.groupcenter.homehas.model.RsyncRequestCouponForYth;
import com.cmall.groupcenter.homehas.model.RsyncResponseCouponForYth;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时推送优惠券一体化产生的优惠券任务到LD
 * @remark 
 * @author 任宏斌
 * @date 2020年5月15日
 */
public class JobSendCoupon extends RootJob {

	@Override
	public void doExecute(JobExecutionContext context) {
		MDataMap timer = DbUp.upTable("ts_task_timer_yth").one("task_nm",this.getClass().getSimpleName());
		String beginTime = timer.get("last_time");
		String endTime = DateUtil.getSysDateTimeString();
		String specialTime = DateUtil.addDateHour(beginTime, -6);
		
		String sSql = "select * from ts_giftvoucher_yth where ot_time > :begin_time and ot_time<= :end_time and synch_yn='N'";
		List<Map<String, Object>> taskList = DbUp.upTable("ts_giftvoucher_yth").dataSqlList(sSql, new MDataMap("begin_time", specialTime, "end_time", endTime));
		if(null != taskList && !taskList.isEmpty()) {
			List<String> couponCodeList = new ArrayList<String>();
			RsyncCouponForYth rsyncCouponForYth = new RsyncCouponForYth();
			RsyncRequestCouponForYth request = rsyncCouponForYth.upRsyncRequest();
			for(Iterator<Map<String, Object>> i = taskList.iterator(); i.hasNext();) {
				Map<String, Object> task = i.next();
				CouponYth couponYth = new CouponYth();
				String coupon_code = task.get("coupon_code")+"";
				couponCodeList.add("'" + coupon_code + "'");
				if(null == task.get("old_status") || "".equals(task.get("old_status"))) {//新增
					String sql1 = "SELECT ac.out_activity_code,mc.member_code,mc.login_name phone,t.money_type "
							+ "FROM ordercenter.oc_coupon_info oc LEFT JOIN ordercenter.oc_coupon_type t "
							+ "ON oc.coupon_type_code=t.coupon_type_code and oc.activity_code=t.activity_code "
							+ "LEFT JOIN ordercenter.oc_activity ac ON oc.activity_code=ac.activity_code "
							+ "LEFT JOIN membercenter.mc_login_info mc ON oc.member_code=mc.member_code "
							+ "WHERE (oc.last_mdf_id != 'ld' OR oc.last_mdf_id IS NULL) AND oc.coupon_code=:coupon_code";
					Map<String, Object> couponInfo = DbUp.upTable("oc_coupon_info").dataSqlOne(sql1, new MDataMap("coupon_code", coupon_code));
					if(null != couponInfo && !couponInfo.isEmpty()) {
						couponYth.setDo_type("C");
						couponYth.setEvent_id(couponInfo.get("out_activity_code")+"");
						couponYth.setMember_code(couponInfo.get("member_code")+"");
						couponYth.setPhone(couponInfo.get("phone")+"");
						couponYth.setCoupon_code(coupon_code);
						couponYth.setDis_type("449748120002".equals(couponInfo.get("money_type"))?"20":"10");
						couponYth.setMdf_id("ld");
						
						//查地址信息
						String sql2 = "SELECT nc.address_name,concat_ws('-',address_province,address_city,address_county,address_street) address "
								+ "FROM newscenter.nc_address nc where nc.address_code=:member_code and nc.address_default=1 LIMIT 1";
						Map<String, Object> addressInfo = DbUp.upTable("nc_address").dataSqlOne(sql2, new MDataMap("member_code", couponInfo.get("member_code")+""));
						if(null!= addressInfo && !addressInfo.isEmpty()) {
							couponYth.setAddress(null == addressInfo.get("address") ? "" : addressInfo.get("address")+"");
							couponYth.setAddress_name(null == addressInfo.get("address_name") ? "" : addressInfo.get("address_name")+"");
						}
						request.getCouponList().add(couponYth);
					}
				}else {
					String sql1 = "SELECT ci.out_coupon_code,ci.status,ci.surplus_money,oa.is_change "
							+ "FROM ordercenter.oc_coupon_info ci left join ordercenter.oc_activity oa "
							+ "on ci.activity_code=oa.activity_code where coupon_code=:coupon_code";
					Map<String, Object> couponInfo = DbUp.upTable("oc_coupon_info").dataSqlOne(sql1, new MDataMap("coupon_code", coupon_code));
					String out_coupon_code = couponInfo.get("out_coupon_code") + "";
					String status = couponInfo.get("status") + "";
					
					if(StringUtils.isNotBlank(out_coupon_code)) {
						
						couponYth.setLj_code(out_coupon_code);
						couponYth.setMdf_id("app");
						couponYth.setCoupon_code(coupon_code);
						BigDecimal old_surplus_money = new BigDecimal(null == task.get("old_surplus_money") ? "0" : task.get("old_surplus_money")+"");
						BigDecimal surplus_money = new BigDecimal(null == task.get("surplus_money") ? "0" : task.get("surplus_money")+"");
						
						//优先根据状态来判断使用或还原 若判断不出来 例如0->0 或1->1 这样数据没有do_type即使传到LD也不会处理
						//可找零券根据状态判断不出来 还要根据剩余金额来判断使用或还原  因为找零券用一次后状态就是1 还原时只有当余额=面额时状态才变为0
						if(("0".equals(task.get("old_status")) && "1".equals(status))
								|| (null != couponInfo.get("is_change") && "Y".equals(couponInfo.get("is_change") + "") && old_surplus_money.compareTo(surplus_money) > 0)) {//使用
							couponYth.setDo_type("U");
							String sql2 = "SELECT GROUP_CONCAT(op.order_code) orderCodes FROM ordercenter.oc_order_pay op "
									+ "LEFT JOIN ordercenter.oc_orderinfo oi ON op.order_code=oi.order_code "
									+ "where op.pay_sequenceid=:coupon_code AND oi.order_status != '4497153900010006' GROUP BY op.pay_sequenceid";
							Map<String, Object> codes = DbUp.upTable("oc_order_pay").dataSqlOne(sql2, new MDataMap("coupon_code", coupon_code));
							
							if(codes == null) {
								continue;
							}
							
							String orderCodes = codes.get("orderCodes")+"";
							couponYth.setHjy_ord_id(orderCodes);
							if("Y".equals(couponInfo.get("is_change") + "")) {
								couponYth.setLj_balance_amt(old_surplus_money.subtract(surplus_money).toString());
							}
						}
						if(("1".equals(task.get("old_status")) && "0".equals(status))
								|| (null != couponInfo.get("is_change") && "Y".equals(couponInfo.get("is_change") + "") && old_surplus_money.compareTo(surplus_money) < 0)) {//还原
							couponYth.setDo_type("R");
							couponYth.setHjy_ord_id("");
							if("Y".equals(couponInfo.get("is_change") + "")) {
								couponYth.setLj_balance_amt(surplus_money.subtract(old_surplus_money).toString());
							}
						}
						
						request.getCouponList().add(couponYth);
					}
				}
			}
			rsyncCouponForYth.doRsync();
			RsyncResponseCouponForYth processResult = rsyncCouponForYth.upProcessResult();
			LogLog.error("优惠券一体化：\\r\\napi_input:"+JSON.toJSONString(request.getCouponList())+"\\r\\napi_result:" + JSON.toJSONString(processResult));
			
			//更新同步标识
			String sql = "update ts_giftvoucher_yth set synch_yn='Y' where coupon_code in ( " + StringUtils.join(couponCodeList, ",") + ")";
			DbUp.upTable("ts_giftvoucher_yth").dataExec(sql, new MDataMap());
			
		}
		
		//更新时间
		String sql1 = "update ts_task_timer_yth set last_time = :last_time where task_nm=:task_nm";
		DbUp.upTable("ts_task_timer_yth").dataExec(sql1, new MDataMap("task_nm", this.getClass().getSimpleName(), "last_time", endTime));
	}

}

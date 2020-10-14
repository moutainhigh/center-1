package com.cmall.groupcenter.jd;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cmall.groupcenter.jd.model.JdOrderMsg;
import com.cmall.systemcenter.common.DateUtil;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmassystem.homehas.RsyncJingdongSupport;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapcom.topdo.TopConfig;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.helper.WebHelper;

public class JdSyncOrderStatusSupport {
	public synchronized void syncOrderStatus(String jd_order) {
		MDataMap mWhereMap = new MDataMap();
		List<Map<String,Object>> dataSqlList = null;
		String sqlStr = null;
		if(StringUtils.isNotBlank(jd_order)) {
			mWhereMap.put("jd_order_id", jd_order);
			sqlStr = "select * from oc_order_jd where jd_order_id =:jd_order_id";
			dataSqlList = DbUp.upTable("oc_order_jd").dataSqlList(sqlStr, mWhereMap);
			mWhereMap.clear();
		}else {
			sqlStr = "select * from oc_order_jd where jd_order_state != 19 and jd_order_state != 17 and jd_order_id != '' and jd_order_id is not null and order_state != 0 and state = 0";
			dataSqlList = DbUp.upTable("oc_order_jd").dataSqlList(sqlStr, mWhereMap);
		}
		for(Map<String,Object> map : dataSqlList) {
			String jd_order_id = map.get("jd_order_id").toString();
			String order_code = map.get("order_code").toString();
			Integer order_state1 = Integer.parseInt(map.get("order_state").toString());
			Integer jd_order_state1 = Integer.parseInt(map.get("jd_order_state").toString());
			Integer state1 = Integer.parseInt(map.get("state").toString());
			if(jd_order_state1 == 17 || jd_order_state1 == 19 || order_state1 == 0 || state1 != 0) {
				continue;
			}
			HashMap<String, Object> paramMap = new HashMap<String, Object>();
			paramMap.put("jdOrderId", jd_order_id);
			paramMap.put("queryExts", "orderType,jdOrderState");
			String callGateway = RsyncJingdongSupport.callGateway("jd.kpl.open.selectjdorder.query", paramMap);
			JSONObject jsonObject = JSONObject.parseObject(callGateway).getJSONObject("jd_kpl_open_selectjdorder_query_response");
			Boolean success = jsonObject.getBoolean("success");
//			String recode = jsonObject.get("code").toString();
//			String resultCode = jsonObject.get("resultCode").toString();
			if(success){
				JdOrderMsg result = JSON.parseObject(jsonObject.getString("result"),JdOrderMsg.class);
				Integer jdOrderState = result.getJdOrderState();
				Integer state = result.getState();
				Integer orderState = result.getOrderState();
				mWhereMap.clear();
				String sql = "";
				if(order_state1 != orderState) {
					mWhereMap.put("order_state", orderState.toString());
					sql = sql.concat("order_state,");
				}
				if(jd_order_state1 != jdOrderState) {
					mWhereMap.put("jd_order_state", jdOrderState.toString());
					sql = sql.concat("jd_order_state,");
				}
				if(state1 != state) {
					mWhereMap.put("state", state.toString());
					sql = sql.concat("state,");
				}
				if(mWhereMap.size() > 0) {
					mWhereMap.put("jd_order_id", jd_order_id);
					mWhereMap.put("update_time", FormatHelper.upDateTime());
					sql = sql.concat("jd_order_id,update_time");
					DbUp.upTable("oc_order_jd").dataUpdate(mWhereMap, sql, "jd_order_id");
					String toStatus = null;
					boolean tuikuan = false;
					//订单状态等待确认收货
					if(orderState == 1 && (jdOrderState == 16 || jdOrderState == 10 || jdOrderState == 9 || jdOrderState == 8)) {
						toStatus = "4497153900010003";
					}
					//订单状态已完成或者物流状态已妥投 惠家有订单改成交易成功
					if(orderState == 1 && (jdOrderState == 19 || state == 1)) {
						toStatus = "4497153900010005";
						//订单交易成功
						if(DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990028") <= 0) {
							JobExecHelper.createExecInfo("449746990028", order_code, DateUtil.addMinute(28800));
						}
						if(DbUp.upTable("fh_share_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990033") <= 0) {
							JobExecHelper.createExecInfo("449746990033", order_code, DateUtil.addMinute(21600));
						}
					}
					//订单状态配送退货或者物流状态已拒收  惠家有订单交易失败
					if(jdOrderState == 17 || state == 2 || orderState == 0) {
						toStatus = "4497153900010006";
						tuikuan = true;
						//订单交易失败，判断是否是分销单，如果是，写入取消订单定时任务
						//取消订单，判断是否是分销单，如果是，写入取消订单分销定时
						if(DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990029") <= 0) {
							JobExecHelper.createExecInfo("449746990029", order_code, DateUtil.getSysDateTimeString());
						}
					}
					if(StringUtils.isNotBlank(toStatus)) {
						MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",
								order_code);
						String fromStatus = dm.get("order_status");
						if(!fromStatus.equals(toStatus)) {
							FlowBussinessService fs = new FlowBussinessService();
							String flowBussinessUid = dm.get("uid");
							String flowType = "449715390008";
							String userCode = "system";
							String remark = "auto by system";
							MDataMap md = new MDataMap();
							md.put("order_code", order_code);
							
							RootResult rr = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,
									toStatus, userCode, remark, md);
							if (rr.getResultCode() == 1) {
								if(tuikuan) {
									JdAfterSaleSupport afterSaleSupport  = new JdAfterSaleSupport();
									afterSaleSupport.createOrderRefundTask(order_code, null);
								}
							} else {
								String noticeMail = TopConfig.Instance.bConfig("oneall.count_notice");
								// 惠家有订单状态变更失败
								if(StringUtils.isNotBlank(noticeMail)){
									MailSupport.INSTANCE.sendMail(noticeMail, "订单状态变更失败["+order_code+"]", "变更状态:"+toStatus+" \r\n失败消息："+rr.getResultMessage());
								}
								WebHelper.errorMessage(order_code, "jdorder", 1,
										"jdorder status on ChangeFlow", rr.getResultMessage(),
										null);
							}
						} 
					}
				}
				
			}
		}
	}
}

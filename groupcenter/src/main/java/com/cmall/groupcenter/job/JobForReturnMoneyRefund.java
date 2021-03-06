package com.cmall.groupcenter.job;


import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.JobExecutionContext;

import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmaspay.PaymentResult;
import com.srnpr.xmaspay.process.refund.PayGateRefundReqProcess;
import com.srnpr.xmaspay.util.PayServiceFactory;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.kvsupport.KvFactory;
import com.srnpr.zapweb.rootweb.RootJobForExclusiveLock;

/**
 * 取消单自动退款
 */
public class JobForReturnMoneyRefund extends RootJobForExclusiveLock {

	static String FIELD_STATUS = "status";
	static String FIELD_NOTIFY_TIME = "notifyTime";
	static String FIELD_MSG = "msg";
	
	static FlowBussinessService service = new FlowBussinessService();
	
	public void doExecute(JobExecutionContext context) {
		doRefund();
	}
	
	private void doRefund() {
		List<MDataMap> returnMoneyList = getReturnMoneyList();
		
		List<String> refundFailedList = new ArrayList<String>();
		List<String> changeStatusFailedList = new ArrayList<String>();
		
		KvFactory kvf = XmasKv.upFactory(EKvSchema.RefundTime);
		
		PaymentResult payRes;
		String returnMoneyCode;
		for(MDataMap map : returnMoneyList) {
			returnMoneyCode = map.get("return_money_code");
			
			// 忽略已经退款的订单
			if(!"1".equals(kvf.hget(returnMoneyCode, FIELD_STATUS))) {
				// 非模拟支付时再掉接口
				if(!map.get("trade_no").toLowerCase().startsWith("sim")) {
					// 走支付网关退款
					PayGateRefundReqProcess.PaymentInput input = new PayGateRefundReqProcess.PaymentInput();
					input.returnMoneyCode = returnMoneyCode;
					input.remark = "订单取消,系统自动退款";
					payRes = PayServiceFactory.getInstance().getPayGateRefundReqProcess().process(input);
					
					// 记录取消是否成功
					kvf.hset(returnMoneyCode, FIELD_STATUS, payRes.getResultCode()+"");
					kvf.hset(returnMoneyCode, FIELD_MSG, payRes.getResultMessage());
					kvf.expire(returnMoneyCode, 2592000);
					
					// 记录退款失败订单号
					if(payRes.getResultCode() != 1) {
						refundFailedList.add(returnMoneyCode);
						
						// 更新自动退款状态
						map.put("refund_flag", "4497471600650001");
						DbUp.upTable("oc_return_money").dataUpdate(map, "refund_flag", "return_money_code");
						continue;
					}
				}
			}
			
			// 变更退款单状态
			RootResult changeFlowRes = service.ChangeFlow(map.get("uid"), "449715390004", "4497153900040003", "4497153900040001", "system", "订单取消,系统自动退款", new MDataMap());
			// 记录状态变更失败订单号
			if(changeFlowRes.getResultCode() != 1) {
				changeStatusFailedList.add(returnMoneyCode);
			}
		}
		
		notifyMsg(refundFailedList, changeStatusFailedList);
	}
	
	/**
	 * 查询需要自动退款的订单
	 * @return
	 */
	private List<MDataMap> getReturnMoneyList() {
		// 取消发货的商户待退款订单
		// 查询24小时内创建的退款单
		String sql = "SELECT oi.big_order_code,rm.uid,rm.return_money_code,rm.order_code,IF(rm.online_money < oi.due_money, rm.online_money, oi.due_money) money,pay.pay_type,pay.trade_no "
				+ " FROM ordercenter.oc_return_money rm,ordercenter.oc_orderinfo oi,ordercenter.oc_orderinfo_upper_payment pay"
				+ " WHERE rm.order_code = oi.order_code AND oi.big_order_code = pay.big_order_code "
				+ " AND pay.pay_type IN('449746280003','449746280005','449746280014','449746280020')"
				+ " AND oi.order_status = '4497153900010006' AND rm.status = '4497153900040003' AND rm.seller_code = 'SI2003' AND oi.pay_type = '449716200001'"
				+ " AND return_goods_code = ''"
				+ " AND rm.online_money > 0"
				+ " AND oi.order_code LIKE 'DD%'";
		
		List<Map<String, Object>> mapList = DbUp.upTable("oc_return_money").dataSqlList(sql, new MDataMap());
		
		List<MDataMap> dataList = new ArrayList<MDataMap>();
		String now = FormatHelper.upDateTime();
		MDataMap mWhereMap = new MDataMap("now", now);
		for(Map<String, Object> map : mapList) {
			mWhereMap.put("code", map.get("order_code").toString());
			
			// 忽略支付时间在2小时以上的订单
			//if(DbUp.upTable("lc_orderstatus").dataCount("now_status = '4497153900010002' AND code = :code AND DATE_ADD(create_time,INTERVAL 2 HOUR) < :now", mWhereMap) > 0) {
			//	continue;
			//}
			
			// 如果是支付宝则必须是新版支付才能退款
			if("449746280003".equals(map.get("pay_type"))) {
				// 如果没查询到新版支付则不退款
				if(DbUp.upTable("oc_payment_paygate").dataCount("c_order = :c_order AND c_paygate LIKE '66%'", new MDataMap("c_order", map.get("big_order_code").toString())) == 0) {
					continue;
				}
			}
			
			dataList.add(new MDataMap(map));
		}
		
		return dataList;
	}
	
	/**
	 * 发送操作失败的通知邮件
	 * @param refundFailedList
	 * @param changeStatusFailedList
	 */
	private void notifyMsg(List<String> refundFailedList,List<String> changeStatusFailedList) {
		if(refundFailedList.isEmpty() && changeStatusFailedList.isEmpty()) {
			return;
		}
		
		KvFactory kvf = XmasKv.upFactory(EKvSchema.RefundTime);
		StringBuilder b = new StringBuilder();
		Date lastTime = null;
		
		// 两小时内只提醒一次
		Date now = DateUtils.addHours(new Date(), -2);
		
		if(!refundFailedList.isEmpty()) {
			StringBuffer tmp = new StringBuffer();
			for(String returnMoneyCode : refundFailedList) {
				try {
					String text = kvf.hget(returnMoneyCode, FIELD_NOTIFY_TIME);
					if(StringUtils.isNotBlank(text)) {
						lastTime = DateUtils.parseDate(kvf.hget(returnMoneyCode, FIELD_NOTIFY_TIME), new String[]{"yyyy-MM-dd HH:mm:ss"});
					} else {
						lastTime = null;
					}
				} catch (ParseException e) {
					lastTime = null;
				}
				
				if(lastTime != null && lastTime.after(now)) {
					continue;
				}
				
				tmp.append(returnMoneyCode).append(",");
				// 记录失败时间
				kvf.hset(returnMoneyCode, FIELD_NOTIFY_TIME, FormatHelper.upDateTime());
			}
			
			if(tmp.length() > 0) {
				b.append("退款失败：").append(tmp);
			}
		}
		
		if(!changeStatusFailedList.isEmpty()) {
			StringBuffer tmp = new StringBuffer();
			for(String returnMoneyCode : changeStatusFailedList) {
				try {
					lastTime = DateUtils.parseDate(kvf.hget(returnMoneyCode, FIELD_NOTIFY_TIME), new String[]{"yyyy-MM-dd HH:mm:ss"});
				} catch (ParseException e) {
					lastTime = null;
				}
				
				if(lastTime != null && lastTime.after(now)) {
					continue;
				}
				
				tmp.append(returnMoneyCode).append(",");
				// 记录失败时间
				kvf.hset(returnMoneyCode, FIELD_NOTIFY_TIME, FormatHelper.upDateTime());
			}
			
			if(tmp.length() > 0) {
				b.append("\r\n");
				b.append("状态变更失败：");
				b.append(tmp);
			}
		}
		
		String sErrorNotice = bConfig("zapweb.mail_notice").trim();
		if (StringUtils.isNotBlank(sErrorNotice) && b.length() > 0) {
			MailSupport.INSTANCE.sendMail(sErrorNotice,"取消单自动退款定时执行异常通知",b.toString());
		}

	}
}

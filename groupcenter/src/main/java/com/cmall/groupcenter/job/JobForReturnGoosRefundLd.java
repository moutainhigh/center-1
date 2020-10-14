package com.cmall.groupcenter.job;


import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.homehas.RsyncGetRtnBankInfo;
import com.cmall.groupcenter.homehas.RsyncModRtnBankStatus;
import com.cmall.groupcenter.homehas.model.RsyncRequestModRtnBankStatus;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetRtnBankInfo.RtnBankInfo;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.srnpr.xmaspay.PaymentResult;
import com.srnpr.xmaspay.process.refund.PayGateRefundReqProcess;
import com.srnpr.xmaspay.util.PayServiceFactory;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basesupport.MailSupport;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.kvsupport.KvFactory;
import com.srnpr.zapweb.rootweb.RootJobForExclusiveLock;

/**
 * LD品自动退款
 */
public class JobForReturnGoosRefundLd extends RootJobForExclusiveLock {

	static String FIELD_STATUS = "status";
	static String FIELD_NOTIFY_TIME = "notifyTime";
	static String FIELD_MSG = "msg";
	
	static FlowBussinessService service = new FlowBussinessService();
	
	public void doExecute(JobExecutionContext context) {
		doRefund();
	}
	
	private void doRefund() {
		List<MDataMap> returnMoneyList = getOrderList();
		
		List<String> refundFailedList = new ArrayList<String>();
		
		PaymentResult payRes;
		String outOrderCode,orderCode,ordSeq;
		for(MDataMap map : returnMoneyList) {
			outOrderCode = map.get("ord_id");
			ordSeq = map.get("ord_seq");
			orderCode = (String)DbUp.upTable("oc_orderinfo").dataGet("order_code", "seller_code = 'SI2003' AND out_order_code = :outOrderCode", new MDataMap("outOrderCode", outOrderCode));
			
			if(orderCode == null) {
				LogFactory.getLog(getClass()).warn("[JobForReturnGoosRefundLd]未查询到待退款订单："+outOrderCode);
				continue;
			}
			
			// 已经退款完成
			if(DbUp.upTable("lc_return_money_ld_log").count("out_order_code",outOrderCode,"ord_seq",ordSeq) > 0) {
				LogFactory.getLog(getClass()).warn("[JobForReturnGoosRefundLd]订单已经退款："+outOrderCode);
				continue;
			}
			
			payRes = null;
			// 排除模拟支付订单
			if(!map.get("pay_no").toLowerCase().startsWith("sim")) {
				// 走支付网关退款
				PayGateRefundReqProcess.PaymentInput input = new PayGateRefundReqProcess.PaymentInput();
				input.orderCode = orderCode;
				input.money = new BigDecimal(map.get("ord_money"));
				input.orderSeq = ordSeq;
				input.remark = "系统自动退款";
				payRes = PayServiceFactory.getInstance().getPayGateRefundReqProcess().process(input);
			}
			
			// 记录退款失败订单号
			if(payRes != null && payRes.getResultCode() != 1) {
				refundFailedList.add(orderCode+"("+outOrderCode+")");
				continue;
			} else {
				notifyLd(map);
			}
			
		}
		
		notifyMsg(refundFailedList);
	}
	
	// 后续定时通知LD系统
	private void notifyLd(MDataMap map) {
		RsyncModRtnBankStatus rsync = new RsyncModRtnBankStatus();
		RsyncRequestModRtnBankStatus req = rsync.upRsyncRequest();
		
		String ordId = map.get("ord_id");
		String ordSeq = map.get("ord_seq");
		
		req.setOrd_id(ordId);
		req.setOrd_seq(ordSeq);
		
		MDataMap logMap = new MDataMap(
				"out_order_code", ordId,
				"ord_seq", ordSeq,
				"exec_num", "1",
				"ord_money", map.get("ord_money"),
				"create_time", FormatHelper.upDateTime(),
				"update_time", FormatHelper.upDateTime()
			);
		
		try {
			if(rsync.doRsync()) {
				// 如果调用成功则更改标识为1
				logMap.put("notify_flag", "1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
	
		DbUp.upTable("lc_return_money_ld_log").dataInsert(logMap);
	}
	
	/**
	 * 查询需要自动退款的订单
	 * @return
	 */
	private List<MDataMap> getOrderList() {
		List<MDataMap> dataList = new ArrayList<MDataMap>();
		
		RsyncGetRtnBankInfo rsync = new RsyncGetRtnBankInfo();
		if(!rsync.doRsync()) {
			return dataList;
		}
		
		Map<String,String> paynoMap = new HashMap<String, String>();
		Map<String,BigDecimal> moneyMap = new HashMap<String, BigDecimal>();
		Map<String,List<String>> seqMap = new HashMap<String, List<String>>();
		List<RtnBankInfo> itemList = rsync.upResponseObject().getResultList();
		for(RtnBankInfo item : itemList) {
			// 排除订单号和序号重复的数据
			if(seqMap.containsKey(item.getOrd_id())
					&& seqMap.get(item.getOrd_id()).contains(item.getOrd_seq())) {
				continue;
			}
			
			paynoMap.put(item.getOrd_id(), StringUtils.trimToEmpty(item.getPay_no()));
			
			if(!moneyMap.containsKey(item.getOrd_id())) {
				moneyMap.put(item.getOrd_id(), new BigDecimal(item.getPay_money()));
			} else {
				moneyMap.put(item.getOrd_id(),moneyMap.get(item.getOrd_id()).add(new BigDecimal(item.getPay_money())));
			}
			
			if(!seqMap.containsKey(item.getOrd_id())) {
				List<String> list = new ArrayList<String>();
				list.add(item.getOrd_seq());
				seqMap.put(item.getOrd_id(), list);
			} else {
				seqMap.get(item.getOrd_id()).add(item.getOrd_seq());
			}
		}
		
		Set<Entry<String, BigDecimal>> entryList = moneyMap.entrySet();
		MDataMap m;
		for(Entry<String, BigDecimal> entry : entryList) {
			m = new MDataMap();
			m.put("ord_id", entry.getKey());
			m.put("ord_money", entry.getValue().toString());
			m.put("pay_no", paynoMap.get(entry.getKey()));
			m.put("ord_seq", StringUtils.join(seqMap.get(entry.getKey()),","));
			dataList.add(m);
		}
		
		return dataList;
	}
	
	/**
	 * 发送操作失败的通知邮件
	 * @param refundFailedList
	 * @param changeStatusFailedList
	 */
	private void notifyMsg(List<String> refundFailedList) {
		if(refundFailedList.isEmpty()) {
			return;
		}
		
		KvFactory kvf = XmasKv.upFactory(EKvSchema.RefundTime);
		StringBuilder b = new StringBuilder();
		Date lastTime = null;
		
		// 两小时内只提醒一次
		Date now = DateUtils.addHours(new Date(), -2);
		
		if(!refundFailedList.isEmpty()) {
			b.append("退款失败：");
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
				
				b.append(returnMoneyCode).append(",");
				// 记录失败时间
				kvf.hset(returnMoneyCode, FIELD_NOTIFY_TIME, FormatHelper.upDateTime());
			}
		}
		
		String sErrorNotice = bConfig("groupcenter.return_money_mail").trim();
		if (StringUtils.isNotBlank(sErrorNotice)) {
			MailSupport.INSTANCE.sendMail(sErrorNotice,"LD退货单自动退款定时执行异常通知",b.toString());
		}

	}
}

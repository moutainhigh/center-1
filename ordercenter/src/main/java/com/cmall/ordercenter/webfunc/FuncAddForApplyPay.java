package com.cmall.ordercenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cmall.ordercenter.model.Invoice;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 商户结算3.0-新增付款申请单
 * 
 * @author zht
 * 
 */
public class FuncAddForApplyPay extends RootFunc {
	private static SimpleDateFormat DateSdf = new SimpleDateFormat("yyyyMMdd");
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// 发票信息
		MWebResult mResult = new MWebResult();
		Date now = new Date();
		String creator = UserFactory.INSTANCE.create().getLoginName();
		String invoicesJson = mDataMap.get("zw_f_invoices_json");
		List<Invoice> invoiceList = JSON.parseArray(invoicesJson, Invoice.class);
		for (Invoice invoice : invoiceList) {
//			MDataMap invoiceMap = this.getValue(invoice);
			MDataMap invoiceMap = new MDataMap();
			invoiceMap.put("invoice_code", invoice.getInvoiceCode());
			invoiceMap.put("amount", String.valueOf(invoice.getAmount()));
			invoiceMap.put("tax_amount", String.valueOf(invoice.getTaxAmount()));
			invoiceMap.put("tax_rate", String.valueOf(invoice.getTaxRate()));
			invoiceMap.put("total_money", String.valueOf(invoice.getTotalMoney()));
			invoiceMap.put("create_time", timeSdf.format(now));
			invoiceMap.put("creator", creator);
			try{
				if (mResult.upFlagTrue()) {
					DbUp.upTable("oc_bill_invoice").dataInsert(invoiceMap);
				}
			}catch (Exception e) {
				e.printStackTrace();
				mResult.inErrorMessage(939301228);
			}
		}
		// 结算单信息
		String payCode = WebHelper.upCode("FK");
//		String payCode = "FK" + DateSdf.format(new Date()) + DailySerial.getInstance().getSerial();	//付款申请单号
		MDataMap settleMap = new MDataMap();
		settleMap.put("pay_code", payCode);
		settleMap.put("merchant_code", mDataMap.get("zw_f_merchant_code"));
		settleMap.put("merchant_name", mDataMap.get("zw_f_merchant_name"));
		settleMap.put("settle_codes", mDataMap.get("zw_f_settle_codes"));
		settleMap.put("invoice_codes", mDataMap.get("zw_f_invoice_codes"));
		settleMap.put("invoice_amount", mDataMap.get("zw_f_invoiceAmount")); 		 //发票应开金额
		settleMap.put("actual_invoice_amount", mDataMap.get("zw_f_invoiceAmount"));  //发票实开金额
		settleMap.put("period_money", mDataMap.get("zw_f_periodMoney"));
		settleMap.put("add_deduction", mDataMap.get("zw_f_addDeduction"));
		//应付金额=发票实开金额-相关扣费
		double tmp = Double.parseDouble(mDataMap.get("zw_f_invoiceAmount")) - Double.parseDouble(mDataMap.get("zw_f_addDeduction"));
		settleMap.put("actual_pay_amount", String.valueOf(tmp));  //应付金额
		//待付金额=应付金额-本期质保金
		tmp = tmp - Double.parseDouble(mDataMap.get("zw_f_periodMoney"));
		settleMap.put("wait_pay_amount", String.valueOf(tmp));
		settleMap.put("create_time", timeSdf.format(now));
		settleMap.put("flag", "4497477900010001");   //待审核
		settleMap.put("is_pay", "4497477900020002");   //是否付款(否)
		settleMap.put("comment", mDataMap.get("zw_f_comment"));
		settleMap.put("creator", creator);
		settleMap.put("is_pay", "4497477900020002");
		try{
			if (mResult.upFlagTrue()) {
				DbUp.upTable("oc_bill_apply_payment").dataInsert(settleMap);
			}
		}catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}
		
		//记日志
		try{
			if (mResult.upFlagTrue()) {
				String ip = WebSessionHelper.create().upIpaddress();
				MDataMap logMap = new MDataMap();
				logMap.put("pay_code", payCode);
				logMap.put("flag", "4497477900010001");
				logMap.put("ip", ip);
				logMap.put("comment", "创建付款申请单");
				logMap.put("create_time", timeSdf.format(now));
				logMap.put("creator", creator);
				DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
			}
		}catch (Exception e) {
			e.printStackTrace();
			mResult.inErrorMessage(939301228);
		}
		
		//变更商户结算单状态(待审核)
		String settleCodes = mDataMap.get("zw_f_settle_codes");
		String merchant_code = mDataMap.get("zw_f_merchant_code");
		if(StringUtils.isNotEmpty(merchant_code)) {
			if(StringUtils.isNotEmpty(settleCodes)) {
				try {
					settleCodes = settleCodes.replaceAll(",", "','");
					String sql = "update oc_bill_merchant_new set flag='4497476900040010' where settle_code in('" + settleCodes + "') and merchant_code='" + merchant_code + "'";
					int count = DbUp.upTable("oc_bill_merchant_new").dataExec(sql, new MDataMap());
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			mResult.setResultMessage(bInfo(969909001));
			mResult.setResultCode(969909001);			
		} else {
			mResult.setResultCode(939303201);
			mResult.setResultMessage(bInfo(939303201));
		}

		return mResult;
	}

	public static class DailySerial {
		private static Date date = new Date();
		private static int serial; // 一天内唯一串号
		private static DailySerial instance;

		private DailySerial() {
		}

		public static synchronized DailySerial getInstance() {
			if (null == instance) {
				instance = new DailySerial();
			}
			return instance;
		}

		public String getSerial() {
			int result = 0;
			Calendar c = Calendar.getInstance();
			String nowDate= DateSdf.format(c.getTime());
			String oldDate = DateSdf.format(date);
			synchronized (date) {
				if (!oldDate.equals(nowDate)) {
					date = c.getTime();
					serial = 0;
					result = ++serial;
				} else {
					result = ++serial;
				}
			}
			return String.format("%04d", result);
		}
	}
}

package com.cmall.ordercenter.webfunc;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cmall.ordercenter.model.Invoice;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webfunc.RootFunc;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 商户结算3.0-修改付款申请单,修改后将申请单状态改为待审核
 * 待审核   4497477900010001
 * 审核通过 4497477900010002
 * 已确认   4497477900010003
 * 已付款   4497477900010004
 * 拒绝     4497477900010005待审核   4497477900010001
 * 审核通过 4497477900010002
 * 已确认   4497477900010003
 * 已付款   4497477900010004
 * 拒绝     4497477900010005
 * @author zht
 * 
 */
public class FuncEditForApplyPay extends RootFunc {
	private static SimpleDateFormat timeSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	@Override
	public MWebResult funcDo(String sOperateUid, MDataMap mDataMap) {
		// 发票信息
		MWebResult mResult = new MWebResult();
		Date now = new Date();
		String payCode = mDataMap.get("zw_f_pay_code");
		String sql = "select invoice_codes from oc_bill_apply_payment where pay_code='" + payCode + "'";
		Map<String, Object> map = DbUp.upTable("oc_bill_apply_payment").dataSqlOne(sql, null);
		if (null != map && map.size() > 0) {
			String invoiceCodes = (String) map.get("invoice_codes");
			String[] invoiceArr = new String[0];
			if (!StringUtils.isEmpty(invoiceCodes)) {
				invoiceArr = invoiceCodes.split(",");
			}
			// 插入修改后的发票信息
			String updator = UserFactory.INSTANCE.create().getLoginName();
			String invoicesJson = mDataMap.get("zw_f_invoices_json");
			List<Invoice> invoiceList = JSON.parseArray(invoicesJson, Invoice.class);
			for (Invoice invoice : invoiceList) {
				MDataMap invoiceMap = new MDataMap();
				invoiceMap.put("invoice_code", invoice.getInvoiceCode());
				invoiceMap.put("amount", String.valueOf(invoice.getAmount()));
				invoiceMap.put("tax_amount", String.valueOf(invoice.getTaxAmount()));
				invoiceMap.put("tax_rate", String.valueOf(invoice.getTaxRate()));
				invoiceMap.put("total_money", String.valueOf(invoice.getTotalMoney()));
				try {
					if (existInvoice(invoice.getInvoiceCode(), invoiceArr)) {
						// 更新已有发票
						sql = "select creator,create_time from oc_bill_invoice where invoice_code='" + invoice.getInvoiceCode() + "'";
						Map<String, Object> oldMap = DbUp.upTable("oc_bill_invoice").dataSqlOne(sql, new MDataMap());
						if(null != oldMap) {
							String creator = oldMap.get("creator") == null ? "" : oldMap.get("creator").toString();
							String createTime = oldMap.get("create_time") == null ? "" : oldMap.get("create_time").toString();
							invoiceMap.put("create_time", createTime);
							invoiceMap.put("creator", creator);
						}
						
						sql = "delete from oc_bill_invoice where invoice_code='" + invoice.getInvoiceCode() + "'";
						DbUp.upTable("oc_bill_invoice").dataExec(sql, new MDataMap());

						invoiceMap.put("update_time", timeSdf.format(now));
						invoiceMap.put("updator", updator);
						if (mResult.upFlagTrue()) {
							DbUp.upTable("oc_bill_invoice").dataInsert(invoiceMap);
						}
					} else {
						// 插入新发票
						invoiceMap.put("create_time", timeSdf.format(now));
						invoiceMap.put("creator", updator);
						if (mResult.upFlagTrue()) {
							DbUp.upTable("oc_bill_invoice").dataInsert(invoiceMap);
						}

					}
				} catch (Exception e) {
					e.printStackTrace();
					mResult.inErrorMessage(939301228);
				}
			}
			// 结算单信息
			MDataMap settleMap = new MDataMap();
			settleMap.put("pay_code", payCode);
			settleMap.put("merchant_code", mDataMap.get("zw_f_merchant_code"));
			settleMap.put("merchant_name", mDataMap.get("zw_f_merchant_name"));
			settleMap.put("settle_codes", mDataMap.get("zw_f_settle_codes"));
			settleMap.put("invoice_codes", mDataMap.get("zw_f_invoice_codes"));
			settleMap.put("invoice_amount", mDataMap.get("zw_f_invoiceAmount")); // 发票应开金额
			settleMap.put("actual_invoice_amount", mDataMap.get("zw_f_invoiceAmount")); // 发票实开金额
			settleMap.put("period_money", mDataMap.get("zw_f_periodMoney"));
			settleMap.put("add_deduction", mDataMap.get("zw_f_addDeduction"));
			// 应付金额=发票实开金额-相关扣费
			double tmp = Double.parseDouble(mDataMap.get("zw_f_invoiceAmount"))
					- Double.parseDouble(mDataMap.get("zw_f_addDeduction"));
			settleMap.put("actual_pay_amount", String.valueOf(tmp)); // 应付金额
			// 待付金额=应付金额-本期质保金
			tmp = tmp - Double.parseDouble(mDataMap.get("zw_f_periodMoney"));
			settleMap.put("wait_pay_amount", String.valueOf(tmp));

			settleMap.put("flag", "4497477900010001"); // 待审核
			settleMap.put("comment", mDataMap.get("zw_f_comment"));
			settleMap.put("update_time", timeSdf.format(now));
			settleMap.put("updator", updator);
			try {
				if (mResult.upFlagTrue()) {
					DbUp.upTable("oc_bill_apply_payment").dataUpdate(settleMap,
							"merchant_code,merchant_name,settle_codes,invoice_codes,invoice_amount,actual_invoice_amount," +
							"period_money,add_deduction,actual_pay_amount,wait_pay_amount,comment,update_time,updator,flag",
							"pay_code");
				}
			} catch (Exception e) {
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
					logMap.put("comment", "修改付款申请单");
					logMap.put("create_time", timeSdf.format(now));
					logMap.put("creator", updator);
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
						sql = "update oc_bill_merchant_new set flag='4497476900040010' where settle_code in('" + settleCodes + "') and merchant_code='" + merchant_code + "'";
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
		}
		return mResult;
	}

	private boolean existInvoice(String invoice, String[] invoiceArr) {
		for (String in : invoiceArr) {
			if (in.equals(invoice)) {
				return true;
			}
		}
		return false;
	}
}

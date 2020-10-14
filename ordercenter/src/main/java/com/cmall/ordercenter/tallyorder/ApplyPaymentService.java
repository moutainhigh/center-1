package com.cmall.ordercenter.tallyorder;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSON;
import com.cmall.ordercenter.model.ApplyPayment;
import com.cmall.ordercenter.model.Invoice;
import com.cmall.ordercenter.model.SellerFinancialStatement;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

/**
 * 商户结算系统3.0-结算申请单
 * @author zht
 *
 */
public class ApplyPaymentService extends BaseClass {
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/**
	 * 创建或修改商户结算付款申请单之前的初始化查询获取
	 * 1.申请单所关联的商户(1:1)
	 * 2.申请单所关联的发票信息(1:N)
	 * 3.申请单所关联的结算单信息(1:N)
	 * 
	 * @param applyPayCode 申请单编码
	 * @return
	 */
	public String initQuery(String uid) {
		ApplyPayment apm = new ApplyPayment();
		if(!StringUtils.isEmpty(uid)) {
			String sql = "select pay_code,merchant_code,merchant_name,settle_codes,invoice_codes,comment,flag,reject_reason  "
					+ "from oc_bill_apply_payment a "
					+ "where uid='" + uid + "'";
			Map<String, Object> map = DbUp.upTable("oc_bill_apply_payment").dataSqlOne(sql, null);
			if(null != map && map.size() > 0) {
				apm.setPayCode(isEmpty(map.get("pay_code")) ? "" : map.get("pay_code").toString());
				apm.setSellerCode(isEmpty(map.get("merchant_code")) ? "" : map.get("merchant_code").toString());
				apm.setSellerName(isEmpty(map.get("merchant_name")) ? "" : map.get("merchant_name").toString());
				apm.setComment(isEmpty(map.get("comment")) ? "" : map.get("comment").toString());
				apm.setInvoiceCodes(isEmpty(map.get("invoice_codes")) ? "" : map.get("invoice_codes").toString());
				apm.setSettleCodes(isEmpty(map.get("settle_codes")) ? "" : map.get("settle_codes").toString());
				apm.setFlag(isEmpty(map.get("flag")) ? "" : map.get("flag").toString());
				apm.setRejectReason(isEmpty(map.get("reject_reason")) ? "" : map.get("reject_reason").toString());
				
				String startTime = "", endTime = "";
				//关联结算单信息
				String settleCodes = (String) map.get("settle_codes");
				String merchantCode = (String) map.get("merchant_code");
				if(!StringUtils.isEmpty(settleCodes)) {
					settleCodes = settleCodes.replace(",", "','");
					sql = "select settle_code,merchant_code,merchant_name,invoice_amount,period_money,add_deduction,"
							+ "actual_pay_amount,start_time,end_time "
							+ "from oc_bill_merchant_new a "
							+ "where a.settle_code in('" + settleCodes + "') and merchant_code='" + merchantCode + "'";
					List<Map<String,Object>> sfsList = DbUp.upTable("oc_bill_merchant_new").dataSqlList(sql, null);
					if(null != sfsList && sfsList.size() > 0) {
						for(Map<String, Object> fs : sfsList) {
							SellerFinancialStatement sfs = new SellerFinancialStatement();
							sfs.setSettleCode(isEmpty(fs.get("settle_code")) ? "" : fs.get("settle_code").toString());
							sfs.setMerchantCode(isEmpty(fs.get("merchant_code")) ? "" : fs.get("merchant_code").toString());
							sfs.setMerchantName(isEmpty(fs.get("merchant_name")) ? "" : fs.get("merchant_name").toString());
							sfs.setActualPayAmount(isEmpty(fs.get("actual_pay_amount")) ? "" : fs.get("actual_pay_amount").toString());
							sfs.setAddDeduction(isEmpty(fs.get("add_deduction")) ? "" : fs.get("add_deduction").toString());
//							sfs.setTaxAmount(isEmpty(fs.get("taxAmount")) ? "" : fs.get("taxAmount").toString());
							sfs.setInvoiceAmount(isEmpty(fs.get("invoice_amount")) ? "" : fs.get("invoice_amount").toString());
							sfs.setPeriodMoney(isEmpty(fs.get("period_money")) ? "" : fs.get("period_money").toString());
							try {
								startTime = isEmpty(fs.get("start_time")) ? "" : sdf.format(sdf.parse(fs.get("start_time").toString()));
								sfs.setStartTime(startTime);
							} catch(Exception e) {
								e.printStackTrace();
							}
							try {
								endTime = isEmpty(fs.get("end_time")) ? "" : sdf.format(sdf.parse(fs.get("end_time").toString()));
								sfs.setEndTime(endTime);
							} catch(Exception e) {
								e.printStackTrace();
							}
							apm.getSettleList().add(sfs);
							
							//结算单关联税额信息
							if(StringUtils.isNotEmpty(startTime) && StringUtils.isNotEmpty(endTime)) {
								String sellerCode = apm.getSellerCode();
								sql = "select sum(input_tax_subtotal) as taxAmount from oc_bill_final_export "
										+ "where small_seller_code='" + sellerCode + "' and start_time='" + startTime +"' and end_time='" + endTime + "'";
								Map<String,Object> taxSum = DbUp.upTable("oc_bill_merchant_new").dataSqlOne(sql, new MDataMap());
								if(null != taxSum && taxSum.size() > 0) {
									sfs.setTaxAmount(isEmpty(taxSum.get("taxAmount")) ? "0.0" : taxSum.get("taxAmount").toString());
								}
							}
						}
					}
				}
				//关联发票信息
				String invoiceCodes = (String) map.get("invoice_codes");
				if(!StringUtils.isEmpty(invoiceCodes)) {
					invoiceCodes = invoiceCodes.replace(",", "','");
					sql = "select invoice_code, amount, tax_amount, tax_rate, total_money, create_time "
						+ "from oc_bill_invoice a "
						+ "where a.invoice_code in('" + invoiceCodes + "')";
					List<Map<String,Object>> invoiceList = DbUp.upTable("oc_bill_invoice").dataSqlList(sql, null);
					if(null != invoiceList && invoiceList.size() > 0) {
						for(Map<String, Object> invoice : invoiceList) {
							Invoice in = new Invoice();
							in.setInvoiceCode(isEmpty(invoice.get("invoice_code")) ? "" : invoice.get("invoice_code").toString());
							in.setAmount(isEmpty(invoice.get("amount")) ? new BigDecimal(0.0) : new BigDecimal(invoice.get("amount").toString()));
							in.setTaxAmount(isEmpty(invoice.get("tax_amount")) ? new BigDecimal(0.0) : new BigDecimal(invoice.get("tax_amount").toString()));
							in.setTaxRate(isEmpty(invoice.get("tax_rate")) ? new BigDecimal(0.0) : new BigDecimal(invoice.get("tax_rate").toString()));
							in.setTotalMoney(isEmpty(invoice.get("total_money")) ? new BigDecimal(0.0) : new BigDecimal(invoice.get("total_money").toString()));
							apm.getInvoiceList().add(in);
						}
					}
				}
				

			}
		}
		return JSON.toJSONString(apm);
	}
	
	private boolean isEmpty(Object obj) {
		return obj == null || obj.toString().equals("");
	}
}

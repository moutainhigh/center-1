package com.cmall.ordercenter.tallyorder.platform;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebSessionHelper;
import com.srnpr.zapweb.webexport.RootExport;
import com.srnpr.zapweb.webfactory.UserFactory;

/** 
 * 商户结算5.0-导出银企直连文件
 * 4497477900060001 : 商品行政待审核
 * 4497477900060002 : 商品行政审核通过
 * 4497477900060003 : 商品行政驳回
 * 4497477900060004 : 财务审核通过
 * 4497477900060005 : 财务驳回
 * 4497477900060006 : 财务已确认
 * 4497477900060007 : 财务已付款
 * 4497477900060008 : 财务反审核
 * 
 * 平台入驻商户结算单状态
 * 4497476900040008 : 未结算
 * 4497476900040009 : 已结算
 * 4497476900040010 : 待审核
 * 4497476900040011 : 已审核
 * @author zht
 */
public class ExportCorpPayForBankPlatform extends RootExport {
	private static SimpleDateFormat sdfStd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	@Override
	public void export(String sOperateId, HttpServletRequest request, HttpServletResponse response) {
		String payCode = request.getParameter("payCode");
		String sellerCode = request.getParameter("sellerCode");
		String sellerName = request.getParameter("sellerName");
		String createTimeFrom = request.getParameter("createTimeFrom");
		String createTimeTo = request.getParameter("createTimeTo");
		String isPay = request.getParameter("isPay");
		
		//只导出已确认的付款申请单
		String where = "";
		String sql = "select a.pay_code as pay_code, a.merchant_code as merchant_code, a.merchant_name as merchant_name, a.period_collect_amount_total as period_collect_amount_total, "
				+ "a.service_fee as service_fee, a.payable_collect_amount as payable_collect_amount, a.add_deduction as addContent_deduction, a.settle_collect_amount as settle_collect_amount,"
				+ "a.period_money as period_money,  a.actual_pay_amount as actual_pay_amount, "
				+ "a.create_time as create_time, a.pay_time as pay_time, b.bank_account as bank_account, b.branch_name as branch_name "
				+ "from ordercenter.oc_bill_apply_payment_pt a, usercenter.uc_seller_info_extend b "
				+ "where a.flag= '4497477900060006' and a.merchant_code=b.small_seller_code and a.actual_pay_amount>0.0 and (a.is_pay='4497477900020002' OR a.is_pay is null) ";
		if (!StringUtils.isEmpty(payCode)) {
			where = "and a.pay_code like '%" + payCode + "%' ";
		}
		if (!StringUtils.isEmpty(sellerCode)) {
			where += "and a.merchant_code like '%" + sellerCode + "%' ";
		}
		if (!StringUtils.isEmpty(sellerName)) {
			where += "and a.merchant_name like '%" + sellerName + "%' ";
		}
		if (!StringUtils.isEmpty(createTimeFrom)
				&& !StringUtils.isEmpty(createTimeTo)) {
			where += "and a.create_time >= '" + createTimeFrom
					+ "' and a.create_time <= '" + createTimeTo + "' ";
		} else if (!StringUtils.isEmpty(createTimeFrom)) {
			where += "and a.create_time >= '" + createTimeFrom + "' ";
		} else if (!StringUtils.isEmpty(createTimeTo)) {
			where += "and a.create_time <= '" + createTimeTo + "' ";
		}
		if (!StringUtils.isEmpty(isPay)) {
			where += "and a.is_pay ='" + isPay + "'";
		}
		try {
			applyPaymentToXml(sql + where, response);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void applyPaymentToXml(String sql, HttpServletResponse response) throws Exception {
		InputStream in = null;
		OutputStream outputStream = null;
		StringBuilder sb = new StringBuilder();
		try {
			List<Map<String, Object>> applyPayList = DbUp.upTable("oc_bill_apply_payment_pt").dataSqlList(sql, null);
			Element root = createRootNode("ufinterface");
			Document document = new Document(root);
			if(null != applyPayList && applyPayList.size() > 0) {
				for (Map<String, Object> applyPay : applyPayList) {
					Element header = null, body = null;
					Element entry = new Element("voucher");
					entry.setAttribute("id", applyPay.get("pay_code").toString());
					root.addContent(entry);
					try {
						header = createHeader("voucher_head", applyPay);
						body = createBody("voucher_body", applyPay);
						if(header != null && body != null) {
							entry.addContent(header);
							entry.addContent(body);
						}
						String payCode = StringUtils.isEmpty((String)applyPay.get("pay_code")) ? "" : applyPay.get("pay_code").toString();
						sb.append(payCode).append(" ");
					} catch(Exception e) {
						throw e;
					}
				}
			}
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
			outputStream = response.getOutputStream();
			response.setContentType("application/xml;charset=UTF-8");
			response.setHeader("Content-disposition", "attachment;filename=" + sdf.format(new Date()) + "_BankPay.xml");
			StringWriter sw = new StringWriter();
			XMLOutputter XMLOut = new XMLOutputter();  
	        try {  
	            Format f = Format.getPrettyFormat();  
	            f.setExpandEmptyElements(true);
	            f.setEncoding("UTF-8");
	            XMLOut.setFormat(f);  
	            XMLOut.output(document, sw); 
	            outputStream.write(sw.toString().getBytes("UTF-8"));
	            outputStream.flush();
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } 
	        
			String creator = UserFactory.INSTANCE.create().getLoginName();
			String ip = WebSessionHelper.create().upIpaddress();
			MDataMap logMap = new MDataMap();
			logMap.put("pay_code", "");
			logMap.put("flag", "4497477900010004_PAY");
			logMap.put("ip", ip);
			logMap.put("comment", sb.toString() + sw.toString());
			logMap.put("create_time", sdfStd.format(new Date()));
			logMap.put("creator", creator);
			DbUp.upTable("lc_apply_for_payment").dataInsert(logMap);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(null != in) in.close();
			if(null != outputStream) outputStream.close();
		}
	}
	
	private Element createRootNode(String nodeName) {
		Element root = new Element(nodeName);
		root.setAttribute("account", "0002");
		root.setAttribute("billtype", "D5");
		root.setAttribute("filename", "");
		root.setAttribute("isexchange", "Y");
		root.setAttribute("proc", "add");
		root.setAttribute("receiver", "01017");
		root.setAttribute("replace", "Y");
		root.setAttribute("roottag", "");
		root.setAttribute("sender", "ufbank017");
		root.setAttribute("subbilltype", "");
		return root;
	}
	
	/**
	 * 生成结算实体(一个结算周期内的某一商户结算单)的报文头信息
	 * @param nodeName 生成结算实体报文头结算名称
	 * @param applyPay 生成结算实体数据
	 */
	private Element createHeader(String nodeName, Map<String, Object> applyPay) {
		Element header = new Element(nodeName);
		
		//是否预收预付标志
		Element prepay = new Element("prepay");
		prepay.setText("N");
		header.addContent(prepay);
		
		//公司
		Element corp = new Element("corp");
		corp.setText("01017");
		header.addContent(corp);
		
		//交易类型
		Element businessType = new Element("businessType");
		businessType.setText("D5");
		header.addContent(businessType);
		
		//单据类型
		Element billTypeCode = new Element("billTypeCode");
		billTypeCode.setText("D5");
		header.addContent(billTypeCode);
		
		Element billnumber = new Element("billnumber");
		billnumber.setText("");
		header.addContent(billnumber);
		
		//单据日期
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Element billDate = new Element("billDate");
		billDate.setText(sdf.format(new Date()));
		header.addContent(billDate);
		
		//系统编号  0应收 1应付 2现金平台
		Element sysid = new Element("sysid");
		sysid.setText("2");
		header.addContent(sysid);
		
		//是否期初单据
		Element initFlag = new Element("initFlag");
		initFlag.setText("N");
		header.addContent(initFlag);
		
		//录入人
//		String creator = UserFactory.INSTANCE.create().getLoginName();
		Element inputOp = new Element("inputOp");
		inputOp.setText("lx");
		header.addContent(inputOp);
		
		//结算方式
		Element settleType = new Element("settleType");
		settleType.setText("02");
		header.addContent(settleType);
		
		//备注
		Element scomment = new Element("scomment");
		String comment = (StringUtils.isEmpty((String) applyPay.get("pay_code")) ? "" : applyPay.get("pay_code").toString());
				//+ "#" + (StringUtils.isEmpty((String) applyPay.get("create_time")) ? "" : applyPay.get("create_time").toString());
		scomment.setText(comment);
		header.addContent(scomment);
		
		//原币金额(待付款金额:wait_pay_amount)
		Element original_sum = new Element("original_sum");
		String actual_pay_amount = ObjectUtils.defaultIfNull(applyPay.get("actual_pay_amount"), new BigDecimal(0.0)).toString();
		try {
			Double.parseDouble(actual_pay_amount);
		} catch(NumberFormatException e) {
			actual_pay_amount = "0.0";
		}
		original_sum.setText(actual_pay_amount);
		header.addContent(original_sum);
		
		//辅币金额
		Element fractional_sum = new Element("fractional_sum");
		fractional_sum.setText("0.00000000");
		header.addContent(fractional_sum);
		
		//本币金额(待付款金额:wait_pay_amount)
		Element local_sum = new Element("local_sum");
		local_sum.setText(actual_pay_amount);
		header.addContent(local_sum);
		
		//??????
		Element lybz = new Element("lybz");
		lybz.setText("0");
		header.addContent(lybz);
		
		//??????
		Element billstatus = new Element("billstatus");
		billstatus.setText("1");
		header.addContent(billstatus);
		
		//??????
		Element kskhyh = new Element("kskhyh");
		kskhyh.setText("");
		header.addContent(kskhyh);

		return header;
	}
	
	/**
	 * 生成结算实体(一个结算周期内的某一商户结算单)的报文体信息
	 * @param nodeName 生成结算实体报文头结算名称
	 * @param applyPay 生成结算实体数据
	 * @return
	 * @throws Exception
	 */
	private Element createBody(String nodeName, Map<String, Object> applyPay) throws Exception {
		Element body = new Element(nodeName);
		
		Element entry = new Element("entry");
		body.addContent(entry);
		
		//金额方向
		Element sum_direction = new Element("sum_direction");
		sum_direction.setText("1");
		entry.addContent(sum_direction);
		
		//摘要
		Element digest = new Element("digest");
		String sellerName = StringUtils.isEmpty((String) applyPay.get("merchant_name")) ? "" : applyPay.get("merchant_name").toString();
		String smallSellerCode = StringUtils.isEmpty((String) applyPay.get("merchant_code")) ? "" : applyPay.get("merchant_code").toString();
		digest.setText("支付供应商" + sellerName + "[" + smallSellerCode  + "]" + "平台入驻商户结算货款");
		entry.addContent(digest);
		
		//结算方式
		Element settleType = new Element("settleType");
		settleType.setText("02");
		entry.addContent(settleType);
		
		//客商主键(用友要求改成客户名称)
		Element customer = new Element("customer");
		customer.setText(StringUtils.isEmpty((String) applyPay.get("merchant_name")) ? "" : applyPay.get("merchant_name").toString());
		entry.addContent(customer);
		
		//币种
		Element currencyId = new Element("currencyId");
		currencyId.setText("CNY");
		entry.addContent(currencyId);
		
		//本币汇率
		Element original_exchange_rate = new Element("original_exchange_rate");
		original_exchange_rate.setText("1");
		entry.addContent(original_exchange_rate);
		
		//辅币汇率
		Element fractional_exchange_rate = new Element("fractional_exchange_rate");
		fractional_exchange_rate.setText("0.00000000");
		entry.addContent(fractional_exchange_rate);
		
		//借方原币金额
		Element debit_original_sum = new Element("debit_original_sum");
		String actual_pay_amount = ObjectUtils.defaultIfNull(applyPay.get("actual_pay_amount"), new BigDecimal(0.0)).toString();
		try {
			Double.parseDouble(actual_pay_amount);
		} catch(NumberFormatException e) {
			actual_pay_amount = "0.0";
		}		
		debit_original_sum.setText(actual_pay_amount);
		entry.addContent(debit_original_sum);
		
		//借方辅币金额
		Element debit_fractional_sum = new Element("debit_fractional_sum");
		debit_fractional_sum.setText("0.00000000");
		entry.addContent(debit_fractional_sum);
		
		//借方本币金额
		Element debit_local_sum = new Element("debit_local_sum");
		debit_local_sum.setText(actual_pay_amount);
		entry.addContent(debit_local_sum);
		
		//原币余额
		Element original_balance = new Element("original_balance");
		original_balance.setText(actual_pay_amount);
		entry.addContent(original_balance);
		
		//辅币余额
		Element fractional_balance = new Element("fractional_balance");
		fractional_balance.setText("0.00000000");
		entry.addContent(fractional_balance);
		
		//本币余额
		Element local_balance = new Element("local_balance");
		local_balance.setText(actual_pay_amount);
		entry.addContent(local_balance);
		
		//数量余额
		Element quantity_balance = new Element("quantity_balance");
		quantity_balance.setText("0.00000000");
		entry.addContent(quantity_balance);
		
		//借方数量 
		Element debit_quantity = new Element("debit_quantity");
		debit_quantity.setText("0.00000000");
		entry.addContent(debit_quantity);
		
		//借方原币税金
		Element debit_original_tax = new Element("debit_original_tax");
		debit_original_tax.setText("0.00000000");
		entry.addContent(debit_original_tax);
		
		//借方辅币税金
		Element debit_fractional_tax = new Element("debit_fractional_tax");
		debit_fractional_tax.setText("0.00000000");
		entry.addContent(debit_fractional_tax);		
		
		//借方本币税金
		Element debit_local_tax = new Element("debit_local_tax");
		debit_local_tax.setText("0.00000000");
		entry.addContent(debit_local_tax);		
		
		//付款银行名称
		String payBankName = bConfig("ordercenter.PAY_BANK_NAME");
		Element pay_bankName = new Element("pay_bankName");
		pay_bankName.setText(payBankName);
		entry.addContent(pay_bankName);		
		
		//付款银行账户
		String payAccount = bConfig("ordercenter.PAY_BANK_ACCOUNT");
		Element pay_accounts = new Element("pay_accounts");
		pay_accounts.setText(payAccount);
		entry.addContent(pay_accounts);		
		
		//收款银行名称
		String branchName = (String) applyPay.get("branch_name");
		if(StringUtils.isEmpty(branchName)) {
			throw new Exception("seller bank name is empty!");
		}
		Element gather_bankName = new Element("gather_bankName");
		gather_bankName.setText(branchName);
		entry.addContent(gather_bankName);		
		
		//收款银行账户
		String bankAccount = (String) applyPay.get("bank_account");
		if(StringUtils.isEmpty(bankAccount)) {
			throw new Exception("seller bank account is empty!");
		}
		Element gather_accounts = new Element("gather_accounts");
		gather_accounts.setText(bankAccount);
		entry.addContent(gather_accounts);		
		
		//借方辅币无税金额
		Element debit_frac_noTax = new Element("debit_frac_noTax");
		debit_frac_noTax.setText("0.00000000");
		entry.addContent(debit_frac_noTax);		
		
		//贷方辅币无税金额
		Element credit_frac_noTax = new Element("credit_frac_noTax");
		credit_frac_noTax.setText("0.00000000");
		entry.addContent(credit_frac_noTax);		
		
		//借方本币无税金额
		Element debit_local_noTax = new Element("debit_local_noTax");
		debit_local_noTax.setText(actual_pay_amount);
		entry.addContent(debit_local_noTax);		
		
		//贷方原币金额
		Element credit_original_sum = new Element("credit_original_sum");
		credit_original_sum.setText("0.00000000");
		entry.addContent(credit_original_sum);		
		
		//贷方辅币金额
		Element credit_frac_sum = new Element("credit_frac_sum");
		credit_frac_sum.setText("0.00000000");
		entry.addContent(credit_frac_sum);		
		
		//贷方本币金额
		Element credit_local_sum = new Element("credit_local_sum");
		credit_local_sum.setText("0.00000000");
		entry.addContent(credit_local_sum);		
		
		//贷方数量
		Element credit_quantity = new Element("credit_quantity");
		credit_quantity.setText("0.00000000");
		entry.addContent(credit_quantity);		
		
		//贷方原币税金
		Element credit_original_Tax = new Element("credit_original_Tax");
		credit_original_Tax.setText("0.00000000");
		entry.addContent(credit_original_Tax);		
		
		//贷方辅币税金
		Element credit_frac_Tax = new Element("credit_frac_Tax");
		credit_frac_Tax.setText("0.00000000");
		entry.addContent(credit_frac_Tax);		
		
		//贷方本币税金
		Element credit_local_Tax = new Element("credit_local_Tax");
		credit_local_Tax.setText("0.00000000");
		entry.addContent(credit_local_Tax);		
		
		//往来对象  0客户，1供应商，2部门，3业务员
		Element object = new Element("object");
		object.setText("1");
		entry.addContent(object);		
		
		//借方原币无税金额
		Element debit_original_noTax = new Element("debit_original_noTax");
		debit_original_noTax.setText(actual_pay_amount);
		entry.addContent(debit_original_noTax);		
		
		//贷方原币无税金额
		Element credit_original_noTax = new Element("credit_original_noTax");
		credit_original_noTax.setText("0.00000000");
		entry.addContent(credit_original_noTax);		
		
		//贷方本币无税金额
		Element credit_local_noTax = new Element("credit_local_noTax");
		credit_local_noTax.setText("0.00000000");
		entry.addContent(credit_local_noTax);		
		
		//税号
		String taxNumber = bConfig("ordercenter.PAYER_TAX_NUMBER");
		Element tax_num = new Element("tax_num");
		tax_num.setText(taxNumber);
		entry.addContent(tax_num);		
		
		//部门编码
		Element deptid = new Element("deptid");
		deptid.setText("05");
		entry.addContent(deptid);
		
		//业务员编码
//		String operator = UserFactory.INSTANCE.create().getLoginName();
		Element employeeId = new Element("employeeId");
		employeeId.setText("05004");
		entry.addContent(employeeId);	
		
		//扣税类别--默认1
		Element Tax_Type = new Element("Tax_Type");
		Tax_Type.setText("1");
		entry.addContent(Tax_Type);	
		
		//交易类型-- 默认为0，客商
		Element tradertype = new Element("tradertype");
		tradertype.setText("0");
		entry.addContent(tradertype);	
		
		Element settlementinfo = new Element("settlementinfo");
		entry.addContent(settlementinfo);	
		

		Element settlement = new Element("settlement");
		settlementinfo.addContent(settlement);	
		
		//币种
		Element currency = new Element("currency");
		currency.setText("CNY");
		settlement.addContent(currency);	
		
		//公司
		Element corp = new Element("corp");
		corp.setText("01017");
		settlement.addContent(corp);	
		
		//原币付款
		Element pay = new Element("pay");
		pay.setText(actual_pay_amount);
		settlement.addContent(pay);	
		
		//本币付款
		Element paylocal = new Element("paylocal");
		paylocal.setText(actual_pay_amount);
		settlement.addContent(paylocal);	
		
		//资金账户-- 付款银行账户
		Element ownaccount = new Element("ownaccount");
		ownaccount.setText(payAccount);
		settlement.addContent(ownaccount);	
		
		//资金账户 --收款银行账户
		Element oppaccount = new Element("oppaccount");
		oppaccount.setText(bankAccount);
		settlement.addContent(oppaccount);	
				
		//本币汇率
		Element localrate = new Element("localrate");
		localrate.setText("1");
		settlement.addContent(localrate);		
		
		//对方类型-- 默认为0，客商
		Element tradertype1 = new Element("tradertype");
		tradertype1.setText("0");
		settlement.addContent(tradertype1);	
		
		//业务单单据类型
		Element tradername = new Element("tradername");
		tradername.setText("002");
		settlement.addContent(tradername);	
		
		//结算方式
		Element balatype = new Element("balatype");
		balatype.setText("02");
		settlement.addContent(balatype);	
		
		return body;
	}
}

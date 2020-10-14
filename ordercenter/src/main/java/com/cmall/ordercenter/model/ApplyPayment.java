package com.cmall.ordercenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 商户结算3.0 付款申请单
 * @author zht
 *
 */
public class ApplyPayment extends BaseClass {
	private String payCode;			//申请付款单号
	private String sellerCode;		//商户编码
	private String sellerName;		//商户名称
	private String comment;			//备注
	private String settleCodes;		//结算单编号
	private String invoiceCodes;	//发票号
	private String flag;			//状态
	private String rejectReason;	//拒绝原因
	
	//关联的商户发票信息
	private List<Invoice> invoiceList = new ArrayList<Invoice>();	
	//关联商户结算单信息
	private List<SellerFinancialStatement> settleList = new ArrayList<SellerFinancialStatement>();
	public String getPayCode() {
		return payCode;
	}
	public void setPayCode(String payCode) {
		this.payCode = payCode;
	}
	public String getSellerCode() {
		return sellerCode;
	}
	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}
	public String getSellerName() {
		return sellerName;
	}
	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}
	public List<Invoice> getInvoiceList() {
		return invoiceList;
	}
	public void setInvoiceList(List<Invoice> invoiceList) {
		this.invoiceList = invoiceList;
	}
	public List<SellerFinancialStatement> getSettleList() {
		return settleList;
	}
	public void setSettleList(List<SellerFinancialStatement> settleList) {
		this.settleList = settleList;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getSettleCodes() {
		return settleCodes;
	}
	public void setSettleCodes(String settleCodes) {
		this.settleCodes = settleCodes;
	}
	public String getInvoiceCodes() {
		return invoiceCodes;
	}
	public void setInvoiceCodes(String invoiceCodes) {
		this.invoiceCodes = invoiceCodes;
	}
	public String getFlag() {
		return flag;
	}
	public void setFlag(String flag) {
		this.flag = flag;
	}
	public String getRejectReason() {
		return rejectReason;
	}
	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
}

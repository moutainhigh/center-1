package com.cmall.usercenter.model.api;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 订单后期处理返回信息
 * @author jlin
 *
 */
public class ApiGetContractNewResult extends RootResultWeb {
	
	@ZapcomApi(value="供应商名称")
	private String sellerName="";
	
	@ZapcomApi(value="业务负责人")
	private String businessPerson="";
	
	@ZapcomApi(value="业务负责人联系电话")
	private String businessPersonPhone="";
	
	@ZapcomApi(value="公司电话")
	private String companyPhone="";
	
	@ZapcomApi(value="发票寄回地址")
	private String invoiceReturnAddress="";
	
	@ZapcomApi(value="发票回寄收件人")
	private String invoiceReturnPerson="";
	
	@ZapcomApi(value="发票回寄收件人电话")
	private String invoiceReturnPhone="";
	
	@ZapcomApi(value="营业执照类型")
	private String businessLicenseType="";
	
	@ZapcomApi(value="注册号(营业执照号)")
	private String registrationNumber="";
	
	@ZapcomApi(value="招商经理")
	private String registerName="";
	
	@ZapcomApi(value="开户行支行名称")
	private String branchName="";
	
	@ZapcomApi(value="账号")
	private String bankAccount="";
	
	@ZapcomApi(value="开户行支行所在地")
	private String branchAddress="";
	
	@ZapcomApi(value="商户类型")
	private String ucSellerType="";
	
	@ZapcomApi(value="结算周期")
	private String accountClearType="";
	
	@ZapcomApi(value="保证金收取方式")
	private String moneyCollectionWay="";
	
	@ZapcomApi(value="质保金比例")
	private String moneyProportion="";
	
	@ZapcomApi(value="最大质保金")
	private String qualityRetentionMoney="";

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getBusinessPerson() {
		return businessPerson;
	}

	public void setBusinessPerson(String businessPerson) {
		this.businessPerson = businessPerson;
	}

	public String getBusinessPersonPhone() {
		return businessPersonPhone;
	}

	public void setBusinessPersonPhone(String businessPersonPhone) {
		this.businessPersonPhone = businessPersonPhone;
	}

	public String getCompanyPhone() {
		return companyPhone;
	}

	public void setCompanyPhone(String companyPhone) {
		this.companyPhone = companyPhone;
	}

	public String getInvoiceReturnAddress() {
		return invoiceReturnAddress;
	}

	public void setInvoiceReturnAddress(String invoiceReturnAddress) {
		this.invoiceReturnAddress = invoiceReturnAddress;
	}

	public String getInvoiceReturnPerson() {
		return invoiceReturnPerson;
	}

	public void setInvoiceReturnPerson(String invoiceReturnPerson) {
		this.invoiceReturnPerson = invoiceReturnPerson;
	}

	public String getInvoiceReturnPhone() {
		return invoiceReturnPhone;
	}

	public void setInvoiceReturnPhone(String invoiceReturnPhone) {
		this.invoiceReturnPhone = invoiceReturnPhone;
	}

	public String getBusinessLicenseType() {
		return businessLicenseType;
	}

	public void setBusinessLicenseType(String businessLicenseType) {
		this.businessLicenseType = businessLicenseType;
	}

	public String getRegistrationNumber() {
		return registrationNumber;
	}

	public void setRegistrationNumber(String registrationNumber) {
		this.registrationNumber = registrationNumber;
	}

	public String getRegisterName() {
		return registerName;
	}

	public void setRegisterName(String registerName) {
		this.registerName = registerName;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getBankAccount() {
		return bankAccount;
	}

	public void setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
	}

	public String getBranchAddress() {
		return branchAddress;
	}

	public void setBranchAddress(String branchAddress) {
		this.branchAddress = branchAddress;
	}

	public String getUcSellerType() {
		return ucSellerType;
	}

	public void setUcSellerType(String ucSellerType) {
		this.ucSellerType = ucSellerType;
	}

	public String getAccountClearType() {
		return accountClearType;
	}

	public void setAccountClearType(String accountClearType) {
		this.accountClearType = accountClearType;
	}

	public String getMoneyCollectionWay() {
		return moneyCollectionWay;
	}

	public void setMoneyCollectionWay(String moneyCollectionWay) {
		this.moneyCollectionWay = moneyCollectionWay;
	}

	public String getMoneyProportion() {
		return moneyProportion;
	}

	public void setMoneyProportion(String moneyProportion) {
		this.moneyProportion = moneyProportion;
	}

	public String getQualityRetentionMoney() {
		return qualityRetentionMoney;
	}

	public void setQualityRetentionMoney(String qualityRetentionMoney) {
		this.qualityRetentionMoney = qualityRetentionMoney;
	}

	
}

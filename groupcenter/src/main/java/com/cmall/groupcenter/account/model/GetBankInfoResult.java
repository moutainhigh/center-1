package com.cmall.groupcenter.account.model;


import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class GetBankInfoResult extends RootResultWeb {

	@ZapcomApi(value = "银行列表")
	private List<BankInfo> bankList = new ArrayList<BankInfo>();
	

	public List<BankInfo> getBankList() {
		return bankList;
	}


	public void setBankList(List<BankInfo> bankList) {
		this.bankList = bankList;
	}


	public class BankInfo{
		
		@ZapcomApi(value = "所属银行")
		private String bankName = "";
		@ZapcomApi(value = "银行卡名称")
		private String bankCardAlias = "";
		@ZapcomApi(value = "卡种")
		private String cardKind = "";
		@ZapcomApi(value = "尾号")
		private String tailNumber = "";
		@ZapcomApi(value = "银行代码")
		private String bankCode = "";
		@ZapcomApi(value = "是否默认银行卡",remark="1为默认")
		private String isDefault = "0";
		@ZapcomApi(value = "银行卡是否可用",remark="1为可用，0为不可用")
		private String isEnable = "1";
		@ZapcomApi(value = "预留手机号")
		private String bankPhone = "";
		@ZapcomApi(value = "卡号",remark="显示前两位后四位中间*代替")
		private String  cardNumber= "";
		@ZapcomApi(value = "银行卡logo",remark="银行卡logo")
		private String  iconUrl= "";
		
		public String getCardNumber() {
			return cardNumber;
		}
		public void setCardNumber(String cardNumber) {
			this.cardNumber = cardNumber;
		}
		public String getBankPhone() {
			return bankPhone;
		}
		public void setBankPhone(String bankPhone) {
			this.bankPhone = bankPhone;
		}
		public String getBankName() {
			return bankName;
		}
		public void setBankName(String bankName) {
			this.bankName = bankName;
		}
		public String getBankCardAlias() {
			return bankCardAlias;
		}
		public void setBankCardAlias(String bankCardAlias) {
			this.bankCardAlias = bankCardAlias;
		}
		public String getCardKind() {
			return cardKind;
		}
		public void setCardKind(String cardKind) {
			this.cardKind = cardKind;
		}
		public String getTailNumber() {
			return tailNumber;
		}
		public void setTailNumber(String tailNumber) {
			this.tailNumber = tailNumber;
		}
		public String getBankCode() {
			return bankCode;
		}
		public void setBankCode(String bankCode) {
			this.bankCode = bankCode;
		}
		public String getIsDefault() {
			return isDefault;
		}
		public void setIsDefault(String isDefault) {
			this.isDefault = isDefault;
		}
		public String getIsEnable() {
			return isEnable;
		}
		public void setIsEnable(String isEnable) {
			this.isEnable = isEnable;
		}
		public String getIconUrl() {
			return iconUrl;
		}
		public void setIconUrl(String iconUrl) {
			this.iconUrl = iconUrl;
		}
		
	}
}

package com.cmall.groupcenter;

import java.util.HashMap;
import java.util.Map;

public class GroupConstant {

	public static final Map<String,String> cardTypeMap = new HashMap<String,String>();
	
	static {
		cardTypeMap.put("1", "储蓄卡");
		cardTypeMap.put("2", "信用卡");
	}
	
	public static final Map<String,String> APPCODEMAP = new HashMap<String,String>();
	static{
		APPCODEMAP.put("449747230001", "SI2003");
	}
	
	
	public enum PapersEnum{
		
		idCard(1,"4497465200090001","身份证"),officerCard(2,"4497465200090002","军官证"),passportCard(3,"4497465200090003","护照"),
		returnHomeCard(4,"4497465200090004","回乡证"),mtpsCard(5,"4497465200090005","台胞证"),policeOfficerCard(6,"4497465200090006","警官证"),
		sergeantCard(7,"4497465200090007","士兵证"),otherCard(8,"4497465200090008","其它证件");
		
		private int cNo;
		
		private String cardType;
		
		private String cardAlias;
		
		private PapersEnum(int cNo,String cardType,String cardAlias){
			this.cNo = cNo;
			this.cardType = cardType;
			this.cardAlias = cardAlias;
		}
		
		public static String getCardTypeByCno(int cNo){
			for(PapersEnum pe : PapersEnum.values()){
				if(pe.getcNo() == cNo){
					return pe.getCardType();
				}
			}
			return null;
		}

		public static String getCnoByCardType(String cardType){
			for(PapersEnum pe : PapersEnum.values()){
				if(pe.getCardType().equals(cardType)){
					return pe.getcNo()+"";
				}
			}
			return null;
		}
		public static String getCardAliasByCardType(String cardType){
			for(PapersEnum pe : PapersEnum.values()){
				if(pe.getCardType().equals(cardType)){
					return pe.getCardAlias();
				}
			}
			return null;
		}
		public int getcNo() {
			return cNo;
		}

		public String getCardType() {
			return cardType;
		}

		public String getCardAlias() {
			return cardAlias;
		}

		public void setCardAlias(String cardAlias) {
			this.cardAlias = cardAlias;
		}
		
	}
	
	public enum PayOrderStatusEnum {
		nonpayment(1,"4497153900010001","下单成功-未付款"),nonshipped(2,"4497153900010002","下单成功-未发货"),shipped(3,"4497153900010003","已发货"),
		tradeSuccess(4,"4497153900010005","交易成功"),tradeFailed(5,"4497153900010006","交易失败");
		
		private int sNo;
		private String markCode;
		private String description;
		
		private PayOrderStatusEnum(int sNo,String markCode,String description){
			this.sNo = sNo;
			this.markCode = markCode;
			this.description = description;
		}
		
		public static String getPayOrderStatusBySno(int sNo){
			for(PayOrderStatusEnum ps : PayOrderStatusEnum.values()){
				if(ps.getsNo() == sNo){
					return ps.getMarkCode();
				}
			}
			return null;
		}

		public int getsNo() {
			return sNo;
		}

		public void setsNo(int sNo) {
			this.sNo = sNo;
		}

		public String getMarkCode() {
			return markCode;
		}

		public void setMarkCode(String markCode) {
			this.markCode = markCode;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}
		
	}




	public enum WalletChangeTypeEnum{

		transferAmount(1,"4497476000020001","转账"),returnBackAmmount(2,"4497476000020002","退款"),withdraw(3,"4497476000020003","提现");

		private int cNo;

		private String type;

		private String alias;

		private WalletChangeTypeEnum(int cNo,String cardType,String cardAlias){
			this.cNo = cNo;
			this.type = cardType;
			this.alias = cardAlias;
		}

		public int getcNo() {
			return cNo;
		}


		public String getType() {
			return type;
		}


		public String getAlias() {
			return alias;
		}

	}

}

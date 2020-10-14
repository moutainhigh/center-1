package com.cmall.groupcenter;

public class FamilyConfig {
	
	/**订单状态：下单成功-未付款*/
	public static final String ORDER_STATUS_UNPAY = "4497153900010001";
	/**订单状态：下单成功-未发货*/
	public static final String ORDER_STATUS_PAYED = "4497153900010002";
	/**已发货*/
	public static final String ORDER_STATUS_DELIVERED = "4497153900010003";
	/**交易成功*/
	public static final String ORDER_STATUS_TRADE_SUCCESS = "4497153900010005";
	/**交易失败*/
	public static final String ORDER_STATUS_TRADE_FAILURE = "4497153900010006";
	
	/**微公社定订单同步值*/
	public static final String ORDER_RSYNC_VALUE = "449747560001";
	
	/**微公社定订单同步单位：年、月、日、时、分、秒*/
	public static final String ORDER_RSYNC_UNIT = "449747560002";
	
	/**微公社定订单同步单位：年、月、日、时、分、秒*/
	public static final String ORDER_RSYNC_DATE = "449747560003";
	
	/**是否参与清分：1 是 0 否*/
	public static final String ISRECKON_YES = "1";
	/**是否参与清分：1 是 0 否*/
	public static final String ISRECKON_NO = "0";
	/**接口同步标志：1 成功 0 失败*/
	public static final String RSYNC_SUCCESS = "1";
	/**接口同步标志：1 成功 0 失败*/
	public static final String RSYNC_FAILURE = "0";
}

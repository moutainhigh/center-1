package com.cmall.ordercenter.helper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.srnpr.zapcom.basehelper.JsonHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;

public class OrderHelper {

	public static String upOrderCodeByOutCode(String sOrderCode) {
		if (StringUtils.isNotEmpty(sOrderCode) && sOrderCode.length() == 8) {
			sOrderCode = DbUp
					.upTable("oc_orderinfo")
					.dataGet("order_code", "",
							new MDataMap("out_order_code", sOrderCode))
					.toString();
		}
		
//		else if(StringUtils.isNotEmpty(sOrderCode) && "OS".equals(sOrderCode.substring(0, 2))){
//			sOrderCode = DbUp
//					.upTable("oc_orderinfo_upper")
//					.dataGet("big_order_code", "",
//							new MDataMap("big_order_code", sOrderCode))
//					.toString();
//		}

		return sOrderCode;
	}
	
	public static String getOrderCodeByOutCode(String sOrderCode){
		Object orderinfo=null;
		if (StringUtils.isNotEmpty(sOrderCode) && sOrderCode.length() != 8) {
			 orderinfo = DbUp.upTable("oc_orderinfo").dataGet("out_order_code", "",new MDataMap("order_code", sOrderCode));
		}
		return orderinfo!=null?orderinfo.toString():"";
	}
	
	public static String convertStatusCode(String statusCode){
		String status = "";
		switch (statusCode) {
			case "4497153900010001":
				status = "下单成功未付款";
				break;
			case "4497153900010002":
				status = "下单成功未发货";
				break;
			case "4497153900010003":
				status = "已发货";
				break;
			case "4497153900010004":
				status = "已收货";
				break;
			case "4497153900010005":
				status = "交易成功";
				break;
			case "4497153900010006":
				status = "交易失败";
				break;
			default:
				status = "状态错误";
				break;
		}
		return status;
	}
}

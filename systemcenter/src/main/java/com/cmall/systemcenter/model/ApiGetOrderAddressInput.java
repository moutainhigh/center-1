package com.cmall.systemcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class ApiGetOrderAddressInput extends RootInput{

	@ZapcomApi(value = "订单编号", remark="订单编号" )
	private String orderCode  = "";
	
	@ZapcomApi(value = "sql类型", remark="1,2" )
	private int sqlType  = 1;
	


	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public int getSqlType() {
		return sqlType;
	}

	public void setSqlType(int sqlType) {
		this.sqlType = sqlType;
	}


	
	
}

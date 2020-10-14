package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;

/**
 * 5.2.6 取消TV品订单返回信息
 * @author cc
 *
 */
public class RsyncModelRecordTvOrderStat {

	/**
	 * 订单号
	 */
	private Long ORD_ID;
	
	/**
	 * 订单序号
	 */
	private Integer ORD_SEQ;
	
	/**
	 * 客户编号
	 */
	private Long CUST_ID;
	
	/**
	 * 订单金额
	 */
	private BigDecimal ORD_AMT = BigDecimal.ZERO;
	
	/**
	 * 收货地址
	 */
	private String ADDR;
	
	/**
	 * 商品名称
	 */
	private String GOOD_NM;

	public Long getORD_ID() {
		return ORD_ID;
	}

	public void setORD_ID(Long oRD_ID) {
		ORD_ID = oRD_ID;
	}

	public Integer getORD_SEQ() {
		return ORD_SEQ;
	}

	public void setORD_SEQ(Integer oRD_SEQ) {
		ORD_SEQ = oRD_SEQ;
	}

	public Long getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(Long cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public BigDecimal getORD_AMT() {
		return ORD_AMT;
	}

	public void setORD_AMT(BigDecimal oRD_AMT) {
		ORD_AMT = oRD_AMT;
	}

	public String getADDR() {
		return ADDR;
	}

	public void setADDR(String aDDR) {
		ADDR = aDDR;
	}

	public String getGOOD_NM() {
		return GOOD_NM;
	}

	public void setGOOD_NM(String gOOD_NM) {
		GOOD_NM = gOOD_NM;
	}
	
}

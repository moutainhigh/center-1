package com.cmall.groupcenter.hserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 订单状态实体
 * 
 * @author jlin
 *
 */
public class OrderStatus {

	@JsonProperty(value = "ORD_ID")  
	private String ORD_ID = "";
	
	@JsonProperty(value = "ORD_SEQ")  
	private String ORD_SEQ = "";
	
	@JsonProperty(value = "ORD_STAT_CD")  
	private String ORD_STAT_CD = "";
	
	@JsonProperty(value = "COD_STAT_CD")  
	private String COD_STAT_CD = "";
	
	@JsonProperty(value = "CHANGE_CD")  
	private String CHANGE_CD = "";

	public String getORD_ID() {
		return ORD_ID;
	}

	public void setORD_ID(String oRD_ID) {
		ORD_ID = oRD_ID;
	}

	public String getORD_SEQ() {
		return ORD_SEQ;
	}

	public void setORD_SEQ(String oRD_SEQ) {
		ORD_SEQ = oRD_SEQ;
	}

	public String getORD_STAT_CD() {
		return ORD_STAT_CD;
	}

	public void setORD_STAT_CD(String oRD_STAT_CD) {
		ORD_STAT_CD = oRD_STAT_CD;
	}

	public String getCOD_STAT_CD() {
		return COD_STAT_CD;
	}

	public void setCOD_STAT_CD(String cOD_STAT_CD) {
		COD_STAT_CD = cOD_STAT_CD;
	}

	public String getCHANGE_CD() {
		return CHANGE_CD;
	}

	public void setCHANGE_CD(String cHANGE_CD) {
		CHANGE_CD = cHANGE_CD;
	}

}

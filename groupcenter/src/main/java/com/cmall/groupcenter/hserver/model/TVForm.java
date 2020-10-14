package com.cmall.groupcenter.hserver.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 节目单状态实体
 * 
 * @author jlin
 *
 */
public class TVForm {

	@JsonProperty(value = "FORM_ID")  
	private String FORM_ID = "";
	
	@JsonProperty(value = "TITLE_NM")  
	private String TITLE_NM = "";
	
	@JsonProperty(value = "FORM_FR_DATE")  
	private String FORM_FR_DATE = "";
	
	@JsonProperty(value = "FORM_END_DATE")  
	private String FORM_END_DATE = "";
	
	@JsonProperty(value = "FORM_CD")  
	private String FORM_CD = "";
	
	@JsonProperty(value = "FORM_SEQ")  
	private String FORM_SEQ = "";
	
	@JsonProperty(value = "SO_ID")  
	private String SO_ID = "";
	
	@JsonProperty(value = "GOOD_ID")  
	private String GOOD_ID = "";
	
	@JsonProperty(value = "FORM_GOOD_MIS")  
	private String FORM_GOOD_MIS = "";
	
	@JsonProperty(value = "CHANGE_CD")  
	private String CHANGE_CD = "";

	public String getFORM_ID() {
		return FORM_ID;
	}

	public void setFORM_ID(String fORM_ID) {
		FORM_ID = fORM_ID;
	}

	public String getTITLE_NM() {
		return TITLE_NM;
	}

	public void setTITLE_NM(String tITLE_NM) {
		TITLE_NM = tITLE_NM;
	}

	public String getFORM_FR_DATE() {
		return FORM_FR_DATE;
	}

	public void setFORM_FR_DATE(String fORM_FR_DATE) {
		FORM_FR_DATE = fORM_FR_DATE;
	}

	public String getFORM_END_DATE() {
		return FORM_END_DATE;
	}

	public void setFORM_END_DATE(String fORM_END_DATE) {
		FORM_END_DATE = fORM_END_DATE;
	}

	public String getFORM_CD() {
		return FORM_CD;
	}

	public void setFORM_CD(String fORM_CD) {
		FORM_CD = fORM_CD;
	}

	public String getFORM_SEQ() {
		return FORM_SEQ;
	}

	public void setFORM_SEQ(String fORM_SEQ) {
		FORM_SEQ = fORM_SEQ;
	}

	public String getSO_ID() {
		return SO_ID;
	}

	public void setSO_ID(String sO_ID) {
		SO_ID = sO_ID;
	}

	public String getGOOD_ID() {
		return GOOD_ID;
	}

	public void setGOOD_ID(String gOOD_ID) {
		GOOD_ID = gOOD_ID;
	}

	public String getFORM_GOOD_MIS() {
		return FORM_GOOD_MIS;
	}

	public void setFORM_GOOD_MIS(String fORM_GOOD_MIS) {
		FORM_GOOD_MIS = fORM_GOOD_MIS;
	}

	public String getCHANGE_CD() {
		return CHANGE_CD;
	}

	public void setCHANGE_CD(String cHANGE_CD) {
		CHANGE_CD = cHANGE_CD;
	}

}

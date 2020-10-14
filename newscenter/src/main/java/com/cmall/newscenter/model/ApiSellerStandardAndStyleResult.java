package com.cmall.newscenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 规格  和 尺码 key value
 * @author wz
 *
 */
public class ApiSellerStandardAndStyleResult {
	@ZapcomApi(value="规格/款式key")
	private String StandardAndStyleKey = "";
	@ZapcomApi(value="规格/款式value")
	private String StandardAndStyleValue = "";
	
	public String getStandardAndStyleKey() {
		return StandardAndStyleKey;
	}
	public void setStandardAndStyleKey(String standardAndStyleKey) {
		StandardAndStyleKey = standardAndStyleKey;
	}
	public String getStandardAndStyleValue() {
		return StandardAndStyleValue;
	}
	public void setStandardAndStyleValue(String standardAndStyleValue) {
		StandardAndStyleValue = standardAndStyleValue;
	}
}

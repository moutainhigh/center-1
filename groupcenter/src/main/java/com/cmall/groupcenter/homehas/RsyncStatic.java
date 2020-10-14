package com.cmall.groupcenter.homehas;

import com.srnpr.zapweb.webface.IWebStatic;

public class RsyncStatic implements IWebStatic {

	
	
	
	private String codeValue="";
	
	
	
	public String getCodeValue() {
		return codeValue;
	}

	public void setCodeValue(String codeValue) {
		this.codeValue = codeValue;
	}

	

	
	public String upCode() {
		
		return codeValue;
	}

	
	public String upDefault() {
		
		return "";
	}

}

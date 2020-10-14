package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 联想结果返回集
 * @author zhouguohui
 *
 */
public class SearchAssociate {

	@ZapcomApi(value="联想词条")
	private  String associateWord;

	@ZapcomApi(value="联想词条结果数目")
	private int associateWordNum;
	
	
	/**
	 * @return the associateWord
	 */
	public String getAssociateWord() {
		return associateWord;
	}
	/**
	 * @param associateWord the associateWord to set
	 */
	public void setAssociateWord(String associateWord) {
		this.associateWord = associateWord;
	}
	/**
	 * @return the associateWordNum
	 */
	public int getAssociateWordNum() {
		return associateWordNum;
	}
	/**
	 * @param associateWordNum the associateWordNum to set
	 */
	public void setAssociateWordNum(int associateWordNum) {
		this.associateWordNum = associateWordNum;
	}
	
}

package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 热词实体
 * @author zhouguohui
 *
 */
public class HotWord {

	@ZapcomApi(value="热词")
	private String hotWord;

	/**
	 * @return the hotWord
	 */
	public String getHotWord() {
		return hotWord;
	}

	/**
	 * @param hotWord the hotWord to set
	 */
	public void setHotWord(String hotWord) {
		this.hotWord = hotWord;
	}
	
}

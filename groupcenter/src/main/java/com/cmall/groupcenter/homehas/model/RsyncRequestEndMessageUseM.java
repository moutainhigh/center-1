package com.cmall.groupcenter.homehas.model;

import com.cmall.groupcenter.groupface.IRsyncRequest;

public class RsyncRequestEndMessageUseM implements IRsyncRequest {

	/**
	 * 手机号码
	 */
	private String hp_tel = "";

	/**
	 * 内容
	 */
	private String content = "";

	public String getHp_tel() {
		return hp_tel;
	}

	public void setHp_tel(String hp_tel) {
		this.hp_tel = hp_tel;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}

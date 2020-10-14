package com.cmall.newscenter.model;

/**
 * 敏感词导入model
 * @author wei.che
 *date 2015-09-10
 */
public class SensitiveWordImport {
	/**
	 * 主键：自增ID
	 */
	private int zid;
	/**
	 * 扩展ID
	 */
	private String uid;
	/**
	 * 敏感词
	 */
	private String sensitiveWord;
	
	public int getZid() {
		return zid;
	}
	public void setZid(int zid) {
		this.zid = zid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getSensitiveWord() {
		return sensitiveWord;
	}
	public void setSensitiveWord(String sensitiveWord) {
		this.sensitiveWord = sensitiveWord;
	}
}

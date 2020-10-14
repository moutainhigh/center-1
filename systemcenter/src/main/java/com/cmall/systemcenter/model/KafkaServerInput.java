package com.cmall.systemcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * storm查询输入参数
 * @author zhouguohui
 * @version 1.0
 */
public class KafkaServerInput extends RootInput{
	@ZapcomApi(value = "查询一条还是多条", require = 1, remark = "O：代表一条，N代表多条，如果为别的值默认为N")
	private String queryListOrOne="";
	@ZapcomApi(value = "查询表明", require = 1, remark = "查询那张表  必须base64加密")
	private String sqlName="";
	@ZapcomApi(value = "查询sql语句", require = 1, remark = "查询的sql语句 必须base64加密")
	private String sqlValue="";
	
	/**
	 * @return the queryListOrOne
	 */
	public String getQueryListOrOne() {
		return queryListOrOne;
	}
	/**
	 * @param queryListOrOne the queryListOrOne to set
	 */
	public void setQueryListOrOne(String queryListOrOne) {
		this.queryListOrOne = queryListOrOne;
	}
	/**
	 * @return the sqlName
	 */
	public String getSqlName() {
		return sqlName;
	}
	/**
	 * @param sqlName the sqlName to set
	 */
	public void setSqlName(String sqlName) {
		this.sqlName = sqlName;
	}
	/**
	 * @return the sqlValue
	 */
	public String getSqlValue() {
		return sqlValue;
	}
	/**
	 * @param sqlValue the sqlValue to set
	 */
	public void setSqlValue(String sqlValue) {
		this.sqlValue = sqlValue;
	}
	
	
}

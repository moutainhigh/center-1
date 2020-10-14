package com.cmall.productcenter.model;

import java.util.List;

/**
 * solr索引库创建时 分类实体类
 * @author zhouguohui
 *
 */
public class SolrDataFacet {
	
	
	/***活动和赠品只在搜索的时间用到**/
	private List<String> isHuoDong;
	private List<String> isZengPin;
	/***一下实体类只为创建索引时使用**/
	private List<String> oneId;
	private List<String> oneName;
	private List<String> twoId;
	private List<String> twoName;
	/**
	 * @return the oneId
	 */
	public List<String> getOneId() {
		return oneId;
	}
	/**
	 * @param oneId the oneId to set
	 */
	public void setOneId(List<String> oneId) {
		this.oneId = oneId;
	}
	/**
	 * @return the oneName
	 */
	public List<String> getOneName() {
		return oneName;
	}
	/**
	 * @param oneName the oneName to set
	 */
	public void setOneName(List<String> oneName) {
		this.oneName = oneName;
	}
	/**
	 * @return the twoId
	 */
	public List<String> getTwoId() {
		return twoId;
	}
	/**
	 * @param twoId the twoId to set
	 */
	public void setTwoId(List<String> twoId) {
		this.twoId = twoId;
	}
	/**
	 * @return the twoName
	 */
	public List<String> getTwoName() {
		return twoName;
	}
	/**
	 * @param twoName the twoName to set
	 */
	public void setTwoName(List<String> twoName) {
		this.twoName = twoName;
	}
	/**
	 * @return the isHuoDong
	 */
	public List<String> getIsHuoDong() {
		return isHuoDong;
	}
	/**
	 * @param isHuoDong the isHuoDong to set
	 */
	public void setIsHuoDong(List<String> isHuoDong) {
		this.isHuoDong = isHuoDong;
	}
	/**
	 * @return the isZengPin
	 */
	public List<String> getIsZengPin() {
		return isZengPin;
	}
	/**
	 * @param isZengPin the isZengPin to set
	 */
	public void setIsZengPin(List<String> isZengPin) {
		this.isZengPin = isZengPin;
	}
	
	
	
}

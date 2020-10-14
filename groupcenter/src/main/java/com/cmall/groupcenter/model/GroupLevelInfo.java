package com.cmall.groupcenter.model;

import java.math.BigDecimal;

public class GroupLevelInfo {

	/**
	 * 清分比例
	 */
	private BigDecimal scaleReckon;

	/**
	 * 级别编号
	 */
	private String levelCode;

	/**
	 * 升级所需用户数
	 */
	private int upgradeMembers;

	/**
	 * 升级所需消费金额
	 */
	private BigDecimal upgradeConsume;

	/**
	 * 计算消费金额深度
	 */
	private int deepConsume;

	/**
	 * 计算清分深度
	 */
	private int deepReckon;

	/**
	 * 下一级别编号
	 */
	private String nextLevel;

	/**
	 * 级别类型
	 */
	private String levelType;

	/**
	 * 级别名称
	 */
	private String levelName;

	public BigDecimal getScaleReckon() {
		return scaleReckon;
	}

	public void setScaleReckon(BigDecimal scaleReckon) {
		this.scaleReckon = scaleReckon;
	}

	public String getLevelCode() {
		return levelCode;
	}

	public void setLevelCode(String levelCode) {
		this.levelCode = levelCode;
	}

	public int getUpgradeMembers() {
		return upgradeMembers;
	}

	public void setUpgradeMembers(int upgradeMembers) {
		this.upgradeMembers = upgradeMembers;
	}

	public BigDecimal getUpgradeConsume() {
		return upgradeConsume;
	}

	public void setUpgradeConsume(BigDecimal upgradeConsume) {
		this.upgradeConsume = upgradeConsume;
	}

	public int getDeepConsume() {
		return deepConsume;
	}

	public void setDeepConsume(int deepConsume) {
		this.deepConsume = deepConsume;
	}

	public int getDeepReckon() {
		return deepReckon;
	}

	public void setDeepReckon(int deepReckon) {
		this.deepReckon = deepReckon;
	}

	public String getNextLevel() {
		return nextLevel;
	}

	public void setNextLevel(String nextLevel) {
		this.nextLevel = nextLevel;
	}

	public String getLevelType() {
		return levelType;
	}

	public void setLevelType(String levelType) {
		this.levelType = levelType;
	}

	public String getLevelName() {
		return levelName;
	}

	public void setLevelName(String levelName) {
		this.levelName = levelName;
	}

}

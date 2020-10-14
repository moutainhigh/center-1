package com.cmall.productcenter.model;


/**
 * 首页版式栏目
 * 
 * @author ligj
 * 
 */
public class HomeColumnModel {

	private String columnCode = "";

	private String columnName = "";

	private String startTime = "";

	private String endTime = "";

	private String columnType = "";

	private String isShowmore = "";

	private String showmoreTitle = "";

	private String showmoreLinktype = "";

	private String showmoreLinkvalue = "";

	private int intervalSecond = 1;

	private String showName = "";

	private String noticeType = "";

	public String getColumnCode() {
		return columnCode;
	}

	public void setColumnCode(String columnCode) {
		this.columnCode = columnCode;
	}

	public String getNoticeType() {
		return noticeType;
	}

	public void setNoticeType(String noticeType) {
		this.noticeType = noticeType;
	}

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	public String getColumnType() {
		return columnType;
	}

	public void setColumnType(String columnType) {
		this.columnType = columnType;
	}

	public String getIsShowmore() {
		return isShowmore;
	}

	public void setIsShowmore(String isShowmore) {
		this.isShowmore = isShowmore;
	}

	public String getShowmoreTitle() {
		return showmoreTitle;
	}

	public void setShowmoreTitle(String showmoreTitle) {
		this.showmoreTitle = showmoreTitle;
	}

	public String getShowmoreLinktype() {
		return showmoreLinktype;
	}

	public void setShowmoreLinktype(String showmoreLinktype) {
		this.showmoreLinktype = showmoreLinktype;
	}

	public String getShowmoreLinkvalue() {
		return showmoreLinkvalue;
	}

	public void setShowmoreLinkvalue(String showmoreLinkvalue) {
		this.showmoreLinkvalue = showmoreLinkvalue;
	}

	public int getIntervalSecond() {
		return intervalSecond;
	}

	public void setIntervalSecond(int intervalSecond) {
		this.intervalSecond = intervalSecond;
	}

	public String getShowName() {
		return showName;
	}

	public void setShowName(String showName) {
		this.showName = showName;
	}

}

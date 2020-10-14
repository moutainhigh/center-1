package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;


/**
 * 分页返回字段描述
 * @author zhouguohui
 *
 */
public class Pager {
	
	@ZapcomApi(value="当前页码")
   private int pageNo;

	@ZapcomApi(value="每页显示记录数")
   private int pageSize;
   
	@ZapcomApi(value="总页数")
   private int pageNum;
   
	@ZapcomApi(value="总记录数")
   private int recordNum;
	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}
	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return the pageNum
	 */
	public int getPageNum() {
		return pageNum;
	}
	/**
	 * @param pageNum the pageNum to set
	 */
	public void setPageNum(int pageNum) {
		this.pageNum = pageNum;
	}
	/**
	 * @return the recordNum
	 */
	public int getRecordNum() {
		return recordNum;
	}
	/**
	 * @param recordNum the recordNum to set
	 */
	public void setRecordNum(int recordNum) {
		this.recordNum = recordNum;
	}
   
}

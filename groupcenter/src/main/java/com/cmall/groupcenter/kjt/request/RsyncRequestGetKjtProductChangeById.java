package com.cmall.groupcenter.kjt.request;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 根据时间段内价格变化的商品ID列表获取  输入参数
 * @author liqt
 *
 */

public class RsyncRequestGetKjtProductChangeById implements IRsyncRequest{
	/**
	 * 订阅商品用的渠道
	 */
	private int SaleChannelSysNo=0;

	private String PriceChangedDateBegin="";
	
	private String PriceChangedDateEnd="";
	
	/**
	 * 默认不限制，传入值将限制返回数量大小，结果StartRow参数一起使用可以进行分页处理。
	 */
	private int LimitRows=0;
	/**
	 * 默认为1，即从第1 笔开始
	 */
	private int StartRow=1;
	public int getSaleChannelSysNo() {
		return SaleChannelSysNo;
	}
	public void setSaleChannelSysNo(int saleChannelSysNo) {
		SaleChannelSysNo = saleChannelSysNo;
	}
	public String getPriceChangedDateBegin() {
		return PriceChangedDateBegin;
	}
	public void setPriceChangedDateBegin(String priceChangedDateBegin) {
		PriceChangedDateBegin = priceChangedDateBegin;
	}
	public String getPriceChangedDateEnd() {
		return PriceChangedDateEnd;
	}
	public void setPriceChangedDateEnd(String priceChangedDateEnd) {
		PriceChangedDateEnd = priceChangedDateEnd;
	}
	public int getLimitRows() {
		return LimitRows;
	}
	public void setLimitRows(int limitRows) {
		LimitRows = limitRows;
	}
	public int getStartRow() {
		return StartRow;
	}
	public void setStartRow(int startRow) {
		StartRow = startRow;
	}
	
	
	
	
}

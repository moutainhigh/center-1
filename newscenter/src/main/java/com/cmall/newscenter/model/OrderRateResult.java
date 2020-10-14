package com.cmall.newscenter.model;


import com.cmall.membercenter.model.ScoredChange;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;
/**
 * 订单-评价输出类
 * @author liqiang
 * date 2014-7-22
 * @version 1.0
 */
public class OrderRateResult extends RootResultWeb{
	
	@ZapcomApi(value="获得积分")
	private ScoredChange scordChange = new ScoredChange();
	
	@ZapcomApi(value="我是否评价过")
	private int commented = 0;

	public ScoredChange getScordChange() {
		return scordChange;
	}

	public void setScordChange(ScoredChange scordChange) {
		this.scordChange = scordChange;
	}

	public int getCommented() {
		return commented;
	}

	public void setCommented(int commented) {
		this.commented = commented;
	}
	
	
}

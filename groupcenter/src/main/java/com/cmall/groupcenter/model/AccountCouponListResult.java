package com.cmall.groupcenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

public class AccountCouponListResult  extends RootResultWeb {

	@ZapcomApi(value = "优惠卷列表", remark = "优惠卷列表")
	List<AccountCouponInfo> accountCoupons = new ArrayList<AccountCouponInfo>();

	@ZapcomApi(value = "翻页结果", remark = "翻页结果")
	PageResults pageResults=new PageResults();
	
	public List<AccountCouponInfo> getAccountCoupons() {
		return accountCoupons;
	}

	public void setAccountCoupons(List<AccountCouponInfo> accountCoupons) {
		this.accountCoupons = accountCoupons;
	}

	public PageResults getPageResults() {
		return pageResults;
	}

	public void setPageResults(PageResults pageResults) {
		this.pageResults = pageResults;
	}
	
	
	
}

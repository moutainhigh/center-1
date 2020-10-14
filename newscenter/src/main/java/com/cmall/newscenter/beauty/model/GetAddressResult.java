package com.cmall.newscenter.beauty.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.newscenter.model.PageResults;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 惠美丽-获取收货地址输出类
 * @author yangrong	
 * date 2014-8-20
 * @version 1.0
 */
public class GetAddressResult extends RootResultWeb  {
	
	@ZapcomApi(value = "收货地址列表")
	private List<BeautyAddress> adress = new ArrayList<BeautyAddress>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public List<BeautyAddress> getAdress() {
		return adress;
	}

	public void setAdress(List<BeautyAddress> adress) {
		this.adress = adress;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}

	
}

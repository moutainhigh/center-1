package com.cmall.newscenter.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.webapi.RootResultWeb;

/**
 * 地址列表输出类
 * @author shiyz
 * date 2014-08-04
 * @version 1.0
 */
public class AddressListResult extends RootResultWeb {

	@ZapcomApi(value = "地址详情")
	private List<Address> address = new ArrayList<Address>();
	
	@ZapcomApi(value = "翻页结果")
	private PageResults paged = new PageResults();

	public List<Address> getAddress() {
		return address;
	}

	public void setAddress(List<Address> address) {
		this.address = address;
	}

	public PageResults getPaged() {
		return paged;
	}

	public void setPaged(PageResults paged) {
		this.paged = paged;
	}
	
}

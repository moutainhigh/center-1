package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 查询客户积分使用明细请求
 * @author zf
 *
 */
public class RsyncGetCustAccmUseDetailRequest implements IRsyncRequest {
	
	/*家有客户编码*/
	private String cust_id;
	
	/*请求页码*/
	private String page_num;
	
	/*每页显示数量*/
	private String page_count;

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getPage_num() {
		return page_num;
	}

	public void setPage_num(String page_num) {
		this.page_num = page_num;
	}

	public String getPage_count() {
		return page_count;
	}

	public void setPage_count(String page_count) {
		this.page_count = page_count;
	}

	
}

package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 查询客户惠币使用明细响应
 * @author zf
 *
 */
public class RsyncGetCustHbUseDetailResponse implements IRsyncResponse {
	
	/*是否成功*/
	private boolean success;
	
	/*是否最后页Y/N*/
	private String page_flag = "";
	
	/*总数量*/
	private String total = "";
	
	/*总页数*/
	private String totalPage = "";
	
	/*是否最后页Y/N*/
	private List<HbInfo> result = new ArrayList<HbInfo>();

	
	
	public String getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(String totalPage) {
		this.totalPage = totalPage;
	}

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getPage_flag() {
		return page_flag;
	}

	public void setPage_flag(String page_flag) {
		this.page_flag = page_flag;
	}

	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public List<HbInfo> getResult() {
		return result;
	}

	public void setResult(List<HbInfo> result) {
		this.result = result;
	}
	
	
}

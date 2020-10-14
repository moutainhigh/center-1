package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**
 * 查询客户积分使用明细响应
 * @author zf
 *
 */
public class RsyncGetCustAccmUseDetailResponse implements IRsyncResponse {
	
	/*是否成功*/
	private boolean success;
	
	/*是否最后页Y/N*/
	private String page_flag = "";
	/*总数量*/
	private String total = "";
	
	/*是否最后页Y/N*/
	private List<AccmInfo> result = new ArrayList<AccmInfo>();

	public boolean getSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	
	
	public String getTotal() {
		return total;
	}

	public void setTotal(String total) {
		this.total = total;
	}

	public String getPage_flag() {
		return page_flag;
	}

	public void setPage_flag(String page_flag) {
		this.page_flag = page_flag;
	}

	public List<AccmInfo> getResult() {
		return result;
	}

	public void setResult(List<AccmInfo> result) {
		this.result = result;
	}
	

}

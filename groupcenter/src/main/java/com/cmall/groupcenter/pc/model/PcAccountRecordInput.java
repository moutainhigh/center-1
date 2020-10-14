package com.cmall.groupcenter.pc.model;

import com.cmall.groupcenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class PcAccountRecordInput extends RootInput{

	@ZapcomApi(value = "起始时间",remark = "")
	private String startTime = "";
	
	@ZapcomApi(value = "结束时间",remark = "")
	private String endTime = "";
	
	@ZapcomApi(value = "类型",remark = "1:入账 2：提现 3：扣款 4：购物")
	private  String searchType = "";

	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption pageOption = new PageOption();

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

	public String getSearchType() {
		return searchType;
	}

	public void setSearchType(String searchType) {
		this.searchType = searchType;
	}

	public PageOption getPageOption() {
		return pageOption;
	}

	public void setPageOption(PageOption pageOption) {
		this.pageOption = pageOption;
	}
	
}

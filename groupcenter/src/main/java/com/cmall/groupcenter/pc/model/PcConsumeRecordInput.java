package com.cmall.groupcenter.pc.model;

import com.cmall.groupcenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

public class PcConsumeRecordInput extends RootInput{

	@ZapcomApi(value = "起始年月",remark = "")
	private String startYearMonth = "";
	
	@ZapcomApi(value = "结束年月",remark = "")
	private String endYearMonth = "";

	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption pageOption = new PageOption();

	public String getStartYearMonth() {
		return startYearMonth;
	}

	public void setStartYearMonth(String startYearMonth) {
		this.startYearMonth = startYearMonth;
	}

	public String getEndYearMonth() {
		return endYearMonth;
	}

	public void setEndYearMonth(String endYearMonth) {
		this.endYearMonth = endYearMonth;
	}

	public PageOption getPageOption() {
		return pageOption;
	}

	public void setPageOption(PageOption pageOption) {
		this.pageOption = pageOption;
	}
	
}

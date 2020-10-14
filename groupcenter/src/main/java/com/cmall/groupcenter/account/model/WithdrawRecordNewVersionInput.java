package com.cmall.groupcenter.account.model;

import com.cmall.groupcenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 新版本返利明细参数(2.1.4版)
 * @author GaoYang
 *
 */
public class WithdrawRecordNewVersionInput extends RootInput{
	
	@ZapcomApi(value = "返利明细类型",remark = "1:全部返利 2:预计返利 3:已返利" ,demo= "1,2,3",require = 1)
	private String rebateType = "";
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption pageOption = new PageOption();

	public String getRebateType() {
		return rebateType;
	}

	public void setRebateType(String rebateType) {
		this.rebateType = rebateType;
	}

	public PageOption getPageOption() {
		return pageOption;
	}

	public void setPageOption(PageOption pageOption) {
		this.pageOption = pageOption;
	}
	
}

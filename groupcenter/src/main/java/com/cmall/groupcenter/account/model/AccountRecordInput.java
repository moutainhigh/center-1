package com.cmall.groupcenter.account.model;

import com.cmall.groupcenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 新版本账户明细参数(2.1.4版)
 * @author GaoYang
 *
 */
public class AccountRecordInput extends RootInput{
	@ZapcomApi(value = "账户明细类型",remark = "1:全部明细 2:入账明细 3:提现明细 4:扣款明细" ,demo= "1,2,3,4",require = 1)
	private String recordType = "";
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption pageOption = new PageOption();

	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	public PageOption getPageOption() {
		return pageOption;
	}

	public void setPageOption(PageOption pageOption) {
		this.pageOption = pageOption;
	}
	
}

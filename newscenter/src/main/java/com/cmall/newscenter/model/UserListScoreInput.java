package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 用户 - 积分记录
 * @author liqiang
 * date 2014-7-23
 * @version 1.0
 */
public class UserListScoreInput extends RootInput{
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}

}

package com.cmall.newscenter.model;


import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 *活动- 获取评价列表输入类
 * @author liqiang
 * date 2014-7-4
 * @version 1.0
 */
public class ActivityGetCommentListsInput extends RootInput {
	
	@ZapcomApi(value="信息编码",remark="信息编码",demo="123456",require=1)
	private String info_code = "";
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();

	public String getInfo_code() {
		return info_code;
	}

	public void setInfo_code(String info_code) {
		this.info_code = info_code;
	}

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}

}

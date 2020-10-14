package com.cmall.groupcenter.account.model;

import com.cmall.groupcenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 获取用户好友列表输入参数
 * @author GaoYang
 *
 */
public class AccountFriendsListInput extends RootInput{
	@ZapcomApi(value = "筛选类型",remark = "1:我的好友 2:本月活跃 3:土豪 4:地主 5:富农 6:中农" ,demo= "1,2,3,4,5,6",require = 1)
	private String selectionType = "";
	
	@ZapcomApi(value = "好友级别",remark = "1:一度好友  2:二度好友  3:推荐人" ,demo= "1,2,3",require = 0)
	private String relationLevel = "";
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption pageOption = new PageOption();

	public String getSelectionType() {
		return selectionType;
	}

	public void setSelectionType(String selectionType) {
		this.selectionType = selectionType;
	}

	public String getRelationLevel() {
		return relationLevel;
	}

	public void setRelationLevel(String relationLevel) {
		this.relationLevel = relationLevel;
	}

	public PageOption getPageOption() {
		return pageOption;
	}

	public void setPageOption(PageOption pageOption) {
		this.pageOption = pageOption;
	}
	
}

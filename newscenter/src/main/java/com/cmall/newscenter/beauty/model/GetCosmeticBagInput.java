package com.cmall.newscenter.beauty.model;

import com.cmall.newscenter.model.PageOption;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * 惠美丽—获取化妆包中的妆品输入类
 * 
 * @author yangrong date: 2015-01-25
 * @version1.3.2
 */
public class GetCosmeticBagInput extends RootInput {
	
	@ZapcomApi(value = "翻页选项",remark = "输入起始页码和每页10条" ,demo= "5,10",require = 1)
	private PageOption paging = new PageOption();

	@ZapcomApi(value = "妆品类型" ,remark="为空查全部   即将过期=449747120001   已过期=449747120002")
	private String cosmetic_type = "";

	public String getCosmetic_type() {
		return cosmetic_type;
	}

	public void setCosmetic_type(String cosmetic_type) {
		this.cosmetic_type = cosmetic_type;
	}

	public PageOption getPaging() {
		return paging;
	}

	public void setPaging(PageOption paging) {
		this.paging = paging;
	}
	
}

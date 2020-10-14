package com.cmall.productcenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 联想搜索推荐词
 * @author zhouguohui
 *
 */
public class ApiSearchAssociateInput extends RootInput{
	
	@ZapcomApi(value="显示数量")
	private int num = 0;
	@ZapcomApi(value="搜索词", require=1)
	private String keyword="";
	@ZapcomApi(value="联想类型",remark="参数为 lxc 和 lxcProductName  lxc为显示单个的词  lxcProductName显示商品名称" )
	private String associateType = "";
	@ZapcomApi(value="数据是否加密",remark="数据如果加密传递默认值为：base64")
	private String baseValue="";
	/**
	 * @return the num
	 */
	public int getNum() {
		return num;
	}
	/**
	 * @param num the num to set
	 */
	public void setNum(int num) {
		this.num = num;
	}
	/**
	 * @return the keyword
	 */
	public String getKeyword() {
		return keyword;
	}
	/**
	 * @param keyword the keyword to set
	 */
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	/**
	 * @return the baseValue
	 */
	public String getBaseValue() {
		return baseValue;
	}
	/**
	 * @param baseValue the baseValue to set
	 */
	public void setBaseValue(String baseValue) {
		this.baseValue = baseValue;
	}
	/**
	 * @return the associateType
	 */
	public String getAssociateType() {
		return associateType;
	}
	/**
	 * @param associateType the associateType to set
	 */
	public void setAssociateType(String associateType) {
		this.associateType = associateType;
	}
			

}

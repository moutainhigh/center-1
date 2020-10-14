package com.cmall.productcenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * 
 * 项目名称：productcenter 
 * 类名称：     HighendTypeInfo 
 * 类描述：     高端商品分类下具体内容
 * 创建人：     GaoYang
 * 创建时间：2014年2月8日下午4:04:46 
 * 修改人：     GaoYang
 * 修改时间：2014年2月8日下午4:04:46
 * 修改备注：  
 * @version
 *
 */
public class HighendTypeInfo extends BaseClass{

	/**
	 * 分类编码
	 */
	private String defineCode = "";
	
	/**
	 * 分类内容
	 */
	private String defineName = "";

	public String getDefineCode() {
		return defineCode;
	}

	public void setDefineCode(String defineCode) {
		this.defineCode = defineCode;
	}

	public String getDefineName() {
		return defineName;
	}

	public void setDefineName(String defineName) {
		this.defineName = defineName;
	}
	
}

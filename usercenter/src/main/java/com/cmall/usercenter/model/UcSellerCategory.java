package com.cmall.usercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * ClassName: UcSellerCategory <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * date: 2013-9-23 下午1:20:29 <br/>
 * @author hxd
 * @version 
 * @since JDK 1.6
 */
public class UcSellerCategory extends BaseClass
{
	/**
	 * 店铺编号
	 */
	private String seller_code;
	/**
	 * 分类编号
	 */
	private String category_code;
	/**
	 * 分类名称
	 */
	private String category_name;
	/**
	 * 父分类编号
	 */
	private String parent_code;
	/**
	 * 排序
	 */
	private String sort;
	public String getSeller_code()
	{
		return seller_code;
	}
	public void setSeller_code(String seller_code)
	{
		this.seller_code = seller_code;
	}
	public String getCategory_code()
	{
		return category_code;
	}
	public void setCategory_code(String category_code)
	{
		this.category_code = category_code;
	}
	public String getCategory_name()
	{
		return category_name;
	}
	public void setCategory_name(String category_name)
	{
		this.category_name = category_name;
	}
	public String getParent_code()
	{
		return parent_code;
	}
	public void setParent_code(String parent_code)
	{
		this.parent_code = parent_code;
	}
	public String getSort()
	{
		return sort;
	}
	public void setSort(String sort)
	{
		this.sort = sort;
	}
	public UcSellerCategory(String seller_code, String category_code,
			String category_name, String parent_code, String sort)
	{
		super();
		this.seller_code = seller_code;
		this.category_code = category_code;
		this.category_name = category_name;
		this.parent_code = parent_code;
		this.sort = sort;
	}
	public UcSellerCategory()
	{
		super();
		// TODO Auto-generated constructor stub
	}
}

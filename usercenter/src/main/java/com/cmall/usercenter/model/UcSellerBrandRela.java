/**
 * Project Name:usercenter
 * File Name:UcSellerBrandRela.java
 * Package Name:com.cmall.usercenter.model
 * Date:2013年9月23日下午5:14:26
 *
*/

package com.cmall.usercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * ClassName:UcSellerBrandRela <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年9月23日 下午5:14:26 <br/>
 * @author   hexd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class UcSellerBrandRela extends BaseClass
{
	/**
	 * 商家code
	 */
	private String seller_code = "";
	/**
	 * 品牌code
	 */
	private String brand_code = "" ;
	public String getSeller_code()
	{
		return seller_code;
	}
	public void setSeller_code(String seller_code)
	{
		this.seller_code = seller_code;
	}
	public String getBrand_code()
	{
		return brand_code;
	}
	public void setBrand_code(String brand_code)
	{
		this.brand_code = brand_code;
	}
	public UcSellerBrandRela(String seller_code, String brand_code)
	{
		super();
		this.seller_code = seller_code;
		this.brand_code = brand_code;
	}
	public UcSellerBrandRela()
	{
		super();
		// TODO Auto-generated constructor stub
	}
}


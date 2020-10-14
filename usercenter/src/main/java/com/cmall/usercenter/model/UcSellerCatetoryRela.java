/**
 * Project Name:usercenter
 * File Name:UcSellerCatetoryRela.java
 * Package Name:com.cmall.usercenter.model
 * Date:2013年9月23日下午5:15:17
 *
*/

package com.cmall.usercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * ClassName:UcSellerCatetoryRela <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年9月23日 下午5:15:17 <br/>
 * @author   hexd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class UcSellerCatetoryRela extends BaseClass
{
	/**
	 * 分类编码
	 */
	private String category_code = "" ;
	/**
	 * 商家编码
	 */
	private String seller_code = "";
	/**
	 * 分成比率
	 */
	private float cpsrate = 0;
	public String getCategory_code()
	{
		return category_code;
	}
	public void setCategory_code(String category_code)
	{
		this.category_code = category_code;
	}
	public String getSeller_code()
	{
		return seller_code;
	}
	public void setSeller_code(String seller_code)
	{
		this.seller_code = seller_code;
	}
	public float getCpsrate()
	{
		return cpsrate;
	}
	public void setCpsrate(float cpsrate)
	{
		this.cpsrate = cpsrate;
	}
	public UcSellerCatetoryRela(String category_code, String seller_code,
			float cpsrate)
	{
		super();
		this.category_code = category_code;
		this.seller_code = seller_code;
		this.cpsrate = cpsrate;
	}
	public UcSellerCatetoryRela()
	{
		super();
	}
}


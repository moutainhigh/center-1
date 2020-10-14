/**
 * Project Name:ordercenter
 * File Name:RetrunMoneyInput.java
 * Package Name:com.cmall.ordercenter.service.money
 * Date:2013年11月11日下午2:52:20
 *
*/

package com.cmall.ordercenter.service.money;

import com.srnpr.zapcom.topapi.RootInput;

/**
 * ClassName:RetrunMoneyInput <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年11月11日 下午2:52:20 <br/>
 * @author   hxd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class ReturnMoneyInput extends RootInput
{
	/**
	 * 退单编号
	 */
	String returnCode = "";

	public String getReturnCode()
	{
		return returnCode;
	}

	public void setReturnCode(String returnCode)
	{
		this.returnCode = returnCode;
	}

	public ReturnMoneyInput(String returnCode)
	{
		super();
		this.returnCode = returnCode;
	}

	public ReturnMoneyInput()
	{
		super();
	}
}


/**
 * Project Name:ordercenter
 * File Name:ReturnMoneyResult.java
 * Package Name:com.cmall.ordercenter.service.money
 * Date:2013年11月11日下午2:54:47
 *
*/

package com.cmall.ordercenter.service.money;

import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.api.GiftVoucherInfo;
import com.srnpr.zapcom.topapi.RootResult;

/**
 * ClassName:ReturnMoneyResult <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * Reason:	 TODO ADD REASON. <br/>
 * Date:     2013年11月11日 下午2:54:47 <br/>
 * @author   hxd
 * @version  
 * @since    JDK 1.6
 * @see 	 
 */
public class ReturnMoneyResult extends RootResult
{
	private List<GiftVoucherInfo> list = new ArrayList<GiftVoucherInfo>();
	
	private String returnMoneyCode;

	public List<GiftVoucherInfo> getList() {
		return list;
	}

	public void setList(List<GiftVoucherInfo> list) {
		this.list = list;
	}

	public String getReturnMoneyCode() {
		return returnMoneyCode;
	}

	public void setReturnMoneyCode(String returnMoneyCode) {
		this.returnMoneyCode = returnMoneyCode;
	}
	
	
}


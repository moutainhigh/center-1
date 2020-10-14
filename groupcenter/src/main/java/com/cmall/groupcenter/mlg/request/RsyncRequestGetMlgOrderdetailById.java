package com.cmall.groupcenter.mlg.request;

import com.cmall.groupcenter.groupface.IRsyncRequest;


/** 
* @ClassName: RsyncRequestGetMlgOrderdetailById 
* @Description: 更具订单id获取订单物流信息
* @author 张海生
* @date 2015-12-28 下午3:17:25 
*  
*/
public class RsyncRequestGetMlgOrderdetailById implements IRsyncRequest {

	/**
	 * 订单id
	 */
	private String order_id = "";

	public String getOrder_id() {
		return order_id;
	}

	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}

}

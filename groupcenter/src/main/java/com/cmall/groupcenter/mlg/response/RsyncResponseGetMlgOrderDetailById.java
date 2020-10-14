package com.cmall.groupcenter.mlg.response;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;
import com.cmall.groupcenter.mlg.model.RsyncModelGetMlgOrderDetailById;


/** 
* @ClassName: RsyncResponseGetMlgOrderDetailById 
* @Description: 根据订单编号查询麦乐购订单物流信息返回实体
* @author 张海生
* @date 2015-12-28 下午4:15:55 
*  
*/
public class RsyncResponseGetMlgOrderDetailById implements IRsyncResponse {
	
	/**
	 * 返回结果code
	 */
	private String code = "";
	
	/**
	 * 麦乐购订单对象集合
	 */
	private  List<RsyncModelGetMlgOrderDetailById> Gou_Orders = new ArrayList<RsyncModelGetMlgOrderDetailById>();
	
	/**
	 * 返回结果描述
	 */
	private String message = "";
	
	/**
	 * 订单号
	 */
	private String OrderId = "";
	
	/**
	 * 时间戳
	 */
	private int timestamp;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public List<RsyncModelGetMlgOrderDetailById> getGou_Orders() {
		return Gou_Orders;
	}

	public void setGou_Orders(List<RsyncModelGetMlgOrderDetailById> gou_Orders) {
		Gou_Orders = gou_Orders;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getOrderId() {
		return OrderId;
	}

	public void setOrderId(String orderId) {
		OrderId = orderId;
	}

	public int getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(int timestamp) {
		this.timestamp = timestamp;
	}

}


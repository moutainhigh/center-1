package com.cmall.ordercenter.model;

/**   
* 某个客户的所有订单状态的汇总
* 
* 项目名称：ordercenter   
* 类名称：OrderStatusGroupModel   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-11-21 上午9:06:09   
* 修改人：yanzj
* 修改时间：2013-11-21 上午9:06:09   
* 修改备注：   
* @version    
*    
*/
public class OrderStatusGroupModel {
	
	/**
	 * 未付款未发货
	 */
	private int orderNoPay = 0;
	/**
	 * 已付款或货到付款未发货
	 */
	private int orderNotSend=0;
	/**
	 * 已发货
	 */
	private int orderSend=0;
	/**
	 * 已收货
	 */
	private int orderReceive=0;
	/**
	 * 交易成功
	 */
	private int orderSuccess = 0;
	/**
	 * 交易失败
	 */
	private int orderUnSuccess = 0;
	
	
	public int getOrderNoPay() {
		return orderNoPay;
	}
	public void setOrderNoPay(int orderNoPay) {
		this.orderNoPay = orderNoPay;
	}
	public int getOrderNotSend() {
		return orderNotSend;
	}
	public void setOrderNotSend(int orderNotSend) {
		this.orderNotSend = orderNotSend;
	}
	public int getOrderSend() {
		return orderSend;
	}
	public void setOrderSend(int orderSend) {
		this.orderSend = orderSend;
	}
	public int getOrderReceive() {
		return orderReceive;
	}
	public void setOrderReceive(int orderReceive) {
		this.orderReceive = orderReceive;
	}
	public int getOrderSuccess() {
		return orderSuccess;
	}
	public void setOrderSuccess(int orderSuccess) {
		this.orderSuccess = orderSuccess;
	}
	public int getOrderUnSuccess() {
		return orderUnSuccess;
	}
	public void setOrderUnSuccess(int orderUnSuccess) {
		this.orderUnSuccess = orderUnSuccess;
	}
}

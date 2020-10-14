package com.cmall.groupcenter.homehas.model;

public class ResponseGetOrderTrackingList {
	private String yc_update_time="";  //更新时间
	private String yc_dis_time="";    //配送时间
	private String yc_express_num=""; //运单号
	private String yc_delivergoods_user_name="";   //送货商名称
	private String outgo_no="";   //轨迹内容
	private String outgo_time="";    //轨迹时间
	/*
	 * 订单号非同步过来的数据，  从咋们自己的库中查的
	 */
	private String orderCode="";  //订单编号
	
	public String getYc_update_time() {
		return yc_update_time;
	}
	public void setYc_update_time(String yc_update_time) {
		this.yc_update_time = yc_update_time;
	}
	public String getYc_dis_time() {
		return yc_dis_time;
	}
	public void setYc_dis_time(String yc_dis_time) {
		this.yc_dis_time = yc_dis_time;
	}
	public String getYc_express_num() {
		return yc_express_num;
	}
	public void setYc_express_num(String yc_express_num) {
		this.yc_express_num = yc_express_num;
	}
	public String getYc_delivergoods_user_name() {
		return yc_delivergoods_user_name;
	}
	public void setYc_delivergoods_user_name(String yc_delivergoods_user_name) {
		this.yc_delivergoods_user_name = yc_delivergoods_user_name;
	}
	public String getOutgo_no() {
		return outgo_no;
	}
	public void setOutgo_no(String outgo_no) {
		this.outgo_no = outgo_no;
	}
	public String getOutgo_time() {
		return outgo_time;
	}
	public void setOutgo_time(String outgo_time) {
		this.outgo_time = outgo_time;
	}
	public String getOrderCode() {
		return orderCode;
	}
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}
	
}

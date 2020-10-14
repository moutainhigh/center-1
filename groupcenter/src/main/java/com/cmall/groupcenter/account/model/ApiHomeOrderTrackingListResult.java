package com.cmall.groupcenter.account.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
/**
 * 轨迹详情
 * @author wz
 *
 */
public class ApiHomeOrderTrackingListResult {
	//运单号 和 送货商品名称   需要放在list外
//	@ZapcomApi(value="运单号")
//	private String yc_express_num="";
//	@ZapcomApi(value="送货商名称")
//	private String yc_delivergoods_user_name="";
	
	
	@ZapcomApi(value="更新时间")
	private String yc_update_time="";
	@ZapcomApi(value="配送时间")
	private String yc_dis_time="";
	@ZapcomApi(value="轨迹内容")
	private String orderTrackContent="";
	@ZapcomApi(value="轨迹时间")
	private String orderTrackTime="";
	
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
	public String getOrderTrackContent() {
		return orderTrackContent;
	}
	public void setOrderTrackContent(String orderTrackContent) {
		this.orderTrackContent = orderTrackContent;
	}
	public String getOrderTrackTime() {
		return orderTrackTime;
	}
	public void setOrderTrackTime(String orderTrackTime) {
		this.orderTrackTime = orderTrackTime;
	}
	
}

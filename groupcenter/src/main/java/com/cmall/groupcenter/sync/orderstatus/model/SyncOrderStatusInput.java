package com.cmall.groupcenter.sync.orderstatus.model;

import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 微公社对接系统通过该接口同步订单最新状态
 * @author chenxk
 *
 */
public class SyncOrderStatusInput extends RootInput{
	
	@ZapcomApi(value="订单集合")
	private List<OrderStatusInfo> orderStatusInfos = new ArrayList<OrderStatusInfo>();
	
	public static class OrderStatusInfo{
		
		@ZapcomApi(value="订单编号",require= 1)
		private String orderCode = "";
		
		@ZapcomApi(value="更新时间",require= 1,verify = "base=datetime")
		private String updateTime = "";
		
		@ZapcomApi(value="订单状态",require= 1,remark="1.未付款2.未发货3.已发货4.交易成功5.交易失败。（每个状态接收一次，同一状态多次以第一次为准）")
		private String orderStatus = "";
		
		@ZapcomApi(value="备注")
		private String remark = "";

		public String getOrderCode() {
			return orderCode;
		}

		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}

		public String getUpdateTime() {
			return updateTime;
		}

		public void setUpdateTime(String updateTime) {
			this.updateTime = updateTime;
		}

		public String getOrderStatus() {
			return orderStatus;
		}

		public void setOrderStatus(String orderStatus) {
			this.orderStatus = orderStatus;
		}

		public String getRemark() {
			return remark;
		}

		public void setRemark(String remark) {
			this.remark = remark;
		}
	}

	public List<OrderStatusInfo> getOrderStatusInfos() {
		return orderStatusInfos;
	}

	public void setOrderStatusInfos(List<OrderStatusInfo> orderStatusInfos) {
		this.orderStatusInfos = orderStatusInfos;
	}
}

package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 订单状态改变通知接口请求报文
 * @author renhongbin
 */
public class ApiOrderStatusChangeNoticInput extends RootInput {

	@ZapcomApi(value="家有购物订单号")
	private String jyOrderCode = "";
	@ZapcomApi(value="状态名称")
	private String status = "";
	@ZapcomApi(value="状态编码")
	private String statusCode = "";
	@ZapcomApi(value="变更时间")
	private String updateTime = "";
	@ZapcomApi(value="物流公司名称")
	private String logisticseName = "";
	@ZapcomApi(value="物流公司编码")
	private String logisticseCode = "";
	@ZapcomApi(value="物流单号")
	private String waybill = "";
	
	public String getJyOrderCode() {
		return jyOrderCode;
	}
	public void setJyOrderCode(String jyOrderCode) {
		this.jyOrderCode = jyOrderCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}
	public String getUpdateTime() {
		return updateTime;
	}
	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}
	public String getLogisticseName() {
		return logisticseName;
	}
	public void setLogisticseName(String logisticseName) {
		this.logisticseName = logisticseName;
	}
	public String getLogisticseCode() {
		return logisticseCode;
	}
	public void setLogisticseCode(String logisticseCode) {
		this.logisticseCode = logisticseCode;
	}
	public String getWaybill() {
		return waybill;
	}
	public void setWaybill(String waybill) {
		this.waybill = waybill;
	}
	
}

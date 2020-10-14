package com.cmall.ordercenter.model.api;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 订单发货通知接口请求报文
 * @author renhongbin
 *
 */
public class ApiOrderShipmentsNoticInput extends RootInput {

	@ZapcomApi(value="家有订单号")
	private String jyOrderCode = "";
	@ZapcomApi(value="物流公司名称")
	private String logisticseName = "";
	@ZapcomApi(value="物流公司编码")
	private String logisticseCode = "";
	@ZapcomApi(value="物流单号")
	private String waybill = "";
	@ZapcomApi(value="发货时间")
	private String deliveryTime = "";
	public String getJyOrderCode() {
		return jyOrderCode;
	}
	public void setJyOrderCode(String jyOrderCode) {
		this.jyOrderCode = jyOrderCode;
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
	public String getDeliveryTime() {
		return deliveryTime;
	}
	public void setDeliveryTime(String deliveryTime) {
		this.deliveryTime = deliveryTime;
	}
	
}

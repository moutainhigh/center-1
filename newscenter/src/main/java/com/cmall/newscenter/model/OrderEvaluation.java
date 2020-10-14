package com.cmall.newscenter.model;

/**
 * 订单评价
 * @author jlin
 *
 */
public class OrderEvaluation {

	/** 订单编号 */
	private String orderCode = "";
	
	/**评价内容*/
	private String orderAssessment = "";
	
	/**原图*/
	private String oderPhotos = "";
	
	/**评价人*/
	private String orderName = "";
	
	/**缩略图*/
	private String orderSmallphotos = "";
	
	/**所属APP*/
	private String manageCode = "";
	
	/**SKU_CODE*/
	private String orderSkuid = "";

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getOrderAssessment() {
		return orderAssessment;
	}

	public void setOrderAssessment(String orderAssessment) {
		this.orderAssessment = orderAssessment;
	}

	public String getOderPhotos() {
		return oderPhotos;
	}

	public void setOderPhotos(String oderPhotos) {
		this.oderPhotos = oderPhotos;
	}

	public String getOrderName() {
		return orderName;
	}

	public void setOrderName(String orderName) {
		this.orderName = orderName;
	}

	public String getOrderSmallphotos() {
		return orderSmallphotos;
	}

	public void setOrderSmallphotos(String orderSmallphotos) {
		this.orderSmallphotos = orderSmallphotos;
	}

	public String getManageCode() {
		return manageCode;
	}

	public void setManageCode(String manageCode) {
		this.manageCode = manageCode;
	}

	public String getOrderSkuid() {
		return orderSkuid;
	}

	public void setOrderSkuid(String orderSkuid) {
		this.orderSkuid = orderSkuid;
	}
	
}

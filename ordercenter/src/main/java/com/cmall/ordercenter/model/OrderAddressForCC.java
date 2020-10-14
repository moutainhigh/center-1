package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**   
*  
* 订单地址信息表
*   
* 项目名称：ordercenter 
* 类名称：OrderAddressForCC
* 类描述：   
* 创建人：zhaoxq 
* 修改备注：   
* @version    
*    
*/
public class OrderAddressForCC{
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode = "";
	
	/**
	 * 地区编码
	 */
	@ZapcomApi(value="地区编码")
	private String areaCode = "";
	
	/**
	 * 地址信息
	 */
	@ZapcomApi(value="地址信息")
	private String address="";
	
	/**
	 * 邮政编码
	 */
	@ZapcomApi(value="邮政编码")
	private String postCode="";
	
	/**
	 * 电话
	 */
	@ZapcomApi(value="电话")
	private String mobilephone="";
	
	/**
	 * 固定电话
	 */
	@ZapcomApi(value="固定电话")
	private String telephone = "";
	
	/**
	 * 收货人
	 */
	@ZapcomApi(value="收货人")
	private String receivePerson = "";
	
	/**
	 * 电子邮箱
	 */
	@ZapcomApi(value="电子邮箱")
	private String email = "";
	
	/**
	 * 发票抬头
	 */
	@ZapcomApi(value="发票抬头")
	private String invoiceTitle = "";
	
	/**
	 * 是否开发票
	 */
	@ZapcomApi(value="是否开发票", remark=" 1:开   0: 不开 ")
	private String flagInvoice="";
	
	/**
	 * 发票内容
	 */
	@ZapcomApi(value="发票内容")
	private String invoiceContent="";
	
	/**
	 * 订单备注
	 */
	@ZapcomApi(value="订单备注")
	private String remark = "";

	public String getInvoiceContent() {
		return invoiceContent;
	}

	public void setInvoiceContent(String invoiceContent) {
		this.invoiceContent = invoiceContent;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostCode() {
		return postCode;
	}

	public void setPostCode(String postCode) {
		this.postCode = postCode;
	}

	public String getMobilephone() {
		return mobilephone;
	}

	public void setMobilephone(String mobilephone) {
		this.mobilephone = mobilephone;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getReceivePerson() {
		return receivePerson;
	}

	public void setReceivePerson(String receivePerson) {
		this.receivePerson = receivePerson;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getInvoiceTitle() {
		return invoiceTitle;
	}

	public void setInvoiceTitle(String invoiceTitle) {
		this.invoiceTitle = invoiceTitle;
	}

	public String getFlagInvoice() {
		return flagInvoice;
	}

	public void setFlagInvoice(String flagInvoice) {
		this.flagInvoice = flagInvoice;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}
}

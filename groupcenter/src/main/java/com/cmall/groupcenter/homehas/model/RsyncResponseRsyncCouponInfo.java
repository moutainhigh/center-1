package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 折扣券信心同步响应
 * @author cc
 *
 */
public class RsyncResponseRsyncCouponInfo implements IRsyncResponse {

	private String success;
	private String msg;
	private List<CouponInfo> items = new ArrayList<CouponInfo>();
	
	public static class CouponInfo{
		
		private String orderCode;//家有订单号
		private String phone;//用户注册手机号
		private String custId;//家有客代号
		private String memberCode;//用户编号
		private String productCode;//商品编码
		private String productCount;//优惠券数量
		private String productPrice;//商品价格
		public String getOrderCode() {
			return orderCode;
		}
		public void setOrderCode(String orderCode) {
			this.orderCode = orderCode;
		}
		public String getPhone() {
			return phone;
		}
		public void setPhone(String phone) {
			this.phone = phone;
		}
		public String getCustId() {
			return custId;
		}
		public void setCustId(String custId) {
			this.custId = custId;
		}
		public String getMemberCode() {
			return memberCode;
		}
		public void setMemberCode(String memberCode) {
			this.memberCode = memberCode;
		}
		public String getProductCode() {
			return productCode;
		}
		public void setProductCode(String productCode) {
			this.productCode = productCode;
		}
		public String getProductPrice() {
			return productPrice;
		}
		public void setProductPrice(String productPrice) {
			this.productPrice = productPrice;
		}
		public String getProductCount() {
			return productCount;
		}
		public void setProductCount(String productCount) {
			this.productCount = productCount;
		}
		
	}

	public String getSuccess() {
		return success;
	}

	public void setSuccess(String success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public List<CouponInfo> getItems() {
		return items;
	}

	public void setItems(List<CouponInfo> items) {
		this.items = items;
	}
	
}

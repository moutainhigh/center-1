package com.cmall.groupcenter.kjt.response;

import java.math.BigDecimal;

import com.cmall.groupcenter.kjt.RsyncKjtResponseBase;

public class RsyncResponseInvoiceFEPBillPost extends RsyncKjtResponseBase {

	private Data Data ;
	
	public Data getData() {
		return Data;
	}

	public void setData(Data data) {
		Data = data;
	}
	
	public static class Data {
		
		private String FEPBillId;
		private BigDecimal PurchasingTotalAmount;
		public String getFEPBillId() {
			return FEPBillId;
		}
		public void setFEPBillId(String fEPBillId) {
			FEPBillId = fEPBillId;
		}
		public BigDecimal getPurchasingTotalAmount() {
			return PurchasingTotalAmount;
		}
		public void setPurchasingTotalAmount(BigDecimal purchasingTotalAmount) {
			PurchasingTotalAmount = purchasingTotalAmount;
		}
	}

}


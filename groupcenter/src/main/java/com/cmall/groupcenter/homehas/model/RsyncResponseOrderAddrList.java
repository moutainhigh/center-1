package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

public class RsyncResponseOrderAddrList implements IRsyncResponse {

	private boolean success;
	private String message;
	private List<AddrInfo> result = new ArrayList<AddrInfo>();

	public static class AddrInfo {

		/*"LRGN_CD": "120000",
        "LRGN_NM": "天津市",
        "LRGN_SHOW_YN": "Y",
        "LRGN_VL_YN": "Y",
        "MRGN_CD": "120100",
        "MRGN_NM": "市辖区",
        "MRGN_SHOW_YN": "N",
        "MRGN_VL_YN": "Y",
        "SRGN_CD": "120118",
        "SRGN_NM": "静海区",
        "SRGN_SHOW_YN": "Y",
        "SRGN_VL_YN": "Y",
        "FRGN_CD": "120118402",
        "FRGN_NM": "天津子牙循环经济产业区",
        "FRGN_SHOW_YN": "Y",
        "FRGN_VL_YN": "Y",
        "IS_SEND": "Y",
        "IS_DELAY":"Y"*/

		// 区域编码
		private String LRGN_CD = "";
		// 区域名称
		private String LRGN_NM = "";
		// 是否展示
		private String LRGN_SHOW_YN = "";
		// 是否有效
		private String LRGN_VL_YN = "";
		private String MRGN_CD = "";
		private String MRGN_NM = "";
		private String MRGN_SHOW_YN = "";
		private String MRGN_VL_YN = "";
		private String SRGN_CD = "";
		private String SRGN_NM = "";
		private String SRGN_SHOW_YN = "";
		private String SRGN_VL_YN = "";
		private String FRGN_CD = "";
		private String FRGN_NM = "";
		private String FRGN_SHOW_YN = "";
		private String FRGN_VL_YN = "";
		// IS_SEND的值为Y时，该地址为可配送地址。IS_SEND的值为N时，则该地址为不可配送地址。
		private String IS_SEND = "";
		// IS_DELAY的值为Y时，该地址为可延迟配送地址。IS_DELAY的值为N时，则该地址为不可延迟配送地址。
		private String IS_DELAY = "";
		
		public String getLRGN_CD() {
			return LRGN_CD;
		}
		public void setLRGN_CD(String lRGN_CD) {
			LRGN_CD = lRGN_CD;
		}
		public String getLRGN_NM() {
			return LRGN_NM;
		}
		public void setLRGN_NM(String lRGN_NM) {
			LRGN_NM = lRGN_NM;
		}
		public String getLRGN_SHOW_YN() {
			return LRGN_SHOW_YN;
		}
		public void setLRGN_SHOW_YN(String lRGN_SHOW_YN) {
			LRGN_SHOW_YN = lRGN_SHOW_YN;
		}
		public String getLRGN_VL_YN() {
			return LRGN_VL_YN;
		}
		public void setLRGN_VL_YN(String lRGN_VL_YN) {
			LRGN_VL_YN = lRGN_VL_YN;
		}
		public String getMRGN_CD() {
			return MRGN_CD;
		}
		public void setMRGN_CD(String mRGN_CD) {
			MRGN_CD = mRGN_CD;
		}
		public String getMRGN_NM() {
			return MRGN_NM;
		}
		public void setMRGN_NM(String mRGN_NM) {
			MRGN_NM = mRGN_NM;
		}
		public String getMRGN_SHOW_YN() {
			return MRGN_SHOW_YN;
		}
		public void setMRGN_SHOW_YN(String mRGN_SHOW_YN) {
			MRGN_SHOW_YN = mRGN_SHOW_YN;
		}
		public String getMRGN_VL_YN() {
			return MRGN_VL_YN;
		}
		public void setMRGN_VL_YN(String mRGN_VL_YN) {
			MRGN_VL_YN = mRGN_VL_YN;
		}
		public String getSRGN_CD() {
			return SRGN_CD;
		}
		public void setSRGN_CD(String sRGN_CD) {
			SRGN_CD = sRGN_CD;
		}
		public String getSRGN_NM() {
			return SRGN_NM;
		}
		public void setSRGN_NM(String sRGN_NM) {
			SRGN_NM = sRGN_NM;
		}
		public String getSRGN_SHOW_YN() {
			return SRGN_SHOW_YN;
		}
		public void setSRGN_SHOW_YN(String sRGN_SHOW_YN) {
			SRGN_SHOW_YN = sRGN_SHOW_YN;
		}
		public String getSRGN_VL_YN() {
			return SRGN_VL_YN;
		}
		public void setSRGN_VL_YN(String sRGN_VL_YN) {
			SRGN_VL_YN = sRGN_VL_YN;
		}
		public String getFRGN_CD() {
			return FRGN_CD;
		}
		public void setFRGN_CD(String fRGN_CD) {
			FRGN_CD = fRGN_CD;
		}
		public String getFRGN_NM() {
			return FRGN_NM;
		}
		public void setFRGN_NM(String fRGN_NM) {
			FRGN_NM = fRGN_NM;
		}
		public String getFRGN_SHOW_YN() {
			return FRGN_SHOW_YN;
		}
		public void setFRGN_SHOW_YN(String fRGN_SHOW_YN) {
			FRGN_SHOW_YN = fRGN_SHOW_YN;
		}
		public String getFRGN_VL_YN() {
			return FRGN_VL_YN;
		}
		public void setFRGN_VL_YN(String fRGN_VL_YN) {
			FRGN_VL_YN = fRGN_VL_YN;
		}
		public String getIS_SEND() {
			return IS_SEND;
		}
		public void setIS_SEND(String iS_SEND) {
			IS_SEND = iS_SEND;
		}
		public String getIS_DELAY() {
			return IS_DELAY;
		}
		public void setIS_DELAY(String iS_DELAY) {
			IS_DELAY = iS_DELAY;
		}
		
		
	}

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public List<AddrInfo> getResult() {
		return result;
	}

	public void setResult(List<AddrInfo> result) {
		this.result = result;
	}
	
}

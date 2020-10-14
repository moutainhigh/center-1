package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;

/**
 * 惠家有礼金券明细
 * @author cc
 *
 */
public class RsyncModelGiftVoucherDetail {
	
	/**
	 * 礼金券编号
	 * 格式：活动编号-客代-礼金券序号
	 */
	private String LJ_CODE;
	
	/**
	 * LD客户代码
	 */
	private String CUST_ID;
	
	/**
	 * 惠家有用户编号
	 */
	private String MEMBERCODE;
	
	/**
	 * 礼金金额
	 */
	private BigDecimal LJ_AMT = new BigDecimal(0);
	
	/**
	 * 相关LD订单号码
	 */
	private String LJ_REL_ID;
	
	/**
	 * 是否使用
	 */
	private String SY_VL;
	
	/**
	 * 是否有效
	 */
	private String VL_YN;
	
	/**
	 * 输入者ID
	 */
	private String ETR_ID;
	
	/**
	 * 输入日期
	 */
	private String ETR_DATE;
	
	/**
	 * 修改者ID
	 */
	private String MDF_ID;
	
	/**
	 * 修改日期
	 */
	private String MDF_DATE;
	
	/**
	 * 有效开始时间
	 */
	private String FR_DATE;
	
	/**
	 * 有效结束时间
	 */
	private String END_DATE;
	
	/**
	 * 礼金使用序号
	 */
	private String LJ_REL_SEQ;
	
	/**
	 * 最低订单金额
	 */
	private BigDecimal ORD_AMT = new BigDecimal(0);
	
	/**
	 * 惠家有关联订单号
	 */
	private String HJY_ORD_ID;
	
	/**
	 * 用户手机号
	 */
	private String MOBILE;

	public String getLJ_CODE() {
		return LJ_CODE;
	}

	public void setLJ_CODE(String lJ_CODE) {
		LJ_CODE = lJ_CODE;
	}

	public BigDecimal getLJ_AMT() {
		return LJ_AMT;
	}

	public void setLJ_AMT(BigDecimal lJ_AMT) {
		LJ_AMT = lJ_AMT;
	}

	public String getLJ_REL_ID() {
		return LJ_REL_ID;
	}

	public void setLJ_REL_ID(String lJ_REL_ID) {
		LJ_REL_ID = lJ_REL_ID;
	}

	public String getSY_VL() {
		return SY_VL;
	}

	public void setSY_VL(String sY_VL) {
		SY_VL = sY_VL;
	}

	public String getVL_YN() {
		return VL_YN;
	}

	public void setVL_YN(String vL_YN) {
		VL_YN = vL_YN;
	}

	public String getETR_ID() {
		return ETR_ID;
	}

	public void setETR_ID(String eTR_ID) {
		ETR_ID = eTR_ID;
	}

	public String getETR_DATE() {
		return ETR_DATE;
	}

	public void setETR_DATE(String eTR_DATE) {
		ETR_DATE = eTR_DATE;
	}

	public String getMDF_ID() {
		return MDF_ID;
	}

	public void setMDF_ID(String mDF_ID) {
		MDF_ID = mDF_ID;
	}

	public String getMDF_DATE() {
		return MDF_DATE;
	}

	public void setMDF_DATE(String mDF_DATE) {
		MDF_DATE = mDF_DATE;
	}

	public String getFR_DATE() {
		return FR_DATE;
	}

	public void setFR_DATE(String fR_DATE) {
		FR_DATE = fR_DATE;
	}

	public String getEND_DATE() {
		return END_DATE;
	}

	public void setEND_DATE(String eND_DATE) {
		END_DATE = eND_DATE;
	}

	public String getLJ_REL_SEQ() {
		return LJ_REL_SEQ;
	}

	public void setLJ_REL_SEQ(String lJ_REL_SEQ) {
		LJ_REL_SEQ = lJ_REL_SEQ;
	}

	public BigDecimal getORD_AMT() {
		return ORD_AMT;
	}

	public void setORD_AMT(BigDecimal oRD_AMT) {
		ORD_AMT = oRD_AMT;
	}

	public String getCUST_ID() {
		return CUST_ID;
	}

	public void setCUST_ID(String cUST_ID) {
		CUST_ID = cUST_ID;
	}

	public String getMEMBERCODE() {
		return MEMBERCODE;
	}

	public void setMEMBERCODE(String mEMBERCODE) {
		MEMBERCODE = mEMBERCODE;
	}

	public String getHJY_ORD_ID() {
		return HJY_ORD_ID;
	}

	public void setHJY_ORD_ID(String hJY_ORD_ID) {
		HJY_ORD_ID = hJY_ORD_ID;
	}

	public String getMOBILE() {
		return MOBILE;
	}

	public void setMOBILE(String mOBILE) {
		MOBILE = mOBILE;
	}
	
}

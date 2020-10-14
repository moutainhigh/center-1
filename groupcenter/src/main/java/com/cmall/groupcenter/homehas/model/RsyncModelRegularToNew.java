package com.cmall.groupcenter.homehas.model;

public class RsyncModelRegularToNew {

	/**
	 * 新老客户类型，old/new
	 */
	private String CUST_TYPE;
	
	/**
	 * 奖励类型：积分、礼金、立减
	 */
	private String SLE_TYPE;
	
	/**
	 * 活动编号
	 */
	private String EVENT_ID;
	
	/**
	 * 媒体中分类ID
	 */
	private String MEDI_MCLSS_ID;
	
	/**
	 * 媒体大分类
	 */
	private String MEDI_LCLSS_ID;
	
	/**
	 * 开始日期
	 */
	private String FR_DATE;
	
	/**
	 * 结束日期
	 */
	private String END_DATE;
	
	/**
	 * 促销类别
	 */
	private String EVENT_CD;
	
	/**
	 * 促销名
	 */
	private String EVENT_NM;
	
	/**
	 * 促销活动说明
	 */
	private String EVENT_DESC;
	
	/**
	 * 有效与否
	 */
	private String VL_YN;
	
	/**
	 * 输入者ID
	 */
	private String ETR_ID;
	
	/**
	 * 创建日期
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
	 * 加赠方式
	 */
	private String GIFT_TP;
	
	/**
	 * 加赠地区
	 */
	private String EVE_ADDR;
	
	/**
	 * 客户等级限定
	 */
	private String CUST_YN;
	
	/**
	 * 订购次数
	 */
	private String GIFT_CNT;
	
	/**
	 * 订购金额
	 */
	private String GIFT_AMT;
	
	/**
	 * 商品限定模式
	 */
	private String GOOD_YN;
	
	/**
	 * 是否允许多次加加购
	 */
	private String IS_MANYJJG;
	
	/**
	 * 01:按订单统计;02:按商品统计
	 */
	private String GRGOOD_TYPE;
	
	/**
	 * 活动参与方式(10 商品  20 订单)
	 */
	private String ATTEND_MODE;
	
	/**
	 * 活动承担部门
	 */
	private String DEPT_EVENT;
	
	/**
	 * 订单是否多次参与
	 */
	private String IS_MANYORDER;
	
	/**
	 * 是否惠家有可用活动
	 */
	private String HJY_EVENT_YN;
	
	/**
	 * 是否可叠加使用:“Y”可叠加  “N”不可
	 */
	private String IS_SUPERPOSITION;
	
	/**
	 * 活动赋予礼金券面额
	 */
	private String DIS_AMT;
	
	/**
	 * 活动赋予礼金券最低使用金额
	 */
	private String LOW_AMT;
	
	/**
	 * 活动赋予礼金券使用开始时间
	 */
	private String ORD_FR_DATE;
	
	/**
	 * 活动赋予礼金券使用结束时间
	 */
	private String ORD_END_DATE;
	
	/**
	 * 限定的商品集合
	 */
	private String GOODLIMIT;
	
	/**
	 * 限定的品类集合,逗号隔开
	 */
	private String CLASSLIMIT;
	
	/**
	 * 禁止的商品集合,逗号隔开
	 */
	private String GOODNOJOIN;

	public String getCLASSLIMIT() {
		return CLASSLIMIT;
	}

	public void setCLASSLIMIT(String cLASSLIMIT) {
		CLASSLIMIT = cLASSLIMIT;
	}

	public String getCUST_TYPE() {
		return CUST_TYPE;
	}

	public void setCUST_TYPE(String cUST_TYPE) {
		CUST_TYPE = cUST_TYPE;
	}

	public String getSLE_TYPE() {
		return SLE_TYPE;
	}

	public void setSLE_TYPE(String sLE_TYPE) {
		SLE_TYPE = sLE_TYPE;
	}

	public String getEVENT_ID() {
		return EVENT_ID;
	}

	public void setEVENT_ID(String eVENT_ID) {
		EVENT_ID = eVENT_ID;
	}

	public String getMEDI_MCLSS_ID() {
		return MEDI_MCLSS_ID;
	}

	public void setMEDI_MCLSS_ID(String mEDI_MCLSS_ID) {
		MEDI_MCLSS_ID = mEDI_MCLSS_ID;
	}

	public String getMEDI_LCLSS_ID() {
		return MEDI_LCLSS_ID;
	}

	public void setMEDI_LCLSS_ID(String mEDI_LCLSS_ID) {
		MEDI_LCLSS_ID = mEDI_LCLSS_ID;
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

	public String getEVENT_CD() {
		return EVENT_CD;
	}

	public void setEVENT_CD(String eVENT_CD) {
		EVENT_CD = eVENT_CD;
	}

	public String getEVENT_NM() {
		return EVENT_NM;
	}

	public void setEVENT_NM(String eVENT_NM) {
		EVENT_NM = eVENT_NM;
	}

	public String getEVENT_DESC() {
		return EVENT_DESC;
	}

	public void setEVENT_DESC(String eVENT_DESC) {
		EVENT_DESC = eVENT_DESC;
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

	public String getGIFT_TP() {
		return GIFT_TP;
	}

	public void setGIFT_TP(String gIFT_TP) {
		GIFT_TP = gIFT_TP;
	}

	public String getEVE_ADDR() {
		return EVE_ADDR;
	}

	public void setEVE_ADDR(String eVE_ADDR) {
		EVE_ADDR = eVE_ADDR;
	}

	public String getCUST_YN() {
		return CUST_YN;
	}

	public void setCUST_YN(String cUST_YN) {
		CUST_YN = cUST_YN;
	}

	public String getGIFT_CNT() {
		return GIFT_CNT;
	}

	public void setGIFT_CNT(String gIFT_CNT) {
		GIFT_CNT = gIFT_CNT;
	}

	public String getGIFT_AMT() {
		return GIFT_AMT;
	}

	public void setGIFT_AMT(String gIFT_AMT) {
		GIFT_AMT = gIFT_AMT;
	}

	public String getGOOD_YN() {
		return GOOD_YN;
	}

	public void setGOOD_YN(String gOOD_YN) {
		GOOD_YN = gOOD_YN;
	}

	public String getIS_MANYJJG() {
		return IS_MANYJJG;
	}

	public void setIS_MANYJJG(String iS_MANYJJG) {
		IS_MANYJJG = iS_MANYJJG;
	}

	public String getGRGOOD_TYPE() {
		return GRGOOD_TYPE;
	}

	public void setGRGOOD_TYPE(String gRGOOD_TYPE) {
		GRGOOD_TYPE = gRGOOD_TYPE;
	}

	public String getATTEND_MODE() {
		return ATTEND_MODE;
	}

	public void setATTEND_MODE(String aTTEND_MODE) {
		ATTEND_MODE = aTTEND_MODE;
	}

	public String getDEPT_EVENT() {
		return DEPT_EVENT;
	}

	public void setDEPT_EVENT(String dEPT_EVENT) {
		DEPT_EVENT = dEPT_EVENT;
	}

	public String getIS_MANYORDER() {
		return IS_MANYORDER;
	}

	public void setIS_MANYORDER(String iS_MANYORDER) {
		IS_MANYORDER = iS_MANYORDER;
	}

	public String getHJY_EVENT_YN() {
		return HJY_EVENT_YN;
	}

	public void setHJY_EVENT_YN(String hJY_EVENT_YN) {
		HJY_EVENT_YN = hJY_EVENT_YN;
	}

	public String getIS_SUPERPOSITION() {
		return IS_SUPERPOSITION;
	}

	public void setIS_SUPERPOSITION(String iS_SUPERPOSITION) {
		IS_SUPERPOSITION = iS_SUPERPOSITION;
	}

	public String getDIS_AMT() {
		return DIS_AMT;
	}

	public void setDIS_AMT(String dIS_AMT) {
		DIS_AMT = dIS_AMT;
	}

	public String getLOW_AMT() {
		return LOW_AMT;
	}

	public void setLOW_AMT(String lOW_AMT) {
		LOW_AMT = lOW_AMT;
	}

	public String getORD_FR_DATE() {
		return ORD_FR_DATE;
	}

	public void setORD_FR_DATE(String oRD_FR_DATE) {
		ORD_FR_DATE = oRD_FR_DATE;
	}

	public String getORD_END_DATE() {
		return ORD_END_DATE;
	}

	public void setORD_END_DATE(String oRD_END_DATE) {
		ORD_END_DATE = oRD_END_DATE;
	}

	public String getGOODLIMIT() {
		return GOODLIMIT;
	}

	public void setGOODLIMIT(String gOODLIMIT) {
		GOODLIMIT = gOODLIMIT;
	}

	public String getGOODNOJOIN() {
		return GOODNOJOIN;
	}

	public void setGOODNOJOIN(String gOODNOJOIN) {
		GOODNOJOIN = gOODNOJOIN;
	}
	
}

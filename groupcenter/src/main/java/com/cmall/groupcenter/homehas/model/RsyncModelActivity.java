package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RsyncModelActivity {

	/**
	 * 促销代码
	 */
	private Integer EVENT_ID;

	/**
	 * 媒体中分类ID
	 */
	private Integer MEDI_MCLSS_ID;

	/**
	 * 媒体大分类ID
	 */
	private Integer MEDI_LCLSS_ID;

	/**
	 * 开始日期
	 */
	private Long FR_DATE;

	/**
	 * 结束日期
	 */
	private Long END_DATE;

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
	 * 有效与否，默认为'Y'
	 */
	private String VL_YN;

	/**
	 * 输入者ID
	 */
	private String ETR_ID;

	/**
	 * 输入日期
	 */
	private Long ETR_DATE;

	/**
	 * 修改者ID 
	 */
	private String MDF_ID;

	/**
	 * 修改日期
	 */
	private Long MDF_DATE;

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
	private Integer GIFT_CNT;

	/**
	 * 订购金额
	 */
	private BigDecimal GIFT_AMT = new BigDecimal(0);

	/**
	 * 商品限定
	 */
	private String GOOD_YN;

	/**
	 * 是否允许多次加价购，默认'Y'
	 */
	private String IS_MANYJJG;
	
	/**
	 * 01:按订单统计；02:按商品统计；默认01
	 */
	private String GRGOOD_TYPE;
	
	/**
	 * 活动参与方式(10 商品 20 订单)
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
	 * 是否可叠加使用
	 */
	private String IS_SUPERPOSITION;
	
	/**
	 * 活动赋予礼金券面额
	 */
	private Integer DIS_AMT;
	
	/**
	 * 活动赋予礼金券最低使用金额
	 */
	private Integer LOW_AMT; 
	
	/**
	 * 活动赋予礼金券使用开始时间
	 */
	private String ORD_FR_DATE;
	
	/**
	 * 活动赋予礼金券使用结束时间
	 */
	private String ORD_END_DATE;
	
	/**
	 * 商品限定字段，值是商品id的集合，逗号隔开
	 */
	private String GOODLIMIT;
	
	/**
	 * 品类限定字段
	 */
	private String CLASSLIMIT;
	
	/**
	 * 不参与活动的商品集合
	 */
	private String GOODNOJOIN;
	
	public Integer getEVENT_ID() {
		return EVENT_ID;
	}

	public void setEVENT_ID(Integer eVENT_ID) {
		EVENT_ID = eVENT_ID;
	}

	public Integer getMEDI_MCLSS_ID() {
		return MEDI_MCLSS_ID;
	}

	public void setMEDI_MCLSS_ID(Integer mEDI_MCLSS_ID) {
		MEDI_MCLSS_ID = mEDI_MCLSS_ID;
	}

	public Integer getMEDI_LCLSS_ID() {
		return MEDI_LCLSS_ID;
	}

	public void setMEDI_LCLSS_ID(Integer mEDI_LCLSS_ID) {
		MEDI_LCLSS_ID = mEDI_LCLSS_ID;
	}

	public Long getFR_DATE() {
		return FR_DATE;
	}

	public void setFR_DATE(Long fR_DATE) {
		FR_DATE = fR_DATE;
	}

	public Long getEND_DATE() {
		return END_DATE;
	}

	public void setEND_DATE(Long eND_DATE) {
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

	public Long getETR_DATE() {
		return ETR_DATE;
	}

	public void setETR_DATE(Long eTR_DATE) {
		ETR_DATE = eTR_DATE;
	}

	public String getMDF_ID() {
		return MDF_ID;
	}

	public void setMDF_ID(String mDF_ID) {
		MDF_ID = mDF_ID;
	}

	public Long getMDF_DATE() {
		return MDF_DATE;
	}

	public void setMDF_DATE(Long mDF_DATE) {
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

	public Integer getGIFT_CNT() {
		return GIFT_CNT;
	}

	public void setGIFT_CNT(Integer gIFT_CNT) {
		GIFT_CNT = gIFT_CNT;
	}

	public BigDecimal getGIFT_AMT() {
		return GIFT_AMT;
	}

	public void setGIFT_AMT(BigDecimal gIFT_AMT) {
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

	public String getGOODLIMIT() {
		return GOODLIMIT;
	}

	public void setGOODLIMIT(String gOODLIMIT) {
		GOODLIMIT = gOODLIMIT;
	}

	public String getCLASSLIMIT() {
		return CLASSLIMIT;
	}

	public void setCLASSLIMIT(String cLASSLIMIT) {
		CLASSLIMIT = cLASSLIMIT;
	}

	public String getGOODNOJOIN() {
		return GOODNOJOIN;
	}

	public void setGOODNOJOIN(String gOODNOJOIN) {
		GOODNOJOIN = gOODNOJOIN;
	}

	public String getIS_SUPERPOSITION() {
		return IS_SUPERPOSITION;
	}

	public void setIS_SUPERPOSITION(String iS_SUPERPOSITION) {
		IS_SUPERPOSITION = iS_SUPERPOSITION;
	}

	public String getHJY_EVENT_YN() {
		return HJY_EVENT_YN;
	}

	public void setHJY_EVENT_YN(String hJY_EVENT_YN) {
		HJY_EVENT_YN = hJY_EVENT_YN;
	}

	public Integer getDIS_AMT() {
		return DIS_AMT;
	}

	public void setDIS_AMT(Integer dIS_AMT) {
		DIS_AMT = dIS_AMT;
	}

	public Integer getLOW_AMT() {
		return LOW_AMT;
	}

	public void setLOW_AMT(Integer lOW_AMT) {
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

	public static void main(String[] args) {
		
		SimpleDateFormat format =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" ); 
		long time = 1526227200000L;  
	    String d = format.format(time); 
	    Date date;
		try {
			date = format.parse(d);
			System.out.println(date);
			System.out.println(date.getTime());
		} catch (ParseException e) {			
			e.printStackTrace();
		}
		
	}
}

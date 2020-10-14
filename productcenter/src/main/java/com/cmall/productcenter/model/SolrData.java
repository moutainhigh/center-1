package com.cmall.productcenter.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.beans.Field;

/**
 * solr索引库创建数据源 <br/>
 * 描述 : 数据库数据封装类 <br/>
 * 创建作者 : zhouguohui <br/>
 * 创建时间 : 2015-08-15 09:25:31 <br/>
 * 文件版本 : V1.0 <br/>
 * 修改历史 : V1.0
 */
public class SolrData {
	/**
	 * solr唯一标示key
	 */
	@Field
	private String k1 ;
	/***
	 * s开头的为字符串
	 */
	@Field
	private String s1;//对应商品的productName
	@Field
	private String s2;
	@Field
	private String s3;
	@Field
	private String s4;
	@Field
	private String s5;
	@Field
	private String s6;
	@Field
	private String s7;
	@Field
	private String s8;
	@Field
	private String s9;
	@Field
	private String s10;
	@Field
	private String s11;
	@Field
	private String s12;
	@Field
	private String s13;//对应商品的品牌
	@Field
	private String s14;//对应商品的品牌编号
	@Field
	private String s15;//对应商品里面的 sellerCode
	/**
	 * i开头的数字
	 */
	@Field
	private int i1;    //i1代表商品是否有货 1代表有货 0代表没有货
	@Field
	private int i2;    //i2代表商品销量值
	@Field
	private int i3;    //i3代表是否海外购 1代表是海外购 0代表不是海外购
	@Field
	private int i4;
	@Field
	private int i5;
	/**
	 * d开头的为双精度数字
	 */
	@Field
	private Double d1; //d1为销售价格
	@Field
	private Double d2; //d2位人气
	@Field
	private Double d3;
	@Field
	private Double d4;
	/**
	 * t为日期格式
	 */
	@Field
	private Date t1;
	/**
	 * l为list集合里面封装String字符串
	 */
	@Field
	private List<String> l1; //l1对应一级分类的名称
	@Field
	private List<String> l2; //l2对应一级分类的编号
	@Field
	private List<String> l3; //l3对应二级分类的名称
	@Field
	private List<String> l4; //l4对应二级分类的编号
	@Field
	private List<String> l5;
	@Field
	private List<String> l6;
	@Field
	private List<String> l7;
	@Field
	private List<String> l8;
	@Field
	private List<String> l9;
	@Field
	private List<String> l10;
	private long counts=0;
	private List<String> categoryName=new ArrayList<String>();
	private List<String> brandName=new ArrayList<String>();
	
	
	
	
	public List<String> getL9() {
		return l9;
	}
	public void setL9(List<String> l9) {
		this.l9 = l9;
	}
	public List<String> getL10() {
		return l10;
	}
	public void setL10(List<String> l10) {
		this.l10 = l10;
	}
	/**
	 * @return the categoryName
	 */
	public List<String> getCategoryName() {
		return categoryName;
	}
	/**
	 * @param categoryName the categoryName to set
	 */
	public void setCategoryName(List<String> categoryName) {
		this.categoryName = categoryName;
	}
	/**
	 * @return the brandName
	 */
	public List<String> getBrandName() {
		return brandName;
	}
	/**
	 * @param brandName the brandName to set
	 */
	public void setBrandName(List<String> brandName) {
		this.brandName = brandName;
	}
	/**
	 * @return the counts
	 */
	public long getCounts() {
		return counts;
	}
	/**
	 * @param counts the counts to set
	 */
	public void setCounts(long counts) {
		this.counts = counts;
	}
	
	/**
	 * @return the k1
	 */
	public String getK1() {
		return k1;
	}
	/**
	 * @param k1 the k1 to set
	 */
	public void setK1(String k1) {
		this.k1 = k1;
	}
	/**
	 * @return the s1
	 */
	public String getS1() {
		return s1;
	}
	/**
	 * @param s1 the s1 to set
	 */
	public void setS1(String s1) {
		this.s1 = s1;
	}
	/**
	 * @return the s2
	 */
	public String getS2() {
		return s2;
	}
	/**
	 * @param s2 the s2 to set
	 */
	public void setS2(String s2) {
		this.s2 = s2;
	}
	/**
	 * @return the s3
	 */
	public String getS3() {
		return s3;
	}
	/**
	 * @param s3 the s3 to set
	 */
	public void setS3(String s3) {
		this.s3 = s3;
	}
	/**
	 * @return the s4
	 */
	public String getS4() {
		return s4;
	}
	/**
	 * @param s4 the s4 to set
	 */
	public void setS4(String s4) {
		this.s4 = s4;
	}
	/**
	 * @return the s5
	 */
	public String getS5() {
		return s5;
	}
	/**
	 * @param s5 the s5 to set
	 */
	public void setS5(String s5) {
		this.s5 = s5;
	}
	/**
	 * @return the s6
	 */
	public String getS6() {
		return s6;
	}
	/**
	 * @param s6 the s6 to set
	 */
	public void setS6(String s6) {
		this.s6 = s6;
	}
	/**
	 * @return the s7
	 */
	public String getS7() {
		return s7;
	}
	/**
	 * @param s7 the s7 to set
	 */
	public void setS7(String s7) {
		this.s7 = s7;
	}
	/**
	 * @return the s8
	 */
	public String getS8() {
		return s8;
	}
	/**
	 * @param s8 the s8 to set
	 */
	public void setS8(String s8) {
		this.s8 = s8;
	}
	/**
	 * @return the s9
	 */
	public String getS9() {
		return s9;
	}
	/**
	 * @param s9 the s9 to set
	 */
	public void setS9(String s9) {
		this.s9 = s9;
	}
	/**
	 * @return the s10
	 */
	public String getS10() {
		return s10;
	}
	/**
	 * @param s10 the s10 to set
	 */
	public void setS10(String s10) {
		this.s10 = s10;
	}
	/**
	 * @return the s11
	 */
	public String getS11() {
		return s11;
	}
	/**
	 * @param s11 the s11 to set
	 */
	public void setS11(String s11) {
		this.s11 = s11;
	}
	/**
	 * @return the s12
	 */
	public String getS12() {
		return s12;
	}
	/**
	 * @param s12 the s12 to set
	 */
	public void setS12(String s12) {
		this.s12 = s12;
	}
	/**
	 * @return the s13
	 */
	public String getS13() {
		return s13;
	}
	/**
	 * @param s13 the s13 to set
	 */
	public void setS13(String s13) {
		this.s13 = s13;
	}
	/**
	 * @return the s14
	 */
	public String getS14() {
		return s14;
	}
	/**
	 * @param s14 the s14 to set
	 */
	public void setS14(String s14) {
		this.s14 = s14;
	}
	/**
	 * @return the s15
	 */
	public String getS15() {
		return s15;
	}
	/**
	 * @param s15 the s15 to set
	 */
	public void setS15(String s15) {
		this.s15 = s15;
	}
	/**
	 * @return the i1
	 */
	public int getI1() {
		return i1;
	}
	/**
	 * @param i1 the i1 to set
	 */
	public void setI1(int i1) {
		this.i1 = i1;
	}
	/**
	 * @return the i2
	 */
	public int getI2() {
		return i2;
	}
	/**
	 * @param i2 the i2 to set
	 */
	public void setI2(int i2) {
		this.i2 = i2;
	}
	/**
	 * @return the i3
	 */
	public int getI3() {
		return i3;
	}
	/**
	 * @param i3 the i3 to set
	 */
	public void setI3(int i3) {
		this.i3 = i3;
	}
	/**
	 * @return the d1
	 */
	public Double getD1() {
		return d1;
	}
	/**
	 * @param d1 the d1 to set
	 */
	public void setD1(Double d1) {
		this.d1 = d1;
	}
	/**
	 * @return the d2
	 */
	public Double getD2() {
		return d2;
	}
	/**
	 * @param d2 the d2 to set
	 */
	public void setD2(Double d2) {
		this.d2 = d2;
	}
	/**
	 * @return the d3
	 */
	public Double getD3() {
		return d3;
	}
	/**
	 * @param d3 the d3 to set
	 */
	public void setD3(Double d3) {
		this.d3 = d3;
	}
	/**
	 * @return the d4
	 */
	public Double getD4() {
		return d4;
	}
	/**
	 * @param d4 the d4 to set
	 */
	public void setD4(Double d4) {
		this.d4 = d4;
	}
	/**
	 * @return the t1
	 */
	public Date getT1() {
		return t1;
	}
	/**
	 * @param t1 the t1 to set
	 */
	public void setT1(Date t1) {
		this.t1 = t1;
	}
	/**
	 * @return the l1
	 */
	public List<String> getL1() {
		return l1;
	}
	/**
	 * @param l1 the l1 to set
	 */
	public void setL1(List<String> l1) {
		this.l1 = l1;
	}
	/**
	 * @return the l2
	 */
	public List<String> getL2() {
		return l2;
	}
	/**
	 * @param l2 the l2 to set
	 */
	public void setL2(List<String> l2) {
		this.l2 = l2;
	}
	/**
	 * @return the l3
	 */
	public List<String> getL3() {
		return l3;
	}
	/**
	 * @param l3 the l3 to set
	 */
	public void setL3(List<String> l3) {
		this.l3 = l3;
	}
	/**
	 * @return the l4
	 */
	public List<String> getL4() {
		return l4;
	}
	/**
	 * @param l4 the l4 to set
	 */
	public void setL4(List<String> l4) {
		this.l4 = l4;
	}
	/**
	 * @return the l5
	 */
	public List<String> getL5() {
		return l5;
	}
	/**
	 * @param l5 the l5 to set
	 */
	public void setL5(List<String> l5) {
		this.l5 = l5;
	}
	/**
	 * @return the l6
	 */
	public List<String> getL6() {
		return l6;
	}
	/**
	 * @param l6 the l6 to set
	 */
	public void setL6(List<String> l6) {
		this.l6 = l6;
	}
	/**
	 * @return the l7
	 */
	public List<String> getL7() {
		return l7;
	}
	/**
	 * @param l7 the l7 to set
	 */
	public void setL7(List<String> l7) {
		this.l7 = l7;
	}
	/**
	 * @return the l8
	 */
	public List<String> getL8() {
		return l8;
	}
	/**
	 * @param l8 the l8 to set
	 */
	public void setL8(List<String> l8) {
		this.l8 = l8;
	}
	/**
	 * @return the i4
	 */
	public int getI4() {
		return i4;
	}
	/**
	 * @param i4 the i4 to set
	 */
	public void setI4(int i4) {
		this.i4 = i4;
	}
	/**
	 * @return the i5
	 */
	public int getI5() {
		return i5;
	}
	/**
	 * @param i5 the i5 to set
	 */
	public void setI5(int i5) {
		this.i5 = i5;
	}
	
	
	
}

package com.cmall.productcenter.model;

/**
 * 
 * @author cc
 *
 */
public class PcProductAdpic {

	/**
     * 
     */
    private Integer zid   = 0 ;
    /**
     * 
     */
    private String uid  = ""  ;
    
    /**
     * 商品编号
     */
    private String product_code  = ""  ;
    
    /**
     * 商品的skuCode
     */
    private String sku_code="";
    
    /**
     * 图片路径
     */
    private String pic_url  = ""  ;
    
    /**
     * 展示开始时间
     */
    private String start_date = "";
    
    /**
     * 展示结束时间
     */
    private String end_date = "";
    
    /**
     * 序号
     */
    private Integer ord_no = 0;

	public Integer getZid() {
		return zid;
	}

	public void setZid(Integer zid) {
		this.zid = zid;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public String getStart_date() {
		return start_date;
	}

	public void setStart_date(String start_date) {
		this.start_date = start_date;
	}

	public String getEnd_date() {
		return end_date;
	}

	public void setEnd_date(String end_date) {
		this.end_date = end_date;
	}

	public Integer getOrd_no() {
		return ord_no;
	}

	public void setOrd_no(Integer ord_no) {
		this.ord_no = ord_no;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}    
}

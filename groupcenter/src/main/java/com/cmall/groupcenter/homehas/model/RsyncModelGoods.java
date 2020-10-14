package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;
import java.util.List;

public class RsyncModelGoods {

	private String good_id;
	private String good_nm;
	private String clss_nm;
	private String lclss_id;
	private String mclss_id;
	private String sclss_id;
	private String sale_cd;
	private String dlv_cd;
	private String accm_cd;
	private BigDecimal prc;
	private BigDecimal costs;
	private BigDecimal cprc;
	private String cd_val_desc;
	private String org_rgn;
	private String maker;
	private String selling_point_desc;
	private String good_func_expl;
	private String as_std_desc;
	private String good_asse_expl;
	private String brand_nm;
	private String cs_dispose_mode;
	private String mdf_date;
	private String accm_qty;
	private String dis_amt;
	private List<Image> images;
	private String validate_flag;//是否是虚拟商品
	private String gross_profit;
	
	private BigDecimal tax_rate;	//税率
	
	//新增属性
	private String prch_type;//一地入库类型
	private BigDecimal accm_rng;//积分
	private String dlr_id;//供应商编号
	private String dlr_nm;//供应商名称
	private String oa_site_no;//入库仓库编号
	private List<Site> site_no_list;//入库仓库编号列表（新）2016-08-04
	
	private String md_id;
	private String md_nm;
	
	//惠家有商品ID
	private String aq_good_id;
	
	private String  is_low_good;	//抄底价商品
	private String is_bill; // 提货券商品  Y ：是  N：否
	private String cust_day; //是否参与会员日
	
	private String prch_cd; // 是否计入毛利  Y ：是  N：否
	private String accm_yn; // 商品是否赋予积分  Y ：是  N：否
	private String no_gift; // Q：清仓商品，T：天天特价
	
	private String is_hwg; // 是否海外购商品  Y：是 N：否
	
	//===5.2.4 违禁品禁止下单 begin===
	private String wd; //长 (cm)
	private String dp; //宽 (cm)
	private String hg; //高 (cm)
	private String wg; //重量 (kg)
	private String is_unpack; //是否拆包件  Y / N
	private String is_danger; //违禁品属性
	private String check_danger; //判断是否需要检查是违禁品
	//===5.2.4 违禁品禁止下单 end===
	
	private String vl_ors; // 是否一件代发:Y/N
	private String dlr_charge; // 是否厂商收款:Y/N
	private String is_csps; // 是否厂商配送:Y/N
	private String so_id; //商品归属
	
	public String getSo_id() {
		return so_id;
	}
	public void setSo_id(String so_id) {
		this.so_id = so_id;
	}
	public String getCust_day() {
		return cust_day;
	}
	public void setCust_day(String cust_day) {
		this.cust_day = cust_day;
	}
	public String getIs_csps() {
		return is_csps;
	}
	public void setIs_csps(String is_csps) {
		this.is_csps = is_csps;
	}
	public String getIs_bill() {
		return is_bill;
	}
	public void setIs_bill(String is_bill) {
		this.is_bill = is_bill;
	}
	public String getMd_id() {
		return md_id;
	}
	public void setMd_id(String md_id) {
		this.md_id = md_id;
	}
	public String getMd_nm() {
		return md_nm;
	}
	public void setMd_nm(String md_nm) {
		this.md_nm = md_nm;
	}
	public String getGood_id() {
		return good_id;
	}
	public void setGood_id(String good_id) {
		this.good_id = good_id;
	}
	public String getGood_nm() {
		return good_nm;
	}
	public void setGood_nm(String good_nm) {
		this.good_nm = good_nm;
	}
	public String getClss_nm() {
		return clss_nm;
	}
	public void setClss_nm(String clss_nm) {
		this.clss_nm = clss_nm;
	}
	public String getLclss_id() {
		return lclss_id;
	}
	public void setLclss_id(String lclss_id) {
		this.lclss_id = lclss_id;
	}
	public String getMclss_id() {
		return mclss_id;
	}
	public void setMclss_id(String mclss_id) {
		this.mclss_id = mclss_id;
	}
	public String getSclss_id() {
		return sclss_id;
	}
	public void setSclss_id(String sclss_id) {
		this.sclss_id = sclss_id;
	}
	public String getSale_cd() {
		return sale_cd;
	}
	public void setSale_cd(String sale_cd) {
		this.sale_cd = sale_cd;
	}
	public String getDlv_cd() {
		return dlv_cd;
	}
	public void setDlv_cd(String dlv_cd) {
		this.dlv_cd = dlv_cd;
	}
	public String getAccm_cd() {
		return accm_cd;
	}
	public void setAccm_cd(String accm_cd) {
		this.accm_cd = accm_cd;
	}
	public BigDecimal getPrc() {
		return prc;
	}
	public void setPrc(BigDecimal prc) {
		this.prc = prc;
	}
	public BigDecimal getCosts() {
		return costs;
	}
	public void setCosts(BigDecimal costs) {
		this.costs = costs;
	}
	public BigDecimal getCprc() {
		return cprc;
	}
	public void setCprc(BigDecimal cprc) {
		this.cprc = cprc;
	}
	public String getCd_val_desc() {
		return cd_val_desc;
	}
	public void setCd_val_desc(String cd_val_desc) {
		this.cd_val_desc = cd_val_desc;
	}
	public String getOrg_rgn() {
		return org_rgn;
	}
	public void setOrg_rgn(String org_rgn) {
		this.org_rgn = org_rgn;
	}
	public String getMaker() {
		return maker;
	}
	public void setMaker(String maker) {
		this.maker = maker;
	}
	public String getSelling_point_desc() {
		return selling_point_desc;
	}
	public void setSelling_point_desc(String selling_point_desc) {
		this.selling_point_desc = selling_point_desc;
	}
	public String getGood_func_expl() {
		return good_func_expl;
	}
	public void setGood_func_expl(String good_func_expl) {
		this.good_func_expl = good_func_expl;
	}
	public String getAs_std_desc() {
		return as_std_desc;
	}
	public void setAs_std_desc(String as_std_desc) {
		this.as_std_desc = as_std_desc;
	}
	public String getGood_asse_expl() {
		return good_asse_expl;
	}
	public void setGood_asse_expl(String good_asse_expl) {
		this.good_asse_expl = good_asse_expl;
	}
	public String getBrand_nm() {
		return brand_nm;
	}
	public void setBrand_nm(String brand_nm) {
		this.brand_nm = brand_nm;
	}
	public String getCs_dispose_mode() {
		return cs_dispose_mode;
	}
	public void setCs_dispose_mode(String cs_dispose_mode) {
		this.cs_dispose_mode = cs_dispose_mode;
	}
	public String getMdf_date() {
		return mdf_date;
	}
	public void setMdf_date(String mdf_date) {
		this.mdf_date = mdf_date;
	}
	public String getAccm_qty() {
		return accm_qty;
	}
	public void setAccm_qty(String accm_qty) {
		this.accm_qty = accm_qty;
	}
	public String getDis_amt() {
		return dis_amt;
	}
	public void setDis_amt(String dis_amt) {
		this.dis_amt = dis_amt;
	}
	
	public List<Image> getImages() {
		return images;
	}
	public void setImages(List<Image> images) {
		this.images = images;
	}
	public String getGross_profit() {
		return gross_profit;
	}
	public void setGross_profit(String gross_profit) {
		this.gross_profit = gross_profit;
	}
	public String getValidate_flag() {
		return validate_flag;
	}
	public void setValidate_flag(String validate_flag) {
		this.validate_flag = validate_flag;
	}
	public String getPrch_type() {
		return prch_type;
	}
	public void setPrch_type(String prch_type) {
		this.prch_type = prch_type;
	}
	public BigDecimal getAccm_rng() {
		return accm_rng;
	}
	public void setAccm_rng(BigDecimal accm_rng) {
		this.accm_rng = accm_rng;
	}
	public String getDlr_id() {
		return dlr_id;
	}
	public void setDlr_id(String dlr_id) {
		this.dlr_id = dlr_id;
	}
	public String getDlr_nm() {
		return dlr_nm;
	}
	public void setDlr_nm(String dlr_nm) {
		this.dlr_nm = dlr_nm;
	}
	public String getOa_site_no() {
		return oa_site_no;
	}
	public void setOa_site_no(String oa_site_no) {
		this.oa_site_no = oa_site_no;
	}
	
	public BigDecimal getTax_rate() {
		return tax_rate;
	}
	public void setTax_rate(BigDecimal tax_rate) {
		this.tax_rate = tax_rate;
	}

	public String getAq_good_id() {
		return aq_good_id;
	}
	public void setAq_good_id(String aq_good_id) {
		this.aq_good_id = aq_good_id;
	}
	public String getIs_low_good() {
		return is_low_good;
	}
	public void setIs_low_good(String is_low_good) {
		this.is_low_good = is_low_good;
	}
	public List<Site> getSite_no_list() {
		return site_no_list;
	}
	public void setSite_no_list(List<Site> site_no_list) {
		this.site_no_list = site_no_list;
	} 
	public String getPrch_cd() {
		return prch_cd;
	}
	public void setPrch_cd(String prch_cd) {
		this.prch_cd = prch_cd;
	}

	public String getAccm_yn() {
		return accm_yn;
	}
	public void setAccm_yn(String accm_yn) {
		this.accm_yn = accm_yn;
	}
	public String getNo_gift() {
		return no_gift;
	}
	public void setNo_gift(String no_gift) {
		this.no_gift = no_gift;
	}
	public String getIs_hwg() {
		return is_hwg;
	}
	public void setIs_hwg(String is_hwg) {
		this.is_hwg = is_hwg;
	}
	public String getWd() {
		return wd;
	}
	public void setWd(String wd) {
		this.wd = wd;
	}
	public String getDp() {
		return dp;
	}
	public void setDp(String dp) {
		this.dp = dp;
	}
	public String getHg() {
		return hg;
	}
	public void setHg(String hg) {
		this.hg = hg;
	}
	public String getWg() {
		return wg;
	}
	public void setWg(String wg) {
		this.wg = wg;
	}
	public String getIs_unpack() {
		return is_unpack;
	}
	public void setIs_unpack(String is_unpack) {
		this.is_unpack = is_unpack;
	}
	public String getIs_danger() {
		return is_danger;
	}
	public void setIs_danger(String is_danger) {
		this.is_danger = is_danger;
	}

	public String getCheck_danger() {
		return check_danger;
	}
	public void setCheck_danger(String check_danger) {
		this.check_danger = check_danger;
	}

	public String getVl_ors() {
		return vl_ors;
	}
	public void setVl_ors(String vl_ors) {
		this.vl_ors = vl_ors;
	}

	public String getDlr_charge() {
		return dlr_charge;
	}
	public void setDlr_charge(String dlr_charge) {
		this.dlr_charge = dlr_charge;
	}


	public static class Site{
		private String SITE_NO;

		public String getSITE_NO() {
			return SITE_NO;
		}

		public void setSITE_NO(String sITE_NO) {
			SITE_NO = sITE_NO;
		}
		
	}
	public static class Image {
		
		private String good_id;
		private int good_seq;
		private String good_image;
		private String good_image_url;
		public String getGood_id() {
			return good_id;
		}
		public void setGood_id(String good_id) {
			this.good_id = good_id;
		}
		public int getGood_seq() {
			return good_seq;
		}
		public void setGood_seq(int good_seq) {
			this.good_seq = good_seq;
		}
		public String getGood_image() {
			return good_image;
		}
		public void setGood_image(String good_image) {
			this.good_image = good_image;
		}
		public String getGood_image_url() {
			return good_image_url;
		}
		public void setGood_image_url(String good_image_url) {
			this.good_image_url = good_image_url;
		}
		
	}
}

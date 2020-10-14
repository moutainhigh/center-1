package com.cmall.groupcenter.homehas.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * 会员信息返回接口
 * 
 * @author srnpr
 * 
 */
public class RsyncResponseInnerCustInfo extends RsyncResponseBase {

	private List<InnerCustInfo> result = new ArrayList<InnerCustInfo>();

	public List<InnerCustInfo> getResult() {
		return result;
	}

	public void setResult(List<InnerCustInfo> result) {
		this.result = result;
	}

	
	public static class InnerCustInfo{
		
		/**
		 * 会员编号
		 */
		private String cust_id="";
		/**
		 * 会员名称
		 */
		private String cust_nm="";
		private String sex_cd="";
		private String cust_lvl_cd="";
		private String cust_dlv_addr_seq="";
		private BigDecimal poss_crdt_amt=BigDecimal.ZERO;
		private BigDecimal poss_accm_amt=BigDecimal.ZERO;
		private BigDecimal poss_ppc_amt=BigDecimal.ZERO;
		private String zip_no="";
		private String addr_1="";
		private String addr_2="";
		private String teld="";
		private String telh="";
		private String teln="";
		private String teli="";
		/**
		 * 手机电话(区号)
		 */
		private String hp_teld="";
		/**
		 * 手机电话(前码)
		 */
		private String hp_telh="";
		/**
		 * 手机电话(号码)
		 */
		private String hp_teln="";
		private String last_bank_cd="";
		private String last_tm_id="";
		private String mail_send_yn="";
		private String mail_id="";
		private String dm_send_yn="";
		private String dl_desc_send_yn="";
		private String main_tm_id="";
		private String cust_source_cd="";
		private String sms_use_yn="";
		private String monpay_cd="";
		private String birth_ymd="";
		private String citi_no="";
		private String web_id="";
		public String getCust_id() {
			return cust_id;
		}
		public void setCust_id(String cust_id) {
			this.cust_id = cust_id;
		}
		public String getCust_nm() {
			return cust_nm;
		}
		public void setCust_nm(String cust_nm) {
			this.cust_nm = cust_nm;
		}
		public String getSex_cd() {
			return sex_cd;
		}
		public void setSex_cd(String sex_cd) {
			this.sex_cd = sex_cd;
		}
		public String getCust_lvl_cd() {
			return cust_lvl_cd;
		}
		public void setCust_lvl_cd(String cust_lvl_cd) {
			this.cust_lvl_cd = cust_lvl_cd;
		}
		public String getCust_dlv_addr_seq() {
			return cust_dlv_addr_seq;
		}
		public void setCust_dlv_addr_seq(String cust_dlv_addr_seq) {
			this.cust_dlv_addr_seq = cust_dlv_addr_seq;
		}
		public BigDecimal getPoss_crdt_amt() {
			return poss_crdt_amt;
		}
		public void setPoss_crdt_amt(BigDecimal poss_crdt_amt) {
			this.poss_crdt_amt = poss_crdt_amt;
		}
		public BigDecimal getPoss_accm_amt() {
			return poss_accm_amt;
		}
		public void setPoss_accm_amt(BigDecimal poss_accm_amt) {
			this.poss_accm_amt = poss_accm_amt;
		}
		public BigDecimal getPoss_ppc_amt() {
			return poss_ppc_amt;
		}
		public void setPoss_ppc_amt(BigDecimal poss_ppc_amt) {
			this.poss_ppc_amt = poss_ppc_amt;
		}
		public String getZip_no() {
			return zip_no;
		}
		public void setZip_no(String zip_no) {
			this.zip_no = zip_no;
		}
		public String getAddr_1() {
			return addr_1;
		}
		public void setAddr_1(String addr_1) {
			this.addr_1 = addr_1;
		}
		public String getAddr_2() {
			return addr_2;
		}
		public void setAddr_2(String addr_2) {
			this.addr_2 = addr_2;
		}
		public String getTeld() {
			return teld;
		}
		public void setTeld(String teld) {
			this.teld = teld;
		}
		public String getTelh() {
			return telh;
		}
		public void setTelh(String telh) {
			this.telh = telh;
		}
		public String getTeln() {
			return teln;
		}
		public void setTeln(String teln) {
			this.teln = teln;
		}
		public String getTeli() {
			return teli;
		}
		public void setTeli(String teli) {
			this.teli = teli;
		}
		public String getHp_teld() {
			return hp_teld;
		}
		public void setHp_teld(String hp_teld) {
			this.hp_teld = hp_teld;
		}
		public String getHp_telh() {
			return hp_telh;
		}
		public void setHp_telh(String hp_telh) {
			this.hp_telh = hp_telh;
		}
		public String getHp_teln() {
			return hp_teln;
		}
		public void setHp_teln(String hp_teln) {
			this.hp_teln = hp_teln;
		}
		public String getLast_bank_cd() {
			return last_bank_cd;
		}
		public void setLast_bank_cd(String last_bank_cd) {
			this.last_bank_cd = last_bank_cd;
		}
		public String getLast_tm_id() {
			return last_tm_id;
		}
		public void setLast_tm_id(String last_tm_id) {
			this.last_tm_id = last_tm_id;
		}
		public String getMail_send_yn() {
			return mail_send_yn;
		}
		public void setMail_send_yn(String mail_send_yn) {
			this.mail_send_yn = mail_send_yn;
		}
		public String getMail_id() {
			return mail_id;
		}
		public void setMail_id(String mail_id) {
			this.mail_id = mail_id;
		}
		public String getDm_send_yn() {
			return dm_send_yn;
		}
		public void setDm_send_yn(String dm_send_yn) {
			this.dm_send_yn = dm_send_yn;
		}
		public String getDl_desc_send_yn() {
			return dl_desc_send_yn;
		}
		public void setDl_desc_send_yn(String dl_desc_send_yn) {
			this.dl_desc_send_yn = dl_desc_send_yn;
		}
		public String getMain_tm_id() {
			return main_tm_id;
		}
		public void setMain_tm_id(String main_tm_id) {
			this.main_tm_id = main_tm_id;
		}
		public String getCust_source_cd() {
			return cust_source_cd;
		}
		public void setCust_source_cd(String cust_source_cd) {
			this.cust_source_cd = cust_source_cd;
		}
		public String getSms_use_yn() {
			return sms_use_yn;
		}
		public void setSms_use_yn(String sms_use_yn) {
			this.sms_use_yn = sms_use_yn;
		}
		public String getMonpay_cd() {
			return monpay_cd;
		}
		public void setMonpay_cd(String monpay_cd) {
			this.monpay_cd = monpay_cd;
		}
		public String getBirth_ymd() {
			return birth_ymd;
		}
		public void setBirth_ymd(String birth_ymd) {
			this.birth_ymd = birth_ymd;
		}
		public String getCiti_no() {
			return citi_no;
		}
		public void setCiti_no(String citi_no) {
			this.citi_no = citi_no;
		}
		public String getWeb_id() {
			return web_id;
		}
		public void setWeb_id(String web_id) {
			this.web_id = web_id;
		}
		
	}
	
}

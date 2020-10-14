package com.cmall.groupcenter.homehas.model;

import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncResponse;

/**
 * 会员信息同步响应
 * @author jl
 *
 */
public class RsyncResponseRsyncCustInfo  implements IRsyncResponse {

	
	private boolean success;
	private String message;
	private List<CustInfo> result = new ArrayList<CustInfo>();
	
	
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

	public List<CustInfo> getResult() {
		return result;
	}

	public void setResult(List<CustInfo> result) {
		this.result = result;
	}

	public static class CustInfo {
		private String cust_id ;
		private String cust_nm ;
		private String sex_cd ;
		private String cust_lvl_cd ;
		private String cust_dlv_addr_seq ;
		private String poss_crdt_amt ;
		private String poss_accm_amt ;
		private String poss_ppc_amt ;
		private String addr_2 ;
		private String teld ;
		private String telh ;
		private String teln ;
		private String teli ;
		private String hp_teld ;
		private String hp_telh ;
		private String hp_teln ;
		private String mail_send_yn ;
		private String mail_id ;
		private String dm_send_yn ;
		private String main_tm_id ;
		private String cust_source_cd ;
		private String sms_use_yn ;
		private String monpay_cd ;
		private String birth_ymd ;
		private String citi_no ;
		private List<CashGift> cashGiftList = new ArrayList<RsyncResponseRsyncCustInfo.CashGift>();
		private List<Point> pointList = new ArrayList<RsyncResponseRsyncCustInfo.Point>();
		private List<StoredGold> storedGoldList = new ArrayList<RsyncResponseRsyncCustInfo.StoredGold>();
		private List<TempGold> tempGoldList = new ArrayList<RsyncResponseRsyncCustInfo.TempGold>();
		private List<Address> addressList = new ArrayList<RsyncResponseRsyncCustInfo.Address>();

		public List<Address> getAddressList() {
			return addressList;
		}
		public void setAddressList(List<Address> addressList) {
			this.addressList = addressList;
		}
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
		public String getPoss_crdt_amt() {
			return poss_crdt_amt;
		}
		public void setPoss_crdt_amt(String poss_crdt_amt) {
			this.poss_crdt_amt = poss_crdt_amt;
		}
		public String getPoss_accm_amt() {
			return poss_accm_amt;
		}
		public void setPoss_accm_amt(String poss_accm_amt) {
			this.poss_accm_amt = poss_accm_amt;
		}
		public String getPoss_ppc_amt() {
			return poss_ppc_amt;
		}
		public void setPoss_ppc_amt(String poss_ppc_amt) {
			this.poss_ppc_amt = poss_ppc_amt;
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
		
		/**
		 * 礼金券数据集合
		 * @return
		 */
		public List<CashGift> getCashGiftList() {
			return cashGiftList;
		}
		public void setCashGiftList(List<CashGift> cashGiftList) {
			this.cashGiftList = cashGiftList;
		}
		
		/**
		 * 积分数据集合
		 * @return
		 */
		public List<Point> getPointList() {
			return pointList;
		}
		public void setPointList(List<Point> pointList) {
			this.pointList = pointList;
		}
		
		/**
		 * 储值金数据集合
		 * @return
		 */
		public List<StoredGold> getStoredGoldList() {
			return storedGoldList;
		}
		public void setStoredGoldList(List<StoredGold> storedGoldList) {
			this.storedGoldList = storedGoldList;
		}
		
		/***
		 * 暂存款数据集合
		 * @return
		 */
		public List<TempGold> getTempGoldList() {
			return tempGoldList;
		}
		public void setTempGoldList(List<TempGold> tempGoldList) {
			this.tempGoldList = tempGoldList;
		}
		
	}
	
	public static class CashGift {
		private String event_id ;
		private String cust_id ;
		private String lj_seq ;
		private String lj_amt ;
		private String lj_rel_id ;
		private String sy_vl ;
		private String vl_yn ;
		private String fr_date ;
		private String end_date ;
		private String etr_date ;
		private String mdf_date ;
		public String getEvent_id() {
			return event_id;
		}
		public void setEvent_id(String event_id) {
			this.event_id = event_id;
		}
		public String getCust_id() {
			return cust_id;
		}
		public void setCust_id(String cust_id) {
			this.cust_id = cust_id;
		}
		public String getLj_seq() {
			return lj_seq;
		}
		public void setLj_seq(String lj_seq) {
			this.lj_seq = lj_seq;
		}
		public String getLj_amt() {
			return lj_amt;
		}
		public void setLj_amt(String lj_amt) {
			this.lj_amt = lj_amt;
		}
		public String getLj_rel_id() {
			return lj_rel_id;
		}
		public void setLj_rel_id(String lj_rel_id) {
			this.lj_rel_id = lj_rel_id;
		}
		public String getSy_vl() {
			return sy_vl;
		}
		public void setSy_vl(String sy_vl) {
			this.sy_vl = sy_vl;
		}
		public String getVl_yn() {
			return vl_yn;
		}
		public void setVl_yn(String vl_yn) {
			this.vl_yn = vl_yn;
		}
		public String getFr_date() {
			return fr_date;
		}
		public void setFr_date(String fr_date) {
			this.fr_date = fr_date;
		}
		public String getEnd_date() {
			return end_date;
		}
		public void setEnd_date(String end_date) {
			this.end_date = end_date;
		}
		public String getEtr_date() {
			return etr_date;
		}
		public void setEtr_date(String etr_date) {
			this.etr_date = etr_date;
		}
		public String getMdf_date() {
			return mdf_date;
		}
		public void setMdf_date(String mdf_date) {
			this.mdf_date = mdf_date;
		}
		
	}
	
	public static class Point {
		
		private String cust_id ;
		private String accm_rsn_cd ;
		private String accm_amt ;
		private String accm_stat_cd ;
		private String etr_date ;
		private String mdf_date ;
		public String getCust_id() {
			return cust_id;
		}
		public void setCust_id(String cust_id) {
			this.cust_id = cust_id;
		}
		public String getAccm_rsn_cd() {
			return accm_rsn_cd;
		}
		public void setAccm_rsn_cd(String accm_rsn_cd) {
			this.accm_rsn_cd = accm_rsn_cd;
		}
		public String getAccm_amt() {
			return accm_amt;
		}
		public void setAccm_amt(String accm_amt) {
			this.accm_amt = accm_amt;
		}
		public String getAccm_stat_cd() {
			return accm_stat_cd;
		}
		public void setAccm_stat_cd(String accm_stat_cd) {
			this.accm_stat_cd = accm_stat_cd;
		}
		public String getEtr_date() {
			return etr_date;
		}
		public void setEtr_date(String etr_date) {
			this.etr_date = etr_date;
		}
		public String getMdf_date() {
			return mdf_date;
		}
		public void setMdf_date(String mdf_date) {
			this.mdf_date = mdf_date;
		}
		
	}
	
	
	public static class StoredGold {
		
		private String cust_id ;
		private String ppc_rsn_cd ;
		private String ppc_amt ;
		private String ppc_stat_cd ;
		private String etr_date ;
		private String mdf_date ;
		public String getCust_id() {
			return cust_id;
		}
		public void setCust_id(String cust_id) {
			this.cust_id = cust_id;
		}
		public String getPpc_rsn_cd() {
			return ppc_rsn_cd;
		}
		public void setPpc_rsn_cd(String ppc_rsn_cd) {
			this.ppc_rsn_cd = ppc_rsn_cd;
		}
		public String getPpc_amt() {
			return ppc_amt;
		}
		public void setPpc_amt(String ppc_amt) {
			this.ppc_amt = ppc_amt;
		}
		public String getPpc_stat_cd() {
			return ppc_stat_cd;
		}
		public void setPpc_stat_cd(String ppc_stat_cd) {
			this.ppc_stat_cd = ppc_stat_cd;
		}
		public String getEtr_date() {
			return etr_date;
		}
		public void setEtr_date(String etr_date) {
			this.etr_date = etr_date;
		}
		public String getMdf_date() {
			return mdf_date;
		}
		public void setMdf_date(String mdf_date) {
			this.mdf_date = mdf_date;
		}
		
	}
	
	public static class TempGold {
		
		private String cust_id ;
		private String crdt_cd ;
		private String crdt_amt ;
		private String crdt_stat_cd ;
		private String etr_date ;
		private String mdf_date ;
		public String getCust_id() {
			return cust_id;
		}
		public void setCust_id(String cust_id) {
			this.cust_id = cust_id;
		}
		public String getCrdt_cd() {
			return crdt_cd;
		}
		public void setCrdt_cd(String crdt_cd) {
			this.crdt_cd = crdt_cd;
		}
		public String getCrdt_amt() {
			return crdt_amt;
		}
		public void setCrdt_amt(String crdt_amt) {
			this.crdt_amt = crdt_amt;
		}
		public String getCrdt_stat_cd() {
			return crdt_stat_cd;
		}
		public void setCrdt_stat_cd(String crdt_stat_cd) {
			this.crdt_stat_cd = crdt_stat_cd;
		}
		public String getEtr_date() {
			return etr_date;
		}
		public void setEtr_date(String etr_date) {
			this.etr_date = etr_date;
		}
		public String getMdf_date() {
			return mdf_date;
		}
		public void setMdf_date(String mdf_date) {
			this.mdf_date = mdf_date;
		}
		
	}
	
	
	public static class Address {
		private String is_default; //是否默认地址
		private String rcver_name; //收货人姓名
		private String zip_no; //邮编
		private String addr_1; //省市区地址
		private String addr_2; //详细地址
		private String etr_date; //创建时间
		private String mdf_date; //修改时间
		private String area_cd; //区域编码
		private String rcver_mobile; //收货人电话
		
		
		public String getRcver_mobile() {
			return rcver_mobile;
		}
		public void setRcver_mobile(String rcver_mobile) {
			this.rcver_mobile = rcver_mobile;
		}
		public String getIs_default() {
			return is_default;
		}
		public void setIs_default(String is_default) {
			this.is_default = is_default;
		}
		public String getRcver_name() {
			return rcver_name;
		}
		public void setRcver_name(String rcver_name) {
			this.rcver_name = rcver_name;
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
		public String getEtr_date() {
			return etr_date;
		}
		public void setEtr_date(String etr_date) {
			this.etr_date = etr_date;
		}
		public String getMdf_date() {
			return mdf_date;
		}
		public void setMdf_date(String mdf_date) {
			this.mdf_date = mdf_date;
		}
		public String getArea_cd() {
			return area_cd;
		}
		public void setArea_cd(String area_cd) {
			this.area_cd = area_cd;
		}
		
		
		
		
	}
}



package com.cmall.groupcenter.homehas.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cmall.groupcenter.groupface.IRsyncRequest;

/**
 * 同步家有订单接口字段
 * 
 * @author jlin
 *
 */
public class RsyncRequestAddOrder implements IRsyncRequest , Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String subsystem = "app"; // 调用子系统
	private String account = ""; // 调用用户
	private String password = ""; // 调用密码
	private String cust_id = ""; // 客户Id 家有会员编号
	private String cust_mobile = ""; // 客户手机
	private List<Object> hidden_json = null; // 查询组合商品时的返回json串
	private String hidden_json_gift = null; // 组合商品赠品
	private String etr_id = "App"; // 创建者ID
	private String web_ord_date = ""; // APP下单日期
	private String web_ord_id = ""; // APP订单号
	private BigDecimal pay_amt = new BigDecimal(0); // 支付金额
	private String pay_time = ""; // 支付时间
	private String pay_no = ""; // 支付流水号
	private String dlv_date = ""; // 特殊服务配送日期
	private String dlv_service = ""; // 特殊服务
	private String dlv_time = ""; // 特殊服务配送时间
	private List<Goods> good_info = new ArrayList<RsyncRequestAddOrder.Goods>(); // 商品信息，多个
	private List<Goods> gift_good_info = new ArrayList<RsyncRequestAddOrder.Goods>(); // 商品信息，多个
	private String coupon_type = ""; // 使用代金券或礼金类别
	private String coupon_id = ""; // 使用的代金券或礼金序号
	private BigDecimal use_coupon_amt = new BigDecimal(0); // 使用的代金券或礼金金额
	private String medi_lclss_id = "7"; // 大媒体分类
	private String medi_mclss_id = "34"; // 中媒体分类
	private BigDecimal dlv_amt = new BigDecimal(0);// 运费

	private BigDecimal order_amt = new BigDecimal(0);// 订单总金额
	private BigDecimal use_crdt_amt = new BigDecimal(0);// 暂存款使用金额
	private BigDecimal use_accm_amt = new BigDecimal(0);// 积分使用金额
	private BigDecimal use_ppc_amt = new BigDecimal(0);// 储值金使用金额
	private String ord_lvl_cd = "10";// 订单等级
	private String event_id = "";// 活动编号
	private String pre_aft_pay_cd = "";// 代付方式
	private String send_bank_cd = ""; // 入款银行
	private int exterior_accm = 0;// 用户获得积分数
	private List<Long> lj_good_id = new ArrayList<Long>(); // 参加活动商品ID组合
	private String virtual_ord = "N"; // 虚拟商品
	private int accm_integral = 1;// 使用积分抵用倍数
	private int hy_type = 1;// 会员活动类型
	private String tel1 = "";// 收货人电话区号
	private String tel2 = "";// 收货人电话（包含分机）
	private String rcver_nm = "";// 收货人姓名
	private String mobile = "";// 收货人手机
	private String laddr = "";// 收货人省
	private String maddr = "";// 收货人市
	private String saddr = "";// 收货人区
	private String send_addr = "";// 收货人详细地址
	private String srgn_cd = "";// 收货人地址行政区划
	private String zip_no = "";// 邮编

	private BigDecimal goods_amt = new BigDecimal(0);
	private BigDecimal dis_amt = new BigDecimal(0);

	private String is_free_dlv_amt = "";// 配送公司是否需要收取运费 是 ：Y，否：N

	private String membercode = "";// 用户编号
	
	private String is_fyjf = "Y";// 是否赋予积分  是 ：Y，否：N
	
	private BigDecimal use_hb_amt;//使用惠币金额
	
	private String is_give_hb;//订单赋予积分还是惠币，Y：惠币，N积分
	
	public static class AddEvent implements Serializable {
		private static final long serialVersionUID = 1L;
		
		private String event_id = ""; // LD系统活动编号
		private BigDecimal save_amt = BigDecimal.ZERO; // 优惠金额
		
		public String getEvent_id() {
			return event_id;
		}
		public void setEvent_id(String event_id) {
			this.event_id = event_id;
		}
		public BigDecimal getSave_amt() {
			return save_amt;
		}
		public void setSave_amt(BigDecimal save_amt) {
			this.save_amt = save_amt;
		}
		public static long getSerialversionuid() {
			return serialVersionUID;
		}
		
	}

	public static class Goods implements Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private long good_id = -1; // 商品代码
		private int good_cnt = 0; // 订购数量
		private BigDecimal good_prc = new BigDecimal(0); // 商品价格 商品原价
		private String color_id = ""; // 家有颜色编号
		private String style_id = ""; // 家有样式编号
		private String dely_fee = "0"; // 商品运费 0
//		private String save_amt = ""; // 活动优惠金额
		private String inv_yn = ""; // 是否修改发票
		private String inv_type = ""; // 发票类型
		private String inv_head = ""; // 发票抬头
		private String event_id = "";

		private String site_no = "";
		private String gift_cd = "";
		private int main_and_gift = 0;
		
		private String tg_cust_id;//订单推广人ID
		
		private BigDecimal tg_hb_amt;//推广人惠币奖励
		
		private String tg_hb_type;//推广收益类型：T 推广赚  M  买家秀

		// 一单不同商品使用不同活动 新增字段
		private String coupon_type = ""; // 折扣类型 0 礼金 1 优惠券 2 折扣立减
//		private String coupon_id = ""; // 礼金序号或者优惠券编码
		private List<CouponInfo> couponList = new ArrayList<RsyncRequestAddOrder.CouponInfo>();
		
		private List<AddEvent> eventList = new ArrayList<AddEvent>();
		
		public List<AddEvent> getEventList() {
			return eventList;
		}

		public void setEventList(List<AddEvent> eventList) {
			this.eventList = eventList;
		}
		
		public List<CouponInfo> getCouponList() {
			return couponList;
		}

		public void setCouponList(List<CouponInfo> couponList) {
			this.couponList = couponList;
		}

		public String getInv_yn() {
			return inv_yn;
		}

		public void setInv_yn(String inv_yn) {
			this.inv_yn = inv_yn;
		}

		public String getInv_type() {
			return inv_type;
		}

		public void setInv_type(String inv_type) {
			this.inv_type = inv_type;
		}

		public int getMain_and_gift() {
			return main_and_gift;
		}

		public void setMain_and_gift(int main_and_gift) {
			this.main_and_gift = main_and_gift;
		}

		public String getSite_no() {
			return site_no;
		}

		public void setSite_no(String site_no) {
			this.site_no = site_no;
		}

		public String getGift_cd() {
			return gift_cd;
		}

		public void setGift_cd(String gift_cd) {
			this.gift_cd = gift_cd;
		}

		public long getGood_id() {
			return good_id;
		}

		public void setGood_id(long good_id) {
			this.good_id = good_id;
		}

		public int getGood_cnt() {
			return good_cnt;
		}

		public void setGood_cnt(int good_cnt) {
			this.good_cnt = good_cnt;
		}

		public BigDecimal getGood_prc() {
			return good_prc;
		}

		public void setGood_prc(BigDecimal good_prc) {
			this.good_prc = good_prc;
		}

		public String getColor_id() {
			return color_id;
		}

		public void setColor_id(String color_id) {
			this.color_id = color_id;
		}

		public String getStyle_id() {
			return style_id;
		}

		public void setStyle_id(String style_id) {
			this.style_id = style_id;
		}

		public String getDely_fee() {
			return dely_fee;
		}

		public void setDely_fee(String dely_fee) {
			this.dely_fee = dely_fee;
		}

//		public String getSave_amt() {
//			return save_amt;
//		}
//
//		public void setSave_amt(String save_amt) {
//			this.save_amt = save_amt;
//		}

		public String getInv_head() {
			return inv_head;
		}

		public void setInv_head(String inv_head) {
			this.inv_head = inv_head;
		}

		public String getEvent_id() {
			return event_id;
		}

		public void setEvent_id(String event_id) {
			this.event_id = event_id;
		}

		public String getCoupon_type() {
			return coupon_type;
		}

		public void setCoupon_type(String coupon_type) {
			this.coupon_type = coupon_type;
		}

		public String getTg_cust_id() {
			return tg_cust_id;
		}

		public void setTg_cust_id(String tg_cust_id) {
			this.tg_cust_id = tg_cust_id;
		}

		public BigDecimal getTg_hb_amt() {
			return tg_hb_amt;
		}

		public void setTg_hb_amt(BigDecimal tg_hb_amt) {
			this.tg_hb_amt = tg_hb_amt;
		}

		public String getTg_hb_type() {
			return tg_hb_type;
		}

		public void setTg_hb_type(String tg_hb_type) {
			this.tg_hb_type = tg_hb_type;
		}

//		public String getCoupon_id() {
//			return coupon_id;
//		}
//
//		public void setCoupon_id(String coupon_id) {
//			this.coupon_id = coupon_id;
//		}
		

	}
	
	public class CouponInfo implements Serializable {
		
		private static final long serialVersionUID = 1L;
		
		private String coupon_id;
		private BigDecimal save_amt = BigDecimal.ZERO;
		public String getCoupon_id() {
			return coupon_id;
		}
		public void setCoupon_id(String coupon_id) {
			this.coupon_id = coupon_id;
		}
		public BigDecimal getSave_amt() {
			return save_amt;
		}
		public void setSave_amt(BigDecimal save_amt) {
			this.save_amt = save_amt;
		}
		
	}

	public BigDecimal getUse_hb_amt() {
		return use_hb_amt;
	}

	public void setUse_hb_amt(BigDecimal use_hb_amt) {
		this.use_hb_amt = use_hb_amt;
	}

	public String getIs_give_hb() {
		return is_give_hb;
	}

	public void setIs_give_hb(String is_give_hb) {
		this.is_give_hb = is_give_hb;
	}

	public String getSubsystem() {
		return subsystem;
	}

	public void setSubsystem(String subsystem) {
		this.subsystem = subsystem;
	}

	public String getCust_mobile() {
		return cust_mobile;
	}

	public void setCust_mobile(String cust_mobile) {
		this.cust_mobile = cust_mobile;
	}

	public String getEtr_id() {
		return etr_id;
	}

	public void setEtr_id(String etr_id) {
		this.etr_id = etr_id;
	}

	public String getWeb_ord_date() {
		return web_ord_date;
	}

	public void setWeb_ord_date(String web_ord_date) {
		this.web_ord_date = web_ord_date;
	}

	public String getWeb_ord_id() {
		return web_ord_id;
	}

	public void setWeb_ord_id(String web_ord_id) {
		this.web_ord_id = web_ord_id;
	}

	public BigDecimal getPay_amt() {
		return pay_amt;
	}

	public void setPay_amt(BigDecimal pay_amt) {
		this.pay_amt = pay_amt;
	}

	public List<Goods> getGood_info() {
		return good_info;
	}

	public void setGood_info(List<Goods> good_info) {
		this.good_info = good_info;
	}

	public String getMedi_lclss_id() {
		return medi_lclss_id;
	}

	public void setMedi_lclss_id(String medi_lclss_id) {
		this.medi_lclss_id = medi_lclss_id;
	}

	public String getMedi_mclss_id() {
		return medi_mclss_id;
	}

	public void setMedi_mclss_id(String medi_mclss_id) {
		this.medi_mclss_id = medi_mclss_id;
	}

	public BigDecimal getDlv_amt() {
		return dlv_amt;
	}

	public void setDlv_amt(BigDecimal dlv_amt) {
		this.dlv_amt = dlv_amt;
	}

	public BigDecimal getOrder_amt() {
		return order_amt;
	}

	public void setOrder_amt(BigDecimal order_amt) {
		this.order_amt = order_amt;
	}

	public BigDecimal getUse_crdt_amt() {
		return use_crdt_amt;
	}

	public void setUse_crdt_amt(BigDecimal use_crdt_amt) {
		this.use_crdt_amt = use_crdt_amt;
	}

	public BigDecimal getUse_accm_amt() {
		return use_accm_amt;
	}

	public void setUse_accm_amt(BigDecimal use_accm_amt) {
		this.use_accm_amt = use_accm_amt;
	}

	public BigDecimal getUse_ppc_amt() {
		return use_ppc_amt;
	}

	public void setUse_ppc_amt(BigDecimal use_ppc_amt) {
		this.use_ppc_amt = use_ppc_amt;
	}

	public String getOrd_lvl_cd() {
		return ord_lvl_cd;
	}

	public void setOrd_lvl_cd(String ord_lvl_cd) {
		this.ord_lvl_cd = ord_lvl_cd;
	}

	public String getEvent_id() {
		return event_id;
	}

	public void setEvent_id(String event_id) {
		this.event_id = event_id;
	}

	public String getPre_aft_pay_cd() {
		return pre_aft_pay_cd;
	}

	public void setPre_aft_pay_cd(String pre_aft_pay_cd) {
		this.pre_aft_pay_cd = pre_aft_pay_cd;
	}

	public int getExterior_accm() {
		return exterior_accm;
	}

	public void setExterior_accm(int exterior_accm) {
		this.exterior_accm = exterior_accm;
	}

	public List<Long> getLj_good_id() {
		return lj_good_id;
	}

	public void setLj_good_id(List<Long> lj_good_id) {
		this.lj_good_id = lj_good_id;
	}

	public String getVirtual_ord() {
		return virtual_ord;
	}

	public void setVirtual_ord(String virtual_ord) {
		this.virtual_ord = virtual_ord;
	}

	public int getAccm_integral() {
		return accm_integral;
	}

	public void setAccm_integral(int accm_integral) {
		this.accm_integral = accm_integral;
	}

	public int getHy_type() {
		return hy_type;
	}

	public void setHy_type(int hy_type) {
		this.hy_type = hy_type;
	}

	public String getRcver_nm() {
		return rcver_nm;
	}

	public void setRcver_nm(String rcver_nm) {
		this.rcver_nm = rcver_nm;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getLaddr() {
		return laddr;
	}

	public void setLaddr(String laddr) {
		this.laddr = laddr;
	}

	public String getMaddr() {
		return maddr;
	}

	public void setMaddr(String maddr) {
		this.maddr = maddr;
	}

	public String getSaddr() {
		return saddr;
	}

	public void setSaddr(String saddr) {
		this.saddr = saddr;
	}

	public String getSend_addr() {
		return send_addr;
	}

	public void setSend_addr(String send_addr) {
		this.send_addr = send_addr;
	}

	public String getSrgn_cd() {
		return srgn_cd;
	}

	public void setSrgn_cd(String srgn_cd) {
		this.srgn_cd = srgn_cd;
	}

	public String getZip_no() {
		return zip_no;
	}

	public void setZip_no(String zip_no) {
		this.zip_no = zip_no;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public List<Goods> getGift_good_info() {
		return gift_good_info;
	}

	public void setGift_good_info(List<Goods> gift_good_info) {
		this.gift_good_info = gift_good_info;
	}

	public String getSend_bank_cd() {
		return send_bank_cd;
	}

	public void setSend_bank_cd(String send_bank_cd) {
		this.send_bank_cd = send_bank_cd;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getCoupon_type() {
		return coupon_type;
	}

	public void setCoupon_type(String coupon_type) {
		this.coupon_type = coupon_type;
	}

	public String getCoupon_id() {
		return coupon_id;
	}

	public void setCoupon_id(String coupon_id) {
		this.coupon_id = coupon_id;
	}

	public BigDecimal getUse_coupon_amt() {
		return use_coupon_amt;
	}

	public void setUse_coupon_amt(BigDecimal use_coupon_amt) {
		this.use_coupon_amt = use_coupon_amt;
	}

	public List<Object> getHidden_json() {
		return hidden_json;
	}

	public void setHidden_json(List<Object> hidden_json) {
		this.hidden_json = hidden_json;
	}

	public String getHidden_json_gift() {
		return hidden_json_gift;
	}

	public void setHidden_json_gift(String hidden_json_gift) {
		this.hidden_json_gift = hidden_json_gift;
	}

	public String getPay_no() {
		return pay_no;
	}

	public void setPay_no(String pay_no) {
		this.pay_no = pay_no;
	}

	public String getPay_time() {
		return pay_time;
	}

	public void setPay_time(String pay_time) {
		this.pay_time = pay_time;
	}

	public String getDlv_date() {
		return dlv_date;
	}

	public void setDlv_date(String dlv_date) {
		this.dlv_date = dlv_date;
	}

	public String getDlv_service() {
		return dlv_service;
	}

	public void setDlv_service(String dlv_service) {
		this.dlv_service = dlv_service;
	}

	public String getDlv_time() {
		return dlv_time;
	}

	public void setDlv_time(String dlv_time) {
		this.dlv_time = dlv_time;
	}

	public String getTel1() {
		return tel1;
	}

	public void setTel1(String tel1) {
		this.tel1 = tel1;
	}

	public String getTel2() {
		return tel2;
	}

	public void setTel2(String tel2) {
		this.tel2 = tel2;
	}

	public BigDecimal getGoods_amt() {
		return goods_amt;
	}

	public void setGoods_amt(BigDecimal goods_amt) {
		this.goods_amt = goods_amt;
	}

	public BigDecimal getDis_amt() {
		return dis_amt;
	}

	public void setDis_amt(BigDecimal dis_amt) {
		this.dis_amt = dis_amt;
	}

	public String getIs_free_dlv_amt() {
		return is_free_dlv_amt;
	}

	public void setIs_free_dlv_amt(String is_free_dlv_amt) {
		this.is_free_dlv_amt = is_free_dlv_amt;
	}

	public String getMembercode() {
		return membercode;
	}

	public void setMembercode(String membercode) {
		this.membercode = membercode;
	}

	public String getIs_fyjf() {
		return is_fyjf;
	}

	public void setIs_fyjf(String is_fyjf) {
		this.is_fyjf = is_fyjf;
	}

}

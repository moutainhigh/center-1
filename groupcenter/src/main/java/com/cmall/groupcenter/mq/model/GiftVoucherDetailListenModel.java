package com.cmall.groupcenter.mq.model;

/**
 * 
 * @remark 礼金券详情
 * @author 任宏斌
 * @date 2018年9月17日
 */
public class GiftVoucherDetailListenModel { 

	/**
	 * 礼金券编号
	 * 格式：活动编号-客代-礼金券序号
	 */
	private String lj_code;
	
	/**
	 * LD客户代码
	 */
	private String cust_id;
	
	/**
	 * 惠家有用户编号
	 */
	private String membercode;
	
	/**
	 * 礼金金额
	 */
	private String lj_amt;
	
	/**
	 * 相关LD订单号码
	 */
	private String lj_rel_id;
	
	/**
	 * 是否使用
	 */
	private String sy_vl;
	
	/**
	 * 是否有效
	 */
	private String vl_yn;
	
	/**
	 * 输入者ID
	 */
	private String etr_id;
	
	/**
	 * 输入日期
	 */
	private String etr_date;
	
	/**
	 * 修改者ID
	 */
	private String mdf_id;
	
	/**
	 * 修改日期
	 */
	private String mdf_date;
	
	/**
	 * 有效开始时间
	 */
	private String fr_date;
	
	/**
	 * 有效结束时间
	 */
	private String end_date;
	
	/**
	 * 礼金使用序号
	 */
	private String lj_rel_seq;
	
	/**
	 * 最低订单金额
	 */
	private String ord_amt;
	
	/**
	 * 惠家有关联订单号
	 */
	private String hjy_ord_id;
	
	/**
	 * 用户手机号
	 */
	private String mobile;
	
	/**
	 * 惠家有优惠券编号
	 */
	private String coupon_code;
	
	/**
	 * 折扣类型 10金额券 20折扣券
	 */
	private String dis_type;
	
	/**
	 * 错误、异常信息
	 */
	private String message;
	
	/**
	 * 客户等级
	 */
	private String cust_lvl_cd;
	
	/**
	 * 礼金余额 可找零时有效
	 */
	private String lj_balance_amt;
	
	/**
	 * 是否允许找零 Y/N
	 */
	private String is_change;

	public String getLj_code() {
		return lj_code;
	}

	public void setLj_code(String lj_code) {
		this.lj_code = lj_code;
	}

	public String getCust_id() {
		return cust_id;
	}

	public void setCust_id(String cust_id) {
		this.cust_id = cust_id;
	}

	public String getMembercode() {
		return membercode;
	}

	public void setMembercode(String membercode) {
		this.membercode = membercode;
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

	public String getEtr_id() {
		return etr_id;
	}

	public void setEtr_id(String etr_id) {
		this.etr_id = etr_id;
	}

	public String getEtr_date() {
		return etr_date;
	}

	public void setEtr_date(String etr_date) {
		this.etr_date = etr_date;
	}

	public String getMdf_id() {
		return mdf_id;
	}

	public void setMdf_id(String mdf_id) {
		this.mdf_id = mdf_id;
	}

	public String getMdf_date() {
		return mdf_date;
	}

	public void setMdf_date(String mdf_date) {
		this.mdf_date = mdf_date;
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

	public String getLj_rel_seq() {
		return lj_rel_seq;
	}

	public void setLj_rel_seq(String lj_rel_seq) {
		this.lj_rel_seq = lj_rel_seq;
	}

	public String getOrd_amt() {
		return ord_amt;
	}

	public void setOrd_amt(String ord_amt) {
		this.ord_amt = ord_amt;
	}

	public String getHjy_ord_id() {
		return hjy_ord_id;
	}

	public void setHjy_ord_id(String hjy_ord_id) {
		this.hjy_ord_id = hjy_ord_id;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCoupon_code() {
		return coupon_code;
	}

	public void setCoupon_code(String coupon_code) {
		this.coupon_code = coupon_code;
	}

	public String getDis_type() {
		return dis_type;
	}

	public void setDis_type(String dis_type) {
		this.dis_type = dis_type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getCust_lvl_cd() {
		return cust_lvl_cd;
	}

	public void setCust_lvl_cd(String cust_lvl_cd) {
		this.cust_lvl_cd = cust_lvl_cd;
	}

	public String getLj_balance_amt() {
		return lj_balance_amt;
	}

	public void setLj_balance_amt(String lj_balance_amt) {
		this.lj_balance_amt = lj_balance_amt;
	}

	public String getIs_change() {
		return is_change;
	}

	public void setIs_change(String is_change) {
		this.is_change = is_change;
	}
}

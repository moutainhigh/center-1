package com.cmall.groupcenter.mq.model;

/**
 * 
 * @remark 活动信息
 * @author 任宏斌
 * @date 2018年9月17日
 */
public class ActivityListenModel {

	/**
	 * 促销代码
	 */
	private Integer event_id;

	/**
	 * 开始日期
	 */
	private String fr_date;

	/**
	 * 结束日期
	 */
	private String end_date;

	/**
	 * 促销类别
	 */
	private String event_cd;

	/**
	 * 促销名
	 */
	private String event_nm;

	/**
	 * 促销活动说明
	 */
	private String event_desc;

	/**
	 * 有效与否，默认为'Y'
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
	 * 加赠方式
	 */
	private String gift_tp;

	/**
	 * 加赠地区
	 */
	private String eve_addr;

	/**
	 * 订购次数
	 */
	private Integer gift_cnt;

	/**
	 * 订购金额
	 */
	private String gift_amt;

	/**
	 * 商品限定
	 */
	private String good_yn;

	/**
	 * 是否允许多次加价购，默认'Y'
	 */
	private String is_manyjjg;
	
	/**
	 * 01:按订单统计；02:按商品统计；默认01
	 */
	private String grgood_type;
	
	/**
	 * 活动参与方式(10 商品 20 订单)
	 */
	private String attend_mode;
	
	/**
	 * 活动承担部门
	 */
	private String dept_event;
	
	/**
	 * 订单是否多次参与
	 */
	private String is_manyorder;
	
	/**
	 * 是否可叠加使用
	 */
	private String is_superposition;
	
	/**
	 * 活动面额
	 */
	private String dis_amt;
	
	/**
	 * 活动最低使用金额
	 */
	private String low_amt; 
	
	/**
	 * 活动使用开始时间
	 */
	private String ord_fr_date;
	
	/**
	 * 活动使用结束时间
	 */
	private String ord_end_date;
	
	/**
	 * 商品限定字段，值是商品id的集合，逗号隔开
	 */
	private String goodlimit;
	
	/**
	 * 品类限定字段
	 */
	private String classlimit;
	
	/**
	 * 不参与活动的商品集合
	 */
	private String goodnojoin;
	
	/**
	 * 区域限制编号
	 */
	private String lrgn_codes;
	
	/**
	 * 客户等级允许编号
	 */
	private String cust_lvl_codes;
	
	/**
	 * 支付方式限制编号
	 */
	private String pay_codes;
	
	/**
	 * 客户限制编号
	 */
	private String cust_codes;
	
	/**
	 * 优惠券编号
	 */
	private String coupus_no;
	
	/**
	 * 订单最大数量
	 */
	private String ord_cnt;
	
	/**
	 * 优惠券类型
	 */
	private String dis_type;
	
	/**
	 * 订购类型
	 */
	private String ord_type;
	
	/**
	 * 错误、异常信息
	 */
	private String message;
	
	/**
	 * 叠加使用上限
	 */
	private String disup_amt;
	
	/**
	 * 最低金额限制方式
	 * 00 无限制
	 * 10 叠加最低订购金额
	 * 20 礼金/商品金额占比
	 */
	private String minlimit_tp;
	
	/**
	 * 礼金/商品金额占比
	 */
	private String minlimit_amt;
	
	/**
	 * 单张使用时不受以上叠加规则限制
	 */
	private String is_onelimit;
	
	/**
	 * 立减金额以下不受最低金额限制
	 */
	private String mindis_amt;
	
	/**
	 * 是否允许找零
	 */
	private String is_change;

	public Integer getEvent_id() {
		return event_id;
	}

	public void setEvent_id(Integer event_id) {
		this.event_id = event_id;
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

	public String getEvent_cd() {
		return event_cd;
	}

	public void setEvent_cd(String event_cd) {
		this.event_cd = event_cd;
	}

	public String getEvent_nm() {
		return event_nm;
	}

	public void setEvent_nm(String event_nm) {
		this.event_nm = event_nm;
	}

	public String getEvent_desc() {
		return event_desc;
	}

	public void setEvent_desc(String event_desc) {
		this.event_desc = event_desc;
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

	public String getGift_tp() {
		return gift_tp;
	}

	public void setGift_tp(String gift_tp) {
		this.gift_tp = gift_tp;
	}

	public String getEve_addr() {
		return eve_addr;
	}

	public void setEve_addr(String eve_addr) {
		this.eve_addr = eve_addr;
	}

	public Integer getGift_cnt() {
		return gift_cnt;
	}

	public void setGift_cnt(Integer gift_cnt) {
		this.gift_cnt = gift_cnt;
	}

	public String getGift_amt() {
		return gift_amt;
	}

	public void setGift_amt(String gift_amt) {
		this.gift_amt = gift_amt;
	}

	public String getGood_yn() {
		return good_yn;
	}

	public void setGood_yn(String good_yn) {
		this.good_yn = good_yn;
	}

	public String getIs_manyjjg() {
		return is_manyjjg;
	}

	public void setIs_manyjjg(String is_manyjjg) {
		this.is_manyjjg = is_manyjjg;
	}

	public String getGrgood_type() {
		return grgood_type;
	}

	public void setGrgood_type(String grgood_type) {
		this.grgood_type = grgood_type;
	}

	public String getAttend_mode() {
		return attend_mode;
	}

	public void setAttend_mode(String attend_mode) {
		this.attend_mode = attend_mode;
	}

	public String getDept_event() {
		return dept_event;
	}

	public void setDept_event(String dept_event) {
		this.dept_event = dept_event;
	}

	public String getIs_manyorder() {
		return is_manyorder;
	}

	public void setIs_manyorder(String is_manyorder) {
		this.is_manyorder = is_manyorder;
	}

	public String getIs_superposition() {
		return is_superposition;
	}

	public void setIs_superposition(String is_superposition) {
		this.is_superposition = is_superposition;
	}

	public String getDis_amt() {
		return dis_amt;
	}

	public void setDis_amt(String dis_amt) {
		this.dis_amt = dis_amt;
	}

	public String getLow_amt() {
		return low_amt;
	}

	public void setLow_amt(String low_amt) {
		this.low_amt = low_amt;
	}

	public String getOrd_fr_date() {
		return ord_fr_date;
	}

	public void setOrd_fr_date(String ord_fr_date) {
		this.ord_fr_date = ord_fr_date;
	}

	public String getOrd_end_date() {
		return ord_end_date;
	}

	public void setOrd_end_date(String ord_end_date) {
		this.ord_end_date = ord_end_date;
	}

	public String getGoodlimit() {
		return goodlimit;
	}

	public void setGoodlimit(String goodlimit) {
		this.goodlimit = goodlimit;
	}

	public String getClasslimit() {
		return classlimit;
	}

	public void setClasslimit(String classlimit) {
		this.classlimit = classlimit;
	}

	public String getGoodnojoin() {
		return goodnojoin;
	}

	public void setGoodnojoin(String goodnojoin) {
		this.goodnojoin = goodnojoin;
	}

	public String getLrgn_codes() {
		return lrgn_codes;
	}

	public void setLrgn_codes(String lrgn_codes) {
		this.lrgn_codes = lrgn_codes;
	}

	public String getCust_lvl_codes() {
		return cust_lvl_codes;
	}

	public void setCust_lvl_codes(String cust_lvl_codes) {
		this.cust_lvl_codes = cust_lvl_codes;
	}

	public String getPay_codes() {
		return pay_codes;
	}

	public void setPay_codes(String pay_codes) {
		this.pay_codes = pay_codes;
	}

	public String getCust_codes() {
		return cust_codes;
	}

	public void setCust_codes(String cust_codes) {
		this.cust_codes = cust_codes;
	}

	public String getCoupus_no() {
		return coupus_no;
	}

	public void setCoupus_no(String coupus_no) {
		this.coupus_no = coupus_no;
	}

	public String getOrd_cnt() {
		return ord_cnt;
	}

	public void setOrd_cnt(String ord_cnt) {
		this.ord_cnt = ord_cnt;
	}

	public String getDis_type() {
		return dis_type;
	}

	public void setDis_type(String dis_type) {
		this.dis_type = dis_type;
	}

	public String getOrd_type() {
		return ord_type;
	}

	public void setOrd_type(String ord_type) {
		this.ord_type = ord_type;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getDisup_amt() {
		return disup_amt;
	}

	public void setDisup_amt(String disup_amt) {
		this.disup_amt = disup_amt;
	}

	public String getMinlimit_tp() {
		return minlimit_tp;
	}

	public void setMinlimit_tp(String minlimit_tp) {
		this.minlimit_tp = minlimit_tp;
	}

	public String getMinlimit_amt() {
		return minlimit_amt;
	}

	public void setMinlimit_amt(String minlimit_amt) {
		this.minlimit_amt = minlimit_amt;
	}

	public String getIs_onelimit() {
		return is_onelimit;
	}

	public void setIs_onelimit(String is_onelimit) {
		this.is_onelimit = is_onelimit;
	}

	public String getMindis_amt() {
		return mindis_amt;
	}

	public void setMindis_amt(String mindis_amt) {
		this.mindis_amt = mindis_amt;
	}

	public String getIs_change() {
		return is_change;
	}

	public void setIs_change(String is_change) {
		this.is_change = is_change;
	}

}
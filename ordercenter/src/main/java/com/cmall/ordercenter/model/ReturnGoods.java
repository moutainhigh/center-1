package com.cmall.ordercenter.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.srnpr.zapcom.baseclass.BaseClass;
/**
 * 退货类
 * @author:     hexd
 * Date:        2013年9月11日
 * project_name:ordercenter
 */
public class ReturnGoods extends BaseClass
{
	/**
	 * 退货单详情
	 */
	List<RetuGoodDetailChild> detailList =new ArrayList<RetuGoodDetailChild>();
	/**
	 * 买家编号
	 */
	private String buyer_code = "";
	/**
	 * 订单编号
	 */
	private String order_code = "";
	/**
	 * 退货原因
	 */
	private String return_reason = "";
	/**
	 * 商家编码
	 */
	private String seller_code = "";

	/**
	 * 联系人
	 */
	private String contacts = "";
	/**
	 * 运费
	 */
	private float transport_money = 0;
	/**
	 * 电话
	 */
	private String mobile = "";
	/**
	 * 地址
	 */
	private String address = "";
	/**
	 * 图片链接
	 */
	private String pic_url = "";
	/**
	 * 退货状态
	 */
	private String status = "";
	
	/**
	 * 退货单号
	 */
	private String return_code =  "";
	
	/**
	 * 描述
	 */
	private String  description = "";

	/**
	 * 第三方买家编号
	 */
	private String small_seller_code="";
	
	private String goods_receipt="";
	private String buyer_mobile="";
	
	private BigDecimal expected_return_money=BigDecimal.ZERO;
	private BigDecimal expected_return_group_money=BigDecimal.ZERO;
	private String flag_return_goods="";
	
	// 退款积分金额
	private BigDecimal expected_return_accm_money=BigDecimal.ZERO;
	
	// 退款惠币金额
	private BigDecimal expected_return_hjycoin_money=BigDecimal.ZERO;
	
	// 退款储值金金额
	private BigDecimal expected_return_ppc_money=BigDecimal.ZERO;
	
	// 退款暂存款金额
	private BigDecimal expected_return_crdt_money=BigDecimal.ZERO;
	
	public BigDecimal getExpected_return_hjycoin_money() {
		return expected_return_hjycoin_money;
	}

	public void setExpected_return_hjycoin_money(BigDecimal expected_return_hjycoin_money) {
		this.expected_return_hjycoin_money = expected_return_hjycoin_money;
	}

	public List<RetuGoodDetailChild> getDetailList() {
		return detailList;
	}

	public void setDetailList(List<RetuGoodDetailChild> detailList) {
		this.detailList = detailList;
	}

	public String getBuyer_code() {
		return buyer_code;
	}

	public void setBuyer_code(String buyer_code) {
		this.buyer_code = buyer_code;
	}

	public String getOrder_code() {
		return order_code;
	}

	public void setOrder_code(String order_code) {
		this.order_code = order_code;
	}

	public String getReturn_reason() {
		return return_reason;
	}

	public void setReturn_reason(String return_reason) {
		this.return_reason = return_reason;
	}

	public String getSeller_code() {
		return seller_code;
	}

	public void setSeller_code(String seller_code) {
		this.seller_code = seller_code;
	}

	public String getContacts() {
		return contacts;
	}

	public void setContacts(String contacts) {
		this.contacts = contacts;
	}

	public float getTransport_money() {
		return transport_money;
	}

	public void setTransport_money(float transport_money) {
		this.transport_money = transport_money;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPic_url() {
		return pic_url;
	}

	public void setPic_url(String pic_url) {
		this.pic_url = pic_url;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getReturn_code() {
		return return_code;
	}

	public void setReturn_code(String return_code) {
		this.return_code = return_code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	
	public String getSmall_seller_code() {
		return small_seller_code;
	}

	public void setSmall_seller_code(String small_seller_code) {
		this.small_seller_code = small_seller_code;
	}

	public ReturnGoods(List<RetuGoodDetailChild> detailList, String buyer_code,
			String order_code, String return_reason, String seller_code,
			String contacts, float transport_money, String mobile,
			String address, String pic_url, String status, String return_code,
			String description) {
		super();
		this.detailList = detailList;
		this.buyer_code = buyer_code;
		this.order_code = order_code;
		this.return_reason = return_reason;
		this.seller_code = seller_code;
		this.contacts = contacts;
		this.transport_money = transport_money;
		this.mobile = mobile;
		this.address = address;
		this.pic_url = pic_url;
		this.status = status;
		this.return_code = return_code;
		this.description = description;
	}

	public ReturnGoods() {
		
		super();
		// TODO Auto-generated constructor stub
		
	}

	public String getGoods_receipt() {
		return goods_receipt;
	}

	public void setGoods_receipt(String goods_receipt) {
		this.goods_receipt = goods_receipt;
	}

	public BigDecimal getExpected_return_money() {
		return expected_return_money;
	}

	public void setExpected_return_money(BigDecimal expected_return_money) {
		this.expected_return_money = expected_return_money;
	}

	public BigDecimal getExpected_return_group_money() {
		return expected_return_group_money;
	}

	public void setExpected_return_group_money(BigDecimal expected_return_group_money) {
		this.expected_return_group_money = expected_return_group_money;
	}

	public String getBuyer_mobile() {
		return buyer_mobile;
	}

	public void setBuyer_mobile(String buyer_mobile) {
		this.buyer_mobile = buyer_mobile;
	}

	public String getFlag_return_goods() {
		return flag_return_goods;
	}

	public void setFlag_return_goods(String flag_return_goods) {
		this.flag_return_goods = flag_return_goods;
	}

	public BigDecimal getExpected_return_accm_money() {
		return expected_return_accm_money;
	}

	public void setExpected_return_accm_money(BigDecimal expected_return_accm_money) {
		this.expected_return_accm_money = expected_return_accm_money;
	}

	public BigDecimal getExpected_return_ppc_money() {
		return expected_return_ppc_money;
	}

	public void setExpected_return_ppc_money(BigDecimal expected_return_ppc_money) {
		this.expected_return_ppc_money = expected_return_ppc_money;
	}

	public BigDecimal getExpected_return_crdt_money() {
		return expected_return_crdt_money;
	}

	public void setExpected_return_crdt_money(BigDecimal expected_return_crdt_money) {
		this.expected_return_crdt_money = expected_return_crdt_money;
	}
	
}

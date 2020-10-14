package com.cmall.ordercenter.model;


import com.srnpr.zapcom.baseclass.BaseClass;
/**
 * 退款类
 * @author:     hexd
 * Date:        2013年9月11日
 * project_name:ordercenter
 */
public class ReturnMoney extends BaseClass
{
	
	/**
	 * 买家编号
	 */
	private String buyer_code = "";
	/**
	 * 订单编号
	 */
	private String order_code = "";
	/**
	 * 退货单编号
	 */
	private String return_goods_code = "";
	/**
	 * 商家编码
	 */
	private String seller_code = "";
	/**
	 * 联系人
	 */
	private String contacts = "";

	/**
	 * 退款金额
	 */
	private float return_money = 0;
	/**
	 * 电话
	 */
	private String mobile = "";
	
	/**
	 * 退货状态
	 */
	private String status = "";
	
	
	/**
	 * 退款单号
	 */
	private String return_money_code = "";
	
	/**
	 * 退款原因
	 */
	private String return_reason =  "";
	
	
	
	/**
	 * 手续费
	 */
	private float poundage = 0;
	
	/**
	 *返还积分 
	 */
	private float virtual_money_deduction =0;

	public String getBuyer_code()
	{
		return buyer_code;
	}

	public void setBuyer_code(String buyer_code)
	{
		this.buyer_code = buyer_code;
	}

	public String getOrder_code()
	{
		return order_code;
	}

	public void setOrder_code(String order_code)
	{
		this.order_code = order_code;
	}

	public String getReturn_goods_code()
	{
		return return_goods_code;
	}

	public void setReturn_goods_code(String return_goods_code)
	{
		this.return_goods_code = return_goods_code;
	}

	public String getSeller_code()
	{
		return seller_code;
	}

	public void setSeller_code(String seller_code)
	{
		this.seller_code = seller_code;
	}

	public String getContacts()
	{
		return contacts;
	}

	public void setContacts(String contacts)
	{
		this.contacts = contacts;
	}

	public float getReturn_money()
	{
		return return_money;
	}

	public void setReturn_money(float return_money)
	{
		this.return_money = return_money;
	}

	public String getMobile()
	{
		return mobile;
	}

	public void setMobile(String mobile)
	{
		this.mobile = mobile;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getReturn_money_code()
	{
		return return_money_code;
	}

	public void setReturn_money_code(String return_money_code)
	{
		this.return_money_code = return_money_code;
	}

	public String getReturn_reason()
	{
		return return_reason;
	}

	public void setReturn_reason(String return_reason)
	{
		this.return_reason = return_reason;
	}

	public float getPoundage()
	{
		return poundage;
	}

	public void setPoundage(float poundage)
	{
		this.poundage = poundage;
	}

	public float getVirtual_money_deduction()
	{
		return virtual_money_deduction;
	}

	public void setVirtual_money_deduction(float virtual_money_deduction)
	{
		this.virtual_money_deduction = virtual_money_deduction;
	}

	public ReturnMoney(String buyer_code, String order_code,
			String return_goods_code, String seller_code, String contacts,
			float return_money, String mobile, String status,
			String return_money_code, String return_reason, float poundage,
			float virtual_money_deduction)
	{
		super();
		this.buyer_code = buyer_code;
		this.order_code = order_code;
		this.return_goods_code = return_goods_code;
		this.seller_code = seller_code;
		this.contacts = contacts;
		this.return_money = return_money;
		this.mobile = mobile;
		this.status = status;
		this.return_money_code = return_money_code;
		this.return_reason = return_reason;
		this.poundage = poundage;
		this.virtual_money_deduction = virtual_money_deduction;
	}

	public ReturnMoney()
	{
		super();
		// TODO Auto-generated constructor stub
	}

	

	
}

package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseclass.BaseClass;
/**
 * 退款流水
 * @author:     何旭东
 * Date:        2013年9月12日
 * project_name:ordercenter
 */
public class ReturnMoneyLog extends BaseClass
{
	/**
	 * 退货单号
	 */
	private String return_money_no  = "";
	/**
	 * 日志信息
	 */
	private String info = "";
	/**
	 * 创建时间
	 */
	private String create_time = "";
	/**
	 * 创建人
	 */
	private String create_user = "";
	/**
	 * 状态
	 */
	private String status = "";
	public String getReturn_money_no()
	{
		return return_money_no;
	}
	public void setReturn_money_no(String return_money_no)
	{
		this.return_money_no = return_money_no;
	}
	public String getInfo()
	{
		return info;
	}
	public void setInfo(String info)
	{
		this.info = info;
	}
	public String getCreate_time()
	{
		return create_time;
	}
	public void setCreate_time(String create_time)
	{
		this.create_time = create_time;
	}
	public String getCreate_user()
	{
		return create_user;
	}
	public void setCreate_user(String create_user)
	{
		this.create_user = create_user;
	}
	public String getStatus()
	{
		return status;
	}
	public void setStatus(String status)
	{
		this.status = status;
	}
	public ReturnMoneyLog(String return_money_no, String info,
			String create_time, String create_user, String status)
	{
		super();
		this.return_money_no = return_money_no;
		this.info = info;
		this.create_time = create_time;
		this.create_user = create_user;
		this.status = status;
	}
	public ReturnMoneyLog()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
}

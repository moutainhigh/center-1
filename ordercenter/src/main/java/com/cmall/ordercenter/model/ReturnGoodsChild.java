package com.cmall.ordercenter.model;

/**
 * 退货类
 * @author:     hexd
 * Date:        2013年9月11日
 * project_name:ordercenter
 */
public class ReturnGoodsChild extends ReturnGoods
{
	/**
	 * 退货单号
	 */
	private String return_code  = "";
	/**
	 * 创建时间
	 */
	private String create_time = "";

	/**
	 * 状态
	 */
	private String status = "";

	public String getReturn_code()
	{
		return return_code;
	}

	public void setReturn_code(String return_code)
	{
		this.return_code = return_code;
	}

	public String getCreate_time()
	{
		return create_time;
	}

	public void setCreate_time(String create_time)
	{
		this.create_time = create_time;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}
}

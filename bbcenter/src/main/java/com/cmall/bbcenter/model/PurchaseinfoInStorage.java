package com.cmall.bbcenter.model;

/**
 * @author hxd
 * 
 */
public class PurchaseinfoInStorage
{
	/**
	 * 商品编码
	 */
	private String product_no = "";
	/**
	 * 产品名称
	 */
	private String product_name = "";
	/**
	 * 供应商名称
	 */
	private String supplier_name = "";
	/**
	 * 采购数量
	 */
	private int purchase_count = 0;
	/**
	 * 入库数量
	 */
	private int in_storage_count = 0;
	/**
	 * 入库状态
	 */
	private String status = "";
	/**
	 * 采购单号
	 */
	private String purchase_no = "";
	
	/**
	 *序号 
	 */
	private String zidd = "";

	public String getProduct_no()
	{
		return product_no;
	}

	public void setProduct_no(String product_no)
	{
		this.product_no = product_no;
	}

	public String getProduct_name()
	{
		return product_name;
	}

	public void setProduct_name(String product_name)
	{
		this.product_name = product_name;
	}

	public String getSupplier_name()
	{
		return supplier_name;
	}

	public void setSupplier_name(String supplier_name)
	{
		this.supplier_name = supplier_name;
	}

	public int getPurchase_count()
	{
		return purchase_count;
	}

	public void setPurchase_count(int purchase_count)
	{
		this.purchase_count = purchase_count;
	}

	public int getIn_storage_count()
	{
		return in_storage_count;
	}

	public void setIn_storage_count(int in_storage_count)
	{
		this.in_storage_count = in_storage_count;
	}

	public String getStatus()
	{
		return status;
	}

	public void setStatus(String status)
	{
		this.status = status;
	}

	public String getPurchase_no()
	{
		return purchase_no;
	}

	public void setPurchase_no(String purchase_no)
	{
		this.purchase_no = purchase_no;
	}

	public String getZidd()
	{
		return zidd;
	}

	public void setZidd(String zidd)
	{
		this.zidd = zidd;
	}

	public PurchaseinfoInStorage(String product_no, String product_name,
			String supplier_name, int purchase_count, int in_storage_count,
			String status, String purchase_no, String zidd)
	{
		super();
		this.product_no = product_no;
		this.product_name = product_name;
		this.supplier_name = supplier_name;
		this.purchase_count = purchase_count;
		this.in_storage_count = in_storage_count;
		this.status = status;
		this.purchase_no = purchase_no;
		this.zidd = zidd;
	}

	public PurchaseinfoInStorage()
	{
		super();
		// TODO Auto-generated constructor stub
	}
	
}

package com.cmall.usercenter.service.sellerinfo;


public class SellerInfo 
{
	/**
	 * 卖家名称
	 */
	private String seller_name = "";
	/**
	 * 商家描述
	 */
	private String seller_description= "";
	/**
	 * 产品品牌编码
	 */
	private String brandCode= "";
	/**
	 * 产品分类编码
	 */
	private String  categoryCode= "";
	/**
	 * 公司名称
	 */
	private String company_name = "";	
	/**
	 * URL
	 */
	private String seller_url = "";
	/**
	 * 商家状态
	 */
	private String seller_status = "";
	/**
	 * 用户名称
	 */
	private String user_name ="" ;
	/**
	 * 用户密码
	 */
	private String user_password = "";
	
	

    /**
     * 所在地
     */
    private String sellerArea  = ""  ;
    /**
     * 联系电话
     */
    private String sellerTelephone  = ""  ;
    /**
     * 退货地址
     */
    private String sellerReturnAddress  = ""  ;
    /**
     * 退货邮编
     */
    private String sellerReturnPostcode  = ""  ;
    /**
     * 退货联系人地址
     */
    private String sellerReturnContact  = ""  ;
    /**
     * 退货联系人电话
     */
    private String sellerReturnTelephone  = ""  ;
    /**
     * 公司名称
     */
    private String sellerCompanyName  = ""  ;
	/**
	 * 新增标识： 空值的时间走新增流程，否则走更新流程
	 */
    private String addFlag = "";
    /**
     * php记录ID
     */
    private String dataId="";
    /**
     * 编辑人员ID
     */
    private String editId = "";
	public String getSeller_name()
	{
		return seller_name;
	}
	public void setSeller_name(String seller_name)
	{
		this.seller_name = seller_name;
	}
	public String getSeller_description()
	{
		return seller_description;
	}
	public void setSeller_description(String seller_description)
	{
		this.seller_description = seller_description;
	}
	public String getBrandCode()
	{
		return brandCode;
	}
	public void setBrandCode(String brandCode)
	{
		this.brandCode = brandCode;
	}
	public String getCategoryCode()
	{
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode)
	{
		this.categoryCode = categoryCode;
	}
	public String getCompany_name()
	{
		return company_name;
	}
	public void setCompany_name(String company_name)
	{
		this.company_name = company_name;
	}
	public String getSeller_url()
	{
		return seller_url;
	}
	public void setSeller_url(String seller_url)
	{
		this.seller_url = seller_url;
	}
	public String getSeller_status()
	{
		return seller_status;
	}
	public void setSeller_status(String seller_status)
	{
		this.seller_status = seller_status;
	}
	public String getUser_name()
	{
		return user_name;
	}
	public void setUser_name(String user_name)
	{
		this.user_name = user_name;
	}
	public String getUser_password()
	{
		return user_password;
	}
	public void setUser_password(String user_password)
	{
		this.user_password = user_password;
	}
	public String getSellerArea()
	{
		return sellerArea;
	}
	public void setSellerArea(String sellerArea)
	{
		this.sellerArea = sellerArea;
	}
	public String getSellerTelephone()
	{
		return sellerTelephone;
	}
	public void setSellerTelephone(String sellerTelephone)
	{
		this.sellerTelephone = sellerTelephone;
	}
	public String getSellerReturnAddress()
	{
		return sellerReturnAddress;
	}
	public void setSellerReturnAddress(String sellerReturnAddress)
	{
		this.sellerReturnAddress = sellerReturnAddress;
	}
	public String getSellerReturnPostcode()
	{
		return sellerReturnPostcode;
	}
	public void setSellerReturnPostcode(String sellerReturnPostcode)
	{
		this.sellerReturnPostcode = sellerReturnPostcode;
	}
	public String getSellerReturnContact()
	{
		return sellerReturnContact;
	}
	public void setSellerReturnContact(String sellerReturnContact)
	{
		this.sellerReturnContact = sellerReturnContact;
	}
	public String getSellerReturnTelephone()
	{
		return sellerReturnTelephone;
	}
	public void setSellerReturnTelephone(String sellerReturnTelephone)
	{
		this.sellerReturnTelephone = sellerReturnTelephone;
	}
	public String getSellerCompanyName()
	{
		return sellerCompanyName;
	}
	public void setSellerCompanyName(String sellerCompanyName)
	{
		this.sellerCompanyName = sellerCompanyName;
	}
	public String getAddFlag()
	{
		return addFlag;
	}
	public void setAddFlag(String addFlag)
	{
		this.addFlag = addFlag;
	}
	public String getDataId()
	{
		return dataId;
	}
	public void setDataId(String dataId)
	{
		this.dataId = dataId;
	}
	public String getEditId()
	{
		return editId;
	}
	public void setEditId(String editId)
	{
		this.editId = editId;
	}
	public SellerInfo(String seller_name, String seller_description,
			String brandCode, String categoryCode, String company_name,
			String seller_url, String seller_status, String user_name,
			String user_password, String sellerArea, String sellerTelephone,
			String sellerReturnAddress, String sellerReturnPostcode,
			String sellerReturnContact, String sellerReturnTelephone,
			String sellerCompanyName, String addFlag, String dataId,
			String editId)
	{
		super();
		this.seller_name = seller_name;
		this.seller_description = seller_description;
		this.brandCode = brandCode;
		this.categoryCode = categoryCode;
		this.company_name = company_name;
		this.seller_url = seller_url;
		this.seller_status = seller_status;
		this.user_name = user_name;
		this.user_password = user_password;
		this.sellerArea = sellerArea;
		this.sellerTelephone = sellerTelephone;
		this.sellerReturnAddress = sellerReturnAddress;
		this.sellerReturnPostcode = sellerReturnPostcode;
		this.sellerReturnContact = sellerReturnContact;
		this.sellerReturnTelephone = sellerReturnTelephone;
		this.sellerCompanyName = sellerCompanyName;
		this.addFlag = addFlag;
		this.dataId = dataId;
		this.editId = editId;
	}
	public SellerInfo()
	{
		super();
		// TODO Auto-generated constructor stub
	}
    
}

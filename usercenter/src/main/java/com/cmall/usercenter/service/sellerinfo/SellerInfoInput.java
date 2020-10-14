package com.cmall.usercenter.service.sellerinfo;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;
/**
 * ClassName: SellerInfoInput <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * date: 2013年10月15日 上午11:37:27 <br/>
 * @author hxd
 * @version 
 * @since JDK 1.6
 */

public class SellerInfoInput extends RootInput
{
	/**
	 * 卖家名称
	 */
	@ZapcomApi(value="卖家名称")
	private String seller_name = "";
	/**
	 * 商家描述
	 */
	@ZapcomApi(value="商家描述")
	private String seller_description= "";
	/**
	 * 产品品牌编码
	 */
	@ZapcomApi(value="产品品牌编码")
	private String brandCode= "";
	/**
	 * 产品分类编码
	 */
	@ZapcomApi(value="产品分类编码")
	private String  categoryCode= "";
	/**
	 * 公司名称
	 */
	@ZapcomApi(value="公司名称")
	private String company_name = "";	
	/**
	 * URL
	 */
	@ZapcomApi(value="URL")
	private String seller_url = "";
	/**
	 * 用户名称
	 */
	@ZapcomApi(value="用户名称")
	private String user_name ="" ;
	/**
	 * 用户密码
	 */
	@ZapcomApi(value="用户密码")
	private String user_password = "";
	
	

    /**
     * 所在地
     */
	@ZapcomApi(value="所在地")
    private String sellerArea  = ""  ;
    /**
     * 联系电话
     */
	@ZapcomApi(value="联系电话")
    private String sellerTelephone  = ""  ;
    /**
     * 退货地址
     */
	@ZapcomApi(value="退货地址")
    private String sellerReturnAddress  = ""  ;
    /**
     * 退货邮编
     */
	@ZapcomApi(value="退货邮编")
    private String sellerReturnPostcode  = ""  ;
    /**
     * 退货联系人地址
     */
	@ZapcomApi(value="退货联系人地址")
    private String sellerReturnContact  = ""  ;
    /**
     * 退货联系人电话
     */
	@ZapcomApi(value="退货联系人电话")
    private String sellerReturnTelephone  = ""  ;
    /**
     * 公司名称
     */
	@ZapcomApi(value="公司名称")
    private String sellerCompanyName  = ""  ;
	/**
	 * 新增标识： 空值的时间走新增流程，否则走更新流程
	 */
	@ZapcomApi(value="新增标识",remark="空值的时间走新增流程，否则走更新流程")
    private String addFlag = "";
    /**
     * php记录ID
     */
	@ZapcomApi(value="php记录ID")
    private String dataId="";
    /**
     * 编辑人员ID
     */
	@ZapcomApi(value="编辑人员ID")
    private String editId = "";
    
    
    
	
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
	public SellerInfoInput(String seller_name, String seller_description,
			String brandCode, String categoryCode, String company_name,
			String seller_url, String user_name, String user_password,
			String sellerArea, String sellerTelephone,
			String sellerReturnAddress, String sellerReturnPostcode,
			String sellerReturnContact, String sellerReturnTelephone,
			String sellerCompanyName, String addFlag) {
		super();
		this.seller_name = seller_name;
		this.seller_description = seller_description;
		this.brandCode = brandCode;
		this.categoryCode = categoryCode;
		this.company_name = company_name;
		this.seller_url = seller_url;
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
	}
	public String getAddFlag() {
		return addFlag;
	}
	public void setAddFlag(String addFlag) {
		this.addFlag = addFlag;
	}
	public String getSellerArea() {
		return sellerArea;
	}
	public void setSellerArea(String sellerArea) {
		this.sellerArea = sellerArea;
	}
	public String getSellerTelephone() {
		return sellerTelephone;
	}
	public void setSellerTelephone(String sellerTelephone) {
		this.sellerTelephone = sellerTelephone;
	}
	public String getSellerReturnAddress() {
		return sellerReturnAddress;
	}
	public void setSellerReturnAddress(String sellerReturnAddress) {
		this.sellerReturnAddress = sellerReturnAddress;
	}
	public String getSellerReturnPostcode() {
		return sellerReturnPostcode;
	}
	public void setSellerReturnPostcode(String sellerReturnPostcode) {
		this.sellerReturnPostcode = sellerReturnPostcode;
	}
	public String getSellerReturnContact() {
		return sellerReturnContact;
	}
	public void setSellerReturnContact(String sellerReturnContact) {
		this.sellerReturnContact = sellerReturnContact;
	}
	public String getSellerReturnTelephone() {
		return sellerReturnTelephone;
	}
	public void setSellerReturnTelephone(String sellerReturnTelephone) {
		this.sellerReturnTelephone = sellerReturnTelephone;
	}
	public String getSellerCompanyName() {
		return sellerCompanyName;
	}
	public void setSellerCompanyName(String sellerCompanyName) {
		this.sellerCompanyName = sellerCompanyName;
	}
	public String getSeller_name() {
		return seller_name;
	}
	public void setSeller_name(String seller_name) {
		this.seller_name = seller_name;
	}
	public String getSeller_description() {
		return seller_description;
	}
	public void setSeller_description(String seller_description) {
		this.seller_description = seller_description;
	}
	public String getBrandCode() {
		return brandCode;
	}
	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}
	public String getCategoryCode() {
		return categoryCode;
	}
	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}
	public String getCompany_name() {
		return company_name;
	}
	public void setCompany_name(String company_name) {
		this.company_name = company_name;
	}
	public String getSeller_url() {
		return seller_url;
	}
	public void setSeller_url(String seller_url) {
		this.seller_url = seller_url;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getUser_password() {
		return user_password;
	}
	public void setUser_password(String user_password) {
		this.user_password = user_password;
	}
	public SellerInfoInput() {
		
		super();
		// TODO Auto-generated constructor stub
		
	}
	public SellerInfoInput(String seller_name, String seller_description,
			String brandCode, String categoryCode, String company_name,
			String seller_url, String user_name, String user_password) {
		super();
		this.seller_name = seller_name;
		this.seller_description = seller_description;
		this.brandCode = brandCode;
		this.categoryCode = categoryCode;
		this.company_name = company_name;
		this.seller_url = seller_url;
		this.user_name = user_name;
		this.user_password = user_password;
	}
	public SellerInfoInput(String seller_name, String seller_description,
			String brandCode, String categoryCode, String company_name,
			String seller_url, String user_name, String user_password,
			String sellerArea, String sellerTelephone,
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
	
	
	
	
}

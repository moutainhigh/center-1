package com.cmall.usercenter.model;
import com.srnpr.zapcom.baseclass.BaseClass;

/**
 * ClassName: UcSellerInfo <br/>
 * Function: TODO ADD FUNCTION. <br/>
 * date: 2013年9月23日 下午2:56:34 <br/>
 * @author hxd
 * @version 
 * @since JDK 1.6
 */
public class UcSellerInfo extends BaseClass
{
	/**
	 * 卖家编号
	 */
	private String seller_code ="" ;
	/**
	 * 卖家名称
	 */
	private String seller_name = "";
	/**
	 * 商家描述
	 */
	private String seller_descrption = "";
	/**
	 * 商家状态
	 * 4497172300040002	初审通过
	 * 4497172300040003	不通过
	 * 4497172300040001	商家待审核
	 * 4497172300040004	终审通过
	 */
	private String seller_status = "";
	/**
	 * 产品品牌编码
	 */
	private String brandCode = "";
	/**
	 * 产品分类编码
	 */
	private String  categoryCode = "";
	/**
	 * 商家账号
	 */
	private String seller_account = "";
	/**
	 * URL
	 */
	private String seller_url;
	
	/**
	 * 卖家logo
	 */
	private String sellerPic="";
	
	
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
     * 退货联系人
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
	 * 商家二级域名
	 */
	private String second_level_domain;
	
	
	/**
	 * 商家二维码图片链接
	 */
	private String qrcode_link;
	
	
    
	
	
	
	public String getSecond_level_domain() {
		return second_level_domain;
	}
	public void setSecond_level_domain(String second_level_domain) {
		this.second_level_domain = second_level_domain;
	}
	public String getQrcode_link() {
		return qrcode_link;
	}
	public void setQrcode_link(String qrcode_link) {
		this.qrcode_link = qrcode_link;
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
	public String getSellerPic() {
		return sellerPic;
	}
	public void setSellerPic(String sellerPic) {
		this.sellerPic = sellerPic;
	}
	
	public String getSeller_code()
	{
		return seller_code;
	}
	public void setSeller_code(String seller_code)
	{
		this.seller_code = seller_code;
	}
	public String getSeller_name()
	{
		return seller_name;
	}
	public void setSeller_name(String seller_name)
	{
		this.seller_name = seller_name;
	}
	public String getSeller_descrption()
	{
		return seller_descrption;
	}
	public void setSeller_descrption(String seller_descrption)
	{
		this.seller_descrption = seller_descrption;
	}
	public String getSeller_status()
	{
		return seller_status;
	}
	public void setSeller_status(String seller_status)
	{
		this.seller_status = seller_status;
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
	public String getSeller_account()
	{
		return seller_account;
	}
	public void setSeller_account(String seller_account)
	{
		this.seller_account = seller_account;
	}
	public String getSeller_url()
	{
		return seller_url;
	}
	public void setSeller_url(String seller_url)
	{
		this.seller_url = seller_url;
	}

	public UcSellerInfo(String seller_code, String seller_name,
			String seller_descrption, String seller_status, String brandCode,
			String categoryCode, String seller_account, String seller_url,
			String sellerPic, String sellerArea, String sellerTelephone,
			String sellerReturnAddress, String sellerReturnPostcode,
			String sellerReturnContact, String sellerReturnTelephone,
			String sellerCompanyName) {
		super();
		this.seller_code = seller_code;
		this.seller_name = seller_name;
		this.seller_descrption = seller_descrption;
		this.seller_status = seller_status;
		this.brandCode = brandCode;
		this.categoryCode = categoryCode;
		this.seller_account = seller_account;
		this.seller_url = seller_url;
		this.sellerPic = sellerPic;
		this.sellerArea = sellerArea;
		this.sellerTelephone = sellerTelephone;
		this.sellerReturnAddress = sellerReturnAddress;
		this.sellerReturnPostcode = sellerReturnPostcode;
		this.sellerReturnContact = sellerReturnContact;
		this.sellerReturnTelephone = sellerReturnTelephone;
		this.sellerCompanyName = sellerCompanyName;
	}
	public UcSellerInfo()
	{
		super();
		// TODO Auto-generated constructor stub
	}
}

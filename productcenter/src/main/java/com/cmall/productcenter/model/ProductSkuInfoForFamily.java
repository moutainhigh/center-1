package com.cmall.productcenter.model;

import java.math.BigDecimal;


/**   
*    
* 项目名称：productcenter   
* 类名称：ProductSkuInfoForFamily   
* 类描述：   
* 创建人：李国杰  
* 修改备注：   
* @version    
*    
*/
public class ProductSkuInfoForFamily {
	

	
	private String uid="";
	
	/**
	 * 产品编号
	 */
	private String skuCode ="";
	/**
	 * 商品编号
	 */
	private String productCode="";
	/**
	 * 销售价
	 */
	private BigDecimal sellPrice=new BigDecimal(0.00);
	/**
	 * 市场价
	 */
	private BigDecimal marketPrice = new BigDecimal(0.00);
	/**
	 * 库存数
	 */
	private int stockNum= 0;
	
	
	/**
	 * skuKey
	 */
	private String skuKey = "";
	
	/**
	 * skuValue
	 */
	private String skuValue = "";
	
	
	 /**
     * 商品的Sku的图片信息
     */
    private String skuPicUrl = "";
	
    
    /**
     * 商品的sku信息
     */
    private String skuName="";
    
    /**
     * 商家编码
     */
    private String sellProductcode="";
    
    
    /**
     * sku安全库存
     */
    private int securityStockNum = 0;
    
    
    /**
     * 卖家编号
     */
    private String sellerCode = "";
    
    
    /**
     * 广告语
     */
    private String skuAdv="";
    
    
    /**
     * sku二维码
     */
    private String qrcodeLink = "";
    
    
    /**
     * 积分抵扣 单位 个 需要 10%折 钱。
     */
    private BigDecimal virtualMoneyDeduction= new BigDecimal(0.00);
    /**
     * 销售数量
     */
    private int sellCount = 0;
    
	/**
	 * 库存总数
	 */
	private int stockNumSum= 0;
	
	/**
	 * 活动
	 */
	private FlashsalesSkuInfo fkiObj = new FlashsalesSkuInfo();

	/**
	 * 是否可卖 Y可卖 N不可卖
	 */
	private String saleYn = "";
	
	/**
	 * 起订数量
	 */
   private Integer miniOrder = 1;
	
	public int getStockNumSum() {
		return stockNumSum;
	}

	public void setStockNumSum(int stockNumSum) {
		this.stockNumSum = stockNumSum;
	}

	public FlashsalesSkuInfo getFkiObj() {
		return fkiObj;
	}

	public void setFkiObj(FlashsalesSkuInfo fkiObj) {
		this.fkiObj = fkiObj;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getSkuCode() {
		return skuCode;
	}

	public void setSkuCode(String skuCode) {
		this.skuCode = skuCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	public int getStockNum() {
		return stockNum;
	}

	public void setStockNum(int stockNum) {
		this.stockNum = stockNum;
	}

	public String getSkuKey() {
		return skuKey;
	}

	public void setSkuKey(String skuKey) {
		this.skuKey = skuKey;
	}

	public String getSkuValue() {
		return skuValue;
	}

	public void setSkuValue(String skuValue) {
		this.skuValue = skuValue;
	}

	public String getSkuPicUrl() {
		return skuPicUrl;
	}

	public void setSkuPicUrl(String skuPicUrl) {
		this.skuPicUrl = skuPicUrl;
	}

	public String getSkuName() {
		return skuName;
	}

	public void setSkuName(String skuName) {
		this.skuName = skuName;
	}

	public String getSellProductcode() {
		return sellProductcode;
	}

	public void setSellProductcode(String sellProductcode) {
		this.sellProductcode = sellProductcode;
	}

	public int getSecurityStockNum() {
		return securityStockNum;
	}

	public void setSecurityStockNum(int securityStockNum) {
		this.securityStockNum = securityStockNum;
	}

	public String getSellerCode() {
		return sellerCode;
	}

	public void setSellerCode(String sellerCode) {
		this.sellerCode = sellerCode;
	}

	public String getSkuAdv() {
		return skuAdv;
	}

	public void setSkuAdv(String skuAdv) {
		this.skuAdv = skuAdv;
	}

	public String getQrcodeLink() {
		return qrcodeLink;
	}

	public void setQrcodeLink(String qrcodeLink) {
		this.qrcodeLink = qrcodeLink;
	}

	public BigDecimal getVirtualMoneyDeduction() {
		return virtualMoneyDeduction;
	}

	public void setVirtualMoneyDeduction(BigDecimal virtualMoneyDeduction) {
		this.virtualMoneyDeduction = virtualMoneyDeduction;
	}

	public int getSellCount() {
		return sellCount;
	}

	public void setSellCount(int sellCount) {
		this.sellCount = sellCount;
	}

	public String getSaleYn() {
		return saleYn;
	}

	public void setSaleYn(String saleYn) {
		this.saleYn = saleYn;
	}

	public Integer getMiniOrder() {
		return miniOrder;
	}

	public void setMiniOrder(Integer miniOrder) {
		this.miniOrder = miniOrder;
	}
	
}

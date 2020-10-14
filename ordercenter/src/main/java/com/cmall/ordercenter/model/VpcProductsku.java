package com.cmall.ordercenter.model;

import java.math.BigDecimal;


/**   
*    
* 项目名称：productcenter   
* 类名称：VpcProductsku   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-25 上午9:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 上午9:41:22   
* 修改备注：   
* @version    
*    
*/
public class VpcProductsku  {
    
    /**
     * 
     */
    private Integer zid   = 0 ;
    /**
     * 
     */
    private String uid  = ""  ;
    /**
     * 商品编码
     */
    private String productCode  = ""  ;
    /**
     * 商品名称
     */
    private String produtName  = ""  ;
    /**
     * 产品编号
     */
    private String skuCode  = ""  ;
    /**
     * 产品名称
     */
    private String skuName  = ""  ;
    /**
     * 销售价
     */
    private BigDecimal sellPrice = new BigDecimal(0.00)   ;
    /**
     * 库存数
     */
    private int stockNum   = 0 ;
    
    
    /**
     * 活动优惠价
     */
    private BigDecimal activitySellPrice =new BigDecimal(0.00);
    
    
    /**
     * 活动库存
     */
    private int activityStockNum =0;
    
    

	public BigDecimal getActivitySellPrice() {
		return activitySellPrice;
	}

	public void setActivitySellPrice(BigDecimal activitySellPrice) {
		this.activitySellPrice = activitySellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public int getActivityStockNum() {
		return activityStockNum;
	}

	public void setActivityStockNum(int activityStockNum) {
		this.activityStockNum = activityStockNum;
	}

	public void setZid(Integer zid) {
        this.zid = zid;
    }
    
    public Integer getZid() {
        return this.zid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }
    
    public String getUid() {
        return this.uid;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getProductCode() {
        return this.productCode;
    }
    public void setProdutName(String produtName) {
        this.produtName = produtName;
    }
    
    public String getProdutName() {
        return this.produtName;
    }
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
    
    public String getSkuCode() {
        return this.skuCode;
    }
    public void setSkuName(String skuName) {
        this.skuName = skuName;
    }
    
    public String getSkuName() {
        return this.skuName;
    }

    public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setStockNum(int stockNum) {
        this.stockNum = stockNum;
    }
    
    public int getStockNum() {
        return this.stockNum;
    }
}


package com.cmall.ordercenter.model;

import java.math.BigDecimal;


/**   
*    
* 项目名称：systemcenter   
* 类名称：OcActivityProductRel   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-25 上午9:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 上午9:41:22   
* 修改备注：   
* @version    
*    
*/
public class OcActivityProductRel  {
    
  
    /**
     * 活动编号
     */
    private String activityCode  = ""  ;
    /**
     * sku编号
     */
    private String skuCode  = ""  ;
    /**
     * 直降类型时有用。
     */
    private BigDecimal sellPrice = new BigDecimal(0.00)   ;
    /**
     * 活动的库存
     */
    private Integer sellStock   = 0 ;

   
    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }
    
    public String getActivityCode() {
        return this.activityCode;
    }
    public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
    
    public String getSkuCode() {
        return this.skuCode;
    }

    public BigDecimal getSellPrice() {
		return sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = sellPrice;
	}

	public void setSellStock(Integer sellStock) {
        this.sellStock = sellStock;
    }
    
    public Integer getSellStock() {
        return this.sellStock;
    }
}


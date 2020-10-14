package com.cmall.ordercenter.model;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;


/**   
*    
* 项目名称：ordercenter  
* 类名称：OcOrderActivityForCC
* 类描述： 
* 创建人：zhaoxq
* 修改备注：   
* @version
*    
*/
public class OcOrderActivityForCC {
    
    /**
     * 订单编号
     */
	@ZapcomApi(value="订单编号")
    private String orderCode  = "";
	
    /**
     * 商品编号
     */
	@ZapcomApi(value="商品编号")
    private String productCode  = "";
	
    /**
     * sku商品编号
     */
	@ZapcomApi(value="sku商品编号")
    private String skuCode = "";

    /**
     * 优惠金额
     */
	@ZapcomApi(value="优惠金额")
    private BigDecimal preferentialMoney = new BigDecimal(0.00);
	
    /**
     * 活动编号
     */
	@ZapcomApi(value="活动编号")
    private String activityCode  = "";
	
    /**
     * 活动类型
     */
	@ZapcomApi(value="活动类型", remark="449715400001:限时特价<br/>"
								   +"449715400002:满减<br/>"
								   +"449715400003:免运费<br/>"
								   +"449715400004:闪购<br/>"
								   +"449715400005:商品试用<br/>"
								   +"449715400006:订单活动（立减，满减）<br/>"
								   +"449715400007:优惠券")
    private String activityType  = "";
    
	public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
    
    public String getOrderCode() {
        return this.orderCode;
    }
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }
    
    public String getProductCode() {
        return this.productCode;
    }
    
	public void setSkuCode(String skuCode) {
        this.skuCode = skuCode;
    }
    
    public String getSkuCode() {
        return this.skuCode;
    }
    public void setPreferentialMoney(BigDecimal preferentialMoney) {
        this.preferentialMoney = preferentialMoney;
    }
    
    public BigDecimal getPreferentialMoney() {
        return this.preferentialMoney;
    }
    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }
    
    public String getActivityCode() {
        return this.activityCode;
    }
    public void setActivityType(String activityType) {
        this.activityType = activityType;
    }
    
    public String getActivityType() {
        return this.activityType;
    }   
}


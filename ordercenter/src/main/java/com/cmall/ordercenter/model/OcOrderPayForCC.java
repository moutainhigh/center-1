package com.cmall.ordercenter.model;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**   
*    
* 项目名称：ordercenter  
* 类名称：OcOrderPayForCC   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-10-29 上午9:30:15   
* 修改人：yanzj
* 修改时间：2013-10-29 上午9:30:15   
* 修改备注：   
* @version    
*    
*/
public class OcOrderPayForCC{
    
    /**
     * 订单编号
     */
	@ZapcomApi(value="订单编号")
    private String orderCode  = "";
	
    /**
     * 流水编号
     */
	@ZapcomApi(value="流水编号")
    private String paySequenceid  = "";
	
    /**
     * 已支付金额
     */
	@ZapcomApi(value="已支付金额")
    private BigDecimal payedMoney = new BigDecimal(0.00);
    
    /**
     * 创建时间 
     */
	@ZapcomApi(value="创建时间 ")
    private String createTime= "";
	
    /**
     * 支付类型
     * 449746280001:礼品卡    449746280002:优惠券    
     * 449746280003:支付宝支付   
     * 449746280004:快钱支付 449746280005:微信支付 
     * 449746280006:储值金 (家有汇)449746280007:
     * 暂存款(家有汇) 449746280008:积分(家有汇)
     */
	@ZapcomApi(value="支付类型")
    private String payType  = "";
    
	/**
     * 支付备注
     */
	@ZapcomApi(value="支付备注")
    private String payRemark  = "";
  
    public void setOrderCode(String orderCode) {
        this.orderCode = orderCode;
    }
    
    public String getOrderCode() {
        return this.orderCode;
    }
    public void setPaySequenceid(String paySequenceid) {
        this.paySequenceid = paySequenceid;
    }
    
    public String getPaySequenceid() {
        return this.paySequenceid;
    }
    public void setPayedMoney(BigDecimal payedMoney) {
        this.payedMoney = payedMoney;
    }
    
    public BigDecimal getPayedMoney() {
        return this.payedMoney;
    }
   
    public void setPayType(String payType) {
        this.payType = payType;
    }
    
    public String getPayType() {
        return this.payType;
    }
    public void setPayRemark(String payRemark) {
        this.payRemark = payRemark;
    }
    
    public String getPayRemark() {
        return this.payRemark;
    }

	public String getCreate_time() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}
}


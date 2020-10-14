package com.cmall.ordercenter.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cmall.productcenter.model.PcProductInfoForI;


/**   
*    
* 项目名称：systemcenter   
* 类名称：OcActivity   
* 类描述：   
* 创建人：yanzj  
* 创建时间：2013-9-25 上午9:41:22   
* 修改人：yanzj
* 修改时间：2013-9-10 上午9:41:22   
* 修改备注：   
* @version    
*    
*/
public class OcActivity  {
	
	/**
	 * 满减对应的商品列表
	 */
	private List<PcProductInfoForI> mjProductList = new ArrayList<PcProductInfoForI>();
    
	/**
	 * 限时限量对应的商品List
	 */
	private List<OcActivityProductRel> productList = new ArrayList<OcActivityProductRel>();
	
	/**
	 * 满减对应的商家分类
	 */
	private List<OcActivitySellercategoryRel> categoryList =  new ArrayList<OcActivitySellercategoryRel>();;
	    
	
	
	
	
    public List<PcProductInfoForI> getMjProductList() {
		return mjProductList;
	}

	public void setMjProductList(List<PcProductInfoForI> mjProductList) {
		this.mjProductList = mjProductList;
	}

	public List<OcActivityProductRel> getProductList() {
		return productList;
	}

	public void setProductList(List<OcActivityProductRel> productList) {
		this.productList = productList;
	}

	public List<OcActivitySellercategoryRel> getCategoryList() {
		return categoryList;
	}

	public void setCategoryList(List<OcActivitySellercategoryRel> categoryList) {
		this.categoryList = categoryList;
	}
	
	/**
     * 活动编号
     */
    private String activityCode  = ""  ;
    
    
    /**
     * 活动名称
     */
    private String activityName="";
    
    /**
     * 活动类型
     */
    private String activityType  = ""  ;
    /**
     * 店铺编号
     */
    private String sellerCode  = ""  ;
 
    /**
     * 商品的单次最大购买数量
     */
    private Integer perMaxcount   = 0 ;
    /**
     * 是否单订单购买
     */
    private Integer perOrderBuy   = 0 ;
    
    /**
     *  限时抢购的价格类型 
     *  4497462600001	直降类型
	 *	4497462600002	直减类型
	 *	4497462600003	折扣百分比
	 *
     */
    private String activityPriceType   = "" ;
 
    /**
     * 直减多少钱
     */
    private BigDecimal skuSubprice = new BigDecimal(0.00)   ;
    
    
    /**
     * 折扣百分比
     */
    private BigDecimal skuPricepercent =new BigDecimal(0.00);
    
    /**
     * 开始时间
     */
    private String beginTime  = ""  ;
    /**
     * 结束时间
     */
    private String endTime  = ""  ;
    /**
     * 是否启用状态 
     */
    private Integer flag   = 0 ;
    /**
     * 订单金额下线
     */
    private BigDecimal orderMinMoney = new BigDecimal(0.00)   ;
    /**
     * 满减金额
     */
    private BigDecimal fullFreeMoney = new BigDecimal(0.00)   ;
    /**
     * 运费金额，如果为0 ，则为全免，否则为运费金额
     */
    private BigDecimal afficationMoney = new BigDecimal(0.00)   ;

    
    /**
     * 剩余时间
     */
    private long remainingTime = 0;

    
    
    public long getRemainingTime() {
		return remainingTime;
	}

	public void setRemainingTime(long remainingTime) {
		this.remainingTime = remainingTime;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
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
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }
    
    public String getSellerCode() {
        return this.sellerCode;
    }
  
    public void setPerMaxcount(Integer perMaxcount) {
        this.perMaxcount = perMaxcount;
    }
    
    public Integer getPerMaxcount() {
        return this.perMaxcount;
    }
    public void setPerOrderBuy(Integer perOrderBuy) {
        this.perOrderBuy = perOrderBuy;
    }
    
    public Integer getPerOrderBuy() {
        return this.perOrderBuy;
    }
    public void setActivityPriceType(String activityPriceType) {
        this.activityPriceType = activityPriceType;
    }
    
    public String getActivityPriceType() {
        return this.activityPriceType;
    }
  
    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }
    
    public String getBeginTime() {
        return this.beginTime;
    }
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
    
    public String getEndTime() {
        return this.endTime;
    }
    public void setFlag(Integer flag) {
        this.flag = flag;
    }
    
    public Integer getFlag() {
        return this.flag;
    }

	public BigDecimal getSkuSubprice() {
		return skuSubprice;
	}

	public void setSkuSubprice(BigDecimal skuSubprice) {
		this.skuSubprice = skuSubprice;
	}

	public BigDecimal getSkuPricepercent() {
		return skuPricepercent;
	}

	public void setSkuPricepercent(BigDecimal skuPricepercent) {
		this.skuPricepercent = skuPricepercent;
	}

	public BigDecimal getOrderMinMoney() {
		return orderMinMoney;
	}

	public void setOrderMinMoney(BigDecimal orderMinMoney) {
		this.orderMinMoney = orderMinMoney;
	}

	public BigDecimal getFullFreeMoney() {
		return fullFreeMoney;
	}

	public void setFullFreeMoney(BigDecimal fullFreeMoney) {
		this.fullFreeMoney = fullFreeMoney;
	}

	public BigDecimal getAfficationMoney() {
		return afficationMoney;
	}

	public void setAfficationMoney(BigDecimal afficationMoney) {
		this.afficationMoney = afficationMoney;
	}
    
}


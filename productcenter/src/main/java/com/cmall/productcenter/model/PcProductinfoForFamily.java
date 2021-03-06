package com.cmall.productcenter.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


/**   
*    
* 项目名称：productcenter   
* 类名称：PcProductinfoForFamily   
* 类描述：   
* 创建人：ligj
* 修改备注：   
* @version    
*    
*/
public class PcProductinfoForFamily  {
    
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
    private String productName  = ""  ;
    /**
     *商品简称 
     */
    private String productShortname = "" ;
    /**
     * 卖家编号
     */
    private String sellerCode  = ""  ;
    /**
     * 品牌编号
     */
    private String brandCode  = ""  ;
    /**
     * 商品重量
     */
    private BigDecimal productWeight = new BigDecimal(0.00)   ;
    /**
     * 上下架状态
     */
    private Integer flagSale   = 0 ;
    /**
     * 
     */
    private String createTime  = ""  ;
    /**
     * 
     */
    private String updateTime  = ""  ;
    
	/**
	 * 最小销售价
	 */
	private BigDecimal minSellPrice= new BigDecimal(0);
	/**
	 * 最大销售价
	 */
	private BigDecimal maxSellPrice= new BigDecimal(0);
	/**
	 * 市场价
	 */
	private BigDecimal marketPrice = new BigDecimal(0);
    /**
     *成本价 
     */
	private BigDecimal costPrice = new BigDecimal(0.00);
	
	/**
	 * 主图的Url
	 */
	private String mainPicUrl = "";
	/**
	 *供应商名称 
	 */
	private String supplierName = "" ;
	/**
	 *商品视频链接
	 */
	private String videoUrl = "" ;
	/**
	 * 标签值 ，用逗号分开 
	 */
	private String labels = "";
	
	
	/**
	 * 是否货到付款 0 否 1 是
	 */
	private int flagPayway = 0;
	
	
	
	/**
	 * 长 宽 高 ，用逗号隔开
	 */
	private String productVolumeItem = "";
	
    
    /**
     * 商品分类信息
     */
    private PcCategoryinfo category = new PcCategoryinfo();;
    
    /**
     * 商品描述信息
     */
    private PcProductdescription description = new PcProductdescription();
    
    
    
    /**
     * 商品图片信息
     */
    private List<PcProductpic> pcPicList = new ArrayList<PcProductpic>();
    
    
    /**
     * 店铺商品分类关系
     */
    private List<UcSellercategoryProductRelation> usprList = new ArrayList<UcSellercategoryProductRelation>();
    
    /**
     * 商品关联属性信息
     */
    private List<PcProductproperty> pcProductpropertyList = new ArrayList<PcProductproperty>();
    
    /**
     * 商品的Sku列表的属性信息
     */
    private List<ProductSkuInfoForFamily> productSkuInfoList  = new ArrayList<ProductSkuInfoForFamily>();
    
    /**
     * 商品的草稿箱及流程审批信息
     */
    private PcProductflow pcProdcutflow = null;
    
    
    /**
     * 旧的商品编号
     */
    private String productCodeOld = "";
    
    
    /**
     * 商品体积
     */
    private BigDecimal productVolume = new BigDecimal(0.00);
    
    /**
     * 运费模板
     */
    private String transportTemplate ="";
    
    /**
     * 商家编码
     */
    private String sellProductcode ="";
    
    
    /**
     * 销售范围限制
     */
    private String saleScopeDid = "";
    
    
    /**
     * 	4497153900060001	待上架
		4497153900060002	已上架
		4497153900060003	商家下架
		4497153900060004	平台强制下架
     */
    private String productStatus = "";
    
    
    /**
     * 品牌名字
     */
    private String brandName = "";
    
    /**
     * 是否是虚拟商品  Y：是  N：否
     */
    private String validate_flag="";
    
    /**
     * 第三方商户编号
     */
    private String smallSellerCode = "";
    
	public String getProductCodeOld() {
		return productCodeOld;
	}

	public void setProductCodeOld(String productCodeOld) {
		this.productCodeOld = productCodeOld;
	}

	public String getSaleScopeDid() {
		return saleScopeDid;
	}

	public void setSaleScopeDid(String saleScopeDid) {
		this.saleScopeDid = saleScopeDid;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getProductVolumeItem() {
		return productVolumeItem;
	}

	public void setProductVolumeItem(String productVolumeItem) {
		this.productVolumeItem = productVolumeItem;
	}

	public int getFlagPayway() {
		return flagPayway;
	}

	public void setFlagPayway(int flagPayway) {
		this.flagPayway = flagPayway;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public List<UcSellercategoryProductRelation> getUsprList() {
		return usprList;
	}

	public void setUsprList(List<UcSellercategoryProductRelation> usprList) {
		this.usprList = usprList;
	}

	public String getProductStatus() {
		return productStatus;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	public String getMainPicUrl() {
		return mainPicUrl;
	}

	public void setMainPicUrl(String mainPicUrl) {
		this.mainPicUrl = mainPicUrl;
	}

	public String getTransportTemplate() {
		return transportTemplate;
	}

	public void setTransportTemplate(String transportTemplate) {
		this.transportTemplate = transportTemplate;
	}

	public String getSellProductcode() {
		return sellProductcode;
	}

	public void setSellProductcode(String sellProductcode) {
		this.sellProductcode = sellProductcode;
	}

	public PcProductflow getPcProdcutflow() {
		return pcProdcutflow;
	}

	public void setPcProdcutflow(PcProductflow pcProdcutflow) {
		this.pcProdcutflow = pcProdcutflow;
	}

	public PcCategoryinfo getCategory() {
		return category;
	}

	public void setCategory(PcCategoryinfo category) {
		this.category = category;
	}

	public PcProductdescription getDescription() {
		return description;
	}

	public void setDescription(PcProductdescription description) {
		this.description = description;
	}

	public List<PcProductpic> getPcPicList() {
		return pcPicList;
	}

	public void setPcPicList(List<PcProductpic> pcPicList) {
		this.pcPicList = pcPicList;
	}

	public List<PcProductproperty> getPcProductpropertyList() {
		return pcProductpropertyList;
	}

	public void setPcProductpropertyList(
			List<PcProductproperty> pcProductpropertyList) {
		this.pcProductpropertyList = pcProductpropertyList;
	}



	public List<ProductSkuInfoForFamily> getProductSkuInfoList() {
		return productSkuInfoList;
	}

	public void setProductSkuInfoList(List<ProductSkuInfoForFamily> productSkuInfoList) {
		this.productSkuInfoList = productSkuInfoList;
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
    public void setProdutName(String productName) {
        this.productName = productName;
    }
    
    public String getProdutName() {
        return this.productName;
    }
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }
    
    public String getSellerCode() {
        return this.sellerCode;
    }
    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }
    
    public String getBrandCode() {
        return this.brandCode;
    }
  
    public void setFlagSale(Integer flagSale) {
        this.flagSale = flagSale;
    }
    
    public Integer getFlagSale() {
        return this.flagSale;
    }
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    
    public String getCreateTime() {
        return this.createTime;
    }
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    
    public String getUpdateTime() {
        return this.updateTime;
    }

	public String getProductShortname() {
		return productShortname;
	}

	public void setProductShortname(String productShortname) {
		this.productShortname = productShortname;
	}

	public String getSupplierName() {
		return supplierName;
	}

	public void setSupplierName(String supplierName) {
		this.supplierName = supplierName;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public BigDecimal getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(BigDecimal productWeight) {
		this.productWeight = productWeight;
	}

	public BigDecimal getMinSellPrice() {
		return minSellPrice;
	}

	public void setMinSellPrice(BigDecimal minSellPrice) {
		this.minSellPrice = minSellPrice;
	}

	public BigDecimal getMaxSellPrice() {
		return maxSellPrice;
	}

	public void setMaxSellPrice(BigDecimal maxSellPrice) {
		this.maxSellPrice = maxSellPrice;
	}

	public BigDecimal getMarketPrice() {
		return marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = marketPrice;
	}

	public BigDecimal getCostPrice() {
		return costPrice;
	}

	public void setCostPrice(BigDecimal costPrice) {
		this.costPrice = costPrice;
	}

	public BigDecimal getProductVolume() {
		return productVolume;
	}

	public void setProductVolume(BigDecimal productVolume) {
		this.productVolume = productVolume;
	}

	public String getValidate_flag() {
		return validate_flag;
	}

	public void setValidate_flag(String validate_flag) {
		this.validate_flag = validate_flag;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getSmallSellerCode() {
		return smallSellerCode;
	}

	public void setSmallSellerCode(String smallSellerCode) {
		this.smallSellerCode = smallSellerCode;
	}

}


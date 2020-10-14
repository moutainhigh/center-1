package com.cmall.dborm.txmodel;

import java.math.BigDecimal;

public class PcProductinfo {
	private String afterSaleAddressUid;
	public String getAfterSaleAddressUid() {
		return afterSaleAddressUid;
	}

	public void setAfterSaleAddressUid(String afterSaleAddressUid) {
		this.afterSaleAddressUid = afterSaleAddressUid;
	}

	/**
	 * 自动上架
	 */
	private String autoSell;
	
	public String getAutoSell() {
		return autoSell;
	}

	public void setAutoSell(String autoSell) {
		this.autoSell = autoSell;
	}
	
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.zid
     *
     * @mbggenerated
     */
    private Integer zid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.uid
     *
     * @mbggenerated
     */
    private String uid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.product_code_old
     *
     * @mbggenerated
     */
    private String productCodeOld;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.product_code
     *
     * @mbggenerated
     */
    private String productCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.product_name
     *
     * @mbggenerated
     */
    private String productName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.product_shortname
     *
     * @mbggenerated
     */
    private String productShortname;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.video_url
     *
     * @mbggenerated
     */
    private String videoUrl;
    
    private String videoMainPic;
    
    private String productDescVideo;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.seller_code
     *
     * @mbggenerated
     */
    private String sellerCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.small_seller_code
     *
     * @mbggenerated
     */
    private String smallSellerCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.brand_code
     *
     * @mbggenerated
     */
    private String brandCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.product_weight
     *
     * @mbggenerated
     */
    private BigDecimal productWeight;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.flag_sale
     *
     * @mbggenerated
     */
    private Integer flagSale;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.create_time
     *
     * @mbggenerated
     */
    private String createTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.update_time
     *
     * @mbggenerated
     */
    private String updateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.min_sell_price
     *
     * @mbggenerated
     */
    private BigDecimal minSellPrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.max_sell_price
     *
     * @mbggenerated
     */
    private BigDecimal maxSellPrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.market_price
     *
     * @mbggenerated
     */
    private BigDecimal marketPrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.cost_price
     *
     * @mbggenerated
     */
    private BigDecimal costPrice;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.tax_rate
     *
     * @mbggenerated
     */
    private BigDecimal taxRate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.product_status
     *
     * @mbggenerated
     */
    private String productStatus;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.product_volume
     *
     * @mbggenerated
     */
    private BigDecimal productVolume;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.transport_template
     *
     * @mbggenerated
     */
    private String transportTemplate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.area_template
     *
     * @mbggenerated
     */
    private String areaTemplate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.sell_productcode
     *
     * @mbggenerated
     */
    private String sellProductcode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.supplier_name
     *
     * @mbggenerated
     */
    private String supplierName;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.mainpic_url
     *
     * @mbggenerated
     */
    private String mainpicUrl;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.labels
     *
     * @mbggenerated
     */
    private String labels;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.flag_payway
     *
     * @mbggenerated
     */
    private Integer flagPayway;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.product_volume_item
     *
     * @mbggenerated
     */
    private String productVolumeItem;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.sale_scope_did
     *
     * @mbggenerated
     */
    private String saleScopeDid;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.validate_flag
     *
     * @mbggenerated
     */
    private String validateFlag;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.product_code_copy
     *
     * @mbggenerated
     */
    private String productCodeCopy;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.adpic_url
     *
     * @mbggenerated
     */
    private String adpicUrl;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.expiry_date
     *
     * @mbggenerated
     */
    private Integer expiryDate;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.expiry_unit
     *
     * @mbggenerated
     */
    private String expiryUnit;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.low_good
     *
     * @mbggenerated
     */
    private String lowGood;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.qualification_category_code
     *
     * @mbggenerated
     */
    private String qualificationCategoryCode;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column pc_productinfo.product_adv
     *
     * @mbggenerated
     */
    private String productAdv;

	/**
	 * 提货券商品 449747110001:否，449747110002:是
	 */
	private String voucherGood;

    private String onlinepayFlag;
    
    private String onlinepayStart;
    
    private String onlinepayEnd;
    
	/**
	 * 直播商品促销语
	 */
    private String tvTips;
    
    /**
     *是否参与会员日 
     */
    private String vipdayFlag;
    
    /**
     *是否计入惠家有毛利标识
     */
    private String prchCd;
    
    /**
     *商品是否赋予积分
     */
    private String accmYn;
    
	/**
	 * 品牌名字
	 */
	private String brandName = "";
	
	/**
	 * 是否一件代发:Y/N
	 */
	private String vlOrs;
	
	/**
	 * 是否厂商收款:Y/N
	 */
	private String dlrCharge;
	
	/**
	 * 是否厂商配送:Y/N
	 */
	private String cspsFlag;
	
	/**
	 * 商品归属
	 */
	private String soId;
	
	public String getSoId() {
		return soId;
	}

	public void setSoId(String soId) {
		this.soId = soId;
	}

	public String getCspsFlag() {
		return cspsFlag;
	}

	public void setCspsFlag(String cspsFlag) {
		this.cspsFlag = cspsFlag;
	}

	public String getDlrCharge() {
		return dlrCharge;
	}

	public void setDlrCharge(String dlrCharge) {
		this.dlrCharge = dlrCharge;
	}

	/**
	 * 税收分类编码
	 */
	private String taxCode;
    
	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getVipdayFlag() {
		return vipdayFlag;
	}

	public void setVipdayFlag(String vipdayFlag) {
		this.vipdayFlag = vipdayFlag;
	}

	public String getOnlinepayFlag() {
		return onlinepayFlag;
	}

	public void setOnlinepayFlag(String onlinepayFlag) {
		this.onlinepayFlag = onlinepayFlag;
	}

	public String getOnlinepayStart() {
		return onlinepayStart;
	}

	public void setOnlinepayStart(String onlinepayStart) {
		this.onlinepayStart = onlinepayStart;
	}

	public String getOnlinepayEnd() {
		return onlinepayEnd;
	}

	public void setOnlinepayEnd(String onlinepayEnd) {
		this.onlinepayEnd = onlinepayEnd;
	}

    public String getVoucherGood() {
		return voucherGood;
	}

	public void setVoucherGood(String voucherGood) {
		this.voucherGood = voucherGood;
	}

	/**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.zid
     *
     * @return the value of pc_productinfo.zid
     *
     * @mbggenerated
     */
    public Integer getZid() {
        return zid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.zid
     *
     * @param zid the value for pc_productinfo.zid
     *
     * @mbggenerated
     */
    public void setZid(Integer zid) {
        this.zid = zid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.uid
     *
     * @return the value of pc_productinfo.uid
     *
     * @mbggenerated
     */
    public String getUid() {
        return uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.uid
     *
     * @param uid the value for pc_productinfo.uid
     *
     * @mbggenerated
     */
    public void setUid(String uid) {
        this.uid = uid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.product_code_old
     *
     * @return the value of pc_productinfo.product_code_old
     *
     * @mbggenerated
     */
    public String getProductCodeOld() {
        return productCodeOld;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.product_code_old
     *
     * @param productCodeOld the value for pc_productinfo.product_code_old
     *
     * @mbggenerated
     */
    public void setProductCodeOld(String productCodeOld) {
        this.productCodeOld = productCodeOld;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.product_code
     *
     * @return the value of pc_productinfo.product_code
     *
     * @mbggenerated
     */
    public String getProductCode() {
        return productCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.product_code
     *
     * @param productCode the value for pc_productinfo.product_code
     *
     * @mbggenerated
     */
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.product_name
     *
     * @return the value of pc_productinfo.product_name
     *
     * @mbggenerated
     */
    public String getProductName() {
        return productName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.product_name
     *
     * @param productName the value for pc_productinfo.product_name
     *
     * @mbggenerated
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.product_shortname
     *
     * @return the value of pc_productinfo.product_shortname
     *
     * @mbggenerated
     */
    public String getProductShortname() {
        return productShortname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.product_shortname
     *
     * @param productShortname the value for pc_productinfo.product_shortname
     *
     * @mbggenerated
     */
    public void setProductShortname(String productShortname) {
        this.productShortname = productShortname;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.video_url
     *
     * @return the value of pc_productinfo.video_url
     *
     * @mbggenerated
     */
    public String getVideoUrl() {
        return videoUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.video_url
     *
     * @param videoUrl the value for pc_productinfo.video_url
     *
     * @mbggenerated
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getVideoMainPic() {
		return videoMainPic;
	}

	public void setVideoMainPic(String videoMainPic) {
		this.videoMainPic = videoMainPic;
	}

	public String getProductDescVideo() {
		return productDescVideo;
	}

	public void setProductDescVideo(String productDescVideo) {
		this.productDescVideo = productDescVideo;
	}

	/**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.seller_code
     *
     * @return the value of pc_productinfo.seller_code
     *
     * @mbggenerated
     */
    public String getSellerCode() {
        return sellerCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.seller_code
     *
     * @param sellerCode the value for pc_productinfo.seller_code
     *
     * @mbggenerated
     */
    public void setSellerCode(String sellerCode) {
        this.sellerCode = sellerCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.small_seller_code
     *
     * @return the value of pc_productinfo.small_seller_code
     *
     * @mbggenerated
     */
    public String getSmallSellerCode() {
        return smallSellerCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.small_seller_code
     *
     * @param smallSellerCode the value for pc_productinfo.small_seller_code
     *
     * @mbggenerated
     */
    public void setSmallSellerCode(String smallSellerCode) {
        this.smallSellerCode = smallSellerCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.brand_code
     *
     * @return the value of pc_productinfo.brand_code
     *
     * @mbggenerated
     */
    public String getBrandCode() {
        return brandCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.brand_code
     *
     * @param brandCode the value for pc_productinfo.brand_code
     *
     * @mbggenerated
     */
    public void setBrandCode(String brandCode) {
        this.brandCode = brandCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.product_weight
     *
     * @return the value of pc_productinfo.product_weight
     *
     * @mbggenerated
     */
    public BigDecimal getProductWeight() {
        return productWeight;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.product_weight
     *
     * @param productWeight the value for pc_productinfo.product_weight
     *
     * @mbggenerated
     */
    public void setProductWeight(BigDecimal productWeight) {
        this.productWeight = productWeight;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.flag_sale
     *
     * @return the value of pc_productinfo.flag_sale
     *
     * @mbggenerated
     */
    public Integer getFlagSale() {
        return flagSale;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.flag_sale
     *
     * @param flagSale the value for pc_productinfo.flag_sale
     *
     * @mbggenerated
     */
    public void setFlagSale(Integer flagSale) {
        this.flagSale = flagSale;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.create_time
     *
     * @return the value of pc_productinfo.create_time
     *
     * @mbggenerated
     */
    public String getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.create_time
     *
     * @param createTime the value for pc_productinfo.create_time
     *
     * @mbggenerated
     */
    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.update_time
     *
     * @return the value of pc_productinfo.update_time
     *
     * @mbggenerated
     */
    public String getUpdateTime() {
        return updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.update_time
     *
     * @param updateTime the value for pc_productinfo.update_time
     *
     * @mbggenerated
     */
    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.min_sell_price
     *
     * @return the value of pc_productinfo.min_sell_price
     *
     * @mbggenerated
     */
    public BigDecimal getMinSellPrice() {
        return minSellPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.min_sell_price
     *
     * @param minSellPrice the value for pc_productinfo.min_sell_price
     *
     * @mbggenerated
     */
    public void setMinSellPrice(BigDecimal minSellPrice) {
        this.minSellPrice = minSellPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.max_sell_price
     *
     * @return the value of pc_productinfo.max_sell_price
     *
     * @mbggenerated
     */
    public BigDecimal getMaxSellPrice() {
        return maxSellPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.max_sell_price
     *
     * @param maxSellPrice the value for pc_productinfo.max_sell_price
     *
     * @mbggenerated
     */
    public void setMaxSellPrice(BigDecimal maxSellPrice) {
        this.maxSellPrice = maxSellPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.market_price
     *
     * @return the value of pc_productinfo.market_price
     *
     * @mbggenerated
     */
    public BigDecimal getMarketPrice() {
        return marketPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.market_price
     *
     * @param marketPrice the value for pc_productinfo.market_price
     *
     * @mbggenerated
     */
    public void setMarketPrice(BigDecimal marketPrice) {
        this.marketPrice = marketPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.cost_price
     *
     * @return the value of pc_productinfo.cost_price
     *
     * @mbggenerated
     */
    public BigDecimal getCostPrice() {
        return costPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.cost_price
     *
     * @param costPrice the value for pc_productinfo.cost_price
     *
     * @mbggenerated
     */
    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.tax_rate
     *
     * @return the value of pc_productinfo.tax_rate
     *
     * @mbggenerated
     */
    public BigDecimal getTaxRate() {
        return taxRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.tax_rate
     *
     * @param taxRate the value for pc_productinfo.tax_rate
     *
     * @mbggenerated
     */
    public void setTaxRate(BigDecimal taxRate) {
        this.taxRate = taxRate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.product_status
     *
     * @return the value of pc_productinfo.product_status
     *
     * @mbggenerated
     */
    public String getProductStatus() {
        return productStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.product_status
     *
     * @param productStatus the value for pc_productinfo.product_status
     *
     * @mbggenerated
     */
    public void setProductStatus(String productStatus) {
        this.productStatus = productStatus;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.product_volume
     *
     * @return the value of pc_productinfo.product_volume
     *
     * @mbggenerated
     */
    public BigDecimal getProductVolume() {
        return productVolume;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.product_volume
     *
     * @param productVolume the value for pc_productinfo.product_volume
     *
     * @mbggenerated
     */
    public void setProductVolume(BigDecimal productVolume) {
        this.productVolume = productVolume;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.transport_template
     *
     * @return the value of pc_productinfo.transport_template
     *
     * @mbggenerated
     */
    public String getTransportTemplate() {
        return transportTemplate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.transport_template
     *
     * @param transportTemplate the value for pc_productinfo.transport_template
     *
     * @mbggenerated
     */
    public void setTransportTemplate(String transportTemplate) {
        this.transportTemplate = transportTemplate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.area_template
     *
     * @return the value of pc_productinfo.area_template
     *
     * @mbggenerated
     */
    public String getAreaTemplate() {
        return areaTemplate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.area_template
     *
     * @param areaTemplate the value for pc_productinfo.area_template
     *
     * @mbggenerated
     */
    public void setAreaTemplate(String areaTemplate) {
        this.areaTemplate = areaTemplate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.sell_productcode
     *
     * @return the value of pc_productinfo.sell_productcode
     *
     * @mbggenerated
     */
    public String getSellProductcode() {
        return sellProductcode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.sell_productcode
     *
     * @param sellProductcode the value for pc_productinfo.sell_productcode
     *
     * @mbggenerated
     */
    public void setSellProductcode(String sellProductcode) {
        this.sellProductcode = sellProductcode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.supplier_name
     *
     * @return the value of pc_productinfo.supplier_name
     *
     * @mbggenerated
     */
    public String getSupplierName() {
        return supplierName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.supplier_name
     *
     * @param supplierName the value for pc_productinfo.supplier_name
     *
     * @mbggenerated
     */
    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.mainpic_url
     *
     * @return the value of pc_productinfo.mainpic_url
     *
     * @mbggenerated
     */
    public String getMainpicUrl() {
        return mainpicUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.mainpic_url
     *
     * @param mainpicUrl the value for pc_productinfo.mainpic_url
     *
     * @mbggenerated
     */
    public void setMainpicUrl(String mainpicUrl) {
        this.mainpicUrl = mainpicUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.labels
     *
     * @return the value of pc_productinfo.labels
     *
     * @mbggenerated
     */
    public String getLabels() {
        return labels;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.labels
     *
     * @param labels the value for pc_productinfo.labels
     *
     * @mbggenerated
     */
    public void setLabels(String labels) {
        this.labels = labels;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.flag_payway
     *
     * @return the value of pc_productinfo.flag_payway
     *
     * @mbggenerated
     */
    public Integer getFlagPayway() {
        return flagPayway;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.flag_payway
     *
     * @param flagPayway the value for pc_productinfo.flag_payway
     *
     * @mbggenerated
     */
    public void setFlagPayway(Integer flagPayway) {
        this.flagPayway = flagPayway;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.product_volume_item
     *
     * @return the value of pc_productinfo.product_volume_item
     *
     * @mbggenerated
     */
    public String getProductVolumeItem() {
        return productVolumeItem;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.product_volume_item
     *
     * @param productVolumeItem the value for pc_productinfo.product_volume_item
     *
     * @mbggenerated
     */
    public void setProductVolumeItem(String productVolumeItem) {
        this.productVolumeItem = productVolumeItem;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.sale_scope_did
     *
     * @return the value of pc_productinfo.sale_scope_did
     *
     * @mbggenerated
     */
    public String getSaleScopeDid() {
        return saleScopeDid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.sale_scope_did
     *
     * @param saleScopeDid the value for pc_productinfo.sale_scope_did
     *
     * @mbggenerated
     */
    public void setSaleScopeDid(String saleScopeDid) {
        this.saleScopeDid = saleScopeDid;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.validate_flag
     *
     * @return the value of pc_productinfo.validate_flag
     *
     * @mbggenerated
     */
    public String getValidateFlag() {
        return validateFlag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.validate_flag
     *
     * @param validateFlag the value for pc_productinfo.validate_flag
     *
     * @mbggenerated
     */
    public void setValidateFlag(String validateFlag) {
        this.validateFlag = validateFlag;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.product_code_copy
     *
     * @return the value of pc_productinfo.product_code_copy
     *
     * @mbggenerated
     */
    public String getProductCodeCopy() {
        return productCodeCopy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.product_code_copy
     *
     * @param productCodeCopy the value for pc_productinfo.product_code_copy
     *
     * @mbggenerated
     */
    public void setProductCodeCopy(String productCodeCopy) {
        this.productCodeCopy = productCodeCopy;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.adpic_url
     *
     * @return the value of pc_productinfo.adpic_url
     *
     * @mbggenerated
     */
    public String getAdpicUrl() {
        return adpicUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.adpic_url
     *
     * @param adpicUrl the value for pc_productinfo.adpic_url
     *
     * @mbggenerated
     */
    public void setAdpicUrl(String adpicUrl) {
        this.adpicUrl = adpicUrl;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.expiry_date
     *
     * @return the value of pc_productinfo.expiry_date
     *
     * @mbggenerated
     */
    public Integer getExpiryDate() {
        return expiryDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.expiry_date
     *
     * @param expiryDate the value for pc_productinfo.expiry_date
     *
     * @mbggenerated
     */
    public void setExpiryDate(Integer expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.expiry_unit
     *
     * @return the value of pc_productinfo.expiry_unit
     *
     * @mbggenerated
     */
    public String getExpiryUnit() {
        return expiryUnit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.expiry_unit
     *
     * @param expiryUnit the value for pc_productinfo.expiry_unit
     *
     * @mbggenerated
     */
    public void setExpiryUnit(String expiryUnit) {
        this.expiryUnit = expiryUnit;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.low_good
     *
     * @return the value of pc_productinfo.low_good
     *
     * @mbggenerated
     */
    public String getLowGood() {
        return lowGood;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.low_good
     *
     * @param lowGood the value for pc_productinfo.low_good
     *
     * @mbggenerated
     */
    public void setLowGood(String lowGood) {
        this.lowGood = lowGood;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.qualification_category_code
     *
     * @return the value of pc_productinfo.qualification_category_code
     *
     * @mbggenerated
     */
    public String getQualificationCategoryCode() {
        return qualificationCategoryCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.qualification_category_code
     *
     * @param qualificationCategoryCode the value for pc_productinfo.qualification_category_code
     *
     * @mbggenerated
     */
    public void setQualificationCategoryCode(String qualificationCategoryCode) {
        this.qualificationCategoryCode = qualificationCategoryCode;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column pc_productinfo.product_adv
     *
     * @return the value of pc_productinfo.product_adv
     *
     * @mbggenerated
     */
    public String getProductAdv() {
        return productAdv;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column pc_productinfo.product_adv
     *
     * @param productAdv the value for pc_productinfo.product_adv
     *
     * @mbggenerated
     */
    public void setProductAdv(String productAdv) {
        this.productAdv = productAdv;
    }

	public String getTvTips() {
		return tvTips;
	}

	public void setTvTips(String tvTips) {
		this.tvTips = tvTips;
	}

	public String getPrchCd() {
		return prchCd;
	}

	public void setPrchCd(String prchCd) {
		this.prchCd = prchCd;
	}

	public String getAccmYn() {
		return accmYn;
	}

	public void setAccmYn(String accmYn) {
		this.accmYn = accmYn;
	}

	public String getVlOrs() {
		return vlOrs;
	}

	public void setVlOrs(String vlOrs) {
		this.vlOrs = vlOrs;
	}

	public String getTaxCode() {
		return taxCode;
	}

	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
  
}
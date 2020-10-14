package com.cmall.ordercenter.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.cmall.ordercenter.model.PropertyInfoForProtuct;
import com.cmall.ordercenter.model.PicInfo;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.helper.MoneyHelper;

/**
 * 商品类  
 * @author zhaoxq
 * @version 1.0
 */
public class ProductInfoForCC extends OrderBase {
	
	/**
	 * 商品编号
	 */
	@ZapcomApi(value="商品编号")
	private String productCode = "";
	
	/**
	 * 商品名称
	 */
	@ZapcomApi(value="商品名称")
	private String productName = "";

	/**
	 * 销售价格
	 */
	@ZapcomApi(value="销售价格")
	private BigDecimal sellPrice = new BigDecimal(0.00);
	
	/**
	 * 商品类型 
	 */
	@ZapcomApi(value="商品类型",remark="是否是虚拟商品  Y：是  N：否")
	private String validateFlag = "";
	
	/**
	 * 商品分类
	 */
	@ZapcomApi(value="商品分类")
	private List<String> productCategoryList = new ArrayList<String>();
	
	/**
	 * 商品状态
	 */
	@ZapcomApi(value="商品状态",remark="4497153900060001:待上架<br/>"
								   + "4497153900060002:已上架<br/>"
								   + "4497153900060003:商家下架<br/>"
								   + "4497153900060004:平台强制下架")
    private String productStatus = "";
	
	/**
	 * ld商品名称
	 */
	@ZapcomApi(value="ld商品名称")
	private String productShortname = "";
	
	/**
	 * 市场价格
	 */
	@ZapcomApi(value="市场价格")
	private BigDecimal marketPrice=new BigDecimal(0.00);

	/**
	 * 商品主图
	 */
	@ZapcomApi(value="商品主图")
    private PicInfo mainpicUrl = new PicInfo();
	
	/**
	 * 商品图片
	 */
	@ZapcomApi(value="商品图片")
    private List<PicInfo> pcPicList = new ArrayList<PicInfo>();
	/**
	 * 商品规格
	 */
	@ZapcomApi(value="商品规格")
	private List<PropertyInfoForProtuct> propertyList = new ArrayList<PropertyInfoForProtuct>();
	
	/**
	 * 商品品牌
	 */
	@ZapcomApi(value="商品品牌")
    private String brandCode = "";
	
	/**
	 * 商品重量
	 */
	@ZapcomApi(value="商品重量")
	private String productWeight = "";

	/**
	 * 商品体积
	 */
	@ZapcomApi(value="商品体积")
	private BigDecimal productVolume = new BigDecimal(0.00);
	
	/**
	 * 运费模式
	 */
	@ZapcomApi(value="运费模式")
	private String transportTemplate = "";
	
	/**
	 * 商品描述
	 */
	@ZapcomApi(value="商品描述")
    private String descriptInfo = "";

	/**
	 * 描述图片
	 */
	@ZapcomApi(value="描述图片")
    private List<PicInfo> discriptPicList = new ArrayList<PicInfo>();
	
	/**
	 * 商品标签
	 */
	@ZapcomApi(value="商品标签")
    private String keyword = "";

	/**
	 * 关键字
	 */
	@ZapcomApi(value="关键字")
	private String labels = "";
	/**
	 * 商品货号（销售渠道）
	 */
	@ZapcomApi(value="商品货号")
	private String sellProductcode = "";
	
	/**
	 * 商品视频连接
	 */
	@ZapcomApi(value="商品视频链接")
	private String videoUrl = "" ;
	
	/**
	 * 虚拟销售基数
	 */
	@ZapcomApi(value="虚拟销售基数")
	private String fictitiousSales = "";
	
	/**
	 * 采购类型
	 */
	@ZapcomApi(value="采购类型",remark="4497471600160001:代销<br/>"
									+"4497471600160002:经销")
	private String purchaseType = "";
	
	/**
	 * 结算方式
	 */
	@ZapcomApi(value="结算方式",remark="4497471600110001:常规结算<br/>"
								   + "4497471600110002:特殊结算<br/>"
								   + "4497471600110003:服务费结算")
	private String settlementType = "";
	
	/**
	 * 创建时间
	 */
	@ZapcomApi(value="创建时间")
	private String createTime = "";
	
	/**
	 * 更新时间
	 */
	@ZapcomApi(value="更新时间")
	private String updateTime = "";
	
	/**
	 * 入库类型
	 */
	@ZapcomApi(value="入库类型")
	private String prchType = "";
	
	/**
	 * 仓库编号
	 */
	@ZapcomApi(value="仓库编号")
	private String oaSiteNo = "";
	
	/**
	 * 供应商编号
	 */
	@ZapcomApi(value="供应商编号")
	private String dlrid = "";
	
	/**
	 * 供应商名称
	 */
	@ZapcomApi(value="供应商名称")
	private String dlrNm = "";
	
	/**
	 * 是否支持货到付款
	 */
	@ZapcomApi(value="是否支持货到付款",remark="0:否  1:是")
	private String flagPayway = "";

	
	@ZapcomApi(value="sku信息")
    private List<ProductSkuInfoForCC> skuList = new ArrayList<ProductSkuInfoForCC>();

	
	public BigDecimal getMarketPrice() {
		return this.marketPrice;
	}

	public void setMarketPrice(BigDecimal marketPrice) {
		this.marketPrice = new BigDecimal(MoneyHelper.format(marketPrice)) ;
	}

	public BigDecimal getSellPrice() {
		return this.sellPrice;
	}

	public void setSellPrice(BigDecimal sellPrice) {
		this.sellPrice = new BigDecimal(MoneyHelper.format(sellPrice)) ;;
	}

	public String getProductStatus() {
		return productStatus;
	}

	public String getValidateFlag() {
		return validateFlag;
	}

	public void setValidateFlag(String validateFlag) {
		this.validateFlag = validateFlag;
	}

	public void setProductStatus(String productStatus) {
		this.productStatus = productStatus;
	}

	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}

	public String getBrandCode() {
		return brandCode;
	}

	public void setBrandCode(String brandCode) {
		this.brandCode = brandCode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public List<String> getProductCategoryList() {
		return productCategoryList;
	}

	public void setProductCategoryList(List<String> productCategoryList) {
		this.productCategoryList = productCategoryList;
	}

	public String getProductShortname() {
		return productShortname;
	}

	public void setProductShortname(String productShortname) {
		this.productShortname = productShortname;
	}

	public List<PropertyInfoForProtuct> getPropertyList() {
		return propertyList;
	}

	public void setPropertyList(List<PropertyInfoForProtuct> propertyList) {
		this.propertyList = propertyList;
	}

	public List<PicInfo> getPcPicList() {
		return pcPicList;
	}

	public void setPcPicList(List<PicInfo> pcPicList) {
		this.pcPicList = pcPicList;
	}

	public String getProductWeight() {
		return productWeight;
	}

	public void setProductWeight(String productWeight) {
		this.productWeight = productWeight;
	}

	public BigDecimal getProductVolume() {
		return productVolume;
	}

	public void setProductVolume(BigDecimal productVolume) {
		this.productVolume = productVolume;
	}

	public String getTransportTemplate() {
		return transportTemplate;
	}

	public void setTransportTemplate(String transportTemplate) {
		this.transportTemplate = transportTemplate;
	}

	public String getDescriptInfo() {
		return descriptInfo;
	}

	public void setDescriptInfo(String descriptInfo) {
		this.descriptInfo = descriptInfo;
	}

	public PicInfo getMainpicUrl() {
		return mainpicUrl;
	}

	public void setMainpicUrl(PicInfo mainpicUrl) {
		this.mainpicUrl = mainpicUrl;
	}

	public List<PicInfo> getDiscriptPicList() {
		return discriptPicList;
	}

	public void setDiscriptPicList(List<PicInfo> discriptPicList) {
		this.discriptPicList = discriptPicList;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getLabels() {
		return labels;
	}

	public void setLabels(String labels) {
		this.labels = labels;
	}

	public String getSellProductcode() {
		return sellProductcode;
	}

	public void setSellProductcode(String sellProductcode) {
		this.sellProductcode = sellProductcode;
	}

	public String getFictitiousSales() {
		return fictitiousSales;
	}

	public void setFictitiousSales(String fictitiousSales) {
		this.fictitiousSales = fictitiousSales;
	}

	public String getPurchaseType() {
		return purchaseType;
	}

	public void setPurchaseType(String purchaseType) {
		this.purchaseType = purchaseType;
	}

	public String getSettlementType() {
		return settlementType;
	}

	public void setSettlementType(String settlementType) {
		this.settlementType = settlementType;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getPrchType() {
		return prchType;
	}

	public void setPrchType(String prchType) {
		this.prchType = prchType;
	}

	public String getOaSiteNo() {
		return oaSiteNo;
	}

	public void setOaSiteNo(String oaSiteNo) {
		this.oaSiteNo = oaSiteNo;
	}

	public String getDlrid() {
		return dlrid;
	}

	public void setDlrid(String dlrid) {
		this.dlrid = dlrid;
	}

	public String getDlrNm() {
		return dlrNm;
	}

	public void setDlrNm(String dlrNm) {
		this.dlrNm = dlrNm;
	}

	public String getFlagPayway() {
		return flagPayway;
	}

	public void setFlagPayway(String flagPayway) {
		this.flagPayway = flagPayway;
	}

	public List<ProductSkuInfoForCC> getSkuList() {
		return skuList;
	}

	public void setSkuList(List<ProductSkuInfoForCC> skuList) {
		this.skuList = skuList;
	}
}

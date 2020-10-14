package com.cmall.productcenter.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.srnpr.xmasorder.model.TagInfo;
import com.srnpr.xmassystem.modelproduct.PlusModelProductLabel;
import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapweb.helper.MoneyHelper;

/**
 * 商品信息返回字段描述
 * @author zhouguohui
 *
 */
public class Item {
	

	@ZapcomApi(value="添加商品分类(LD商品,普通商品,跨境商品,跨境直邮,平台入驻,缤纷商品)标签字段")
	private String proClassifyTag = "";
	
	// 546添加,返回商品列表标签的宽高
	@ZapcomApi(value="商品分类标签图片高度")
	private String proClassifyTagH;
	
	@ZapcomApi(value="商品分类标签图片宽度")
	private String proClassifyTagW;
	
	@ZapcomApi(value="添加按钮",remark="0：表示无加入按钮，1：标示有加入按钮")
	private int hostessButton=0;
	
	@ZapcomApi(value="商品编号")
	private String productCode;
	
	@ZapcomApi(value="商品名称")
	private String productName;
	
	@ZapcomApi(value="拼团标识", remark="拼团编码：4497472600010024")
	private String groupBuying = "";
	
	@ZapcomApi(value="是否拼团商品", remark="是：4497472000050001，否：4497472000050002")
	private String productType = "4497472000050002";
	
	@ZapcomApi(value="拼团商品原价", remark="如果是拼团商品的话，需要显示的划线价（原实际售价）")
	private BigDecimal skuPrice;
	
	@ZapcomApi(value="拼团商品展示价", remark="如果是拼团商品的话，需要显示的拼团购买价格")
	private BigDecimal groupBuyingPrice;
	
	@ZapcomApi(value="几人团", remark="需要几人参团，字符串类型的数字")
	private String collagePersonCount;
	
	@ZapcomApi(value="标签集合",remark="秒杀、闪购、拼团、特价、会员日、满减、领券、赠品（最多展示三个）")
	private List<String> tagList;
	
	@ZapcomApi(value="带样式的商品活动标签")
	private List<TagInfo> tagInfoList = new ArrayList<TagInfo>();
	
	@ZapcomApi(value="商品标签",remark="LB160108100002:生鲜商品;LB160108100003:TV商品;LB160108100004:海外购商品")
    private List<String> labelsList = new ArrayList<String>();
	
	@ZapcomApi(value="商品标签图片地址",remark="3.9.2以后开始使用")
	private String labelsPic;
	
	@ZapcomApi(value="标签")
	private String tag;
	
	@ZapcomApi(value="图片地址")
	private String imgUrl;
	
	@ZapcomApi(value="原价", remark = "字段废弃，划线价以skuPrice为准")
	private BigDecimal originalPrice;
	
	@ZapcomApi(value="当前价格")
	private BigDecimal currentPrice;
	
	@ZapcomApi(value="是否有货")
	private String stockNum;
	
	@ZapcomApi(value="商品销量")
	private int productNumber;
	
	@ZapcomApi(value="是否海外购",remark="0代表不是海外购  1代表海外购")
	private String flagTheSea = "0";
	
	@ZapcomApi(value="商品参加活动",remark="商品所参加的：内购，特价，闪购，秒杀，拍卖等标签都在改字段存放")
	private List<String> activityList;
	
	@ZapcomApi(value="商品参加赠品",remark="该字段为3.8.0版本及以后版本提供，该字段只放赠品")
	private List<String> otherShow;
	
	@ZapcomApi(value="商品标签详细信息",remark="")
    private List<PlusModelProductLabel> labelsInfo = new ArrayList<PlusModelProductLabel>();

	@ZapcomApi(value = "sku关联商品下的所有sku实际库存")
	private int allSkuRealStock = 0;
	
	@ZapcomApi(value = "分销券优惠面额")
	private String couponValue = "";
	
	public String getCouponValue() {	
		if (StringUtils.isNotBlank(couponValue)) {
			couponValue =MoneyHelper.format(new BigDecimal(couponValue));  
		}
		return couponValue;
	}
	public void setCouponValue(String couponValue) {
		this.couponValue = couponValue;
	}

	public List<PlusModelProductLabel> getLabelsInfo() {
		return labelsInfo;
	}
	public void setLabelsInfo(List<PlusModelProductLabel> labelsInfo) {
		this.labelsInfo = labelsInfo;
	}
	public String getProClassifyTagH() {
		return proClassifyTagH;
	}
	public void setProClassifyTagH(String proClassifyTagH) {
		this.proClassifyTagH = proClassifyTagH;
	}
	public String getProClassifyTagW() {
		return proClassifyTagW;
	}
	public void setProClassifyTagW(String proClassifyTagW) {
		this.proClassifyTagW = proClassifyTagW;
	}
	public String getCollagePersonCount() {
		return collagePersonCount;
	}
	public void setCollagePersonCount(String collagePersonCount) {
		this.collagePersonCount = collagePersonCount;
	}
	public BigDecimal getGroupBuyingPrice() {
		return groupBuyingPrice;
	}
	public void setGroupBuyingPrice(BigDecimal groupBuyingPrice) {
		this.groupBuyingPrice = groupBuyingPrice;
	}
	public String getProductType() {
		return productType;
	}
	public void setProductType(String productType) {
		this.productType = productType;
	}
	public BigDecimal getSkuPrice() {
		return skuPrice;
	}
	public void setSkuPrice(BigDecimal skuPrice) {
		this.skuPrice = skuPrice;
	}
	public String getGroupBuying() {
		return groupBuying;
	}
	public void setGroupBuying(String groupBuying) {
		this.groupBuying = groupBuying;
	}
	public String getProClassifyTag() {
		return proClassifyTag;
	}
	public void setProClassifyTag(String proClassifyTag) {
		this.proClassifyTag = proClassifyTag;
	}
	/**
	 * @return the hostessButton
	 */
	public int getHostessButton() {
		return hostessButton;
	}
	/**
	 * @param hostessButton the hostessButton to set
	 */
	public void setHostessButton(int hostessButton) {
		this.hostessButton = hostessButton;
	}
	/**
	 * @return the productName
	 */
	public String getProductName() {
		return productName;
	}
	/**
	 * @param productName the productName to set
	 */
	public void setProductName(String productName) {
		this.productName = productName;
	}
	/**
	 * @return the tagList
	 */
	public List<String> getTagList() {
		return tagList;
	}
	/**
	 * @param tagList the tagList to set
	 */
	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}
	
	public List<TagInfo> getTagInfoList() {
		return tagInfoList;
	}
	public void setTagInfoList(List<TagInfo> tagInfoList) {
		this.tagInfoList = tagInfoList;
	}
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}
	/**
	 * @param tag the tag to set
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}
	/**
	 * @return the imgUrl
	 */
	public String getImgUrl() {
		return imgUrl;
	}
	/**
	 * @param imgUrl the imgUrl to set
	 */
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	/**
	 * @return the originalPrice
	 */
	public BigDecimal getOriginalPrice() {
		return originalPrice;
	}
	/**
	 * @param originalPrice the originalPrice to set
	 */
	public void setOriginalPrice(BigDecimal originalPrice) {
		this.originalPrice = originalPrice;
	}
	/**
	 * @return the currentPrice
	 */
	public BigDecimal getCurrentPrice() {
		return currentPrice;
	}
	/**
	 * @param currentPrice the currentPrice to set
	 */
	public void setCurrentPrice(BigDecimal currentPrice) {
		this.currentPrice = currentPrice;
	}
	/**
	 * @return the productCode
	 */
	public String getProductCode() {
		return productCode;
	}
	/**
	 * @param productCode the productCode to set
	 */
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}
	/**
	 * @return the activityList
	 */
	public List<String> getActivityList() {
		return activityList;
	}
	/**
	 * @param activityList the activityList to set
	 */
	public void setActivityList(List<String> activityList) {
		this.activityList = activityList;
	}
	/**
	 * @return the stockNum
	 */
	public String getStockNum() {
		return stockNum;
	}
	/**
	 * @param stockNum the stockNum to set
	 */
	public void setStockNum(String stockNum) {
		this.stockNum = stockNum;
	}
	/**
	 * @return the productNumber
	 */
	public int getProductNumber() {
		return productNumber;
	}
	/**
	 * @param productNumber the productNumber to set
	 */
	public void setProductNumber(int productNumber) {
		this.productNumber = productNumber;
	}
	/**
	 * @return the otherShow
	 */
	public List<String> getOtherShow() {
		return otherShow;
	}
	/**
	 * @param otherShow the otherShow to set
	 */
	public void setOtherShow(List<String> otherShow) {
		this.otherShow = otherShow;
	}
	/**
	 * @return the flagTheSea
	 */
	public String getFlagTheSea() {
		return flagTheSea;
	}
	/**
	 * @param flagTheSea the flagTheSea to set
	 */
	public void setFlagTheSea(String flagTheSea) {
		this.flagTheSea = flagTheSea;
	}
	/**
	 * @return the labelsList
	 */
	public List<String> getLabelsList() {
		return labelsList;
	}
	/**
	 * @param labelsList the labelsList to set
	 */
	public void setLabelsList(List<String> labelsList) {
		this.labelsList = labelsList;
	}
	/**
	 * @return the labelsPic
	 */
	public String getLabelsPic() {
		return labelsPic;
	}
	/**
	 * @param labelsPic the labelsPic to set
	 */
	public void setLabelsPic(String labelsPic) {
		this.labelsPic = labelsPic;
	}

	public int getAllSkuRealStock() {
		return allSkuRealStock;
	}

	public void setAllSkuRealStock(int allSkuRealStock) {
		this.allSkuRealStock = allSkuRealStock;
	}

}

package com.cmall.productcenter.model.api;

import java.math.BigDecimal;

import com.srnpr.zapcom.baseannotation.ZapcomApi;
import com.srnpr.zapcom.topapi.RootInput;

/**
 * 搜索输入参数
 * @author zhouguohui
 *
 */
public class ApiSearchResultsInput extends RootInput{
	
	@ZapcomApi(value="搜索关键字")
	private String keyWord="";
	@ZapcomApi(value="分类搜索关键字",demo="当用户点击分类精确查找的时间传递值,这个字段用于PC端点击分类时传递的参数")
	private String categerkeyWord="";
	@ZapcomApi(value="品牌搜索关键字",demo="当用户点击品牌精确查找的时间传递值,这个字段用于PC端点击品牌时传递的参数")
	private String brandKeyWord="";
	@ZapcomApi(value = "用户类型", remark="用户类型" ,demo="4497469400050001:内购会员，4497469400050002:注册会员")
	private String buyerType = "4497469400050002";
	@ZapcomApi(value = "搜索最低价格", remark="用户搜索最低价格,这个字段用于PC端按价格区间查询的时间传递的最低价格")
	private BigDecimal minPrice =BigDecimal.ZERO ;
	@ZapcomApi(value = "搜索最高价格", remark="用户搜索最高价格,这个字段用于PC端按价格区间查询的时间传递的最高价格")
	private BigDecimal maxPrice = BigDecimal.ZERO;
	@ZapcomApi(value="排序字段",remark="0、默认；1、销量；2、上架时间；3、价格；4、人气;5、top50,默认为：0")
	private int sortType=0;
	@ZapcomApi(value="正序倒序",remark="1、正序；2、倒序，默认为：2")
	private int sortFlag=0;
	@ZapcomApi(value="每页读取记录数",remark="默认为:10")
	private int pageSize=0;
	@ZapcomApi(value="读取页码",remark="默认为：1")
	private int pageNo=0;
	@ZapcomApi(value="屏幕宽度",require=1,remark="用于搜索主图图片的压缩")
	private int screenWidth;
	@ZapcomApi(value="数据是否加密",remark="数据如果加密传递默认值为：base64")
	private String baseValue="";
	@ZapcomApi(value="是否精确搜索",remark="如果按二级分类搜索默认值为：category,按品牌搜索默认值为：brand，按top50精确搜索值为：top50")
	private String categoryOrBrand="";
	@ZapcomApi(value="用户名",remark="登陆用户名")
	private String userName="";
	@ZapcomApi(value="主播类型",remark="0:为普通用户，1:为主播用户，2:为其他")
	private int hostessId=0;
	@ZapcomApi(value = "渠道编号", remark = "惠家有app：449747430001，wap商城：449747430002，微信商城：449747430003，PC：449747430004")
	private String channelId = "449747430001";
	@ZapcomApi(value="用户编号")
	private String memberCode = "";
	@ZapcomApi(value = "是否显示内购", remark = "默认值为0，显示内购活动传递1")
	private Integer isPurchase = 0;
	@ZapcomApi(value = "品类编号", remark = "搜三级品类则传三级品类编号，搜四级品类则传四级品类编号，多个编号用逗号隔开")
	private String categoryCode = "";
	
	@ZapcomApi(value = "家有服务下选中的内容", remark = "如果选中多个，用英文逗号拼接",demo="家有自营,促销商品")
	private String jyfw = "";
	@ZapcomApi(value = "优惠券类型编号", remark = "优惠券类型编号")
	private String couponTypeCode = "";
	
	
	public String getJyfw() {
		return jyfw;
	}

	public void setJyfw(String jyfw) {
		this.jyfw = jyfw;
	}
	
	public Integer getIsPurchase() {
		return isPurchase;
	}

	public void setIsPurchase(Integer isPurchase) {
		this.isPurchase = isPurchase;
	}
	/**
	 * @return the channelId
	 */
	public String getChannelId() {
		return channelId;
	}
	/**
	 * @param channelId the channelId to set
	 */
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public int getHostessId() {
		return hostessId;
	}
	public void setHostessId(int hostessId) {
		this.hostessId = hostessId;
	}
	/**
	 * @return the keyWord
	 */
	public String getKeyWord() {
		return keyWord;
	}
	/**
	 * @param keyWord the keyWord to set
	 */
	public void setKeyWord(String keyWord) {
		this.keyWord = keyWord;
	}
	/**
	 * @return the pageSize
	 */
	public int getPageSize() {
		return pageSize;
	}
	/**
	 * @param pageSize the pageSize to set
	 */
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	/**
	 * @return the pageNo
	 */
	public int getPageNo() {
		return pageNo;
	}
	/**
	 * @param pageNo the pageNo to set
	 */
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	/**
	 * @return the sortType
	 */
	public int getSortType() {
		return sortType;
	}
	/**
	 * @param sortType the sortType to set
	 */
	public void setSortType(int sortType) {
		this.sortType = sortType;
	}
	
	/**
	 * @return the sortFlag
	 */
	public int getSortFlag() {
		return sortFlag;
	}
	/**
	 * @param sortFlag the sortFlag to set
	 */
	public void setSortFlag(int sortFlag) {
		this.sortFlag = sortFlag;
	}
	/**
	 * @return the screenWidth
	 */
	public int getScreenWidth() {
		return screenWidth;
	}
	/**
	 * @param screenWidth the screenWidth to set
	 */
	public void setScreenWidth(int screenWidth) {
		this.screenWidth = screenWidth;
	}
	/**
	 * @return the baseValue
	 */
	public String getBaseValue() {
		return baseValue;
	}
	/**
	 * @param baseValue the baseValue to set
	 */
	public void setBaseValue(String baseValue) {
		this.baseValue = baseValue;
	}
	/**
	 * @return the categoryOrBrand
	 */
	public String getCategoryOrBrand() {
		return categoryOrBrand;
	}
	/**
	 * @param categoryOrBrand the categoryOrBrand to set
	 */
	public void setCategoryOrBrand(String categoryOrBrand) {
		this.categoryOrBrand = categoryOrBrand;
	}
	/**
	 * @return the buyerType
	 */
	public String getBuyerType() {
		return buyerType;
	}
	/**
	 * @param buyerType the buyerType to set
	 */
	public void setBuyerType(String buyerType) {
		this.buyerType = buyerType;
	}
	/**
	 * @return the userName
	 */
	public String getUserName() {
		return userName;
	}
	/**
	 * @param userName the userName to set
	 */
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public BigDecimal getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(BigDecimal minPrice) {
		this.minPrice = minPrice;
	}
	public BigDecimal getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(BigDecimal maxPrice) {
		this.maxPrice = maxPrice;
	}
	/**
	 * @return the categerkeyWord
	 */
	public String getCategerkeyWord() {
		return categerkeyWord;
	}
	/**
	 * @param categerkeyWord the categerkeyWord to set
	 */
	public void setCategerkeyWord(String categerkeyWord) {
		this.categerkeyWord = categerkeyWord;
	}
	/**
	 * @return the brandKeyWord
	 */
	public String getBrandKeyWord() {
		return brandKeyWord;
	}
	/**
	 * @param brandKeyWord the brandKeyWord to set
	 */
	public void setBrandKeyWord(String brandKeyWord) {
		this.brandKeyWord = brandKeyWord;
	}
	public String getMemberCode() {
		return memberCode;
	}
	public void setMemberCode(String memberCode) {
		this.memberCode = memberCode;
	}

	public String getCategoryCode() {
		return categoryCode;
	}

	public void setCategoryCode(String categoryCode) {
		this.categoryCode = categoryCode;
	}

	public String getCouponTypeCode() {
		return couponTypeCode;
	}

	public void setCouponTypeCode(String couponTypeCode) {
		this.couponTypeCode = couponTypeCode;
	}
	
}
	

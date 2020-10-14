package com.cmall.ordercenter.model;

import com.srnpr.zapcom.baseannotation.ZapcomApi;



/**   
 * 	购物车添加的商品信息对象
*    xiegj
*/
public class GoodsInfoForAdd  {
	@ZapcomApi(value = "sku编号", remark = "sku编号",require = 1, demo = "8019123456")
	private String sku_code = "";
	
	@ZapcomApi(value = "商品数量", remark = "商品数量",require = 1, demo = "123456")
	private int sku_num = 0;
	
	@ZapcomApi(value = "地区编号", remark = "可不填写，添加购物车不再需要区域编号", demo = "123456")
	private String area_code = "";
	
	@ZapcomApi(value = "商品编号", remark = "商品编号", demo = "8016123456")
	private String product_code = "";
	
	@ZapcomApi(value = "是否选择", remark = "是否选择：1：是，0：否", demo = "1")
	private String chooseFlag = "0";
	
	@ZapcomApi(value="是否原价购买",remark = "（0：否；1：是）针对商品参与活动但活动库存售罄的情况下，想要原价购买")
	private String flg = "0";
	
	@ZapcomApi(value = "是否显示内购", remark = "默认值为0，显示内购活动传递1")
	private Integer isPurchase = 0;
	
	@ZapcomApi(value="积分商城活动ID")
	private String integralDetailId = "";
	
	@ZapcomApi(value="是否为加价购商品 0否 1是")
	private String ifJJGFlag = "0";
	
	@ZapcomApi(value="推广赚推广人编号",remark = "")
	private String tgzUserCode = "";
	
	@ZapcomApi(value="推广赚买家秀编号",remark = "")
	private String tgzShowCode = "";

	@ZapcomApi(value="分销人编号", remark="")
	private String fxrcode = "";
	
	@ZapcomApi(value="推广人编号", remark="")
	private String shareCode = "";
	
	@ZapcomApi(value="农场指定页下单标识", remark = "如果是农场指定页下单且赠送雨露则传 1 ,其他表示非指定")
	private String isNCSpecial = "0";
	
	public String getIsNCSpecial() {
		return isNCSpecial;
	}

	public void setIsNCSpecial(String isNCSpecial) {
		this.isNCSpecial = isNCSpecial;
	}

	public String getIfJJGFlag() {
		return ifJJGFlag;
	}

	public void setIfJJGFlag(String ifJJGFlag) {
		this.ifJJGFlag = ifJJGFlag;
	}

	public String getTgzUserCode() {
		return tgzUserCode;
	}

	public void setTgzUserCode(String tgzUserCode) {
		this.tgzUserCode = tgzUserCode;
	}

	public String getTgzShowCode() {
		return tgzShowCode;
	}

	public void setTgzShowCode(String tgzShowCode) {
		this.tgzShowCode = tgzShowCode;
	}

	public Integer getIsPurchase() {
		return isPurchase;
	}

	public void setIsPurchase(Integer isPurchase) {
		this.isPurchase = isPurchase;
	}

	public String getSku_code() {
		return sku_code;
	}

	public void setSku_code(String sku_code) {
		this.sku_code = sku_code;
	}

	public int getSku_num() {
		return sku_num;
	}

	public void setSku_num(int sku_num) {
		this.sku_num = sku_num;
	}

	public String getArea_code() {
		return area_code;
	}

	public void setArea_code(String area_code) {
		this.area_code = area_code;
	}

	public String getProduct_code() {
		return product_code;
	}

	public void setProduct_code(String product_code) {
		this.product_code = product_code;
	}

	public String getChooseFlag() {
		return chooseFlag;
	}

	public void setChooseFlag(String chooseFlag) {
		this.chooseFlag = chooseFlag;
	}

	public String getFlg() {
		return flg;
	}

	public void setFlg(String flg) {
		this.flg = flg;
	}

	public String getIntegralDetailId() {
		return integralDetailId;
	}

	public void setIntegralDetailId(String integralDetailId) {
		this.integralDetailId = integralDetailId;
	}

	public String getFxrcode() {
		return fxrcode;
	}

	public void setFxrcode(String fxrcode) {
		this.fxrcode = fxrcode;
	}

	public String getShareCode() {
		return shareCode;
	}

	public void setShareCode(String shareCode) {
		this.shareCode = shareCode;
	}
	
}


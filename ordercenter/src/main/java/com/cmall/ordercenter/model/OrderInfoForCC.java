package com.cmall.ordercenter.model;

import java.math.BigDecimal;
import java.util.List;

import com.srnpr.zapcom.baseannotation.ZapcomApi;

/**   
*    
* 项目名称：ordercenter 
* 类名称：Order   
* 类描述：   
* 创建人：zhaoxq  
* 创建时间：2015-10-16 
* @version    
*    
*/
public class OrderInfoForCC extends OrderBase {
	
	/**
	 * 订单编号
	 */
	@ZapcomApi(value="订单编号")
	private String orderCode = "";
	
	/**
	 * 订单来源
	 */
	@ZapcomApi(value="订单来源",remark="449715190001:正常订单<br/>"
								   + "449715190002:android订单<br/>"
								   + "449715190003:ios订单<br/>"
								   + "449715190004:网站手机订单<br/>"
								   + "449715190005:网站订单<br/>"
								   + "449715190006：微信订单<br/>"
								   + "449715190007:扫码购订单<br/>"
								   + "449715190008:客服订单<br/>"
								   + "449715190009:分销订单<br/>")
	private String orderSource = "";
	
	/**
	 * 订单类型
	 */
	@ZapcomApi(value="订单类型",remark="449715200001:商城订单<br/>"
									+"449715200002:好物产订单<br/>"
									+"449715200003:试用商品<br/>"
									+"449715200004:闪购订单<br/>"
									+"449715200005:普通订单<br/>"
									+"449715200006:会员订单<br/>"
									+"449715200007:内购订单<br/>"
									+"449715200008:折扣订单<br/>"
									+"449715200009:尊享订单<br/>"
									+"449715200010:扫码购订单<br/>"
									+"449715200011:MT订单<br/>"
									+"449715200012:LD分销订单")
	private String orderType = "";
	
	/**
	 * 订单状态
	 */
	@ZapcomApi(value="订单状态",remark="4497153900010001:下单成功-未付款<br/>"
									+"4497153900010002:下单成功-未发货<br/>"
									+"4497153900010003:已发货<br/>"
									+"4497153900010004:已收货<br/>"
									+"4497153900010005:交易成功<br/>"
									+"4497153900010006:交易失败")
	private String orderStatus = "";

	/**
	 * 订单来源平台
	 */
	@ZapcomApi(value="下单平台",remark="SI3003:沙皮狗<br/>"
									+"SI2003:惠家有<br/>"
									+"SI2009:家有汇<br/>"
									+"SI2011:微公社<br/>"
									+"SI2015:微商城<br/>"
									+"SI2019:享卖小店")
	private String orderChannel="";
	
	/**
	 * 注册手机号
	 */
	@ZapcomApi(value="注册手机号")
	private String registerMobile="";
	
	/**
	 * 付款方式
	 */
	@ZapcomApi(value="付款方式 ",remark="449716200001:在线支付<br/>"
									  +"449716200002:货到付款<br/>"
									  +"449716200003:积分支付<br/>"
									  +"449716200004:微信支付<br/>"
									  +"449746280001:礼品卡<br/>"
								      +"449746280002:优惠券<br/>"
								      +"449746280003:支付宝支付<br/>"
								      +"449746280004:快钱支付<br/>"
								      +"449746280005:微信支付<br/>"
								      +"449746280006:储值金<br/>"
								      +"449746280007:暂存款<br/>"
								      +"449746280008:积分<br/>"
								      +"449746280009:微公社支付<br/>"
								      +"449746280010:分销支付")
	private String payType = "";
	
	/**
	 * 配送方式 值  说明
	 * 449715210001	快递
	 * 449715210002	邮局
	 */
	@ZapcomApi(value="配送方式",remark="449715210001:快递<br/>"
		                           + "449715210002:邮局")
	private String sendType = "";
	
	/**
	 * 商品运费(实际运费)
	 */
	@ZapcomApi(value="商品运费")
	private BigDecimal transportMoney =new BigDecimal(0.00);
	
	

	/**
	 * 订单金额=商品金额+商品运费-商品活动金额
	 */
	@ZapcomApi(value="订单金额")
	private BigDecimal orderMoney = new BigDecimal(0.00);
	
	/**
	 * 优惠金额
	 */
	@ZapcomApi(value="优惠金额")
	private BigDecimal orderSaveAmt = new BigDecimal(0.00);
	
	/**
	 * 微公社支付金额
	 */
	@ZapcomApi(value="微公社支付金额")
	private BigDecimal cgroupPayAmt = new BigDecimal(0.00);

	/**
	 *  应付款
	 */
	@ZapcomApi(value="应付款")
	private BigDecimal dueMoney = new BigDecimal(0.00); 
	
	/**
	 * 已付款金额
	 */
	@ZapcomApi(value="已付款金额")
	private BigDecimal payedMoney = new BigDecimal(0.00);
	
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
	 * 外部订单编号
	 */
	@ZapcomApi(value="外部订单编号")
	private String outOrderCode = "";
	
	/**
	 * 
	 * 订单辅助状态
	 */
	@ZapcomApi(value="订单辅助状态",remark="4497153900140001:已取消<br/>"
			                          + "4497153900140002:空")
	private String orderStatusExt = "";

	
	/**
	 * 支付订单编号
	 */
	@ZapcomApi(value="支付订单编号")
	private String bigOrderCode = "";
	
	/**
	 * 发货商
	 */
	@ZapcomApi(value="发货商")
	private String shipper = "";
	
	/**
	 * 商户名称
	 */
	@ZapcomApi(value="商户名称")
	private String sellerName = "";
	
	/**
	 * 商品详情
	 */
	@ZapcomApi(value="商品详情")
	private List<OrderDetailForCC> productList = null;
	
	
	/**
	 * 订单活动
	 */
	@ZapcomApi(value="订单活动")
	private List<OcOrderActivityForCC> activityList = null; 
	
	/**
	 * 支付信息
	 */
	@ZapcomApi(value="支付信息")
	private List<OcOrderPayForCC> ocOrderPayList = null;
	
	/**
	 * 配送信息
	 */
	@ZapcomApi(value="配送信息")
	private OrderAddressForCC address = null;
	

	/**
	 * 发货信息
	 */
	@ZapcomApi(value="发货信息")
	private OcOrderShipmentsForCC ocorderShipments = null;
		
	/**
	 * 运单流水
	 */
	@ZapcomApi(value="运单流水")
	private List<ExpressForCC> expressList = null;
	
	/**
	 * 日志流水
	 */
	@ZapcomApi(value="日志流水")
	private List<LcOrderStatusForCC> lcOrderStatusList = null;

	/**
	 * 订单备注
	 */
	@ZapcomApi(value="订单备注")
	private List<OrderRemarkForCC> orderRemarkList = null;
	
	/**
	 * 退货信息
	 */
	@ZapcomApi(value="退货信息")
	private List<ReturnGoodsForCC> returngoodsList = null;
	
	/**
	 * 退货日志流水
	 */
	@ZapcomApi(value="退货日志流水")
	private List<ReturnGoodsLogForCC> returngoodsLogList = null;
	
	/**
	 * 换货信息
	 */
	@ZapcomApi(value="换货信息")
	private List<ExchangegoodsForCC> changegoodsList = null;
	
	/**
	 * 换货日志流水
	 */
	@ZapcomApi(value="换货日志流水")
	private List<ExchangegoodsStatusLogForCC> changegoodsLogList = null;
	
	/**
	 * 退款信息
	 */
	@ZapcomApi(value="退款信息")
	private List<ReturnMoneyForCC> returnMoneyList = null;
	
	/**
	 * 退款日志流水
	 */
	@ZapcomApi(value="退款日志流水")
	private List<ReturnMoneyLogForCC> returnMoneyLogList = null;
	
	public List<ExpressForCC> getExpressList() {
		return expressList;
	}

	public void setExpressList(List<ExpressForCC> expressList) {
		this.expressList = expressList;
	}

	public String getUpdateTime() {
		return updateTime;
	}

	public void setUpdateTime(String updateTime) {
		this.updateTime = updateTime;
	}

	public String getOrderChannel() {
		return orderChannel;
	}

	public void setOrderChannel(String orderChannel) {
		this.orderChannel = orderChannel;
	}

	public OcOrderShipmentsForCC getOcorderShipments() {
		return ocorderShipments;
	}

	public void setOcorderShipments(OcOrderShipmentsForCC ocorderShipments) {
		this.ocorderShipments = ocorderShipments;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public List<OcOrderPayForCC> getOcOrderPayList() {
		return ocOrderPayList;
	}

	public void setOcOrderPayList(List<OcOrderPayForCC> ocOrderPayList) {
		this.ocOrderPayList = ocOrderPayList;
	}

	public List<OcOrderActivityForCC> getActivityList() {
		return activityList;
	}

	public void setActivityList(List<OcOrderActivityForCC> activityList) {
		this.activityList = activityList;
	}

	public OrderAddressForCC getAddress() {
		return address;
	}

	public void setAddress(OrderAddressForCC address) {
		this.address = address;
	}

	public List<OrderDetailForCC> getProductList() {
		return productList;
	}

	public void setProductList(List<OrderDetailForCC> productList) {
		this.productList = productList;
	}
	
	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getOrderType() {
		return orderType;
	}

	public void setOrderType(String orderType) {
		this.orderType = orderType;
	}

	public String getOrderStatus() {
		return orderStatus;
	}

	public void setOrderStatus(String orderStatus) {
		this.orderStatus = orderStatus;
	}

	public String getShipper() {
		return shipper;
	}

	public void setShipper(String shipper) {
		this.shipper = shipper;
	}

	public String getSellerName() {
		return sellerName;
	}

	public void setSellerName(String sellerName) {
		this.sellerName = sellerName;
	}

	public String getPayType() {
		return payType;
	}

	public void setPayType(String payType) {
		this.payType = payType;
	}

	public String getSendType() {
		return sendType;
	}

	public void setSendType(String sendType) {
		this.sendType = sendType;
	}


	public BigDecimal getTransportMoney() {
		return transportMoney;
	}

	public void setTransportMoney(BigDecimal transportMoney) {
		this.transportMoney = transportMoney;
	}

	public BigDecimal getOrderMoney() {
		return orderMoney;
	}

	public void setOrderMoney(BigDecimal orderMoney) {
		this.orderMoney = orderMoney;
	}

	
	public BigDecimal getOrderSaveAmt() {
		return orderSaveAmt;
	}

	public void setOrderSaveAmt(BigDecimal orderSaveAmt) {
		this.orderSaveAmt = orderSaveAmt;
	}

	public BigDecimal getPayedMoney() {
		return payedMoney;
	}

	public void setPayedMoney(BigDecimal payedMoney) {
		this.payedMoney = payedMoney;
	}


	public BigDecimal getDueMoney() {
		return dueMoney;
	}

	public void setDueMoney(BigDecimal dueMoney) {
		this.dueMoney = dueMoney;
	}

	public BigDecimal getCgroupPayAmt() {
		return cgroupPayAmt;
	}

	public void setCgroupPayAmt(BigDecimal cgroupPayAmt) {
		this.cgroupPayAmt = cgroupPayAmt;
	}

	public String getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getRegisterMobile() {
		return registerMobile;
	}

	public void setRegisterMobile(String registerMobile) {
		this.registerMobile = registerMobile;
	}

	public String getOutOrderCode() {
		return outOrderCode;
	}

	public void setOutOrderCode(String outOrderCode) {
		this.outOrderCode = outOrderCode;
	}

	public String getOrderStatusExt() {
		return orderStatusExt;
	}

	public void setOrderStatusExt(String orderStatusExt) {
		this.orderStatusExt = orderStatusExt;
	}

	public String getBigOrderCode() {
		return bigOrderCode;
	}

	public void setBigOrderCode(String bigOrderCode) {
		this.bigOrderCode = bigOrderCode;
	}

	public List<LcOrderStatusForCC> getLcOrderStatusList() {
		return lcOrderStatusList;
	}

	public void setLcOrderStatusList(List<LcOrderStatusForCC> lcOrderStatusList) {
		this.lcOrderStatusList = lcOrderStatusList;
	}

	public List<OrderRemarkForCC> getOrderRemarkList() {
		return orderRemarkList;
	}

	public void setOrderRemarkList(List<OrderRemarkForCC> orderRemarkList) {
		this.orderRemarkList = orderRemarkList;
	}

	public List<ReturnGoodsForCC> getReturngoodsList() {
		return returngoodsList;
	}

	public void setReturngoodsList(List<ReturnGoodsForCC> returngoodsList) {
		this.returngoodsList = returngoodsList;
	}

	public List<ReturnGoodsLogForCC> getReturngoodsLogList() {
		return returngoodsLogList;
	}

	public void setReturngoodsLogList(List<ReturnGoodsLogForCC> returngoodsLogList) {
		this.returngoodsLogList = returngoodsLogList;
	}

	public List<ExchangegoodsForCC> getChangegoodsList() {
		return changegoodsList;
	}

	public void setChangegoodsList(List<ExchangegoodsForCC> changegoodsList) {
		this.changegoodsList = changegoodsList;
	}

	public List<ExchangegoodsStatusLogForCC> getChangegoodsLogList() {
		return changegoodsLogList;
	}

	public void setChangegoodsLogList(
			List<ExchangegoodsStatusLogForCC> changegoodsLogList) {
		this.changegoodsLogList = changegoodsLogList;
	}

	public List<ReturnMoneyForCC> getReturnMoneyList() {
		return returnMoneyList;
	}

	public void setReturnMoneyList(List<ReturnMoneyForCC> returnMoneyList) {
		this.returnMoneyList = returnMoneyList;
	}

	public List<ReturnMoneyLogForCC> getReturnMoneyLogList() {
		return returnMoneyLogList;
	}

	public void setReturnMoneyLogList(List<ReturnMoneyLogForCC> returnMoneyLogList) {
		this.returnMoneyLogList = returnMoneyLogList;
	}
}

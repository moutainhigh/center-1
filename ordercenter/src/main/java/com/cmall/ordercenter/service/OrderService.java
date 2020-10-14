package com.cmall.ordercenter.service;

import java.math.BigDecimal;
import java.sql.Types;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;

import com.cmall.dborm.txmodel.JifenInfo;
import com.cmall.dborm.txmodel.JifenLogExample;
import com.cmall.membercenter.memberdo.MemberConst;
import com.cmall.ordercenter.common.CreateOrderCallableStatement;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.common.OrderConst;
import com.cmall.ordercenter.common.SqlCommon;
import com.cmall.ordercenter.model.Express;
import com.cmall.ordercenter.model.OcOrderActivity;
import com.cmall.ordercenter.model.OcOrderPay;
import com.cmall.ordercenter.model.OcOrderShipments;
import com.cmall.ordercenter.model.Order;
import com.cmall.ordercenter.model.OrderAddress;
import com.cmall.ordercenter.model.OrderDetail;
import com.cmall.ordercenter.model.OrderStatusGroupModel;
import com.cmall.ordercenter.model.OrderStatusLog;
import com.cmall.ordercenter.model.SkuForCache;
import com.cmall.ordercenter.model.UcSellerInfo;
import com.cmall.ordercenter.model.api.ApiCancelModel;
import com.cmall.ordercenter.model.api.ApiCancelOrderResult;
import com.cmall.ordercenter.model.api.ApiOperateInput;
import com.cmall.ordercenter.model.api.ApiOrderShipmentsNoticInput;
import com.cmall.ordercenter.model.api.ApiRollbackCouponResult;
import com.cmall.ordercenter.model.api.GiftVoucherInfo;
import com.cmall.ordercenter.service.api.ApiOrderShipmentsNotic;
import com.cmall.ordercenter.service.cache.ProductSkuCache;
import com.cmall.ordercenter.service.money.CreateMoneyService;
import com.cmall.ordercenter.txservice.TxJiFenService;
import com.cmall.ordercenter.txservice.TxOrderService;
import com.cmall.ordercenter.util.CouponUtil;
import com.cmall.productcenter.model.PcPropertyinfoForFamily;
import com.cmall.productcenter.model.ProductSkuInfo;
import com.cmall.productcenter.service.ProductService;
import com.cmall.productcenter.service.ProductSkuInfoService;
import com.cmall.systemcenter.jms.ProductJmsSupport;
import com.cmall.systemcenter.service.FlowBussinessService;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.xmasorder.channel.service.PorscheOrderService;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.load.LoadMemberLevel;
import com.srnpr.xmassystem.load.LoadProductOrderNum;
import com.srnpr.xmassystem.modelevent.PlusModelMemberLevel;
import com.srnpr.xmassystem.modelevent.PlusModelMemberLevelQuery;
import com.srnpr.xmassystem.plusquery.PlusModelQuery;
import com.srnpr.xmassystem.service.PlusServiceLjq;
import com.srnpr.xmassystem.service.PlusServiceSeller;
import com.srnpr.xmassystem.support.PlusSupportLD;
import com.srnpr.xmassystem.support.PlusSupportMember;
import com.srnpr.xmassystem.support.PlusSupportStock;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basehelper.VersionHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.basemodel.MObjMap;
import com.srnpr.zapcom.basesupport.SerializeSupport;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapdata.dbsupport.DbTemplate;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.helper.WebHelper;
import com.srnpr.zapweb.usermodel.MUserInfo;
import com.srnpr.zapweb.webfactory.UserFactory;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 
 * 项目名称：ordercenter 类名称：OrderService 类描述： 创建人：yanzj 创建时间：2013-9-2 上午11:03:25
 * 修改人：yanzj 修改时间：2013-9-2 上午11:03:25 修改备注：
 * 
 * @version
 * 
 */
public class OrderService extends BaseClass {
	
	public OrderStatusGroupModel getOrderStatusGroupCount(String buyerCode,
			String fromTime, String orderStatus, String orderType,
			String orderChannel) {

		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyerCode);

		String orderWhereStr = "buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and order_status<>'4497153900010007'";

		if (fromTime != null && !fromTime.equals("")) {
			mapParam.put("create_time", fromTime);
			orderWhereStr += " and create_time>=:create_time";
		}

		if (orderStatus != null && !orderStatus.equals("")) {
			mapParam.put("order_status", orderStatus);
			orderWhereStr += " and order_status=:order_status";
		}

		if (orderType != null && !orderType.equals("")) {
			mapParam.put("order_type", orderType);
			orderWhereStr += " and order_type=:order_type";
		}

		if (orderChannel != null && !orderChannel.equals("")) {
			mapParam.put("order_channel", orderChannel);
			orderWhereStr += " and order_channel=:order_channel";
		}

		List<Map<String, Object>> listMap = DbUp.upTable("oc_orderinfo")
				.dataSqlList(
						"SELECT order_status,COUNT(1) as count FROM oc_orderinfo where "
								+ orderWhereStr + " GROUP BY order_status ",
						mapParam);

		OrderStatusGroupModel osgm = new OrderStatusGroupModel();
		if (listMap != null) {

			int size = listMap.size();

			Map<String, Object> urMapParam = new MObjMap<String, Object>();

			for (int j = 0; j < size; j++) {
				urMapParam = listMap.get(j);
				int count = Integer
						.parseInt(urMapParam.get("count").toString());
				if (urMapParam.get("order_status").toString().trim()
						.equals("4497153900010001")) {
					osgm.setOrderNoPay(count);
				} else if (urMapParam.get("order_status").toString().trim()
						.equals("4497153900010002")) {
					osgm.setOrderNotSend(count);
				} else if (urMapParam.get("order_status").toString().trim()
						.equals("4497153900010004")) {
					osgm.setOrderReceive(count);
				} else if (urMapParam.get("order_status").toString().trim()
						.equals("4497153900010003")) {
					osgm.setOrderSend(count);
				} else if (urMapParam.get("order_status").toString().trim()
						.equals("4497153900010005")) {
					osgm.setOrderSuccess(count);
				} else if (urMapParam.get("order_status").toString().trim()
						.equals("4497153900010006")) {
					osgm.setOrderUnSuccess(count);
				}
			}
		}
		return osgm;
	}

	/**
	 * 添加订单
	 * 
	 * @param list
	 * @param error
	 * @return
	 */
	public int AddOrderList(List<Order> list, StringBuffer error) {
		int ret = 0;

		//System.out.println("AddOrderBegin");
		List<Order> successList = new ArrayList<Order>();
		for (Order order : list) {
			String orderCode = com.srnpr.zapweb.helper.WebHelper
					.upCode(OrderConst.OrderHead);
			//System.out.println("AddOrder" + orderCode);
			order.setOrderCode(orderCode);

			ret = this.AddOrder(orderCode, order, error);

			if (ret == 1)
				successList.add(order);
			else {

				// 如果添加失败，则调用挨个取消接口，如果取消失败，则发邮件。
				for (Order orderdel : successList) {
					this.CancelOrderForCreate(orderdel);
				}
				break;

			}
		}

		if (ret == 1) {
			String str = "";
			for (Order order : list) {
				str = str + order.getOrderCode() + ",";
			}
			;
			if (!str.equals(""))
				error.append(str.substring(0, str.length() - 1));
		}

		return ret;
	}

	/**
	 * 订单取消接口
	 * 
	 * @param cancelList
	 * @return
	 */
	public ApiCancelOrderResult CancelOrderForList(String orderCodes) {
		
		String operater="";
		try {
			operater = UserFactory.INSTANCE.create().getUserCode();
		} catch (Exception e) {
			operater="system";
		}
		
		return CancelOrderForList(orderCodes, operater);
	}

	/**
	 * 订单转失败，或者客户取消时，调用此接口
	 * 
	 * @param order
	 * @return
	 */
	public ApiRollbackCouponResult CancelOrder(Order order) {
		String operater=UserFactory.INSTANCE.create().getUserCode();
		return CancelOrder(order, operater);
	}
	
	
	public ApiCancelOrderResult CancelOrderForList(String orderCodes,String operater) {
		List<Order> cancelList = new ArrayList<Order>();

		ApiCancelOrderResult ret = new ApiCancelOrderResult();

		if (orderCodes == null) {
			ret.setResultCode(939301051);
			ret.setResultMessage(bInfo(939301051));
		} else {

			String[] orderCodeList = orderCodes.split(",");
			if (orderCodeList == null || orderCodeList.length == 0) {
				ret.setResultCode(939301051);
				ret.setResultMessage(bInfo(939301051));
			} else {

				for (String orderCode : orderCodeList) {
					Order order = this.getOrder(orderCode);
					if (order != null) {
						cancelList.add(order);
					} else {
						ret.setResultCode(939301051);
						ret.setResultMessage(bInfo(939301051));
						break;
					}
				}

				if (ret.getResultCode() == SqlCommon.SuccessFlag) {
					for (Order order : cancelList) {

						ApiCancelModel acm = new ApiCancelModel();
						try {
							ApiRollbackCouponResult rr = this.CancelOrder(order,operater);

							acm.setOrderCode(order.getOrderCode());
							acm.setResultCode(rr.getResultCode());
							acm.setResultMessage(rr.getResultMessage());
							
							for(GiftVoucherInfo map : rr.getReWriteLD()) {
								ret.getReWriteLD().add(map);
							}
							
						} catch (Exception e) {
							acm.setOrderCode(order.getOrderCode());
							acm.setResultCode(939301053);
							acm.setResultMessage(bInfo(939301053));
						}
						ret.getList().add(acm);

						if (acm.getResultCode() != SqlCommon.SuccessFlag) {
							ret.setResultCode(acm.getResultCode());
							ret.setResultMessage(acm.getResultMessage());
						}						
						
					}
				}

			}
		}

		return ret;
	}
	public ApiRollbackCouponResult CancelOrder(Order order,String operater) {
		ApiRollbackCouponResult ret = new ApiRollbackCouponResult();

		FlowBussinessService fs = new FlowBussinessService();

		MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",
				order.getOrderCode());

		if (dm != null) {
			String flowBussinessUid = dm.get("uid");
			String fromStatus = dm.get("order_status");
			String toStatus = "4497153900010006";
			String flowType = "449715390008";

			String userCode = "system";
			String remark = "auto by system";
			MDataMap md = new MDataMap();
			md.put("order_code", order.getOrderCode());
			
			RootResult rr = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,
					toStatus, StringUtils.isBlank(operater)?userCode:operater, remark, md);
			if (rr.getResultCode() == 1) {
				//促销系统回滚库存
				new PlusHelperNotice().onCancelIcOrder(order.getOrderCode(),order.getBuyerCode());
				// 加钱
				this.CancelOrderForStockAndMoney(order, order.getOrderCode());
				try {
					List<GiftVoucherInfo> reWriteLD = (new CouponUtil()).rollbackCoupon(order.getOrderCode());//优惠券回滚
					ret.setReWriteLD(reWriteLD);
					// 加积分
					this.CancelOrderForJiFen(order);
				} catch (Exception ex) {

					ex.printStackTrace();
				}

				//积分商城订单退货退活动库存
				for(OrderDetail orderDetail : order.getProductList()) {
					Map<String, Object> map = DbUp.upTable("oc_order_activity").dataSqlOne("select activity_code from oc_order_activity where order_code = '" + order.getOrderCode() + 
							"' and (activity_type = '4497472600010022' or activity_type = '4497472600010023') and sku_code = '" + orderDetail.getSkuCode() + 
							"' and product_code = '" + orderDetail.getProductCode() + "'", new MDataMap());
					if(map != null) {
						DbUp.upTable("fh_apphome_channel_details").upTemplate().update("update fh_apphome_channel_details set allow_count = allow_count + " + orderDetail.getSkuNum() + 
								" where uid = '" + MapUtils.getString(map, "activity_code", "") + "'", new HashMap<String, Object>());
					}
				}
				//此处新增取消订单定时任务。仅限APP自营品取消
				if(DbUp.upTable("fh_agent_order_detail").count("order_code", order.getOrderCode())>0 && DbUp.upTable("za_exectimer").count("exec_info",order.getOrderCode(),"exec_type","449746990029") <= 0) {
					JobExecHelper.createExecInfo("449746990029",  order.getOrderCode(), DateUtil.addMinute(5));//插入定时任务，五分钟后执行
				}				
				//判断 如果为渠道商订单 则加预存款
				if("449715190034".equals(order.getOrderSource())) {
					MDataMap orderChannel = DbUp.upTable("oc_order_channel").one("order_code", order.getOrderCode());
					MWebResult mWebResult = new PorscheOrderService().cancelOrderReturnAdvanceBalance(orderChannel.get("channel_seller_code"), order.getOrderMoney().toString());
					if(mWebResult.upFlagTrue()) {
						MDataMap one = DbUp.upTable("uc_channel_sellerinfo").one("channel_seller_code", orderChannel.get("channel_seller_code"));
						//记录日志
						String logRemark = "取消订单还原，第三方订单号：" + dm.get("out_order_code");
						new PorscheOrderService().insertChannelMoneyLog(orderChannel.get("channel_seller_code"), "449748420002", 
								dm.get("order_money"), one.get("advance_balance"), order.getOrderCode(), logRemark);
					}
				}
			} else {
				WebHelper.errorMessage(order.getOrderCode(), "addorder", 1,
						"CancelOrder on ChangeFlow", rr.getResultMessage(),
						null);
			}
		}

		return ret;
	}
	
	/**
	 * 商家取消订单
	 * @param order
	 * @return
	 */
	public RootResult cancelOrderByShop(String order_code,String operater) {
		RootResult ret = new RootResult();

		FlowBussinessService fs = new FlowBussinessService();

		MDataMap orderMap = DbUp.upTable("oc_orderinfo").one("order_code",order_code);
		
		if(orderMap==null||"4497153900010006".equals(orderMap.get("order_status"))||"4497153900010005".equals(orderMap.get("order_status"))){
			ret.setResultCode(939302001);
			ret.setResultMessage(bInfo(939302001, order_code));
			return ret;
		}
		
		String flowBussinessUid = orderMap.get("uid");
		String fromStatus = orderMap.get("order_status");
		String small_seller_code = orderMap.get("small_seller_code");
		String toStatus = "4497153900010006";
		String flowType = "449715390008";
//		ret = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,toStatus, small_seller_code, "update by shop", new MDataMap("order_code",order_code));
		ret = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,toStatus, operater, "update by shop", new MDataMap("order_code",order_code));
		
		if (ret.getResultCode() == 1) {
			
			//LD订单 走线下退款，不生成退款单
			if(MemberConst.MANAGE_CODE_HOMEHAS.equals(orderMap.get("small_seller_code"))){
				return ret;
			}
			
//			if(StringUtils.equals("SF03KJT", orderMap.get("small_seller_code"))){//此处坑，海外购不再生成退款单  规则不明确，现在手工生成退款单
			if(new PlusServiceSeller().isKJSeller(orderMap.get("small_seller_code"))){
				return ret;
			}
			
			//生成退款单
			CreateMoneyService createMoneyService = new CreateMoneyService();
			createMoneyService.creatReturnMoney(order_code);
			
		}else{
			WebHelper.errorMessage(order_code, "addorder", 1,"cancelOrderByShop on ChangeFlow", ret.getResultMessage(),null);
		}
		
		return ret;
	}
	
	
	/**
	 * 订单转失败，商家调用
	 * 
	 * @param order
	 * @return
	 */
	public RootResult CancelOrderForReturnMoney(Order order, String remark,
			String operator)

	{
		RootResult ret = new RootResult();

		FlowBussinessService fs = new FlowBussinessService();

		MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",
				order.getOrderCode());

		if (dm != null) {
			String flowBussinessUid = dm.get("uid");
			String fromStatus = dm.get("order_status");
			String toStatus = "4497153900010006";
			String flowType = "449715390008";

			String userCode = operator;

			MDataMap md = new MDataMap();
			md.put("order_code", order.getOrderCode());
			ret = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,
					toStatus, userCode, remark, md);
			if (ret.getResultCode() == 1) {
				// 加钱
				/*
				 * this.CancelOrderForStockAndMoney(order,
				 * order.getOrderCode()); try{ //加积分
				 * this.CancelOrderForJiFen(order); }catch(Exception ex){
				 * 
				 * ex.printStackTrace(); }
				 */

				// 创建退款单
				try {

				} catch (Exception e) {
					ret.setResultCode(939301096);
					ret.setResultMessage(bInfo(939301096));
				}

			} else {
				WebHelper.errorMessage(order.getOrderCode(), "order", 1,
						"CancelOrderForReturnMoney", ret.getResultMessage(),
						null);
			}
		}

		return ret;
	}

	/**
	 * 添加订单
	 * 
	 * @param orderCode
	 * @param order
	 * @param error
	 *            如果出错，返回具体错误内容,为必传项
	 * @return 如果正确，返回 1，否则，返回错误的编号
	 */
	public int AddOrder(String orderCode, Order order, StringBuffer error) {
		int errorCode = 939301000;

		boolean allreadysubProductStock = false;

		try {

			order.setOrderCode(orderCode);

			// 939301002=地址不能为空！
			if (order.getAddress() == null) {
				error.append(bInfo(939301002));
				errorCode = 939301002;
			} else {
				// 订单商品不能为空
				if (order.getProductList() == null
						|| order.getProductList().size() == 0) {
					error.append(bInfo(939301001));
					errorCode = 939301001;
				} else {
					RootResult rr = new RootResult();

					// 库存处理
					try {

						rr = this.AddOrderForStockAndMoney(order, orderCode);

					} catch (Exception ex) {
						rr.setResultCode(939301042);
						rr.setResultMessage(bInfo(939301042,
								order.getOrderCode()));
						WebHelper.errorMessage(orderCode, "addOrder", 1,
								"AddOrder-CancelOrderForStockAndMoney",
								ex.getMessage(), ex);
					}

					// 商品库存减少成功
					if (rr.getResultCode() == SqlCommon.SuccessFlag) {
						allreadysubProductStock = true;

						// 调用 添加订单的存储过程
						List<SqlParameter> params = new ArrayList<SqlParameter>();
						params.add(new SqlOutParameter("outFlag", Types.VARCHAR));
						params.add(new SqlOutParameter("error", Types.VARCHAR));
						params.add(new SqlParameter("orderCode", Types.VARCHAR));
						params.add(new SqlParameter("orderSource",
								Types.VARCHAR));
						params.add(new SqlParameter("orderType", Types.VARCHAR));
						params.add(new SqlParameter("orderStatus",
								Types.VARCHAR));
						params.add(new SqlParameter("sellerCode", Types.VARCHAR));
						params.add(new SqlParameter("buyerCode", Types.VARCHAR));
						params.add(new SqlParameter("payType", Types.VARCHAR));
						params.add(new SqlParameter("sendType", Types.VARCHAR));
						params.add(new SqlParameter("productMoney",
								Types.DECIMAL));
						params.add(new SqlParameter("transportMoney",
								Types.DECIMAL));
						params.add(new SqlParameter("promotionMoney",
								Types.DECIMAL));
						params.add(new SqlParameter("orderMoney", Types.DECIMAL));
						params.add(new SqlParameter("payedMoney", Types.DECIMAL));
						params.add(new SqlParameter("createTime", Types.VARCHAR));
						params.add(new SqlParameter("updateTime", Types.VARCHAR));
						params.add(new SqlParameter("areaCode", Types.VARCHAR));
						params.add(new SqlParameter("_address", Types.VARCHAR));
						params.add(new SqlParameter("postCode", Types.VARCHAR));
						params.add(new SqlParameter("_mobilephone",
								Types.VARCHAR));
						params.add(new SqlParameter("_telephone", Types.VARCHAR));
						params.add(new SqlParameter("receivePerson",
								Types.VARCHAR));
						params.add(new SqlParameter("_email", Types.VARCHAR));
						params.add(new SqlParameter("invoiceTitle",
								Types.VARCHAR));
						params.add(new SqlParameter("flagInvoice",
								Types.VARCHAR));
						params.add(new SqlParameter("_remark", Types.VARCHAR));
						params.add(new SqlParameter("detailStr", Types.VARCHAR));
						params.add(new SqlParameter("productsplit",
								Types.VARCHAR));
						params.add(new SqlParameter("itemsplit", Types.VARCHAR));
						CreateOrderCallableStatement cscc = new CreateOrderCallableStatement(
								order);

						DbTemplate dt = DbUp.upTable("oc_orderinfo")
								.upTemplate();
						Map<String, Object> outValues = dt.getJdbcOperations()
								.call(cscc, params);

						String returnCode = outValues.get("outFlag").toString();

						errorCode = Integer.parseInt(returnCode);
						// 订单处理成功
						if (errorCode == SqlCommon.SuccessFlag) {

							// 调用添加订单的日志。
							OrderStatusLogService osls = new OrderStatusLogService();
							OrderStatusLog log = new OrderStatusLog();
							log.setCode(orderCode);
							log.setInfo("");
//							log.setCreateUser("system");
							log.setCreateUser(order.getBuyerCode());
							log.setCreateTime(DateUtil.getSysDateTimeString());
							log.setOldStatus("");
							// 未处理
							log.setNowStatus(order.getOrderStatus());
							//
							osls.AddOrderStatusLogService(log);

						} else {// 订单处理失败

							if (error != null)
								error.append(bInfo(
										Integer.parseInt(returnCode), outValues
												.get("error").toString()));

							// 库存处理
							try {

								this.CancelOrderForStockAndMoney(order,
										orderCode);

							} catch (Exception ex) {
								WebHelper.errorMessage(orderCode, "addOrder",
										1,
										"AddOrder-CancelOrderForStockAndMoney",
										ex.getMessage(), ex);
							}
						}
					} else {
						errorCode = rr.getResultCode();
						error.append(rr.getResultMessage());
					}
				}
			}

		} catch (Exception e) {

			// 如果处理失败，
			if (allreadysubProductStock) {
				// 调用商品中心的库存处理函数 - 加库存
				// 库存处理
				try {

					this.CancelOrderForStockAndMoney(order, orderCode);

				} catch (Exception ex) {
					WebHelper.errorMessage(orderCode, "addOrder", 1,
							"AddOrder-CancelOrderForStockAndMoney",
							ex.getMessage(), ex);
				}
			}

			if (error != null)
				error.append(bInfo(errorCode));
			return errorCode;
		}

		return errorCode;
	}

	/**
	 * 订单发货
	 * 
	 * @param oos
	 * @param sellerOrder
	 * @return
	 */
	public RootResult shipmentForOrderAndSellerOrder(OcOrderShipments oos,
			String sellerOrder) {

		RootResult rr = new RootResult();

		MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",
				oos.getOrderCode(), "seller_code", sellerOrder);

		if (dm == null) {
			rr.setResultCode(939301031);
			rr.setResultMessage(bInfo(939301031));
		} else {

			MDataMap mDataMap = new MDataMap();
			mDataMap.put("order_code", oos.getOrderCode());

			FlowBussinessService fs = new FlowBussinessService();

			String flowBussinessUid = dm.get("uid");
			String fromStatus = dm.get("order_status");
			String toStatus = "4497153900010003";
			String flowType = "449715390001";

			MUserInfo userInfo = null;
			String manageCode = "";

			try {
				if (UserFactory.INSTANCE != null) {
					userInfo = UserFactory.INSTANCE.create();
					manageCode = userInfo.getManageCode();
				}
			} catch (Exception e) {
				manageCode = "system";
			}

			String remark = oos.getRemark();
			MDataMap md = new MDataMap();
			md.put("order_code", oos.getOrderCode());
			rr = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,
					toStatus, manageCode, remark, md);

			if (rr.getResultCode() == 1) {
				try {
					UUID uuid = UUID.randomUUID();

					MDataMap insertDatamap = new MDataMap();

					insertDatamap.put("uid", uuid.toString().replace("-", ""));
					insertDatamap.put("order_code", oos.getOrderCode());
					insertDatamap.put("logisticse_code",
							oos.getLogisticseCode());
					insertDatamap.put("logisticse_name",
							oos.getLogisticseName());
					insertDatamap.put("waybill", oos.getWaybill());
					insertDatamap.put("creator", manageCode);
					insertDatamap.put("create_time",
							DateUtil.getSysDateTimeString());
					insertDatamap.put("remark", oos.getRemark());
					DbUp.upTable("oc_order_shipments")
							.dataInsert(insertDatamap);
					
					//判断订单来源 若为多彩宝订单 则调用多彩宝下单通知接口
					if(dm.containsKey("order_source") && "449715190014".equals(dm.get("order_source"))){
						ApiOrderShipmentsNoticInput inputParam = new ApiOrderShipmentsNoticInput();
						inputParam.setJyOrderCode(oos.getOrderCode());
						inputParam.setLogisticseCode(oos.getLogisticseCode());
						inputParam.setLogisticseName(oos.getLogisticseName());
						inputParam.setWaybill(oos.getWaybill());
						inputParam.setDeliveryTime(DateUtil.getSysDateTimeString());
						new ApiOrderShipmentsNotic().Process(inputParam, new MDataMap());
					}
				} catch (Exception ex) {
					bLogError(939301006, oos.getOrderCode());
				}
			}
		}
		return rr;
	}

	/**
	 * 订单发货
	 * 
	 * @param oos
	 * @return
	 */
	public RootResult shipmentForOrder(OcOrderShipments oos) {

		RootResult rr = new RootResult();

		MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code", oos.getOrderCode());

		if (dm == null) {
			rr.setResultCode(939301031);
			rr.setResultMessage(bInfo(939301031));
		} else {

			MDataMap mDataMap = new MDataMap();
			mDataMap.put("order_code", oos.getOrderCode());

			FlowBussinessService fs = new FlowBussinessService();

			String flowBussinessUid = dm.get("uid");
			String fromStatus = dm.get("order_status");
			String toStatus = "4497153900010003";
			String flowType = "449715390001";

			MUserInfo userInfo = null;
			String manageCode = "";

			try {
				if (UserFactory.INSTANCE != null) {
					userInfo = UserFactory.INSTANCE.create();
//					manageCode = userInfo.getManageCode();
					manageCode = userInfo.getUserCode();
				}
			} catch (Exception e) {
				manageCode = "system";
			}

			String remark = oos.getRemark();
			MDataMap md = new MDataMap();
			md.put("order_code", oos.getOrderCode());
			md.put("manage_code", manageCode);
			rr = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus, toStatus, manageCode, remark, md);

			if (rr.getResultCode() == 1) {
				try {
					UUID uuid = UUID.randomUUID();

					MDataMap insertDatamap = new MDataMap();

					insertDatamap.put("uid", uuid.toString().replace("-", ""));
					insertDatamap.put("order_code", oos.getOrderCode());
					insertDatamap.put("logisticse_code", oos.getLogisticseCode());
					insertDatamap.put("logisticse_name", oos.getLogisticseName());
					insertDatamap.put("waybill", oos.getWaybill());
					insertDatamap.put("creator", manageCode);
					insertDatamap.put("create_time", DateUtil.getSysDateTimeString());
					insertDatamap.put("remark", oos.getRemark());
					DbUp.upTable("oc_order_shipments").dataInsert(insertDatamap);
					
					//判断订单来源 若为多彩宝订单 则调用多彩宝下单通知接口
					if(dm.containsKey("order_source") && "449715190014".equals(dm.get("order_source"))){
						ApiOrderShipmentsNoticInput inputParam = new ApiOrderShipmentsNoticInput();
						inputParam.setJyOrderCode(oos.getOrderCode());
						inputParam.setLogisticseCode(oos.getLogisticseCode());
						inputParam.setLogisticseName(oos.getLogisticseName());
						inputParam.setWaybill(oos.getWaybill());
						inputParam.setDeliveryTime(DateUtil.getSysDateTimeString());
						new ApiOrderShipmentsNotic().Process(inputParam, new MDataMap());
					}
				} catch (Exception ex) {
					bLogError(939301006, oos.getOrderCode());
				}
			}
		}
		return rr;
	}
	
	/**
	 * 支付成功之后返回的页面
	 * 
	 * @param pay
	 * @return
	 */
	public RootResult paySucess(OcOrderPay pay) {

		RootResult rr = new RootResult();

		if (pay == null) {
			rr.setResultCode(939301030);
			rr.setResultMessage(bInfo(939301030));
		} else if (pay.getPayedMoney() <= 0) {
			rr.setResultCode(939301032);
			rr.setResultMessage(bInfo(939301032));
		} else {

			MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",
					pay.getOrderCode());

			if (dm == null) {
				rr.setResultCode(939301031);
				rr.setResultMessage(bInfo(939301031));
			} else {

				float orderMoney = Float.parseFloat(dm.get("order_money"));
				float payedMoney = Float.parseFloat(dm.get("payed_money"));
				float dueMoney = Float.parseFloat(dm.get("due_money"));
				MDataMap mDataMap = new MDataMap();

				mDataMap.put("order_code", pay.getOrderCode());
				mDataMap.put("payed_money", String.valueOf(pay.getPayedMoney()));
				mDataMap.put("uid", dm.get("uid"));

				// 流转时使用，重要
				mDataMap.put("isFK", "1");
				// 流转时使用，重要

				if ((payedMoney + pay.getPayedMoney()) == orderMoney) {

					FlowBussinessService fs = new FlowBussinessService();

					String flowBussinessUid = dm.get("uid");
					String fromStatus = dm.get("order_status");
					String toStatus = "4497153900010002";
					String flowType = "449715390008";

					String remark = pay.getPayRemark();

					RootResult ret = fs.ChangeFlow(flowBussinessUid, flowType,
							fromStatus, toStatus, "system", remark, mDataMap);

					if (ret.getResultCode() == 1) {

						try {
							UUID uuid = UUID.randomUUID();

							MDataMap insertDatamap = new MDataMap();

							insertDatamap.put("uid",
									uuid.toString().replace("-", ""));
							insertDatamap.put("order_code", pay.getOrderCode());
							insertDatamap.put("pay_sequenceid",
									pay.getPaySequenceid());
							insertDatamap.put("create_time",
									DateUtil.getSysDateTimeString());
							insertDatamap.put("payed_money",
									String.valueOf(dm.get("due_money")));
							insertDatamap.put("pay_type", pay.getPayType());
							insertDatamap.put("pay_remark", pay.getPayRemark());
							insertDatamap.put("merchant_id",
									pay.getMerchantId());
							DbUp.upTable("oc_order_pay").dataInsert(
									insertDatamap);
						} catch (Exception ex) {
							WebHelper.errorMessage("payForOrder", "paySuccess",
									1, "orderSevicepaySucess",
									"paySucess:" + pay.getOrderCode() + ":"
											+ pay.getPayedMoney(), ex);
						}
					} else {
						return ret;
					}
				} else {
					rr.setResultCode(939301039);
					rr.setResultMessage(bInfo(939301039));
				}
			}
		}

		return rr;
	}

	/**
	 * @param input
	 *            1 取消 2 发货 3 确认收货-处理到完成
	 */
	public RootResult operate(ApiOperateInput input,String operater) {

		RootResult rr = new RootResult();

		if (input.getOrderCode() == null || input.getOrderCode().equals("")) {
			rr.setResultMessage(bInfo(939301031));
			rr.setResultCode(939301031);
		} else {
			if (input.getType() == 1) {
				Order order = this.getOrder(input.getOrderCode());

				if (order == null) {
					rr.setResultMessage(bInfo(939301031));
					rr.setResultCode(939301031);
				} else {
					return this.CancelOrder(order);
				}

			} else if (input.getType() == 2) {

				if (input.getOos() == null) {
					rr.setResultMessage(bInfo(939301079));
					rr.setResultCode(939301079);
				} else {
					return this.shipmentForOrder(input.getOos());
				}
			} else if (input.getType() == 3) {
				
				return this.changForRecieveByUser(input.getOrderCode(),operater);
			}
		}

		return rr;

	}

	/**
	 * 客户收货的时候改变
	 * 
	 * @param orderCode
	 * @return
	 */
	public RootResult changForRecieveByUser(String orderCode,String operater) {
		RootResult rr = new RootResult();
		if (orderCode == null || orderCode.equals("")) {
			rr.setResultCode(939301031);
			rr.setResultMessage(bInfo(939301031));
		} else {
			MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",
					orderCode);

			if (dm == null) {
				rr.setResultCode(939301031);
				rr.setResultMessage(bInfo(939301031));
			} else {

				FlowBussinessService fs = new FlowBussinessService();

				String flowBussinessUid = dm.get("uid");
				String fromStatus = dm.get("order_status");
				String toStatus = "4497153900010005";
				String flowType = "449715390008";
				String userCode = "system";
				String remark = "auto by system";
				MDataMap md = new MDataMap();
				md.put("order_code", orderCode);
				rr = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,
						toStatus,StringUtils.isBlank(operater)? userCode:operater, remark, md);
				   
				//做分销人金额信息统计更新
				updateDistributionInfos(orderCode);
				/**
				 * 确认收货修改   赠送的优惠券的状态
				 */
				if(rr.getResultCode() == 1) {
					//判断该订单的大大订单下的小单是否全部收货。如果全部收货则   修改下单送券的优惠券状态
					MDataMap mDataMap = new MDataMap();
					mDataMap.put("big_order_code", dm.get("big_order_code"));
					
					int dataCount = DbUp.upTable("oc_orderinfo").dataCount(" big_order_code =:big_order_code AND order_status != '4497153900010005' ", mDataMap);
					if(dataCount <= 0) {//全部确认收货
						String sSql = "  UPDATE ordercenter.oc_coupon_info SET blocked = 0 WHERE big_order_code = :big_order_code  ";
						DbUp.upTable("oc_coupon_info").dataExec(sSql, mDataMap);
					}
				}
				
			}
		}

		return rr;
	}
	
	private void updateDistributionInfos(String order_code) {
		// TODO Auto-generated method stub
		MDataMap one = DbUp.upTable("oc_orderdetail").one("order_code",order_code);
		String distribution_member_id = one.get("distribution_member_id");
		if(one!=null&&StringUtils.isNotBlank(distribution_member_id)) {
		    BigDecimal sku_price =BigDecimal.valueOf(Double.parseDouble(one.get("sku_price").toString()));
		    String sku_num = one.get("sku_num").toString();
			Map<String, Object> dataSqlOne = DbUp.upTable("oc_distribution_info").dataSqlOne("select * from oc_distribution_info where distribution_member_id=:distribution_member_id", new MDataMap("distribution_member_id",distribution_member_id));
		    if(dataSqlOne!=null) {
		    	Integer  order_success_num = Integer.parseInt(dataSqlOne.get("order_success_num").toString());
				BigDecimal order_success_value = BigDecimal.valueOf(Double.parseDouble(dataSqlOne.get("order_success_value").toString()));
				order_success_num = order_success_num+1;
				BigDecimal allSkuPrice = sku_price.multiply(BigDecimal.valueOf(Double.parseDouble(sku_num)));
				order_success_value=order_success_value.add(allSkuPrice);
				order_success_value.setScale(2,BigDecimal.ROUND_HALF_DOWN);
				DbUp.upTable("oc_distribution_info").dataUpdate(new MDataMap("order_success_num",order_success_num.toString(),"order_success_value",order_success_value.toString(),"distribution_member_id",distribution_member_id), "order_success_num,order_success_value", "distribution_member_id");
		    
		    }
		}
	}
	public void autoChangToSuccessFor15Days() {

		MDataMap mapParam = new MDataMap();

		mapParam.put("order_status", "4497153900010003");
		mapParam.put("seller_code", MemberConst.MANAGE_CODE_HOMEHAS);
		mapParam.put("seller_code2", MemberConst.MANAGE_CODE_SPDOG);
//		涉及订单范围：LD订单、商户订单、跨境通订单、沙皮狗自营订单
		List<MDataMap> listMap = DbUp
				.upTable("oc_orderinfo")
				.query("uid,order_code,order_status,big_order_code",
						"create_time desc",
						"order_status=:order_status and (seller_code=:seller_code or seller_code=:seller_code2) and (UNIX_TIMESTAMP()-UNIX_TIMESTAMP(update_time))>1296000",
						//更新为20天
//						"order_status=:order_status and (seller_code=:seller_code or seller_code=:seller_code2) and (UNIX_TIMESTAMP()-UNIX_TIMESTAMP(update_time))>1728000",
						mapParam, -1, -1);

		if (listMap != null) {
			for (MDataMap mdm : listMap) {

				FlowBussinessService fs = new FlowBussinessService();

				String flowBussinessUid = mdm.get("uid");
				String fromStatus = mdm.get("order_status");
				String toStatus = "4497153900010005";
				String flowType = "449715390008";

				String userCode = "system";
				String remark = "auto by system";
				MDataMap md = new MDataMap();
				md.put("order_code", mdm.get("order_code"));
				try {
					RootResult rr = fs.ChangeFlow(flowBussinessUid, flowType,
							fromStatus, toStatus, userCode, remark, md);

					if (rr.getResultCode() != 1) {
					//	System.out.println(rr.getResultMessage());
					}
					
					/**
					 * 确认收货修改   赠送的优惠券的状态
					 */
					if(rr.getResultCode() == 1) {
						//判断该订单的大大订单下的小单是否全部收货。如果全部收货则   修改下单送券的优惠券状态
						MDataMap mDataMap = new MDataMap();
						mDataMap.put("big_order_code", mdm.get("big_order_code"));
						
						int dataCount = DbUp.upTable("oc_orderinfo").dataCount(" big_order_code =:big_order_code and order_source not in('449715190014','449715190037') AND order_status != '4497153900010005' ", mDataMap);
						if(dataCount <= 0) {//全部确认收货
							String sSql = "  UPDATE ordercenter.oc_coupon_info SET blocked = 0 WHERE big_order_code = :big_order_code  ";
							DbUp.upTable("oc_coupon_info").dataExec(sSql, mDataMap);
						}
						
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				
				
			}
		}
	}
	

	/**
	 * 订单如果14天未确认收货，则系统自动收货
	 */
	public void autoChangToSuccessFor14Days() {

		List<Order> listOrder = new ArrayList<Order>();
		MDataMap mapParam = new MDataMap();

		mapParam.put("order_status", "4497153900010003");
		mapParam.put("seller_code", "SI2003");

		List<MDataMap> listMap = DbUp
				.upTable("oc_orderinfo")
				.query("uid,order_code,order_status,seller_code",
						"create_time desc",
						"order_status=:order_status and order_source not in('449715190014','449715190037') and seller_code=:seller_code and (UNIX_TIMESTAMP()-UNIX_TIMESTAMP(create_time))>1209600 ",
						mapParam, -1, -1);

		if (listMap != null) {
			for (MDataMap mdm : listMap) {

				String seller_code = mdm.get("seller_code");
				if(MemberConst.MANAGE_CODE_HOMEHAS.equals(seller_code)){
					continue;
				}
				
				FlowBussinessService fs = new FlowBussinessService();

				String flowBussinessUid = mdm.get("uid");
				String fromStatus = mdm.get("order_status");
				String toStatus = "4497153900010005";
				String flowType = "449715390008";

				String userCode = "system";
				String remark = "auto by system";
				MDataMap md = new MDataMap();
				md.put("order_code", mdm.get("order_code"));
				try {
					RootResult rr = fs.ChangeFlow(flowBussinessUid, flowType,
							fromStatus, toStatus, userCode, remark, md);

					if (rr.getResultCode() != 1) {
					//	System.out.println(rr.getResultMessage());
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 订单如果24小时未付款，则自动关闭,订单自动处理到交易关闭
	 */
	public void autoChangeToOverFor24hour() {

		List<Order> listOrder = new ArrayList<Order>();
		MDataMap mapParam = new MDataMap();

		mapParam.put("order_status", "4497153900010001");

		List<MDataMap> listMap = DbUp
				.upTable("oc_orderinfo")
				.query("order_code",
						"create_time desc",
						"order_status=:order_status and order_source not in('449715190014','449715190037') and (UNIX_TIMESTAMP()-UNIX_TIMESTAMP(create_time))>86400",
						mapParam, -1, -1);

		if (listMap != null) {
			for (MDataMap mdm : listMap) {
				String orderCode = mdm.get("order_code");
				SerializeSupport ss = new SerializeSupport<Order>();
				Order pic = new Order();
				ss.serialize(mdm, pic);
				listOrder.add(pic);
			}
		}

		if (listOrder != null) {

			for (Order md : listOrder) {
				try {
					// 反礼品卡时用到
					md.setOcOrderPayList(this.getOrderPayListByOrderCode(md
							.getOrderCode()));
					// 反库存时使用
					md.setProductList(this.getOrderDetaiListByOrderCode(md
							.getOrderCode()));
					ApiRollbackCouponResult rr = this.CancelOrder(md);
					List<GiftVoucherInfo> reWriteLD = rr.getReWriteLD();
					if(reWriteLD != null && reWriteLD.size() > 0) {
						//回写礼金券
						new OrderService().reWriteGiftVoucherToLD(reWriteLD);
					}
				} catch (Exception e) {
					WebHelper.errorMessage(md.getOrderCode(), "cacelOrder", 1,
							"autoChangeToOverFor24hour", "自动关闭订单发生异常", e);
				}
			}
		}
	}
	
	/**
	 * 取消订单/取消发货回写礼金券给LD
	 * @param reWriteLD
	 */
	public void reWriteGiftVoucherToLD(List<GiftVoucherInfo> reWriteLD) {
		if(reWriteLD != null && reWriteLD.size() > 0) {
			List<com.srnpr.xmassystem.invoke.ref.model.GiftVoucherInfo> param = new ArrayList<com.srnpr.xmassystem.invoke.ref.model.GiftVoucherInfo>();
			for(GiftVoucherInfo info : reWriteLD) {
				com.srnpr.xmassystem.invoke.ref.model.GiftVoucherInfo copy = new com.srnpr.xmassystem.invoke.ref.model.GiftVoucherInfo();
				copy.setHjy_ord_id(info.getHjy_ord_id());
				copy.setLj_code(info.getLj_code());
				param.add(copy);
			}
			new PlusServiceLjq().reWriteGiftVoucherToLD(param);
		}
	}

	/**
	 * 获取订单
	 * 
	 * @param orderCode
	 * @return
	 */
	public Order getOrder(String orderCode) {

		Order ret = null;

		MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code", orderCode);

		if (dm != null) {

			SerializeSupport ss = new SerializeSupport<Order>();
			Order pic = new Order();
			ss.serialize(dm, pic);
			ret = pic;

			ret.setAddress(this.getOrderAddressByOrderCode(orderCode));
			ret.setActivityList(this.getOrderActivityListByOrderCode(orderCode));
			ret.setOcOrderPayList(this.getOrderPayListByOrderCode(orderCode));
			ret.setProductList(this.getOrderDetaiListByOrderCode(orderCode));
			ret.setOcorderShipments(this
					.getOcOrderShipmentsByOrderCode(orderCode));
			ret.setExpressList(this.getExpressList(orderCode));

		}

		return ret;

	}

	/**
	 * 获取订单
	 * 
	 * @param orderCode
	 * @param buyerCode
	 * @return
	 */
	public Order getOrderByBuyer(String orderCode, String buyerCode) {

		Order ret = null;

		MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code", orderCode,
				"buyer_code", buyerCode);

		if (dm != null) {

			SerializeSupport ss = new SerializeSupport<Order>();
			Order pic = new Order();
			ss.serialize(dm, pic);
			ret = pic;

			ret.setAddress(this.getOrderAddressByOrderCode(orderCode));
			ret.setActivityList(this.getOrderActivityListByOrderCode(orderCode));
			ret.setOcOrderPayList(this.getOrderPayListByOrderCode(orderCode));
			ret.setProductList(this.getOrderDetaiListByOrderCode(orderCode));
			ret.setExpressList(this.getExpressList(orderCode));

			MDataMap dm1 = DbUp.upTable("uc_sellerinfo").one("seller_code",
					pic.getSellerCode());
			if (dm1 != null) {
				UcSellerInfo mpic = new UcSellerInfo();
				mpic.setSellerCode(dm1.get("seller_code"));
				mpic.setSellerName(dm1.get("seller_name"));
				mpic.setSellerPic(dm1.get("seller_pic"));
				pic.setSellerInfo(mpic);
			}
		}

		return ret;

	}

	/**
	 * 获取多个订单
	 * 
	 * @param buyerCode
	 * @param orders
	 * @return
	 */
	public List<Order> getMultiOrdersList(String buyerCode, String orders) {
		List<Order> ret = new ArrayList<Order>();

		if (buyerCode == null || orders == null)
			return ret;

		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyerCode);

		String[] ary = orders.split(",");

		String whereStr = "";

		for (int i = 0; i < ary.length; i++) {

			if (!ary[i].equals("")) {
				mapParam.put("order_code" + i, ary[i]);
				whereStr += " order_code=:order_code" + i + " or";
			}
		}

		String orderWhereStr = "buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and order_status<>'4497153900010007'";

		if (whereStr.length() > 2) {
			orderWhereStr += " and ("
					+ whereStr.substring(0, whereStr.length() - 2) + ")";
		} else {
			return ret;
		}

		List<MDataMap> listMap = DbUp.upTable("oc_orderinfo").query("",
				"create_time desc", orderWhereStr, mapParam, -1, -1);

		if (listMap != null) {

			int size = listMap.size();
			SerializeSupport ss = new SerializeSupport<Order>();

			whereStr = "";
			MDataMap urMapParam = new MDataMap();

			for (int j = 0; j < size; j++) {
				Order pic = new Order();
				ss.serialize(listMap.get(j), pic);
				ret.add(pic);

				pic.setProductList(this.getOrderDetaiListByOrderCode(pic
						.getOrderCode()));

				if (!pic.getSellerCode().equals("")) {
					urMapParam.put("seller_code" + j, pic.getSellerCode());
					whereStr += " seller_code=:seller_code" + j + " or";
				}

				pic.setAddress(this.getOrderAddressByOrderCode(pic
						.getOrderCode()));
				pic.setOcorderShipments(this.getOcOrderShipmentsByOrderCode(pic
						.getOrderCode()));
				pic.setExpressList(this.getExpressList(pic.getOrderCode()));
			}

			if (whereStr.length() > 2) {
				whereStr = whereStr.substring(0, whereStr.length() - 2);
				List<MDataMap> pListMap = DbUp.upTable("uc_sellerinfo").query(
						"seller_code,seller_name,seller_pic", "", whereStr,
						urMapParam, -1, -1);
				size = pListMap.size();

				// List<UcSellerInfo> ucsiList = new ArrayList<UcSellerInfo>();

				for (int j = 0; j < size; j++) {

					UcSellerInfo pic = new UcSellerInfo();

					pic.setSellerCode(pListMap.get(j).get("seller_code"));
					pic.setSellerName(pListMap.get(j).get("seller_name"));
					pic.setSellerPic(pListMap.get(j).get("seller_pic"));

					// ucsiList.add(pic);

					for (Order or : ret) {
						if (or.getSellerCode().equals(pic.getSellerCode())) {
							or.setSellerInfo(pic);
						}
					}
				}
			}
		}
		return ret;
	}

	/**
	 * 获取订单列表，只有订单基本信息，请注意
	 * 
	 * @param sellerCode
	 * @param type
	 *            1 查询店铺已支付的且未发送到第三方的订单-4497153900010002, 2 查询店铺支付后取消的订单
	 *            4497153900010006, 3 查询店铺完成的订单 4497153900010005
	 * @return
	 */
	public List<Order> getOrderListBySellerCode(String sellerCode, int type) {
		List<Order> ret = new ArrayList<Order>();

		MDataMap mapParam = new MDataMap();
		mapParam.put("seller_code", sellerCode);

		String orderStatus = "";

		if (type == 1) {
			orderStatus = "4497153900010002";
		} else if (type == 2) {
			orderStatus = "4497153900010006";
		} else if (type == 3) {
			orderStatus = "4497153900010005";
		}

		mapParam.put("order_status", orderStatus);

		String orderWhereStr = "seller_code=:seller_code and order_source not in('449715190014','449715190037') and order_status<>'4497153900010007' and order_status=:order_status";

		List<MDataMap> listMap = DbUp.upTable("oc_orderinfo").query("",
				"create_time desc", orderWhereStr, mapParam, -1, -1);

		if (listMap != null) {

			int size = listMap.size();

			for (int j = 0; j < size; j++) {
				ret.add(this.getOrder(listMap.get(j).get("order_code")));
			}
		}
		return ret;
	}

	/**
	 * 获取订单
	 * 
	 * @param sellerCode
	 *            店铺编号
	 * @param fromTime
	 *            从时间 默认 是当前-前三天
	 * @param orderStatus
	 *            4497153900010001 下单成功-未付款 4497153900010002 下单成功-未发货
	 *            4497153900010003 已发货 4497153900010004 已收货 4497153900010005
	 *            交易成功 4497153900010006 交易失败
	 * @return
	 */
	public List<Order> getOrderListBySellerCode(String sellerCode,
			String fromTime, String orderStatus) {
		List<Order> ret = new ArrayList<Order>();

		MDataMap mapParam = new MDataMap();
		mapParam.put("seller_code", sellerCode);

		if (fromTime == null || fromTime.equals("")) {
			fromTime = DateUtil.getTimeCompareSomeDay(-3);
		}

		mapParam.put("create_time", fromTime);
		mapParam.put("order_status", orderStatus);
		String orderWhereStr = "seller_code=:seller_code and order_source not in('449715190014','449715190037') and order_status<>'4497153900010007' and order_status=:order_status and create_time>=:create_time AND NOT EXISTS (SELECT transfer_order_id FROM oc_order_transfer WHERE transfer_order_id= order_code and transfer_order_status='Y')";

		List<MDataMap> listMap = DbUp.upTable("oc_orderinfo").query("",
				"create_time desc", orderWhereStr, mapParam, -1, -1);

		if (listMap != null) {

			int size = listMap.size();

			for (int j = 0; j < size; j++) {
				ret.add(this.getOrder(listMap.get(j).get("order_code")));
			}
		}
		return ret;
	}

	/**
	 * 获取订单
	 * 
	 * @param sellerCode
	 *            店铺编号
	 * @param fromTime
	 *            开始时间
	 * @param toTime
	 *            结束时间
	 * @param orderStatus
	 *            4497153900010001 下单成功-未付款 4497153900010002 下单成功-未发货
	 *            4497153900010003 已发货 4497153900010004 已收货 4497153900010005
	 *            交易成功 4497153900010006 交易失败
	 * @return
	 */
	public List<Order> getOrderListBySellerCode(String sellerCode,
			String fromTime, String toTime, String orderStatus) {
		List<Order> ret = new ArrayList<Order>();

		MDataMap mapParam = new MDataMap();
		mapParam.put("seller_code", sellerCode);

		mapParam.put("create_time", fromTime);
		mapParam.put("create_timeto", toTime);
		mapParam.put("order_status", orderStatus);
		String orderWhereStr = "seller_code=:seller_code and order_source not in('449715190014','449715190037') and order_status<>'4497153900010007' and order_status=:order_status and create_time>=:create_time and create_time<=:create_timeto ";

		List<MDataMap> listMap = DbUp.upTable("oc_orderinfo").query("",
				"create_time desc", orderWhereStr, mapParam, -1, -1);

		if (listMap != null) {

			int size = listMap.size();

			for (int j = 0; j < size; j++) {
				ret.add(this.getOrder(listMap.get(j).get("order_code")));
			}
		}
		return ret;
	}

	/**
	 * 获取订单列表，只有订单基本信息，请注意
	 * 
	 * @param buyerCode
	 * @fromTime 从什么时间开始
	 * @return
	 */
	public List<Order> getOrderList(String buyerCode, String fromTime,
			String orderStatus, String orderType, String orderChannel) {
		List<Order> ret = new ArrayList<Order>();

		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyerCode);

		String orderWhereStr = "buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and order_status<>'4497153900010007'  and delete_flag='0' ";

		if (fromTime != null && !fromTime.equals("")) {
			mapParam.put("create_time", fromTime);
			orderWhereStr += " and create_time>=:create_time";
		}

		if (orderStatus != null && !orderStatus.equals("")) {
			mapParam.put("order_status", orderStatus);
			orderWhereStr += " and order_status=:order_status";
		}

		if (orderType != null && !orderType.equals("")) {
			mapParam.put("order_type", orderType);
			orderWhereStr += " and order_type=:order_type";
		}

		if (orderChannel != null && !orderChannel.equals("")) {
			mapParam.put("order_channel", orderChannel);
			orderWhereStr += " and order_channel=:order_channel";
		}

		List<MDataMap> listMap = DbUp.upTable("oc_orderinfo").query("",
				"create_time desc", orderWhereStr, mapParam, -1, -1);

		if (listMap != null) {

			int size = listMap.size();
			SerializeSupport ss = new SerializeSupport<Order>();

			String whereStr = "";
			MDataMap urMapParam = new MDataMap();

			// String whereOrderStr = "";
			// MDataMap urOrderParam = new MDataMap();

			for (int j = 0; j < size; j++) {
				Order pic = new Order();
				ss.serialize(listMap.get(j), pic);
				ret.add(pic);

				pic.setProductList(this.getOrderDetaiListByOrderCode(pic
						.getOrderCode()));

				if (!pic.getSellerCode().equals("")) {
					urMapParam.put("seller_code" + j, pic.getSellerCode());
					whereStr += " seller_code=:seller_code" + j + " or";

					// urOrderParam.put("order_code" + j, pic.getOrderCode());
					// whereOrderStr += " order_code=:order_code" + j + " or";
				}

				pic.setAddress(this.getOrderAddressByOrderCode(pic
						.getOrderCode()));
				pic.setOcorderShipments(this.getOcOrderShipmentsByOrderCode(pic
						.getOrderCode()));
				pic.setExpressList(this.getExpressList(pic.getOrderCode()));
			}

			if (whereStr.length() > 2) {
				whereStr = whereStr.substring(0, whereStr.length() - 2);
				List<MDataMap> pListMap = DbUp.upTable("uc_sellerinfo").query(
						"seller_code,seller_name,seller_pic", "", whereStr,
						urMapParam, -1, -1);
				size = pListMap.size();

				// List<UcSellerInfo> ucsiList = new ArrayList<UcSellerInfo>();

				for (int j = 0; j < size; j++) {

					UcSellerInfo pic = new UcSellerInfo();

					pic.setSellerCode(pListMap.get(j).get("seller_code"));
					pic.setSellerName(pListMap.get(j).get("seller_name"));
					pic.setSellerPic(pListMap.get(j).get("seller_pic"));

					// ucsiList.add(pic);

					for (Order or : ret) {
						if (or.getSellerCode().equals(pic.getSellerCode())) {
							or.setSellerInfo(pic);
						}
					}
				}

			}
		}
		return ret;
	}

	private List<Express> getExpressList(String orderCode) {
		List<Express> ret = new ArrayList<Express>();
		MDataMap mapParam = new MDataMap();
		mapParam.put("order_code", orderCode);
		List<MDataMap> listMap = DbUp.upTable("oc_express_detail").query("",
				"time asc", "order_code=:order_code", mapParam, -1, -1);

		int size = listMap.size();
		SerializeSupport ss = new SerializeSupport<Express>();

		MDataMap express = new MDataMap();

		for (int j = 0; j < size; j++) {
			Express pic = new Express();
			ss.serialize(listMap.get(j), pic);
			ret.add(pic);

			if (express.containsKey(pic.getWaybill())) {
				pic.setLogisticseName(express.get(pic.getWaybill()));
			} else {
				MDataMap mdOne = DbUp.upTable("oc_order_shipments").one(
						"order_code", orderCode, "waybill", pic.getWaybill());
				if (mdOne != null) {
					pic.setLogisticseName(mdOne.get("logisticse_name"));
				}
				express.put(pic.getWaybill(), pic.getLogisticseName());
			}

		}

		return ret;
	}

	/**
	 * 获取订单地址通过商品编号
	 * 
	 * @param orderCode
	 * @return
	 */
	private OrderAddress getOrderAddressByOrderCode(String orderCode) {

		OrderAddress address = null;

		MDataMap dm = DbUp.upTable("oc_orderadress").one("order_code",
				orderCode);

		if (dm != null) {

			SerializeSupport ss = new SerializeSupport<OrderAddress>();
			OrderAddress pic = new OrderAddress();
			ss.serialize(dm, pic);
			address = pic;

		}

		return address;
	}

	/**
	 * 获取订单的明细,通过商品编号
	 * 
	 * @param orderCode
	 * @return
	 */
	private List<OrderDetail> getOrderDetaiListByOrderCode(String orderCode) {
		List<OrderDetail> ret = new ArrayList<OrderDetail>();

		MDataMap mapParam = new MDataMap();
		mapParam.put("order_code", orderCode);

		List<MDataMap> listMap = DbUp.upTable("oc_orderdetail").query("", "",
				"order_code=:order_code", mapParam, -1, -1);

		if (listMap != null) {

			int size = listMap.size();
			SerializeSupport ss = new SerializeSupport<OrderDetail>();

			for (int j = 0; j < size; j++) {
				OrderDetail pic = new OrderDetail();
				ss.serialize(listMap.get(j), pic);
				ret.add(pic);
			}
		}

		return ret;
	}

	/**
	 * 获取订单的活动list，通过商品编号
	 * 
	 * @param orderCode
	 * @return
	 */
	private List<OcOrderActivity> getOrderActivityListByOrderCode(
			String orderCode) {
		List<OcOrderActivity> ret = new ArrayList<OcOrderActivity>();

		MDataMap mapParam = new MDataMap();
		mapParam.put("order_code", orderCode);

		List<MDataMap> listMap = DbUp.upTable("oc_order_activity").query("",
				"", "order_code=:order_code", mapParam, -1, -1);

		if (listMap != null) {

			int size = listMap.size();
			SerializeSupport ss = new SerializeSupport<OcOrderActivity>();

			for (int j = 0; j < size; j++) {
				OcOrderActivity pic = new OcOrderActivity();
				ss.serialize(listMap.get(j), pic);
				ret.add(pic);
			}
		}
		return ret;
	}

	/**
	 * 获取订单的支付list，通过商品编号
	 * 
	 * @param orderCode
	 * @return
	 */
	private List<OcOrderPay> getOrderPayListByOrderCode(String orderCode) {
		List<OcOrderPay> ret = new ArrayList<OcOrderPay>();

		MDataMap mapParam = new MDataMap();
		mapParam.put("order_code", orderCode);

		List<MDataMap> listMap = DbUp.upTable("oc_order_pay").query("", "",
				"order_code=:order_code AND payed_money > 0", mapParam, -1, -1);

		if (listMap != null) {

			int size = listMap.size();
			SerializeSupport ss = new SerializeSupport<OcOrderPay>();

			for (int j = 0; j < size; j++) {
				OcOrderPay pic = new OcOrderPay();
				ss.serialize(listMap.get(j), pic);
				ret.add(pic);
			}
		}

		return ret;
	}

	/**
	 * 获取订单运输信息
	 * 
	 * @param orderCode
	 * @return
	 */
	private OcOrderShipments getOcOrderShipmentsByOrderCode(String orderCode) {

		OcOrderShipments ocOrderShipments = null;

		MDataMap dm = DbUp.upTable("oc_order_shipments").one("order_code",
				orderCode);

		if (dm != null) {

			SerializeSupport ss = new SerializeSupport<OcOrderShipments>();
			OcOrderShipments pic = new OcOrderShipments();
			ss.serialize(dm, pic);
			ocOrderShipments = pic;

		}

		return ocOrderShipments;
	}

	/**
	 * 如果创建失败是，调用此取消订单的接口
	 * 
	 * @param order
	 * @return
	 */
	private RootResult CancelOrderForCreate(Order order) {
		RootResult ret = new RootResult();

		FlowBussinessService fs = new FlowBussinessService();

		MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",
				order.getOrderCode());

		if (dm != null) {
			try {
				String flowBussinessUid = dm.get("uid");
				String fromStatus = dm.get("order_status");
				String toStatus = "4497153900010007";
				String flowType = "449715390008";
				String userCode = "system";
				String remark = "auto by system";
				MDataMap md = new MDataMap();
				md.put("order_code", order.getOrderCode());
				ret = fs.ChangeFlow(flowBussinessUid, flowType, fromStatus,
						toStatus, userCode, remark, md);
				if (ret.getResultCode() == 1) {
					this.CancelOrderForStockAndMoney(order,
							order.getOrderCode());
				} else {
					WebHelper.errorMessage(order.getOrderCode(), "addorder", 1,
							"CancelOrderForCreate on ChangeFlow",
							ret.getResultMessage(), null);
				}
			} catch (Exception e) {

				WebHelper.errorMessage(order.getOrderCode(), "addorder", 1,
						"CancelOrderForCreate", ret.getResultMessage(), e);
			}

		}

		return ret;
	}

	/**
	 * 添加订单，减钱，减库存
	 * 
	 * @param order
	 * @param relateOrders
	 * @return
	 */
	private RootResult AddOrderForStockAndMoney(Order order, String relateOrders) {
		RootResult ret = new RootResult();

		PresentCardService pcs = new PresentCardService();
		// 先减钱
		ret = pcs.usePresentCard(order, 0);

		if (ret.getResultCode() == SqlCommon.SuccessFlag) {
			ProductSkuInfoService pss = new ProductSkuInfoService();
			List<ProductSkuInfo> list = new ArrayList<ProductSkuInfo>();
			for (OrderDetail od : order.getProductList()) {
				od.setOrderCode(order.getOrderCode());

				ProductSkuInfo psi = new ProductSkuInfo();

				psi.setProductCode(od.getProductCode());
				psi.setSkuCode(od.getSkuCode());
				psi.setStockNum(od.getSkuNum());

				list.add(psi);
			}
			StringBuffer error = new StringBuffer();
			// 调用商品中心的库存处理函数 - 减库存
			int errorcodeP = pss.DecreaseProductStockForOrder(
					order.getOrderCode(), list, error);

			// 如果失败，把钱加回来，否则
			if (errorcodeP != SqlCommon.SuccessFlag) {
				RootResult ret1 = pcs.usePresentCard(order, 1);
				// EmailService es = new EmailService();
				// String mailTo = bConfig("OrderErrorEmail");

				if (ret1.getResultCode() != SqlCommon.SuccessFlag) {
					String errorInfo = bInfo(939301044, order.getOrderCode(),
							relateOrders, ret1.getResultMessage());
					// 发送邮件
					WebHelper
							.errorMessage(
									order.getOrderCode(),
									"addorder",
									1,
									"OrderService.AddOrderForStockAndMoney-usePresentCard",
									errorInfo, null);

				}

				ret.setResultCode(errorcodeP);
				ret.setResultMessage(error.toString());
			}
		}

		return ret;
	}

	/**
	 * 取消订单，加钱，加库存
	 * 
	 * @param order
	 * @param relateOrders
	 * @return
	 */
	private RootResult CancelOrderForStockAndMoney(Order order,
			String relateOrders) {
		RootResult ret = new RootResult();

		PresentCardService pcs = new PresentCardService();
		// 加钱
		ret = pcs.usePresentCard(order, 1);

		//返回微公社余额
		if(order.getOcOrderPayList()!=null&&!order.getOcOrderPayList().isEmpty()){
			for (int i = 0; i < order.getOcOrderPayList().size(); i++) {
				OcOrderPay op = order.getOcOrderPayList().get(i);
				if(StringUtility.isNotNull(op.getPayType())&&"449746280009".equals(op.getPayType())){
					//写入定时任务，定时执行返还微公社金额
					MDataMap jobMap = new MDataMap();
					jobMap.put("uid", WebHelper.upUuid());
					jobMap.put("exec_code", WebHelper.upCode("ET"));
					jobMap.put("exec_type", "449746990004");
					jobMap.put("exec_info", order.getOrderCode());
					jobMap.put("create_time", DateUtil.getSysDateTimeString());
					jobMap.put("begin_time", "");
					jobMap.put("end_time", "");
					jobMap.put("exec_time", DateUtil.getSysDateTimeString());
					jobMap.put("flag_success","0");
					jobMap.put("remark", "OrderService line 1776");
					jobMap.put("exec_number", "0");
					DbUp.upTable("za_exectimer").dataInsert(jobMap);
				}
			}
		}
		if (ret.getResultCode() == SqlCommon.SuccessFlag) {

		} else {
			WebHelper.errorMessage(order.getOrderCode(), "cancelorder", 1,
					"OrderService.CancelOrderForStockAndMoney-usePresentCard",
					ret.getResultMessage(), null);
		}
		for (OrderDetail od : order.getProductList()) {
			// TV商品订单取消订单时不还原占用的实际库存，避免出现实际库存重新从LD同步后又加了下单占用库存的问题
			if("SI2003".equalsIgnoreCase(order.getSmallSellerCode())){
				continue;
			}
			// 多货主商品取消时不返还库存，库存同步以定时为准
			if(DbUp.upTable("pc_productinfo_ext").count("product_code",od.getProductCode(),"delivery_store_type","4497471600430002") > 0) {
				continue;
			}
			if("1".equals(od.getGiftFlag())){
				new PlusSupportStock().skuStockForCancelOrder(od.getOrderCode(), od.getSkuCode(), 0-od.getSkuNum());
			}
		}
//		ProductSkuInfoService pss = new ProductSkuInfoService();
//		List<ProductSkuInfo> list = new ArrayList<ProductSkuInfo>();
//		boolean sflag = false;// 是否走仓库路线
//		for (OrderDetail od : order.getProductList()) {
//			od.setOrderCode(order.getOrderCode());
//
//			ProductSkuInfo psi = new ProductSkuInfo();
//
//			psi.setProductCode(od.getProductCode());
//			psi.setSkuCode(od.getSkuCode());
//			psi.setStockNum(od.getSkuNum());
//
//			list.add(psi);
//
//			if (od.getStoreCode() != null && !"".equals(od.getStoreCode())) { // 存在区域信息时，应该走仓库路线
//				sflag = true;
//			}
//		}
//		StringBuffer error = new StringBuffer();
//		int errorcodeP = 1;
//
//		if (sflag) {
//			TxOrderService txs = BeansHelper
//					.upBean("bean_com_cmall_ordercenter_txservice_TxOrderService");
//			try {
//				txs.doAddStockNum(order.getProductList());
//			} catch (Exception e) {
//				errorcodeP = 939301112;
//				error.append(bInfo(939301112, order.getOrderCode()));
//				bLogError(
//						0,
//						bInfo(939301112, order.getOrderCode()) + ":"
//								+ e.getMessage());
//			}
//		} else {
//			// 调用商品中心的库存处理函数 - 加库存
//			errorcodeP = pss.AddProductStockForOrder(order.getOrderCode(),
//					list, error);
//		}

//		if (errorcodeP != SqlCommon.SuccessFlag) {
//
//			ret.setResultCode(errorcodeP);
//			ret.setResultMessage(error.toString());
//
//			WebHelper
//					.errorMessage(
//							order.getOrderCode(),
//							"cancelorder",
//							1,
//							"OrderService.CancelOrderForStockAndMoney-AddProductStockForOrder",
//							ret.getResultMessage(), null);
//		}

		return ret;
	}

	/**
	 * 添加订单
	 * 
	 * @param list
	 * @param error
	 * @param district_code
	 *            区域代码，用于减仓库库存 ,若传入为null ，则不按分仓库区域
	 * @return
	 */
	public int AddOrderListTx(List<Order> list, StringBuffer error,
			String district_code) {
		int ret = 1;

		List<Order> successList = new ArrayList<Order>();
		TxOrderService txs = BeansHelper
				.upBean("bean_com_cmall_ordercenter_txservice_TxOrderService");
		PresentCardService pcs = new PresentCardService();

		RootResult rret = new RootResult();

		if (list != null && list.size() > 0) {
			rret = this.AddOrderForJiFen(list.get(0));

			if (rret.getResultCode() != 1) {
				error.append(rret.getResultMessage());
				return rret.getResultCode();
			}
		}

		// 减礼品卡等相关的钱
		for (Order order : list) {
			String orderCode = com.srnpr.zapweb.helper.WebHelper
					.upCode(OrderConst.OrderHead);

			order.setOrderCode(orderCode);
			// 先减钱
			RootResult rr = pcs.usePresentCard(order, 0);

			if (rr.getResultCode() == 1) {
				successList.add(order);
			} else {
				ret = rr.getResultCode();
				error.append(rr.getResultMessage());
				break;
			}
		}

		if (list.size() == successList.size()) {
			RootResult rr = new RootResult();
			try {
				txs.insertOrder(successList, rr, "system", district_code);

				try {

					for (Order order : list) {

						if (order.getProductList() != null
								&& order.getProductList().size() > 0) {

							String skuStr = "";

							for (OrderDetail psi : order.getProductList()) {
								skuStr += psi.getSkuCode() + ",";
							}

							if (skuStr.length() > 0) {
								skuStr = skuStr.substring(0,
										skuStr.length() - 1);
							}
							//System.out.println("begin-Jms-stockChange");
							ProductJmsSupport pjs = new ProductJmsSupport();
							pjs.onChangeForSkuChangeStock(skuStr);
							//System.out.println("end-Jms-stockChange");
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
				}

			} catch (Exception e) {
				if (rr.getResultCode() != 1) {
					ret = 939301082;
					error.append(bInfo(939301082, e.getMessage()));
				} else {
					ret = rr.getResultCode();
					error.append(rr.getResultMessage());
				}

				for (Order order : successList) {
					pcs.usePresentCard(order, 1);
				}

				rret = this.CancelOrderForJiFen(list.get(0));

				if (rret.getResultCode() != 1) {
					WebHelper
							.errorMessage(
									list.get(0).getOrderPhpcode(),
									"addOrder-add-jifen",
									1,
									"om.cmall.ordercenter.service.OrderService",
									"add-jifen-userCode:"
											+ list.get(0).getBuyerCode()
											+ "-jifenMoney:"
											+ list.get(0)
													.getAllVirtualMoneyDeduction(),
									null);
				}
			}
		} else {
			for (Order order : successList) {
				pcs.usePresentCard(order, 1);
			}

			rret = this.CancelOrderForJiFen(list.get(0));

			if (rret.getResultCode() != 1) {
				WebHelper.errorMessage(list.get(0).getOrderPhpcode(),
						"addOrder-add-jifen", 1,
						"om.cmall.ordercenter.service.OrderService",
						"add-jifen-userCode:" + list.get(0).getBuyerCode()
								+ "-jifenMoney:"
								+ list.get(0).getAllVirtualMoneyDeduction(),
						null);
			}
		}

		if (ret == 1) {
			String str = "";
			for (Order order : list) {
				str = str + order.getOrderCode() + ",";
			}
			;
			if (!str.equals(""))
				error.append(str.substring(0, str.length() - 1));
		}

		return ret;
	}

	public int AddOrderListTx(List<Order> list, StringBuffer error) {
		return AddOrderListTx(list, error, null);
	}

	/**
	 * 下订单，暂时支持惠家有和家有汇拆单
	 * @param list
	 * @param ret
	 * @return 大订单编号
	 */
	public String AddOrderListForSupper(List<Order> list, RootResult ret) {
		
		TxOrderService txs = BeansHelper.upBean("bean_com_cmall_ordercenter_txservice_TxOrderService");
		
		try {
			return txs.createOrder(list, ret, "system");
		} catch (Exception e) {
			ret.setResultCode(939301082);
			ret.setResultMessage(bInfo(939301082, e.getMessage()));
			WebHelper.errorMessage(list.get(0).getOrderCode(),"addOrder", 1,"om.cmall.ordercenter.service.OrderService",e.getMessage(),e);
		}
		return null;
	}
	
	
	
	/**
	 * 校验是否可售和是否价格过低
	 * 
	 * @param list
	 * @return
	 */
	public RootResult IsProductForSaleAndForLowPrice(List<Order> list) {
		RootResult ret = new RootResult();

		String whereStr = "";
		MDataMap urMapParam = new MDataMap();

		// String whereSkuStr = "";
		// MDataMap urSkuMapParam = new MDataMap();

		int i = 0;

		// ConcurrentHashMap<String,Float> hashSku = new
		// ConcurrentHashMap<String, Float>();

		for (Order order : list) {
			List<OrderDetail> detailList = order.getProductList();

			for (OrderDetail od : detailList) {
				String skuCode = od.getSkuCode();
				urMapParam.put("product_code" + i, od.getProductCode());
				whereStr += " product_code=:product_code" + i + " or";

				// urSkuMapParam.put("sku_code" + i, skuCode);
				// whereSkuStr += " sku_code=:sku_code" + i + " or";

				// if(!hashSku.containsKey(skuCode)){
				// hashSku.put(skuCode, od.getSkuPrice());
				// }
				i++;
			}
		}

		if (whereStr.length() > 2) {
			whereStr = whereStr.substring(0, whereStr.length() - 2);
			// whereSkuStr = whereSkuStr.substring(0, whereSkuStr.length() - 2);
		} else {
			return ret;
		}

		List<MDataMap> pListMap = DbUp.upTable("pc_productinfo").query(
				"product_code,product_status,flag_sale,product_name", "",
				whereStr, urMapParam, -1, -1);

		// List<MDataMap> pSkuListMap = DbUp.upTable("pc_skuinfo").query(
		// "sku_code,sell_price,sku_name", "", whereSkuStr, urSkuMapParam, -1,
		// -1);

		if (pListMap != null) {
			// String productCode = "";
			String productName = "";
			String productStatus = "";
			int flagSale = 0;

			for (MDataMap md : pListMap) {
				// productCode = md.get("product_code");
				productName = md.get("product_name");
				productStatus = md.get("product_status");
				flagSale = Integer.parseInt(md.get("flag_sale"));

				if (flagSale == 1
						&& productStatus.equals(ProductService.ProductStatusSJ)) {

				} else {
					ret.setResultCode(939301085);
					ret.setResultMessage(bInfo(939301085, productName));
					break;
				}
			}
		}

		if (ret.getResultCode() != 1)
			return ret;
		else {

			String skuCode = "";
			String skuName = "";
			BigDecimal sellPrice = new BigDecimal(0.00);
			BigDecimal percentForProduct = new BigDecimal(
					bConfig("ordercenter.OrderForProductDiscount"));

			ProductSkuCache psc = new ProductSkuCache();
			for (Order order : list) {
				List<OrderDetail> detailList = order.getProductList();

				for (OrderDetail od : detailList) {

					skuName = od.getSkuName();
					skuCode = od.getSkuCode();
					sellPrice = od.getSkuPrice();

					SkuForCache sfc = psc.upValue(skuCode);

					if (sfc == null || sfc.getPsi() == null) {
						ret.setResultCode(939301090);
						ret.setResultMessage(bInfo(939301090, skuName));
						break;
					} else {
						// 如果销售价格低于 当前售价的 70% ，则报库存不足的错误!
						if (sellPrice.compareTo(sfc.getPsi().getSellPrice()
								.multiply(percentForProduct)) == 1) {

						} else {
							ret.setResultCode(939301087);
							ret.setResultMessage(bInfo(939301087, skuName));
							break;
						}
					}

				}
			}

		}

		return ret;

	}

	/*
	 * 频繁下单的校验,传入购买者的code
	 */
	public RootResult FrequentlyToOrder(String buyerCode) {
		RootResult ret = new RootResult();
		MDataMap mWhereMap = new MDataMap();
		int interval = Integer
				.parseInt(bConfig("ordercenter.OrderIntervalTimeS"));
		mWhereMap.put("buyer_code", buyerCode);

		int count = DbUp.upTable("oc_orderinfo").dataCount(
				" buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and (UNIX_TIMESTAMP()-UNIX_TIMESTAMP(create_time))< "
						+ interval, mWhereMap);

		if (count > 0) {
			ret.setResultCode(939301089);
			ret.setResultMessage(bInfo(939301089));
			return ret;
		} else
			return ret;
	}

	private RootResult AddOrderForJiFen(Order order) {

		JifenInfo jf = new JifenInfo();
		RootResult rret = new RootResult();

		if (order != null) {
			if (order.getAllVirtualMoneyDeduction() > 0) {
				TxJiFenService txjf = BeansHelper
						.upBean("bean_com_cmall_ordercenter_txservice_TxJiFenService");
				jf.setObject(order.getBuyerCode());
				jf.setValue(order.getAllVirtualMoneyDeduction());

				txjf.updateJiFen(jf, 1, rret, "system", "system",
						order.getOrderPhpcode());
			}

		}

		return rret;

	}

	private RootResult CancelOrderForJiFen(Order order) {

		RootResult rret = new RootResult();
		if (order.getAllVirtualMoneyDeduction() > 0) {
			String uuid = WebHelper.addLock(order.getOrderPhpcode(), 300);

			if (uuid.equals("")) {
				rret.setResultCode(949701005);
				rret.setResultMessage(bInfo(949701005));
				if (rret.getResultCode() != 1) {
					WebHelper
							.errorMessage(
									order.getOrderPhpcode(),
									"cancelOrder-cancel-jifen-locking",
									1,
									"om.cmall.ordercenter.service.OrderService",
									"add-jifen-userCode:"
											+ order.getBuyerCode()
											+ "-jifenMoney:"
											+ order.getAllVirtualMoneyDeduction(),
									null);
				}
				return rret;
			}

			JifenInfo jf = new JifenInfo();
			com.cmall.dborm.txmapper.JifenLogMapper lsom = BeansHelper
					.upBean("bean_com_cmall_dborm_txmapper_JifenLogMapper");

			JifenLogExample selectExample = new JifenLogExample();
			selectExample.createCriteria().andToIdEqualTo(order.getBuyerCode())
					.andTradeCodeEqualTo(order.getOrderPhpcode());
			int count = lsom.countByExample(selectExample);

			if (count >= 2) {
				rret.setResultCode(939301095);
				rret.setResultMessage(bInfo(939301095, order.getOrderPhpcode()));
				WebHelper.unLock(uuid);
				return rret;
			}

			if (order != null) {

				TxJiFenService txjf = BeansHelper
						.upBean("bean_com_cmall_ordercenter_txservice_TxJiFenService");
				jf.setObject(order.getBuyerCode());
				jf.setValue(order.getAllVirtualMoneyDeduction());

				txjf.updateJiFen(jf, 0, rret, "system", "system",
						order.getOrderPhpcode());

				if (rret.getResultCode() != 1) {
					WebHelper
							.errorMessage(
									order.getOrderPhpcode(),
									"cancelOrder-cancel-jifen",
									1,
									"om.cmall.ordercenter.service.OrderService",
									"add-jifen-userCode:"
											+ order.getBuyerCode()
											+ "-jifenMoney:"
											+ order.getAllVirtualMoneyDeduction(),
									null);
				}

			}
			WebHelper.unLock(uuid);

		}

		return rret;
	}

	/**
	 * 获取订单的详细信息
	 * 
	 * @param sellerCode
	 *            卖家编号 不能为空
	 * @param buyerCode
	 *            买家编号 不能为空
	 * @param channel
	 *            订单来源
	 * @param fromTime
	 *            创建时间
	 * @param orderType
	 *            订单类型
	 * @param orderStatus
	 *            订单状态 4497153900010001 下单成功-未付款 4497153900010002 下单成功-未发货
	 *            4497153900010003 已发货 4497153900010004 已收货 4497153900010005
	 *            交易成功 4497153900010006 交易失败
	 * @return List<Order>
	 */
	public List<Order> getOrderListByBuyerAndOrderChannel(String seller_code,
			String buyerCode, String channel, String fromTime,
			String orderType, String orderStatus) {

		List<Order> re = new ArrayList<Order>();
		MDataMap mapParam = new MDataMap();
		if (seller_code != null && !"".equals(seller_code)) {
			mapParam.put("seller_code", seller_code);
		}
		if (buyerCode != null && !"".equals(buyerCode)) {
			mapParam.put("buyer_code", buyerCode);
		}
		if (fromTime != null && !fromTime.equals("")) {
			mapParam.put("create_time", fromTime);
		}
		if (orderStatus != null && !orderStatus.equals("")) {
			mapParam.put("order_status", orderStatus);
		}
		if (orderType != null && !orderType.equals("")) {
			mapParam.put("order_type", orderType);
		}
		if (channel != null && !channel.equals("")) {
			mapParam.put("order_channel", channel);
		}
		List<MDataMap> li = DbUp.upTable("oc_orderinfo").queryAll("", "", "",
				mapParam);
		if (li != null && !li.isEmpty()) {
			Iterator<MDataMap> it = li.iterator();
			while (it.hasNext()) {
				MDataMap dm = (MDataMap) it.next();
				if (dm != null) {
					SerializeSupport ss = new SerializeSupport<Order>();
					Order pic = new Order();
					ss.serialize(dm, pic);
					pic.setAddress(this.getOrderAddressByOrderCode(pic
							.getOrderCode()));
					pic.setActivityList(this
							.getOrderActivityListByOrderCode(pic.getOrderCode()));
					pic.setOcOrderPayList(this.getOrderPayListByOrderCode(pic
							.getOrderCode()));
					pic.setProductList(this.getOrderDetaiListByOrderCode(pic
							.getOrderCode()));
					pic.setOcorderShipments(this
							.getOcOrderShipmentsByOrderCode(pic.getOrderCode()));
					pic.setExpressList(this.getExpressList(pic.getOrderCode()));
					re.add(pic);
				}
			}
		}
		return re;
	}

	/**
	 * 个人中心订单数量
	 * 
	 * @param buyer_code
	 *            买家编号
	 * @return
	 */
	public List<Map<String, Object>> personagetOrderNumber(String buyer_code) {
		List<Map<String, Object>> buyerList = new ArrayList<Map<String, Object>>();
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("delete_flag", "0");
		/*
		 * 获取需要过滤的订单类型
		 */
		String orderTypeQueryWhere = getNotInOrderType();
		
		if(VersionHelper.checkServerVersion("3.5.63.55")){
			if (!"".equals(buyer_code)) {
				buyerList = DbUp
						.upTable("oc_orderdetail")
						.dataSqlList(
								"select count(1) as number,order_status from ordercenter.oc_orderinfo " +
								"where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and order_type not in ("+orderTypeQueryWhere+")" +
								"group by ordercenter.oc_orderinfo.order_status",
								mapParam);
				if (!buyerList.isEmpty()) {
					return buyerList;
				}
			}
		}else{
			if (!"".equals(buyer_code)) {
				buyerList = DbUp
						.upTable("oc_orderdetail")
						.dataSqlList(
								"select count(1) as number,order_status from ordercenter.oc_orderinfo " +
								"where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and out_order_code !='' " +
								"group by ordercenter.oc_orderinfo.order_status",
								mapParam);
				if (!buyerList.isEmpty()) {
					return buyerList;
				}
			}
		}
		
		return null;
	}

	/**
	 * 返回买家的订单总数(惠家有)
	 * 
	 * @param buyer_code
	 *            买家编号
	 * @return
	 */
	public int orderCount(String buyer_code, String order_status) {
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("delete_flag", "0");
		mapParam.put("order_status", order_status);
		
		
		if(VersionHelper.checkServerVersion("3.5.51.51")){
			if (!"".equals(buyer_code) && "".equals(order_status)) {
				int countPage = DbUp.upTable("oc_orderinfo").dataCount(
						"buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag ",
						mapParam);
				return countPage;
			}
			if (!"".equals(buyer_code) && !"".equals(order_status)) {
				int countPage = DbUp
						.upTable("oc_orderinfo")
						.dataCount(
								"buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and order_status=:order_status ",
								mapParam);
				return countPage;
			}
		}else{
			if (!"".equals(buyer_code) && "".equals(order_status)) {
				int countPage = DbUp.upTable("oc_orderinfo").dataCount(
						"buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and out_order_code !='' ",
						mapParam);
				return countPage;
			}
			if (!"".equals(buyer_code) && !"".equals(order_status)) {
				int countPage = DbUp
						.upTable("oc_orderinfo")
						.dataCount(
								"buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and order_status=:order_status and out_order_code !=''  ",
								mapParam);
				return countPage;
			}
		}
		
		return 0;
	}
	/**
	 * 统计用户下订单总数(最新)
	 * @param buyer_code
	 * @param order_status
	 * @return
	 */
	public int orderCountNew(String buyer_code, String order_status){
		int count = 0;
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("delete_flag", "0");
		mapParam.put("order_status", order_status);
		/*
		 * 获取需要过滤的订单类型
		 */
		String orderTypeQueryWhere = getNotInOrderType();
		
		if(VersionHelper.checkServerVersion("3.5.51.51")){
			if(buyer_code!=null && !"".equals(buyer_code)){
				if("4497153900010001".equals(order_status)){   //统计待支付
					count = orderCountPayment(buyer_code); 
				
				}else if("".equals(order_status)){   //统计所有
					int countPage = DbUp.upTable("oc_orderinfo").dataCount(
							"buyer_code=:buyer_code and delete_flag=:delete_flag and order_source not in('449715190014','449715190037') and order_status!='4497153900010001'  and order_type not in ("+orderTypeQueryWhere+") ",
							mapParam);
					count = countPage + orderCountPayment(buyer_code);
				
				}else if(!"".equals(order_status) && !"4497153900010001".equals(order_status)){   
					count = DbUp.upTable("oc_orderinfo")
							.dataCount("buyer_code=:buyer_code and delete_flag=:delete_flag and order_source not in('449715190014','449715190037') and order_status=:order_status  and order_type not in ("+orderTypeQueryWhere+") ",
									mapParam);
				}
			}
		}else{
			if(buyer_code!=null && !"".equals(buyer_code)){
				if("4497153900010001".equals(order_status)){   //统计待支付
					count = orderCountPayment(buyer_code); 
				
				}else if("".equals(order_status)){   //统计所有
					int countPage = DbUp.upTable("oc_orderinfo").dataCount(
							"buyer_code=:buyer_code and delete_flag=:delete_flag and out_order_code !='' and order_source not in('449715190014','449715190037')  and order_status!='4497153900010001'",
							mapParam);
					count = countPage + orderCountPayment(buyer_code);
				
				}else if(!"".equals(order_status) && !"4497153900010001".equals(order_status)){   
					count = DbUp.upTable("oc_orderinfo")
							.dataCount("buyer_code=:buyer_code and delete_flag=:delete_flag and order_source not in('449715190014','449715190037')  and order_status=:order_status and out_order_code !=''  ",
									mapParam);
				}
			}
		}
		
		
		return count;
		
	}
	
	/**
	 * 统计用户下待支付订单总数
	 * @param buyer_code
	 * @param order_status
	 * @return
	 */
	public static int orderCountPayment(String buyer_code) {
		int count = 0;
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("delete_flag", "0");
		mapParam.put("order_status", "4497153900010001");
		/*
		 * 获取需要过滤的订单类型
		 */
		String orderTypeQueryWhere = new OrderService().getNotInOrderType();
		if(buyer_code!=null && !"".equals(buyer_code)){
			Map<String,Object> countMap = DbUp.upTable("oc_orderinfo").dataSqlOne("select count(distinct big_order_code) as countBigOrder  from  oc_orderinfo " +
					"where order_status=:order_status  and order_source not in('449715190014','449715190037') and buyer_code=:buyer_code and delete_flag='0' and order_type not in ("+orderTypeQueryWhere+") ", mapParam);
			
			if(countMap!=null && !"".equals(countMap) && countMap.size()>0){
				count = Integer.parseInt(countMap.get("countBigOrder").toString());
			}
		}
		return count;
	}
	
	
	/**
	 * 返回买家的订单总数(惠美丽)
	 * 
	 * @param buyer_code
	 *            买家编号
	 * @return
	 */
	public int orderCountForBeauty(String buyer_code, String order_status) {
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("delete_flag", "0");
		mapParam.put("order_status", order_status);

		if (!"".equals(buyer_code) && "".equals(order_status)) {
			int countPage = DbUp.upTable("oc_orderinfo").dataCount(
					"buyer_code=:buyer_code and delete_flag=:delete_flag ",
					mapParam);
			return countPage;
		}
		if (!"".equals(buyer_code) && !"".equals(order_status)) {
			int countPage = DbUp
					.upTable("oc_orderinfo")
					.dataCount(
							"buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and order_status=:order_status ",
							mapParam);
			return countPage;
		}
		return 0;
	}

	/**
	 * 返回买家的订单总数(家有惠)
	 * 
	 * @param buyer_code
	 * @param order_status
	 * @param create_time
	 * @return
	 */
	public int orderHomeCount(String buyer_code, String order_status,
			String create_time, String createTimeBefore) {
		String sql = "";
		int countPage = 0;
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("delete_flag", "0");
		mapParam.put("order_status", order_status);
		mapParam.put("create_time", create_time);

		if (!"".equals(buyer_code) && "".equals(order_status)
				&& "".equals(create_time) && "".equals(createTimeBefore)) {
			countPage = DbUp.upTable("oc_orderinfo").dataCount(
					"buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and out_order_code != ''",
					mapParam);
		} else if (!"".equals(buyer_code) && !"".equals(order_status)
				&& "".equals(create_time) && "".equals(createTimeBefore)) {
			countPage = DbUp
					.upTable("oc_orderinfo")
					.dataCount(
							"buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and order_status=:order_status and out_order_code != ''",
							mapParam);
		} else if (!"".equals(buyer_code) && "".equals(order_status)
				&& !"".equals(create_time) && "".equals(createTimeBefore)) {
			sql = "buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and out_order_code != '' and  create_time like '"+create_time+"%'";
			countPage = DbUp
					.upTable("oc_orderinfo")
					.dataCount(
							sql,
							mapParam);
		} else if (!"".equals(buyer_code) && !"".equals(order_status)
				&& !"".equals(create_time)) {
			sql = "buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and order_status=:order_status and out_order_code != '' and create_time like '"+create_time+"%'";
			countPage = DbUp
					.upTable("oc_orderinfo")
					.dataCount(
							sql,
							mapParam);
		}else if(!"".equals(buyer_code) && "".equals(order_status)
				&& "".equals(create_time) && !"".equals(createTimeBefore)){
			sql = "buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and out_order_code != '' and create_time < YEAR('"+createTimeBefore+"')";
			countPage = DbUp
					.upTable("oc_orderinfo")
					.dataCount(
							sql,
							mapParam);
		}else if (!"".equals(buyer_code) && !"".equals(order_status)
				&& "".equals(create_time) && !"".equals(createTimeBefore)) {
			sql = "buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and order_status=:order_status and out_order_code != '' and create_time < YEAR('"+createTimeBefore+"')";
			countPage = DbUp
					.upTable("oc_orderinfo")
					.dataCount(
							sql,
							mapParam);
		}
		return countPage;
	}

	/**
	 * 查询带状态的订单总数
	 * 
	 * @param buyer_code
	 * @param order_status
	 * @return
	 */
	public int waitPaymentCount(String buyer_code, String order_status) {
		int countPage = 0;
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("delete_flag", "0");
		mapParam.put("order_status", order_status);

		if (!"".equals(buyer_code) && buyer_code != null
				&& !"".equals(order_status) && order_status != null) {
			countPage = DbUp
					.upTable("oc_orderinfo")
					.dataCount(
							"buyer_code=:buyer_code and delete_flag=:delete_flag and order_source not in('449715190014','449715190037') and order_status=:order_status and out_order_code != ''",
							mapParam);
		}
		return countPage;
	}

	/**
	 * 获取订单信息(惠家有)(获取大订单号)
	 * 
	 * @param buyer_code
	 *            买家编号
	 * @param order_status
	 *            状态
	 * @param nextPage
	 *            页数
	 * @param isPage
	 * 			 是否分页
	 * @return
	 */
	public List<Map<String, Object>> orderInformation(String buyer_code,
			String order_status, String nextPage, String sellerCode,boolean isPage) {
		
		String bigOrderCode = "";
		List<Map<String, Object>> buyerList = new ArrayList<Map<String, Object>>();
		List<Map<String, Object>> buyerNewList = new ArrayList<Map<String, Object>>();

		OrderPageService orderPageService = new OrderPageService();
		Map<String, Integer> map = orderPageService.pageNumber(Integer
				.parseInt(nextPage));

		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("order_status", order_status);
		if(isPage) {
			mapParam.put("startNumber", map.get("startNumber").toString());
			mapParam.put("endNumber", map.get("endNumber").toString());
		}
		
		mapParam.put("delete_flag", "0");
		mapParam.put("seller_code", sellerCode);
		
		if(VersionHelper.checkServerVersion("3.5.51.51")){
//			if (order_status != null && !"".equals(order_status)) {
//				buyerList = DbUp
//						.upTable("oc_orderinfo")
//						.dataSqlList(
//								"select DISTINCT big_order_code from ordercenter.oc_orderinfo where buyer_code=:buyer_code and delete_flag=:delete_flag and order_status=:order_status and seller_code=:seller_code and order_type not in ('449715200013','449715200014','449715200015') ORDER BY update_time DESC, zid desc LIMIT "
//										+ mapParam.get("startNumber")
//										+ ","
//										+ mapParam.get("endNumber") + "", mapParam);
//				
//			} else {
//				buyerList = DbUp
//						.upTable("oc_orderinfo")
//						.dataSqlList(
//								"select DISTINCT big_order_code from ordercenter.oc_orderinfo where buyer_code=:buyer_code and delete_flag=:delete_flag and seller_code=:seller_code and order_type not in ('449715200013','449715200014','449715200015') ORDER BY update_time DESC, zid desc LIMIT "
//										+ mapParam.get("startNumber")
//										+ ","
//										+ mapParam.get("endNumber") + "", mapParam);
//			}
//			
//			for(Map<String, Object> mapOrder : buyerList){
//				bigOrderCode = "'"+mapOrder.get("big_order_code")+"',"+bigOrderCode;
//			}
//			if(!"".equals(bigOrderCode)){
//				bigOrderCode = "("+bigOrderCode.substring(0, bigOrderCode.length()-1)+")";
//				
//				String sSql = "select * " +
//						"from ordercenter.oc_orderinfo where big_order_code in "+bigOrderCode+" and delete_flag='0' "+(StringUtils.isBlank(order_status)?"":" and order_status=:order_status ")+" ORDER BY update_time DESC, zid desc";
//				
//				buyerNewList = DbUp.upTable("oc_orderinfo").dataSqlList(sSql, new MDataMap("order_status", order_status));
//			}
			
			String notInOrderType = getNotInOrderType();
			
			//分页临时解决方案
			String sSql = "";
			if (isPage) {
				sSql = "select * " +
						"from  ordercenter.oc_orderinfo where buyer_code=:buyer_code and seller_code=:seller_code and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') ) and delete_flag='0' and order_source not in('449715190014','449715190037') and order_type not in ("+notInOrderType+") "+(StringUtils.isBlank(order_status)?"":" and order_status=:order_status ")+" ORDER BY update_time DESC, zid desc LIMIT " + mapParam.get("startNumber") + "," + mapParam.get("endNumber") ;
			} else {
				sSql = "select * " +
						"from  ordercenter.oc_orderinfo where buyer_code=:buyer_code  and seller_code=:seller_code and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') ) and delete_flag='0' and order_source not in('449715190014','449715190037') and order_type not in ("+notInOrderType+") "+(StringUtils.isBlank(order_status)?"":" and order_status=:order_status ")+" ORDER BY update_time DESC, zid desc ";
			}
			
			buyerNewList = DbUp.upTable("oc_orderinfo").dataSqlList(sSql, mapParam);
			
			
			
			
			
			
//			if (order_status != null && !"".equals(order_status)) {
//				buyerList = DbUp
//						.upTable("oc_orderinfo")
//						.dataSqlList(
//								"SELECT   order_status,order_code,create_time,due_money,out_order_code,seller_code,big_order_code FROM  ordercenter.oc_orderinfo AS p WHERE p.big_order_code IN (select DISTINCT big_order_code from ordercenter.oc_orderinfo where buyer_code=:buyer_code and delete_flag=:delete_flag and order_status=:order_status and seller_code=:seller_code) ORDER BY update_time DESC, zid desc LIMIT "
//										+ mapParam.get("startNumber")
//										+ ","
//										+ mapParam.get("endNumber") + "", mapParam);
//			} else {
//				buyerList = DbUp
//						.upTable("oc_orderinfo")
//						.dataSqlList(
//								"SELECT  order_status,order_code,create_time,due_money,out_order_code,seller_code,big_order_code  FROM  ordercenter.oc_orderinfo AS p WHERE p.big_order_code IN  (select DISTINCT big_order_code from ordercenter.oc_orderinfo where buyer_code=:buyer_code and delete_flag=:delete_flag and seller_code=:seller_code) ORDER BY update_time DESC, zid desc LIMIT "
//										+ mapParam.get("startNumber")
//										+ ","
//										+ mapParam.get("endNumber") + "", mapParam);
//			}
		}else{
			if (order_status != null && !"".equals(order_status)) {
				buyerNewList = DbUp
						.upTable("oc_orderinfo")
						.dataSqlList(
								"select * from ordercenter.oc_orderinfo where buyer_code=:buyer_code  and delete_flag=:delete_flag and order_source not in('449715190014','449715190037') and order_status=:order_status and seller_code=:seller_code and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') ) and out_order_code != '' ORDER BY update_time DESC, zid desc LIMIT "
										+ mapParam.get("startNumber")
										+ ","
										+ mapParam.get("endNumber") + "", mapParam);
			} else {
				buyerNewList = DbUp
						.upTable("oc_orderinfo")
						.dataSqlList(
								"select * from ordercenter.oc_orderinfo where buyer_code=:buyer_code  and delete_flag=:delete_flag and order_source not in('449715190014','449715190037') and seller_code=:seller_code and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') ) and  out_order_code != ''  ORDER BY update_time DESC, zid desc LIMIT "
										+ mapParam.get("startNumber")
										+ ","
										+ mapParam.get("endNumber") + "", mapParam);
			}
		}
		
		if (!buyerNewList.isEmpty()) {
			return buyerNewList;
		}
		return null;
	}
	/**
	 * 获取订单信息(惠家有)(通过小订单获取)
	 * @param buyer_code
	 * @param order_status
	 * @param nextPage
	 * @param sellerCode
	 * @param isPage 是否分页
	 * @return
	 */
	public List<Map<String, Object>> orderInformationSmall(String buyer_code,
			String order_status, String nextPage, String sellerCode ,boolean isPage) {
		
		List<Map<String, Object>> buyerNewList = new ArrayList<Map<String, Object>>();

		OrderPageService orderPageService = new OrderPageService();
		Map<String, Integer> map = orderPageService.pageNumber(Integer
				.parseInt(nextPage));

		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("order_status", order_status);
		if (isPage) {
			mapParam.put("startNumber", map.get("startNumber").toString());
			mapParam.put("endNumber", map.get("endNumber").toString());
		}
		
		mapParam.put("delete_flag", "0");
		mapParam.put("seller_code", sellerCode);
		String notInOrderType = getNotInOrderType();
		if (order_status != null && !"".equals(order_status)) {
			String sql = "";
			if (isPage) {
				sql = "select order_status,order_code,create_time,due_money,out_order_code,seller_code,big_order_code,small_seller_code,update_time,zid from ordercenter.oc_orderinfo where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and  delete_flag=:delete_flag and order_status=:order_status and seller_code=:seller_code and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') ) and order_type not in ("+notInOrderType+") ORDER BY update_time DESC, zid desc LIMIT ";
				if("4497153900010005".equals(order_status)){
					sql = "select order_status,order_code,create_time,due_money,out_order_code,seller_code,big_order_code,small_seller_code,update_time,zid from ordercenter.oc_orderinfo where"
							+ " buyer_code=:buyer_code and delete_flag=:delete_flag and order_source not in('449715190014','449715190037') and order_status=:order_status and seller_code=:seller_code and order_type not in ("+notInOrderType+") "
							+ " and order_code not in (select order_code from newscenter.nc_order_evaluation where order_name=:buyer_code and manage_code=:seller_code )"
							+ " and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') )"
							+ "  ORDER BY update_time DESC, zid desc LIMIT ";
				}
				
				buyerNewList = DbUp
						.upTable("oc_orderinfo")
						.dataSqlList(
								sql
										+ mapParam.get("startNumber")
										+ ","
										+ mapParam.get("endNumber") + "", mapParam);
			} else {
				sql = "select order_status,order_code,create_time,due_money,out_order_code,seller_code,big_order_code,small_seller_code,update_time,zid from ordercenter.oc_orderinfo where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and order_status=:order_status and seller_code=:seller_code and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') ) and order_type not in ("+notInOrderType+") ORDER BY update_time DESC, zid desc  ";
				if("4497153900010005".equals(order_status)){
					sql = "select order_status,order_code,create_time,due_money,out_order_code,seller_code,big_order_code,small_seller_code,update_time,zid from ordercenter.oc_orderinfo where"
							+ " buyer_code=:buyer_code and delete_flag=:delete_flag and order_source not in('449715190014','449715190037') and order_status=:order_status and seller_code=:seller_code and order_type not in ("+notInOrderType+") "
							+ " and order_code not in (select order_code from newscenter.nc_order_evaluation where order_name=:buyer_code and manage_code=:seller_code )"
							+ " and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') )"
							+ "  ORDER BY update_time DESC, zid desc  ";
				}
				
				buyerNewList = DbUp
						.upTable("oc_orderinfo")
						.dataSqlList(sql, mapParam);
			}
			
			
		} else {
			if (isPage) {
				buyerNewList = DbUp
						.upTable("oc_orderinfo")
						.dataSqlList(
								"select order_status,order_code,create_time,due_money,out_order_code,seller_code,big_order_code,small_seller_code,update_time,zid from ordercenter.oc_orderinfo where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and seller_code=:seller_code and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') )   ORDER BY update_time DESC, zid desc LIMIT "
										+ mapParam.get("startNumber")
										+ ","
										+ mapParam.get("endNumber") + "", mapParam);
			} else {
				buyerNewList = DbUp
						.upTable("oc_orderinfo")
						.dataSqlList(
								"select order_status,order_code,create_time,due_money,out_order_code,seller_code,big_order_code,small_seller_code,update_time,zid from ordercenter.oc_orderinfo where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and seller_code=:seller_code and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') )   ORDER BY update_time DESC, zid desc "
										, mapParam);
			}
			
		}
		
		if (!buyerNewList.isEmpty()) {
			return buyerNewList;
		}
		return null;
	}
	
	/**
	 * 获取订单信息(惠家有)(通过小订单获取)，包含已经评价的订单
	 * @param buyer_code
	 * @param order_status
	 * @param nextPage
	 * @param sellerCode
	 * @param isPage 是否分页
	 * @return
	 */
	public List<Map<String, Object>> orderInformationSmallV2(String buyer_code,
			String order_status, String nextPage, String sellerCode ,boolean isPage) {
		
		List<Map<String, Object>> buyerNewList = new ArrayList<Map<String, Object>>();

		OrderPageService orderPageService = new OrderPageService();
		Map<String, Integer> map = orderPageService.pageNumber(Integer
				.parseInt(nextPage));

		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("order_status", order_status);
		if (isPage) {
			mapParam.put("startNumber", map.get("startNumber").toString());
			mapParam.put("endNumber", map.get("endNumber").toString());
		}
		
		mapParam.put("delete_flag", "0");
		mapParam.put("seller_code", sellerCode);
		String notInOrderType = getNotInOrderType();
		String sql = "select order_status,order_code,order_type,create_time,due_money,out_order_code,seller_code,big_order_code,small_seller_code,update_time,zid from ordercenter.oc_orderinfo";
		
		String whereSql = " where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and seller_code=:seller_code and order_type not in ("+notInOrderType+") ";
		if(StringUtils.isNotBlank(order_status)){
			whereSql += " and order_status=:order_status";
		}
		
		whereSql += " and (org_ord_id = '' or (org_ord_id != '' and order_status != '4497153900010002') )";
		
		sql += whereSql;
		sql += " ORDER BY update_time DESC, zid desc";
		
		if(isPage){
			sql += " LIMIT " + mapParam.get("startNumber") + "," +mapParam.get("endNumber");
		}
		
		buyerNewList = DbUp.upTable("oc_orderinfo").dataSqlList(sql, mapParam);
		
		if (!buyerNewList.isEmpty()) {
			return buyerNewList;
		}
		return null;
	}
	
	/**
	 * 获取订单信息(家有惠)
	 * 
	 * @param buyer_code
	 * @param order_status
	 * @param create_time
	 * @param nextPage
	 * @return
	 */
	public List<Map<String, Object>> orderHomeInformation(String buyer_code,
			String order_status, String create_time, String createTimeBefore, int  nextPage, int num) {
		List<Map<String, Object>> buyerList = new ArrayList<Map<String, Object>>();
		OrderPageService orderPageService = new OrderPageService();
		Map<String, Integer> map = orderPageService.pageNumber(nextPage,num);
		String sql = "";
		
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("order_status", order_status);
		//mapParam.put("create_time", create_time);
		mapParam.put("startNumber", map.get("startNumber").toString());
		mapParam.put("endNumber", map.get("endNumber").toString());
		mapParam.put("delete_flag", "0");
		
		
		if (!"".equals(order_status) && "".equals(create_time) && "".equals(createTimeBefore)) {
			buyerList = DbUp
					.upTable("oc_orderinfo")
					.dataSqlList(
							"select order_status,order_code,create_time,due_money,out_order_code,seller_code,pay_type from ordercenter.oc_orderinfo where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and order_status=:order_status and out_order_code != '' ORDER BY update_time DESC LIMIT "
									+ mapParam.get("startNumber")
									+ ","
									+ mapParam.get("endNumber") + "", mapParam);
		} else if (!"".equals(order_status) && !"".equals(create_time) && "".equals(createTimeBefore) ) {
			sql = "select order_status,order_code,create_time,due_money,out_order_code,seller_code,pay_type from ordercenter.oc_orderinfo " +
					"where buyer_code=:buyer_code and delete_flag=:delete_flag and order_source not in('449715190014','449715190037') and order_status=:order_status and create_time like '"+create_time+"%' and out_order_code != '' " +
							"ORDER BY update_time DESC LIMIT "+ mapParam.get("startNumber")+ ","+ mapParam.get("endNumber") + "";
			buyerList = DbUp
					.upTable("oc_orderinfo")
					.dataSqlList(sql, mapParam);
			
		} else if(!"".equals(order_status) && "".equals(create_time) && !"".equals(createTimeBefore) ){
			
			sql = "select order_status,order_code,create_time,due_money,out_order_code,seller_code,pay_type from ordercenter.oc_orderinfo " +
					"where buyer_code=:buyer_code and delete_flag=:delete_flag and order_source not in('449715190014','449715190037') and order_status=:order_status and create_time < YEAR('"+createTimeBefore+"') and out_order_code != '' " +
							"ORDER BY update_time DESC LIMIT "+ mapParam.get("startNumber")+ ","+ mapParam.get("endNumber") + "";
			buyerList = DbUp
					.upTable("oc_orderinfo")
					.dataSqlList(sql, mapParam);
			
		}else if ("".equals(order_status) && "".equals(create_time) && "".equals(createTimeBefore)) {
			buyerList = DbUp
					.upTable("oc_orderinfo")
					.dataSqlList(
							"select order_status,order_code,create_time,due_money,out_order_code,seller_code,pay_type from ordercenter.oc_orderinfo where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and out_order_code != '' ORDER BY update_time DESC LIMIT "
									+ mapParam.get("startNumber")
									+ ","
									+ mapParam.get("endNumber") + "", mapParam);
		}else if("".equals(order_status) && !"".equals(create_time) && "".equals(createTimeBefore)){
			sql = "select order_status,order_code,create_time,due_money,out_order_code,seller_code,pay_type from ordercenter.oc_orderinfo " +
					"where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and create_time like '"+create_time+"%' and out_order_code != '' " +
							"ORDER BY update_time DESC LIMIT "+ mapParam.get("startNumber")+ ","+ mapParam.get("endNumber") + "";
			buyerList = DbUp
					.upTable("oc_orderinfo")
					.dataSqlList(sql, mapParam);
		}else if("".equals(order_status) && "".equals(create_time) && !"".equals(createTimeBefore)){
			sql = "select order_status,order_code,create_time,due_money,out_order_code,seller_code,pay_type from ordercenter.oc_orderinfo " +
					"where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and create_time < YEAR('"+createTimeBefore+"') and out_order_code != '' " +
							"ORDER BY update_time DESC LIMIT "+ mapParam.get("startNumber")+ ","+ mapParam.get("endNumber") + "";
			buyerList = DbUp
					.upTable("oc_orderinfo")
					.dataSqlList(sql, mapParam);
		}

		if (!buyerList.isEmpty()) {
			return buyerList;
		}
		return null;
	}

	/**
	 * 获取订单信息（惠美丽）
	 * 
	 * @param buyer_code
	 *            买家编号
	 * @param order_status
	 *            状态
	 * @param nextPage
	 *            页数
	 * @return
	 */
	public List<Map<String, Object>> orderInformationForBeauty(
			String buyer_code, String order_status, String nextPage) {
		List<Map<String, Object>> buyerList = new ArrayList<Map<String, Object>>();

		OrderPageService orderPageService = new OrderPageService();
		Map<String, Integer> map = orderPageService.pageNumber(Integer
				.parseInt(nextPage));

		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("order_status", order_status);
		mapParam.put("startNumber", map.get("startNumber").toString());
		mapParam.put("endNumber", map.get("endNumber").toString());
		mapParam.put("delete_flag", "0");
		if (order_status != null && !"".equals(order_status)) {
			buyerList = DbUp
					.upTable("oc_orderinfo")
					.dataSqlList(
							"select order_status,order_code,create_time,due_money,out_order_code,pay_type,seller_code from ordercenter.oc_orderinfo where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and order_status=:order_status ORDER BY update_time DESC LIMIT "
									+ mapParam.get("startNumber")
									+ ","
									+ mapParam.get("endNumber") + "", mapParam);
		} else {
			buyerList = DbUp
					.upTable("oc_orderinfo")
					.dataSqlList(
							"select order_status,order_code,create_time,due_money,out_order_code,pay_type,seller_code from ordercenter.oc_orderinfo where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag ORDER BY update_time DESC LIMIT "
									+ mapParam.get("startNumber")
									+ ","
									+ mapParam.get("endNumber") + "", mapParam);
		}
		
		List<Map<String, Object>> buyerLists = new ArrayList<Map<String, Object>>();
		
		if(buyerList!=null&&buyerList.size()!=0){
			for (int i = 0; i < buyerList.size(); i++) {
				
				if(!buyerList.get(i).get("pay_type").equals("")&&buyerList.get(i).get("pay_type")!=null){
					buyerLists.add(buyerList.get(i));
				}
			}
		}

		if (!buyerLists.isEmpty()) {
			return buyerLists;
		}
		return null;
	}

	/**
	 * 查询订单商品数量(每一个订单数量加一起的总数) 和 商品code、商品单价
	 * 
	 * @param map
	 * @return
	 */
	public List<Map<String, Object>> orderSellerNumber(Map<String, Object> map) {
		List<Map<String, Object>> buyerList = new ArrayList<Map<String, Object>>();
		MDataMap mapParam = new MDataMap();
		mapParam.put("order_code", map.get("order_code").toString());

		buyerList = DbUp
				.upTable("oc_orderdetail")
				.dataSqlList(
						 "select sku_num,sku_code,sku_price,group_price,show_price,coupon_price,integral_money from ordercenter.oc_orderdetail where order_code =:order_code and gift_flag=1",
						mapParam);
		if (!buyerList.isEmpty()) {
			return buyerList;
		}
		return null;
	}

	/**
	 * 查询商品信息
	 * 
	 * @param sku_code
	 * @return
	 */
	public List<Map<String, Object>> sellerInformation(String sku_code) {
		List<Map<String, Object>> buyerList = new ArrayList<Map<String, Object>>();
		MDataMap mapParam = new MDataMap();
		mapParam.put("sku_code", sku_code);

		buyerList = DbUp
				.upTable("pc_skuinfo")
				.dataSqlList(
						"select sku_code,pc_productinfo.product_code as product_code,pc_productinfo.small_seller_code,IF(sku_picurl='',mainpic_url,sku_picurl) as 'sku_picurl',sku_name,stock_num,sku_key,sku_keyvalue,product_shortname from pc_skuinfo,pc_productinfo where pc_skuinfo.product_code = pc_productinfo.product_code	and sku_code =:sku_code",
						mapParam);

		if (!buyerList.isEmpty()) {
			return buyerList;
		}
		return null;
	}
	
	public List<Map<String, Object>> sellerInformation1(String product_code,String color_style) {
		List<Map<String, Object>> buyerList = new ArrayList<Map<String, Object>>();
		MDataMap mapParam = new MDataMap();
		mapParam.put("product_code", product_code);
		mapParam.put("color_style", color_style);

		buyerList = DbUp
				.upTable("pc_skuinfo")
				.dataSqlList(
						"select sku_code,a.product_code as product_code,IF(sku_picurl='',mainpic_url,sku_picurl) as 'sku_picurl',sku_name,stock_num,sku_key,sku_keyvalue,product_shortname from pc_skuinfo a join pc_productinfo b on a.product_code = b.product_code where a.product_code=:product_code and a.sku_key=:color_style",
						mapParam);

		if (!buyerList.isEmpty()) {
			return buyerList;
		}
		return null;
	}
	
	/**
	 * 查询SKU编码
	 * @param product_code
	 * @param color_style
	 * @return
	 */
	public String getSkuCodeByColorStyle(String product_code, String color_style) {
		MDataMap mapParam = new MDataMap();
		mapParam.put("product_code", product_code);
		mapParam.put("color_style", color_style);
		Map<String, Object> map = DbUp.upTable("pc_skuinfo").dataSqlOne("select sku_code from pc_skuinfo where product_code=:product_code and sku_key=:color_style", mapParam);
		if(map != null && map.get("sku_code") != null) {
			return map.get("sku_code").toString();
		}
		return "";
	}

	/**
	 * 截取尺码 和 款型
	 * 
	 * @param standardAndStyle
	 * @return
	 */
	public List<PcPropertyinfoForFamily> sellerStandardAndStyle(
			String standardAndStyle) {
		String standardAndStyleOneList[] = null;
		String resultValue[] = null;
		List<PcPropertyinfoForFamily> list = new ArrayList<PcPropertyinfoForFamily>();
		PcPropertyinfoForFamily pcPropertyinfoForFamily = null;

		if (standardAndStyle != null && !"".equals(standardAndStyle)) {
			standardAndStyleOneList = standardAndStyle.split("&");
			if (standardAndStyleOneList != null) {
				for (int i = 0; i < standardAndStyleOneList.length; i++) {
					pcPropertyinfoForFamily = new PcPropertyinfoForFamily();
					resultValue = standardAndStyleOneList[i].split("=");
					pcPropertyinfoForFamily.setPropertyKey(resultValue[0]);
					pcPropertyinfoForFamily.setPropertyValue(resultValue[1]);
					list.add(pcPropertyinfoForFamily);
				}
			} else {
				pcPropertyinfoForFamily = new PcPropertyinfoForFamily();
				resultValue = standardAndStyle.split("=");
				pcPropertyinfoForFamily.setPropertyKey(resultValue[0]);
				pcPropertyinfoForFamily.setPropertyValue(resultValue[1]);
				list.add(pcPropertyinfoForFamily);
			}
			return list;
		}
		return null;
	}

	public List<MDataMap> orderStateValue(String orderStatus) {
		List<MDataMap> list = new ArrayList<MDataMap>();
		MDataMap mapParam = new MDataMap();
		mapParam.put("parent_code", "449715390001");
		mapParam.put("define_code", orderStatus);
		if (orderStatus != null && !"".equals(orderStatus)) {
			list = DbUp.upTable("sc_define").queryAll("", "", "", mapParam);
			if (!list.isEmpty() && list != null) {
				return list;
			}
		}
		return null;
	}

	public List<MDataMap> flashSales(String sku_code) {
		List<MDataMap> list = new ArrayList<MDataMap>();
		MDataMap mapParam = new MDataMap();
		mapParam.put("sku_code", sku_code);

		if (sku_code != null && !"".equals(sku_code)) {
			list = DbUp.upTable("oc_flashsales_skuInfo").queryAll("", "", "",
					mapParam);
			if (list != null && !list.isEmpty()) {
				return list;
			}
		}
		return null;
	}

	public int orderCountDeleteFlag(String order_code) {
		MDataMap mapParam = new MDataMap();
		mapParam.put("order_code", order_code);
		mapParam.put("delete_flag", "0");

		if (!"".equals(order_code)) {
			int countPage = DbUp
					.upTable("oc_orderinfo")
					.dataCount(
							"(order_code=:order_code or out_order_code=:order_code) and delete_flag=:delete_flag",
							mapParam);
			return countPage;
		}
		return 0;
	}
	
	public int bigCountDeleteFlag(String bigOrderCode){
		MDataMap mapParam = new MDataMap();
		mapParam.put("big_order_code", bigOrderCode);
		mapParam.put("delete_flag", "0");
		
		if (!"".equals(bigOrderCode)) {
			int countPage = DbUp
					.upTable("oc_orderinfo_upper")
					.dataCount(
							"big_order_code=:big_order_code and delete_flag=:delete_flag",
							mapParam);
			return countPage;
		}
		return 0;
		
	}

	/***
	 * 删除用户的订单[订单状态必须是已经结束]
	 * 
	 * @param orderCode
	 * @param buyerCode
	 * @return 0 删除成功 1删除失败，不存在该订单 2删除失败，该订单未结束 3删除失败
	 */
	public int deleteOrderByBuyer(String orderCode, String buyerCode) {

		MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code", orderCode,
				"buyer_code", buyerCode, "delete_flag", "0");
		if (dm != null && dm.size() > 0) {
			String order_status = dm.get("order_status");
			if ("4497153900010005".equals(order_status)
					|| "4497153900010006".equals(order_status)
					|| "4497153900010004".equals(order_status)) {
				// 更新删除状态
				DbUp.upTable("oc_orderinfo").dataUpdate(
						new MDataMap("order_code", orderCode, "buyer_code",
								buyerCode, "delete_flag", "1", "update_time",
								com.cmall.systemcenter.common.DateUtil
										.getSysDateTimeString()),
						"delete_flag,update_time", "order_code,buyer_code");

				// 此处省略记日志的过程.............

				return 0;
			} else { // 2删除失败，该订单未结束
				return 2;
			}
		} else { // 1删除失败，不存在该订单
			return 1;
		}
	}
	/**
	 * 惠家有88折金额
	 * @param orderCode
	 * @return
	 */
	public  String discountSku(String orderCode){
		BigDecimal sum = new BigDecimal(0);
		BigDecimal discountSum = new BigDecimal(0);
		Order order = new Order();
		List<OrderDetail> list = new ArrayList<OrderDetail>();
		
		if(orderCode!=null && !"".equals(orderCode)){
			order = getOrder(orderCode);
			list = order.getProductList();
			for(OrderDetail orderDetail : list){
				sum = sum.add(new BigDecimal(orderDetail.getSkuPrice().doubleValue() * orderDetail.getSkuNum() * 0.88).setScale(2, BigDecimal.ROUND_HALF_UP));
				//sum += (int)Math.rint(orderDetail.getSkuPrice().intValue() * orderDetail.getSkuNum() * 0.88);
			}
			discountSum = order.getOrderMoney().subtract(discountSum);
		}
		return discountSum.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
	}
	/**
	 * 查询用户的订单
	 * @param buyerCode
	 * @return
	 */
	public List<Map<String, Object>> buyerCodeSelect(String buyerCode){
		//String sql = "select * from oc_orderinfo buyer_code like '%"+buyerCode+"%'"; 
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("buyer_code", buyerCode);
		mDataMap.put("delete_flag", "0");
		
		String sql = "select * from oc_orderinfo where buyer_code=:buyer_code and order_source not in('449715190014','449715190037') and delete_flag=:delete_flag and  out_order_code != ''" +
				"ORDER BY update_time DESC ";
		
		List<Map<String, Object>> list = DbUp.upTable("oc_orderinfo").dataSqlList(sql, mDataMap);
		return list;
	}
	/**
	 * 模糊查询订单信息
	 * @param orderCode
	 * @return
	 */
	public List<Map<String,Object>> orderLikeSelect(String orderCode,String buyerCode){
		MDataMap map = new MDataMap();
		map.put("order_code", orderCode);
		map.put("delete_flag", "0");
		map.put("buyer_code", buyerCode);
		String sql ="select * from oc_orderinfo where order_code like '%"+orderCode+"%' and order_source not in('449715190014','449715190037') and buyer_code=:buyer_code and delete_flag=:delete_flag and  out_order_code != ''  " +
				"ORDER BY update_time DESC ";
		
		List<Map<String,Object>> list = DbUp.upTable("oc_orderinfo").dataSqlList(sql, map);
		
		return list;
	}
	/**
	 * 单用户模糊查询统计
	 * @param orderCode
	 * @param buyerCode
	 * @return
	 */
	public int orderLikeCount(String orderCode,String buyerCode){
		int countPage = 0;
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyerCode);
		mapParam.put("delete_flag", "0");
		mapParam.put("order_code", orderCode);
		
		String sql = "order_code like '%"+orderCode+"%' and order_source not in('449715190014','449715190037') and buyer_code=:buyer_code and delete_flag=:delete_flag and  out_order_code != ''";
		countPage = DbUp.upTable("oc_orderinfo").dataCount(sql, mapParam);
		return countPage;
	}
	
	
	/**
	 * 查询商品信息(按商品名称模糊查询)
	 * 
	 * @param sku_code
	 * @return
	 */
	public List<Map<String, Object>> sellerLikeInformation(String skuName,String skuCode) {
		List<Map<String, Object>> buyerList = new ArrayList<Map<String, Object>>();
		MDataMap mapParam = new MDataMap();
		mapParam.put("sku_code", skuCode);
		
		String sql = "select sku_code,pc_productinfo.product_code as product_code,mainpic_url as 'sku_picurl',sku_name,stock_num,sku_key,sku_keyvalue,product_shortname " +
				"from pc_skuinfo,pc_productinfo " +
				"where pc_skuinfo.product_code = pc_productinfo.product_code and sku_code=:sku_code	and sku_name like '%"+skuName+"%'";
		buyerList = DbUp
				.upTable("pc_skuinfo")
				.dataSqlList(
						sql,
						mapParam);

		if (!buyerList.isEmpty()) {
			return buyerList;
		}
		return null;
	}
	/**
	 * 通过大订单获取所有小订单的商品名称
	 * @param bigOrderCode
	 * @return
	 */
	public String productNameAll(String bigOrderCode){
		MDataMap mDataMap = new MDataMap();
		mDataMap.put("big_order_code", bigOrderCode);
		
		String productName  =  "";
		List<MDataMap> orderinfoList =  DbUp.upTable("oc_orderinfo").queryAll("", "", "", mDataMap);
		for(MDataMap map : orderinfoList){
			productName += map.get("product_name")+"|";
		}
		if(productName.length()>=127){
			productName = productName.substring(0, 127);
		}
		return productName;
	}
	/**
	 * 判断是否是KJT 订单
	 * @param orderCode
	 * @param small_seller_code
	 * @param seller_code
	 * @return
	 */
	public List<Map<String, Object>> ifKJTOrder(String orderCode,String small_seller_code){  
		
		List<Map<String, Object>> mapList = DbUp.upTable("oc_orderinfo").dataSqlList("select * from oc_orderinfo where small_seller_code=:small_seller_code  and order_source not in('449715190014','449715190037')   and   (order_code=:order_code or big_order_code=:order_code)", 
				new MDataMap("small_seller_code",small_seller_code,"order_code",orderCode));
		
		
		return mapList;
	}
	
	
	/**
	 * 获取不展示的订单类型编号
	 * @return 订单类型string  eg:'449715200013','449715200014'
	 */
	public String getNotInOrderType() {
		
		/**
		 * 获取第三方导入订单类型,包括一元购订单和拼好货订单(以上都为不展示的订单类型)
		 */
		String orderTypeQueryWhere = "'449715200013','449715200014'";
		String orderTypeStr = WebHelper.getImportOrderSource();
		if(StringUtils.isNotBlank(orderTypeStr)) {
			// 给逗号分割的内容追加单引号
			if(!orderTypeStr.contains("'") && orderTypeStr.contains(",")){
				orderTypeStr = orderTypeStr.replaceAll(",", "','");
			}
			
			// 添加前置单引号
			if(!orderTypeStr.startsWith("'")){
				orderTypeStr = "'" + orderTypeStr; 
			}
			
			// 添加后置单引号
			if(!orderTypeStr.endsWith("'")){
				orderTypeStr = orderTypeStr + "'"; 
			}
			
			orderTypeQueryWhere += "," + orderTypeStr + "";
		}
		
		return orderTypeQueryWhere;
	}
	
	/**
	 * TV品取消发货原因列表
	 * 
	 * @return
	 */
	public static List<MDataMap> getReasonList(){
		List<MDataMap> reasonList = new ArrayList<MDataMap>();
		MDataMap mDataMap5 = new MDataMap();
		mDataMap5.put("return_reson_code", "C05");
		mDataMap5.put("return_reson", "对商品价格不满");
		reasonList.add(mDataMap5);
		
		MDataMap mDataMap7 = new MDataMap();
		mDataMap7.put("return_reson_code", "C07");
		mDataMap7.put("return_reson", "家人不喜欢/不同意");
		reasonList.add(mDataMap7);
		
		MDataMap mDataMap8 = new MDataMap();
		mDataMap8.put("return_reson_code", "C08");
		mDataMap8.put("return_reson", "没时间收货");
		reasonList.add(mDataMap8);
		
		MDataMap mDataMapA = new MDataMap();
		mDataMapA.put("return_reson_code", "C0A");
		mDataMapA.put("return_reson", "更改地址");
		reasonList.add(mDataMapA);
		
		MDataMap mDataMapB = new MDataMap();
		mDataMapB.put("return_reson_code", "C0B");
		mDataMapB.put("return_reson", "重复购买");
		reasonList.add(mDataMapB);
		
		MDataMap mDataMapE = new MDataMap();
		mDataMapE.put("return_reson_code", "C0E");
		mDataMapE.put("return_reson", "赠品变动");
		reasonList.add(mDataMapE);
		
		MDataMap mDataMapF = new MDataMap();
		mDataMapF.put("return_reson_code", "C0F");
		mDataMapF.put("return_reson", "价格不一致");
		reasonList.add(mDataMapF);
		
		MDataMap mDataMapR = new MDataMap();
		mDataMapR.put("return_reson_code", "C0R");
		mDataMapR.put("return_reson", "不能按时送货");
		reasonList.add(mDataMapR);
		
		MDataMap mDataMapS = new MDataMap();
		mDataMapS.put("return_reson_code", "C0S");
		mDataMapS.put("return_reson", "买其他商品了");
		reasonList.add(mDataMapS);
		
		MDataMap mDataMapT = new MDataMap();
		mDataMapT.put("return_reson_code", "C0T");
		mDataMapT.put("return_reson", "其他原因");
		reasonList.add(mDataMapT);
		
		return reasonList;
	}
	
	public void autoChangeToGoodValueFor15Days() {


		String sql = "SELECT DISTINCT a.order_code code1,a.update_time,a.buyer_code,a.seller_code,c.sku_code,c.product_code pc,c.sku_name sn,d.login_name ln  FROM ordercenter.oc_orderinfo a LEFT JOIN ordercenter.oc_orderdetail c ON a.order_code = c.order_code LEFT JOIN newscenter.nc_order_evaluation b  ON a.order_code = b.order_code and b.order_skuid  = c.sku_code LEFT JOIN membercenter.mc_login_info  d ON a.buyer_code  = d.member_code  WHERE b.order_code IS NULL AND c.gift_flag = '1' AND a.order_status='4497153900010005' AND a.buyer_code NOT IN ('MI141013127809','MI141013140461','MI141014112225','MI141106100061','MI141124100247','MI141130100630','MI141211100657') " + 
				"  AND  (UNIX_TIMESTAMP()-UNIX_TIMESTAMP(update_time))>1296000 AND a.zid > 4650393 ";
		List<Map<String, Object>> dataSqlList = DbUp.upTable("oc_orderinfo").dataSqlList(sql, null);
		if (dataSqlList != null) {
			for(int i =0;i<dataSqlList.size();i++) {
				Map<String, Object> mdm = dataSqlList.get(i);
				try {
					MDataMap mdata = new MDataMap();
					mdata.put("order_code", mdm.get("code1")==null?"":mdm.get("code1").toString());
					mdata.put("order_assessment", "用户未填写评价内容");
					String update_time = mdm.get("update_time").toString();
					//操作时间后推15天
					if(!"".equals(update_time)&&update_time.length()==19) {
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
						try {
							Date date = sdf.parse(update_time);
							Calendar ca = Calendar.getInstance();
							ca.setTime(date);
					        ca.add(Calendar.DATE, 15);// num为增加的天数，可以改变的
					        date = ca.getTime();
					        String enddate = sdf.format(date);
					        update_time =enddate;   
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					mdata.put("oder_creattime", update_time);
					mdata.put("order_name", mdm.get("buyer_code")==null?"":mdm.get("buyer_code").toString());
					mdata.put("manage_code", mdm.get("seller_code")==null?"":mdm.get("seller_code").toString());
					mdata.put("order_skuid", mdm.get("sku_code")==null?"":mdm.get("sku_code").toString());
					mdata.put("product_code", mdm.get("pc")==null?"":mdm.get("pc").toString());
					mdata.put("grade", "5");
					mdata.put("grade_type", "好评");
					mdata.put("sku_name", mdm.get("sn")==null?"":mdm.get("sn").toString());
					mdata.put("user_mobile", mdm.get("ln")==null?"0":mdm.get("ln").toString());
					mdata.put("auto_good_evaluation_flag", "1");
					DbUp.upTable("nc_order_evaluation").dataInsert(mdata);
					
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * 获取订单信息(惠家有)(通过小订单获取)，包含已经评价的订单<br/>
	 * 仅查询订单状态为4497153900010003,4497153900010005
	 * @param buyer_code
	 * @param sellerCode
	 * @return
	 */
	public List<Map<String, Object>> orderInformationSmallV3(String buyer_code, String sellerCode) {
		
		List<Map<String, Object>> buyerNewList = new ArrayList<Map<String, Object>>();
		
		MDataMap mapParam = new MDataMap();
		mapParam.put("buyer_code", buyer_code);
		mapParam.put("delete_flag", "0");
		mapParam.put("seller_code", sellerCode);
		String notInOrderType = getNotInOrderType();
		
		String sql = "SELECT o.order_status, o.order_code, o.order_type, o.create_time, o.due_money, o.out_order_code, o.seller_code, o.big_order_code, o.small_seller_code, o.update_time, o.zid";
		sql += " FROM ordercenter.oc_orderinfo o LEFT JOIN logcenter.lc_orderstatus l ON o.order_code=l.code AND l.now_status='4497153900010005'" //关联订单状态变更表 查签收时间
				+ " AND l.zid=(SELECT l2.zid FROM logcenter.lc_orderstatus l2 WHERE l2.`code`=o.order_code AND l2.now_status='4497153900010005' ORDER BY l2.create_time ASC LIMIT 1) "; //存在多条签收时 取第一条
		sql += " WHERE o.buyer_code = :buyer_code AND o.order_source NOT IN ('449715190014','449715190037') AND o.delete_flag = :delete_flag AND o.seller_code = :seller_code AND o.order_type NOT IN ("+notInOrderType+")";
		sql += " AND o.order_status IN ('4497153900010003','4497153900010005')"; //订单只取待收货、确认收货的
		sql += " AND IFNULL(o.create_time,NOW()) > DATE_SUB(NOW(),INTERVAL 45 day)"; //订单要创建45天内的
		sql += " AND (o.org_ord_id = '' OR (o.org_ord_id != '' and o.order_status != '4497153900010002') )"; //LD品换货新单未出库 则不展示
		sql += " AND IFNULL(l.create_time,NOW()) > DATE_SUB(NOW(),INTERVAL 15 day)"; //确认收货的订单 只取15天内的
		sql += " ORDER BY o.update_time DESC,o.zid DESC";
		
		buyerNewList = DbUp.upTable("oc_orderinfo").dataSqlList(sql, mapParam);
		
		if (!buyerNewList.isEmpty()) {
			return buyerNewList;
		}
		return null;
	}
	
	/**
	 * 全通路订单列表、售后申请列表<br/>
	 * 是否拉取Ld订单
	 */
	public boolean upSyncLDOrder() {
		//取缓存
		String isSyncLdOrder = new PlusSupportLD().upSyncLdOrder();
		if(isSyncLdOrder == null) {
			//取数据库
			MDataMap map = DbUp.upTable("sc_define").one("define_code","44974822");
			if(map != null) {
				isSyncLdOrder = map.get("define_name").toString();
				new PlusSupportLD().fixSyncLdOrder(isSyncLdOrder);//存入缓存
				if("Y".equals(isSyncLdOrder)) {
					return true;
				}
			}
		} else {
			if("Y".equals(isSyncLdOrder)) {
				return true;
			}
		}
		return false;
	}
	
	
	/**
	 * 查询用户某一商品的下单量 <br>
	 *@deprecated 统一走有缓存的重载方法 getProductOrderNum(String productCode)
	 */
	@Deprecated
	public int getProductOrderNum(String smallSeller,String productCode,String  orderStatus) {
		//取缓存
		//Map<String, Object> one = DbUp.upTable("oc_orderdetail").dataSqlOne("select IFNull(count(a.order_code),0) as saleNum from oc_orderdetail a, oc_orderinfo b where a.product_code=:product_code and  a.order_code=b.order_code and  b.order_status=:order_status and b.small_seller_code=:small_seller_code  ", new MDataMap("product_code",productCode,"small_seller_code",smallSeller,"order_status",orderStatus));
		//return Integer.parseInt(one.get("saleNum").toString());
		return getProductOrderNum(productCode);
	}
	
	/**
	 * 查询用户某一商品的下单量
	 * 
	 */
	public int getProductOrderNum(String productCode) {
		return new LoadProductOrderNum().upInfoByCode(new PlusModelQuery(productCode)).getNum();
	}
	
}

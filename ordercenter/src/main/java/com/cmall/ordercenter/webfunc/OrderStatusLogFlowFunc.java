package com.cmall.ordercenter.webfunc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;

import com.cmall.membercenter.support.MemberLoginSupport;
import com.cmall.ordercenter.common.DateUtil;
import com.cmall.ordercenter.common.OrderConst;
import com.cmall.ordercenter.helper.OrderHelper;
import com.cmall.ordercenter.model.OrderStatusLog;
import com.cmall.ordercenter.model.api.ApiOrderStatusChangeNoticInput;
import com.cmall.ordercenter.service.OrderStatusLogService;
import com.cmall.ordercenter.service.api.ApiOrderStatusChangeNotic;
import com.cmall.ordercenter.util.CouponUtil;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.common.CouponConst;
import com.cmall.systemcenter.enumer.JmsNameEnumer;
import com.cmall.systemcenter.jms.JmsNoticeSupport;
import com.cmall.systemcenter.systemface.IFlowFunc;
import com.cmall.systemcenter.util.StringUtility;
import com.srnpr.xmasorder.service.TeslaCrdtService;
import com.srnpr.xmasorder.service.TeslaPpcService;
import com.srnpr.xmaspay.util.PayServiceFactory;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.helper.PlusHelperNotice;
import com.srnpr.xmassystem.service.HjycoinService;
import com.srnpr.xmassystem.service.PlusServiceAccm;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.helper.WebHelper;

public class OrderStatusLogFlowFunc implements IFlowFunc {

	public RootResult BeforeFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		// TODO Auto-generated method stub
		return null;
	}

	public RootResult afterFlowChange(String flowCode, String outCode,
			String fromStatus, String toStatus, MDataMap mSubMap) {
		MDataMap orderinfo=DbUp.upTable("oc_orderinfo").one("order_code",mSubMap.get("order_code"));
		RootResult result = new RootResult();
		if(mSubMap!=null){
			if(mSubMap.containsKey("isFK")){
				DbUp.upTable("oc_orderinfo").dataExec("update oc_orderinfo set payed_money=payed_money+:payed_money,due_money=due_money-:payed_money where order_code=:order_code", mSubMap);
			}
		}
		
		//在次添加订单辅助状态
		if("4497153900010001".equals(fromStatus)&&"4497153900010006".equals(toStatus)){
			if(mSubMap.containsKey("order_code")){
				DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status_ext","4497153900140001","order_code",mSubMap.get("order_code")), "order_status_ext", "order_code");
			}
			
			//获取大订单号 取消支付
			String big_order_code=(String)DbUp.upTable("oc_orderinfo").dataGet("big_order_code", "order_code=:order_code", new MDataMap("order_code",mSubMap.get("order_code")));
			PayServiceFactory.getInstance().getWechatTradeCancelProcess().process(big_order_code);
			PayServiceFactory.getInstance().getAlipayTradeCancelProcess().process(big_order_code);
		}else if("4497153900010002".equals(fromStatus)&&"4497153900010006".equals(toStatus)){
			
			if(mSubMap.containsKey("order_code")){
				DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status_ext","4497153900140005","order_code",mSubMap.get("order_code")), "order_status_ext", "order_code");
			}
		}else if(("4497153900010003".equals(fromStatus) ||"4497153900010005".equals(fromStatus)) &&"4497153900010006".equals(toStatus)){
			
			if(mSubMap.containsKey("order_code")){
				DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status_ext","4497153900140004","order_code",mSubMap.get("order_code")), "order_status_ext", "order_code");
			}
		}
		
		// 如果已经存在交易成功则不再重复记录交易成功日志，其他情况正常记录日志
		if(!"4497153900010005".equals(toStatus) || DbUp.upTable("lc_orderstatus").count("code", mSubMap.get("order_code"), "now_status", "4497153900010005") == 0) {
			//调用添加订单的日志。
			OrderStatusLogService osls = new OrderStatusLogService();
			OrderStatusLog log = new OrderStatusLog();
			log.setCode(outCode);
			if(mSubMap!=null){
				if(mSubMap.containsKey("order_code"))
					log.setCode(mSubMap.get("order_code"));
			}
			
			log.setInfo("");
			
			
			if(mSubMap!=null){
//				if(mSubMap.containsKey("manage_code"))
//					log.setCreateUser(mSubMap.get("manage_code"));
				if(mSubMap.containsKey("userCodex"))
					log.setCreateUser(mSubMap.get("userCodex"));
				
				else
					log.setCreateUser("system");
			}else{
				log.setCreateUser("system");
			}
			
			
			log.setCreateTime(DateUtil.getSysDateTimeString());
			log.setOldStatus(fromStatus);
			//未处理
			log.setNowStatus(toStatus);
			//
			osls.AddOrderStatusLogService(log);
		}
		
		if("4497153900010006".equals(toStatus)){
			// 取消订单时，如果用了惠豆支付则需要返还惠豆
			//HjybeanService.addHjyBeanTimer(HjyBeanExecType.CANCEL, mSubMap.get("order_code"), mSubMap.get("order_code"));
			
			if("4497153900010001".equals(fromStatus) 
					|| "4497153900010002".equals(fromStatus)
					|| "4497153900010008".equals(fromStatus)
					|| "4497153900010003".equals(fromStatus)
					|| "4497153900010005".equals(fromStatus)){
				// 取消订单退还使用的积分
				try {
					new PlusServiceAccm().addExecInfoForCancel(mSubMap.get("order_code"), "订单取消");
				} catch (Exception e) {
					e.printStackTrace();
					WebHelper.errorMessage(mSubMap.get("order_code"), "cancelForAccmAmt", 1,"cancelForAccmAmt on OrderStatusLogFlowFunc", "", e);
				}
				//取消订单，扣除预估惠币
				HjycoinService.addExecTimer("449746990036",mSubMap.get("order_code"));
				//判断有没有推广人
				if(DbUp.upTable("fh_tgz_order_detail").count("order_code",mSubMap.get("order_code"))>0) {//是推广赚订单
					//取消订单扣除推广人预估惠币
					HjycoinService.addExecTimer("449746990042",mSubMap.get("order_code"));
				}
				// 取消订单退还使用的惠币
				try {
					new HjycoinService().addExecInfoForCancel(mSubMap.get("order_code"), "订单取消");
				} catch (Exception e) {
					e.printStackTrace();
					WebHelper.errorMessage(mSubMap.get("order_code"), "cancelForHjycoin", 1,"cancelForAccmAmt on OrderStatusLogFlowFunc", "", e);
				}
				// 取消订单退还使用的储值金
				try {
					new TeslaPpcService().addExecInfoForCancel(mSubMap.get("order_code"), "订单取消");
				} catch (Exception e) {
					e.printStackTrace();
					WebHelper.errorMessage(mSubMap.get("order_code"), "cancelForPpcAmt", 1,"cancelForPpcAmt on OrderStatusLogFlowFunc", "", e);
				}
				// 取消订单退还使用的暂存款
				try {
					new TeslaCrdtService().addExecInfoForCancel(mSubMap.get("order_code"), "订单取消");
				} catch (Exception e) {
					e.printStackTrace();
					WebHelper.errorMessage(mSubMap.get("order_code"), "cancelForCrdtAmt", 1,"cancelForCrdtAmt on OrderStatusLogFlowFunc", "", e);
				}
				
				// 如果是取消发货,扣减惠惠农场用户水滴
				try {
					// 查询该订单是否赠送雨滴
					MDataMap farmOrder = DbUp.upTable("sc_huodong_farm_order").one("order_code",mSubMap.get("order_code"),"is_cancel","0","is_give_water","1");
					if(farmOrder != null) {
						// 如果赠送过雨滴,则添加扣减雨滴和进度定时
						JobExecHelper.createExecInfo("449746990046", mSubMap.get("order_code"), null);
					}
				} catch (Exception e) {
					e.printStackTrace();
					WebHelper.errorMessage(mSubMap.get("order_code"), "cancelOrderReduceFarmWater", 9,"cancelOrderReduceFarmWater on OrderStatusLogFlowFunc", "", e);
				}
			}
			
			// 取消发货时如果全部子单都已经取消则还原优惠券
			if("4497153900010002".equals(fromStatus)
					|| "4497153900010008".equals(fromStatus)){
				CouponUtil couponUtil = new CouponUtil();
				// 同一个大单号下面关联的优惠券都还原
				String bigOrderCode = (String)DbUp.upTable("oc_orderinfo").dataGet("big_order_code", "", new MDataMap("order_code",mSubMap.get("order_code")));
				if(couponUtil.isCanRollbackCoupon(bigOrderCode)) {
					couponUtil.rollbackCoupon(bigOrderCode);
				}
			}
			
			// 订单变成交易失败时清除用户有效订单缓存
			// PlusSupportMember#ACTIVE_ORDER_SUM
			XmasKv.upFactory(EKvSchema.Member).hdel(orderinfo.get("buyer_code"), "activeOrderSum");
		}
		
		//售后信息
		if("4497153900010005".equals(toStatus)){
			String small_seller_code=orderinfo.get("small_seller_code");
			String order_code=orderinfo.get("order_code");
//			if(StringUtils.startsWith(small_seller_code, "SF031")){//商户订单
				//修改为根据商品类型判断
			if("SI2003".equals(small_seller_code) || StringUtils.isNotBlank(WebHelper.getSellerType(small_seller_code))){//此处为空暂且判定为惠家有商品，此处有坑，我先挖，能埋了谁算谁。系统该重构了！！！
//				MDataMap payMap=DbUp.upTable("oc_order_pay").oneWhere("", "", "order_code=:order_code and pay_type in ('449746280006','449746280007')", "order_code",order_code);
//				if(payMap==null||payMap.isEmpty()){
					DbUp.upTable("oc_orderdetail").dataUpdate(new MDataMap("order_code",order_code,"flag_asale","0"), "flag_asale", "order_code");
//				}
				
//				List<MDataMap> detailList=DbUp.upTable("oc_orderdetail").queryByWhere("order_code",order_code,"gift_flag","1");
//				if(detailList!=null&&!detailList.isEmpty()){
//					
//					for (MDataMap detail : detailList) {
//						String sku_code=detail.get("sku_code");
//						
//						//无售后库存的不再售后
//						int maxReturnNum = new ReturnGoodsService().getAchangeNum(order_code, sku_code);
//						
//						if(maxReturnNum>0){
//							DbUp.upTable("oc_orderdetail").dataUpdate(new MDataMap("order_code",order_code,"sku_code",sku_code,"flag_asale","0"), "flag_asale", "order_code,sku_code");
//						}
//					}
//				}
				
			}
			
			// 订单签收送惠豆
			//HjybeanService.addHjyBeanTimer(HjyBeanExecType.SUCCESS, order_code, order_code);
			
			new PlusServiceAccm().addExecInfoForSuccess(order_code);
		}
		
		// 如果从未发货到交易失败做促销库存以及普通商品库存恢复
		if("4497153900010002".equals(fromStatus) && "4497153900010006".equals(toStatus)){
			PlusHelperNotice.onCancelOrderForStock(mSubMap.get("order_code"));
			
			//积分商城订单，返活动库存
			//积分商城订单退货退活动库存
			List<Map<String, Object>> detailList = DbUp.upTable("oc_orderdetail").dataSqlList("select order_code, sku_code, sku_num, product_code from oc_orderdetail where order_code = :order_code", mSubMap);
			for(Map<String, Object> detail : detailList) {
				Map<String, Object> map = DbUp.upTable("oc_order_activity").dataSqlOne("select activity_code from oc_order_activity where order_code = '" + MapUtils.getString(detail, "order_code", "") + 
						"' and (activity_type = '4497472600010022' or activity_type = '4497472600010023') and sku_code = '" + MapUtils.getString(detail, "sku_code", "") + 
						"' and product_code = '" + MapUtils.getString(detail, "product_code", "") + "'", new MDataMap());
				if(map != null) {
					DbUp.upTable("fh_apphome_channel_details").upTemplate().update("update fh_apphome_channel_details set allow_count = allow_count + " + MapUtils.getString(detail, "sku_num", "") + 
							" where uid = '" + MapUtils.getString(map, "activity_code", "") + "'", new HashMap<String, Object>());
				}
			}
		}
		
		
		/**各种发优惠券*******start******/
		if("4497153900010005".equals(toStatus)){//为交易成功时送券
			MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code",mSubMap.get("order_code"));
			if(AppConst.MANAGE_CODE_HOMEHAS.equals(dm.get("seller_code")) || AppConst.MANAGE_CODE_CDOG.equals(dm.get("seller_code"))){//在线支付下单收货送
				String moblie = new MemberLoginSupport().getMoblie(dm.get("buyer_code"));
				if(StringUtility.isNotNull(moblie)&&StringUtility.isNotNull(dm.get("pay_type"))){
					String payType = dm.get("pay_type");
					if(OrderConst.ONLINEPAYPAL.equals(payType)){
						JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnDistributeCoupon,
								CouponConst.pay_re_coupon,new MDataMap("mobile", moblie, "manage_code", dm.get("seller_code")));
					}else if(OrderConst.COD.equals(payType)){
						JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnDistributeCoupon,
								CouponConst.al_re__coupon,new MDataMap("mobile", moblie, "manage_code", dm.get("seller_code")));
					}
				}
				//524:避免被邀请人下单后邀请人再次送券的情况
//				List<MDataMap> list = DbUp.upTable("oc_orderinfo").queryAll("zid", "", "buyer_code=:buyer_code and order_status!='4497153900010006'", new MDataMap("buyer_code",dm.get("buyer_code")));
//				if(list!=null&&!list.isEmpty()&&list.size()==1){
//				List<MDataMap> li = DbUp.upTable("gc_recommend_info").queryByWhere("recommended_mobile",moblie,"source","1");
//					if(li!=null&&!li.isEmpty()){
//						for (MDataMap mDataMap : li) {
//							if(StringUtils.isNotBlank(mDataMap.get("mobile"))){
//								JmsNoticeSupport.INSTANCE.sendQueue(JmsNameEnumer.OnDistributeCoupon,
//										CouponConst.referees__coupon,new MDataMap("mobile", mDataMap.get("mobile"), "manage_code", dm.get("seller_code")));
//							}
//						}
//					}
//				}
			}
		}
		/**各种发优惠券*******end******/
		
		//若为多彩宝订单 则通知多彩宝 订单状态变更 （不包括发货状态）
		if(mSubMap!=null && mSubMap.containsKey("order_code") && !"4497153900010003".equals(toStatus)){
			MDataMap dm = DbUp.upTable("oc_orderinfo").one("order_code", mSubMap.get("order_code"));
			if(dm.containsKey("order_source") && "449715190014".equals(dm.get("order_source"))){
				//若为多彩宝取消订单 则不发送状态变更通知
//				if(!"4497153900010006".equals(toStatus) 
//						|| ("4497153900010006".equals(toStatus) && !mSubMap.containsKey("isDcb"))){
				ApiOrderStatusChangeNoticInput inputParam = new ApiOrderStatusChangeNoticInput();
				inputParam.setJyOrderCode(mSubMap.get("order_code"));
				inputParam.setStatus(OrderHelper.convertStatusCode(toStatus));
				inputParam.setStatusCode(toStatus);
				inputParam.setUpdateTime(DateUtil.getSysDateTimeString());
				//添加物流信息
				MDataMap oneWhere = DbUp.upTable("oc_order_shipments").oneWhere("logisticse_name,logisticse_code,waybill", "", "", "order_code", inputParam.getJyOrderCode());
				if(null != oneWhere){
					inputParam.setLogisticseName(oneWhere.get("logisticse_name"));
					inputParam.setLogisticseCode(oneWhere.get("logisticse_code"));
					inputParam.setWaybill(oneWhere.get("waybill"));
				}
				new ApiOrderStatusChangeNotic().Process(inputParam, new MDataMap());
//				}
			}
		}
		// 对日志的处理
		result.setResultCode(1);
		return result;
	}

}

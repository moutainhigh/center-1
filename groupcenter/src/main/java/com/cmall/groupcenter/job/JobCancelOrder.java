package com.cmall.groupcenter.job;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.quartz.JobExecutionContext;

import com.cmall.groupcenter.service.KaolaOrderService;
import com.cmall.ordercenter.model.api.ApiCancelOrderResult;
import com.cmall.ordercenter.model.api.GiftVoucherInfo;
import com.cmall.ordercenter.service.OrderService;
import com.cmall.systemcenter.common.AppConst;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.xmassystem.enumer.EKvSchema;
import com.srnpr.xmassystem.top.XmasSystemConst;
import com.srnpr.xmassystem.up.XmasKv;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapcom.topapi.RootResult;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/***
 * 取消订单<br>
 * 满足条件：订单在线支付、15分钟未付款、有商品参与闪购 <br>
 * 每五分钟执行一次
 * 
 * @author jlin
 *
 */
public class JobCancelOrder extends RootJob {

	public void doExecute(JobExecutionContext context) {

		OrderService os = new OrderService();

		List<MDataMap> orderList = DbUp
				.upTable("oc_orderinfo")
				.queryAll(
						"order_code,buyer_code,out_order_code,due_money ",
						"",
						" order_status='4497153900010001' and  pay_type='449716200001' and seller_code in ('SI2003','SI2009','SI3003') and create_time<:nowHour24 ",
						new MDataMap("nowHour24", DateUtil.toString(
								DateUtil.addSecond(new Date(), - (int)XmasSystemConst.CANCEL_ORDER_TIME),
								DateUtil.sdfDateTime)));
		if (orderList != null && orderList.size() > 0) {
			for (MDataMap map : orderList) {

				String order_code = map.get("order_code");
				String buyer_code = map.get("buyer_code");
				String out_order_code = map.get("out_order_code");
				BigDecimal due_money = new BigDecimal(map.get("due_money"));

				MDataMap mWhereMap = new MDataMap("order_code", order_code);

				if (DbUp.upTable("oc_order_activity")
						.dataCount(
								"order_code =:order_code and activity_type='449715400004'",
								mWhereMap) > 0) {
					continue;
				}

				ApiCancelOrderResult rr = os.CancelOrderForList(order_code);

				if (rr.getResultCode() == 1) {
					bLogInfo(0, "success to cancel order by " + order_code);

					// 向 oc_order_cancel_h 插入一条数据，定时请求惠家有接口取消订单
					String now = DateUtil.getSysDateTimeString();
					DbUp.upTable("oc_order_cancel_h").insert("order_code",
							order_code, "buyer_code", buyer_code,
							"out_order_code", out_order_code, "call_flag", "1",
							"create_time", now, "update_time", now, "canceler",
							"system");
					
					List<GiftVoucherInfo> reWriteLD = rr.getReWriteLD();
					if(reWriteLD != null && reWriteLD.size() > 0) {
						//回写礼金券
						new OrderService().reWriteGiftVoucherToLD(reWriteLD);
					}
				}

			}
		}
		
		
		// 新加逻辑 取出所有未支付的订单 判断是否参与了促销系统 如果参与了促销系统 则根据促销系统取消订单
		List<MDataMap> orderListIc = DbUp.upTable("oc_orderinfo").queryAll("order_code,buyer_code,out_order_code,due_money,small_seller_code ",""," order_status='4497153900010001' and  pay_type='449716200001' and seller_code in ('SI2003','SI2009','SI3003') ",new MDataMap());
		if (orderListIc != null && orderListIc.size() > 0) {
			for (MDataMap map : orderListIc) {

				String order_code = map.get("order_code");				
				String buyer_code = map.get("buyer_code");
				String out_order_code = map.get("out_order_code");
				String small_seller_code = map.get("small_seller_code");

				// 判断取消缓存中有该订单号且预计取消时间小于当前时间 则开始进入取消流程
				if (XmasKv.upFactory(EKvSchema.TimeCancelOrder).exists(order_code)&& XmasKv.upFactory(EKvSchema.TimeCancelOrder).get(order_code).compareTo(DateHelper.upNow()) <= 0) {

					ApiCancelOrderResult rr = os.CancelOrderForList(order_code);
					if (rr.getResultCode() == 1) {
						bLogInfo(0, "success to cancel order by " + order_code);

						// 向 oc_order_cancel_h 插入一条数据，定时请求惠家有接口取消订单
						String now = DateUtil.getSysDateTimeString();
						DbUp.upTable("oc_order_cancel_h").insert("order_code", order_code, "buyer_code", buyer_code,"out_order_code", out_order_code, "call_flag", "1", "create_time", now, "update_time",now, "canceler", "system");
					
						//如果是网易考拉订单，需要调用网易考拉取消订单的接口
						if(AppConst.MANAGE_CODE_WYKL.equals(small_seller_code)) {
							new KaolaOrderService().cancelKaolaOrder(order_code);
						}
						
						//判断如果是拼团单，则修改拼团状态
						updateCollageStatus(order_code);
						
						List<GiftVoucherInfo> reWriteLD = rr.getReWriteLD();
						if(reWriteLD != null && reWriteLD.size() > 0) {
							//回写礼金券
							new OrderService().reWriteGiftVoucherToLD(reWriteLD);
						}
					}

				}
			}
		}		
	}
	
	private void updateCollageStatus(String orderCode) {
		//判断是否为拼团单
		MDataMap ptMap = DbUp.upTable("sc_event_collage_item").onePriLib("collage_ord_code", orderCode);
		if(ptMap != null) {
			String collageCode = ptMap.get("collage_code");
			String memberType = ptMap.get("collage_member_type");
			if("449748310001".equals(memberType)) {//拼团单为团长创建
				//修改拼团主表状态为拼团失败
				DbUp.upTable("sc_event_collage").dataUpdate(new MDataMap("collage_code", collageCode, "collage_status", "449748300003"), "collage_status", "collage_code");
			}
		}
	}
	
	public static void main(String[] args) {
//		JobCancelOrder cancelOrder = new JobCancelOrder();
//		cancelOrder.doExecute(null);
		
//		DateUtil.addMinute(new Date(), -20);
		
		String s = DateUtil.toString(DateUtil.addMinute(new Date(), -20),DateUtil.sdfDateTime);
		System.out.println(s);
	}
}

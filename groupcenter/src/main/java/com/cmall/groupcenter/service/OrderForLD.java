package com.cmall.groupcenter.service;

import java.math.BigDecimal;
import java.util.List;

import com.cmall.groupcenter.homehas.RsyncCancelOrderReal;
import com.cmall.groupcenter.homehas.model.RsyncModelOrderInfo;
import com.cmall.membercenter.memberdo.MemberConst;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.srnpr.xmasorder.enumer.ETeslaExec;
import com.srnpr.xmasorder.model.TeslaModelOrderDetail;
import com.srnpr.xmasorder.model.TeslaModelShowGoods;
import com.srnpr.xmasorder.period.TeslaPeriodOrderForDistributor;
import com.srnpr.xmasorder.x.TeslaXOrder;
import com.srnpr.xmasorder.x.TeslaXResult;
import com.srnpr.xmassystem.util.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.webmodel.MWebResult;

/***
 * LD 订单处理类
 * @author jlin
 *
 */
public class OrderForLD extends BaseClass {

	/**
	 * 分销订单
	 * @param distributorOrders
	 */
	public void distOrder(List<RsyncModelOrderInfo> distributorOrders) {
		
		//此时的心情：千万只草泥马在心头狂奔
		//若订单同步失败，异步的方式通知LD，需求暂未定
		//支持一单多货的情况，拆单后多个订单公用一个LD的订单编号
		
		// 合单
		Multimap<String, RsyncModelOrderInfo> orderMap = ArrayListMultimap.create();
		for (RsyncModelOrderInfo rsyncModelOrderInfo : distributorOrders) {
			orderMap.put(rsyncModelOrderInfo.getYc_orderform_num(), rsyncModelOrderInfo);
		}

		for (String orderid : orderMap.keys()) {
			// 判断是否重复订单
			if (DbUp.upTable("oc_orderinfo").count("out_order_code", orderid) < 1) {

				TeslaXOrder teslaXOrder = adapterOrder((List<RsyncModelOrderInfo>) orderMap.get(orderid));
				TeslaXResult teslaXResult = new TeslaXResult();
				try {
					teslaXResult = new TeslaPeriodOrderForDistributor().doRefresh(teslaXOrder);
				} catch (Exception e) {
					teslaXResult.inErrorMessage(918558005);
					e.printStackTrace();
				}finally {
					//日志记录
					DbUp.upTable("lc_dis_order").dataInsert(new MDataMap("order_code",teslaXOrder.getUorderInfo().getBigOrderCode(),"out_order_code",orderid,"result_code",String.valueOf(teslaXResult.getResultCode()),"result_msg",teslaXResult.getResultMessage(),"create_time",DateUtil.getSysDateTimeString()));
				}
				
			}
		}
	}
	
	/**
	 * 把LD订单转换为标准的订单
	 * @param rsyncModelOrderInfo
	 * @return
	 */
	private TeslaXOrder adapterOrder(List<RsyncModelOrderInfo> distributorOrders) {

		RsyncModelOrderInfo uorderInfo=distributorOrders.get(0);
		TeslaXOrder teslaXOrder = new TeslaXOrder();
		
		for (RsyncModelOrderInfo rsyncModelOrderInfo : distributorOrders) {
			
			TeslaModelOrderDetail orderDetail = new TeslaModelOrderDetail();
			orderDetail.setProductCode(rsyncModelOrderInfo.getAq_good_id());
			orderDetail.setSkuCode(rsyncModelOrderInfo.getAq_sku_id());
			orderDetail.setSkuNum(rsyncModelOrderInfo.getYc_goods_count());
			teslaXOrder.getOrderDetails().add(orderDetail);
			
			//草泥马再次狂奔中
			TeslaModelShowGoods showGoods = new TeslaModelShowGoods();
			showGoods.setProductCode(rsyncModelOrderInfo.getAq_good_id());
			showGoods.setSkuCode(rsyncModelOrderInfo.getAq_sku_id());
			showGoods.setSkuNum(rsyncModelOrderInfo.getYc_goods_count());
			teslaXOrder.getShowGoods().add(showGoods);
			
			
			//计算改详情金额，后端有金额校验	成交价*商品数量
			teslaXOrder.setCheck_pay_money(teslaXOrder.getCheck_pay_money().add(rsyncModelOrderInfo.getYc_after_base_price().multiply(new BigDecimal(rsyncModelOrderInfo.getYc_goods_count()))));
		}
		
		// 订单基本信息
		teslaXOrder.getUorderInfo().setBuyerCode(bConfig("groupcenter.AQY_code"));
		teslaXOrder.getUorderInfo().setSellerCode(MemberConst.MANAGE_CODE_HOMEHAS);
		teslaXOrder.getUorderInfo().setOrderType("449715200012");
		teslaXOrder.getUorderInfo().setOrderSource("449715190009");//统一为分销订单
//		teslaXOrder.getUorderInfo().setPayType(bConfig("groupcenter.FX_PAY_TYPE"));
		teslaXOrder.getUorderInfo().setPayType("449716200001");
		
		//其他扩展信息
		teslaXOrder.getOrderOther().setOut_order_code(uorderInfo.getYc_orderform_num());
		teslaXOrder.getStatus().setExecStep(ETeslaExec.iqiyi);
		
		// 订单地址
		teslaXOrder.getAddress().setAddress(uorderInfo.getDlv_addr_seq());
		teslaXOrder.getAddress().setAreaCode(uorderInfo.getYc_city());
		teslaXOrder.getAddress().setMobilephone(uorderInfo.getReceive_mobile());
		teslaXOrder.getAddress().setReceivePerson(uorderInfo.getYc_claimuser());
		
		return teslaXOrder;
	}
	
	
	/**
	 * 取消LD订单
	 * @param out_order_code
	 * @param canceler
	 * @param app_code
	 * @return
	 */
	public MWebResult cancelOrderForLD(String out_order_code,String canceler,String app_code){
		
		RsyncCancelOrderReal cancelOrderReal = new RsyncCancelOrderReal();
		
		cancelOrderReal.upRsyncRequest().setCan_rsn_cd("C4"); //取消原因  系统未记取消原因，所以统一使用 取消_顾客_改变心意
		cancelOrderReal.upRsyncRequest().setCan_rsn_cd("C0T");
		cancelOrderReal.upRsyncRequest().setMdf_id("app");
		cancelOrderReal.upRsyncRequest().setOrd_id(out_order_code);
		cancelOrderReal.upRsyncRequest().setSubsystem("app");
		
		if (MemberConst.MANAGE_CODE_HPOOL.equals(app_code)) {
			cancelOrderReal.upRsyncRequest().setMdf_id("web");
			cancelOrderReal.upRsyncRequest().setSubsystem("001");
		}
		
		cancelOrderReal.doRsync();
		
		return cancelOrderReal.getRsyncResult();
	}
	
}

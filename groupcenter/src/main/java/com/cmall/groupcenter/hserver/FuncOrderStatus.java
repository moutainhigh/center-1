package com.cmall.groupcenter.hserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.cmall.groupcenter.hserver.model.FuncOrderStatusRequest;
import com.cmall.groupcenter.hserver.model.OrderStatus;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.xmassystem.enumer.HjyBeanExecType;
import com.srnpr.xmassystem.service.HjybeanService;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;

/**
 * 订单状态处理类
 * 
 * @author jlin
 *
 */
public class FuncOrderStatus implements IAcceptFunc<FuncOrderStatusRequest> {

	
	@Override
	public AcceptResult doProcess(FuncOrderStatusRequest request) {

		AcceptResult acceptResult = new AcceptResult();
		
		List<OrderStatus> results = request.getResults();
		
		acceptResult.setProcessNum(results.size());
		
		if (results != null) {
			
			// 先循环一次确认订单状态，避免一单多商品出现多个订单状态
			Map<String,String> statusMap = new HashMap<String, String>();
			for (OrderStatus orderStatus : results) {
				statusMap.put(orderStatus.getORD_ID(), statusMapper(orderStatus.getORD_STAT_CD(), orderStatus.getCOD_STAT_CD(), ""));
			}
			
			for (OrderStatus orderStatus : results) {
				
				if(orderStatus==null ){
					continue;
				}
				
				String ord_id = orderStatus.getORD_ID();
//				String ord_seq = orderStatus.getORD_SEQ();
				String ord_stat_cd = orderStatus.getORD_STAT_CD();
				String cod_stat_cd = orderStatus.getCOD_STAT_CD();
//				String change_cd = orderStatus.getCHANGE_CD();

				if(StringUtils.isBlank(ord_id)){
					continue;
				}
				
				//MDataMap orderInfo = DbUp.upTable("oc_orderinfo").one("out_order_code", ord_id);
				//List<MDataMap> orderList = DbUp.upTable("oc_orderinfo").queryByWhere("out_order_code", ord_id);
				// 直接使用主库查询，避免从库延迟造成的数据重复插入
				List<Map<String, Object>> dataList = DbUp.upTable("oc_orderinfo").upTemplate().queryForList("select order_code,order_status,pay_type from oc_orderinfo where out_order_code = :out_order_code", new MDataMap("out_order_code", ord_id));
				MDataMap orderInfo = null;
				if(dataList != null && !dataList.isEmpty()){
					// 如果存在2个订单对应同一个外部订单号，则只更新DD开头的订单
					for(Map<String, Object> mp : dataList){
						orderInfo = new MDataMap(mp);
						if(orderInfo.get("order_code").startsWith("DD")){
							break;
						}
					}
				}
				
				if (orderInfo == null || orderInfo.isEmpty()) {
					continue;
				}

				String order_status = orderInfo.get("order_status");
				String order_code = orderInfo.get("order_code");
				String new_status = statusMap.get(ord_id);
				if (StringUtils.isBlank(new_status)) {
					continue;
				}
				
				if(order_status.equals(new_status)){
					continue;
				}
				
				// 忽略订单受理状态，避免把惠家有订单的已付款状态给修改为未付款
				if("4497153900010001".equals(new_status)){
					continue;
				}
				
				// 如果数据库已经是发货状态则不再更新为未发货
				if("4497153900010002".equals(new_status) && "4497153900010003".equals(order_status)){
					continue;
				}
				
				// 订单状态是取消中的在同步订单的定时任务中进行处理，此处不再处理
				if("4497153900010008".equals(order_status)) {
					continue;
				}
				
				// 订单状态是已签收则此处不再处理
				if("4497153900010005".equals(order_status)) {
					continue;
				}
				
				DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status",new_status,"update_time",DateUtil.getSysDateTimeString(),"order_code",order_code), "order_status,update_time", "order_code");
				DbUp.upTable("lc_orderstatus").dataInsert(new MDataMap("code",order_code,"create_time",DateUtil.getSysDateTimeString(),"create_user","system","old_status",order_status,"now_status",new_status,"info","FuncOrderStatus"));
			
				if("4497153900010005".equals(new_status)){
					// 订单签收送惠豆
					HjybeanService.addHjyBeanTimer(HjyBeanExecType.SUCCESS, order_code, order_code);
					//订单交易成功，判断是否是分销单，如果是，写入定时计算可提现收入
					if(DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990028") <= 0) {
						JobExecHelper.createExecInfo("449746990028", order_code, DateUtil.addMinute(28800));
					}
					if(DbUp.upTable("fh_share_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990033") <= 0) {
						JobExecHelper.createExecInfo("449746990033", order_code, DateUtil.addMinute(21600));
					}
				}
				
				if("4497153900010006".equals(new_status)){
					// 订单取消返还惠豆
					HjybeanService.addHjyBeanTimer(HjyBeanExecType.CANCEL, order_code, order_code);
					//取消订单，判断是否是分销单，如果是，写入取消订单分销定时
					if(DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990029") <= 0) {
						JobExecHelper.createExecInfo("449746990029", order_code, DateUtil.getSysDateTimeString());
					}
				}
				acceptResult.setSuccessNum(acceptResult.getSuccessNum()+1);
			}
		}

		return acceptResult;
	}

	// ord_stat_cd
	// 10 订单受理
	// 20 入款确认
	// 30 欠交订单
	// 40 出库指示
	// 50 出库确定
	// 60 完成出库
	// 91 受理后取消
	// 92 入款后取消
	// 93 取消欠交订单
	// 94 出库指示后取消
	// cod_stat_cd
	// 30 配送中
	// 31 拒收
	// 40 丢失
	// 41 上门取货丢失
	// 90 签收
	// 91 销售退货

	// 编号: 4497153900010001 名称: 下单成功-未付款
	// 编号: 4497153900010002 名称: 下单成功-未发货
	// 编号: 4497153900010003 名称: 已发货
	// 编号: 4497153900010004 名称: 已收货
	// 编号: 4497153900010005 名称: 交易成功
	// 编号: 4497153900010006 名称: 交易失败
	private String statusMapper(String ord_stat_cd, String cod_stat_cd, String pay_type) {

		if ("10".equals(ord_stat_cd)) {
			// 货到付款订单没有订单受理状态
			//if ("449716200002".equals(pay_type)) {
			//	return "4497153900010002";
			//}
			return "4497153900010001";
		}

		if ("20".equals(ord_stat_cd) || "40".equals(ord_stat_cd) || "30".equals(ord_stat_cd) || "50".equals(ord_stat_cd)) {
			return "4497153900010002";
		}

		if ("60".equals(ord_stat_cd)) {
			if ("30".equals(cod_stat_cd) || "40".equals(cod_stat_cd)) {
				return "4497153900010003";
			}

			if ("90".equals(cod_stat_cd) || "91".equals(cod_stat_cd)) {
				return "4497153900010005";
			}

			if ("31".equals(cod_stat_cd)) {
				return "4497153900010006";
			}
		}

		if ("91".equals(ord_stat_cd) || "92".equals(ord_stat_cd) || "93".equals(ord_stat_cd) || "94".equals(ord_stat_cd)) {
			return "4497153900010006";
		}

		return "";
	}
	
}

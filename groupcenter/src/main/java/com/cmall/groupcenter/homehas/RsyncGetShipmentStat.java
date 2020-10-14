package com.cmall.groupcenter.homehas;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ArrayUtils;

import com.cmall.dborm.txmodel.groupcenter.GcExtendOrderStatusHomehas;
import com.cmall.groupcenter.homehas.config.RsyncConfigGetShipmentStat;
import com.cmall.groupcenter.homehas.model.RsyncDateCheck;
import com.cmall.groupcenter.homehas.model.RsyncModelShipmentStat;
import com.cmall.groupcenter.homehas.model.RsyncRequestGetShipmentStat;
import com.cmall.groupcenter.homehas.model.RsyncResponseGetShipmentStat;
import com.cmall.groupcenter.homehas.model.RsyncResult;
import com.cmall.groupcenter.service.HomehasShipmentStatService;
import com.cmall.systemcenter.common.DateUtil;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.JobExecHelper;
import com.srnpr.zapweb.webmodel.MWebResult;

/**
 * 同步一定时间范围内的配送信息
 * 
 * @author srnpr
 * 
 */
public class RsyncGetShipmentStat
		extends
		RsyncHomeHas<RsyncConfigGetShipmentStat, RsyncRequestGetShipmentStat, RsyncResponseGetShipmentStat> {

	final static RsyncConfigGetShipmentStat CONFIG_GET_SHIPMENT_STAT = new RsyncConfigGetShipmentStat();

	public RsyncConfigGetShipmentStat upConfig() {
		return CONFIG_GET_SHIPMENT_STAT;
	}

	public RsyncRequestGetShipmentStat upRsyncRequest() {

		RsyncRequestGetShipmentStat request = new RsyncRequestGetShipmentStat();

		RsyncDateCheck rsyncDateCheck = upDateCheck(upConfig());
		request.setStart_time(rsyncDateCheck.getStartDate());
		request.setEnd_time(rsyncDateCheck.getEndDate());
		
		return request;
	}

	HomehasOrderProcess homehasOrderProcess = new HomehasOrderProcess();

	public RsyncResult doProcess(RsyncRequestGetShipmentStat tRequest,
			RsyncResponseGetShipmentStat tResponse) {

		RsyncResult result = new RsyncResult();

		// 定义成功的数量合计
		int iSuccessSum = 0;

		if (result.upFlagTrue()) {
			if (tResponse != null && tResponse.getResult() != null) {
				result.setProcessNum(tResponse.getResult().size());
			} else {
				result.setProcessNum(0);

			}
		}

		// 开始循环处理结果数据
		if (result.upFlagTrue()) {

			// 判断有需要处理的数据才开始处理
			if (result.getProcessNum() > 0) {

				// 设置预期处理数量
				result.setProcessNum(tResponse.getResult().size());

				HomehasShipmentStatService shipmentStatService = new HomehasShipmentStatService();
				
				// 统一订单配送状态，防止一单多货出现多状态时，订单状态反复变更的问题
				Map<String,String> orderStatusMap = new HashMap<String, String>();
				for (RsyncModelShipmentStat model : tResponse.getResult()) {
					orderStatusMap.put(model.getOrd_id(), model.getCod_stat_cd());
				}
				
				for (RsyncModelShipmentStat model : tResponse.getResult()) {

					// 更新状态信息
					GcExtendOrderStatusHomehas orderStatus = new GcExtendOrderStatusHomehas();
					orderStatus.setOrderCode(model.getOrd_id());
					orderStatus.setSendStatus(model.getCod_stat_cd());
					// 有状态更新时间 则将此时间标记订单配送状态完成的时间
					if(StringUtils.isNotBlank(model.getStat_date())){
						orderStatus.setUpdateTime(model.getStat_date());
					}else{
						// 没有状态更新时间 所以只能将时间置为同步的结束时间 以标记如果订单配送完成的时间
						orderStatus.setUpdateTime(tRequest.getEnd_time());
					}
					
					//MWebResult mResult = homehasOrderProcess.insertOrderStatus(orderStatus);
					MWebResult mResult = new MWebResult();
					
					// 记录配送状态日志
					shipmentStatService.insertCodStatLog(model);
					
					//此处插入订单状态更新的代码
					String order_code="";
					String order_status="";
					String order_status_ext="";//订单辅助状态
					String org_ord_id="";
					List<MDataMap> list=DbUp.upTable("oc_orderinfo").query("order_code,order_status,order_status_ext,org_ord_id", "", "out_order_code=:out_order_code", new MDataMap("out_order_code",model.getOrd_id()), 0, 1);
					if(list!=null&&list.size()>0){
						order_code=list.get(0).get("order_code");
						order_status=list.get(0).get("order_status");
						order_status_ext=list.get(0).get("order_status_ext");
						org_ord_id=list.get(0).get("org_ord_id");
						
						String state=stateMapper(model.getOrd_id(),orderStatusMap.get(model.getOrd_id()), "");
						if(!"".equals(state)&&!state.equals(order_status)){ //比对与原来不一样的时候更新，并且在日志表中插入一条记录
							// 如果数据库已经是发货状态则不再更新为未发货
							if("4497153900010002".equals(state) && "4497153900010003".equals(order_status)){
								continue;
							}
							// 如果原单已经交易成功，然后进行了销退，则原单状态保持不变
							if("".equals(org_ord_id)) {
								String cod_stat_cd=trim(orderStatusMap.get(model.getOrd_id()));
								if("4497153900010005".equals(order_status)) {
									if("31".equals(cod_stat_cd) || "91".equals(cod_stat_cd)) {
										continue;
									}								
								}
							}							
							DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status",state,"update_time",DateUtil.getSysDateTimeString(),"order_code",order_code), "order_status,update_time", "order_code");
							if("4497153900010005".equals(state)) {
								if(DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990028") <= 0) {
									JobExecHelper.createExecInfo("449746990028", order_code, DateUtil.addMinute(28800));
								}
								if(DbUp.upTable("fh_share_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990033") <= 0) {
									JobExecHelper.createExecInfo("449746990033", order_code, DateUtil.addMinute(21600));
								}
							}
							if("4497153900010006".equals(state)) {
								//取消订单，判断是否是分销单，如果是，写入取消订单分销定时
								if(DbUp.upTable("fh_agent_order_detail").count("order_code",order_code)>0 && DbUp.upTable("za_exectimer").count("exec_info",order_code,"exec_type","449746990029") <= 0) {
									JobExecHelper.createExecInfo("449746990029", order_code, DateUtil.getSysDateTimeString());
								}
							}
							DbUp.upTable("lc_orderstatus").dataInsert(new MDataMap("code",order_code,"create_time",DateUtil.getSysDateTimeString(),"create_user","system","old_status",order_status,"now_status",state,"info","RsyncGetShipmentStat"));
						}
						
						/**
						 * 5.3.2 LD订单更新辅助状态
						 * 修改定时同步订单信息任务，LD订单需匹配出订单辅助状态，更新表字段
						 */
						String sql = "select order_code,cod_stat_cd from oc_order_ld_detail where order_code=:order_code and gift_cd = ''";
						List<Map<String, Object>> ldList = DbUp.upTable("oc_order_ld_detail").dataSqlList(sql, new MDataMap("order_code", order_code));
						if(ldList != null && ldList.size() > 0) {
							int refundSize = 0;
							for(Map<String, Object> map : ldList) {
								String cod_stat_cd = map.get("cod_stat_cd") == null ? "" : map.get("cod_stat_cd").toString();
								// 是否有销退的，判断一下部分销退的情况
								if("31".equals(cod_stat_cd) || "91".equals(cod_stat_cd)) {
									refundSize++;
								}
							}
							
							// 全部销退
							if(refundSize > 0 && refundSize == ldList.size() && !"4497153900140004".equals(order_status_ext)) {
								DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status_ext","4497153900140004","update_time",DateUtil.getSysDateTimeString(),"order_code",order_code), "order_status_ext", "order_code");
							}
							
							// 部分销退
							if(refundSize > 0 && refundSize < ldList.size() && !"4497153900140003".equals(order_status_ext)) {
								DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status_ext","4497153900140003","update_time",DateUtil.getSysDateTimeString(),"order_code",order_code), "order_status_ext", "order_code");
							}
							
							// 没有销退的则清除销退状态
							if(refundSize == 0 && ArrayUtils.contains(new String[]{"4497153900140003","4497153900140004"}, order_status_ext)) {
								DbUp.upTable("oc_orderinfo").dataUpdate(new MDataMap("order_status_ext","4497153900140002","update_time",DateUtil.getSysDateTimeString(),"order_code",order_code), "order_status_ext", "order_code");
							}
							
						}						
					}
					
					// 如果成功则将成功计数加1
					if (mResult.upFlagTrue()) {
						iSuccessSum++;

					} else {

						if (result.getResultList() == null) {
							result.setResultList(new ArrayList<Object>());
						}

						result.getResultList().add(mResult.getResultMessage());
					}

				}

				result.setProcessData(bInfo(918501102, result.getProcessNum(),
						iSuccessSum, result.getProcessNum() - iSuccessSum));

			}
		}

		// 如果操作都成功 则设置状态保存数据为同步结束时间 以方便下一轮调用
		if (result.upFlagTrue()) {
			// 设置处理成功数量
			result.setSuccessNum(iSuccessSum);
			result.setStatusData(tRequest.getEnd_time());
		}

		return result;
	}


	public RsyncResponseGetShipmentStat upResponseObject() {

		return new RsyncResponseGetShipmentStat();
	}

	
	/**
	 * LD 与 ERP 订单状态映射<br> 若映射不到的字段，不修改ERP状态
	 * @param yc_orderform_status 家有订单状态
	 * @param cod_stat_cd 配送状态
	 * @return
	 */
	public String stateMapper(String out_order_code,String cod_stat_cd,String yc_orderform_status){
		cod_stat_cd=trim(cod_stat_cd);
		yc_orderform_status=trim(yc_orderform_status);
//		---------------------配送状态----------------------
//		30	配送中
//		31	拒收
//		40	丢失
//		41	上门取货丢失
//		90	签收
//		91	销售退货
//		---------------------订单状态----------------------
//		10	订单受理
//		20	入款确认
//		30	欠交订单
//		40	出库指示
//		50	出库确定
//		60	完成出库(出库之后是否签收，根据cod_stat_cd物流状态确定)
//		91	受理后取消
//		92	入款后取消
//		93	取消欠交订单
//		94	出库指示后取消
//		99	电子自动取消
//		97	拒收外呼
//		98	二次配送
//		69	再配
//		70	配送完成
//		96	拒收
//--------------------------------
		
//		编号: 4497153900010001   名称: 下单成功-未付款 
//		编号: 4497153900010002  名称: 下单成功-未发货
//		编号: 4497153900010003  名称: 已发货 
//		编号: 4497153900010004  名称: 已收货 
//		编号: 4497153900010005  名称: 交易成功 
//		编号: 4497153900010006  名称: 交易失败 
		
		if(!"".equals(cod_stat_cd)){//配送状态
			if("30".equals(cod_stat_cd)||"40".equals(cod_stat_cd)){
				return "4497153900010003";
			}else if("90".equals(cod_stat_cd) || "91".equals(cod_stat_cd)) {
				return "4497153900010005";
			} else if("31".equals(cod_stat_cd)) {
				return "4497153900010006";
			}
		} else if(!"".equals(yc_orderform_status)) {
			
			if("30".equals(yc_orderform_status)){
				
//				如果LD系统商品缺货，不管是否支付。LD系统均将订单的状态置为“欠交订单”。针对LD此处理流程，系统从LD同步订单状态30时，处理如下：
//				 在线支付订单：如果订单未付款，系统订单状态不变（下单成功-未付款）；订单已付款，订单状态变更为“下单成功-未发货”。
//				 货到付款订单：系统订单状态不变（下单成功-未发货）。
				MDataMap dataMap=DbUp.upTable("oc_orderinfo").one("out_order_code",out_order_code);
				String big_order_code=dataMap.get("big_order_code");
				String order_code=dataMap.get("order_code");
				String sql="SELECT out_trade_no FROM oc_payment where (out_trade_no='"+order_code+"' "+(StringUtils.isBlank(big_order_code)?"":"or out_trade_no='"+big_order_code+"'") +" )  and (trade_status='TRADE_SUCCESS' or trade_status='TRADE_FINISHED') limit 1 ";
				List<Map<String, Object>> list=DbUp.upTable("oc_payment").dataSqlList(sql, null);
				if(list!=null&&list.size()>0){
					return "4497153900010002";
				}
				
			}else if("20".equals(yc_orderform_status)||"40".equals(yc_orderform_status)||"50".equals(yc_orderform_status)){
//			if("10".equals(yc_orderform_status)||"20".equals(yc_orderform_status)||"40".equals(yc_orderform_status)||"50".equals(yc_orderform_status)||"30".equals(yc_orderform_status)){
				return "4497153900010002";
			}else if("91".equals(yc_orderform_status)||"92".equals(yc_orderform_status)||"94".equals(yc_orderform_status)||"93".equals(yc_orderform_status)) {
				return "4497153900010006";
			}
		}
		
		return "";
	}
	
	private String trim(Object obj) {
		return obj == null ? "" : obj.toString().trim();
	}
}

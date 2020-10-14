package com.cmall.groupcenter.job;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.dborm.txmapper.groupcenter.GcRebateOrderMapper;
import com.cmall.dborm.txmodel.groupcenter.GcRebateOrder;
import com.cmall.groupcenter.txservice.TxRebateOrderService;
import com.srnpr.zapcom.basehelper.BeansHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;

/**
 * 定时更新返利订单状态 
 * @author chenbin
 *
 */
public class JobUpdateRebateOrder extends RootJob{

	public void doExecute(JobExecutionContext context) {
		GcRebateOrderMapper gcRebateOrderMapper=BeansHelper.upBean("bean_com_cmall_dborm_txmapper_GcRebateOrderMapper");
		TxRebateOrderService txRebateOrderService=BeansHelper
				.upBean("bean_com_cmall_groupcenter_txservice_TxRebateOrderService");
		/*//取非交易失败、交易成功且7天之内的订单;
		String sql="select zid,order_code,account_code,order_send_time from gc_rebate_order where (order_status!='4497153900010005' and order_status!='4497153900010006') or "
				+ " (order_status='5'  and DATE_SUB(CURDATE(), INTERVAL 8 DAY)<date(order_finish_time)) ";*/
		//非已返利、已取消订单
//		String sql="select zid,order_code,account_code,order_send_time from gc_rebate_order where rebate_status!='4497465200170004' and rebate_status!='4497465200170003'";
		String sql="select zid,order_code,account_code,order_send_time from gc_rebate_order where rebate_status!='4497465200170004' and rebate_status!='4497465200170003'"
				+ " and DATE_SUB(CURDATE(), INTERVAL 1 MONTH)<=date(create_time) ";
		List<Map<String, Object>> orderList=DbUp.upTable("gc_rebate_order").dataSqlList(sql, null);
		if(orderList!=null&&orderList.size()>0){
			for(Map<String, Object> order:orderList){
				GcRebateOrder gcRebateOrder=txRebateOrderService.updateRebateOrder(order.get("order_code").toString(), order.get("account_code").toString());
				
				if(gcRebateOrder!=null){
					//更新发货时间
					if(gcRebateOrder.getOrderSendTime()==null&&(order.get("order_send_time")==null||StringUtils.isBlank(order.get("order_send_time").toString()))){
						MDataMap codeMap=DbUp.upTable("oc_orderinfo").oneWhere("order_code", "", " order_code=:order_code or out_order_code=:order_code", "order_code",order.get("order_code").toString());
						if(codeMap!=null){
							MDataMap orderMap = DbUp.upTable("lc_orderstatus").one("code",codeMap.get("order_code"),"now_status","4497153900010003");
							if(orderMap!=null){
								gcRebateOrder.setOrderSendTime(orderMap.get("create_time"));
							}
						}
						
					}
					gcRebateOrder.setZid(Integer.valueOf(order.get("zid").toString()));
					gcRebateOrderMapper.updateByPrimaryKeySelective(gcRebateOrder);
				}
				
			}
		}
	}
}

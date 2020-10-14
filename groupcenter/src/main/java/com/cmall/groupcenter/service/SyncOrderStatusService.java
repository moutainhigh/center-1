package com.cmall.groupcenter.service;

import org.apache.commons.lang.StringUtils;

import com.cmall.groupcenter.GroupConstant.PayOrderStatusEnum;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.support.GroupReckonSupport;
import com.cmall.groupcenter.sync.orderstatus.model.SyncOrderStatusInput;
import com.cmall.groupcenter.sync.orderstatus.model.SyncOrderStatusResult;
import com.cmall.ordercenter.common.DateUtil;
import com.srnpr.zapcom.baseclass.BaseClass;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.helper.WebHelper;


/**
 * 微公社对接系统通过该接口同步订单最新状态
 * @author chenxk
 *
 */
public class SyncOrderStatusService extends BaseClass{

	public synchronized SyncOrderStatusResult saveOrderStatusInfo(SyncOrderStatusInput param,String manageCode){
		
		SyncOrderStatusResult result = new SyncOrderStatusResult();
		
		String lshCode = WebHelper.upCode("LSH");
		if(param.getOrderStatusInfos().size() == 0){
			result.inErrorMessage(918519010);
		}else{
			GroupReckonSupport groupReckonSupport = new GroupReckonSupport();
			for(SyncOrderStatusInput.OrderStatusInfo orderStatusInfo : param.getOrderStatusInfos()){
				
				DbUp.upTable("gc_sync_order_status").insert("uid",WebHelper.upUuid(),"update_time",orderStatusInfo.getUpdateTime(),
						"order_code",orderStatusInfo.getOrderCode(),"order_status",PayOrderStatusEnum.getPayOrderStatusBySno(Integer.valueOf(orderStatusInfo.getOrderStatus())),
						"remark",orderStatusInfo.getRemark(),"create_time",DateUtil.getSysDateTimeString(),"manage_code",manageCode,
						"status_serial_num",lshCode);
				
				try{
					setRebateOrderStatus(orderStatusInfo);  // fengl 通过订单状态更新返利状态
				}catch(Exception e){
					e.printStackTrace();
				}
				
				
				if(PayOrderStatusEnum.tradeSuccess.getsNo() == Integer.valueOf(orderStatusInfo.getOrderStatus())){
					DbUp.upTable("gc_reckon_order_info").dataUpdate(new MDataMap("order_finish_time",orderStatusInfo.getUpdateTime(),"order_code",orderStatusInfo.getOrderCode(),"manage_code",manageCode), "order_finish_time", "order_code,manage_code");
					groupReckonSupport.checkCreateStep(orderStatusInfo.getOrderCode(),GroupConst.RECKON_ORDER_EXEC_TYPE_IN);
				}
				if(PayOrderStatusEnum.tradeFailed.getsNo() == Integer.valueOf(orderStatusInfo.getOrderStatus())){
					//首先校验订单是否已存在部分退货.不存在：添加逆向返利、逆向清分流程,否则不添加
					if(DbUp.upTable("gc_reckon_order_step").count("order_code",orderStatusInfo.getOrderCode(),"exec_type",GroupConst.THIRD_RECKON_ORDER_EXEC_TYPE_BACK)<1){
						//订单失败进行逆向返利、逆向清分
						groupReckonSupport.checkCreateStep(orderStatusInfo.getOrderCode(),GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);
						groupReckonSupport.checkCreateStep(orderStatusInfo.getOrderCode(),GroupConst.RECKON_ORDER_EXEC_TYPE_BACK);
					}

				}
			}
			result.setStatusSerialNum(lshCode);
		}
		return result;
	}
	
	public static void setRebateOrderStatus(SyncOrderStatusInput.OrderStatusInfo orderStatusInfo){

		MDataMap  mdatamap=new MDataMap();
		
		String sendtime=null;
		String finishtime=null;
		String canceltime=null;
		
		String orderStatus= PayOrderStatusEnum.getPayOrderStatusBySno(Integer.valueOf(orderStatusInfo.getOrderStatus()));
		String orderCode=orderStatusInfo.getOrderCode();
		mdatamap.put("order_code", orderCode);
		mdatamap.put("order_status", orderStatus);
			if(orderStatus.equals("4497153900010001")){//下单成功-未付款 
				mdatamap.put("rebate_status", "4497465200170001");
				DbUp.upTable("gc_rebate_order").dataUpdate(mdatamap, "order_status,rebate_status", "order_code");
			}
			else if(orderStatus.equals("4497153900010002")||orderStatus.equals("4497153900010003")){//下单成功-未发货、 已发货
				mdatamap.put("rebate_status", "4497465200170002");
				sendtime=orderStatusInfo.getUpdateTime();
			}
			else if(orderStatus.equals("4497153900010004")||orderStatus.equals("4497153900010005")){//已收货、交易成功
				finishtime=orderStatusInfo.getUpdateTime();
				if(DbUp.upTable("gc_rebate_log").count("order_code",orderCode,"rebate_change_type","4497465200140004","flag_status","1")>0){
					mdatamap.put("rebate_status", "4497465200170004");
				}
				else{
					mdatamap.put("rebate_status", "4497465200170002");
				}
			}
			else if(orderStatus.equals("4497153900010006")){//交易失败
				//未转入提现账户的更新为已取消
				if(DbUp.upTable("gc_rebate_log").count("order_code",orderCode,"rebate_change_type","4497465200140004","flag_status","1")<1){
					mdatamap.put("rebate_status", "4497465200170003");
					canceltime=orderStatusInfo.getUpdateTime();
				}
				
			}
			
			
			if(sendtime!=null&&!sendtime.equals("")){
				mdatamap.put("order_send_time", sendtime);
				DbUp.upTable("gc_rebate_order").dataUpdate(mdatamap, "order_status,rebate_status,order_send_time", "order_code");

			}
			if(finishtime!=null&&!finishtime.equals("")){
				mdatamap.put("order_finish_time", finishtime);
				DbUp.upTable("gc_rebate_order").dataUpdate(mdatamap, "order_status,rebate_status,order_finish_time", "order_code");

			}
			if(canceltime!=null&&!canceltime.equals("")){
				mdatamap.put("order_cancel_time", canceltime);
				DbUp.upTable("gc_rebate_order").dataUpdate(mdatamap, "order_status,rebate_status,order_cancel_time", "order_code");
			}
	
	}
}

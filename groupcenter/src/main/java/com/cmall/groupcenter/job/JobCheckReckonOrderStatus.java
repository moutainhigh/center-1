package com.cmall.groupcenter.job;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.quartz.JobExecutionContext;

import com.cmall.dborm.txmodel.groupcenter.GcExtendOrderStatusHomehas;
import com.cmall.groupcenter.groupdo.GroupConst;
import com.cmall.groupcenter.model.ReckonStep;
import com.cmall.groupcenter.support.GroupReckonSupport;
import com.srnpr.zapcom.basehelper.DateHelper;
import com.srnpr.zapcom.basehelper.FormatHelper;
import com.srnpr.zapcom.basemodel.MDataMap;
import com.srnpr.zapdata.dbdo.DbUp;
import com.srnpr.zapweb.rootweb.RootJob;
import com.srnpr.zapweb.webdo.WebConst;
import com.srnpr.zapweb.webmodel.MWebResult;

/***
 * 1.在gc_reckon_order_info中查询字段order_finish_time=''and manage_code='SI2003'的订单，只处理开始时间到结束时间（从gc_config_content表中设置）内的订单，按照降序排列
 * 2.对order_finish_time=''的订单在gc_extend_order_status_homehas中状态为90 签收 或者退货状态 时 插入清分执行步骤表(gc_reckon_order_step)
 * 3.当状态为签收（90）时：更新order_finish_time 为gc_extend_order_status_homehas 的update_time 时间
 *   1）step表中如没有正向清分流程 插入正向清分，有的话不用插入
 *   2）step表中如没有正向返利流程，插入正向返利，有的话不用插入
 *   3）step表中如有逆向清分流程，exec_start_time 时间更新为空字符
 *   4）step表中如有逆向清分流程，没有逆向返利流程，插入逆向返利流程，有逆向返利流程就不插入
 *   5）step表中如没有逆向清分流程，则不用插入逆向返利流程
 * 4.当状态为退货（order_status:91,92,93,94,95,96,97,98,99, send_status:31,91）时
 *   1) step表中如有逆向清分流程，则不用插入
 *   2）step表中如没有逆向清分流程，则插入逆向清分流程
 *   3）step表中如没有逆向返利流程，则插入逆向返利流程
 * 
 * 
 * @author fengl
 * 
 */
public class JobCheckReckonOrderStatus extends RootJob {
	
	public void doExecute(JobExecutionContext context) {
		//设置默认时间
		String beginTime="2015-06-01 00:00:00";
		String endTime="2017-01-01 00:00:00";
		//取配置时间
		MDataMap configMap=DbUp.upTable("gc_config_content").one("config_name","order_finish_time_recheck");
		if(configMap!=null&&StringUtils.isNotBlank(configMap.get("config_content"))){
			String[] contents=configMap.get("config_content").split(",");
			beginTime=contents[0];
			endTime=contents[1];
		}
//		List<Map<String, Object>> mapList=DbUp.upTable("gc_reckon_order_info").dataQuery("order_code", "-order_create_time ", "order_code='21000337' and order_finish_time='' and order_create_time>='2015-00-00 00:00:00'", null, -1, 0);
		List<Map<String, Object>> mapList=DbUp.upTable("gc_reckon_order_info").dataQuery("order_code,process_remark", "-order_create_time ", "manage_code='SI2003' and order_finish_time='' and order_create_time>='"+beginTime+"' and order_create_time<'"+endTime+"'", null, -1, 0);

		if(mapList !=null && mapList.size()!=0){
			for (int i = 0; i < mapList.size(); i++) {
				Map<String, Object> map = mapList.get(i);
				if(map.get("order_code")!=null && !map.get("order_code").equals("")){
					String processRemark="";
					if(null==map.get("process_remark")||map.get("process_remark").equals("")){
						processRemark="";
					}else{
						processRemark=map.get("process_remark").toString();
					}
					List<Map<String, Object>> list = DbUp.upTable("gc_extend_order_status_homehas").dataQuery("", "order_code", "order_code='"+map.get("order_code")+"'", null, -1, 0);
					if(list !=null && list.size()!=0){
						for (int j = 0; j < list.size(); j++) {
							Map<String, Object> mapHome = list.get(j);
							GcExtendOrderStatusHomehas orderStatus = new GcExtendOrderStatusHomehas();
							orderStatus.setChangeStatus(mapHome.get("change_status").toString());
							orderStatus.setOrderCode(mapHome.get("order_code").toString());
							orderStatus.setOrderStatus(mapHome.get("order_status").toString());
							orderStatus.setUpdateTime(mapHome.get("update_time").toString());
							orderStatus.setSendStatus(mapHome.get("send_status").toString());
							// 定义清分类型
							String sReckon_Type = "";

							// 定义返利类型
							String rebateType = "";
							boolean isTrue=false;
							boolean isSignTrue=false;
							MWebResult mWebResult=new MWebResult();
							// 如果订单状态是90 已签收 则正向清分
							if (orderStatus.getOrderStatus().equals("90")
									|| orderStatus.getSendStatus().equals("90")) {
								sReckon_Type = GroupConst.RECKON_ORDER_EXEC_TYPE_IN;

								// 更新清分订单上的订单完成时间
								MDataMap mUpdateOrderMap = new MDataMap();
								mUpdateOrderMap.inAllValues("order_code",
										orderStatus.getOrderCode(), "order_finish_time",
										orderStatus.getUpdateTime(),"process_remark",processRemark+"|"+FormatHelper.upDateTime()+"执行定时添加签收时间");

								// 更新表
								DbUp.upTable("gc_reckon_order_info").dataUpdate(
										mUpdateOrderMap, "order_finish_time,process_remark", "order_code");
							
								isSignTrue=true;
								
							} else {
								String sOrderStatus = ",91,92,93,94,95,96,97,98,99,";
								String sSendStatus = ",31,91,";
//								String sChangestatus = ",10,";

								boolean bFlagBack = false;
								// 判断是否有订单状态
								if (StringUtils.contains(sOrderStatus,
										"," + orderStatus.getOrderStatus() + ",")) {
									bFlagBack = true;
									isTrue=true;
								}
								// 判断是否有配送状态
								if (StringUtils.contains(sSendStatus,
										"," + orderStatus.getSendStatus() + ",")) {
									bFlagBack = true;
									isTrue=true;
								}
								
								if (bFlagBack) {
									sReckon_Type = GroupConst.RECKON_ORDER_EXEC_TYPE_BACK;
									rebateType = GroupConst.REBATE_ORDER_EXEC_TYPE_BACK;
								}
								

							}
							
								// 如果触发了清分流程变更 则开始插入流程变更表
							if (StringUtils.isNotEmpty(sReckon_Type)) {

									MDataMap mOrderMap = DbUp.upTable("gc_reckon_order_info").one(
											"order_code", orderStatus.getOrderCode());

									// 判断订单存在且订单参加清分流程
									if (mOrderMap != null
											&& mOrderMap.get("flag_reckon").equals("1")) {

										ReckonStep reckonStep = new ReckonStep();
										reckonStep.setAccountCode(mOrderMap.get("account_code"));
										reckonStep.setExecType(sReckon_Type);
										reckonStep.setOrderCode(mOrderMap.get("order_code"));

										mWebResult=new GroupReckonSupport()
												.createReckonStep(reckonStep); //915805140
									} else {

										bLogInfo(918505132, orderStatus.getOrderCode());
									}
							}
							GroupReckonSupport groupReckonSupport = new GroupReckonSupport();
							//状态为签收的流程
							if(isSignTrue){
								groupReckonSupport.checkCreateStep(orderStatus.getOrderCode(),
								     GroupConst.REBATE_ORDER_EXEC_TYPE_IN);

						     // 定义 逆向清分 唯一约束
								String sUqcode = GroupConst.RECKON_ORDER_EXEC_TYPE_BACK + WebConst.CONST_SPLIT_DOWN
										+ orderStatus.getOrderCode();

								// 如果唯一约束不存在，则逆向清分流程不存在
								if (DbUp.upTable("gc_reckon_order_step").count("uqcode", sUqcode) == 0) {
									
								}else{ //存在逆向清分，修改exec_start_time为当前时间，逆向返利流程没有插入逆向返利流程，有则不插入
									MDataMap mDataMap= new MDataMap();

									mDataMap.inAllValues("uqcode",
											sUqcode, "exec_start_time", "");
									DbUp.upTable("gc_reckon_order_step").dataUpdate(mDataMap, "exec_start_time", "uqcode");

							        
									groupReckonSupport.checkCreateStep(orderStatus.getOrderCode(),
										     GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);
								}
 
							}
							//状态为退货的流程
							if(isTrue){	

								if (StringUtils.isNotEmpty(rebateType)) {
									groupReckonSupport.checkCreateStep(orderStatus.getOrderCode(),
											GroupConst.REBATE_ORDER_EXEC_TYPE_BACK);
								}
								
								if(mWebResult.getResultCode()==1){
										MDataMap mUpdateMap = new MDataMap();
										mUpdateMap.inAllValues("order_code",
												orderStatus.getOrderCode(), "process_remark",processRemark+"|"+FormatHelper.upDateTime()+"执行定时添加退货|");

										// 更新表
										DbUp.upTable("gc_reckon_order_info").dataUpdate(
												mUpdateMap, "process_remark", "order_code");
								}
							}


						}
					}
				}
			}
		}
		
		

	}


//
//	// 测试专用
//	public static void main(String[] args) {
//
//		JobCheckReckonOrderStatus job = new JobCheckReckonOrderStatus();
//		job.doExecute(null);	
//		
//	}
}
